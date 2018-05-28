package ru.eamosov.revolut.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.codec.digest.DigestUtils;
import ru.eamosov.revolut.api.Transaction;
import ru.eamosov.revolut.utils.DateTimePersister;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by fluder on 26/05/2018.
 */
@DatabaseTable(tableName = "transactions")
public class TransactionImpl implements Transaction {

    @DatabaseField(id = true)
    private UUID id;

    @DatabaseField(canBeNull = false, indexName="transactions_accountId_date_idx")
    private UUID accountId;

    @DatabaseField
    private UUID conjugateAccountId;

    @DatabaseField
    private UUID conjugateTransactionId;

    @DatabaseField(canBeNull = false, persisterClass = DateTimePersister.class, indexName="transactions_accountId_date_idx")
    private ZonedDateTime date;

    @DatabaseField(canBeNull = false)
    private long amount;

    @DatabaseField(canBeNull = false)
    private long balance;

    @DatabaseField(unique = true)
    private UUID prevTransactionId;

    @DatabaseField(canBeNull = false)
    private String hash;


    public String computeHash(String prevTransactionHash) {
        return DigestUtils.sha1Hex(String.format("%s:%s:%s:%s:%d:%d:%d:%s:%s",
                                                 id,
                                                 accountId,
                                                 conjugateAccountId,
                                                 conjugateTransactionId,
                                                 date == null ? null : date.toInstant().toEpochMilli(),
                                                 amount,
                                                 balance,
                                                 prevTransactionId,
                                                 prevTransactionHash));
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public UUID getConjugateAccountId() {
        return conjugateAccountId;
    }

    public void setConjugateAccountId(UUID otherAccountId) {
        this.conjugateAccountId = otherAccountId;
    }

    public UUID getConjugateTransactionId() {
        return conjugateTransactionId;
    }

    public void setConjugateTransactionId(UUID otherTransactionId) {
        this.conjugateTransactionId = otherTransactionId;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public UUID getPrevTransactionId() {
        return prevTransactionId;
    }

    public void setPrevTransactionId(UUID prevTransactionId) {
        this.prevTransactionId = prevTransactionId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TransactionImpl that = (TransactionImpl) o;
        return amount == that.amount &&
            balance == that.balance &&
            Objects.equals(id, that.id) &&
            Objects.equals(accountId, that.accountId) &&
            Objects.equals(conjugateAccountId, that.conjugateAccountId) &&
            Objects.equals(conjugateTransactionId, that.conjugateTransactionId) &&
            Objects.equals(date, that.date) &&
            Objects.equals(prevTransactionId, that.prevTransactionId) &&
            Objects.equals(hash, that.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountId, conjugateAccountId, conjugateTransactionId, date, amount, balance, prevTransactionId, hash);
    }

    @Override
    public String toString() {
        return "TransactionImpl{" +
            "id=" + id +
            ", accountId=" + accountId +
            ", conjugateAccountId=" + conjugateAccountId +
            ", conjugateTransactionId=" + conjugateTransactionId +
            ", date=" + date +
            ", amount=" + amount +
            ", balance=" + balance +
            ", prevTransactionId=" + prevTransactionId +
            ", hash='" + hash + '\'' +
            '}';
    }
}
