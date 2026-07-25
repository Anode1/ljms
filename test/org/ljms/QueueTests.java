package org.ljms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import junit.framework.TestCase;

/**
 * The behaviour tests. Needs a database; creates its own table and drops it.
 *
 * Set the three constants below to a scratch database, then "ant test".
 * It drops and recreates the table on every test method.
 *
 * The one that matters is {@link #testRaceNoTaskTakenTwice}. Everything else
 * here can be established by reading the code; "N workers never take the same
 * task" cannot. Remove the "AND status = ?" from QueueDAO's compare-and-swap
 * and every other test in this file still passes — only that one fails. That
 * is the shape of concurrency bugs, and the reason the test exists.
 */
public class QueueTests extends TestCase {

    // ------------------------------------------------------------------
    // EDIT THESE. A SCRATCH database, please: setUp drops and recreates the
    // QUEUE table on every single test method. Kept separate from the
    // constants in Processor so that pointing a worker at production can
    // never point the tests there too.
    // ------------------------------------------------------------------

    private static final String DB_URL      = "jdbc:mysql://127.0.0.1/ljms_test";
    private static final String DB_USER     = "<db-user>";
    private static final String DB_PASSWORD = "<db-password>";

    private static final String TYPE  = "TEST_TASK";
    private static final String NODE  = "testnode";
    private static final String OWNER = "testnode 2026-01-01 00:00:00.000";
    private static final int    LEASE = 60;

    private Connections db;
    private QueueDAO queue;


    public void setUp() throws Exception {

        db = () -> DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        queue = new QueueDAO(db);

        sql("DROP TABLE IF EXISTS QUEUE");
        sql("CREATE TABLE QUEUE ("                                         +
            "  id          BIGINT       NOT NULL AUTO_INCREMENT,"          +
            "  task_type   VARCHAR(32)  NOT NULL,"                         +
            "  ref_id      BIGINT           NULL,"                         +
            "  payload     TEXT             NULL,"                         +
            "  status      VARCHAR(16)  NOT NULL DEFAULT 'NEW',"           +
            "  not_before  DATETIME         NULL,"                         +
            "  owner       VARCHAR(128)     NULL,"                         +
            "  owner_node  VARCHAR(64)      NULL,"                         +
            "  lease_until DATETIME         NULL,"                         +
            "  error       VARCHAR(1000)    NULL,"                         +
            "  created     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "  started     DATETIME         NULL,"                         +
            "  finished    DATETIME         NULL,"                         +
            "  PRIMARY KEY (id),"                                          +
            "  KEY ix_queue_take  (status, id),"                           +
            "  KEY ix_queue_lease (status, lease_until),"                  +
            "  KEY ix_queue_node  (status, owner_node)"                    +
            ")");
    }

    // JUnit 3 lifecycle override
    public void tearDown() throws Exception {
        sql("DROP TABLE IF EXISTS QUEUE");
    }


    /** put() enqueues, take() hands it over and stamps owner and lease. */
    public void testPutThenTake() throws Exception {

        long id = queue.put(TYPE, Long.valueOf(42), "{\"a\":1}", null);

        Task task = queue.take(OWNER, NODE, LEASE);

        assertNotNull("the task we just put should come back", task);
        assertEquals(id, task.id);
        assertEquals(TYPE, task.type);
        assertEquals(42, task.refId);
        assertEquals("{\"a\":1}", task.payload);
        assertEquals(Queue.IN_PROCESS, task.status);

        assertEquals(Queue.IN_PROCESS, col(id, "status"));
        assertEquals(OWNER, col(id, "owner"));
        assertEquals(NODE, col(id, "owner_node"));
        assertNotNull("taking a task must set the lease", col(id, "lease_until"));

        assertNull("only one task was enqueued", queue.take(OWNER, NODE, LEASE));
    }

    /** An empty queue hands back null rather than blocking or throwing. */
    public void testTakeOnEmptyQueue() throws Exception {
        assertNull("nothing enqueued, so nothing to take", queue.take(OWNER, NODE, LEASE));
    }

