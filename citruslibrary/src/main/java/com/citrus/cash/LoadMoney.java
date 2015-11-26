package com.citrus.cash;

import com.citrus.sdk.Callback;
import com.citrus.sdk.payment.PaymentType;

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.CitrusClient#loadMoney(PaymentType.LoadMoney, Callback)} instead.
 */
@Deprecated
public class LoadMoney {
    String amount = "", return_url = "";

    public LoadMoney(String amount, String return_url) {
        this.amount = amount;
        this.return_url = return_url;
    }

    public String getAmount() {
        return this.amount;
    }

    public String getReturl() {
        return this.return_url;
    }

}
