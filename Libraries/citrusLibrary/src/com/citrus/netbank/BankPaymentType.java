package com.citrus.netbank;


/**
 * @deprecated in v3
 * <p/>
 * This class is no lolnger supported.
 */
@Deprecated
public enum BankPaymentType {

    CID("CID"),

    TOKEN("paymentOptionIdToken");

    private final String name;

    private BankPaymentType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

}
