/*
 * Copyright (c) 2001, 2026 Vasili Gavrilov. MIT License; see LICENSE.
 */
package org.ljms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.lang.reflect.Proxy;

/**
 * Throughput measurement, so the numbers in the README are reproducible rather
 * than asserted. Run it on your own hardware and database:
 *
 * <pre>   ant bench</pre>
 *
 * Set the constants below first, same scratch database as the tests, and it
 * drops and recreates the table.
 *
 * What it measures is one full task cycle: take() + done(), which is one
 * SELECT and two UPDATEs. It does no work in between, so the numbers are the
 * queue's own overhead and nothing else, an upper bound you will never see in
 * production, where the work dominates.
 *
 * It runs each worker count twice, unpooled and pooled, because the difference
 * is the single largest factor and it is not the queue's SQL. Reporting only
 * the pooled number would flatter it; reporting only the unpooled one would
 * misrepresent what the design costs.
 */
public class Bench {

    // ------------------------------------------------------------------
    // EDIT THESE, a scratch database. Drops and recreates the QUEUE table.
    // ------------------------------------------------------------------

    private static final String DB_URL      = "jdbc:mysql://127.0.0.1/ljms_test";
    private static final String DB_USER     = "<db-user>";
    private static final String DB_PASSWORD = "<db-password>";

    private static final int TASKS   = 500;
    private static final int[] SIZES = { 1, 2, 4, 8 };

    /** A pool in its crudest form: one connection per thread, never closed. */
    private static final ThreadLocal<Connection> HELD = new ThreadLocal<Connection>();


    public static void main(String[] args) throws Exception {

        Connections direct = () -> DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        Connections pooled = () -> {
            Connection held = HELD.get();
            if (held == null || held.isClosed()) HELD.set(held = direct.getConnection());
            // LJMS closes what it opens, so hand back a wrapper that ignores close()
            return (Connection) Proxy.newProxyInstance(
                    Bench.class.getClassLoader(), new Class[] { Connection.class },
                    (p, m, a) -> "close".equals(m.getName()) ? null : m.invoke(HELD.get(), a));
        };

        createTable(direct);

        direct.getConnection().close();                    // warm the driver
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) direct.getConnection().close();
        System.out.printf("%nbare connect + close: %.1f ms each%n%n",
                          (System.currentTimeMillis() - t0) / 20.0);

        System.out.printf("%-9s %-8s %12s %10s%n", "workers", "conns", "tasks/sec", "ms/task");
        System.out.println("---------------------------------------------");
        for (int workers : SIZES) run(direct, workers, "direct");
        for (int workers : SIZES) run(pooled, workers, "pooled");

        drop(direct);
    }


    private static void run(Connections db, int workers, String label) throws Exception {

        final QueueDAO queue = new QueueDAO(db, "QUEUE", Dialect.MYSQL);
        truncate(db);
        for (int i = 0; i < TASKS; i++) queue.put("BENCH", Long.valueOf(i), null);

        final int[] done = new int[workers];
        Thread[] threads = new Thread[workers];
        long t0 = System.currentTimeMillis();

        for (int w = 0; w < workers; w++) {
            final int index = w;
            final String node  = "bench" + w;
            final String owner = node + " 2026-01-01 00:00:00.000";
            threads[w] = new Thread() {
                public void run() {
                    try {
                        int emptyInARow = 0;
                        while (emptyInARow < 5) {
                            Task task = queue.take(owner, node, 60);
                            if (task == null) { emptyInARow++; continue; }
                            emptyInARow = 0;
                            queue.done(task.id, owner);
                            done[index]++;
                        }
                    }
                    catch (Exception e) { e.printStackTrace(); }
                }
            };
            threads[w].start();
        }
        for (Thread t : threads) t.join();

        long ms = System.currentTimeMillis() - t0;
        int total = 0;
        for (int d : done) total += d;
        System.out.printf("%-9d %-8s %12.0f %10.2f%n",
                          workers, label, total * 1000.0 / ms, (double) ms / total);
    }


    private static void createTable(Connections db) throws Exception {
        exec(db, "DROP TABLE IF EXISTS QUEUE");
        exec(db, "CREATE TABLE QUEUE ("
               + "  id BIGINT NOT NULL AUTO_INCREMENT, task_type VARCHAR(32) NOT NULL,"
               + "  ref_id BIGINT NULL, payload TEXT NULL,"
               + "  status VARCHAR(16) NOT NULL DEFAULT 'NEW', not_before DATETIME NULL,"
               + "  owner VARCHAR(128) NULL, owner_node VARCHAR(64) NULL,"
               + "  lease_until DATETIME NULL, error VARCHAR(1000) NULL,"
               + "  created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
               + "  started DATETIME NULL, finished DATETIME NULL,"
               + "  PRIMARY KEY (id), KEY ix_queue_take (status, id),"
               + "  KEY ix_queue_lease (status, lease_until),"
               + "  KEY ix_queue_node (status, owner_node))");
    }

    private static void truncate(Connections db) throws Exception { exec(db, "TRUNCATE TABLE QUEUE"); }
    private static void drop(Connections db)     throws Exception { exec(db, "DROP TABLE IF EXISTS QUEUE"); }

    private static void exec(Connections db, String statement) throws Exception {
        Connection con = null;
        Statement st = null;
        try {
            con = db.getConnection();
            st = con.createStatement();
            st.execute(statement);
        }
        finally {
            if (st != null) try { st.close(); } catch (Exception ignore) {}
            if (con != null) try { con.close(); } catch (Exception ignore) {}
        }
    }
}
