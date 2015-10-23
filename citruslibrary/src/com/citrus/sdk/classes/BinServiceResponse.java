package com.citrus.sdk.classes;

import com.citrus.sdk.payment.CardOption;

/**
 * Created by salil on 20/10/15.
 */
public class BinServiceResponse {

    private String cardtype = null;
    private CardOption.CardScheme cardScheme = null;
    private String country = null;
    private String issuingbank = null;

    public BinServiceResponse(String cardtype, CardOption.CardScheme cardScheme, String country, String issuingbank) {
        this.cardtype = cardtype;
        this.cardScheme = cardScheme;
        this.country = country;
        this.issuingbank = issuingbank;
    }


    public String getCardtype() {
        return cardtype;
    }

    public CardOption.CardScheme getCardScheme() {
        return cardScheme;
    }

    public String getCountry() {
        return country;
    }

    public String getIssuingbank() {
        return issuingbank;
    }
}
