package org.ljms;

/**
 * The queue's states. Specified in doc/Queue_States.txt, implemented by
 * {@link QueueDAO}, driven by {@link Processor}.
 *
 * <pre>
 *   NEW        = start | IN_PROCESS[take]
 *   IN_PROCESS = DONE[done] | ERROR[error] | NEW[^expire]
 *   ERROR      = NEW[restart]
 *   DONE       = terminal
 * </pre>
 *
 * Deliberately just the names: the doc is the spec and the SQL is the
 * implementation, so a third copy of the edges here would only give them
 * something to drift from. QueueTests checks the doc against the graph rules,
 * and checks these names against the doc.
 *
 * Names, not codes. A status column you can read without the source is worth
 * the handful of extra bytes: "SELECT status, COUNT(*) ... GROUP BY status"
 * should answer the question by itself. The ancestor of this queue used
 * numeric codes, and its recovery swept a hand-written list —
 * ('90','02','81','82','86','19') — that silently stopped covering every
 * in-flight state once new ones were added. Nobody reviewing that line could
 * see what was missing.
 */
public class Queue {

    /** Enqueued. The only state a worker takes; always available once due. */
    public static final String NEW        = "NEW";

    /** Taken, being worked on by the owner stamped in the row, until lease_until. */
    public static final String IN_PROCESS = "IN_PROCESS";

    /** Finished successfully. Terminal. */
    public static final String DONE       = "DONE";

    /**
     * Failed. Terminal for the machine, parked for a human: no automatic retry
     * ever runs. Restarting the worker, after the code or data is fixed, is
     * the retry — see {@link QueueDAO#requeueErrors}.
     */
    public static final String ERROR      = "ERROR";

    private Queue() {}
}
