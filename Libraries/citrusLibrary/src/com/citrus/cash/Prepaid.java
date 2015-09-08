package com.citrus.cash;

import android.app.Activity;
import android.os.AsyncTask;

import com.citrus.mobile.Callback;
import com.citrus.mobile.Config;
import com.citrus.mobile.OauthToken;
import com.citrus.mobile.RESTclient;
import com.citrus.mobile.User;
import com.citrus.sdk.payment.PaymentType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.CitrusClient#payUsingCitrusCash(PaymentType.CitrusCash, com.citrus.sdk.Callback)} instead.
 */
@Deprecated
public class Prepaid {

    private String emailId, base_url;

    private Callback callback;

    private Activity activity;

    private JSONObject response;

    public Prepaid(String email) {
        this.emailId = email;
        this.base_url = Config.getEnv();
    }

    public String getUserEmail() {
        return emailId;
    }

    public void getBalance(Activity activity, Callback callback) {
        this.activity = activity;

        this.callback = callback;

        new GetBalance().execute();

    }

    private class GetBalance extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            OauthToken token = new OauthToken(activity, User.SIGNIN_TOKEN);
            String access_token = null;

            try {
                JSONObject jsontoken = token.getuserToken();
                if (jsontoken != null) {
                    access_token = token.getuserToken().getString("access_token");
                } else {
                    return "User token not found -  Bind user first!";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return "Bind Token not found - Bind user first";
            }

            JSONObject headers = new JSONObject();

            try {
                headers.put("Authorization", "Bearer " + access_token);
            } catch (JSONException e) {
                e.printStackTrace();
                return "Bind Token not found - Bind user first";
            }

            RESTclient resTclient = new RESTclient("newbalance", base_url, null, headers);


            try {
                response = resTclient.makePostrequest();
            } catch (IOException e) {
                e.printStackTrace();
                return "Could not get user balance";
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            callback.onTaskexecuted(result, "");
        }

    }
}
