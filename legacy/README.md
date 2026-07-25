# legacy

The first LJMS, from 2001: a small open-source library with an in-memory
queue. Different mechanism from the current one — the queue lived in the
process, so it did not survive a restart — but the same idea: a unit of work
carries its own state, and whichever worker is free takes the next one.

The source is not published here yet.

The current LJMS is one level up: the same idea with the queue in a table,
where it outlives the process.
