/*
 *
 *    Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 */

package com.citrus.sdk.payment;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by salil on 7/5/15.
 */
public class PaymentBill implements Parcelable {

    @SerializedName("amount")
    private
    Amount amount = null;
    @SerializedName("alteredAmount")
    private
    Amount alteredAmount = null;
    @SerializedName("requestSignature")
    private
    String requestSignature = null;
    @SerializedName("merchantTxnId")
    private
    String merchantTransactionId = null;
    @SerializedName("merchantAccessKey")
    private
    String merchantAccessKey = null;
    @SerializedName("returnUrl")
    private
    String returnUrl = null;
    @SerializedName("notifyUrl")
    private
    String notifyUrl = null;
    @SerializedName("customParameters")
    private
    Map<String, String> customParametersMap = null;


    /**
     * @param amount
     * @param requestSignature
     * @param merchantTransactionId
     * @param merchantAccessKey
     * @param returnUrl
     * @throws CitrusException <p> when either transaction amount or transactionId or merchantAccessKey or requestSignature or returnUrl is null or transactionId is more than 24 characters. </p>
     */
    public PaymentBill(Amount amount, String requestSignature, String merchantTransactionId,
                       String merchantAccessKey, String returnUrl) throws CitrusException {
        this.amount = amount;
        this.requestSignature = requestSignature;
        this.merchantTransactionId = merchantTransactionId;
        this.merchantAccessKey = merchantAccessKey;
        this.returnUrl = returnUrl;

        if (amount == null || TextUtils.isEmpty(amount.getValue())) {
            throw new CitrusException("Transaction Amount should not be null or empty.");
        } else if (!(amount.getValueAsDouble() > 0)) {
            throw new CitrusException("Transaction Amount should be greater than 0");
        } else if (TextUtils.isEmpty(merchantTransactionId)) {
            throw new CitrusException("merchantTransactionId should not be null or empty.");
        } else if (merchantTransactionId.length() > 24) {
            throw new CitrusException("merchantTransactionId should not be more than 24 characters.");
        } else if (TextUtils.isEmpty(returnUrl)) {
            throw new CitrusException("Return Url should not be null or empty.");
        } else if (TextUtils.isEmpty(requestSignature)) {
            throw new CitrusException("requestSignature should not be null or empty.");
        } else if (TextUtils.isEmpty(merchantAccessKey)) {
            throw new CitrusException("merchantAccessKey should not be null or empty.");
        }
    }

    /**
     * @param amount
     * @param requestSignature
     * @param merchantTransactionId
     * @param merchantAccessKey
     * @param returnUrl
     * @param notifyUrl
     * @param customParametersMap
     * @throws CitrusException <p> when either transaction amount or transactionId or merchantAccessKey or requestSignature or returnUrl is null or transactionId is more than 24 characters. </p>
     */
    public PaymentBill(Amount amount, String requestSignature, String merchantTransactionId,
                       String merchantAccessKey, String returnUrl, String notifyUrl,
                       Map<String, String> customParametersMap) throws CitrusException {
        this.amount = amount;
        this.requestSignature = requestSignature;
        this.merchantTransactionId = merchantTransactionId;
        this.merchantAccessKey = merchantAccessKey;
        this.returnUrl = returnUrl;
        this.notifyUrl = notifyUrl;
        this.customParametersMap = customParametersMap;

        if (amount == null || TextUtils.isEmpty(amount.getValue())) {
            throw new CitrusException("Transaction Amount should not be null or empty.");
        } else if (!(amount.getValueAsDouble() > 0)) {
            throw new CitrusException("Transaction Amount should be greater than 0");
        } else if (TextUtils.isEmpty(merchantTransactionId)) {
            throw new CitrusException("merchantTransactionId should not be null or empty.");
        } else if (merchantTransactionId.length() > 24) {
            throw new CitrusException("merchantTransactionId should not be more than 24 characters.");
        } else if (TextUtils.isEmpty(returnUrl)) {
            throw new CitrusException("Return Url should not be null or empty.");
        } else if (TextUtils.isEmpty(requestSignature)) {
            throw new CitrusException("requestSignature should not be null or empty.");
        } else if (TextUtils.isEmpty(merchantAccessKey)) {
            throw new CitrusException("merchantAccessKey should not be null or empty.");
        }
    }

    private PaymentBill() {

    }

    public Amount getAmount() {
        return amount;
    }

    public String getRequestSignature() {
        return requestSignature;
    }

    public String getMerchantTransactionId() {
        return merchantTransactionId;
    }

