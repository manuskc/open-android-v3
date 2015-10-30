/*
   Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.citrus.mobile;

import android.app.Activity;
import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.citrus.cash.PersistentConfig;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class User {

	public static final String SIGNIN_TOKEN = "signin_token";
	
	public static final String SIGNUP_TOKEN = "signup_token";
	
	public static final String PREPAID_TOKEN = "prepaid_token";
	
    private Activity activity;

    private String base_url;

    public User(Activity activity) {
        this.activity = activity;
        base_url = Config.getEnv();
    }

    // UNUSED
//    private JSONObject getSignupToken(final String email, final String mobile) {
//        JSONObject response = new JSONObject();
//
//
//        JSONObject userJson = new JSONObject();
//
//        try {
//            userJson.put("client_id", Config.getSignupId());
//
//            userJson.put("client_secret", Config.getSignupSecret());
//
//            userJson.put("grant_type", "implicit");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JSONObject headers = new JSONObject();
//
//        try {
//            headers.put("Content-Type", "application/x-www-form-urlencoded");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RESTclient restclient = new RESTclient("signup",base_url, userJson, headers);
//
//        try {
//            response = restclient.makePostrequest();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (response.has("access_token")) {
//        	OauthToken signuptoken = new OauthToken(activity, SIGNUP_TOKEN);
//        	signuptoken.createToken(response);
//            return signupuser(email, mobile, response);
//        }
//        else {
//            return response;
//        }
//
//    }

    // UNUSED
//    public JSONObject binduser(final String email, final String mobile) {
//        return getSignupToken(email, mobile);
//    }

    // UNUSED
//    private JSONObject signupuser(final String email, String mobile, JSONObject token) {
//
//        JSONObject signupJson = new JSONObject();
//
//        JSONObject response = new JSONObject();
//
//        try {
//            signupJson.put("email", email);
//
//            signupJson.put("mobile", mobile);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JSONObject headers = new JSONObject();
//
//        try {
//            headers.put("Content-Type", "application/x-www-form-urlencoded");
//            headers.put("Authorization", "Bearer " + token.getString("access_token"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RESTclient restclient = new RESTclient("bind", base_url, signupJson, headers);
//
//        try {
//            response = restclient.makePostrequest();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (response.has("username")) {
//            return signinUser(email);
//        }
//        else {
//            return response;
//        }
//    }

    // UNUSED
//    public JSONObject signinUser(String email) {
//        JSONObject response = new JSONObject();
//
//        JSONObject userJson = new JSONObject();
//
//        try {
//            userJson.put("client_id", Config.getSigninId());
//
//            userJson.put("client_secret", Config.getSigninSecret());
//
//            userJson.put("grant_type", "username");
//
//            userJson.put("username", email);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JSONObject headers = new JSONObject();
//
//        try {
//            headers.put("Content-Type", "application/x-www-form-urlencoded");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RESTclient restclient = new RESTclient("signin", base_url, userJson, headers);
//
//        try {
//            response = restclient.makePostrequest();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (response.has("access_token")) {
//            OauthToken token = new OauthToken(activity, SIGNIN_TOKEN);
//            token.createToken(response);
//
//            return SuccessCall.successMessage("user bound", null);
//        }
//        else {
//            return response;
//        }
//
//    }


    public final static boolean logoutUser(Activity activity) {

        CookieSyncManager.createInstance(activity);

        CookieManager.getInstance().setCookie(Config.getBaseURL(), Constants.CITRUS_PREPAID_COOKIE);//remove App Cookie

        new PersistentConfig(activity).clearToken(); //clear stored cookies

        OauthToken token = new OauthToken(activity, "");
        return token.clearToken(); //clear stored oauth token


    }
    public final static boolean logoutUser(Context context) {

        CookieSyncManager.createInstance(context);

        CitrusClient citrusClient = CitrusClient.getInstance(context);

        String setCookie = CookieManager.getInstance().getCookie(citrusClient.getEnvironment().getBaseUrl());
        CookieManager.getInstance().setCookie(citrusClient.getEnvironment().getBaseUrl(), Constants.CITRUS_PREPAID_COOKIE);//remove App Cookie

        new PersistentConfig(context).clearToken(); //clear stored cookies

        OauthToken token = new OauthToken(context, "");
        return token.clearToken(); //clear stored oauth token

    }


    // UNUSED
//    public final static boolean isUserLoggedIn(Activity activity) {
//        OauthToken token = new OauthToken(activity, PREPAID_TOKEN);
//        if(token.getuserToken() == null)
//            return false;
//        else
//            return true;
//    }

}