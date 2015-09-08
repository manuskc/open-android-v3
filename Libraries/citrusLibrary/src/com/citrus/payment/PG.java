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


import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.citrus.card.Card;
import com.citrus.cash.LoadMoney;
import com.citrus.cash.Prepaid;
import com.citrus.mobile.Callback;
import com.citrus.mobile.Config;
import com.citrus.mobile.Errorclass;
import com.citrus.mobile.OauthToken;
import com.citrus.mobile.RESTclient;
import com.citrus.mobile.User;
import com.citrus.netbank.Bank;
import com.citrus.netbank.BankPaymentType;
import com.citrus.retrofit.RetroFitClient;
import com.citrus.sdk.ResponseMessages;
import com.citrus.sdk.classes.AccessToken;
import com.citrus.sdk.classes.CitrusPrepaidBill;
import com.citrus.sdk.classes.StructResponsePOJO;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.response.CitrusError;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedString;


public class PG {

    Activity activity;

    private Card card;
    private Bill bill;
    private UserDetails userDetails;
    private Callback callback, internal;

    private JSONObject payment;

    private Bank bank;

    private Prepaid prepaid;

    private String paymenttype;

    private JSONObject customParameters = null;

    private LoadMoney loadmoney;

    ArrayList<String> mylist = new ArrayList<String>();

    public PG(PaymentOption paymentOption, Bill bill, UserDetails userDetails) {
        if (paymentOption != null) {
            if (paymentOption instanceof CardOption) {
                CardOption cardOption = (CardOption) paymentOption;
                // If token payment
                if (cardOption.getToken() != null) {
                    this.card = new Card(cardOption.getToken(), cardOption.getCardCVV());
                    this.paymenttype = "cardtoken";
                } else {
                    this.card = new Card(cardOption.getCardNumber(), cardOption.getCardExpiryMonth(), cardOption.getCardExpiryYear(), cardOption.getCardCVV(), cardOption.getCardHolderName(), cardOption.getCardType());
                    this.paymenttype = "card";
                }
            } else if (paymentOption instanceof NetbankingOption) {
                NetbankingOption netbankingOption = (NetbankingOption) paymentOption;
                // If token payment
                if (netbankingOption.getToken() != null) {
                    this.bank = new Bank(netbankingOption.getToken(), BankPaymentType.TOKEN);
                    this.paymenttype = this.bank.getPaymentType().toString();
                } else {
                    this.bank = new Bank(netbankingOption.getBankCID());
                    this.paymenttype = "netbank";
                }
            }
        }
        this.bill = bill;
        this.userDetails = userDetails;
        this.customParameters = bill.getCustomParameters();
    }

    public PG(PaymentOption paymentOption, LoadMoney load, UserDetails userDetails) {
        if (paymentOption != null) {
            if (paymentOption instanceof CardOption) {
                CardOption cardOption = (CardOption) paymentOption;
                // If token payment
                if (cardOption.getToken() != null) {
                    this.card = new Card(cardOption.getToken(), cardOption.getCardCVV());
                    this.paymenttype = "cardtoken";
                } else {
                    this.card = new Card(cardOption.getCardNumber(), cardOption.getCardExpiryMonth(), cardOption.getCardExpiryYear(), cardOption.getCardCVV(), cardOption.getCardHolderName(), cardOption.getCardType());
                    this.paymenttype = "card";
                }
            } else if (paymentOption instanceof NetbankingOption) {
                NetbankingOption netbankingOption = (NetbankingOption) paymentOption;
                // If token payment
                if (netbankingOption.getToken() != null) {
                    this.bank = new Bank(netbankingOption.getToken(), BankPaymentType.TOKEN);
                    this.paymenttype = this.bank.getPaymentType().toString();
                } else {
                    this.bank = new Bank(netbankingOption.getBankCID());
                    this.paymenttype = "netbank";
                }
            }
        }
        this.loadmoney = load;
        this.userDetails = userDetails;
    }


    public PG(Card card, Bill bill, UserDetails userDetails) {
        this.card = card;
        this.bill = bill;
        this.userDetails = userDetails;

        if (TextUtils.isEmpty(card.getCardNumber())) {
            paymenttype = "cardtoken";
        } else {
            paymenttype = "card";
        }
        this.customParameters = bill.getCustomParameters();
    }

