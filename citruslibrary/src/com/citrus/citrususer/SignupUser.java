package com.citrus.citrususer;

import android.app.Activity;

import com.citrus.mobile.Config;
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
 * Use {@link com.citrus.sdk.CitrusClient#signUp(String, String, String, Callback)} instead.
 */
@Deprecated
public class SignupUser {

    Activity activity;

    private String email, mobile, base_url, random_pass;

    JSONObject signuptokenJson, signintokenJson;

    public SignupUser(Activity activity) {
        this.activity = activity;
    }

    public JSONObject register(String email, String mobile) {

        this.email = email;

        this.mobile = mobile;

        this.base_url = Config.getEnv();

        return binduser();
    }

    private JSONObject binduser() {
        User user = new User(activity);

        JSONObject result = user.binduser(this.email, this.mobile);

        if (result.has("status")) {
            return signinRandomPassword();
        }

        return result;
    }

    private JSONObject signinRandomPassword() {
        JSONObject response = new JSONObject();

        JSONObject userJson = new JSONObject();

        RandomPassword pwd = new RandomPassword();

        random_pass = pwd.generate(this.email, this.mobile);

        try {
            userJson.put("client_id", Config.getSigninId());

            userJson.put("client_secret", Config.getSigninSecret());

            userJson.put("grant_type", "password");

            userJson.put("username", email);

            userJson.put("password", random_pass);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject headers = new JSONObject();

        try {
            headers.put("Content-Type", "application/x-www-form-urlencoded");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RESTclient restclient = new RESTclient("signin", base_url, userJson, headers);

        try {
            response = restclient.makePostrequest();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.has("access_token")) {
            return SuccessCall.successMessage("set user password", null);
        } else {
            return SuccessCall.successMessage("password is set already", null);
        }

    }

}
