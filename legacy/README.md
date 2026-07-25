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

The delivery path in `src/org/is/jms/` has been given three fixes, none of
them tested, since the archive cannot be compiled without the JMS API. They
are the ones you would otherwise hit on the first run.

1. **`Thread.stop()` no longer merely deprecated, it throws.** It was
   deprecated as unsafe in 1998 and raises `UnsupportedOperationException` on
   Java 20 and later, so `close()` would have thrown on any current JVM.
   Replaced by a `volatile boolean` the loop checks, plus an interrupt so the
   thread does not sit in a sleep or a blocking read. Same in
   `util/ThreadUtils` and `net/Impl1Server`, where closing the server socket
   already unblocked `accept()` and the `stop()` was doing nothing useful.

2. **The consumer loop was `while(true)` with nothing to stop it**, which is
   why `close()` had to reach for `Thread.stop()`. The flag was the cause; the
   deprecated call was the symptom.

3. **One exception ended delivery for good.** The `catch(JMSException)` sat
   outside the loop, so any failure from `getMessage()` or from the
   application's `onMessage()` killed the delivery thread permanently, and a
   `RuntimeException` from a listener was not caught at all, silently, on a
   daemon thread. Now caught per message: report it and carry on.

Left alone: `net/Impl2*`, `Impl3Server`, `logmanager` and the examples still
call `Thread.stop()`, with comments saying interrupt was avoided deliberately
for JDK 1.0 applet compatibility. That was a real constraint in 2001 and those
classes are outside the queue path.

A note on the 40 ms sleep in the delivery loop, since it looks like polling and
mostly is not: the socket transport blocks inside `getMessage()`, so there the
sleep is a throttle between deliveries. Only `HttpTransport` genuinely polls,
issuing a fresh GET per call, and in 2001 HTTP left little choice.

## Licence

GNU GPL v2, see [LICENSE](LICENSE). Everything outside this directory is MIT.
The two are separate programs sharing no code, kept side by side as an archive
beside a rewrite.

Copyright (C) 2001 Vasili Gavrilov. Some files are marked SrcPortal, the
author's company at the time, since dissolved. The same hands either way.
