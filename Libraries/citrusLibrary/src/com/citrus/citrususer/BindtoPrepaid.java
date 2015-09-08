package com.citrus.citrususer;

import android.app.Activity;

import com.citrus.mobile.Config;
import com.citrus.mobile.Errorclass;
import com.citrus.mobile.OauthToken;
import com.citrus.mobile.RESTclient;
import com.citrus.mobile.User;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @deprecated in v3
 * <p/>
 * This class is no longer supported.
 */
@Deprecated
public class BindtoPrepaid {
    Activity activity;

    String email, mobile, password;

    PrepaidOauth oauth;

    public BindtoPrepaid(Activity activity, String email, String mobile, String password) {
        this.activity = activity;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        oauth = new PrepaidOauth(activity, email, password);
    }

    public JSONObject setPassword() {
        JSONObject headers = new JSONObject();
        JSONObject userJson = new JSONObject();
        JSONObject response = new JSONObject();

        try {
            OauthToken token = new OauthToken(activity, User.SIGNIN_TOKEN);
            JSONObject jsontoken = token.getuserToken();
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            if (jsontoken != null) {
                headers.put("Authorization", "Bearer " + jsontoken.getString("access_token"));
            } else {
                return Errorclass.addErrorFlag("Signin Token not found - Link user first", null);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RandomPassword pwd = new RandomPassword();

        String random_pass = pwd.generate(this.email, this.mobile);

        try {
            userJson.put("old", random_pass);

            userJson.put("new", password);

            RESTclient restclient = new RESTclient("password", Config.getEnv(), userJson, headers);

            response = restclient.makePutrequest();

            if (response == null) {
                return createprepaidAccount();
            } else {
                return Errorclass.addErrorFlag("", response);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Errorclass.addErrorFlag("could not update password", null);
    }

    private JSONObject createprepaidAccount() {
        JSONObject result = oauth.create();

        if (result.has("status")) {
            return getbalance();
        }

        return result;
    }

    private JSONObject getbalance() {
        JSONObject result = oauth.getbalance();

        if (result.has("status")) {
            return getPrepaidCookie();
        }

        return result;
    }

    private JSONObject getPrepaidCookie() {
        JSONObject result = oauth.getsetCookie();
        return result;
    }


}
