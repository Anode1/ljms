package org.ljms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.regex.Pattern;

/**
 * The queue table: one table, N worker processes, no broker.
 * States in {@link Queue}, spec in doc/Queue_States.txt, loop in
 * {@link Processor}.
 *
 * <b>This is optimistic locking</b>, compare-and-swap, if you come to it from
 * concurrent programming rather than from databases. Nothing is locked while
 * anyone decides anything: the row is read, and the write that claims it is
 * made conditional on the row not having changed since.
 * "UPDATE ... WHERE id = ? AND status = 'NEW'" is that conditional write, and
 * the affected-row count is its answer: 1 means we won, 0 means someone else
 * did and we read again.
 *
 * So the correctness comes from the predicate, not from holding anything: no
 * SELECT ... FOR UPDATE, no transaction spanning the read and the update, no
 * transaction spanning the work, no table locks, no synchronized. Every
 * transition is one predicated single-row UPDATE under autocommit.
 *
 * That is the whole reason you can run as many workers as you like. Contention
 * costs wasted attempts, never blocked waiters, so the cost grows linearly with
 * workers instead of collapsing past some threshold. The pattern this avoids is
 * a lock held across application time, SELECT FOR UPDATE, do the work, COMMIT
 *, which looks perfect in testing, where there is no contention, and convoys
 * under real load: workers queue behind the leader, the connection pool drains,
 * timeouts cascade.
 *
 * <b>No automatic retries.</b> A task that fails goes to ERROR and stays there:
 * one failure, one log line, one row to look at. A retry loop multiplies one
 * problem across the log and hides how many distinct problems there are. The
 * fix is a human fix, to the code or to the data, and restarting the worker
 * is the retry ({@link #requeueErrors}). The rest of the queue keeps flowing
 * past a parked task rather than stalling on it.
 *
 * <b>Recovery is by lease, not by owner.</b> Taking a task sets lease_until; a
 * row still IN_PROCESS past its lease is presumed abandoned and returned to NEW
 * by whichever worker notices. One timestamp comparison, and it covers three
 * cases that owner-matching cannot:
 * <ul>
 *   <li>a dead worker's tasks come back without waiting for that worker to
 *       restart, owner-keyed recovery strands them if the host never returns
 *       under the same name;</li>
 *   <li>two workers sharing a node id cannot steal each other's in-flight
 *       rows, so the node id is a label, not a correctness requirement;</li>
 *   <li>when the database is what failed, a worker cannot write "put me back"
 *       anywhere, the lease expires on its own.</li>
 * </ul>
 *
 * <b>At-least-once.</b> A worker that is slow rather than dead can lose its
 * lease and have its task run twice. Tasks must be idempotent, or fence
 * themselves with {@link #extendLease}, which returns 0 once the lease is gone.
 *
 * Instances are cheap and hold no state beyond configuration; create one and
 * keep it, or make one per call. There is no global mutable state anywhere in
 * LJMS.
 */
public class QueueDAO {

    /** Table and schema names are concatenated, so they are checked, not trusted. */
    private static final Pattern SAFE_NAME = Pattern.compile("[A-Za-z0-9_$.]+");

    /** Error text is truncated to this before being stored. */
    private static final int ERROR_MAX = 1000;

    /** How many times a take re-reads the head after losing a race. */
    private static final int TAKE_ATTEMPTS = 5;

    private static final String COLS = "id, task_type, ref_id, payload, status";

    private final Connections db;
    private final String table;
    private final Dialect dialect;


    /** Table "QUEUE" on MySQL. */
    public QueueDAO(Connections db) {
        this(db, "QUEUE", Dialect.MYSQL);
    }

    public QueueDAO(Connections db, String table, Dialect dialect) {
        if (db == null)      throw new IllegalArgumentException("Connections is required");
        if (dialect == null) throw new IllegalArgumentException("Dialect is required");
        if (table == null || !SAFE_NAME.matcher(table).matches()) {
            throw new IllegalArgumentException("Unsafe table name: " + table);
        }
        this.db      = db;
        this.table   = table;
        this.dialect = dialect;
    }


