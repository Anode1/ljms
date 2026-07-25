/* ljms -- see queue.h.
 * Copyright (c) 2001, 2026 Vasili Gavrilov. MIT License; see ../LICENSE. */
#include "queue.h"

#include <stdio.h>
#include <string.h>

#include <sql.h>
#include <sqlext.h>

/* The one expression that differs between databases: "now + N seconds", with N
 * bound as a parameter. Everything else here is plain SQL.
 *   DB2:        CURRENT TIMESTAMP + ? SECONDS
 *   PostgreSQL: CURRENT_TIMESTAMP + (CAST(? AS integer) * interval '1 second')
 *   Oracle:     CURRENT_TIMESTAMP + NUMTODSINTERVAL(?, 'SECOND')
 *   SQL Server: DATEADD(second, ?, CURRENT_TIMESTAMP) */
#define LJMS_LEASE_EXPIRY "CURRENT TIMESTAMP + ? SECONDS"

/* The clause that keeps a sorted query to its first row. Not an optimisation:
 * without it the optimiser plans for the whole result set and stops using the
 * index on (status, id).
 *   DB2, Oracle, PostgreSQL: FETCH FIRST 1 ROWS ONLY
 *   MySQL:                   LIMIT 1 */
#define LJMS_FIRST_ROW "FETCH FIRST 1 ROWS ONLY"

/* How many times a take re-reads the head after losing a race. */
#define LJMS_TAKE_ATTEMPTS 5

static SQLHENV env = SQL_NULL_HENV;
static SQLHDBC dbc = SQL_NULL_HDBC;

/* True for SQL_SUCCESS and SQL_SUCCESS_WITH_INFO, which is the ODBC idiom. */
static int ok(SQLRETURN r)
{
    return (r == SQL_SUCCESS || r == SQL_SUCCESS_WITH_INFO) ? 1 : 0;
}

/* Print the driver's diagnostics, so a failure says what the database said
 * rather than only that something failed. */
static void diag(SQLSMALLINT type, SQLHANDLE h, const char *what)
{
    SQLCHAR state[6];
    SQLCHAR text[SQL_MAX_MESSAGE_LENGTH];
    SQLINTEGER native = 0;
    SQLSMALLINT len = 0;
    SQLSMALLINT rec = 1;

    while (SQLGetDiagRec(type, h, rec, state, &native, text,
                         (SQLSMALLINT)sizeof text, &len) == SQL_SUCCESS) {
        (void)fprintf(stderr, "ljms: %s: %s %s\n", what, (char *)state, (char *)text);
        rec++;
    }
}

/* Allocate a statement handle. Returns 0 on success. */
static int stmt_open(SQLHSTMT *out)
{
    if (!ok(SQLAllocHandle(SQL_HANDLE_STMT, dbc, out))) {
        diag(SQL_HANDLE_DBC, dbc, "alloc stmt");
        return -1;
    }
    return 0;
}

static void stmt_close(SQLHSTMT st)
{
    (void)SQLFreeHandle(SQL_HANDLE_STMT, st);
}

/* Rows affected by the statement just executed, or -1. This is the return
 * value of the compare-and-swap: 1 means we won, 0 means somebody else did. */
static int affected(SQLHSTMT st)
{
    SQLLEN n = 0;
    if (!ok(SQLRowCount(st, &n))) {
        diag(SQL_HANDLE_STMT, st, "row count");
        return -1;
    }
    return (int)n;
}

int ljms_connect(const char *dsn, const char *user, const char *pass)
{
    if (!ok(SQLAllocHandle(SQL_HANDLE_ENV, SQL_NULL_HANDLE, &env))) return -1;

    if (!ok(SQLSetEnvAttr(env, SQL_ATTR_ODBC_VERSION, (SQLPOINTER)SQL_OV_ODBC3, 0))) {
        diag(SQL_HANDLE_ENV, env, "set odbc version");
        return -1;
    }
    if (!ok(SQLAllocHandle(SQL_HANDLE_DBC, env, &dbc))) {
        diag(SQL_HANDLE_ENV, env, "alloc dbc");
        return -1;
    }
    if (!ok(SQLConnect(dbc, (SQLCHAR *)dsn,  SQL_NTS,
                            (SQLCHAR *)user, SQL_NTS,
                            (SQLCHAR *)pass, SQL_NTS))) {
        diag(SQL_HANDLE_DBC, dbc, "connect");
        return -1;
    }

    /* Autocommit, deliberately. Every state change here is one self-contained
     * statement that must commit on its own; inside a caller-managed
     * transaction they would be rolled back and the same task would run
     * forever, reporting nothing worse than a lost lease each cycle. */
    if (!ok(SQLSetConnectAttr(dbc, SQL_ATTR_AUTOCOMMIT,
                              (SQLPOINTER)SQL_AUTOCOMMIT_ON, 0))) {
        diag(SQL_HANDLE_DBC, dbc, "set autocommit");
        return -1;
    }
    return 0;
}

