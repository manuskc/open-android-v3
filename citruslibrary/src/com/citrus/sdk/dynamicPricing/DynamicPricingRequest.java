package com.citrus.sdk.dynamicPricing;

import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.payment.PaymentBill;
import com.citrus.sdk.payment.PaymentOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by salil on 23/7/15.
 */
public class DynamicPricingRequest {
    private DynamicPricingRequestType dynamicPricingRequestType;
    private PaymentBill paymentBill;

    public DynamicPricingRequest(DynamicPricingRequestType dynamicPricingRequestType, PaymentBill paymentBill) {
        this.dynamicPricingRequestType = dynamicPricingRequestType;
        this.paymentBill = paymentBill;
    }

    public DynamicPricingRequestType getDynamicPricingRequestType() {
        return dynamicPricingRequestType;
    }

    public PaymentBill getPaymentBill() {
        return paymentBill;
    }

    public static String toJSON(DynamicPricingRequest request) {
        String response = null;

        if (request != null) {
            JSONObject jsonObject = new JSONObject();

            DynamicPricingRequestType dynamicPricingRequestType = request.getDynamicPricingRequestType();
            Amount originalAmount = dynamicPricingRequestType.getOriginalAmount();
            CitrusUser user = dynamicPricingRequestType.getCitrusUser();
            PaymentOption paymentOption = dynamicPricingRequestType.getPaymentOption();
            PaymentBill paymentBill = request.getPaymentBill();
            Map<String, String> extraParamsMap = dynamicPricingRequestType.getExtraParameters();

            try {
                jsonObject.put("email", (user != null ? user.getEmailId() : null));
                jsonObject.put("phone", (user != null ? user.getMobileNo() : null));
                jsonObject.put("merchantTransactionId", paymentBill.getMerchantTransactionId());
                jsonObject.put("merchantAccessKey", paymentBill.getMerchantAccessKey());
                jsonObject.put("signature", paymentBill.getDpSignature());
                jsonObject.put("originalAmount", Amount.toJSONObject(originalAmount));
                jsonObject.put("paymentInfo", getPaymentInformation(paymentOption));

                JSONObject extraParams = new JSONObject();
                if (extraParamsMap != null) {
                    for (String key : extraParamsMap.keySet()) {
                        extraParams.put(key, extraParamsMap.get(key));
                    }
                }
                // Put the operation type for request
                extraParams.put("operation", dynamicPricingRequestType.getDPOperationName());
                jsonObject.put("extraParams", extraParams);

                // Rule Name is required in case of Calculate Price and Validate and Altered Amount is required in case of Validate.
                if (dynamicPricingRequestType instanceof DynamicPricingRequestType.CalculatePrice) {
                    jsonObject.put("ruleName", ((DynamicPricingRequestType.CalculatePrice) dynamicPricingRequestType).getRuleName());
                } else if (dynamicPricingRequestType instanceof DynamicPricingRequestType.ValidateRule) {
                    Amount alteredAmount = ((DynamicPricingRequestType.ValidateRule) dynamicPricingRequestType).getAlteredAmount();

                    jsonObject.put("ruleName", ((DynamicPricingRequestType.ValidateRule) dynamicPricingRequestType).getRuleName());
                    jsonObject.put("alteredAmount", Amount.toJSONObject(alteredAmount));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            response = jsonObject.toString();
        }

        return response;
    }

    private static JSONObject getPaymentInformation(PaymentOption paymentOption) {

        JSONObject jsonObject = new JSONObject();
        try {
            if (paymentOption != null) {
                if (paymentOption.getToken() != null) {
                    jsonObject.put("paymentToken", paymentOption.getToken());
                } else if (paymentOption instanceof NetbankingOption) {
                    jsonObject.put("issuerId", ((NetbankingOption) paymentOption).getBankCID());
                } else if (paymentOption instanceof CardOption) {
                    jsonObject.put("cardNo", ((CardOption) paymentOption).getCardNumber());
                    jsonObject.put("cardType", ((CardOption) paymentOption).getCardScheme().getName().toUpperCase()); // For DP card types are all in CAPS.
                }

                jsonObject.put("paymentMode", paymentOption.getDynamicPricingPaymentMode());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}