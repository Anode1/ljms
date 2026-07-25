package org.ljms;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The worker: a loop that takes one task at a time and runs it.
 *
 * <b>This is the file you edit.</b> Put your work in {@link #work} — that is
 * the whole integration story. LJMS is a template, not a framework: you copy
 * six files and own them, rather than depending on a jar and implementing an
 * interface it dictates.
 *
 * <pre>
 *   Connections db = () -&gt; DriverManager.getConnection(url, user, password);
 *
 *   Processor worker = new Processor(db);
 *   worker.node = "worker1";          // optional; defaults to the hostname
 *   worker.start();                   // runs until the JVM is asked to stop
 * </pre>
 *
 * Run one, or run twenty — they coordinate through the table itself, and no
 * worker ever holds a lock (see {@link QueueDAO}). Each stamps rows with a
 * token unique to this JVM incarnation, "&lt;node&gt; &lt;start timestamp&gt;".
 *
 * <b>Two kinds of failure, two reactions.</b>
 * <ul>
 *   <li><i>A task fails</i> — its row goes to ERROR, one log line, and the
 *       worker moves on to the next task. ERROR is terminal, so nothing
 *       retries it and nothing repeats the log line. Fix the code or the data,
 *       restart the worker, and startup returns this worker's ERROR rows to
 *       NEW. Restart is the retry.</li>
 *   <li><i>The queue machinery fails</i> — usually the database is gone. That
 *       is not one task's problem, so the worker backs off and, after
 *       {@link #maxErrors} consecutive failed cycles, stops rather than
 *       filling the log forever. Whatever it was holding is freed by lease
 *       expiry, with no help from this process.</li>
 * </ul>
 *
 * Logging is java.util.logging, so LJMS depends on nothing. Route it to
 * whatever you actually use with the standard bridges, or replace the four
 * log calls.
 */
public class Processor {

    private static final Logger log = Logger.getLogger(Processor.class.getName());

    // ------------------------------------------------------------------
    // EDIT THESE. Used only by main() — that is, only when you run a worker
    // from queue.sh. If your application already knows how to reach the
    // database, ignore them entirely and hand a Connections to the
    // constructor instead.
    //
    // There is no properties file and no config class on purpose: every
    // project already has its own way of holding credentials, and a template
    // that insists on one just means a layer to rip out. Three constants you
    // can see are less to undo than a configuration system you have to.
    // ------------------------------------------------------------------

    public static final String DB_URL      = "jdbc:mysql://127.0.0.1/ljms";
    public static final String DB_USER     = "<db-user>";
    public static final String DB_PASSWORD = "<db-password>";


    /** Worker id. Defaults to the hostname. Diagnostics, not correctness. */
    public String node = hostName();

    /** How long to wait before polling again when the queue is empty. */
    public long pollingMs = 5000;

    /**
     * How long a taken task stays ours. Must exceed the longest task, or the
     * task must extend it — see {@link #extendLease}. Too short means work
     * runs twice; too long means a dead worker's task waits that long.
     */
    public int leaseSeconds = 1800;

    /** How long to wait after a failed cycle (database down, typically). */
    public long errorBackoffMs = 60000;

    /** Consecutive failed cycles before the worker gives up and exits. */
    public int maxErrors = 10;

    /**
     * How long a shutdown waits for the task in hand to finish. Past this the
     * worker hands the task back so another can take it at once, and stops.
     * Set it to 0 to stop immediately and always hand back — cheaper if your
     * tasks are long and safely repeatable. Keep queue.sh's patience above it.
     */
    public long shutdownWaitMs = 55000;

    /** Set by {@link #stop}, or by the shutdown hook {@link #main} installs. */
    private volatile boolean terminating;

    private final QueueDAO queue;
    private String owner;

    /** When the lease sweep last ran, so it runs on a timer, not per task. */
    private long lastRecovery;

    /** The task being worked on right now, so a shutdown can release it. */
    private volatile Task inFlight;

    /**
     * The lease token stamped on every row this worker takes:
     * "&lt;node&gt; &lt;the moment this worker first asked&gt;". The node part says
     * who, the timestamp part says which incarnation.
     *
     * Derived on first use rather than in the constructor, so {@link #node} can
     * still be set afterwards, and fixed from then on — it has to be stable for
     * the life of the worker or its own outcome writes would stop matching.
     */
    protected String owner() {
        if (owner == null) {
            // The random tail is not decoration. Two JVMs sharing a node id -
            // the default, since it is the hostname - and starting in the same
            // millisecond would otherwise get the same token, and so would one
            // restarted across a clock step back. Identical tokens mean a
            // stalled worker's error() can match a live sibling's claim and
            // park a task the sibling is still running.
            owner = node + " "
                  + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " "
                  + Long.toHexString(new java.security.SecureRandom().nextLong() >>> 40);
        }
        return owner;
    }


    public Processor(Connections db) {
        this(new QueueDAO(db));
    }

    public Processor(QueueDAO queue) {
        if (queue == null) throw new IllegalArgumentException("QueueDAO is required");
        this.queue = queue;
    }


    /**
     * <b>Your work goes here.</b> Empty on purpose.
     *
     * Dispatch on task.type, read task.refId / task.payload, do the job.
     * Throw plainly on failure: the loop parks the row in ERROR with one log
     * line and carries on, and nothing retries it.
     *
     * <pre>
     *   void work(Task task) throws Exception {
     *       if ("SEND_REPORT".equals(task.type)) sendReport(task.refId);
     *       else throw new Exception("Unknown task type: " + task.type);
     *   }
     * </pre>
     *
     * A task that can outlive {@link #leaseSeconds} must call
     * {@link #extendLease} as it goes, and stop if that returns false.
     */
    protected void work(Task task) throws Exception {
    }


    /**
     * Keeps a long task's lease alive. Call it periodically from {@link #work}.
     *
     * @return false if the lease is already gone — another worker now owns this
     *         task, so abandon it immediately and write no outcome
     */
    protected boolean extendLease(Task task) throws Exception {
        return queue.extendLease(task.id, owner(), leaseSeconds) > 0;
    }


    /** Runs until {@link #stop}, the JVM shuts down, or the database stays down. */
    public void start() throws Exception {

        log.info("LJMS worker starting, owner=" + owner());

        int errors = 0;
        boolean requeuedErrors = false;

        while (!terminating) {
            try {
                // Restart is the retry: whatever this worker failed last time
                // goes back on the queue, on the assumption it was restarted
                // because the cause was fixed.
                //
                // Inside the loop, not before it: if the database happens to be
                // down at startup this has to back off like any other failure,
                // rather than killing a worker that would have recovered a
                // minute later. Runs once, on the first cycle that reaches the
                // database.
                if (!requeuedErrors) {
                    int requeued = queue.requeueErrors(node);
                    requeuedErrors = true;
                    if (requeued > 0) log.warning("Re-queued " + requeued + " previously failed task(s)");
                }

                // Free anything a dead worker was holding. Owner-independent,
                // so this covers other workers too, not just our own crashes.
                //
                // Not every cycle: a lease is measured in minutes, so sweeping
                // per task would add an UPDATE to every task to notice nothing.
                // Half a lease is often enough to be timely and rare enough to
                // vanish against real work.
                long now = System.currentTimeMillis();
                if (now - lastRecovery > leaseSeconds * 500L) {
                    lastRecovery = now;
                    int recovered = queue.recoverExpired();
                    if (recovered > 0) log.warning("Recovered " + recovered + " task(s) with an expired lease");
                }

                Task task = queue.take(owner(), node, leaseSeconds);
                errors = 0;                              // the machinery is healthy

                if (task == null) {                      // nothing due
                    sleep(pollingMs);
                    continue;
                }

                process(task);
            }
            catch (Exception e) {
                errors++;
                log.log(Level.SEVERE, "Queue cycle failed (" + errors + " in a row)", e);
                if (errors >= maxErrors) {
                    log.severe("Giving up after " + errors + " consecutive failures - stopping " + owner());
                    throw e;
                }
                sleep(errorBackoffMs);
            }
        }

        log.info("LJMS worker stopped, owner=" + owner());
    }

    /** Asks the loop to finish the task in hand and return. */
    public void stop() {
        terminating = true;
    }


    /**
     * Runs one task and records the outcome. Never throws: every path leaves
     * the row out of IN_PROCESS, or it would sit there until its lease expires.
     */
    void process(Task task) {
        inFlight = task;
        try {
            log.info("Processing " + task);

            work(task);

            if (queue.done(task.id, owner()) == 0) {
                log.warning("id=" + task.id + " finished, but the lease was already lost - "
                          + "another worker may have run it too");
            }
            else {
                log.info("Finished id=" + task.id);
            }
        }
        catch (Throwable t) {
            // Throwable, not Exception. An Error - OutOfMemory, StackOverflow,
            // NoClassDefFound, all reachable from work() - would otherwise pass
            // straight through, leaving the row IN_PROCESS holding a live
            // lease. After the lease expired it would return to NEW keeping its
            // id, become the head of the queue again, and kill the next worker
            // that took it. One poison task would stop the whole queue and
            // every worker in turn, with cron restarting them into it forever.
            //
            // One failure, one log line, one terminal row. No retry.
            log.log(Level.SEVERE, "Failed id=" + task.id + " type=" + task.type
                  + " - parked in " + Queue.ERROR + "; fix and restart the worker to retry", t);
            try {
                if (queue.error(task.id, owner(), t.getClass().getSimpleName() + ": " + t.getMessage()) == 0) {
                    log.warning("id=" + task.id + " failed, but the lease was already lost - "
                              + "the failure is not recorded on the row and another worker has it");
                }
            }
            catch (Exception e2) {
                // The database went away while recording the outcome. The row
                // stays IN_PROCESS and its lease expiry will free it.
                log.log(Level.SEVERE, "Could not record the failure of id=" + task.id, e2);
            }

            // A worker that has hit an Error is not in a fit state to carry on.
            // Let it die - but only now that the task is parked, so nothing
            // else dies on the same one.
            if (t instanceof Error) throw (Error) t;
        }
        finally {
            inFlight = null;
        }
    }


    /**
     * Called by the shutdown hook once the wait has run out: hands back the
     * task still in progress so another worker can take it immediately, rather
     * than leaving it stranded for the rest of its lease.
     */
    private void releaseInFlight() {
        Task task = inFlight;
        if (task == null) return;
        try {
            if (queue.abandon(task.id, owner()) > 0) {
                log.warning("Released id=" + task.id + " back to " + Queue.NEW
                          + " - it was still running when this worker was stopped");
            }
        }
        catch (Exception e) {
            log.log(Level.WARNING, "Could not release id=" + task.id
                  + "; it will wait out its lease instead", e);
        }
    }


    /** Sleeps in slices, so shutdown does not have to wait out the whole interval. */
    private void sleep(long ms) {
        long deadline = System.currentTimeMillis() + ms;
        while (!terminating) {
            long left = deadline - System.currentTimeMillis();
            if (left <= 0) return;
            try { Thread.sleep(Math.min(500L, left)); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        }
    }

    private static String hostName() {
        try { return InetAddress.getLocalHost().getHostName(); }
        catch (Exception e) { return "unknown"; }
    }


    /**
     * Runs a worker from the command line — what queue.sh starts.
     *
     * <pre>   java org.ljms.Processor [node]</pre>
     *
     * Reads {@link #DB_URL} and friends above. Pass a node name only when you
     * run more than one worker on the same host, so their log lines and the
     * owner column tell them apart.
     */
    public static void main(String[] args) {
        try {
            Connections db = () -> java.sql.DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            final Processor worker = new Processor(db);
            if (args.length > 0) worker.node = args[0];

            // The hook must WAIT for the worker, not just ask it to stop. A JVM
            // runs its shutdown hooks and then halts as soon as they return —
            // it does not wait for other threads. A hook that only sets the
            // flag would let SIGTERM destroy the worker in the middle of
            // work(), leaving the row IN_PROCESS holding a live lease, so an
            // ordinary deliberate stop would strand a task for the full lease
            // and half-perform its side effects. That is the failure the lease
            // exists to handle for crashes, and there is no reason to inflict
            // it on a planned shutdown.
            final Thread workerThread = Thread.currentThread();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    log.info("Shutdown requested - finishing the task in hand...");
                    worker.stop();
                    try { workerThread.join(worker.shutdownWaitMs); }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    if (workerThread.isAlive()) {
                        log.warning("Task did not finish within " + worker.shutdownWaitMs
                                  + " ms; stopping anyway.");
                        worker.releaseInFlight();
                    }
                }
            });

            worker.start();
        }
        catch (Throwable t) {
            log.log(Level.SEVERE, "LJMS worker terminated", t);
            System.exit(1);
        }
    }
}
