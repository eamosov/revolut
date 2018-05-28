package ru.eamosov.revolut.api;

/**
 * TransferResult
 */
public interface TransferResult {

    enum ErrCode {
        ACCOUNT_NOT_FOUND,
        INSUFFICIENT_BALANCE
    }

    /**
     * Transaction in case of success or NULL otherwise
     * @return {@link Transaction}
     */
    Transaction getTransaction();

    /**
     * Code of the error or null
     * @return {@link ErrCode}
     */
    ErrCode getCode();

    /**
     * Description of the error or null
     * @return
     */
    String getError();
}
