package com.citrus.sdk.classes;

import com.citrus.sdk.payment.CardOption;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mangesh.kadam on 8/27/2015.
 */
public class CardBinDetails {

    private String cardtype;
    private String cardscheme;
    private String country;
    private String issuingbank;

    /**
     *
     * @return
     * The cardtype
     */
    public CardOption.CardType getCardtype() {
        if("Debit".equalsIgnoreCase(cardtype))
            return CardOption.CardType.DEBIT;
        else
            return CardOption.CardType.CREDIT;
    }

    /**
     *
     * @param cardtype
     * The cardtype
     */
    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    /**
     *
     * @return
     * The cardscheme
     */
    public String getCardscheme() {
        return cardscheme;
    }

    /**
     *
     * @param cardscheme
     * The cardscheme
     */
    public void setCardscheme(String cardscheme) {
        this.cardscheme = cardscheme;
    }

    /**
     *
     * @return
     * The country
     */
    public String getCountry() {
        return country;
    }

    /**
     *
     * @param country
     * The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     *
     * @return
     * The issuingbank
     */
    public String getIssuingbank() {
        return issuingbank;
    }

    /**
     *
     * @param issuingbank
     * The issuingbank
     */
    public void setIssuingbank(String issuingbank) {
        this.issuingbank = issuingbank;
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

}
