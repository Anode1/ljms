# The worker in C

An illustration, not a second implementation. The Java version one level up is
the project; this shows that the design is a few statements and a loop rather
than anything language-specific, and that the same table serves a C worker and
a Java one at the same time, since neither holds a lock and both agree on four
strings.

```
queue.h    the four states, one task, and the operations
queue.c    the SQL, including the compare-and-swap that is the whole lock
worker.c   the loop, with your work in work()
```

## Status: not compiled

There is no ODBC on the machine this was written on, so **none of this has been
built or run.** Treat it as you would the PostgreSQL and Oracle dialects in the
Java version: transcribed carefully from the documentation, and waiting for the
first person with the headers to say whether it holds.

## No Makefile, no tests

Deliberately. A build system and a test harness are project infrastructure, not
part of the idea, and there is already a template for that:
[aisconfig](https://github.com/Anode1/aisconfig), which is an ANSI C project
skeleton with POSIX argument handling and a Makefile that does not need editing
when you add a file. Start there and drop these three files in.

Roughly:

```sh
cc -std=c99 -Wall -Wextra -pedantic -c queue.c worker.c
cc queue.o worker.o -lodbc -o ljms-worker
```

## Why ODBC

Because DB2 CLI is ODBC, which is where this shape was first written in C, and
because it reaches PostgreSQL, MySQL, SQL Server and the rest through their own
drivers without this repository vendoring a client library it would then have to
keep current.

The two expressions that differ between databases are `#define`s at the top of
`queue.c`, one for date arithmetic and one for the row limit, exactly as
`Dialect.java` holds them on the Java side. The defaults are DB2 spelling.

## What to read

`cas()` in `queue.c`. Everything else is bookkeeping around it:

```c
UPDATE QUEUE SET status = ?, owner = ?, ... WHERE id = ? AND status = ?
```

The second `status` test is the lock. `SQLRowCount` afterwards is its return
value: one row means the task is yours, zero means another worker got there
first and you read the head again. Nothing is held while `work()` runs, which
is why you can start as many of these as you like.

## Style

Follows [iac](https://github.com/Anode1/iac) and
[ais](https://github.com/Anode1/ais): C99, no dynamic allocation, fixed buffers
with bounds checks, a comment above each function saying why rather than what,
and errors returned rather than thrown.