    /**
     * A connection of our own, checked.
     *
     * Every state change here is one self-contained statement that must commit
     * on its own. Handed a connection with autocommit off, which a pool can
     * easily be configured to give, and which HikariCP, container DataSources
     * and Spring-managed connections all do, nothing would ever be committed:
     * close() would roll the claim and the outcome back, the row would stay
     * NEW, and every worker would take and run the same task forever, reporting
     * nothing worse than a "lease was already lost" warning each cycle. Silent,
     * and catastrophic. Cheaper to refuse than to debug.
     *
     * The put(Connection, ...) overload deliberately does NOT go through here:
     * enqueueing inside your own transaction is a supported thing to want.
     */
    private Connection open() throws Exception {
        Connection con = db.getConnection();
        try {
            if (!con.getAutoCommit()) {
                throw new IllegalStateException(
                        "LJMS needs connections in autocommit mode, and this one is not. "
                      + "Every state change is a single self-contained statement; inside a "
                      + "caller-managed transaction they would be rolled back on close and "
                      + "the same task would be run forever. Configure the pool with "
                      + "autoCommit=true, or give LJMS its own Connections.");
            }
        }
        catch (Exception e) {
            try { con.close(); } catch (Exception ignore) {}
            throw e;
        }
        return con;
    }


    // ------------------------------------------------------------------
    // take
    // ------------------------------------------------------------------

    /**
     * Takes the next due task, or returns null if there is nothing due.
     *
     * Read the head of the queue, then compare-and-swap it out of NEW. The
     * UPDATE's "AND status = 'NEW'" is the whole lock: whichever worker's
     * UPDATE lands first moves the row, and everyone else matches 0 rows.
     *
     * Do not "optimise" that predicate away on the grounds that we just
     * selected the row. The SELECT is a snapshot read, under REPEATABLE READ
     * it can hand you a row another worker has already taken and committed
     * and it is only harmless because the UPDATE re-checks status with a
     * current read. Removing it hands the same task to two workers, silently,
     * and only under load. LJMS's race test exists to catch exactly that: with
     * the predicate removed, every other test still passes.
     *
     * A lost race is retried here, up to TAKE_ATTEMPTS times, so that a busy
     * queue does not routinely look empty to the caller. It is a reduction in
     * probability, not a guarantee: null means "nothing due, or every attempt
     * lost a race", and the caller cannot tell which. Under heavy contention a
     * worker can therefore sleep out a polling interval with work still in the
     * table. Losing all attempts requires losing on TAKE_ATTEMPTS successive
     * and different head rows, so it is rare rather than impossible.
     *
     * @param owner        this worker's lease token, see {@link Processor}
     * @param ownerNode    the node part of it, without the incarnation
     * @param leaseSeconds how long the claim holds before other workers may
     *                     reclaim the row; must exceed the longest task, or
     *                     the task must call {@link #extendLease}
     */
    public Task take(String owner, String ownerNode, int leaseSeconds) throws Exception {
        Connection con = null;
        try {
            con = open();

            // Bounded. Each attempt re-reads the head, so a loss means some
            // other worker took that particular row and the next attempt sees
            // a different one; losing all of them means being beaten on five
            // successive rows. If that happens the caller polls again shortly.
            for (int attempt = 0; attempt < TAKE_ATTEMPTS; attempt++) {

                Task task = head(con);
                if (task == null) return null;                   // nothing due

                if (cas(con, task.id, owner, ownerNode, leaseSeconds) > 0) {
                    task.status = Queue.IN_PROCESS;
                    return task;
                }
                // Somebody else took it between our read and our update.
                // The next head() will skip it, it is no longer NEW.
            }
            return null;
        }
        finally {
            if (con != null) try { con.close(); } catch (Exception ignore) {}
        }
    }

