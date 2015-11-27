package com.citrus.sdk;

import com.citrus.sdk.payment.PaymentOption;

/**
 * Created by salil on 27/11/15.
 */
public class RetrofitAPIWrapper {
    private static RetrofitAPIWrapper instance = null;

    public static RetrofitAPIWrapper getInstance() {
        if (instance == null) {
            synchronized (RetrofitAPIWrapper.class) {
                if (instance == null) {
                    instance = new RetrofitAPIWrapper();
                }
            }
        }

        return instance;
    }

    private RetrofitAPIWrapper() {
    }


    public synchronized void getMerchantPaymentOptions(final Callback<PaymentOption> callback) {

    }

    public synchronized void getLoadMoneyPaymentOptions(final Callback<PaymentOption> callback) {

    }
}
