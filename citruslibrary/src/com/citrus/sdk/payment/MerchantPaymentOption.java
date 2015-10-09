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

import android.text.TextUtils;

import com.citrus.sdk.classes.PGHealth;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.citrus.sdk.payment.CardOption.CardScheme;

/**
 * Created by salil on 12/5/15.
 */
public class MerchantPaymentOption {

    private Set<CardOption.CardScheme> creditCardSchemeSet = null;
    private Set<CardOption.CardScheme> debitCardSchemeSet = null;
    private ArrayList<NetbankingOption> netbankingOptionList = null;

    private MerchantPaymentOption(Set<CardScheme> creditCardSchemeSet,
                                  Set<CardScheme> debitCardSchemeSet,
                                  ArrayList<NetbankingOption> netbankingOptionList) {
        this.creditCardSchemeSet = creditCardSchemeSet;
        this.debitCardSchemeSet = debitCardSchemeSet;
        this.netbankingOptionList = netbankingOptionList;
    }

    public Set<CardScheme> getCreditCardSchemeSet() {
        return creditCardSchemeSet;
    }

    public Set<CardScheme> getDebitCardSchemeSet() {
        return debitCardSchemeSet;
    }

    public ArrayList<NetbankingOption> getNetbankingOptionList() {
        return netbankingOptionList;
    }

    public static MerchantPaymentOption getMerchantPaymentOptions(JsonObject merchantPaymentOptionsObj) {
        return getMerchantPaymentOptions(merchantPaymentOptionsObj, null);
    }

    public static MerchantPaymentOption getMerchantPaymentOptions(JsonObject merchantPaymentOptionsObj, Map<String, PGHealth> pgHealthMap) {
        MerchantPaymentOption merchantPaymentOption;
        Set<CardScheme> debitCardSchemeSet = null;
        Set<CardScheme> creditCardSchemeSet = null;
        ArrayList<NetbankingOption> netbankingOptionList = null;

        JsonArray bankArray = merchantPaymentOptionsObj.getAsJsonArray("netBanking");
        JsonArray creditCardArray = merchantPaymentOptionsObj.getAsJsonArray("creditCard");
        JsonArray debitCardArray = merchantPaymentOptionsObj.getAsJsonArray("debitCard");

        int size = -1;
        // Parse credit card scheme
        size = creditCardArray.size();
        for (int i = 0; i < size; i++) {
            JsonElement element = creditCardArray.get(i);
            String cardScheme = element.getAsString();

            if (creditCardSchemeSet == null) {
                creditCardSchemeSet = new HashSet<>();
            }

            if (getCardScheme(cardScheme) != null) {
                creditCardSchemeSet.add(getCardScheme(cardScheme));
            }
        }

        // Parse debit card scheme
        size = debitCardArray.size();
        for (int i = 0; i < size; i++) {
            JsonElement element = debitCardArray.get(i);
            String cardScheme = element.getAsString();

            if (debitCardSchemeSet == null) {
                debitCardSchemeSet = new HashSet<>();
            }

            if (getCardScheme(cardScheme) != null) {
                debitCardSchemeSet.add(getCardScheme(cardScheme));
            }
        }

        // Parse netbanking options
        size = bankArray.size();
        for (int i = 0; i < size; i++) {
            JsonElement element = bankArray.get(i);
            if (element.isJsonObject()) {
                JsonObject bankOption = element.getAsJsonObject();

                element = bankOption.get("bankName");
                String bankName = element.getAsString();

                element = bankOption.get("issuerCode");
                String issuerCode = element.getAsString();

                if (!TextUtils.isEmpty(bankName) && !TextUtils.isEmpty(issuerCode)) {
                    NetbankingOption netbankingOption = new NetbankingOption(bankName, issuerCode);

                    if (pgHealthMap != null) {
                        netbankingOption.setPgHealth(pgHealthMap.get(issuerCode));
                    }

                    if (netbankingOptionList == null) {
                        netbankingOptionList = new ArrayList<>();
                    }

                    netbankingOptionList.add(netbankingOption);
                }
            }
        }

        merchantPaymentOption = new MerchantPaymentOption(creditCardSchemeSet, debitCardSchemeSet, netbankingOptionList);

        return merchantPaymentOption;
    }

    private static CardScheme getCardScheme(String cardScheme) {
        if ("Visa".equalsIgnoreCase(cardScheme)) {
            return CardScheme.VISA;
        } else if ("Master Card".equalsIgnoreCase(cardScheme)) {
            return CardScheme.MASTER_CARD;
        } else if ("Amex".equalsIgnoreCase(cardScheme)) {
            return CardScheme.AMEX;
        } else if ("Maestro Card".equalsIgnoreCase(cardScheme)) {
            return CardScheme.MAESTRO;
        } else if ("Diners".equalsIgnoreCase(cardScheme)) {
            return CardScheme.DINERS;
        } else if ("Discover".equalsIgnoreCase(cardScheme)) {
            return CardScheme.DISCOVER;
        } else if ("Jcb".equalsIgnoreCase(cardScheme)) {
            return CardScheme.JCB;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "MerchantPaymentOption{" +
                "creditCardSchemeSet=" + creditCardSchemeSet +
                ", debitCardSchemeSet=" + debitCardSchemeSet +
                ", netbankingOptionList=" + netbankingOptionList +
                '}';
    }
}