    public PG(Bank bank, Bill bill, UserDetails userDetails) {
        this.bank = bank;
        this.bill = bill;
        this.userDetails = userDetails;
        if (this.bank.getPaymentType() != null)
            paymenttype = this.bank.getPaymentType().toString();
        else
            paymenttype = "netbank";

        this.customParameters = bill.getCustomParameters();
    }

    public PG(Prepaid prepaid, Bill bill, UserDetails userDetails) {
        this.bill = bill;
        this.userDetails = userDetails;
        this.prepaid = prepaid;
        paymenttype = "prepaid";

        this.customParameters = bill.getCustomParameters();
    }

    public PG(Card card, LoadMoney load, UserDetails userDetails) {
        this.card = card;
        this.userDetails = userDetails;

        this.loadmoney = load;

        if (TextUtils.isEmpty(card.getCardNumber())) {
            paymenttype = "cardtoken";
        } else {
            paymenttype = "card";
        }

    }

    public PG(Bank bank, LoadMoney load, UserDetails userDetails) {
        this.bank = bank;
        this.userDetails = userDetails;
        this.loadmoney = load;
        if (this.bank.getPaymentType() != null)
            paymenttype = this.bank.getPaymentType().toString();
        else
            paymenttype = "netbank";
    }

    /**
     * @param callback
     * @deprecated Use {@link com.citrus.sdk.CitrusActivity} instead.
     */
    public void charge(Callback callback) {
        this.callback = callback;

        validate();

    }

    public void load(Activity activity, Callback callback) {
        this.callback = callback;

        this.activity = activity;

        internal = new Callback() {

            @Override
            public void onTaskexecuted(String success, String error) {
                if (!TextUtils.isEmpty(success)) {
                    formprepaidBill(success);
                } else {
                    PG.this.callback.onTaskexecuted("", error);
                }

            }
        };

        getPrepaidBill(loadmoney.getAmount(), loadmoney.getReturl());
    }

    private void formprepaidBill(String prepaid_bill) {
        this.bill = new Bill(prepaid_bill, "prepaid");

        validate();
    }

    public void setCustomParameters(JSONObject customParameters) {
        this.customParameters = customParameters;
    }

    private void validate() {

        if (TextUtils.equals(paymenttype.toString(), "card") || TextUtils.equals(paymenttype.toString(), "cardtoken")) {
            if (TextUtils.isEmpty(card.getCardNumber()) && TextUtils.isEmpty(card.getcardToken())) {
                callback.onTaskexecuted("", "Invalid Card or Card token!");
                return;
            }

            if (!TextUtils.isEmpty(card.getCardNumber())) {
                if (!card.validateCard()) {
                    callback.onTaskexecuted("", "Invalid Card!");
                    return;
                }
            }
        }


        String access_key = bill.getAccess_key();
        String txn_id = bill.getTxnId();
        String signature = bill.getSignature();
        String returnUrl = bill.getReturnurl();

        String email = userDetails.getEmail();
        String mobile = userDetails.getMobile();
        String firstname = userDetails.getFirstname();
        String lastname = userDetails.getLastname();

        mylist.add(access_key);
        mylist.add(txn_id);
        mylist.add(signature);
        mylist.add(returnUrl);
        mylist.add(email);
        mylist.add(mobile);
        mylist.add(firstname);
        mylist.add(lastname);

        checkifnull();

        formjson();
    }

    private void checkifnull() {
        for (String param : mylist) {
            if (TextUtils.isEmpty(param)) {
                callback.onTaskexecuted("", "Bill or userdetails can not contain empty parameters");
                return;
            }
        }
    }

