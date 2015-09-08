package com.citrus.asynch;

import android.app.Activity;
import android.os.AsyncTask;

import com.citrus.mobile.Callback;
import com.citrus.mobile.OauthToken;
import com.citrus.mobile.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.CitrusClient#isUserSignedIn(com.citrus.sdk.Callback)} instead.
 */
@Deprecated
public class WalletStatus extends AsyncTask<Void, Void, JSONObject> {

    Callback callback;

    Activity activity;

    boolean status = true;

    public WalletStatus(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {

        OauthToken prepaid_token = new OauthToken(activity, User.PREPAID_TOKEN);

        JSONObject prepaid_tokenjson = prepaid_token.getuserToken();

        JSONObject result = new JSONObject();

        if (prepaid_tokenjson == null)
            status = false;


        try {

            result.put("status", "200");
            result.put("userloggedin", status);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        callback.onTaskexecuted(result.toString(), "");

    }
}
