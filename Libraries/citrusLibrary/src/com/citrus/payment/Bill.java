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
package com.citrus.payment;

import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.BillGeneratorPOJO;
import com.citrus.sdk.payment.PaymentBill;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @deprecated in v3
 * <p/>
 * Use {@link PaymentBill} instead.
 */
@Deprecated
public class Bill {
    private String txnId, signature, access_key, returnurl, notifyurl = null;
    private JSONObject customParameters = null;
    private JSONObject amount;

    public Bill(String bill) {
        JSONObject jsonBill = null;
        try {
            jsonBill = new JSONObject(bill);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            this.txnId = jsonBill.getString("merchantTxnId");
            this.amount = jsonBill.getJSONObject("amount");
            this.signature = jsonBill.getString("requestSignature");
            this.access_key = jsonBill.getString("merchantAccessKey");
            this.returnurl = jsonBill.getString("returnUrl");

            if (jsonBill.has("notifyUrl")) {
                this.notifyurl = jsonBill.getString("notifyUrl");
            }

            if (jsonBill.has("customParameters")) {
                this.customParameters = jsonBill.getJSONObject("customParameters");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bill(String bill, String type) {
        JSONObject jsonBill = null;
        try {
            jsonBill = new JSONObject(bill);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            this.txnId = jsonBill.getString("merchantTransactionId");
            this.amount = jsonBill.getJSONObject("amount");
            this.signature = jsonBill.getString("signature");
            this.access_key = jsonBill.getString("merchantAccessKey");
            this.returnurl = jsonBill.getString("returnUrl");

            if (jsonBill.has("notifyUrl")) {
                this.notifyurl = jsonBill.getString("notifyUrl");
            }

            if (jsonBill.has("customParameters")) {
                this.customParameters = jsonBill.getJSONObject("customParameters");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public Bill(BillGeneratorPOJO billGeneratorPOJO) {
        this.txnId = billGeneratorPOJO.getMerchantTxnId();
        this.amount = Amount.toJSONObject(billGeneratorPOJO.getAmount());
        this.signature = billGeneratorPOJO.getRequestSignature();
        this.access_key = billGeneratorPOJO.getMerchantAccessKey();
        this.returnurl = billGeneratorPOJO.getReturnUrl();
        if (billGeneratorPOJO.getNotifyUrl() != null)
            this.notifyurl = billGeneratorPOJO.getNotifyUrl();
    }


    public Bill(PaymentBill paymentBill) {
        this.txnId = paymentBill.getMerchantTransactionId();
        this.amount = Amount.toJSONObject(paymentBill.getAmount());
        this.signature = paymentBill.getRequestSignature();
        this.access_key = paymentBill.getMerchantAccessKey();
        this.returnurl = paymentBill.getReturnUrl();
        if (paymentBill.getNotifyUrl() != null)
            this.notifyurl = paymentBill.getNotifyUrl();
    }

    public String getTxnId() {
        return this.txnId;
    }

    public JSONObject getAmount() {
        return this.amount;
    }

    public String getSignature() {
        return this.signature;
    }

    public String getAccess_key() {
        return this.access_key;
    }

    public String getReturnurl() {
        return this.returnurl;
    }

    public String getNotifyurl() {
        return this.notifyurl;
    }

    public JSONObject getCustomParameters() {
        return customParameters;
    }
}