void ljms_disconnect(void)
{
    if (dbc != SQL_NULL_HDBC) {
        (void)SQLDisconnect(dbc);
        (void)SQLFreeHandle(SQL_HANDLE_DBC, dbc);
        dbc = SQL_NULL_HDBC;
    }
    if (env != SQL_NULL_HENV) {
        (void)SQLFreeHandle(SQL_HANDLE_ENV, env);
        env = SQL_NULL_HENV;
    }
}

/* Read the oldest task that is due, without claiming it. Returns 1 and fills
 * OUT, 0 if nothing is due, -1 on error. */
static int head(ljms_task *out)
{
    static const char *q =
        "SELECT id, task_type, ref_id, payload, attempts"
        "  FROM QUEUE"
        " WHERE status = ?"
        "   AND (not_before IS NULL OR not_before <= CURRENT TIMESTAMP)"
        " ORDER BY id " LJMS_FIRST_ROW;

    SQLHSTMT st;
    SQLLEN ind_ref = 0;
    SQLLEN ind_pay = 0;
    SQLRETURN r;
    int got = -1;

    if (stmt_open(&st) != 0) return -1;

    if (ok(SQLPrepare(st, (SQLCHAR *)q, SQL_NTS))
     && ok(SQLBindParameter(st, 1, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            16, 0, (SQLPOINTER)LJMS_NEW, 0, NULL))
     && ok(SQLExecute(st))) {

        r = SQLFetch(st);
        if (r == SQL_NO_DATA) {
            got = 0;
        } else if (ok(r)) {
            out->ref_id = 0;
            out->payload[0] = '\0';
            (void)SQLGetData(st, 1, SQL_C_SLONG, &out->id, 0, NULL);
            (void)SQLGetData(st, 2, SQL_C_CHAR, out->type, (SQLLEN)sizeof out->type, NULL);
            (void)SQLGetData(st, 3, SQL_C_SLONG, &out->ref_id, 0, &ind_ref);
            (void)SQLGetData(st, 4, SQL_C_CHAR, out->payload,
                             (SQLLEN)sizeof out->payload, &ind_pay);
            (void)SQLGetData(st, 5, SQL_C_SLONG, &out->attempts, 0, NULL);
            if (ind_ref == SQL_NULL_DATA) out->ref_id = 0;
            if (ind_pay == SQL_NULL_DATA) out->payload[0] = '\0';
            got = 1;
        } else {
            diag(SQL_HANDLE_STMT, st, "head fetch");
        }
    } else {
        diag(SQL_HANDLE_STMT, st, "head");
    }

    stmt_close(st);
    return got;
}

/* The compare-and-swap. Moves the row out of NEW only if it is still NEW.
 * Returns 1 if we took it, 0 if another worker got there first, -1 on error.
 *
 * "AND status = ?" is the whole lock. Do not remove it on the grounds that we
 * have just selected the row: that SELECT is a snapshot read and can hand back
 * a row another worker already took and committed. The UPDATE re-checks with a
 * current read, which is the only reason the snapshot is harmless. Take it out
 * and two workers get the same task, silently, and only under load. */
