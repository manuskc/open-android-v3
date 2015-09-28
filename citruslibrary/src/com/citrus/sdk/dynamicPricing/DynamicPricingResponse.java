package com.citrus.sdk.dynamicPricing;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.payment.PaymentBill;
import com.citrus.sdk.payment.PaymentOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by salil on 23/7/15.
 */
public class DynamicPricingResponse implements Parcelable {

    public enum Status {
        SUCCESS, RULE_VALIDATION_FAILED, NO_ELIGIBLE_RULE_FOUND, CALCULATE_PRICING_FAILED, RULE_DOES_NOT_EXIST, OPERATION_NOT_PERMITTED, FAILED;

        public static Status getStatus(int responseCode) {
            switch (responseCode) {
                case 0:
                    return SUCCESS;
                case 1:
                    return RULE_VALIDATION_FAILED;
                case 3:
                    return RULE_DOES_NOT_EXIST;
                case 5:
                    return CALCULATE_PRICING_FAILED;
                case 6:
                    return OPERATION_NOT_PERMITTED;
                case 7:
                    return NO_ELIGIBLE_RULE_FOUND;
                case -1:
                default:
                    return FAILED;

            }
        }
    }

    private int resultCode = -1;
    private Amount originalAmount = null;
    private Amount alteredAmount = null;
    private String offerToken = null;
    private Map<String, String> extraParameters = null;
    private PaymentBill paymentBill = null;
    private PaymentOption paymentOption = null;
    private CitrusUser citrusUser = null;

    private DynamicPricingResponse() {
    }

    public DynamicPricingResponse(int resultCode, Amount originalAmount, Amount alteredAmount, String offerToken, Map<String, String> extraParameters) {
        this.resultCode = resultCode;
        this.originalAmount = originalAmount;
        this.alteredAmount = alteredAmount;
        this.offerToken = offerToken;
        this.extraParameters = extraParameters;
    }

    public Status getStatus() {
        return Status.getStatus(resultCode);
    }

    public String getMessage() {
        switch (getStatus()) {
            case SUCCESS:
                return "Success";
            case RULE_VALIDATION_FAILED:
                return "Rule Validation Failed.";
            case NO_ELIGIBLE_RULE_FOUND:
                return "No Eligible Rule Found.";
            case CALCULATE_PRICING_FAILED:
                return "Failed To Calculate Price";
            case RULE_DOES_NOT_EXIST:
                return "Rule Does Not Exist.";
            case OPERATION_NOT_PERMITTED:
                return "Operation Not Permitted.";
            case FAILED:
            default:
                return "Failed To Apply Dynamic Pricing.";
        }
    }

    public Amount getOriginalAmount() {
        return originalAmount;
    }

    public Amount getAlteredAmount() {
        return alteredAmount;
    }

    public String getOfferToken() {
        return offerToken;
    }

    /**
     * Get human readable consumer message for the rule.
     *
     * @return
     */
    public String getConsumerMessage() {
        return ((extraParameters != null) ? extraParameters.get("consumerMessage") : "");
    }

    public Map<String, String> getExtraParameters() {
        return extraParameters;
    }

    public PaymentBill getPaymentBill() {
        return paymentBill;
    }

    public void setPaymentBill(PaymentBill paymentBill) {
        this.paymentBill = paymentBill;
    }

    public PaymentOption getPaymentOption() {
        return paymentOption;
    }

    public void setPaymentOption(PaymentOption paymentOption) {
        this.paymentOption = paymentOption;
    }

    public CitrusUser getCitrusUser() {
        return citrusUser;
    }

    public void setCitrusUser(CitrusUser citrusUser) {
        this.citrusUser = citrusUser;
    }

    public static final DynamicPricingResponse fromJSON(String json) {
        DynamicPricingResponse dynamicPricingResponse = null;


        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                dynamicPricingResponse = new DynamicPricingResponse();
                dynamicPricingResponse.resultCode = jsonObject.optInt("resultCode", -1);
                dynamicPricingResponse.originalAmount = Amount.fromJSONObject(jsonObject.optJSONObject("originalAmount"));
                dynamicPricingResponse.alteredAmount = Amount.fromJSONObject(jsonObject.optJSONObject("alteredAmount"));
                dynamicPricingResponse.offerToken = jsonObject.optString("offerToken");

                Map<String, String> extraParamsMap = new HashMap<>();

                JSONObject extraParams = jsonObject.optJSONObject("extraParams");
                if (extraParams != null) {
                    Iterator<String> iterator = extraParams.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        extraParamsMap.put(key, extraParams.optString(key));
                    }
                    dynamicPricingResponse.extraParameters = extraParamsMap;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return dynamicPricingResponse;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.resultCode);
        dest.writeParcelable(this.originalAmount, 0);
        dest.writeParcelable(this.alteredAmount, 0);
        dest.writeString(this.offerToken);
        dest.writeMap(this.extraParameters);
        dest.writeParcelable(this.paymentBill, 0);
        dest.writeParcelable(this.paymentOption, 0);
        dest.writeParcelable(this.citrusUser, 0);
    }

    protected DynamicPricingResponse(Parcel in) {
        this.resultCode = in.readInt();
        this.originalAmount = in.readParcelable(Amount.class.getClassLoader());
        this.alteredAmount = in.readParcelable(Amount.class.getClassLoader());
        this.offerToken = in.readString();
        this.extraParameters = in.readHashMap(String.class.getClassLoader());
        this.paymentBill = in.readParcelable(PaymentBill.class.getClassLoader());
        this.paymentOption = in.readParcelable(PaymentOption.class.getClassLoader());
        this.citrusUser = in.readParcelable(CitrusUser.class.getClassLoader());
    }

    public static final Creator<DynamicPricingResponse> CREATOR = new Creator<DynamicPricingResponse>() {
        public DynamicPricingResponse createFromParcel(Parcel source) {
            return new DynamicPricingResponse(source);
        }

        public DynamicPricingResponse[] newArray(int size) {
            return new DynamicPricingResponse[size];
        }
    };
}
