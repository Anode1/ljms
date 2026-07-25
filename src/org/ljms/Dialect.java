package org.ljms;

/**
 * The two SQL expressions that differ between databases: date arithmetic and
 * row limiting. Everything else in {@link QueueDAO} is plain SQL that runs
 * unchanged anywhere, SELECT, UPDATE, INSERT, CURRENT_TIMESTAMP, COUNT.
 *
 * Not quite the whole portability surface: QueueDAO.put() retrieves its
 * generated key with Statement.RETURN_GENERATED_KEYS, which the Oracle driver
 * wants written as a named column instead (see sql/oracle.sql). So porting is
 * one line here plus a schema, and for Oracle one line of source.
 *
 * Note it is the <i>database</i> that computes "now", never the worker. Lease
 * expiry is compared against database time, so taking "now" from a worker's
 * own clock would make leases expire early or late by however much the host
 * clocks disagree, and on a queue whose whole recovery story is the lease,
 * that is not a detail.
 */
public enum Dialect {

    MYSQL   ("CURRENT_TIMESTAMP + INTERVAL ? SECOND",          "LIMIT 1"),

    // The cast is not decoration: PostgreSQL infers a parameter's type from
    // its context, and "? * interval" leaves it ambiguous enough that the
    // driver can report "could not determine data type of parameter".
    POSTGRES("CURRENT_TIMESTAMP + (CAST(? AS integer) * interval '1 second')",
                                                               "FETCH FIRST 1 ROWS ONLY"),

    // CURRENT_TIMESTAMP, not SYSTIMESTAMP: on Oracle those are different
    // clocks - session time zone versus database server time zone - and
    // lease_until is written by this expression but compared against
    // CURRENT_TIMESTAMP in head() and recoverExpired(). Mixing them makes
    // leases expire early or late by the offset between the two.
    ORACLE  ("CURRENT_TIMESTAMP + NUMTODSINTERVAL(?, 'SECOND')", "FETCH FIRST 1 ROWS ONLY"),

    /** SQL Server 2012+. */
    MSSQL   ("DATEADD(second, ?, CURRENT_TIMESTAMP)",          "OFFSET 0 ROWS FETCH FIRST 1 ROWS ONLY");


    private final String nowPlusSeconds;
    private final String firstRowOnly;

    Dialect(String nowPlusSeconds, String firstRowOnly) {
        this.nowPlusSeconds = nowPlusSeconds;
        this.firstRowOnly   = firstRowOnly;
    }

    /** "now + N seconds", where N is a bound parameter. */
    public String nowPlusSeconds() {
        return nowPlusSeconds;
    }

    /**
     * Trailing clause that keeps a sorted query to its first row.
     *
     * Not an optimisation, a correctness-of-plan matter. Without it the
     * optimiser plans for the whole result set: MySQL walks the primary key in
     * id order to avoid a sort and filters on status as it goes, examining
     * hundreds of rows to hand back one, and the index on (status, id) is not
     * used at all. Reading only the first row of the cursor does not help,
     * because the plan is already chosen. "FETCH FIRST" is the SQL:2008
     * spelling; MySQL never adopted it and uses LIMIT.
     */
    public String firstRowOnly() {
        return firstRowOnly;
    }
}
