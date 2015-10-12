package com.citrus.citrususer;

import android.app.Activity;

import com.citrus.mobile.Config;
import com.citrus.mobile.Errorclass;
import com.citrus.mobile.OauthToken;
import com.citrus.mobile.RESTclient;
import com.citrus.mobile.SuccessCall;
import com.citrus.mobile.User;
import com.citrus.sdk.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.CitrusClient#resetPassword(String, Callback)} instead.
 */
@Deprecated
public class ResetPassword {
    private Activity activity;

    private String emailid;

    public ResetPassword(Activity activity, String email) {
        this.activity = activity;
        this.emailid = email;
    }

    public JSONObject sendresetEmail() {
        OauthToken signuptoken = new OauthToken(activity, User.SIGNUP_TOKEN);

        JSONObject jsonToken = signuptoken.getuserToken();

        JSONObject response = null, params = null;

        if (jsonToken != null) {

            JSONObject headers = new JSONObject();

            try {
                headers.put("Authorization", "Bearer " + jsonToken.getString("access_token"));
            } catch (JSONException e) {
                e.printStackTrace();
                return Errorclass.addErrorFlag("Could not reset Password - signup token missing", response);
            }

            try {
                params = new JSONObject().put("username", emailid);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            RESTclient restClient = new RESTclient("resetpassword", Config.getEnv(), params, headers);

            try {
                response = restClient.makePostrequest();
            } catch (IOException e) {
                e.printStackTrace();
                return Errorclass.addErrorFlag("Check your internet connection", response);
            }

            if (response == null) {
                return SuccessCall.successMessage("Reset Password Email sent on the mail Id", response);
            } else {
                return Errorclass.addErrorFlag("", response);
            }

        } else {
            return Errorclass.addErrorFlag("User is not bound - bind the user first", null);
        }
    }
}
