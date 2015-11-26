package com.citrus.analytics;

/**
 * Created by MANGESH KADAM on 4/24/2015.
 */
public enum PaymentType {
    CREDIT_CARD(2),
    DEBIT_CARD(3),
    NET_BANKING(5),
    CITRUS_CASH(7);

    private final int paymentType;

    private String name;

    PaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public int getValue() {
        return  paymentType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
