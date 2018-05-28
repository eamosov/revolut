package ru.eamosov.revolut.tests;

import com.j256.ormlite.db.HsqldbDatabaseType;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.junit.Test;
import ru.eamosov.revolut.api.BankService;
import ru.eamosov.revolut.api.Transaction;
import ru.eamosov.revolut.api.TransferResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Various tests for {@link BankService}
 */
public abstract class BaseTest {

    final protected ConnectionSource connection;

    protected abstract BankService getBankService();

    public BaseTest() throws SQLException {
        connection = new JdbcPooledConnectionSource("jdbc:hsqldb:mem:mymemdb" + UUID.randomUUID(), new HsqldbDatabaseType());
        //connection = new JdbcPooledConnectionSource("jdbc:postgresql://ubuntu.local/revolut?user=revolut", new PostgresDatabaseType());
    }

    @Test
    public void testCreateAccount() throws Exception {

        final UUID id = UUID.randomUUID();

        assertThat(getBankService().getBalance(id), nullValue());

        final long balance = 345;
        assertThat(getBankService().createAccount(id, balance), equalTo(true));

        final Transaction tr = getBankService().getHistory(id).iterator().next();
        assertThat(tr, notNullValue());
        assertThat(tr.getBalance(), equalTo(balance));
        assertThat(tr.getPrevTransactionId(), nullValue());

        getBankService().checkTransactionHash(tr.getId());

        assertThat(getBankService().createAccount(id, balance), equalTo(false));
    }

    @Test
    public void testTransfer() throws Exception {

        final UUID accountId1 = UUID.randomUUID();
        assertThat(getBankService().createAccount(accountId1, 100), equalTo(true));

        UUID accountId2 = UUID.randomUUID();

        TransferResult tr = getBankService().transfer(accountId1, accountId2, 10);
        assertThat(tr.getCode(), equalTo(TransferResult.ErrCode.ACCOUNT_NOT_FOUND));
        assertThat(tr.getError(), equalTo("Couldn't find account " + accountId2));

        assertThat(getBankService().createAccount(accountId2, 1), equalTo(true));

        tr = getBankService().transfer(accountId1, accountId2, 500);
        assertThat(tr.getCode(), equalTo(TransferResult.ErrCode.INSUFFICIENT_BALANCE));
        assertThat(tr.getError(), equalTo("insufficient balance=" + getBankService().getBalance(accountId1)));

        tr = getBankService().transfer(accountId1, accountId2, 25);
        assertThat(tr.getCode(), nullValue());
        assertThat(tr.getTransaction(), notNullValue());
        assertThat(tr.getTransaction().getBalance(), equalTo(75L));
        assertThat(tr.getTransaction().getAmount(), equalTo(-25L));

        assertThat(getBankService().getBalance(accountId2), equalTo(26L));

        assertThat(getBankService().checkTransactionHash(tr.getTransaction().getId()), equalTo(true));
        assertThat(getBankService().checkTransactionHash(tr.getTransaction()
                                                           .getConjugateTransactionId()), equalTo(true));


        tr = getBankService().transfer(accountId2, accountId1, 3);
        assertThat(tr.getCode(), nullValue());
        assertThat(tr.getTransaction(), notNullValue());
        assertThat(tr.getTransaction().getBalance(), equalTo(23L));
        assertThat(tr.getTransaction().getAmount(), equalTo(-3L));
        assertThat(getBankService().checkTransactionHash(tr.getTransaction().getId()), equalTo(true));
        assertThat(getBankService().checkTransactionHash(tr.getTransaction()
                                                           .getConjugateTransactionId()), equalTo(true));

        assertThat(getBankService().getBalance(accountId1), equalTo(78L));
        assertThat(getBankService().getBalance(accountId2), equalTo(23L));


        assertThat(getBankService().getHistory(accountId1), hasSize(3));
        assertThat(getBankService().getHistory(accountId2), hasSize(3));
    }


    @Test
    public void testConcurrentTransfer() throws Exception {

        final UUID accountId1 = UUID.randomUUID();
        getBankService().createAccount(accountId1, 1001);
        final UUID accountId2 = UUID.randomUUID();
        getBankService().createAccount(accountId2, 1002);

        final ExecutorService executor = Executors.newCachedThreadPool();
        final List<Future> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            futures.add(executor.submit(() -> getBankService().transfer(accountId1, accountId2, 10)));
        }

        for (Future f : futures) {
            f.get();
        }

        assertThat(getBankService().getBalance(accountId1), equalTo(1L));
        assertThat(getBankService().getBalance(accountId2), equalTo(2002L));

        assertThat(getBankService().checkTransactionHash(getBankService().getHistory(accountId1)
                                                                         .get(0)
                                                                         .getId()), equalTo(true));
        assertThat(getBankService().checkTransactionHash(getBankService().getHistory(accountId2)
                                                                         .get(0)
                                                                         .getId()), equalTo(true));
    }

}
