package ru.eamosov.revolut;

import com.j256.ormlite.db.PostgresDatabaseType;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import ru.eamosov.revolut.jetty.JettyServer;

/**
 * Created by fluder on 19/05/2018.
 */
public class Main {

    public static void main(String argv[]) throws Exception {

        final ConnectionSource connection = new JdbcPooledConnectionSource(System.getProperty("PG_URI", "jdbc:postgresql://localhost/revolut?user=revolut"), new PostgresDatabaseType());
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
