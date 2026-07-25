-- LJMS queue table, SQL Server 2012+.
-- Use Dialect.MSSQL.

CREATE TABLE QUEUE (
  id          BIGINT         NOT NULL IDENTITY(1,1),
  task_type   VARCHAR(32)    NOT NULL,          -- what kind of work
  ref_id      BIGINT             NULL,          -- a row id in your own tables
  payload     VARCHAR(MAX)       NULL,          -- anything else the task needs
  status      VARCHAR(16)    NOT NULL DEFAULT 'NEW',
  not_before  DATETIME2          NULL,          -- run no earlier than
  owner       VARCHAR(128)       NULL,          -- node + JVM incarnation
  owner_node  VARCHAR(64)        NULL,          -- node alone
  lease_until DATETIME2          NULL,          -- this claim expires at
  error       VARCHAR(1000)      NULL,
  created     DATETIME2      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  started     DATETIME2          NULL,
  finished    DATETIME2          NULL,
  PRIMARY KEY (id)
);

CREATE INDEX ix_queue_take  ON QUEUE (status, id);            -- the head-of-queue read
CREATE INDEX ix_queue_lease ON QUEUE (status, lease_until);   -- recovery sweep
CREATE INDEX ix_queue_node  ON QUEUE (status, owner_node);    -- restart-is-the-retry sweep
