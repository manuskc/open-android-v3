package com.citrus.asynch;


import android.app.Activity;
import android.os.AsyncTask;

import com.citrus.mobile.Callback;
import com.citrus.mobile.Config;
import com.citrus.mobile.OauthToken;
import com.citrus.mobile.RESTclient;
import com.citrus.mobile.User;
import com.citrus.sdk.classes.CashoutInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.CitrusClient#cashout(CashoutInfo, com.citrus.sdk.Callback)} for all the APIs.
 */
@Deprecated
public class CashOutAsynch extends AsyncTask<Void, Void, JSONObject> {
    private double mAmount;
    private Activity mContext;
    private String owner, mAccountNumber, mIfscCode;
    private Callback mCallback = null;

    public CashOutAsynch(Activity mContext, double amount, String owner, String accountNumber, String ifscCode, Callback callback) {
        this.mContext = mContext;
        this.mCallback = callback;
        this.mAmount = amount;
        this.owner = owner;
        this.mAccountNumber = accountNumber;
        this.mIfscCode = ifscCode;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {

        JSONObject txnDetails = null;
        OauthToken token = new OauthToken(mContext, User.PREPAID_TOKEN);
        String accessToken = null;

        try {
            accessToken = token.getuserToken().getString("access_token");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RESTclient resTclient = new RESTclient("cashout", Config.getEnv(), null, null);
        txnDetails = resTclient.makeWithdrawRequest(accessToken, mAmount, "INR", owner, mAccountNumber, mIfscCode);
        return txnDetails;
    }

    @Override
    protected void onPostExecute(JSONObject txnDetails) {
        if (txnDetails != null) {
            mCallback.onTaskexecuted(txnDetails.toString(), null);
        } else {
            mCallback.onTaskexecuted(null, "Error occurred while withdrawing money");
        }

    }
}