    /** Tasks come out oldest first. */
    public void testTakesInFifoOrder() throws Exception {

        long first  = queue.put(TYPE, 0L, null);
        long second = queue.put(TYPE, 0L, null);

        Task task = queue.take(OWNER, NODE, LEASE);
        assertEquals("oldest first", first, task.id);
        queue.done(first, OWNER);

        task = queue.take(OWNER, NODE, LEASE);
        assertEquals(second, task.id);
    }

    /** not_before holds a task back until its time — overnight runs, retry-later. */
    public void testNotBeforeHoldsATaskBack() throws Exception {

        queue.put(TYPE, null, null, "2099-01-01 00:00:00");
        assertNull("not_before is in the future, so nothing is due",
                   queue.take(OWNER, NODE, LEASE));

        long due = queue.put(TYPE, null, null, "2000-01-01 00:00:00");

        Task task = queue.take(OWNER, NODE, LEASE);
        assertNotNull("a past not_before is due now", task);
        assertEquals(due, task.id);
    }

    /** done() is terminal and releases the lease. */
    public void testDone() throws Exception {

        long id = queue.put(TYPE, 0L, null);
        queue.take(OWNER, NODE, LEASE);

        assertEquals(1, queue.done(id, OWNER));
        assertEquals(Queue.DONE, col(id, "status"));
        assertNull("a finished task holds no lease", col(id, "lease_until"));
        assertNotNull("finished should be stamped", col(id, "finished"));
    }

    /** error() parks the task with its message; nothing retries it. */
    public void testError() throws Exception {

        long id = queue.put(TYPE, 0L, null);
        queue.take(OWNER, NODE, LEASE);

        assertEquals(1, queue.error(id, OWNER, "boom"));
        assertEquals(Queue.ERROR, col(id, "status"));
        assertEquals("boom", col(id, "error"));
        assertNull("a parked task holds no lease", col(id, "lease_until"));
    }

    /**
     * The ownership guard: a worker that has lost its lease must not overwrite
     * whoever holds the task now. Both outcomes report 0 instead.
     */
    public void testOutcomeFromTheWrongOwnerIsANoOp() throws Exception {

        long id = queue.put(TYPE, 0L, null);
        queue.take(OWNER, NODE, LEASE);

        assertEquals("someone else's done() must not land",
                     0, queue.done(id, "impostor 2026-01-01 00:00:00.000"));
        assertEquals("someone else's error() must not land",
                     0, queue.error(id, "impostor 2026-01-01 00:00:00.000", "x"));

        assertEquals("the task is still ours", Queue.IN_PROCESS, col(id, "status"));
    }

    /**
     * A row left IN_PROCESS by a failed outcome-write carries our own owner.
     * take() must still return the task it just took, not that stale one.
     */
    public void testTakeIgnoresAStaleRowHeldByTheSameOwner() throws Exception {

        queue.put(TYPE, 0L, null);
        queue.take(OWNER, NODE, LEASE);       // taken, then "the database went
                                              // away" - no outcome ever written
        long fresh = queue.put(TYPE, 0L, null);

        Task task = queue.take(OWNER, NODE, LEASE);
        assertNotNull("there is a due task", task);
        assertEquals("take() must return the task it just took, not the stale one",
                     fresh, task.id);
    }

    /** extendLease() moves a live lease out. */
    public void testExtendLease() throws Exception {

        long id = queue.put(TYPE, 0L, null);
        queue.take(OWNER, NODE, LEASE);

        String before = col(id, "lease_until");
        Thread.sleep(1100);                       // DATETIME resolution is a second

        assertEquals(1, queue.extendLease(id, OWNER, LEASE));
        assertFalse("the lease should have moved", before.equals(col(id, "lease_until")));
    }

    /**
     * extendLease() is the fence for irreversible work, so it has to fail once
     * the lease is gone — not merely once someone else has swept the row.
     *
     * The previous version of this test forced lease_until into the past and
     * asserted extendLease returned 1, which is what the code did and the
     * opposite of what every doc promised. A test can pin a bug as firmly as
     * it can catch one.
     */
    public void testExtendLeaseFailsOnAnExpiredLease() throws Exception {

        long id = queue.put(TYPE, 0L, null);
        queue.take(OWNER, NODE, LEASE);

        sql("UPDATE QUEUE SET lease_until = '2000-01-01 00:00:00' WHERE id = " + id);

        assertEquals("the lease has expired, even though nothing has swept the row yet",
                     0, queue.extendLease(id, OWNER, LEASE));
    }

