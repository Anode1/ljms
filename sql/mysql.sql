-- LJMS queue table, MySQL 5.7+ / 8.x.
--
-- One table. Everything the queue does is in here: what to run (task_type,
-- ref_id, payload), where it is (status), who has it (owner, lease_until),
-- and when it may run (not_before).
--
-- Rename it if you like and pass the name to QueueDAO; nothing below is
-- referenced by name from the code except the columns.

CREATE TABLE QUEUE (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  task_type   VARCHAR(32)  NOT NULL,            -- what kind of work
  ref_id      BIGINT           NULL,            -- a row id in your own tables
  payload     TEXT             NULL,            -- anything else the task needs
  status      VARCHAR(16)  NOT NULL DEFAULT 'NEW',
  not_before  DATETIME         NULL,            -- run no earlier than
  owner       VARCHAR(128)     NULL,            -- node + JVM incarnation
  owner_node  VARCHAR(64)      NULL,            -- node alone
  lease_until DATETIME         NULL,            -- this claim expires at
  error       VARCHAR(1000)    NULL,
  created     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  started     DATETIME         NULL,
  finished    DATETIME         NULL,
  PRIMARY KEY (id),
  KEY ix_queue_take  (status, id),              -- the head-of-queue read
  KEY ix_queue_lease (status, lease_until),     -- recovery sweep
  KEY ix_queue_node  (status, owner_node)       -- restart-is-the-retry sweep
);
