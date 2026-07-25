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

## Licence

GNU GPL v2, see [LICENSE](LICENSE). Everything outside this directory is MIT.
The two are separate programs sharing no code, kept side by side as an archive
beside a rewrite.

Copyright (C) 2001 Vasili Gavrilov. Some files are marked SrcPortal, the
author's company at the time, since dissolved. The same hands either way.
