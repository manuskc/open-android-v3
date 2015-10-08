package com.citrus.asynch;

import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;

import com.citrus.mobile.Callback;
import com.citrus.mobile.OauthToken;
import com.citrus.mobile.User;

/**
 * @deprecated in v3
 * <p/>
 * This class is not longer supported.
 */
@Deprecated
public class GetPrepaidtoken extends AsyncTask<Void, Void, JSONObject> {

    Activity activity;

    Callback callback;

    public GetPrepaidtoken(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {

        OauthToken token = new OauthToken(activity, User.PREPAID_TOKEN);

        JSONObject tokenjson = token.getuserToken();

        return tokenjson;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);

        if (result == null) {
            callback.onTaskexecuted("", "Prepaid Token Not Found - did you sign in the user?");
        } else {
            callback.onTaskexecuted(result.toString(), "");
        }
    }
}
