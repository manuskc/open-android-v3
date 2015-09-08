package com.citrus.sdk.classes;

import com.citrus.sdk.classes.Amount;
import com.google.gson.annotations.Expose;

/**
 * Created by MANGESH KADAM on 5/8/2015.
 */
public class BillGeneratorPOJO {

    @Expose
    private String merchantTxnId;
    @Expose
    private Amount amount;
    @Expose
    private String requestSignature;
    @Expose
    private String merchantAccessKey;
    @Expose
    private String returnUrl;
    @Expose
    private String notifyUrl;

    /**
     *
     * @return
     * The merchantTxnId
     */
    public String getMerchantTxnId() {
        return merchantTxnId;
    }

    /**
     *
     * @param merchantTxnId
     * The merchantTxnId
     */
    public void setMerchantTxnId(String merchantTxnId) {
        this.merchantTxnId = merchantTxnId;
    }

    /**
     *
     * @return
     * The amount
     */
    public Amount getAmount() {
        return amount;
    }

    /**
     *
     * @param amount
     * The amount
     */
    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    /**
     *
     * @return
     * The requestSignature
     */
    public String getRequestSignature() {
        return requestSignature;
    }

    /**
     *
     * @param requestSignature
     * The requestSignature
     */
    public void setRequestSignature(String requestSignature) {
        this.requestSignature = requestSignature;
    }

    /**
     *
     * @return
     * The merchantAccessKey
     */
    public String getMerchantAccessKey() {
        return merchantAccessKey;
    }

    /**
     *
     * @param merchantAccessKey
     * The merchantAccessKey
     */
    public void setMerchantAccessKey(String merchantAccessKey) {
        this.merchantAccessKey = merchantAccessKey;
    }

    /**
     *
     * @return
     * The returnUrl
     */
    public String getReturnUrl() {
        return returnUrl;
    }

    /**
     *
     * @param returnUrl
     * The returnUrl
     */
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    /**
     *
     * @return
     * The notifyUrl
     */
    public String getNotifyUrl() {
        return notifyUrl;
    }

    /**
     *
     * @param notifyUrl
     * The notifyUrl
     */
    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

}
