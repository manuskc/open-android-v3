/*
 *
 *    Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 */
package com.citrus.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.citrus.retrofit.RetroFitClient;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.Constants;
import com.citrus.sdk.Environment;
import com.citrus.sdk.ResponseMessages;
import com.citrus.sdk.classes.AccessToken;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OauthToken {
    private static final String STORED_VALUES = "UserStorage";

    //emailID and MobileNo is saved after SignIn token is received
    public static final String EMAIL_ID = "email_id";

    public static final String MOBILE_NO = "mobile_no";

    private static final String ENVIRONMENT = "environment";

    private Context context;

    private Activity activity;

    private JSONObject jsontoken;

    private SharedPreferences tokenPrefs;

    private String base_url, token_type;

    private com.citrus.sdk.Callback callback;
    private CitrusClient citrusClient;

    public OauthToken(Activity activity, String token_type) {
        this.activity = activity;
        tokenPrefs = this.activity.getSharedPreferences(STORED_VALUES, 0);
        base_url = Config.getEnv();
        this.token_type = token_type;
    }


    public OauthToken(Context context, String token_type) {
        this.context = context;
        tokenPrefs = this.context.getSharedPreferences(STORED_VALUES, 0);
        base_url = Config.getEnv();
        this.token_type = token_type;
    }

    public OauthToken(Context context) {
        this.context = context;
        tokenPrefs = this.context.getSharedPreferences(STORED_VALUES, 0);
        base_url = Config.getEnv();
    }

//    public OauthToken(Context context, com.citrus.sdk.Callback callback, String token_type) {
//        this.context = context;
//        tokenPrefs = this.context.getSharedPreferences(STORED_VALUES, 0);
//        base_url = Config.getEnv();
//    }

    public boolean createToken(JSONObject usertoken) {

        jsontoken = new JSONObject();

        long expiry = new Date().getTime() / 1000l;

        try {
            expiry += usertoken.getLong("expires_in");
            jsontoken.put("expiry", expiry);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (Iterator<?> keys = usertoken.keys(); keys.hasNext(); ) {
            String key = (String) keys.next();

            try {
                jsontoken.put(key, usertoken.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return storeToken();

    }

    private boolean storeToken() {
        SharedPreferences.Editor editor = tokenPrefs.edit();
        editor.putString(token_type, jsontoken.toString());
        return editor.commit();
    }

    public JSONObject getuserToken() {
        JSONObject token = null;
        try {
            if (tokenPrefs.contains(token_type)) {
                token = new JSONObject(tokenPrefs.getString(token_type, null));
            } else {
                return null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        if (token.has("refresh_token")) {
            return refreshToken(token);
        } else {
            return token;
        }

    }

    private JSONObject refreshToken(JSONObject token) {

        if (hasExpired(token)) {
            try {
                return refresh(token.getString("refresh_token"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return token;

    }

    private boolean hasExpired(JSONObject token) {
        try {
            return token.getLong("expiry") <= (new Date().getTime() / 1000l);
        } catch (JSONException e) {
            return true;
        }
    }

    private JSONObject refresh(String refreshToken) {
        JSONObject response = new JSONObject();

        JSONObject userJson = new JSONObject();

        try {
//            userJson.put("client_id", Config.getSigninId());
//            userJson.put("client_secret", Config.getSigninSecret());

            citrusClient = CitrusClient.getInstance(context);

            userJson.put("client_id", citrusClient.getSigninId());
            userJson.put("client_secret", citrusClient.getSigninSecret());
            userJson.put("grant_type", "refresh_token");

            userJson.put("refresh_token", refreshToken);

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
            jsontoken = response;
            storeToken();
            return response;
        } else {
            return null;
        }
    }


    public void getSignUpToken(com.citrus.sdk.Callback<AccessToken> callback) {
        this.token_type = Constants.SIGNUP_TOKEN;
        getAccessToken(callback);
    }

    public void getSignInToken(com.citrus.sdk.Callback<AccessToken> callback) {
        this.token_type = Constants.SIGNIN_TOKEN;
        getAccessToken(callback);
    }


    public void getPrepaidToken(com.citrus.sdk.Callback<AccessToken> callback) {
        this.token_type = Constants.PREPAID_TOKEN;
        getAccessToken(callback);
    }

    public void getPayUsingCitrusCashToken(com.citrus.sdk.Callback<AccessToken> callback) {
        this.token_type = Constants.PAY_USING_CITRUS_CASH_TOKEN;
        getAccessToken(callback);
    }

    private void getAccessToken(com.citrus.sdk.Callback<AccessToken> callback) {

        JSONObject token = null;
        try {
            if (tokenPrefs.contains(token_type)) {
                token = new JSONObject(tokenPrefs.getString(token_type, null)); //read token from shared preferences
                if (token.has("refresh_token")) { //check if token contains refresh token element
                    refreshToken(token, callback);
                } else { //return AccessToken Object
                    Gson gson = new GsonBuilder().create();
                    AccessToken accessToken = gson.fromJson(token.toString(), AccessToken.class);
                    callback.success(accessToken);
                }
            } else {
                String errorMessage = token_type.equalsIgnoreCase(Constants.SIGNUP_TOKEN)? ResponseMessages.ERROR_SIGNUP_TOKEN_NOT_FOUND:ResponseMessages.ERROR_SIGNIN_TOKEN_NOT_FOUND;
                CitrusError error = new CitrusError(errorMessage, CitrusResponse.Status.FAILED);//token not found in shared preferences!!!
                callback.error(error);
            }

        } catch (JSONException e) {
            CitrusError error = new CitrusError("Failed to get Access Token", CitrusResponse.Status.FAILED);
            callback.error(error);
        }


    }


    private void refreshToken(JSONObject token, com.citrus.sdk.Callback callback) {

        if (hasExpired(token)) {
            try {
                getRefreshToken(token.getString("refresh_token"), callback);
            } catch (JSONException e) {
                CitrusError error = new CitrusError("Failed to get Access Token", CitrusResponse.Status.FAILED);
                callback.error(error);
            }
        }
        else {
            Gson gson = new GsonBuilder().create();
            AccessToken accessToken = gson.fromJson(token.toString(), AccessToken.class);
            callback.success(accessToken);
        }

    }


    //get Refresh TOken from RetroFitClient
    private void getRefreshToken(String refreshToken, final com.citrus.sdk.Callback callback) {
        citrusClient = CitrusClient.getInstance(context);

        RetroFitClient.getCitrusRetroFitClient().getRefreshTokenAsync(citrusClient.getSigninId(), citrusClient.getSigninSecret(), OAuth2GrantType.refresh_token.toString(), refreshToken, new Callback<AccessToken>() {
//        RetroFitClient.getCitrusRetroFitClient().getRefreshTokenAsync(Config.getSigninId(), Config.getSigninSecret(), OAuth2GrantType.refresh_token.toString(), refreshToken, new Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken, Response response) {
                if (accessToken.getAccessToken() != null) {
                    jsontoken = accessToken.getJSON();
                    storeToken();
                    callback.success(accessToken);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                CitrusError citrusError = new CitrusError(error.getMessage(), CitrusResponse.Status.FAILED);
                callback.error(citrusError);
            }
        });

    }

    public boolean clearToken() {
        SharedPreferences.Editor editor = tokenPrefs.edit();
        editor.clear();
        return editor.commit();
    }


    public boolean saveUserDetails(String emailId, String mobileNo) {

        SharedPreferences.Editor editor = tokenPrefs.edit();
        editor.putString(EMAIL_ID, emailId);
        editor.putString(MOBILE_NO, mobileNo);
        return editor.commit();
    }


    public String getEmailId() {
        return tokenPrefs.getString(EMAIL_ID, null);
    }

    public String getMobileNumber() {
        return tokenPrefs.getString(MOBILE_NO, null);
    }

    public boolean saveEnvironment(Environment environment) {
        SharedPreferences.Editor editor = tokenPrefs.edit();
        editor.putString(ENVIRONMENT, environment.toString());
        return editor.commit();
    }

    public Environment getCurrentEnvironment() {
        String environment = tokenPrefs.getString(ENVIRONMENT, null);
        return (environment==null)?Environment.NONE:Environment.valueOf(environment);
    }
}
