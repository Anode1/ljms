# LJMS: a queue that is one table

**445 lines of Java. One table. No broker, no dependencies, no locks.**\
*A queue is a state machine over rows. Everything else is packaging.*

Most work queues arrive as infrastructure: a broker to install, monitor, back up, upgrade and page someone about at 3am. LJMS is a table in the database you already have, and a loop that polls it. You copy six files into your project, put your work in one empty method, and run as many workers as you like.

It is a template, not a framework. There is no jar to depend on and no interface it makes you implement — you own the copy, and when you need it to do something different you edit it.

```
src/org/ljms/
  Queue.java          46 lines   the four states
  Task.java           31 lines   one row
  Connections.java    28 lines   the one thing you supply
  Dialect.java        40 lines   the entire portability surface: one SQL expression
  QueueDAO.java      476 lines   the SQL
  Processor.java     272 lines   the loop, with your work() in the middle
```

## Quick start

```sh
mysql mydb < sql/mysql.sql          # or postgres.sql / oracle.sql
ant                                 # compile; JDK only
ant prove                           # check the state machine (no database, 19ms)
```

Two things to edit, both marked `EDIT THESE`: the database constants at the top of `Processor.java`, and the ones in `QueueTests.java` if you want to run the tests. There is no properties file and no config class — every project already has a way of holding credentials, and a template that insists on its own just leaves you a layer to rip out.

Put your work in `Processor.work()`:

```java
protected void work(Task task) throws Exception {
    if ("PUBLISH_STUDY".equals(task.type)) publish(task.refId);
    else throw new Exception("Unknown task type: " + task.type);
}
```

Enqueue from anywhere:

```java
Connections db = () -> DriverManager.getConnection(url, user, password);
new QueueDAO(db).put("PUBLISH_STUDY", studyId, null);
```

Run a worker (`./queue.sh start`, cron, systemd, or in-process):

```java
Processor worker = new Processor(db);
worker.start();
```

That is the whole API. `put`, `take`, `done`, `error` — the names `java.util.concurrent.BlockingQueue` already uses.

## The states

```
NEW        = start | IN_PROCESS[take]
IN_PROCESS = DONE[done] | ERROR[error] | NEW[^expire]
ERROR      = NEW[restart]
DONE       = terminal
```

### The prover

That block in [`doc/Queue_States.txt`](doc/Queue_States.txt) is not documentation of the code — it **is** the specification, and `ant prove` reads it:

```
$ ant prove
OK (7 tests)      0.019s, no database
```

It parses the character form and checks six properties of the graph:

| check | catches |
| --- | --- |
| a start state exists | a machine with no entry |
| every state reachable from start | a state nothing can ever reach |
| every non-terminal has an outgoing edge | a task that can get stuck forever |
| terminals have no outgoing edge | a "final" state that isn't |
| no (state, event) maps to two targets | non-determinism |
| some path reaches a terminal | a machine no task can ever leave |

Add a state to the doc and it is checked automatically — nobody has to think of the test case. A finite automaton is the tractable tier of the Chomsky hierarchy, so these are decidable properties of the graph, not opinions about it. This is the old discipline of drawing the state diagram and proving correctness before writing the code, except the diagram is machine-readable and the proof runs in CI.

What it deliberately **cannot** see is liveness: the machine has cycles (`NEW → IN_PROCESS → NEW` on expiry, `ERROR → NEW` on restart) and reachability says nothing about termination. That argument is made by hand in the doc, and it rests on neither edge firing by itself — expiry needs a worker to die, restart needs a person. There is no automatic retry edge, which is exactly what would make the first cycle self-sustaining.

## Why

**Nothing is ever locked.** Every transition is one predicated single-row `UPDATE` under autocommit:

```sql
UPDATE QUEUE SET status='IN_PROCESS', owner=? WHERE id=? AND status='NEW'
```

That is a compare-and-swap — the affected-row count is the return value, 1 means you won, 0 means someone else did. No `SELECT ... FOR UPDATE`, no transaction spanning the read and the write, no transaction spanning the work, no table locks, no `synchronized`. The only lock is the one the database takes *inside* that statement, released at statement end.

This is why you can run as many workers as you like. Contention costs wasted attempts, never blocked waiters, so the cost grows linearly with workers instead of collapsing past a threshold. The pattern it avoids — take a lock, do the work, commit — looks perfect in testing, where there is no contention, and convoys under real load: workers queue behind the leader, the pool drains, timeouts cascade, and by then it is structural.

