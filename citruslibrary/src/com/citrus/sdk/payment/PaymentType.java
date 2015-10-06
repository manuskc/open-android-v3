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

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.Constants;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by salil on 24/4/15.
 */
public abstract class PaymentType implements Parcelable {

    protected Amount amount;
    protected String url;
    protected PaymentBill paymentBill = null;
    protected PaymentOption paymentOption = null;
    protected CitrusUser citrusUser = null;

    private PaymentType() {
    }

    protected PaymentType(PaymentBill paymentBill) {
        this.paymentBill = paymentBill;
        this.amount = paymentBill.getAmount();
    }

    public PaymentType(Amount amount, String url, PaymentOption paymentOption, CitrusUser citrusUser) {
        this.amount = amount;
        this.url = url;
        this.paymentOption = paymentOption;
        this.citrusUser = citrusUser;
    }

    public PaymentType(PaymentBill paymentBill, PaymentOption paymentOption, CitrusUser citrusUser) {
        this.paymentBill = paymentBill;
        this.paymentOption = paymentOption;
        this.citrusUser = citrusUser;
    }

    public Amount getAmount() {
        return amount;
    }

    public String getUrl() {
        return url;
    }

    public PaymentBill getPaymentBill() {
        return paymentBill;
    }

    public PaymentOption getPaymentOption() {
        return paymentOption;
    }

    public CitrusUser getCitrusUser() {
        return citrusUser;
    }

    public abstract String getIntentAction();

    public static class LoadMoney extends PaymentType implements Parcelable {
        private PaymentOption paymentOption = null;

        private LoadMoney() {
        }

        /**
         * @param amount
         * @param returnUrl - For response of the LoadMoney transaction
         * @throws CitrusException - if Amount or Amount value or returnUrl is null.
         * @deprecated - Please use {@link com.citrus.sdk.payment.PaymentType.LoadMoney#LoadMoney(Amount, String, PaymentOption)}
         */
        public LoadMoney(Amount amount, String returnUrl) throws CitrusException {
            super(amount, returnUrl, null, null);

            if (amount == null || TextUtils.isEmpty(amount.getValue())) {
                throw new CitrusException("Amount should be not null or blank.");
            } else if (!(amount.getValueAsDouble() > 0)) {
                throw new CitrusException("Amount should be greater than 0");
            } else if (returnUrl == null) {
                throw new CitrusException("returnUrl should be not null.");
            }
        }

        /**
         * @param amount        - Amount to be loaded
         * @param returnUrl     - For response of the LoadMoney transaction
         * @param paymentOption - PaymentOption to be used e.g. Card details or netbanking selected.
         * @throws CitrusException - if Amount or returnUrl, or paymentOption is null.
         */
        public LoadMoney(Amount amount, String returnUrl, PaymentOption paymentOption) throws CitrusException {
            super(amount, returnUrl, paymentOption, null);
            this.paymentOption = paymentOption;

            if (amount == null || TextUtils.isEmpty(amount.getValue())) {
                throw new CitrusException("Amount should be not null or blank.");
            } else if (!(amount.getValueAsDouble() > 0)) {
                throw new CitrusException("Amount should be greater than 0");
            } else if (returnUrl == null) {
                throw new CitrusException("returnUrl should be not null.");
            } else if (paymentOption == null) {
                throw new CitrusException("PaymentOption should be not null.");
            }
        }

        public PaymentOption getPaymentOption() {
            return paymentOption;
        }

        @Override
        public final String getIntentAction() {
            return Constants.ACTION_LOAD_MONEY;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.paymentOption, 0);
            dest.writeParcelable(this.amount, 0);
            dest.writeString(this.url);
            dest.writeParcelable(this.paymentBill, 0);
            dest.writeParcelable(this.paymentOption, 0);
            dest.writeParcelable(this.citrusUser, 0);
        }

        private LoadMoney(Parcel in) {
            this.paymentOption = in.readParcelable(PaymentOption.class.getClassLoader());
            this.amount = in.readParcelable(Amount.class.getClassLoader());
            this.url = in.readString();
            this.paymentBill = in.readParcelable(PaymentBill.class.getClassLoader());
            this.paymentOption = in.readParcelable(PaymentOption.class.getClassLoader());
            this.citrusUser = in.readParcelable(CitrusUser.class.getClassLoader());
        }

