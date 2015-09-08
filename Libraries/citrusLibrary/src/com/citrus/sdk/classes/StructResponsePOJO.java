package com.citrus.sdk.classes;

/**
 * Created by MANGESH KADAM on 5/7/2015.
 */

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;


public class StructResponsePOJO {

    @Expose
    private String redirectUrl;
    @Expose
    private String pgRespCode;
    @Expose
    private String txMsg;

    /**
     * @return The redirectUrl
     */
    public String getRedirectUrl() {
        return redirectUrl;
    }

    /**
     * @param redirectUrl The redirectUrl
     */
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    /**
     * @return The pgRespCode
     */
    public String getPgRespCode() {
        return pgRespCode;
    }

    /**
     * @param pgRespCode The pgRespCode
     */
    public void setPgRespCode(String pgRespCode) {
        this.pgRespCode = pgRespCode;
    }

    /**
     * @return The txMsg
     */
    public String getTxMsg() {
        return txMsg;
    }

    /**
     * @param txMsg The txMsg
     */
    public void setTxMsg(String txMsg) {
        this.txMsg = txMsg;
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
