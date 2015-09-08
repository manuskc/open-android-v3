package com.citrus.asynch;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.citrus.mobile.Callback;
import com.citrus.mobile.Errorclass;
import com.citrus.mobile.RESTclient;

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.CitrusClient#getMerchantPaymentOptions(com.citrus.sdk.Callback)} instead.
 */
@Deprecated
public class PaymentOptions extends AsyncTask<Void, Void, JSONObject> {

    Callback callback;

    String vanity;

    public PaymentOptions(Callback callback, String vanity) {
        this.callback = callback;
        this.vanity = vanity;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {

        JSONObject header, param, response;

        header = new JSONObject();

        try {
            header.put("Content-Type", "application/x-www-form-urlencoded");
        } catch (JSONException e) {
            return Errorclass.addErrorFlag("Could not add headers", null);
        }

        param = new JSONObject();
        try {
            param.put("vanity", vanity);
        } catch (JSONException e) {
            return Errorclass.addErrorFlag("Could not add vanity url", null);
        }

        RESTclient restClient = new RESTclient("paymentoptions", com.citrus.mobile.Config.getEnv(), param, header);

        try {
            response = restClient.makePostrequest();
        } catch (IOException e) {
            e.printStackTrace();
            return Errorclass.addErrorFlag("Check your internet connection!", null);
        }

        return response;

    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);

        if (result.has("error")) {
            callback.onTaskexecuted("", result.toString());
        } else {
            callback.onTaskexecuted(result.toString(), "");
        }

    }
}
