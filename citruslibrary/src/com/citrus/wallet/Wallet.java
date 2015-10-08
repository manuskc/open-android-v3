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
package com.citrus.wallet;

import android.app.Activity;
import android.text.TextUtils;

import com.citrus.card.Card;
import com.citrus.card.CardType;
import com.citrus.mobile.Config;
import com.citrus.mobile.OauthToken;
import com.citrus.mobile.RESTclient;
import com.citrus.mobile.User;
import com.citrus.netbank.Bank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @deprecated in v3
 * <p/>
 * This class is no longer supported.
 */
@Deprecated
public class Wallet {
    private Card card;
    private Bank bank;

    public Wallet(Card card) {
        this.card = card;
        base_url = Config.getEnv();
    }

    public Wallet(Bank bank) {
        this.bank = bank;
        base_url = Config.getEnv();
    }

    private String base_url;

    public Wallet() {
        base_url = Config.getEnv();
    }

    /**
     * Save the netbanking option as user preffered option.
     *
     * @param activity
     * @return
     */
    public String saveBank(Activity activity) {

        /*
         * Following json will be required to save the bank as payment option.
          *
          * {"paymentOptions":[{"owner":"","type":"netbanking","bank":"ICICI Bank"}],"type":"payment","defaultOption":""}
         */

        OauthToken token = new OauthToken(activity, User.SIGNIN_TOKEN);
        String access_token = null;

        try {
            access_token = token.getuserToken().getString("access_token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject bankJson = new JSONObject();
        JSONObject bankDetails = new JSONObject();

        try {
            bankJson.put("type", "payment");
            bankJson.put("defaultOption", "");
            bankDetails.put("owner", "");
            bankDetails.put("type", "netbanking");
            bankDetails.put("bank", bank.getBankName());

            JSONArray array = new JSONArray();
            array.put(bankDetails);

            bankJson.put("paymentOptions", array);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject headers = new JSONObject();

        try {
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + access_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RESTclient resTclient = new RESTclient("wallet", base_url, null, headers);

        JSONObject response = resTclient.makePutrequest(bankJson);

        if (response == null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("response", "bank saved successfully!");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject.toString();
        }

        return response.toString();

    }

    public String saveCard(Activity activity) {

        OauthToken token = new OauthToken(activity, User.SIGNIN_TOKEN);
        String access_token = null;

        try {
            access_token = token.getuserToken().getString("access_token");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject cardJson = new JSONObject();
        JSONObject cardDetails = new JSONObject();


        try {
            cardJson.put("type", "payment");
            cardDetails.put("owner", card.getCardHolderName());
            cardDetails.put("number", card.getCardNumber());
            cardDetails.put("scheme", CardType.getScheme(card.getCardType()));
            cardDetails.put("type", card.getCrdr().toLowerCase());

            if (card.getCardType() != null && "MTRO".equalsIgnoreCase(card.getCardType().toString())
                    && TextUtils.isEmpty(card.getCvvNumber())) {
                cardDetails.put("expiryDate", "11/19");
            } else {
                cardDetails.put("expiryDate",
                        card.getExpiryMonth() + "/" + card.getExpiryYear().substring(card.getExpiryYear().length() - 2));
            }
            JSONArray array = new JSONArray();
            array.put(cardDetails);

            cardJson.put("paymentOptions", array);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject headers = new JSONObject();

        try {
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + access_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RESTclient resTclient = new RESTclient("wallet", base_url, null, headers);

        JSONObject response = resTclient.makePutrequest(cardJson);

        if (response == null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("response", "card saved successfully!");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject.toString();
        }

        return response.toString();

    }

    public String getWallet(Activity activity) {

        OauthToken token = new OauthToken(activity, User.SIGNIN_TOKEN);
        String access_token = null;

        try {
            access_token = token.getuserToken().getString("access_token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject headers = new JSONObject();

        try {
            headers.put("Authorization", "Bearer " + access_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RESTclient resTclient = new RESTclient("wallet", base_url, null, headers);

        JSONObject response;
        try {
            response = resTclient.makegetRequest();
        } catch (JSONException e) {
            e.printStackTrace();
            return "Unable to get User Wallet";
        }

        return response.toString();
    }
}
