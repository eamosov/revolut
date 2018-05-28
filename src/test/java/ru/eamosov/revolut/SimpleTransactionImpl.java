package ru.eamosov.revolut;

import ru.eamosov.revolut.api.Transaction;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by fluder on 27/05/2018.
 */
public class SimpleTransactionImpl implements Transaction {

    public UUID id;

    private UUID accountId;

    private UUID conjugateAccountId;

    private UUID conjugateTransactionId;

    private ZonedDateTime date;

    private long amount;

    private long balance;

    private UUID prevTransactionId;

    private String hash;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public UUID getAccountId() {
        return accountId;
    }

    @Override
    public UUID getConjugateAccountId() {
        return conjugateAccountId;
    }

    @Override
    public UUID getConjugateTransactionId() {
        return conjugateTransactionId;
    }

    @Override
    public ZonedDateTime getDate() {
        return date;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public long getBalance() {
        return balance;
    }

    @Override
    public UUID getPrevTransactionId() {
        return prevTransactionId;
    }

    @Override
    public String getHash() {
        return hash;
    }
}