    /** And it must fail for anyone who never held the lease. */
    public void testExtendLeaseFailsForTheWrongOwner() throws Exception {

        long id = queue.put(TYPE, 0L, null);
        queue.take(OWNER, NODE, LEASE);

        assertEquals(0, queue.extendLease(id, "impostor 2026-01-01 00:00:00.000", LEASE));
    }

    /**
     * A worker being shut down hands its task back at once, rather than
     * leaving it stranded for the rest of its lease.
     *
     * The ancestor of this design killed the task immediately and relied on the
     * restart to reclaim it, which worked because recovery there was scoped to
     * the owner. With lease-based recovery that policy would cost a full lease
     * of latency on every ordinary stop, so the departing worker says so.
     */
    public void testAbandonReturnsATaskImmediately() throws Exception {

        long id = queue.put(TYPE, 0L, null);
        queue.take(OWNER, NODE, LEASE);

        assertEquals(1, queue.abandon(id, OWNER));
        assertEquals(Queue.NEW, col(id, "status"));
        assertNull("and it holds no lease, so it is takeable now", col(id, "lease_until"));

        Task task = queue.take("someone-else 2026-01-01 00:00:00.000", "someone-else", LEASE);
        assertNotNull("another worker can take it straight away, not in 30 minutes", task);
        assertEquals(id, task.id);
    }

    /** Only the holder can hand a task back. */
    public void testAbandonFromTheWrongOwnerIsANoOp() throws Exception {

        long id = queue.put(TYPE, 0L, null);
        queue.take(OWNER, NODE, LEASE);

        assertEquals(0, queue.abandon(id, "impostor 2026-01-01 00:00:00.000"));
        assertEquals(Queue.IN_PROCESS, col(id, "status"));
    }

    /**
     * A worker that dies mid-task leaves its row IN_PROCESS. Recovery is by
     * lease expiry, so any worker frees it — no waiting for the dead one to
     * come back — and a live lease is never disturbed.
     */
    public void testRecoverExpiredReturnsAbandonedTasks() throws Exception {

        long abandoned = queue.put(TYPE, 0L, null);
        queue.take(OWNER, NODE, LEASE);
        sql("UPDATE QUEUE SET lease_until = '2000-01-01 00:00:00' WHERE id = " + abandoned);

        long live = queue.put(TYPE, 0L, null);
        queue.take("othernode 2026-01-01 00:00:00.000", "othernode", LEASE);

        assertEquals("only the expired lease comes back", 1, queue.recoverExpired());

        assertEquals(Queue.NEW, col(abandoned, "status"));
        assertNull("a recovered task holds no owner", col(abandoned, "owner"));
        assertEquals("a live lease must not be touched", Queue.IN_PROCESS, col(live, "status"));
    }

    /** Recovery must not resurrect finished work. */
    public void testRecoverExpiredLeavesFinishedTasksAlone() throws Exception {

        long id = queue.put(TYPE, 0L, null);
        queue.take(OWNER, NODE, LEASE);
        queue.done(id, OWNER);

        assertEquals(0, queue.recoverExpired());
        assertEquals(Queue.DONE, col(id, "status"));
    }

    /**
     * Restart is the retry — but only for this worker's own failures.
     * Restarting one worker must not replay another's.
     */
    public void testRequeueErrorsIsScopedToThisNode() throws Exception {

        long mine = queue.put(TYPE, 0L, null);
        queue.take(OWNER, NODE, LEASE);
        queue.error(mine, OWNER, "mine failed");

        long theirs = queue.put(TYPE, 0L, null);
        queue.take("othernode 2026-01-01 00:00:00.000", "othernode", LEASE);
        queue.error(theirs, "othernode 2026-01-01 00:00:00.000", "theirs failed");

        assertEquals("only this node's failures are re-queued", 1, queue.requeueErrors(NODE));

        assertEquals(Queue.NEW, col(mine, "status"));
        assertEquals("another worker's failures must be left alone",
                     Queue.ERROR, col(theirs, "status"));
    }

