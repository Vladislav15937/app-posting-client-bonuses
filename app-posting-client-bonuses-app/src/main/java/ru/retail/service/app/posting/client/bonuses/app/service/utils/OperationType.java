package ru.retail.service.app.posting.client.bonuses.app.service.utils;

import ru.retail.service.app.posting.client.bonuses.app.entity.Transaction;

public enum OperationType {
    ACCRUE {
        @Override
        public long calculateNewBalance(long oldBalance, long amount) {
            return oldBalance + amount;
        }

        @Override
        public long getSignedAmount(long amount) {
            return amount;
        }

        @Override
        public Transaction.TransactionType getTransactionType() {
            return Transaction.TransactionType.ACCRUAL;
        }

        @Override
        public String getLogPrefix() {
            return "Начисление";
        }

        @Override
        public String getPastTense() {
            return "начислены";
        }
    },
    WRITE_OFF {
        @Override
        public long calculateNewBalance(long oldBalance, long amount) {
            return oldBalance - amount;
        }

        @Override
        public long getSignedAmount(long amount) {
            return -amount;
        }

        @Override
        public Transaction.TransactionType getTransactionType() {
            return Transaction.TransactionType.WRITE_OFF;
        }

        @Override
        public String getLogPrefix() {
            return "Списание";
        }

        @Override
        public String getPastTense() {
            return "списаны";
        }
    };

    public abstract long calculateNewBalance(long oldBalance, long amount);

    public abstract long getSignedAmount(long amount);

    public abstract Transaction.TransactionType getTransactionType();

    public abstract String getLogPrefix();

    public abstract String getPastTense();
}
