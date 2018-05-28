package ru.eamosov.revolut.rest;

import org.eclipse.jetty.server.Request;
import ru.eamosov.revolut.BankServiceImpl;

import java.util.Objects;
import java.util.UUID;

/**
 * Created by fluder on 27/05/2018.
 */
public abstract class AbstractRestController<R> {

    protected final BankServiceImpl bankService;

    public AbstractRestController(BankServiceImpl bankService) {
        this.bankService = bankService;
    }

    protected UUID getUuidParameter(Request request, String name) {
        return UUID.fromString(getStringParameter(request, name));
    }

    protected Long getLongParameter(Request request, String name) {
        return Long.parseLong(getStringParameter(request, name));
    }

    protected String getStringParameter(Request request, String name) {
        final String v = request.getParameter(name);
        Objects.requireNonNull(v, "required parameter \"" + name + "\" not found");
        return v;
    }

    public abstract String getPath();

    public abstract R handle(String target, Request baseRequest) throws Exception;
}
