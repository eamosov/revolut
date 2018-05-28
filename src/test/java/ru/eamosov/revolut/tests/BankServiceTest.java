package ru.eamosov.revolut.tests;

import ru.eamosov.revolut.api.BankService;
import ru.eamosov.revolut.BankServiceImpl;

import java.sql.SQLException;

/**
 * Test {@link BankService} as local service
 */
public class BankServiceTest extends BaseTest {

    final BankServiceImpl bankService = new BankServiceImpl(connection);

    public BankServiceTest() throws SQLException {
    }

    @Override
    protected BankService getBankService() {
        return bankService;
    }


}