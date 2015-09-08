package com.citrus.analytics;

/**
 * Created by MANGESH KADAM on 4/24/2015.
 */
public enum TransactionType {
    SUCCESS(151),
    FAIL(157),
    CANCELLED(163);

    private final int transactionType;

    TransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public int getValue() {
        return  transactionType;
    }

}
