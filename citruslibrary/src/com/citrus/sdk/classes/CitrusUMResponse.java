package com.citrus.sdk.classes;

/**
 * Created by MANGESH KADAM on 6/23/2015.
 */


import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CitrusUMResponse {

    private String responseCode;
    private String responseMessage;
    private ResponseData responseData;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The responseCode
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * @param responseCode The responseCode
     */
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * @return The responseMessage
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * @param responseMessage The responseMessage
     */
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    /**
     * @return The responseData
     */
    public ResponseData getResponseData() {
        return responseData;
    }

    /**
     * @param responseData The responseData
     */
    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public JSONObject getJSON() {
        Gson gson = new Gson();
        String json = gson.toJson(this);

        try {
            return new JSONObject(json);
        } catch (JSONException var4) {
            return null;
        }
    }

}
