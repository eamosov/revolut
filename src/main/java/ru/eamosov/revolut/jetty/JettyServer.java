package ru.eamosov.revolut.jetty;

import com.j256.ormlite.support.ConnectionSource;
import org.eclipse.jetty.server.Server;
import ru.eamosov.revolut.BankServiceImpl;
import ru.eamosov.revolut.rest.BalanceController;
import ru.eamosov.revolut.rest.CheckTransactionHashController;
import ru.eamosov.revolut.rest.CreateController;
import ru.eamosov.revolut.rest.HistoryController;
import ru.eamosov.revolut.rest.TransferController;

import java.sql.SQLException;

/**
 * Created by fluder on 27/05/2018.
 */
public class JettyServer {

    private final Server jetty;

    public JettyServer(final ConnectionSource connection, int port) throws SQLException {
        final BankServiceImpl bankService = new BankServiceImpl(connection);

        final JettyRestHandler jettyRestHandler = new JettyRestHandler();

        jettyRestHandler.registerController(new CreateController(bankService));
        jettyRestHandler.registerController(new TransferController(bankService));
        jettyRestHandler.registerController(new BalanceController(bankService));
        jettyRestHandler.registerController(new HistoryController(bankService));
        jettyRestHandler.registerController(new CheckTransactionHashController(bankService));

        jetty = new Server(port);
        jetty.setHandler(jettyRestHandler);
    }

    public void start() throws Exception {
        jetty.start();
    }

    public void stop() throws Exception {
        jetty.stop();
    }
}