static int cas(long id, const char *owner, const char *node, int lease_s)
{
    static const char *q =
        "UPDATE QUEUE"
        "   SET status = ?, owner = ?, owner_node = ?,"
        "       attempts = attempts + 1,"
        "       started = CURRENT TIMESTAMP, finished = NULL,"
        "       lease_until = " LJMS_LEASE_EXPIRY
        " WHERE id = ? AND status = ?";

    SQLHSTMT st;
    SQLINTEGER lease = (SQLINTEGER)lease_s;
    SQLINTEGER key = (SQLINTEGER)id;
    int n = -1;

    if (stmt_open(&st) != 0) return -1;

    if (ok(SQLPrepare(st, (SQLCHAR *)q, SQL_NTS))
     && ok(SQLBindParameter(st, 1, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            16, 0, (SQLPOINTER)LJMS_IN_PROCESS, 0, NULL))
     && ok(SQLBindParameter(st, 2, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            LJMS_OWNER_MAX, 0, (SQLPOINTER)owner, 0, NULL))
     && ok(SQLBindParameter(st, 3, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            64, 0, (SQLPOINTER)node, 0, NULL))
     && ok(SQLBindParameter(st, 4, SQL_PARAM_INPUT, SQL_C_SLONG, SQL_INTEGER,
                            0, 0, &lease, 0, NULL))
     && ok(SQLBindParameter(st, 5, SQL_PARAM_INPUT, SQL_C_SLONG, SQL_INTEGER,
                            0, 0, &key, 0, NULL))
     && ok(SQLBindParameter(st, 6, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            16, 0, (SQLPOINTER)LJMS_NEW, 0, NULL))
     && ok(SQLExecute(st))) {
        n = affected(st);
    } else {
        diag(SQL_HANDLE_STMT, st, "claim");
    }

    stmt_close(st);
    return n;
}

int ljms_take(const char *owner, const char *node, int lease_s, ljms_task *out)
{
    int attempt;

    /* Bounded. Each attempt re-reads the head, so a loss means somebody took
     * that particular row and the next attempt sees a different one. Losing
     * all of them means being beaten on five successive rows. */
    for (attempt = 0; attempt < LJMS_TAKE_ATTEMPTS; attempt++) {
        int found = head(out);
        int won;

        if (found <= 0) return found;           /* nothing due, or an error */

        won = cas(out->id, owner, node, lease_s);
        if (won < 0)  return -1;
        if (won > 0) {
            /* head() read the row as it was before the claim; bring the field
             * the claim changed up to date rather than re-reading it. */
            out->attempts++;
            return 1;
        }
    }
    return 0;
}

/* Move a task out of IN_PROCESS, if this owner still holds it. The owner test
 * is what stops a worker that lost its lease from overwriting whoever has the
 * task now: it becomes a no-op returning 0 instead. */
static int finish(long id, const char *owner, const char *status, const char *message)
{
    static const char *q =
        "UPDATE QUEUE"
        "   SET status = ?, error = ?, lease_until = NULL,"
        "       finished = CURRENT TIMESTAMP"
        " WHERE id = ? AND owner = ? AND status = ?";

    SQLHSTMT st;
    SQLINTEGER key = (SQLINTEGER)id;
    SQLLEN ind = (message == NULL) ? SQL_NULL_DATA : SQL_NTS;
    int n = -1;

    if (stmt_open(&st) != 0) return -1;

    if (ok(SQLPrepare(st, (SQLCHAR *)q, SQL_NTS))
     && ok(SQLBindParameter(st, 1, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            16, 0, (SQLPOINTER)status, 0, NULL))
     && ok(SQLBindParameter(st, 2, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            1000, 0, (SQLPOINTER)message, 0, &ind))
     && ok(SQLBindParameter(st, 3, SQL_PARAM_INPUT, SQL_C_SLONG, SQL_INTEGER,
                            0, 0, &key, 0, NULL))
     && ok(SQLBindParameter(st, 4, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            LJMS_OWNER_MAX, 0, (SQLPOINTER)owner, 0, NULL))
     && ok(SQLBindParameter(st, 5, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            16, 0, (SQLPOINTER)LJMS_IN_PROCESS, 0, NULL))
     && ok(SQLExecute(st))) {
        n = affected(st);
    } else {
        diag(SQL_HANDLE_STMT, st, "finish");
    }

    stmt_close(st);
    return n;
}

int ljms_done(long id, const char *owner)
{
    return finish(id, owner, LJMS_DONE, NULL);
}

int ljms_error(long id, const char *owner, const char *message)
{
    return finish(id, owner, LJMS_ERROR, message);
}

