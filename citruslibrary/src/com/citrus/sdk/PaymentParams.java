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

package com.citrus.sdk;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.citrus.mobile.Config;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.payment.PaymentType;

import java.util.ArrayList;

/**
 * Created by salil on 9/3/15.
 */
@Deprecated
/**
 * This class is permanently discontinued.
 * @deprecated - Please use {@link com.citrus.sdk.classes.CitrusConfig} for customization.
 */
public final class PaymentParams implements Parcelable {

    public static final Creator<PaymentParams> CREATOR = new Creator<PaymentParams>() {
        public PaymentParams createFromParcel(Parcel source) {
            return new PaymentParams(source);
        }

        public PaymentParams[] newArray(int size) {
            return new PaymentParams[size];
        }
    };
    /**
     * Transaction amount.
     */
    final Amount transactionAmount;
    // Following are the parameters used internally.
    ArrayList<NetbankingOption> netbankingOptionList = new ArrayList<>(); // Netbanking options enabled for the merchant.
    ArrayList<NetbankingOption> topNetbankingOptions = new ArrayList<>(); // List of top n banks. This list will contain 0 to 4 items depending upon the enabled netbanking options for the merchant.
    /**
     * Bill url. This page will be hosted on your server. Host pages depending upon your backend technology.
     * It is required to authenticate the transaction. The merchant needs to sign the particular transaction.
     */
//    final String billUrl;
    ArrayList<PaymentOption> userSavedOptionList = new ArrayList<>(); // List of different payment options saved for the user.
    /**
     * User details. Email id and mobile no. will be used to fetch saved payment options for that user.
     */
    CitrusUser user;
    /**
     * Name of the merchant. For the display purpose.
     */
    String merchantOrTitleName;
    /**
     * Accent color for the app, in the form of #123456.
     * This is the color of the status bar when the is opened.
     * This will be used only on android versions lollipop and above.
     */
    private String colorPrimaryDark = "#E7961D";
    /**
     * Main color code for the app in the form #123456
     */
    private String colorPrimary = "#F9A323";
    /**
     * Primary text color. #123456
     */
    private String textColorPrimary = "#ffffff";
    /**
     * Accent color for the app, will be used to display common actions.
     */
    private String accentColor;
    /**
     * JSON Key store containing the merchant credentials. The json is in the following format.
     * <p/>
     * {"access_key":"06SLEEBYLVZELISZ5ECU",
     * "signup-id":"kkizp9tsqg-signup",
     * "signup-secret":"39c50a32eaabaf382223fdd05f331e1c",
     * "signin-id":"kkizp9tsqg-signin",
     * "signin-secret":"1fc1f57639ec87cf4d49920f6b3a2c9d",
     * "vanity_Url":"https://www.citruspay.com/kkizp9tsqg"
     * }
     */
    private String jsonKeyStore;
    /**
     * Merchant vanity to fetch the available payment options.
     */
    private String vanity;
    /**
     * Merchant accessKey found on merchant portal.
     */
    private String accessKey;
    /**
     * Merchant keys required for user signin and signup.
     */
    private String signinId;
    private String signinSecret;
    private String signupId;
    private String signupSecret;
    private PaymentType paymentType = null;
    private Environment environment = Environment.SANDBOX;
    private PaymentOption paymentOption = null;

//    private PaymentParams(Amount transactionAmount, String billUrl, String jsonKeyStore) {
//        this.transactionAmount = transactionAmount;
//        this.billUrl = billUrl;
//        this.jsonKeyStore = jsonKeyStore;
//
//        // Parse the jsonKeystore.
//        try {
//            JSONObject keyStore = new JSONObject(jsonKeyStore);
//            this.accessKey = keyStore.getString("access_key");
//            this.signupId = keyStore.getString("signup-id");
//            this.signupSecret = keyStore.getString("signup-secret");
//            this.signinId = keyStore.getString("signin-id");
//            this.signinSecret = keyStore.getString("signin-secret");
//
//            String vanity = keyStore.getString("vanity_Url");
//            // If vanity is in the form of https://www.citruspay.com/vanity
//            // take only vanity part else as it is.
//            if (vanity.startsWith("http") || vanity.startsWith("https")) {
//                vanity = vanity.substring(vanity.lastIndexOf("/") + 1);
//            }
//            this.vanity = vanity;
//
//            // Set merchant keys.
//            Config.setupSignupId(signupId);
//            Config.setupSignupSecret(signupSecret);
//            Config.setSigninId(signinId);
//            Config.setSigninSecret(signinSecret);
//            Config.setVanity(this.vanity);
//
//        } catch (JSONException ex) {
//            throw new IllegalArgumentException("The json keystore is not a valid json.");
//        }
//    }

//    public static PaymentParams builder(double transactionAmount, String billUrl, String jsonKeyStore) {
//        if (transactionAmount <= 0 && TextUtils.isEmpty(billUrl) && TextUtils.isEmpty(jsonKeyStore)) {
//            throw new IllegalArgumentException("Mandatory parameters missing...");
//        }
//
//        return new PaymentParams(transactionAmount, billUrl, jsonKeyStore);
//    }


    private PaymentParams(Amount amount, PaymentType paymentType, PaymentOption paymentOption) {
        if (amount != null && paymentType != null
                && ((paymentType instanceof PaymentType.CitrusCash && paymentOption == null)
                || ((paymentOption != null && (paymentType instanceof PaymentType.PGPayment || paymentType instanceof PaymentType.LoadMoney))))) {
            this.transactionAmount = amount;
            this.paymentType = paymentType;
            this.paymentOption = paymentOption;
            environment(Environment.SANDBOX);
        } else {
            throw new IllegalArgumentException("Please make sure to pass amount, paymentType and selected paymentOption");
        }
    }