    public String getMerchantAccessKey() {
        return merchantAccessKey;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public Map<String, String> getCustomParametersMap() {
        return customParametersMap;
    }

    public static PaymentBill fromJSON(String json) {
        PaymentBill paymentBill = null;

        JSONObject billObject = null;
        try {
            billObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (billObject != null) {
            Amount amount = null;
            String requestSignature = null;
            String merchantTransactionId = null; // TODO: Do the validation of the transaction id length
            String merchantAccessKey = null;
            String returnUrl = null;
            String notifyUrl = null;
            Map<String, String> customParametersMap = null;


            amount = Amount.fromJSONObject(billObject.optJSONObject("amount"));
            requestSignature = billObject.optString("requestSignature");
            merchantTransactionId = billObject.optString("merchantTxnId");
            merchantAccessKey = billObject.optString("merchantAccessKey");
            returnUrl = billObject.optString("returnUrl");
            notifyUrl = billObject.optString("notifyUrl");

            JSONObject customParamsObject = billObject.optJSONObject("customParameters");
            if (customParamsObject != null) {
                customParametersMap = new HashMap<>();
                Iterator<String> iter = customParamsObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    String value = customParamsObject.optString(key);

                    customParametersMap.put(key, value);
                }
            }

            if (amount != null && requestSignature != null && returnUrl != null
                    && merchantAccessKey != null && merchantTransactionId != null) {

                try {
                    paymentBill = new PaymentBill(amount, requestSignature, merchantTransactionId,
                            merchantAccessKey, returnUrl, notifyUrl, customParametersMap);
                } catch (CitrusException e) {
                    e.printStackTrace();
                }
            }
        }

        return paymentBill;
    }

    public static JSONObject toJSONObject(PaymentBill paymentBill) {
        JSONObject billObject = null;

        if (paymentBill != null) {
            Amount amount = paymentBill.getAmount();
            String merchantAccessKey = paymentBill.getMerchantAccessKey();
            String merchantTransactionId = paymentBill.getMerchantTransactionId();
            String requestSignature = paymentBill.getRequestSignature();
            String returnUrl = paymentBill.getReturnUrl();
            String notifyUrl = paymentBill.getNotifyUrl();
            Map<String, String> customParametersMap = paymentBill.getCustomParametersMap();

            if (amount != null && requestSignature != null && merchantAccessKey != null
                    && merchantTransactionId != null && returnUrl != null) {

                try {
                    billObject = new JSONObject();
                    billObject.put("amount", Amount.toJSONObject(amount));
                    billObject.put("merchantTxnId", merchantTransactionId);
                    billObject.put("merchantAccessKey", merchantAccessKey);
                    billObject.put("requestSignature", requestSignature);
                    billObject.put("returnUrl", returnUrl);

                    if (!TextUtils.isEmpty(notifyUrl)) {
                        billObject.put("notifyUrl", notifyUrl);
                    }

                    // Putting customParameters
                    if (customParametersMap != null && customParametersMap.size() > 0) {
                        JSONObject customParamsObj = new JSONObject();
                        for (Map.Entry<String, String> entry : customParametersMap.entrySet()) {
                            customParamsObj.put(entry.getKey(), entry.getValue());
                        }

                        billObject.put("customParameters", customParamsObj);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return billObject;
    }

    @Override
    public String toString() {
        return "PaymentBill{" +
                "amount=" + amount +
                ", requestSignature='" + requestSignature + '\'' +
                ", merchantTransactionId='" + merchantTransactionId + '\'' +
                ", merchantAccessKey='" + merchantAccessKey + '\'' +
                ", returnUrl='" + returnUrl + '\'' +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", customParametersMap=" + customParametersMap +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.amount, 0);
        dest.writeString(this.requestSignature);
        dest.writeString(this.merchantTransactionId);
        dest.writeString(this.merchantAccessKey);
        dest.writeString(this.returnUrl);
        dest.writeString(this.notifyUrl);
        dest.writeMap(this.customParametersMap);
    }

    protected PaymentBill(Parcel in) {
        this.amount = in.readParcelable(Amount.class.getClassLoader());
        this.requestSignature = in.readString();
        this.merchantTransactionId = in.readString();
        this.merchantAccessKey = in.readString();
        this.returnUrl = in.readString();
        this.notifyUrl = in.readString();
        this.customParametersMap = in.readHashMap(String.class.getClassLoader());
    }

    public static final Creator<PaymentBill> CREATOR = new Creator<PaymentBill>() {
        public PaymentBill createFromParcel(Parcel source) {
            return new PaymentBill(source);
        }

        public PaymentBill[] newArray(int size) {
            return new PaymentBill[size];
        }
    };
}
