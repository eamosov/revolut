package ru.eamosov.revolut.rest;

import org.eclipse.jetty.server.Request;
import ru.eamosov.revolut.BankServiceImpl;

/**
 * Created by fluder on 27/05/2018.
 */
public class CreateController extends AbstractRestController<Boolean> {

    public CreateController(BankServiceImpl bankService) {
        super(bankService);
    }

    @Override
    public String getPath() {
        return "/create";
    }

    @Override
    public Boolean handle(String target, Request baseRequest) throws Exception {
        return bankService.createAccount(getUuidParameter(baseRequest, "id"),
                                         getLongParameter(baseRequest, "balance"));
    }
}