    /** The loop marks a task that ran cleanly as DONE. */
    public void testProcessorMarksASuccessfulTaskDone() throws Exception {

        long id = queue.put(TYPE, 0L, null);

        Processor worker = new Processor(queue);
        worker.node = NODE;
        worker.process(queue.take(worker.owner(), NODE, LEASE));

        assertEquals(Queue.DONE, col(id, "status"));
    }

    /**
     * A throwing task parks its own row and does not take the worker down —
     * one failure, one terminal row, and the queue keeps flowing. The work()
     * below is also exactly how you plug your own logic in.
     */
    public void testProcessorParksAFailedTaskAndSurvives() throws Exception {

        long id = queue.put(TYPE, 0L, null);

        Processor worker = new Processor(queue) {
            protected void work(Task task) throws Exception {
                throw new IllegalStateException("bad data");
            }
        };
        worker.node = NODE;
        worker.process(queue.take(worker.owner(), NODE, LEASE));   // must not throw

        assertEquals(Queue.ERROR, col(id, "status"));
        assertTrue("the failure is recorded on the row, not only in the log",
                   col(id, "error").contains("bad data"));
    }

    /**
     * An Error out of work() must still park the row before the worker dies.
     *
     * If it does not, the row keeps its lease, returns to NEW when the lease
     * expires, keeps its id and so becomes the head of the queue again — and
     * kills the next worker that takes it. One poison task would stop the
     * whole queue and every worker in turn, with cron restarting them into it.
     */
    public void testProcessorParksATaskThatThrowsAnError() throws Exception {

        long id = queue.put(TYPE, 0L, null);

        Processor worker = new Processor(queue) {
            protected void work(Task task) throws Exception {
                throw new StackOverflowError("simulated");
            }
        };
        worker.node = NODE;

        try {
            worker.process(queue.take(worker.owner(), NODE, LEASE));
            fail("an Error must reach the caller - a worker in that state should stop");
        }
        catch (StackOverflowError expected) { /* the worker dies, as it should */ }

        assertEquals("but the row must be parked first, or it poisons every worker",
                     Queue.ERROR, col(id, "status"));
        assertNull("and it must not still hold a lease", col(id, "lease_until"));
    }

    /** "2 of 10" — position counts only waiting tasks of the same type. */
    public void testPositionAndPending() throws Exception {

        long a = queue.put(TYPE, 0L, null);
        long b = queue.put(TYPE, 0L, null);
        long c = queue.put(TYPE, 0L, null);
        queue.put("OTHER_TYPE", 0L, null);              // must not be counted
        queue.put(TYPE, null, null, "2099-01-01 00:00:00");   // nor must one not yet due

        assertEquals("a task held back by not_before is not ahead of anyone",
                     3, queue.pending(TYPE));
        assertEquals(1, queue.position(TYPE, a));
        assertEquals(2, queue.position(TYPE, b));
        assertEquals(3, queue.position(TYPE, c));

        queue.take(OWNER, NODE, LEASE);                 // a is no longer waiting

        assertEquals(2, queue.pending(TYPE));
        assertEquals("b has moved to the front", 1, queue.position(TYPE, b));
    }

    /**
     * A single worker must never hold more than one connection at a time, and
     * none at all while idle or while work() runs.
     *
     * This is what lets one worker occupy exactly one slot of max_connections,
     * and what keeps a connection alive for milliseconds rather than long
     * enough to be closed under it by wait_timeout. It holds because every
     * method opens in a try and closes in the finally, and none nests —
     * take() passes its connection down to head() and cas() rather than
     * letting them open their own. Easy to break by accident; hence a test.
     */
    public void testOneWorkerHoldsOneConnectionAtATime() throws Exception {

        final int[] open = { 0 }, max = { 0 };

        Connections counted = () -> {
            final Connection real = db.getConnection();
            open[0]++;
            if (open[0] > max[0]) max[0] = open[0];
            return (Connection) java.lang.reflect.Proxy.newProxyInstance(
                    getClass().getClassLoader(), new Class[] { Connection.class },
                    (proxy, method, args) -> {
                        if ("close".equals(method.getName())) { open[0]--; real.close(); return null; }
                        return method.invoke(real, args);
                    });
        };

        QueueDAO counting = new QueueDAO(counted);
        for (int i = 0; i < 5; i++) counting.put(TYPE, Long.valueOf(i), null);

        Task task;
        while ((task = counting.take(OWNER, NODE, LEASE)) != null) {
            assertEquals("nothing may be held while the task runs", 0, open[0]);
            counting.done(task.id, OWNER);
        }
        counting.recoverExpired();
        counting.requeueErrors(NODE);
        counting.pending(TYPE);

        assertEquals("a single worker must never hold two connections at once", 1, max[0]);
        assertEquals("and none once it goes idle", 0, open[0]);
    }

