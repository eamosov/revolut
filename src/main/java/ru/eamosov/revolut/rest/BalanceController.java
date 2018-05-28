package ru.eamosov.revolut.rest;

import org.eclipse.jetty.server.Request;
import ru.eamosov.revolut.BankServiceImpl;

/**
 * Created by fluder on 27/05/2018.
 */
public class BalanceController extends AbstractRestController<Long> {

    public BalanceController(BankServiceImpl bankService) {
        super(bankService);
    }

    @Override
    public String getPath() {
        return "/balance";
    }

    @Override
    public Long handle(String target, Request baseRequest) throws Exception {
        return bankService.getBalance(getUuidParameter(baseRequest, "id"));
    }
}