    /** The oldest due task still waiting, or null. */
    private Task head(Connection con) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String q =
                    "SELECT " + COLS +
                    "  FROM " + table +
                    " WHERE status = ? " +
                    "   AND (not_before IS NULL OR not_before <= CURRENT_TIMESTAMP) " +
                    " ORDER BY id " + dialect.firstRowOnly();
            ps = con.prepareStatement(q);
            ps.setString(1, Queue.NEW);
            rs = ps.executeQuery();
            return rs.next() ? row(rs) : null;
        }
        finally {
            if (rs != null) try { rs.close(); } catch (Exception ignore) {}
            if (ps != null) try { ps.close(); } catch (Exception ignore) {}
        }
    }

    /**
     * The compare-and-swap. Moves the row out of NEW only if it is still NEW.
     *
     * @return 1 if we took it, 0 if another worker got there first
     */
    private int cas(Connection con, long id, String owner, String ownerNode, int leaseSeconds)
            throws Exception {
        PreparedStatement ps = null;
        try {
            String q =
                    "UPDATE " + table +
                    "   SET status = ?, owner = ?, owner_node = ?, " +
                    "       started = CURRENT_TIMESTAMP, finished = NULL, " +
                    "       lease_until = " + dialect.nowPlusSeconds() + " " +
                    " WHERE id = ? AND status = ?";
            ps = con.prepareStatement(q);
            ps.setString(1, Queue.IN_PROCESS);
            ps.setString(2, owner);
            ps.setString(3, ownerNode);
            ps.setInt(4, leaseSeconds);
            ps.setLong(5, id);
            ps.setString(6, Queue.NEW);          // <-- the lock. Do not remove.
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) try { ps.close(); } catch (Exception ignore) {}
        }
    }


    // ------------------------------------------------------------------
    // Outcomes
    //
    // Both carry "AND owner = ? AND status = 'IN_PROCESS'", so a worker can
    // only move a row it still holds. Losing the lease makes them no-ops
    // (return 0) rather than clobbering whoever holds it now, check the
    // return value.
    // ------------------------------------------------------------------

    /** IN_PROCESS -&gt; DONE. Returns 0 if the lease was lost meanwhile. */
    public int done(long id, String owner) throws Exception {
        return finish(id, owner, Queue.DONE, null);
    }

    /** IN_PROCESS -&gt; ERROR. Returns 0 if the lease was lost meanwhile. */
    public int error(long id, String owner, String message) throws Exception {
        return finish(id, owner, Queue.ERROR, message);
    }

    private int finish(long id, String owner, String status, String message) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = open();
            String q =
                    "UPDATE " + table +
                    "   SET status = ?, error = ?, lease_until = NULL, " +
                    "       finished = CURRENT_TIMESTAMP " +
                    " WHERE id = ? AND owner = ? AND status = ?";
            ps = con.prepareStatement(q);
            ps.setString(1, status);
            ps.setString(2, truncate(message));
            ps.setLong(3, id);
            ps.setString(4, owner);
            ps.setString(5, Queue.IN_PROCESS);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) try { ps.close(); } catch (Exception ignore) {}
            if (con != null) try { con.close(); } catch (Exception ignore) {}
        }
    }

    /**
     * Extends the lease on a task still being worked on, for tasks that can
     * outlive the lease they were taken with.
     *
     * @return 0 if the lease is already gone: you no longer own the task and
     *         must abandon it without writing any outcome
     */
    public int extendLease(long id, String owner, int leaseSeconds) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = open();
            String q =
                    "UPDATE " + table +
                    "   SET lease_until = " + dialect.nowPlusSeconds() + " " +
                    " WHERE id = ? AND owner = ? AND status = ? " +
                    // The lease itself, not just the ownership stamp. Without
                    // this a worker whose lease ran out an hour ago is told it
                    // still owns the task, right up until some other worker
                    // happens to sweep - and sweeps only run between tasks, so
                    // a fleet busy with long tasks runs none at all. This is
                    // the fence; it has to test the thing it fences.
                    "   AND lease_until > CURRENT_TIMESTAMP";
            ps = con.prepareStatement(q);
            ps.setInt(1, leaseSeconds);
            ps.setLong(2, id);
            ps.setString(3, owner);
            ps.setString(4, Queue.IN_PROCESS);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) try { ps.close(); } catch (Exception ignore) {}
            if (con != null) try { con.close(); } catch (Exception ignore) {}
        }
    }


    // ------------------------------------------------------------------
    // Recovery
    // ------------------------------------------------------------------

    /**
     * IN_PROCESS -&gt; NEW for one task, immediately: the worker holding it is
     * being shut down and will not finish it.
     *
     * Without this, a deliberate stop mid-task would leave the row unavailable
     * for the whole remaining lease, half an hour by default, because lease
     * expiry is the only other way out of IN_PROCESS. A worker that knows it is
     * leaving can say so, and the task is takeable again at once.
     *
     * The ownership guard means a task that turns out to finish anyway cannot
     * then overwrite this: its done() matches nothing. It may, of course,
     * already have run, that is at-least-once, and it is what lease expiry
     * would have done later regardless.
     *
     * @return 0 if we no longer held it
     */
    public int abandon(long id, String owner) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = open();
            String q =
                    "UPDATE " + table +
                    "   SET status = ?, owner = NULL, started = NULL, lease_until = NULL, " +
                    "       error = 'released - worker shut down before finishing' " +
                    " WHERE id = ? AND owner = ? AND status = ?";
            ps = con.prepareStatement(q);
            ps.setString(1, Queue.NEW);
            ps.setLong(2, id);
            ps.setString(3, owner);
            ps.setString(4, Queue.IN_PROCESS);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) try { ps.close(); } catch (Exception ignore) {}
            if (con != null) try { con.close(); } catch (Exception ignore) {}
        }
    }

    /**
     * IN_PROCESS -&gt; NEW for every row whose lease has run out: the worker
     * holding it died, hung, or lost the database. Owner-independent, so the
     * next worker to run this recovers it, no waiting for the dead one.
     *
     * @return number of tasks recovered
     */
    public int recoverExpired() throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = open();
            String q =
                    "UPDATE " + table +
                    "   SET status = ?, owner = NULL, started = NULL, lease_until = NULL, " +
                    "       error = 'lease expired - worker presumed dead' " +
                    " WHERE status = ? AND lease_until IS NOT NULL " +
                    "   AND lease_until < CURRENT_TIMESTAMP";
            ps = con.prepareStatement(q);
            ps.setString(1, Queue.NEW);
            ps.setString(2, Queue.IN_PROCESS);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) try { ps.close(); } catch (Exception ignore) {}
            if (con != null) try { con.close(); } catch (Exception ignore) {}
        }
    }

    /**
     * ERROR -&gt; NEW for the tasks this worker failed. Run once at startup:
     * restarting the worker, after the code or the data has been fixed, is how
     * a failed task is retried, nothing else retries anywhere.
     *
     * Scoped to this worker's node, so restarting one worker does not replay
     * another's failures. Nothing here is in flight (ERROR rows hold no lease),
     * so a shared node id is harmless.
     *
     * @return number of tasks re-queued
     */
    public int requeueErrors(String ownerNode) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = open();
            String q =
                    "UPDATE " + table +
                    "   SET status = ?, owner = NULL, started = NULL, finished = NULL, " +
                    "       lease_until = NULL, not_before = NULL " +
                    " WHERE status = ? AND owner_node = ?";
            ps = con.prepareStatement(q);
            ps.setString(1, Queue.NEW);
            ps.setString(2, Queue.ERROR);
            ps.setString(3, ownerNode);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) try { ps.close(); } catch (Exception ignore) {}
            if (con != null) try { con.close(); } catch (Exception ignore) {}
        }
    }


    /**
     * ERROR -&gt; NEW for every failed task, whoever failed it.
     *
     * The node-scoped {@link #requeueErrors(String)} is the automatic path, and
     * it depends on the node id being the same as last time. That is fine for a
     * fixed host and wrong for a container, whose hostname is its id and
     * changes at every redeploy, leaving that worker's ERROR rows with no path
     * out at all, because the node-scoped sweep is the only one. This is the
     * operator's way back: deliberate, unscoped, and never called automatically.
     *
     * @return number of tasks re-queued
     */
    public int requeueAllErrors() throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = open();
            String q =
                    "UPDATE " + table +
                    "   SET status = ?, owner = NULL, owner_node = NULL, started = NULL, " +
                    "       finished = NULL, lease_until = NULL, not_before = NULL " +
                    " WHERE status = ?";
            ps = con.prepareStatement(q);
            ps.setString(1, Queue.NEW);
            ps.setString(2, Queue.ERROR);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) try { ps.close(); } catch (Exception ignore) {}
            if (con != null) try { con.close(); } catch (Exception ignore) {}
        }
    }


    // ------------------------------------------------------------------
    // put / inspect
    // ------------------------------------------------------------------

    /** Enqueues a task, due immediately. */
    public long put(String taskType, long refId, String payload) throws Exception {
        return put(taskType, Long.valueOf(refId), payload, null);
    }

    /**
     * Enqueues a task.
     *
     * @param refId     a row id in your own tables, or null
     * @param notBefore SQL timestamp ("2026-07-25 02:00:00") to hold the task
     *                  until, or null to make it due immediately
     * @return the generated id
     */
    public long put(String taskType, Long refId, String payload, String notBefore) throws Exception {
        Connection con = null;
        try {
            con = open();
            return put(con, taskType, refId, payload, notBefore);
        }
        finally {
            if (con != null) try { con.close(); } catch (Exception ignore) {}
        }
    }

    /**
     * Enqueues on a connection you already have, so putting a task and the
     * database work that justifies it can share one transaction, if you are
     * running without autocommit.
     */
    public long put(Connection con, String taskType, Long refId,
                    String payload, String notBefore) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String q =
                    "INSERT INTO " + table +
                    "  (task_type, ref_id, payload, status, not_before) " +
                    "VALUES (?, ?, ?, ?, ?)";
            ps = con.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, taskType);
            if (refId == null) ps.setNull(2, Types.BIGINT); else ps.setLong(2, refId.longValue());
            ps.setString(3, payload);
            ps.setString(4, Queue.NEW);
            // setTimestamp, not setString. PostgreSQL binds a String as
            // varchar and refuses it ("column not_before is of type timestamp
            // ... but expression is of type character varying"); Oracle applies
            // the NLS date format and raises ORA-01861. Only MySQL is forgiving,
            // and MySQL is the one dialect that gets tested here.
            if (notBefore == null) ps.setNull(5, Types.TIMESTAMP);
            else                   ps.setTimestamp(5, java.sql.Timestamp.valueOf(notBefore));
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (!rs.next()) throw new Exception("insert into " + table + " generated no key");
            return rs.getLong(1);
        }
        finally {
            if (rs != null) try { rs.close(); } catch (Exception ignore) {}
            if (ps != null) try { ps.close(); } catch (Exception ignore) {}
        }
    }

    /** Reads a task, but only if this owner still holds it. */
    public Task get(Connection con, long id, String owner) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String q =
                    "SELECT " + COLS +
                    "  FROM " + table +
                    " WHERE id = ? AND owner = ? AND status = ?";
            ps = con.prepareStatement(q);
            ps.setLong(1, id);
            ps.setString(2, owner);
            ps.setString(3, Queue.IN_PROCESS);
            rs = ps.executeQuery();
            return rs.next() ? row(rs) : null;
        }
        finally {
            if (rs != null) try { rs.close(); } catch (Exception ignore) {}
            if (ps != null) try { ps.close(); } catch (Exception ignore) {}
        }
    }

    /**
     * 1-based position of a waiting task among tasks of its type, the "2" in
     * "2 of 10". Tasks are taken in id order, so this is how many are ahead of
     * it, plus one.
     *
     * Counts only tasks that are actually due: one held back by not_before is
     * not ahead of you in any sense you would want reported. Assumes the task
     * you are asking about is itself still waiting, it does not check, so a
     * DONE task reports the position it would have if it were not.
     */
    public int position(String taskType, long id) throws Exception {
        return count("SELECT 1 + COUNT(*) FROM " + table +
                     " WHERE status = ? AND task_type = ? AND id < ?" +
                     "   AND (not_before IS NULL OR not_before <= CURRENT_TIMESTAMP)",
                     taskType, Long.valueOf(id));
    }

    /** How many tasks of this type are waiting, the "10" in "2 of 10". */
    public int pending(String taskType) throws Exception {
        return count("SELECT COUNT(*) FROM " + table +
                     " WHERE status = ? AND task_type = ?" +
                     "   AND (not_before IS NULL OR not_before <= CURRENT_TIMESTAMP)",
                     taskType, null);
    }

    private int count(String q, String taskType, Long id) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = open();
            ps = con.prepareStatement(q);
            ps.setString(1, Queue.NEW);
            ps.setString(2, taskType);
            if (id != null) ps.setLong(3, id.longValue());
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
        finally {
            if (rs != null) try { rs.close(); } catch (Exception ignore) {}
            if (ps != null) try { ps.close(); } catch (Exception ignore) {}
            if (con != null) try { con.close(); } catch (Exception ignore) {}
        }
    }


    private static Task row(ResultSet rs) throws Exception {
        Task t   = new Task();
        t.id     = rs.getLong("id");
        t.type   = rs.getString("task_type");
        t.refId  = rs.getLong("ref_id");     // 0 when NULL; refId is yours to interpret
        t.payload= rs.getString("payload");
        t.status = rs.getString("status");
        return t;
    }

    private static String truncate(String s) {
        if (s == null) return null;
        return s.length() <= ERROR_MAX ? s : s.substring(0, ERROR_MAX);
    }
}