    private void formjson() {
        JSONObject paymentToken = new JSONObject();
        boolean isTokenizedPayment = false;
        JSONObject paymentmode;
        if (TextUtils.equals(paymenttype.toString(), "card")) {

            paymentmode = new JSONObject();
            try {
                if (card.getCardType() != null && "MTRO".equalsIgnoreCase(card.getCardType().toString())
                        && TextUtils.isEmpty(card.getCvvNumber())) {
                    paymentmode.put("cvv", "123"); //dummy value
                } else {
                    paymentmode.put("cvv", card.getCvvNumber());
                }


                paymentmode.put("holder", card.getCardHolderName());
                paymentmode.put("number", card.getCardNumber());
                paymentmode.put("scheme", card.getCardType());
                paymentmode.put("type", card.getCrdr());
                if (card.getCardType() != null && "MTRO".equalsIgnoreCase(card.getCardType().toString())
                        && TextUtils.isEmpty(card.getExpiryMonth()) && TextUtils.isEmpty(card.getExpiryYear())) {
                    paymentmode.put("expiry", "11/2019"); // This is the dummy value
                } else {
                    paymentmode.put("expiry", card.getExpiryMonth() + "/" + card.getExpiryYear());
                }

                paymentToken.put("type", "paymentOptionToken");
                paymentToken.put("paymentMode", paymentmode);
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onTaskexecuted("", "Problem forming payment Json");
                return;
            }

        } else if (TextUtils.equals(paymenttype.toString(), "prepaid")) { //pay using citrus cash
            isTokenizedPayment = true;
            paymentmode = new JSONObject();
            try {
                paymentmode.put("cvv", "000");
                paymentmode.put("holder", prepaid.getUserEmail());
                paymentmode.put("number", "1234561234561234");
                paymentmode.put("scheme", "CPAY");
                paymentmode.put("type", "prepaid");
                paymentmode.put("expiry", "04/2030");

                paymentToken.put("type", "paymentOptionToken");
                paymentToken.put("paymentMode", paymentmode);
            } catch (JSONException e) {
                e.printStackTrace();
                callback.onTaskexecuted("", "Problem forming payment Json");
                return;
            }
        } else if (TextUtils.equals(paymenttype.toString(), "cardtoken")) { //tokenized card payment
            isTokenizedPayment = true;
            try {
                paymentToken.put("type", "paymentOptionIdToken");
                paymentToken.put("id", card.getcardToken());
                paymentToken.put("cvv", card.getCvvNumber());
                isTokenizedPayment = true;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            try {
                if (paymenttype != null && paymenttype.equalsIgnoreCase(BankPaymentType.TOKEN.toString())) { //tokenized bank payment
                    isTokenizedPayment = true;
                    paymentToken.put("id", bank.getBankToken());
                    paymentToken.put("type", bank.getPaymentType().toString());
                } else { //bank payment with CID
                    paymentmode = new JSONObject();
                    paymentmode.put("type", "netbanking");
                    paymentmode.put("code", bank.getCidnumber());
                    paymentToken.put("type", "paymentOptionToken");
                    paymentToken.put("paymentMode", paymentmode);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        JSONObject userdetails = new JSONObject();

        JSONObject address = new JSONObject();

        try {
            address.put("state", userDetails.getState());
            address.put("street1", userDetails.getStreet1());
            address.put("street2", userDetails.getStreet2());
            address.put("city", userDetails.getCity());
            address.put("country", userDetails.getCountry());
            address.put("zip", userDetails.getZip());
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onTaskexecuted("", "Problem forming in address Json");
            return;
        }

        try {
            userdetails.put("email", userDetails.getEmail());
            userdetails.put("mobileNo", userDetails.getMobile());
            userdetails.put("firstName", userDetails.getFirstname());
            userdetails.put("lastName", userDetails.getLastname());
            userdetails.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onTaskexecuted("", "Problem forming in userdetails Json");
            return;
        }

        payment = new JSONObject();

        try {
            payment.put("returnUrl", bill.getReturnurl());

            if (bill.getNotifyurl() != null) {
                payment.put("notifyUrl", bill.getNotifyurl());
            }

            payment.put("amount", bill.getAmount());
            payment.put("merchantAccessKey", bill.getAccess_key());

            if (customParameters != null) {
                payment.put("customParameters", customParameters);
            }


            payment.put("paymentToken", paymentToken);
            payment.put("merchantTxnId", bill.getTxnId());
            payment.put("requestSignature", bill.getSignature());
            if (isTokenizedPayment) //Priyank Changes {
            {
                payment.put("requestOrigin", "MSDKW");
            } else {
                payment.put("requestOrigin", "MSDKG");
            }
            payment.put("userDetails", userdetails);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onTaskexecuted("", "Problem forming in userdetails Json");
            return;
        }

        JSONObject headers = new JSONObject();

        try {
            headers.put("Content-Type", "application/json");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //new MakePayment(payment, headers, callback).execute();
        retrofitCharge();

    }




    private void retrofitCharge() {
        RetroFitClient.getCitrusRetroFitClient().getPaymentResponse(new TypedString(payment.toString()), new retrofit.Callback<StructResponsePOJO>() {
            @Override
            public void success(StructResponsePOJO structResponse, Response response) {
                String motoResponse = structResponse.getJSON().toString();
                Logger.d("MOTO SUCCESSFUL***" + motoResponse);
                callback.onTaskexecuted(motoResponse, "");


            }

            @Override
            public void failure(RetrofitError error) {
                Logger.d("FAILED MOTO CALL**** " + error.getMessage());
                if(error.getKind() == RetrofitError.Kind.NETWORK)
                    callback.onTaskexecuted("", ResponseMessages.ERROR_NETWORK_CONNECTION);
                else
                    callback.onTaskexecuted("", error.getMessage());
            }
        });

    }

    private class GetPrepaidbill extends AsyncTask<String, Void, JSONObject> {
        JSONObject headers, params, response = null;

        @Override
        protected JSONObject doInBackground(String... params) {
            headers = new JSONObject();
            OauthToken token = new OauthToken(activity, User.PREPAID_TOKEN);
            try {
                JSONObject tokenjson = token.getuserToken();
                String access_token = null;
                if (tokenjson != null) {
                    access_token = tokenjson.getString("access_token");
                } else {
                    return Errorclass.addErrorFlag("Prepaid Oauth Token is missing - did you sign in the user?", null);
                }

                try {
                    headers.put("Authorization", "Bearer " + access_token);
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return Errorclass.addErrorFlag("Prepaid Oauth Token is missing - did you sign in the user?", null);
            }

            try {
                this.params = new JSONObject();
                this.params.put("amount", params[0]);
                this.params.put("currency", "INR");
                this.params.put("redirect", params[1]);

            } catch (JSONException e) {
                e.printStackTrace();
                return Errorclass.addErrorFlag("Prepaid bill parameters are missing", null);
            }

            RESTclient restClient = new RESTclient("prepaidbill", Config.getEnv(), this.params, headers);

            try {
                response = restClient.makePostrequest();
            } catch (IOException e) {
                e.printStackTrace();
                return Errorclass.addErrorFlag("IO Exception - check if internet is working!", null);
            }

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            if (result.has("error")) {
                internal.onTaskexecuted("", result.toString());
            } else {
                internal.onTaskexecuted(result.toString(), "");
            }
        }
    }


    void getPrepaidBill(final String amount, final String returnURL) {
        OauthToken token = new OauthToken(activity);
        token.getPrepaidToken(new com.citrus.sdk.Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken) {
                String header = "Bearer " + accessToken.getAccessToken();
                RetroFitClient.getCitrusRetroFitClient().getPrepaidBill(header, amount, returnURL, "INR", new retrofit.Callback<CitrusPrepaidBill>() {
                    @Override
                    public void success(CitrusPrepaidBill citrusPrepaidBill, Response response) {
                        Logger.d("PREPAID BILL RESONSE ***" + citrusPrepaidBill.toString());
                        internal.onTaskexecuted(citrusPrepaidBill.toString(), "");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        String json = null;
                        String message = null;
                        if (error != null && error.getResponse() != null && error.getResponse().getBody() != null) {
                            json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                        }

                        Logger.d("Failed to get Prepaid Bill" + json);

                        if (json != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                message = jsonObject.optString("errorMessage");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        internal.onTaskexecuted("", message);
                    }
                });
            }

            @Override
            public void error(CitrusError error) {
                Logger.d("Failed to get Prepaid Token***");
                internal.onTaskexecuted("", "{\"message\":\"Prepaid Oauth Token is missing - did you sign in the user?\",\"error\":\"600\"}");
            }
        });


    }

}
