package com.citrus.asynch;


import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.citrus.mobile.Callback;
import com.citrus.mobile.Config;
import com.citrus.mobile.OauthToken;
import com.citrus.mobile.RESTclient;
import com.citrus.mobile.User;
import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.classes.Amount;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.CitrusClient#sendMoney(Amount, CitrusUser, String, com.citrus.sdk.Callback)} instead.
 */
@Deprecated
public class SendMoneyAsync extends AsyncTask<Void, Void, JSONObject>{
    private Amount mAmount;
    private Activity mContext;
    private CitrusUser toUser;
    private Callback mCallback = null;
    private String message;
    private String error = null;

    public SendMoneyAsync(Activity mContext, Amount amount, CitrusUser toUser, String message, Callback callback) {
        this.mContext = mContext;
        this.mCallback = callback;
        this.mAmount = amount;
        this.toUser = toUser;
        this.message = message;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {

        JSONObject txnDetails = null;
        OauthToken token = new OauthToken(mContext, User.PREPAID_TOKEN);
        String accessToken = null;

        try {
            if (token != null && token.getuserToken() != null) {
                accessToken = token.getuserToken().getString("access_token");
            } else {
                error = "Please login the user.";
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        long validMobileNumber = -1;
        if (!TextUtils.isEmpty(toUser.getMobileNo())) {
            validMobileNumber = com.citrus.card.TextUtils.isValidMobileNumber(toUser.getMobileNo());

            if (validMobileNumber != -1) {
                toUser.setMobileNo(validMobileNumber + "");
            } else {
                error = "Please enter correct Mobile No.";
                return null;
            }
        }

        RESTclient resTclient = new RESTclient(null, Config.getEnv(), null, null);
        txnDetails = resTclient.makeSendMoneyRequest(accessToken, toUser, mAmount, message);

        if (txnDetails != null) {
            return txnDetails;
        } else {
            error = "Error Occurred while sending money.";
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject txnDetails) {
        if (txnDetails != null) {
            mCallback.onTaskexecuted(txnDetails.toString(), null);
        } else {
            mCallback.onTaskexecuted(null, error);
        }

    }
}