**The race is actually tested.** `testRaceNoTaskTakenTwice` runs 8 workers against 200 tasks and asserts every task was handed out exactly once. It has been verified to fail: delete `AND status = ?` from the compare-and-swap and it reports ~200 duplicates — while **every other test in the suite still passes.** That is the shape of concurrency bugs, and why a queue you cannot run this test against is a queue you are guessing about.

**No automatic retries.** A task that fails goes to `ERROR` and stays there: one failure, one log line, one row to look at. Retry loops multiply one problem across the log and hide how many distinct problems there are. The fix is a human fix — to the code or to the data — and restarting the worker is the retry: startup returns that worker's `ERROR` rows to `NEW`. Meanwhile the queue flows past a parked task instead of stalling on it.

**Recovery is by lease, not by owner.** Taking a task sets `lease_until`; a row still `IN_PROCESS` past its lease is presumed abandoned and returned to `NEW` by whichever worker notices. One timestamp comparison, and it covers three cases owner-matching cannot: a dead worker's tasks come back without waiting for that host to return under the same name; two workers sharing a node id cannot steal each other's in-flight rows; and when the *database* is what failed, a worker cannot write "put me back" anywhere — the lease expires on its own.

**Delivery is at-least-once.** A worker that is slow rather than dead can lose its lease and have its task run twice. Make tasks idempotent, or fence them with `extendLease()`, which returns false once the lease is gone. LJMS says this out loud rather than implying exactly-once, which no queue with a network in it can honestly offer.

**States are names, not codes.** `NEW`, `IN_PROCESS`, `DONE`, `ERROR` — the same symbol in the spec, the constant, the SQL literal and the table, so `SELECT status, COUNT(*) ... GROUP BY status` answers the question without the source. Numeric codes need two mappings that can drift, and they hide errors: this design descends from one that used them, and whose recovery swept a hand-written list — `('90','02','81','82','86','19')` — that silently stopped covering every in-flight state once new ones were added. Nobody reviewing that line could see what was missing.

## Performance

Reproducible, not asserted — `ant bench` runs this on your own hardware. One task here is `take()` + `done()`: one SELECT and two UPDATEs, with no work in between, so these are the queue's own overhead and nothing else.

Intel i7-1165G7, MySQL 8.0.46, JDK 21, local SSD, default `innodb_flush_log_at_trx_commit=1`:

| workers | tasks/sec (direct) | tasks/sec (pooled) |
| --- | --- | --- |
| 1 | 83 | 269 |
| 2 | 179 | 394 |
| 4 | 291 | **412** |
| 8 | 295 | 365 |

Three things that table says, more useful than the numbers themselves:

**Connection setup dominates if you let it.** A bare `DriverManager` connect costs ~11 ms here, and LJMS opens one per operation. Handing it a pooled `Connections` is a one-line lambda and roughly triples throughput. That is why `Connections` is an interface and not a URL.

**The floor is durability, not SQL.** Even pooled, ~2.4 ms per task is mostly two commits — the claim and the outcome — and a commit is an fsync. You cannot go below two durable writes per task and still know, after a crash, whether the task ran. Batching claims (take N, one commit) is the only real lever, and it trades exactly that knowledge.

**Contention costs, but does not collapse.** Eight workers are slower than four: they read the same head row, and the losers retry. Throughput sags; nothing blocks, nothing times out, nothing cascades. A design that holds a lock across the work does not sag there — it falls over, and only in production.

If you need thousands of tasks a second, use a broker. If you need hundreds, this is a table.

## When it fits

**Good fit**
- Anything already backed by a relational database, where a broker would be the only new piece of infrastructure.
- Work measured in seconds to hours, at tens to thousands of tasks a day: publishing, imports, report generation, model runs, batch jobs, sending mail.
- Sequencing and admission control — "run these one at a time", "not more than N at once", "start this at 2am" — which `not_before` and a one-line capacity check handle without a scheduler.
- Small teams with nobody to operate a broker, and anyone who has to be able to answer "what is the queue doing?" with a `SELECT`.
- Regulated or audited work, where every state change being a visible row with a timestamp is worth more than throughput.

**Bad fit**
- Thousands of messages a second — polling and per-task commits are the wrong shape; use a broker.
- Fan-out, pub/sub, topics, replay, or many consumers per message. LJMS hands each task to exactly one worker.
- Sub-second latency requirements: a poll interval is a poll interval. You can shorten it, but that is load, not a design.
- No database, or a database you must not add a table to.

## Questions

