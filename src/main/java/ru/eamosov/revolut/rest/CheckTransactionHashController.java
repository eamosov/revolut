package ru.eamosov.revolut.rest;

import org.eclipse.jetty.server.Request;
import ru.eamosov.revolut.BankServiceImpl;

/**
 * Created by fluder on 27/05/2018.
 */
public class CheckTransactionHashController extends AbstractRestController<Boolean> {

    public CheckTransactionHashController(BankServiceImpl bankService) {
        super(bankService);
    }

    @Override
    public String getPath() {
        return "/checkTransactionHash";
    }

    @Override
    public Boolean handle(String target, Request baseRequest) throws Exception {
        return bankService.checkTransactionHash(getUuidParameter(baseRequest, "id"));
    }
}
