package ru.eamosov.revolut.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Created by fluder on 27/05/2018.
 */
public class TransactionDao {

    private final Dao<TransactionImpl, UUID> dao;

    public TransactionDao(ConnectionSource connectionSource) throws SQLException {
        this.dao = DaoManager.createDao(connectionSource, TransactionImpl.class);
        this.dao.setObjectCache(true);
        TableUtils.createTableIfNotExists(connectionSource, TransactionImpl.class);
    }

    public TransactionImpl get(UUID id) throws SQLException {
        return dao.queryForId(id);
    }

    public void create(TransactionImpl entity) throws SQLException {
        dao.create(entity);
    }

    public TransactionImpl getLastTransaction(UUID accountId) throws SQLException {

        final List<TransactionImpl> trs = dao.queryBuilder()
                                             .limit(1L)
                                             .orderBy("date", false)
                                             .where()
                                             .eq("accountId", accountId)
                                             .query();

        return trs.isEmpty() ? null : trs.get(0);
    }

    public List<TransactionImpl> getTransactions(UUID accountId) throws SQLException {
        return dao.queryBuilder()
                  .orderBy("date", false)
                  .where()
                  .eq("accountId", accountId)
                  .query();
    }

}
