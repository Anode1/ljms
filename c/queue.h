/* ljms -- the queue table, in C. See queue.c.
 * Copyright (c) 2001, 2026 Vasili Gavrilov. MIT License; see ../LICENSE. */
#ifndef LJMS_QUEUE_H
#define LJMS_QUEUE_H

/* The four states. The same tokens the Java worker and doc/Queue_States.txt
 * use, because a status column you can read without the source is the point. */
#define LJMS_NEW        "NEW"
#define LJMS_IN_PROCESS "IN_PROCESS"
#define LJMS_DONE       "DONE"
#define LJMS_ERROR      "ERROR"

#define LJMS_TYPE_MAX     32
#define LJMS_PAYLOAD_MAX 4096
#define LJMS_OWNER_MAX    128

/* One row of the queue, as handed to the work function. */
typedef struct {
    long id;
    char type[LJMS_TYPE_MAX + 1];
    long ref_id;                        /* 0 when NULL; yours to interpret */
    char payload[LJMS_PAYLOAD_MAX + 1];
    int  attempts;                      /* times claimed, including now */
} ljms_task;

/* Open and close the connection. dsn is an ODBC data source name; DB2 CLI,
 * PostgreSQL, MySQL and SQL Server all present one. Returns 0 on success. */
int  ljms_connect(const char *dsn, const char *user, const char *pass);
void ljms_disconnect(void);

/* Take the next due task. Returns 1 and fills OUT, 0 if nothing is due,
 * -1 on error. This is where the optimistic lock lives; see queue.c. */
int ljms_take(const char *owner, const char *node, int lease_s, ljms_task *out);

/* Move a task out of IN_PROCESS. Each returns 1 if it landed, 0 if the lease
 * had already been lost, -1 on error. */
int ljms_done(long id, const char *owner);
int ljms_error(long id, const char *owner, const char *message);
int ljms_abandon(long id, const char *owner);

/* Return every row whose lease has run out to NEW. Returns the number of rows
 * recovered, or -1 on error. */
int ljms_recover_expired(void);

/* Put a task on the queue. Returns 0 on success. */
int ljms_put(const char *type, long ref_id, const char *payload);

#endif /* LJMS_QUEUE_H */
