package com.citrus.sdk.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.dynamicPricing.DynamicPricingResponse;
import com.citrus.sdk.otp.NetBankForOTP;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.CreditCardOption;
import com.citrus.sdk.payment.DebitCardOption;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.payment.PaymentBill;
import com.citrus.sdk.payment.PaymentOption;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * Created by salil on 8/7/15.
 */
public final class Utils {

    public static boolean isNetworkConnected(Context context) {

        boolean connected = false;

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        connected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        cm = null;
        return connected;
    }

    public static String removeSpecialCharacters(String input) {
        String str = null;
        if (!TextUtils.isEmpty(input)) {
            str = input.replaceAll("[^\\w\\s-]", "").replaceAll("[-_]", "");
        }

        return str;
    }

    public static String getOTP(String message, NetBankForOTP netBankForOTP) {
        String otp = "";
        int length = netBankForOTP.getOTPLength();

        String[] nbs = message.split("\\D+");

        if (nbs.length != 0) {
            for (String number : nbs) {
                if (number.length() == length) {
                    return number;
                }
            }
        }

        return otp;
    }

    /**
     * Get the transaction parameters for cancel transaction in URL Encoded
     *
     * @return
     */
    public static String getURLEncodedParamsForCancelTransaction(CitrusUser citrusUser, PaymentBill paymentBill, PaymentOption paymentOption, DynamicPricingResponse dynamicPricingResponse, String vanity) {

        CitrusUser.Address address = citrusUser != null ? citrusUser.getAddress() : null;
        StringBuffer buffer = new StringBuffer();

        try {
            // Merchant Vanity
            buffer.append("vanityUrl=");
            if (!TextUtils.isEmpty(vanity)) {
                buffer.append(vanity);
            }

            // User Details
            buffer.append("&firstName=");
            if (citrusUser != null && !TextUtils.isEmpty(citrusUser.getFirstName())) {
                buffer.append(URLEncoder.encode(citrusUser.getFirstName(), "utf-8"));
            }

            buffer.append("&lastName=");
            if (citrusUser != null && !TextUtils.isEmpty(citrusUser.getLastName())) {
                buffer.append(URLEncoder.encode(citrusUser.getLastName(), "utf-8"));
            }

            buffer.append("&email=");
            if (citrusUser != null && !TextUtils.isEmpty(citrusUser.getEmailId())) {
                buffer.append(URLEncoder.encode(citrusUser.getEmailId(), "utf-8"));
            }

            buffer.append("&phoneNumber=");
            if (citrusUser != null && !TextUtils.isEmpty(citrusUser.getMobileNo())) {
                buffer.append(URLEncoder.encode(citrusUser.getMobileNo(), "utf-8"));
            }

            buffer.append("&addressCountry=");
            if (address != null && !TextUtils.isEmpty(address.getCountry())) {
                buffer.append(URLEncoder.encode(address.getCountry(), "utf-8"));
            }

            buffer.append("&addressState=");
            if (address != null && !TextUtils.isEmpty(address.getState())) {
                buffer.append(URLEncoder.encode(address.getState(), "utf-8"));
            }

            buffer.append("&addressCity=");
            if (address != null && !TextUtils.isEmpty(address.getCity())) {
                buffer.append(URLEncoder.encode(address.getCity(), "utf-8"));
            }

            buffer.append("&addressStreet1=");
            if (address != null && !TextUtils.isEmpty(address.getStreet1())) {
                buffer.append(URLEncoder.encode(address.getStreet1(), "utf-8"));
            }

            buffer.append("&addressStreet2=");
            if (address != null && !TextUtils.isEmpty(address.getStreet2())) {
                buffer.append(URLEncoder.encode(address.getStreet2(), "utf-8"));
            }

            buffer.append("&addressZip=");
            if (address != null && !TextUtils.isEmpty(address.getZip())) {
                buffer.append(URLEncoder.encode(address.getZip(), "utf-8"));
            }

            // PaymentOption Details
            if (paymentOption != null) {
                if (paymentOption instanceof CardOption) {
                    CardOption cardOption = (CardOption) paymentOption;
                    buffer.append("&paymentMode=");
                    buffer.append(URLEncoder.encode((cardOption instanceof CreditCardOption ? "CREDIT_CARD" : "DEBIT_CARD"), "utf-8"));

                    // Card Details
                    buffer.append("&cardNumber=");
                    if (!TextUtils.isEmpty(cardOption.getCardNumber())) {
                        buffer.append(URLEncoder.encode(cardOption.getCardNumber(), "utf-8"));
                    }

                    buffer.append("&cvvNumber=");
                    if (!TextUtils.isEmpty(cardOption.getCardCVV())) {
                        buffer.append(URLEncoder.encode(cardOption.getCardCVV(), "utf-8"));
                    }

                    buffer.append("&expiryMonth=");
                    if (!TextUtils.isEmpty(cardOption.getCardExpiryMonth())) {
                        buffer.append(URLEncoder.encode(cardOption.getCardExpiryMonth(), "utf-8"));
                    }

                    buffer.append("&expiryYear=");
                    if (!TextUtils.isEmpty(cardOption.getCardExpiryYear())) {
                        buffer.append(URLEncoder.encode(cardOption.getCardExpiryYear(), "utf-8"));
                    }

                    buffer.append("&cardType=");
                    if (cardOption.getCardScheme() != null) {
                        buffer.append(URLEncoder.encode(cardOption.getCardScheme().getName(), "utf-8"));
                    }
                } else if (paymentOption instanceof NetbankingOption) {
                    buffer.append("&paymentMode=");
                    buffer.append(URLEncoder.encode("NET_BANKING", "utf-8"));
                }
            }

            // PaymentDetails
            if (paymentBill != null) {
                buffer.append("&returnUrl=");
                if (!TextUtils.isEmpty(paymentBill.getReturnUrl())) {
                    buffer.append(URLEncoder.encode(paymentBill.getReturnUrl(), "utf-8"));
                }

                buffer.append("&notifyUrl=");
                if (!TextUtils.isEmpty(paymentBill.getNotifyUrl())) {
                    buffer.append(URLEncoder.encode(paymentBill.getNotifyUrl(), "utf-8"));
                }

                // Amount
                Amount amount = paymentBill.getAmount();
                if (amount != null) {
                    buffer.append("&orderAmount=");
                    buffer.append(URLEncoder.encode(amount.getValue(), "utf-8"));

                    buffer.append("&currency=");
                    buffer.append(URLEncoder.encode(amount.getCurrency(), "utf-8"));
                }

                buffer.append("&secSignature=");
                if (!TextUtils.isEmpty(paymentBill.getRequestSignature())) {
                    buffer.append(URLEncoder.encode(paymentBill.getRequestSignature(), "utf-8"));
                }

                buffer.append("&merchantTxnId=");
                if (!TextUtils.isEmpty(paymentBill.getMerchantTransactionId())) {
                    buffer.append(URLEncoder.encode(paymentBill.getMerchantTransactionId(), "utf-8"));
                }

                buffer.append("&merchantAccessKey=");
                if (!TextUtils.isEmpty(paymentBill.getMerchantAccessKey())) {
                    buffer.append(URLEncoder.encode(paymentBill.getMerchantAccessKey(), "utf-8"));
                }

                // Dynamic Pricing.
                buffer.append("&dpSignature=");
                if (TextUtils.isEmpty(paymentBill.getDpSignature())) {
                    buffer.append(URLEncoder.encode(paymentBill.getDpSignature(), "utf-8"));
                }

                Map<String, String> customParametersMap = paymentBill.getCustomParametersMap();
                // Sending the customParameters.
                if (customParametersMap != null) {
                    Set<String> keys = customParametersMap.keySet();
                    int count = 0;
                    for (String key : keys) {
                        // Customparameters are sent in following manner.
                        // customParams%5B3%5D.name=OriginPhoneNo&customParams%5B3%5D.value=9988878899 i.e. in decoded format "customParams[3].name=OriginPhoneNo&customParams[3].value=9988878899"
                        buffer.append("&");
                        buffer.append(URLEncoder.encode(String.format("customParams[%d].name", count), "utf-8"));
                        buffer.append(String.format("=%s", URLEncoder.encode(key, "utf-8")));

                        buffer.append("&");
                        buffer.append(URLEncoder.encode(String.format("customParams[%d].value", count), "utf-8"));
                        buffer.append(String.format("=%s", URLEncoder.encode(customParametersMap.get(key), "utf-8")));

                        count++;
                    }
                }
            }

            // Dynamic Pricing
            if (dynamicPricingResponse != null) {
                buffer.append("&alteredAmount=");

                if (dynamicPricingResponse.getAlteredAmount() != null) {
                    buffer.append(URLEncoder.encode(dynamicPricingResponse.getAlteredAmount().getValue(), "utf-8"));
                }
            }

            // Other required parameters.
            buffer.append("&isEMI=");
            buffer.append("&pgCode=");
            buffer.append("&dpFlag=");
            buffer.append("&errorMessage=");
            buffer.append("&retryCount=0");
            buffer.append("&paymentModeType=Editable");

            buffer.append("&couponCode=");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
