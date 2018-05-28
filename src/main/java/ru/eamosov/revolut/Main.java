package ru.eamosov.revolut;

import com.j256.ormlite.db.HsqldbDatabaseType;
import com.j256.ormlite.db.PostgresDatabaseType;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import ru.eamosov.revolut.jetty.JettyServer;

/**
 * Created by fluder on 19/05/2018.
 */
public class Main {

    public static void main(String argv[]) throws Exception {

        final String pgUri = System.getProperty("PG_URI");

        final ConnectionSource connection;

        if (pgUri != null) {
            connection = new JdbcPooledConnectionSource(pgUri, new PostgresDatabaseType());
        } else {
            connection = new JdbcPooledConnectionSource("jdbc:hsqldb:mem:revolut", new HsqldbDatabaseType());
        }

        final JettyServer jettyServer = new JettyServer(connection, 8080);
        jettyServer.start();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }

        jettyServer.stop();
    }
}
