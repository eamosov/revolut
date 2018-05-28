package ru.eamosov.revolut.tests;

import org.junit.After;
import org.junit.Before;
import ru.eamosov.revolut.api.BankService;
import ru.eamosov.revolut.HttpClientBankService;
import ru.eamosov.revolut.jetty.JettyServer;

import java.sql.SQLException;

/**
 * Test {@link BankService} as REST service
 */
public class BankServiceHttpTest extends BaseTest {

    static int port = 8080;

    private JettyServer jettyServer;
    private BankService bankService = new HttpClientBankService("localhost", port);

    public BankServiceHttpTest() throws SQLException {
    }

    @Override
    protected BankService getBankService() {
        return bankService;
    }

    @Before
    public void startRestService() throws Exception {
        jettyServer = new JettyServer(connection, port);
        jettyServer.start();
    }

    @After
    public void stopRestService() throws Exception {
        jettyServer.stop();
    }
}
