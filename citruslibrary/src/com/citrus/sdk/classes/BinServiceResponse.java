package com.citrus.sdk.classes;

import android.text.TextUtils;

import com.citrus.sdk.otp.NetBankForOTP;
import com.citrus.sdk.payment.CardOption;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by salil on 20/10/15.
 */
public class BinServiceResponse {

    private CardOption.CardScheme cardScheme = null;
    private NetBankForOTP netBankForOTP = null;

    public BinServiceResponse(CardOption.CardScheme cardScheme, NetBankForOTP netBankForOTP) {
        this.cardScheme = cardScheme;
        this.netBankForOTP = netBankForOTP;
    }

    /**
     * Parse the json and get the binserviceresponse.
     *
     * @return
     */
    public static BinServiceResponse fromJSON(String json) {
        BinServiceResponse response = null;
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                String scheme = jsonObject.optString("cardscheme");
                String cardtype = jsonObject.optString("cardtype");
                CardOption.CardScheme cardScheme = CardOption.CardScheme.getCardScheme(scheme);
                String issuingbank = jsonObject.optString("issuingbank");
                NetBankForOTP netBankForOTP = NetBankForOTP.getNetBankForOTP(cardtype, issuingbank);

                response = new BinServiceResponse(cardScheme, netBankForOTP);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    public CardOption.CardScheme getCardScheme() {
        return cardScheme;
    }

    public NetBankForOTP getNetBankForOTP() {
        return netBankForOTP;
    }
}
