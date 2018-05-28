package ru.eamosov.revolut;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.eamosov.revolut.api.BankService;
import ru.eamosov.revolut.api.Transaction;
import ru.eamosov.revolut.api.TransferResult;
import ru.eamosov.revolut.model.TransactionDao;
import ru.eamosov.revolut.model.TransactionImpl;
import ru.eamosov.revolut.model.TransferResultImpl;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * Local implementation of BankService
 */
public class BankServiceImpl implements BankService {

    private static final Logger log = LoggerFactory.getLogger(BankServiceImpl.class);

    private final TransactionDao transactions;
    private final TransactionManager transactionManager;

    public BankServiceImpl(ConnectionSource connection) throws SQLException {
        this.transactions = new TransactionDao(connection);
        this.transactionManager = new TransactionManager(connection);
    }

    @Override
    public boolean createAccount(UUID id, long balance) throws SQLException {

        log.debug("createAccount({}, {})", id, balance);

        Objects.requireNonNull(id, "accountId must not be null");

        try {
            return transactionManager.callInTransaction(() -> {

                final TransactionImpl transaction = new TransactionImpl();
                transaction.setId(id);
                transaction.setAccountId(id);
                transaction.setDate(ZonedDateTime.now(ZoneId.of("GMT")));
                transaction.setAmount(balance);
                transaction.setBalance(balance);
                transaction.setHash(transaction.computeHash(null));
                transactions.create(transaction);

                return true;
            });
        } catch (SQLException e) {
            if (isViolatesUniqueConstraint(e)) {
                return false;
            } else {
                throw e;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Transaction> getHistory(UUID id) throws SQLException {

        log.debug("getHistory({})", id);

        Objects.requireNonNull(id, "accountId must not be null");
        return (List) transactions.getTransactions(id);
    }

    @Override
    public Long getBalance(UUID id) throws SQLException {

        log.debug("getBalance({})", id);

        Objects.requireNonNull(id, "accountId must not be null");

        final Transaction tr = transactions.getLastTransaction(id);

        return tr == null ? null : tr.getBalance();
    }

    private TransferResult tryTransfer(UUID srcAccountId, UUID dstAccountId, long amount) throws SQLException {

        return transactionManager.callInTransaction(() -> {


            final Transaction srcLastTransaction = transactions.getLastTransaction(srcAccountId);

            if (srcLastTransaction == null) {
                return new TransferResultImpl(TransferResult.ErrCode.ACCOUNT_NOT_FOUND, "Couldn't find account " + srcAccountId);
            }

            final Transaction dstLastTransaction = transactions.getLastTransaction(dstAccountId);

            if (dstLastTransaction == null) {
                return new TransferResultImpl(TransferResult.ErrCode.ACCOUNT_NOT_FOUND, "Couldn't find account " + dstAccountId);
            }

            if (srcLastTransaction.getBalance() < amount) {
                return new TransferResultImpl(TransferResult.ErrCode.INSUFFICIENT_BALANCE, "insufficient balance=" + srcLastTransaction
                    .getBalance());
            }

            final ZonedDateTime date = ZonedDateTime.now(ZoneId.of("GMT"));

            final UUID srcTransactionId = UUID.randomUUID();
            final UUID dstTransactionId = UUID.randomUUID();

            final TransactionImpl srcTransaction = new TransactionImpl();
            srcTransaction.setId(srcTransactionId);
            srcTransaction.setAccountId(srcAccountId);
            srcTransaction.setConjugateAccountId(dstTransactionId);
            srcTransaction.setConjugateTransactionId(dstTransactionId);
            srcTransaction.setDate(date.isAfter(srcLastTransaction.getDate()) ? date : srcLastTransaction.getDate()
                                                                                                         .plusSeconds(1));
            srcTransaction.setAmount(-amount);
            srcTransaction.setBalance(srcLastTransaction.getBalance() - amount);
            srcTransaction.setPrevTransactionId(srcLastTransaction.getId());
            srcTransaction.setHash(srcTransaction.computeHash(srcLastTransaction.getHash()));
            transactions.create(srcTransaction);


            final TransactionImpl dstTransaction = new TransactionImpl();
            dstTransaction.setId(dstTransactionId);
            dstTransaction.setAccountId(dstAccountId);
            dstTransaction.setConjugateAccountId(srcAccountId);
            dstTransaction.setConjugateTransactionId(srcTransactionId);
            dstTransaction.setDate(date.isAfter(dstLastTransaction.getDate()) ? date : dstLastTransaction.getDate()
                                                                                                         .plusSeconds(1));
            dstTransaction.setAmount(amount);
            dstTransaction.setBalance(dstLastTransaction.getBalance() + amount);
            dstTransaction.setPrevTransactionId(dstLastTransaction.getId());
            dstTransaction.setHash(dstTransaction.computeHash(dstLastTransaction.getHash()));
            transactions.create(dstTransaction);

            return new TransferResultImpl(srcTransaction);
        });
    }

    private boolean isViolatesUniqueConstraint(SQLException e) {

        return e.getCause() instanceof SQLIntegrityConstraintViolationException ||
            e.getCause() instanceof PSQLException && e.getCause()
                                                      .getMessage()
                                                      .contains("duplicate key value violates unique constraint");

    }

    //TODO Actually transfer algorithm may be improved and splitted: one database transaction for outbound and one for inbound. It will help build partitioning without double-phase commit but needs some complications.
    @Override
    public TransferResult transfer(UUID src, UUID dst, long amount) throws SQLException {

        log.debug("transfer({}, {}, {})", src, dst, amount);

        Objects.requireNonNull(src, "srcAccountId must not be null");
        Objects.requireNonNull(dst, "dstAccountId must not be null");

        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be grater then zero");
        }

        do {
            try {
                return tryTransfer(src, dst, amount);
            } catch (SQLException e) {
                if (!isViolatesUniqueConstraint(e)) {
                    throw e;
                }

                log.debug("Concurrent update of balance has occurred while transferring from {} to {}, wait random time and try again", src, dst);

                try {
                    Thread.sleep(new Random().nextInt(100));
                } catch (InterruptedException ignored) {
                }
            }
        } while (true);
    }

    @Override
    public boolean checkTransactionHash(UUID id) throws SQLException {
        final TransactionImpl transaction = transactions.get(id);

        return transaction != null && checkTransactionHash(transaction);
    }

    private boolean checkTransactionHash(TransactionImpl tr) throws SQLException {

        log.debug("checkTransactionHash({})", tr.getId());

        final TransactionImpl prevTr;
        if (tr.getPrevTransactionId() != null) {
            prevTr = transactions.get(tr.getPrevTransactionId());
            if (prevTr == null) {
                log.error("checkTransaction({}): could't find prevTransactionId={}", tr.getId(), tr.getPrevTransactionId());
                return false;
            }
        } else {
            prevTr = null;
        }

        if (tr.getBalance() != (prevTr != null ? prevTr.getBalance() : 0) + tr.getAmount()) {
            log.error("checkTransaction({}): balance != prevBalance + amount");
            return false;
        }

        final String computedHash = tr.computeHash(prevTr != null ? prevTr.getHash() : null);
        if (!tr.getHash().equals(computedHash)) {
            log.error("checkTransaction({}): hash is invalid, hash={}, computed={}", tr.getId(), tr.getHash(), computedHash);
            return false;
        }

        return prevTr == null || checkTransactionHash(prevTr);
    }
}
