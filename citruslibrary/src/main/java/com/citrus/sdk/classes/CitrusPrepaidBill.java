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

package com.citrus.sdk.classes;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MANGESH KADAM on 5/26/2015.
 */
public class CitrusPrepaidBill {

    private String merchantTransactionId;
    private String merchant;
    private String customer;
    private Amount amount;
    private String description;
    private String signature;
    private String merchantAccessKey;
    private String returnUrl;
    private String notifyUrl;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The merchantTransactionId
     */
    public String getMerchantTransactionId() {
        return merchantTransactionId;
    }

    /**
     *
     * @param merchantTransactionId
     * The merchantTransactionId
     */
    public void setMerchantTransactionId(String merchantTransactionId) {
        this.merchantTransactionId = merchantTransactionId;
    }

    /**
     *
     * @return
     * The merchant
     */
    public String getMerchant() {
        return merchant;
    }

    /**
     *
     * @param merchant
     * The merchant
     */
    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    /**
     *
     * @return
     * The customer
     */
    public String getCustomer() {
        return customer;
    }

    /**
     *
     * @param customer
     * The customer
     */
    public void setCustomer(String customer) {
        this.customer = customer;
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
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     *
     * @param signature
     * The signature
     */
    public void setSignature(String signature) {
        this.signature = signature;
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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public JSONObject getJSON() {
        final Gson gson = new Gson();
        String json = gson.toJson(this);
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return getJSON().toString();
    }
}