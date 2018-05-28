package ru.eamosov.revolut.rest;

import org.eclipse.jetty.server.Request;
import ru.eamosov.revolut.BankServiceImpl;
import ru.eamosov.revolut.api.TransferResult;

/**
 * Created by fluder on 27/05/2018.
 */
public class TransferController extends AbstractRestController<TransferResult> {

    public TransferController(BankServiceImpl bankService) {
        super(bankService);
    }

    @Override
    public String getPath() {
        return "/transfer";
    }

    @Override
    public TransferResult handle(String target, Request baseRequest) throws Exception {

        return bankService.transfer(getUuidParameter(baseRequest, "src"),
                                    getUuidParameter(baseRequest, "dst"),
                                    getLongParameter(baseRequest, "amount"));

    }
}
