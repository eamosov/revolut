package ru.eamosov.revolut.api;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * BankService
 */
public interface BankService {

    /**
     * Create an Account with balance
     * <p>
     * REST:  /create?id=${id}&amp;balance=${balance}
     *
     * @param id create account with this id
     * @param balance create account with balance
     * @return true in case of success or false otherwise (account already exists)
     * @throws SQLException
     */
    boolean createAccount(UUID id, long balance) throws SQLException;

    /**
     * Get all transactions for account accountId
     * <p>
     * REST: /history?id=${id}
     *
     * @param id accounts' id
     * @return sorted list of transactions {@link Transaction}
     * @throws SQLException
     */
    List<Transaction> getHistory(UUID id) throws SQLException;

    /**
     * Get account's balance
     * <p>
     * REST: /balance?id=${id}
     *
     * @param id accounts' id
     * @return accounts' balance
     * @throws SQLException
     */
    Long getBalance(UUID id) throws SQLException;

    /**
     * Transfer money from src to dst
     * <p>
     * REST: /transfer?src=${src}&amp;dst=${dst}&amp;amount=${amount}
     *
     * @param src source account Id
     * @param dst destination account Id
     * @param amount - must be &gt; 0
     * @return {@link TransferResult}
     * @throws SQLException
     */
    TransferResult transfer(UUID src, UUID dst, long amount) throws SQLException;

    /**
     * Check integrity of transaction with id ${id} and all it's parent transactions
     * <p>
     * REST: /checkTransactionHash?id=${id}
     *
     * @param id accounts' id
     * @return true in case of success or false otherwise
     * @throws SQLException
     */
    boolean checkTransactionHash(UUID id) throws SQLException;
}