/* Hand a task back at once, because this worker is stopping and will not
 * finish it. Without this a deliberate stop would leave the row untouchable
 * for the rest of its lease, which is what the lease is for when a worker
 * dies, not when it is asked to leave. */
int ljms_abandon(long id, const char *owner)
{
    static const char *q =
        "UPDATE QUEUE"
        "   SET status = ?, owner = NULL, started = NULL, lease_until = NULL,"
        "       error = 'released - worker shut down before finishing'"
        " WHERE id = ? AND owner = ? AND status = ?";

    SQLHSTMT st;
    SQLINTEGER key = (SQLINTEGER)id;
    int n = -1;

    if (stmt_open(&st) != 0) return -1;

    if (ok(SQLPrepare(st, (SQLCHAR *)q, SQL_NTS))
     && ok(SQLBindParameter(st, 1, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            16, 0, (SQLPOINTER)LJMS_NEW, 0, NULL))
     && ok(SQLBindParameter(st, 2, SQL_PARAM_INPUT, SQL_C_SLONG, SQL_INTEGER,
                            0, 0, &key, 0, NULL))
     && ok(SQLBindParameter(st, 3, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            LJMS_OWNER_MAX, 0, (SQLPOINTER)owner, 0, NULL))
     && ok(SQLBindParameter(st, 4, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            16, 0, (SQLPOINTER)LJMS_IN_PROCESS, 0, NULL))
     && ok(SQLExecute(st))) {
        n = affected(st);
    } else {
        diag(SQL_HANDLE_STMT, st, "abandon");
    }

    stmt_close(st);
    return n;
}

/* Return every row whose lease has run out to NEW: the worker holding it died,
 * hung, or lost the database. Owner-independent on purpose, so a dead worker's
 * tasks come back without waiting for that host to return under the same name,
 * and so a worker that has lost the database does not need to write anything
 * in order to be recovered. */
int ljms_recover_expired(void)
{
    static const char *q =
        "UPDATE QUEUE"
        "   SET status = ?, owner = NULL, started = NULL, lease_until = NULL,"
        "       error = 'lease expired - worker presumed dead'"
        " WHERE status = ? AND lease_until IS NOT NULL"
        "   AND lease_until < CURRENT TIMESTAMP";

    SQLHSTMT st;
    int n = -1;

    if (stmt_open(&st) != 0) return -1;

    if (ok(SQLPrepare(st, (SQLCHAR *)q, SQL_NTS))
     && ok(SQLBindParameter(st, 1, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            16, 0, (SQLPOINTER)LJMS_NEW, 0, NULL))
     && ok(SQLBindParameter(st, 2, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            16, 0, (SQLPOINTER)LJMS_IN_PROCESS, 0, NULL))
     && ok(SQLExecute(st))) {
        n = affected(st);
    } else {
        diag(SQL_HANDLE_STMT, st, "recover");
    }

    stmt_close(st);
    return n;
}

int ljms_put(const char *type, long ref_id, const char *payload)
{
    static const char *q =
        "INSERT INTO QUEUE (task_type, ref_id, payload, status)"
        " VALUES (?, ?, ?, ?)";

    SQLHSTMT st;
    SQLINTEGER ref = (SQLINTEGER)ref_id;
    SQLLEN ind = (payload == NULL) ? SQL_NULL_DATA : SQL_NTS;
    int rc = -1;

    if (stmt_open(&st) != 0) return -1;

    if (ok(SQLPrepare(st, (SQLCHAR *)q, SQL_NTS))
     && ok(SQLBindParameter(st, 1, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            LJMS_TYPE_MAX, 0, (SQLPOINTER)type, 0, NULL))
     && ok(SQLBindParameter(st, 2, SQL_PARAM_INPUT, SQL_C_SLONG, SQL_INTEGER,
                            0, 0, &ref, 0, NULL))
     && ok(SQLBindParameter(st, 3, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            LJMS_PAYLOAD_MAX, 0, (SQLPOINTER)payload, 0, &ind))
     && ok(SQLBindParameter(st, 4, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR,
                            16, 0, (SQLPOINTER)LJMS_NEW, 0, NULL))
     && ok(SQLExecute(st))) {
        rc = 0;
    } else {
        diag(SQL_HANDLE_STMT, st, "put");
    }

    stmt_close(st);
    return rc;
}
