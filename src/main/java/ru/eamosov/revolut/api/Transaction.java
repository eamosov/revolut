package ru.eamosov.revolut.api;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Transaction
 */
public interface Transaction {

    /**
     * Get transaction identifier
     * @return
     */
    UUID getId();

    /**
     * Get account which this transaction belongs to
     * @return
     */
    UUID getAccountId();

    /**
     * Get account which conjugate transaction belongs to
     * @return
     */
    UUID getConjugateAccountId();

    /**
     * Get id of conjugate transaction
     * @return
     */
    UUID getConjugateTransactionId();

    /**
     * Transaction date
     * @return
     */
    ZonedDateTime getDate();

    /**
     * Transaction amount. amount &lt; 0 for inbound transaction and amount &gt; 0 for outbound one.
     * @return
     */
    long getAmount();

    /**
     * Balance for transactions' account after transaction.
     * @return
     */
    long getBalance();

    /**
     * Reference to previous transaction for transactions' account.
     * @return
     */
    UUID getPrevTransactionId();

    /**
     * sha1(id:accountId:otherAccountId:otherTransactionId:date:amount:balance:prevTransactionId:prevTransaction.hash)
     * @return
     */
    String getHash();
}
