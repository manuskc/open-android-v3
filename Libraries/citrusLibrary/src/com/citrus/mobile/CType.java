package com.citrus.mobile;

/**
 * Created by MANGESH KADAM on 4/23/2015.
 */
public enum CType {
    DEBIT("debit"),
    CREDIT("credit");

    private final String cardType;

    CType(String cardType) {
        this.cardType = cardType;
    }


    @Override
    public String toString() {
        return cardType;
    }
}
