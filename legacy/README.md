# legacy: LJMS, 2001

The original LJMS: a small open-source library implementing the JMS interface
over an **in-memory** queue, published on SourceForge and since deleted there.
This may be the only surviving copy.

Different mechanism from the queue one level up, since this one lived inside
the process and did not survive a restart, but the same idea underneath: a unit
of work carries its own state, and whichever worker is free takes the next one.
The name of the current project comes from here.

## An example, not something to build

It is kept for reference and for the shape of the approach, not as working
software. It will not compile as it stands, deliberately:

- **Sun's `javax.jms` API sources have been removed.** The library was written
  against Sun's JMS 1.0.2 API and shipped a copy of those interfaces. They were
  Sun's, not ours to republish. That API lives on as Jakarta Messaging.
- Build output, jars, CVS metadata and a duplicated copy of the 0.4 release
  tree are gone as well. 26 MB became 2 MB, and what remains is the source,
  the project website of the time, and the examples.

If the in-memory approach is ever wanted again, reimplement it against an
interface that still exists rather than reviving this.

## If you copy this approach, fix these first

A read of the delivery path (`src/org/is/jms/SessionImpl.java`,
`LSessionRunnableImpl.java`) turns up four things worth knowing. They are left
as they were, since the archive cannot be compiled without the JMS API and an
untested fix to 25-year-old code is worth less than a note saying what to
watch for.

1. **`worker.stop()` in `SessionImpl.close()` no longer works at all.**
   `Thread.stop()` was deprecated as inherently unsafe in 1998 and has thrown
   `UnsupportedOperationException` since Java 20, so `close()` would throw on
   any current JVM. Replace it with a `volatile boolean` the loop checks, which
   is what the queue one level up does.

2. **The consumer loop is `while(true)` with nothing to stop it.** That is why
   `close()` had to reach for `Thread.stop()` in the first place. Same fix as
   above; the flag is the cause, the deprecated call was the symptom.

3. **It polls when it did not have to.** The loop sleeps 40 ms between
   deliveries, so it wakes 25 times a second forever and adds up to 40 ms of
   latency. An in-memory queue is one of the few places where you can do
   better: `Object.wait()` and `notify()` would wake the consumer exactly when
   a message arrives, at no cost while idle. The database version polls because
   a table gives you nothing to block on. This one had a doorbell and did not
   use it.

4. **One exception ends delivery permanently.** The `catch(JMSException)` sits
   outside the `while`, so any failure from `getMessage()` or from the
   application's `onMessage()` terminates the delivery thread for good, and a
   `RuntimeException` from a listener is not caught at all. On a daemon thread
   that is silent. Catch per message, decide what to do with the bad one, and
   keep the loop running.

## Licence

GNU GPL v2, see [LICENSE](LICENSE). Everything outside this directory is MIT.
The two are separate programs sharing no code, kept side by side as an archive
beside a rewrite.

Copyright (C) 2001 Vasili Gavrilov. Some files are marked SrcPortal, the
author's company at the time, since dissolved. The same hands either way.
