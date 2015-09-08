/*
   Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.citrus.sdk.payment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Created by salil on 4/3/15.
 */
public final class CitrusCash extends PaymentOption {

    private String amount = null;

    public CitrusCash() {
    }

    /**
     * @param name  - User friendly name of the payment option. e.g. Debit Card (4242) or Net Banking - ICICI Bank
     * @param token - Token for payment option, used for tokenized payment.
     */
    public CitrusCash(String name, String token) {
        super(name, token);
    }

    public CitrusCash(String amount) {
        this.amount = amount;
    }

    @Override
    public Drawable getOptionIcon(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(context.getResources().getIdentifier("citrus_cash", "drawable", context.getPackageName()), null);
        } else {
            return context.getResources().getDrawable(context.getResources().getIdentifier("citrus_cash", "drawable", context.getPackageName()));
        }
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public String getSavePaymentOptionObject() {
        return null;
    }

    @Override
    public com.citrus.analytics.PaymentType getAnalyticsPaymentType() {
        return com.citrus.analytics.PaymentType.CITRUS_CASH;
    }

    @Override
    public String toString() {
        return super.toString() + "CitrusCash{" +
                "amount='" + amount + '\'' +
                '}';
    }
}