    private PaymentParams(Parcel in) {
        this.netbankingOptionList = (ArrayList<NetbankingOption>) in.readSerializable();
        this.topNetbankingOptions = (ArrayList<NetbankingOption>) in.readSerializable();
        this.userSavedOptionList = (ArrayList<PaymentOption>) in.readSerializable();
        this.user = in.readParcelable(CitrusUser.class.getClassLoader());
        this.colorPrimaryDark = in.readString();
        this.colorPrimary = in.readString();
        this.textColorPrimary = in.readString();
        this.accentColor = in.readString();
        this.transactionAmount = in.readParcelable(Amount.class.getClassLoader());
        this.merchantOrTitleName = in.readString();
        this.jsonKeyStore = in.readString();
        this.vanity = in.readString();
        this.accessKey = in.readString();
        this.signinId = in.readString();
        this.signinSecret = in.readString();
        this.signupId = in.readString();
        this.signupSecret = in.readString();
        this.paymentType = in.readParcelable(PaymentType.class.getClassLoader());
        int tmpEnvironment = in.readInt();
        this.environment = tmpEnvironment == -1 ? null : Environment.values()[tmpEnvironment];
        this.paymentOption = in.readParcelable(PaymentOption.class.getClassLoader());
    }

    public static PaymentParams builder(Amount transactionAmount, PaymentType paymentType, PaymentOption paymentOption) {
//        if (transactionAmount <= 0 && TextUtils.isEmpty(billUrl) && TextUtils.isEmpty(jsonKeyStore)) {
//            throw new IllegalArgumentException("Mandatory parameters missing...");
//        }

        return new PaymentParams(transactionAmount, paymentType, paymentOption);
    }

    public PaymentParams colorPrimaryDark(String colorPrimaryDark) {
        if (!TextUtils.isEmpty(colorPrimaryDark)) {
            this.colorPrimaryDark = colorPrimaryDark;
        }
        return this;
    }

    public PaymentParams colorPrimary(String colorPrimary) {
        if (!TextUtils.isEmpty(colorPrimary)) {
            this.colorPrimary = colorPrimary;
        }
        return this;
    }

    public PaymentParams textColorPrimary(String textColorPrimary) {
        if (!TextUtils.isEmpty(textColorPrimary)) {
            this.textColorPrimary = textColorPrimary;
        }
        return this;
    }

    //    public PaymentParams accentColor(String accentColor) {
//    if (!TextUtils.isEmpty(accentColor)) {
//        this.accentColor = accentColor;
//    }
//        return this;
//    }
//
    public PaymentParams merchantOrTitleName(String merchantOrTitleName) {
        this.merchantOrTitleName = merchantOrTitleName;
        return this;
    }

    public PaymentParams user(CitrusUser user) {
        this.user = user;

        return this;
    }

    public PaymentParams environment(Environment environment) {
        this.environment = environment;

        if (environment == Environment.PRODUCTION) {
            Config.setEnv("production");
        } else if (environment == Environment.SANDBOX) {
            Config.setEnv("sandbox");
        }

        return this;
    }

    public PaymentParams paymentType(PaymentType paymentType) {
        this.paymentType = paymentType;

        return this;
    }

//    public String getBillUrl() {
//        return billUrl;
//    }

    public PaymentParams vanity(String vanity) {
        this.vanity = vanity;

        return this;
    }

    public CitrusUser getUser() {
        if (user == null) {
            user = CitrusUser.DEFAULT_USER;
        }
        return user;
    }

    public String getColorPrimaryDark() {
        return colorPrimaryDark;
    }

    public String getColorPrimary() {
        return colorPrimary;
    }

    public String getTextColorPrimary() {
        return textColorPrimary;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public Amount getTransactionAmount() {
        return transactionAmount;
    }

    public String getMerchantOrTitleName() {
        return merchantOrTitleName;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public PaymentOption getPaymentOption() {
        return paymentOption;
    }

    public String getVanity() {
        return vanity;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSigninId() {
        return signinId;
    }

    public String getSigninSecret() {
        return signinSecret;
    }

    public String getSignupId() {
        return signupId;
    }

    public String getSignupSecret() {
        return signupSecret;
    }

    public String getJsonKeyStore() {
        return jsonKeyStore;
    }

    public PaymentParams build() {
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.netbankingOptionList);
        dest.writeSerializable(this.topNetbankingOptions);
        dest.writeSerializable(this.userSavedOptionList);
        dest.writeParcelable(this.user, 0);
        dest.writeString(this.colorPrimaryDark);
        dest.writeString(this.colorPrimary);
        dest.writeString(this.textColorPrimary);
        dest.writeString(this.accentColor);
        dest.writeParcelable(this.transactionAmount, 0);
        dest.writeString(this.merchantOrTitleName);
        dest.writeString(this.jsonKeyStore);
        dest.writeString(this.vanity);
        dest.writeString(this.accessKey);
        dest.writeString(this.signinId);
        dest.writeString(this.signinSecret);
        dest.writeString(this.signupId);
        dest.writeString(this.signupSecret);
        dest.writeParcelable(this.paymentType, 0);
        dest.writeInt(this.environment == null ? -1 : this.environment.ordinal());
        dest.writeParcelable(this.paymentOption, 0);
    }

    public enum Environment {
        PRODUCTION, SANDBOX
    }
}
