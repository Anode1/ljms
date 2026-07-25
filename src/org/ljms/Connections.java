package org.ljms;

import java.sql.Connection;

/**
 * Where LJMS gets a database connection. The only thing you have to supply.
 *
 * One method, so anything you already have adapts in a line, a pool, a JNDI
 * DataSource, a plain DriverManager call:
 *
 * <pre>
 *   Connections db = () -&gt; DriverManager.getConnection(url, user, password);
 *   Connections db = () -&gt; myDataSource.getConnection();
 *   Connections db = () -&gt; myExistingConnectionProvider.getConnection();
 * </pre>
 *
 * LJMS opens a connection per operation and closes it in a finally, so a bare
 * DriverManager works and a pool is an optimisation, not a requirement. It
 * never holds one across a task, which is what lets a worker sit idle for
 * hours without occupying anything.
 *
 * Deliberately not javax.sql.DataSource: implementing that interface means
 * writing seven methods you do not need. Adapting one is a lambda.
 */
public interface Connections {

    Connection getConnection() throws Exception;
}
