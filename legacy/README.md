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

The delivery path in `src/org/is/jms/` has been brought up to modern practice,
so that nobody copying it inherits code that cannot run. None of it is tested,
since the archive does not compile without the JMS API.

Each of these was the right answer in 2001 and is the wrong one now. That is
the only reason they were touched.

1. **`Thread.stop()` now throws rather than merely being deprecated.** In 2001
   it was the ordinary way to end a thread, and several call sites say in their
   comments that interrupt was avoided on purpose, for JDK 1.0 applet
   runtimes. Since Java 20 it raises `UnsupportedOperationException`, so those
   paths would fail outright on any current JVM.

   Every one of them is now an interrupt, throughout the library and the
   examples, and in the delivery loop a `volatile boolean` the loop checks as
   well, so the thread stops at a defined point rather than wherever it
   happened to be. In `net/Impl1Server` the call was doing nothing anyway,
   since closing the server socket already unblocks `accept()`.

   Nobody needs applet compatibility now, and leaving code that cannot run in
   order to preserve the reason it was written that way helps no one.

2. **The consumer loop was `while(true)` with nothing to stop it**, which is
   why `close()` had to reach for `Thread.stop()`. The flag was the cause; the
   deprecated call was the symptom.

3. **One exception ended delivery for good.** The `catch(JMSException)` sat
   outside the loop, so any failure from `getMessage()` or from the
   application's `onMessage()` killed the delivery thread permanently, and a
   `RuntimeException` from a listener was not caught at all, silently, on a
   daemon thread. Now caught per message: report it and carry on.

`Thread.suspend()` and `resume()`, which throw for the same reason, do not
appear anywhere here.

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