    /**
     * The whole justification for the design: N workers hammering take()
     * against the same rows, with no lock held anywhere, must still hand out
     * every task exactly once.
     *
     * A duplicate here means the compare-and-swap is not doing its job.
     */
    public void testRaceNoTaskTakenTwice() throws Exception {

        final int WORKERS = 8;
        final int TASKS   = 200;

        for (int i = 0; i < TASKS; i++) queue.put(TYPE, Long.valueOf(i), null, null);

        final List<Long>      taken  = new CopyOnWriteArrayList<Long>();
        final List<Throwable> failed = new CopyOnWriteArrayList<Throwable>();

        List<Thread> workers = new ArrayList<Thread>();
        for (int w = 0; w < WORKERS; w++) {
            final String owner = "racenode" + w + " 2026-01-01 00:00:00.000";
            final String node  = "racenode" + w;

            Thread worker = new Thread() {
                public void run() {
                    try {
                        // take() returns null both for "empty" and for "lost
                        // every retry", so a null is not proof the queue is
                        // drained. Ask the table before giving up, or eight
                        // threads colliding on the last few rows can end the
                        // run early and fail an assertion that is not wrong.
                        while (true) {
                            Task task = queue.take(owner, node, LEASE);
                            if (task == null) {
                                if (queue.pending(TYPE) == 0) break;
                                Thread.sleep(5);
                                continue;
                            }
                            taken.add(Long.valueOf(task.id));
                            queue.done(task.id, owner);
                        }
                    }
                    catch (Throwable e) { failed.add(e); }
                }
            };
            workers.add(worker);
            worker.start();
        }
        for (Thread worker : workers) {
            worker.join(120000);
            assertFalse("a worker thread hung; the counts below would be read while it "
                      + "was still running", worker.isAlive());
        }

        assertTrue("worker threads failed: " + failed, failed.isEmpty());

        Set<Long> distinct = new HashSet<Long>(taken);
        assertEquals("a task was handed to more than one worker: "
                   + (taken.size() - distinct.size()) + " duplicate(s) out of "
                   + taken.size() + " takes", taken.size(), distinct.size());
        assertEquals("every task should have been taken", TASKS, distinct.size());

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = db.getConnection();
            ps = con.prepareStatement("SELECT COUNT(*) FROM QUEUE WHERE status = ?");
            ps.setString(1, Queue.DONE);
            rs = ps.executeQuery();
            rs.next();
            assertEquals("every task should have finished", TASKS, rs.getInt(1));
        }
        finally {
            if (rs != null) try { rs.close(); } catch (Exception ignore) {}
            if (ps != null) try { ps.close(); } catch (Exception ignore) {}
            if (con != null) try { con.close(); } catch (Exception ignore) {}
        }
    }


    /** One column of one row, as text. */
    private String col(long id, String column) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = db.getConnection();
            ps = con.prepareStatement("SELECT " + column + " FROM QUEUE WHERE id = ?");
            ps.setLong(1, id);
            rs = ps.executeQuery();
            return rs.next() ? rs.getString(1) : null;
        }
        finally {
            if (rs != null) try { rs.close(); } catch (Exception ignore) {}
            if (ps != null) try { ps.close(); } catch (Exception ignore) {}
            if (con != null) try { con.close(); } catch (Exception ignore) {}
        }
    }

    /** Table setup, and the direct row edits that simulate a dead worker. */
    private void sql(String statement) throws Exception {
        Connection con = null;
        Statement st = null;
        try {
            con = db.getConnection();
            st = con.createStatement();
            st.execute(statement);
        }
        finally {
            if (st != null) try { st.close(); } catch (Exception ignore) {}
            if (con != null) try { con.close(); } catch (Exception ignore) {}
        }
    }
}
