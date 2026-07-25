package org.ljms;

/**
 * One row of the queue, as handed to {@link Processor#work}.
 *
 * Public fields, no getters. This is a record you read, not an object with
 * behaviour, and a bean with five accessors would be five times the code
 * saying the same thing.
 */
public class Task {

    /** Primary key. Also the order tasks are taken in. */
    public long id;

    /** What kind of work this is — you dispatch on it in {@link Processor#work}. */
    public String type;

    /** The thing being queued: a row id in your own tables. May be 0 if unused. */
    public long refId;

    /** Anything else the task needs. JSON, CSV, a filename — the queue never reads it. */
    public String payload;

    /** Always {@link Queue#IN_PROCESS} by the time you see it. */
    public String status;


    public String toString() {
        return "Task[id=" + id + " type=" + type + " refId=" + refId + " status=" + status + "]";
    }
}
