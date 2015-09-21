package com.citrus.sdk.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by salil on 29/4/15.
 */
public class PaymentResponse extends CitrusResponse implements Parcelable {
    @SerializedName("id")
    protected String transactionId = null;
    @SerializedName("amount")
    protected Amount transactionAmount = null;
    @SerializedName("balance")
    protected Amount balanceAmount = null;
    @SerializedName("cutsomer")
    protected String customer = null;
    @SerializedName("merchant")
    protected String merchantName = null;
    @SerializedName("date")
    protected String date = null;
    protected TransactionResponse transactionResponse = null;
    private JSONObject responseParams = null;

    protected CitrusUser user = null;

    PaymentResponse() {
        super();
    }

    public PaymentResponse(String message, Status status, String transactionId, Amount transactionAmount, Amount balanceAmount, CitrusUser user) {
        super(message, status);

        this.transactionId = transactionId;
        this.transactionAmount = transactionAmount;
        this.balanceAmount = balanceAmount;
        this.user = user;
    }

    public PaymentResponse(String message, Status status, String transactionId, Amount transactionAmount, Amount balanceAmount, CitrusUser user, String merchantName, String date, TransactionResponse transactionResponse) {
        super(message, status);
        this.transactionId = transactionId;
        this.transactionAmount = transactionAmount;
        this.balanceAmount = balanceAmount;
        this.merchantName = merchantName;
        this.date = date;
        this.transactionResponse = transactionResponse;
        this.user = user;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Amount getTransactionAmount() {
        return transactionAmount;
    }

    public Amount getBalanceAmount() {
        return balanceAmount;
    }

    public CitrusUser getUser() {
        return user;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getDate() {
        return date;
    }

    public String getCustomer() {
        return customer;
    }

    private void setResponseParams(JSONObject responseParams) {
        this.responseParams = responseParams;
    }

    public String getURLEncodedParams() {
        StringBuffer buffer = new StringBuffer();

        if (responseParams != null) {
            Iterator<String> keys = responseParams.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                buffer.append(key);
                buffer.append("=");
                try {
                    buffer.append(URLEncoder.encode(responseParams.optString(key), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                buffer.append("&");
            }
        }

        return buffer.toString();
    }

    public static PaymentResponse fromJSON(String json) {
        PaymentResponse paymentResponse = null;

        try {
            JSONObject jsonObject = new JSONObject(json);
            String customer = jsonObject.optString("customer", jsonObject.optString("cutsomer"));
            CitrusResponse.Status status = CitrusResponse.Status.valueOf(jsonObject.optString("status"));
            String message = ((status == Status.SUCCESSFUL) ? "Transaction Successful" : jsonObject.optString("reason"));
            String date = jsonObject.optString("date");
            String merchantName = jsonObject.optString("merchant");
            Amount transactionAmount = Amount.fromJSONObject(jsonObject.optJSONObject("amount"));
            Amount balanceAmount = Amount.fromJSONObject(jsonObject.optJSONObject("balance"));
            CitrusUser citrusUser = CitrusUser.fromJSONObject(jsonObject);

            Map<String, String> customParamsMap = null;
            JSONObject customParamsObject = jsonObject.optJSONObject("customParams");
            if (customParamsObject != null) {
                customParamsMap = new HashMap<>();
                Iterator<String> iter = customParamsObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    String value = customParamsObject.optString(key);

                    customParamsMap.put(key, value);
                }
            }

            JSONObject responseParams = jsonObject.optJSONObject("responseParams");

            TransactionResponse transactionResponse = TransactionResponse.fromJSONObject(responseParams, customParamsMap);
            String transactionId = ((transactionResponse != null) ? transactionResponse.getTransactionId() : null);
            paymentResponse = new PaymentResponse(message, status, transactionId, transactionAmount, balanceAmount, citrusUser, merchantName, date, transactionResponse);

            paymentResponse.setResponseParams(responseParams);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return paymentResponse;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "transactionId='" + transactionId + '\'' +
                ", transactionAmount=" + transactionAmount +
                ", balanceAmount=" + balanceAmount +
                ", customer='" + customer + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", date='" + date + '\'' +
                ", transactionResponse=" + transactionResponse +
                ", user=" + user +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.transactionId);
        dest.writeParcelable(this.transactionAmount, 0);
        dest.writeParcelable(this.balanceAmount, 0);
        dest.writeString(this.customer);
        dest.writeString(this.merchantName);
        dest.writeString(this.date);
        dest.writeParcelable(this.transactionResponse, 0);
        dest.writeParcelable(this.user, 0);
    }

    protected PaymentResponse(Parcel in) {
        super(in);
        this.transactionId = in.readString();
        this.transactionAmount = in.readParcelable(Amount.class.getClassLoader());
        this.balanceAmount = in.readParcelable(Amount.class.getClassLoader());
        this.customer = in.readString();
        this.merchantName = in.readString();
        this.date = in.readString();
        this.transactionResponse = in.readParcelable(TransactionResponse.class.getClassLoader());
        this.user = in.readParcelable(CitrusUser.class.getClassLoader());
    }

    public static final Creator<PaymentResponse> CREATOR = new Creator<PaymentResponse>() {
        public PaymentResponse createFromParcel(Parcel source) {
            return new PaymentResponse(source);
        }

        public PaymentResponse[] newArray(int size) {
            return new PaymentResponse[size];
        }
    };
}
