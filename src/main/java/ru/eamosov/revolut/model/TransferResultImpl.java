package ru.eamosov.revolut.model;

import ru.eamosov.revolut.api.Transaction;
import ru.eamosov.revolut.api.TransferResult;

/**
 * Created by fluder on 27/05/2018.
 */
public class TransferResultImpl implements TransferResult {

    private final Transaction transaction;
    private final ErrCode code;
    private final String error;

    public TransferResultImpl(Transaction transaction) {
        this.transaction = transaction;
        this.code = null;
        this.error = null;
    }

    public TransferResultImpl(ErrCode code, String error) {
        this.code = code;
        this.error = error;
        this.transaction = null;
    }

    @Override
    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public ErrCode getCode() {
        return code;
    }

    @Override
    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "TransferResult{" +
            "transaction=" + transaction +
            ", code=" + code +
            ", error='" + error + '\'' +
            '}';
    }
}