        public static final Creator<LoadMoney> CREATOR = new Creator<LoadMoney>() {
            public LoadMoney createFromParcel(Parcel source) {
                return new LoadMoney(source);
            }

            public LoadMoney[] newArray(int size) {
                return new LoadMoney[size];
            }
        };
    }

    public static class CitrusCash extends PaymentType implements Parcelable {

        private CitrusCash() {
        }

        /**
         * @param amount  - amount to be Payed
         * @param billUrl - Url of the billGenerator
         * @throws CitrusException - If amount or billUrl or both are null.
         */
        public CitrusCash(Amount amount, String billUrl) throws CitrusException {
            super(amount, billUrl, null, null);

            if (amount == null || TextUtils.isEmpty(amount.getValue())) {
                throw new CitrusException("Amount should be not null or blank.");
            } else if (!(amount.getValueAsDouble() > 0)) {
                throw new CitrusException("Amount should be greater than 0");
            } else if (billUrl == null) {
                throw new CitrusException("billUrl should be not null.");
            }
        }

        /**
         * Pay using the response of the billGenerator. If you are fetching your bill, please use this constructor.
         *
         * @param paymentBill
         */
        public CitrusCash(PaymentBill paymentBill) {
            super(paymentBill);
        }

        public final void setPaymentBill(PaymentBill paymentBill) {
            this.paymentBill = paymentBill;
        }

        public final void setCitrusUser(CitrusUser citrusUser) {
            this.citrusUser = citrusUser;
        }

        public final String getPaymentJSON() {
            String json = "";

            JSONObject jsonObject = PaymentBill.toJSONObject(paymentBill);
            if (jsonObject != null) {
                try {
                    jsonObject.put("userDetails", CitrusUser.toJSONObject(citrusUser));
                    jsonObject.put("requestOrigin", "MSDKW");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                json = jsonObject.toString();
            }

            return json;
        }

        @Override
        public final String getIntentAction() {
            return Constants.ACTION_PAY_USING_CASH;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.amount, 0);
            dest.writeString(this.url);
            dest.writeParcelable(this.paymentBill, 0);
            dest.writeParcelable(this.paymentOption, 0);
            dest.writeParcelable(this.citrusUser, 0);
        }

        private CitrusCash(Parcel in) {
            this.amount = in.readParcelable(Amount.class.getClassLoader());
            this.url = in.readString();
            this.paymentBill = in.readParcelable(PaymentBill.class.getClassLoader());
            this.paymentOption = in.readParcelable(PaymentOption.class.getClassLoader());
            this.citrusUser = in.readParcelable(CitrusUser.class.getClassLoader());
        }

        public static final Creator<CitrusCash> CREATOR = new Creator<CitrusCash>() {
            public CitrusCash createFromParcel(Parcel source) {
                return new CitrusCash(source);
            }

            public CitrusCash[] newArray(int size) {
                return new CitrusCash[size];
            }
        };
    }

    public static class PGPayment extends PaymentType implements Parcelable {

        private PGPayment() {
        }

        /**
         * @param amount  - Amount to be payed
         * @param billUrl - url of the billGenerator
         * @throws CitrusException - if amount or billUrl is null.
         * @deprecated - Use {@link com.citrus.sdk.payment.PaymentType.PGPayment#PGPayment(Amount, String, PaymentOption, CitrusUser)}
         */
        public PGPayment(Amount amount, String billUrl) throws CitrusException {
            super(amount, billUrl, null, null);

            if (amount == null || TextUtils.isEmpty(amount.getValue())) {
                throw new CitrusException("Amount should be not null or blank.");
            } else if (!(amount.getValueAsDouble() > 0)) {
                throw new CitrusException("Amount should be greater than 0");
            } else if (billUrl == null) {
                throw new CitrusException("Url should be not null.");
            }
        }

        /**
         * @param amount        - Amount to be Payed
         * @param billUrl       - url of the billGenerator
         * @param paymentOption - PaymentOption to be used e.g. Card details or netbanking selected.
         * @param citrusUser    - Details of the user, who wants to make the payment. Please enter whatever details you may have such as email Id or mobile no. Keep it null if you do not have any details.
         * @throws CitrusException - if Amount or returnUrl, or paymentOption is null.
         */
        public PGPayment(Amount amount, String billUrl, PaymentOption paymentOption, CitrusUser citrusUser) throws CitrusException {
            super(amount, billUrl, paymentOption, citrusUser);

            if (amount == null || TextUtils.isEmpty(amount.getValue())) {
                throw new CitrusException("Amount should be not null or blank.");
            } else if (!(amount.getValueAsDouble() > 0)) {
                throw new CitrusException("Amount should be greater than 0");
            } else if (billUrl == null) {
                throw new CitrusException("returnUrl should be not null.");
            } else if (paymentOption == null) {
                throw new CitrusException("PaymentOption should be not null.");
            }
        }

        /**
         * Pay using the response of the billGenerator. If you are fetching your bill, please use this constructor.
         * We have permanently discontinued this Constructor. Please use {@link com.citrus.sdk.payment.PaymentType.PGPayment#PGPayment(PaymentBill, PaymentOption)}
         *
         * @param paymentBill
         * @deprecated - Use {@link com.citrus.sdk.payment.PaymentType.PGPayment#PGPayment(PaymentBill, PaymentOption)} instead.
         */
        public PGPayment(PaymentBill paymentBill) {
            super(paymentBill);
        }

        /**
         * Pay using the response of the billGenerator. If you are fetching your bill, please use this constructor.
         *
         * @param paymentBill
         * @param paymentOption
         * @throws CitrusException if the paymentBill or paymentOption is null.
         */
        public PGPayment(PaymentBill paymentBill, PaymentOption paymentOption) throws CitrusException {
            super(paymentBill);
            this.paymentOption = paymentOption;

            if (paymentBill == null) {
                throw new CitrusException("PaymentBill should not be null.");
            }

            if (paymentOption == null) {
                throw new CitrusException("PaymentBill should not be null.");
            }

        }

        /**
         * Pay using the response of the billGenerator. If you are fetching your bill, please use this constructor.
         *
         * @param paymentBill
         * @param paymentOption
         * @param citrusUser
         * @throws CitrusException if the paymentBill or paymentOption is null.
         */
        public PGPayment(PaymentBill paymentBill, PaymentOption paymentOption, CitrusUser citrusUser) throws CitrusException {
            super(paymentBill, paymentOption, citrusUser);
            this.paymentOption = paymentOption;
            this.citrusUser = citrusUser;

            if (paymentBill == null) {
                throw new CitrusException("PaymentBill should not be null.");
            }

            if (paymentOption == null) {
                throw new CitrusException("PaymentBill should not be null.");
            }
        }

        @Override
        public final String getIntentAction() {
            return Constants.ACTION_PG_PAYMENT;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.paymentOption, 0);
            dest.writeParcelable(this.citrusUser, 0);
            dest.writeParcelable(this.amount, 0);
            dest.writeString(this.url);
            dest.writeParcelable(this.paymentBill, 0);
        }

        private PGPayment(Parcel in) {
            this.paymentOption = in.readParcelable(PaymentOption.class.getClassLoader());
            this.citrusUser = in.readParcelable(CitrusUser.class.getClassLoader());
            this.amount = in.readParcelable(Amount.class.getClassLoader());
            this.url = in.readString();
            this.paymentBill = in.readParcelable(PaymentBill.class.getClassLoader());
        }

        public static final Creator<PGPayment> CREATOR = new Creator<PGPayment>() {
            public PGPayment createFromParcel(Parcel source) {
                return new PGPayment(source);
            }

            public PGPayment[] newArray(int size) {
                return new PGPayment[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.amount, 0);
        dest.writeString(this.url);
        dest.writeParcelable(this.paymentBill, 0);
        dest.writeParcelable(this.paymentOption, 0);
        dest.writeParcelable(this.citrusUser, 0);
    }

    private PaymentType(Parcel in) {
        this.amount = in.readParcelable(Amount.class.getClassLoader());
        this.url = in.readString();
        this.paymentBill = in.readParcelable(PaymentBill.class.getClassLoader());
        this.paymentOption = in.readParcelable(PaymentOption.class.getClassLoader());
        this.citrusUser = in.readParcelable(CitrusUser.class.getClassLoader());
    }

}
