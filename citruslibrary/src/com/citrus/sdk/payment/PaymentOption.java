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
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.citrus.sdk.classes.PGHealth;

import org.json.JSONObject;

import static com.citrus.sdk.payment.CardOption.CardScheme;

/**
 * Created by salil on 13/2/15.
 */
public abstract class PaymentOption implements Parcelable {

    /**
     * Following variables will be used in case of tokenized payments and mostly internally.
     * Hence no public constructor with these variables is required. If required create a constructor
     * with default access modifier so as to avoid confusion for the merchant developer.
     */
    protected String name = null; // Denotes the friendly name for the payment option.
    protected String token = null; // Denotes the token for the payment option.
    protected boolean savePaymentOption = false;
    protected PGHealth pgHealth = PGHealth.UNKNOWN;

    PaymentOption() {
    }

    /**
     * @param name  - User friendly name of the payment option. e.g. Debit Card (4242) or Net Banking - ICICI Bank
     * @param token - Token for payment option, used for tokenized payment.
     */
    PaymentOption(String name, String token) {
        this.name = name;
        this.token = token;
    }

    private PaymentOption(Parcel in) {
        this.name = in.readString();
        this.token = in.readString();
        this.savePaymentOption = in.readByte() != 0;
    }

    public static PaymentOption fromJSONObject(JSONObject walletObject) {

        PaymentOption paymentOption = null;
        String type = walletObject.optString("type");
        String name = walletObject.optString("name");
        String token = walletObject.optString("token");
        String cardHolderName = walletObject.optString("owner");
        String expiry = walletObject.optString("expiryDate");
        String cardNumber = walletObject.optString("number");
        String cardScheme = walletObject.optString("scheme");

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(token)) {
            if (TextUtils.equals("credit", type)) {
                paymentOption = new CreditCardOption(name, token, cardHolderName, cardNumber, CardScheme.getCardScheme(cardScheme), expiry);
            } else if (TextUtils.equals("debit", type)) {
                paymentOption = new DebitCardOption(name, token, cardHolderName, cardNumber, CardScheme.getCardScheme(cardScheme), expiry);
            } else {
                String bankName = walletObject.optString("bank");
                paymentOption = new NetbankingOption(name, token, bankName);
            }
        }

        return paymentOption;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public boolean isSavePaymentOption() {
        return savePaymentOption;
    }

    public void setSavePaymentOption(boolean savePaymentOption) {
        this.savePaymentOption = savePaymentOption;
    }

    public PGHealth getPgHealth() {
        return pgHealth;
    }

    protected void setPgHealth(PGHealth pgHealth) {
        this.pgHealth = pgHealth;
    }

    public abstract String getSavePaymentOptionObject();

    public abstract Drawable getOptionIcon(Context context);

    public abstract com.citrus.analytics.PaymentType getAnalyticsPaymentType();

    public abstract String getDynamicPricingPaymentMode();

    @Override
    public String toString() {
        return "PaymentOption{" +
                "name='" + name + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public static final Creator<PaymentOption> CREATOR = new Creator<PaymentOption>() {
        public PaymentOption createFromParcel(Parcel source) {
            return null;
        }

        public PaymentOption[] newArray(int size) {
            return new PaymentOption[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.token);
        dest.writeByte(savePaymentOption ? (byte) 1 : (byte) 0);
    }
}
