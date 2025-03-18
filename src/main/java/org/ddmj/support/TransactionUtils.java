package org.ddmj.support;

import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionUtils {

    public static void doAfterTransaction(Runnable action) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new DoTransactionCompletion(action));
        }
    }
}