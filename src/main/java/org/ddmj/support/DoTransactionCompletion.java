package org.ddmj.support;

import org.springframework.transaction.support.TransactionSynchronization;

public class DoTransactionCompletion implements TransactionSynchronization {

    private final Runnable runnable;

    public DoTransactionCompletion(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void afterCompletion(int status) {
        if (status == TransactionSynchronization.STATUS_COMMITTED) {
            this.runnable.run();
        }
    }
}