**Why not RabbitMQ, Kafka, or SQS?**
Because they are a service, and you already run a database. A broker means another thing to install, secure, monitor, back up, upgrade and be paged about, plus the operational question of what happens when your queue and your data disagree. LJMS is in the same transaction domain as your data: enqueueing a task and the work that justifies it can share one commit. If you need fan-out, topics, cross-datacentre replication or a million messages a second, use a broker — that is what they are for. If you need "run these jobs, one at a time, and do not lose them", this is a table.

**Why not Quartz, db-scheduler, or JobRunr?**
They are good, and they are schedulers with a queue inside, framework-shaped and usually Spring-oriented. LJMS is the queue without the scheduler, and small enough to read in one sitting — which matters more than features when the thing goes wrong at 3am and you have to reason about state you cannot see.

**Why not `java.util.concurrent`?**
Because that queue dies with the JVM. LJMS survives a restart, a crash, and a machine, because the queue is a table.

**Is polling not wasteful?**
Yes, and it is unavoidable here — that is the honest trade. A broker can block you in the kernel (`epoll`) until a message arrives and wake you with zero wasted work; a database has no such doorbell, so a worker has to ask. What you pay is one indexed `SELECT` per worker per interval. What you get is no listener to keep alive, no reconnect logic, no message lost on a dropped connection, and a worker you can `kill -9` at any instant without consequence, because it holds nothing. At the scale where the polling itself is a real cost, you have outgrown this design.

**Only one task at a time per worker?**
Yes, and it is deliberate: it makes the state machine four states instead of a concurrency model. Want more parallelism? Start more workers. They coordinate through the table and hold nothing, which is the entire point.

**Is 445 lines not too small to trust?**
It is small enough to *read*, which is a stronger form of trust than "it is popular". The design has been in production a long time — the ancestor of this queue has run Ontario health-facility submissions for over fifteen years, and a sibling has run hospital ML pipelines for five. What is new here is the refinement: the lease, the portable SQL, the specified state machine, and the tests. Those are the parts the originals did not have, and writing them found real bugs in both the originals and this rewrite.

**Which databases?**
MySQL, PostgreSQL, Oracle and SQL Server ship in `Dialect`. Everything else is plain SQL — the only thing the standard leaves to vendors is date arithmetic, so it lives in that one enum. Adding a database is one line, not an abstraction layer. (Only MySQL is covered by a CI-run test today; the others are transcribed and unrun. Say so in an issue if you run one.)

## Layout

```
src/          the six files you copy
test/         StateMachineTest (the prover), QueueTests (behaviour + race), Bench
sql/          mysql.sql, postgres.sql, oracle.sql
doc/          Queue_States.txt  — the specification the prover reads
              development.txt   — internals, invariants, porting, design notes
legacy/       the original LJMS: a P2P light queue, kept for its history
queue.sh      start | run | stop | status
```

Run the tests against a **scratch** database — they drop and recreate the table on every test method:

```sh
ant test        # after setting the DB constants at the top of QueueTests.java
```

## History

The first LJMS, in 2001, was a small open-source library with an in-memory queue. Different mechanism entirely — the queue lived in the process — but the same idea: a unit of work carries its own state, and whichever worker is free takes the next one. It happened to predate Sun's JMS, which is where the name came from; it is kept here for continuity, not as any sort of claim. That original is in [`legacy/`](legacy/).

The idea has since carried four production systems, and this is the fourth pass at it: the version that moves the queue into a table, where it survives the process. The direct ancestor has been running Ontario health-facility submissions for over fifteen years, and a sibling has run hospital ML pipelines for five. What is new here is the lease, the portable SQL, the specification with a prover, and the tests — writing which found real bugs in both the ancestors and this rewrite.

The shape is much older than any of it. A mainframe job queue — JES on z/OS — does the same thing: work sits on a queue in an explicit state, an operator can see it, a failed job is *held* for a person rather than retried blindly, and killing the address space loses nothing because the state of the work is data rather than process memory. That last property is most of why those systems are so hard to kill, and it is the one worth copying. Everything that loses work on restart — in-memory queues, actor mailboxes, thread pools — keeps the state of the work inside the process doing the work.

## Related

Part of a family of deliberately thin tools: [ais](https://github.com/Anode1/ais) (associative index, plain text instead of a database), [agent-recipes](https://github.com/Anode1/agent-recipes) (practices for working with coding agents). The common thread is that a small thing you can read beats a large thing you must trust.

## Licence

MIT — see [LICENSE](LICENSE).
