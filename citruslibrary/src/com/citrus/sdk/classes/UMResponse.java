package com.citrus.sdk.classes;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by salil on 16/7/15.
 */
public abstract class UMResponse {

    /* The response will be in following format.
        {
        "responseCode": "R-100-0",
        "responseMessage": "Message",
        "responseData": { }
        }
     *
     */
    protected final String responseCode;
    protected final String responseMessage;
    protected final int ERROR_CODE_SOME_ERROR_OCCURRED = -1;

    protected final String ERROR_MESSAGE_SOME_ERROR_OCCURRED = "Some Error Occurred";

    public UMResponse(String responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public abstract int getResponseCode();

    public String getResponseMessage() {
        return responseMessage;
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
