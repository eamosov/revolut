package ru.eamosov.revolut.rest;

import org.eclipse.jetty.server.Request;
import ru.eamosov.revolut.BankServiceImpl;
import ru.eamosov.revolut.api.Transaction;

import java.util.List;

/**
 * Created by fluder on 27/05/2018.
 */
public class HistoryController extends AbstractRestController<List<? extends Transaction>> {

    public HistoryController(BankServiceImpl bankService) {
        super(bankService);
    }

    @Override
    public String getPath() {
        return "/history";
    }

    @Override
    public List<? extends Transaction> handle(String target, Request baseRequest) throws Exception {
        return bankService.getHistory(getUuidParameter(baseRequest, "id"));
    }
}
