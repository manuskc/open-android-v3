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

package com.citrus.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.citrus.analytics.EventsManager;
import com.citrus.cash.PersistentConfig;
import com.citrus.citrususer.RandomPassword;
import com.citrus.mobile.Config;
import com.citrus.mobile.OAuth2GrantType;
import com.citrus.mobile.OauthToken;
import com.citrus.mobile.User;
import com.citrus.retrofit.API;
import com.citrus.retrofit.RetroFitClient;
import com.citrus.sdk.classes.AccessToken;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.BindPOJO;
import com.citrus.sdk.classes.CashoutInfo;
import com.citrus.sdk.classes.MemberInfo;
import com.citrus.sdk.classes.PGHealth;
import com.citrus.sdk.classes.PGHealthResponse;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.CreditCardOption;
import com.citrus.sdk.payment.DebitCardOption;
import com.citrus.sdk.payment.MerchantPaymentOption;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.payment.PaymentBill;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.BindUserResponse;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusLogger;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.sdk.response.PaymentResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import de.greenrobot.event.EventBus;
import eventbus.CookieEvents;
import retrofit.ResponseCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedString;

import static com.citrus.sdk.response.CitrusResponse.Status;

/**
 * Created by salil on 11/5/15.
 */
public class CitrusClient {

    private static final String SIGNIN_TOKEN = "signin_token";
    private static final String SIGNUP_TOKEN = "signup_token";
    private static final String PREPAID_TOKEN = "prepaid_token";


    private String signinId;
    private String signinSecret;
    private String signupId;
    private String signupSecret;
    private String vanity;

    private String merchantName;
    private Environment environment = Environment.SANDBOX;
    private Amount balanceAmount;
    private static CitrusClient instance;
    private final Context mContext;
    private SharedPreferences mSharedPreferences;
    private MerchantPaymentOption merchantPaymentOption = null;

    private API retrofitClient;
    private API citrusBaseUrlClient;
    private String prepaidCookie = null;
    private OauthToken oauthToken = null;
    private CookieManager cookieManager;
    private BroadcastReceiver paymentEventReceiver = null;
    private Map<String, PGHealth> pgHealthMap = null;
    private boolean initialized = false;
    private CitrusUser citrusUser = null;
    private boolean showDummyScreen = false;

    private CitrusClient(Context context) {
        mContext = context;

        initRetrofitClient();
        oauthToken = new OauthToken(context);
    }

    public void enableLog(boolean enable) {
        if (enable) {
            CitrusLogger.enableLogs();
        } else {
            CitrusLogger.disableLogs();
        }
    }

    public void showDummyScreenWhilePayments(boolean showDummyScreen) {
        this.showDummyScreen = showDummyScreen;
    }

    public boolean isShowDummyScreenWhilePayments() {
        return showDummyScreen;
    }

    public void init(@NonNull String signupId, @NonNull String signupSecret, @NonNull String signinId, @NonNull String signinSecret, @NonNull String vanity, @NonNull Environment environment) {
        if (!initialized) {

            this.signupId = signupId;
            this.signupSecret = signupSecret;
            this.signinId = signinId;
            this.signinSecret = signinSecret;
            this.vanity = vanity;

            if (!CitrusLogger.isEnableLogs()) {
                CitrusLogger.disableLogs();
            }

            if (environment == null) {
                this.environment = Environment.SANDBOX;
            }
            this.environment = environment;
            saveSDKEnvironment();

            if (validate()) {
                initRetrofitClient();
                initCitrusBaseUrlClient();
            }

            // TODO: Remove full dependency on this class.
            Config.setupSignupId(signupId);
            Config.setupSignupSecret(signupSecret);

            Config.setSigninId(signinId);
            Config.setSigninSecret(signinSecret);
            Config.setVanity(vanity);
            switch (environment) {
                case SANDBOX:
                    Config.setEnv("sandbox");
                    break;
                case PRODUCTION:
                    Config.setEnv("production");
                    break;
            }
            Logger.d("VANITY*** " + vanity);
            EventsManager.logInitSDKEvents(mContext);

            fetchPGHealthForAllBanks();

            getMerchantPaymentOptions(null);

            // Fetch profile info if the user is signed in.
            // If not signed in the information will be fetched once the user signs in.
            isUserSignedIn(new Callback<Boolean>() {
                @Override
                public void success(Boolean signedIn) {
                    if (signedIn) {
                        getProfileInfo(null);
                    }
                }

                @Override
                public void error(CitrusError error) {
                    // Not required to handle the error.
                }
            });

            initialized = true;
        }
    }

    private void fetchPGHealthForAllBanks() {

        citrusBaseUrlClient.getPGHealthForAllBanks(vanity, "ALLBANKS", new retrofit.Callback<JsonElement>() {
                    @Override
                    public void success(JsonElement jsonElement, Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(jsonElement.toString());
                            Iterator<String> keys = jsonObject.keys();
                            while (keys.hasNext()) {
                                if (pgHealthMap == null) {
                                    pgHealthMap = new HashMap<String, PGHealth>();
                                }
                                String key = keys.next();
                                String health = jsonObject.optString(key);

                                pgHealthMap.put(key, PGHealth.getPGHealth(health));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // Logger.e("Error while fetching the health");
                    }
                }
        );

    }

    private void saveSDKEnvironment() {
        if (oauthToken.getCurrentEnvironment() == Environment.NONE) { //no environment saved till now
            oauthToken.saveEnvironment(environment);
            Logger.d("NO ENVIRONMENT EXISTS earlier");
        } else if (oauthToken.getCurrentEnvironment() == environment) {
            //dont save new enviroment
            Logger.d("PREVIOUS AND CURRENT ENVIRONMENT IS SAME");
        } else { //environment changed-  logout user, save new environment
            signOut(new Callback<CitrusResponse>() {
                @Override
                public void success(CitrusResponse citrusResponse) {
                    oauthToken.saveEnvironment(environment);
                    Logger.d("ENVIRONMMENT MISMATCH ***" + "user Logging out");
                }

                @Override
                public void error(CitrusError error) {
                    oauthToken.saveEnvironment(environment);
                }
            });
        }
    }

    private void initRetrofitClient() {
        RetroFitClient.initRetroFitClient(environment);
        retrofitClient = RetroFitClient.getCitrusRetroFitClient();
    }

    private void initCitrusBaseUrlClient() {
        citrusBaseUrlClient = RetroFitClient.getCitrusBaseUrlClient(environment.getBaseCitrusUrl());
    }

    public static CitrusClient getInstance(Context context) {
        if (instance == null) {
            synchronized (CitrusClient.class) {
                if (instance == null) {
                    instance = new CitrusClient(context);
                }
            }
        }

        return instance;
    }

    // Public APIS start

    /**
     * This api will check whether the user is existing user or not. If the user is existing user,
     * then it will return the existing details, else it will create an account internally and
     * then call signUp to set the password and activate the account.
     *
     * @param emailId  - emailId of the user
     * @param mobileNo - mobileNo of the user
     * @param callback - callback
     */
    public synchronized void isCitrusMember(final String emailId, final String mobileNo, final Callback<Boolean> callback) {

        bindUser(emailId, mobileNo, new Callback<String>() {
            @Override
            public void success(String s) {
                if (ResponseMessages.SUCCESS_MESSAGE_USER_BIND.equalsIgnoreCase(s)) { //bind Successful
                    RandomPassword pwd = new RandomPassword();

                    String random_pass = pwd.generate(emailId, mobileNo);

                    retrofitClient.getSignInWithPasswordResponse(signinId, signinSecret, emailId, random_pass, OAuth2GrantType.password.toString(), new retrofit.Callback<AccessToken>() {
                        @Override
                        public void success(AccessToken accessToken, Response response) {
                            Logger.d("User Not A Citrus Member. Please Sign Up User.");
                            sendResponse(callback, false);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Logger.d("User Already A Citrus Member. Please Sign In User.");
                            sendResponse(callback, true);
                        }
                    });
                } else {
                    sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_BIND_USER, Status.FAILED));
                }
            }

            @Override
            public void error(CitrusError error) {
                sendError(callback, error);
            }
        });
    }

    public synchronized void getMemberInfo(final String emailId, final String mobileNo, final Callback<MemberInfo> callback) {
        if (validate()) {
            retrofitClient.getSignUpToken(signupId, signupSecret, OAuth2GrantType.implicit.toString(), new retrofit.Callback<AccessToken>() {
                @Override
                public void success(AccessToken accessToken, Response response) {
                    if (accessToken != null && accessToken.getHeaderAccessToken() != null) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("email", emailId);
                            jsonObject.put("mobile", mobileNo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        retrofitClient.getMemberInfo(accessToken.getHeaderAccessToken(), new TypedString(jsonObject.toString()), new retrofit.Callback<JsonElement>() {
                            @Override
                            public void success(JsonElement jsonElement, Response response) {
                                MemberInfo memberInfo = MemberInfo.fromJSON(jsonElement.toString());

                                if (memberInfo != null) {
                                    sendResponse(callback, memberInfo);
                                } else {
                                    sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_MEMBER_INFO, Status.FAILED));
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                sendError(callback, error);
                            }
                        });
                    } else {
                        sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_SIGNUP_TOKEN, Status.FAILED));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    sendError(callback, error);
                }
            });
        }
    }

    /**
     * This method can be used by non prepaid merchants - where email and mobile is enough for save/get Cards.
     * This method will create Citrus Account of the user  with email. All the cards will be saved to emailID.
     *
     * @param emailId  - emailId of the user
     * @param mobileNo - mobileNo of the user
     * @param callback - callback
     */
    public synchronized void bindUser(final String emailId, final String mobileNo, final Callback<String> callback) {
        if (validate()) {

            retrofitClient.getSignUpToken(signupId, signupSecret, OAuth2GrantType.implicit.toString(), new retrofit.Callback<AccessToken>() {
                @Override
                public void success(AccessToken accessToken, Response response) {
                    Logger.d("accessToken " + accessToken.getJSON().toString());

                    if (accessToken.getHeaderAccessToken() != null) {
                        OauthToken signuptoken = new OauthToken(mContext, SIGNUP_TOKEN);
                        signuptoken.createToken(accessToken.getJSON()); //Oauth Token received

                        retrofitClient.getBindResponse(accessToken.getHeaderAccessToken(), emailId, mobileNo, new retrofit.Callback<BindPOJO>() {
                            @Override
                            public void success(BindPOJO bindPOJO, Response response) {
                                Logger.d("BIND RESPONSE " + bindPOJO.getUsername());

                                retrofitClient.getSignInToken(signinId, signinSecret, emailId, OAuth2GrantType.username.toString(), new retrofit.Callback<AccessToken>() {
                                    @Override
                                    public void success(AccessToken accessToken, Response response) {
                                        Logger.d("SIGNIN accessToken" + accessToken.getJSON().toString());
                                        if (accessToken.getHeaderAccessToken() != null) {
                                            OauthToken token = new OauthToken(mContext, SIGNIN_TOKEN);
                                            token.createToken(accessToken.getJSON());
                                            token.saveUserDetails(emailId, mobileNo);//save email and mobile No of the user
                                            Logger.d("USER BIND SUCCESSFULLY***");
                                            callback.success(ResponseMessages.SUCCESS_MESSAGE_USER_BIND);
                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        sendError(callback, error);
                                    }
                                });
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                sendError(callback, error);
                            }
                        });
                    } else {
                        sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_LINK_USER, Status.FAILED));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    sendError(callback, error);
                }
            });
        }
    }

    public synchronized void bindUserByMobile(final String emailId, final String mobileNo, final Callback<BindUserResponse> callback) {
        if (validate()) {
            getMemberInfo(emailId, mobileNo, new Callback<MemberInfo>() {
                @Override
                public void success(final MemberInfo memberInfo) {
                    // No need to check for not null, since if null the callback will be in error.
                    retrofitClient.getSignUpToken(signupId, signupSecret,
                            OAuth2GrantType.implicit.toString(), new retrofit.Callback<AccessToken>() {
                                @Override
                                public void success(AccessToken accessToken, Response response) {
                                    Logger.d("accessToken " + accessToken.getJSON().toString());

                                    if (accessToken.getHeaderAccessToken() != null) {
                                        OauthToken signuptoken = new OauthToken(mContext, SIGNUP_TOKEN);
                                        signuptoken.createToken(accessToken.getJSON()); //Oauth Token received

                                        retrofitClient.bindUserByMobile(accessToken.getHeaderAccessToken(), emailId, mobileNo, new retrofit.Callback<BindPOJO>() {
                                            @Override
                                            public void success(final BindPOJO bindPOJO, Response response) {
                                                Logger.d("BIND BY MOBILE RESPONSE " + bindPOJO.getUsername());

                                                final BindUserResponse bindUserResponse;

                                                // If the user is fresh user then send the password reset link.
                                                if (memberInfo.getProfileByMobile() == null && memberInfo.getProfileByEmail() == null) {
                                                    bindUserResponse = new BindUserResponse(BindUserResponse.RESPONSE_CODE_NEW_USER_BOUND);

                                                    resetPassword(emailId, new Callback<CitrusResponse>() {
                                                        @Override
                                                        public void success(CitrusResponse citrusResponse) {
                                                        }

                                                        @Override
                                                        public void error(CitrusError error) {
                                                        }
                                                    });
                                                } else {
                                                    bindUserResponse = new BindUserResponse(BindUserResponse.RESPONSE_CODE_EXISTING_USER_BOUND);
                                                }

                                                retrofitClient.getSignInToken(signinId, signinSecret, bindPOJO.getUsername(), OAuth2GrantType.username.toString(), new retrofit.Callback<AccessToken>() {
                                                    @Override
                                                    public void success(AccessToken accessToken, Response response) {
                                                        Logger.d("SIGNIN accessToken" + accessToken.getJSON().toString());
                                                        if (accessToken.getHeaderAccessToken() != null) {
                                                            OauthToken token = new OauthToken(mContext, SIGNIN_TOKEN);
                                                            token.createToken(accessToken.getJSON());
                                                            token.saveUserDetails(emailId, mobileNo);//save email and mobile No of the user
                                                            Logger.d("USER BIND BY MOBILE SUCCESSFULLY***");

                                                            sendResponse(callback, bindUserResponse);
                                                        }
                                                    }

                                                    @Override
                                                    public void failure(RetrofitError error) {
                                                        sendError(callback, error);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void failure(RetrofitError error) {
                                                sendError(callback, error);
                                            }
                                        });
                                    } else {
                                        sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_SIGNUP_TOKEN, Status.FAILED));
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    sendError(callback, error);
                                }
                            });
                }

                @Override
                public void error(CitrusError error) {
                    sendError(callback, error);
                }
            });
        }
    }

    /**
     * @param emailId
     * @param password
     * @param callback
     */
    public synchronized void signIn(final String emailId, final String password, final Callback<CitrusResponse> callback) {

        //grant Type username token saved
        retrofitClient.getSignInToken(signinId, signinSecret, emailId, OAuth2GrantType.username.toString(), new retrofit.Callback<AccessToken>() {

            @Override
            public void success(AccessToken accessToken, Response response) {
                if (accessToken.getHeaderAccessToken() != null) {
                    OauthToken token = new OauthToken(mContext, SIGNIN_TOKEN);
                    token.createToken(accessToken.getJSON());///grant Type username token saved

                    retrofitClient.getSignInWithPasswordResponse(signinId, signinSecret, emailId, password, OAuth2GrantType.password.toString(), new retrofit.Callback<AccessToken>() {
                        @Override
                        public void success(AccessToken accessToken, Response response) {
                            Logger.d("SIGN IN RESPONSE " + accessToken.getJSON().toString());
                            if (accessToken.getHeaderAccessToken() != null) {
                                // Fetch the profileInfo
                                getProfileInfo(null);

                                OauthToken token = new OauthToken(mContext, PREPAID_TOKEN);
                                token.createToken(accessToken.getJSON());///grant Type password token saved
                                token.saveUserDetails(emailId, null);//save email ID of the signed in user

                                // Activate the user's prepaid account, if not already.
                                activatePrepaidUser(new Callback<Amount>() {
                                    @Override
                                    public void success(Amount amount) {
                                        RetroFitClient.setInterCeptor();
                                        EventBus.getDefault().register(CitrusClient.this);

                                        retrofitClient.getCookie(emailId, password, "true", new retrofit.Callback<String>() {
                                            @Override
                                            public void success(String s, Response response) {
                                                // NOOP
                                                // This method will never be called.
                                            }

                                            @Override
                                            public void failure(RetrofitError error) {
                                                if (prepaidCookie != null) {
                                                    cookieManager = CookieManager.getInstance();
                                                    PersistentConfig config = new PersistentConfig(mContext);
                                                    if (config.getCookieString() != null) {
                                                        cookieManager.getInstance().removeSessionCookie();
                                                    }
                                                    CookieSyncManager.createInstance(mContext);
                                                    config.setCookie(prepaidCookie);
                                                } else {
                                                    Logger.d("PREPAID LOGIN UNSUCCESSFUL");
                                                }
                                                EventBus.getDefault().unregister(CitrusClient.this);

                                                // Since we have a got the cookie, we are giving the callback.
                                                sendResponse(callback, new CitrusResponse(ResponseMessages.SUCCESS_MESSAGE_SIGNIN, Status.SUCCESSFUL));
                                            }
                                        });
                                    }

                                    @Override
                                    public void error(CitrusError error) {
                                        sendError(callback, error);
                                    }
                                });
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Logger.d("SIGN IN RESPONSE ERROR **" + error.getMessage());
                            sendError(callback, error);
                        }
                    });

                }
            }

            @Override
            public void failure(RetrofitError error) {
                sendError(callback, error);
            }
        });
    }

    /**
     * Signin with mobile no and password.
     *
     * @param mobileNo
     * @param password
     * @param callback
     */
    public synchronized void signInWithMobileNo(final String mobileNo, final String password, final Callback<CitrusResponse> callback) {

        //grant Type username token saved
        retrofitClient.getSignInToken(signinId, signinSecret, mobileNo, OAuth2GrantType.username.toString(), new retrofit.Callback<AccessToken>() {

            @Override
            public void success(AccessToken accessToken, Response response) {
                if (accessToken.getHeaderAccessToken() != null) {
                    OauthToken token = new OauthToken(mContext, SIGNIN_TOKEN);
                    token.createToken(accessToken.getJSON());///grant Type username token saved

                    retrofitClient.getSignInWithPasswordResponse(signinId, signinSecret, mobileNo, password, OAuth2GrantType.password.toString(), new retrofit.Callback<AccessToken>() {
                        @Override
                        public void success(AccessToken accessToken, Response response) {
                            // Fetch the profileInfo
                            getProfileInfo(null);

                            Logger.d("SIGN IN RESPONSE " + accessToken.getJSON().toString());
                            if (accessToken.getHeaderAccessToken() != null) {
                                final OauthToken token = new OauthToken(mContext, PREPAID_TOKEN);
                                token.createToken(accessToken.getJSON());///grant Type password token saved

                                // Fetch the associated emailId and save the emailId.
                                // This is async since we are just updating the details and not dependent upon the response.
                                getMemberInfo(null, mobileNo, new Callback<MemberInfo>() {
                                    @Override
                                    public void success(MemberInfo memberInfo) {
                                        if (memberInfo != null && memberInfo.getProfileByMobile() != null) {

                                            token.saveUserDetails(memberInfo.getProfileByMobile().getEmailId(), mobileNo);//save email ID of the signed in user
                                        }
                                    }

                                    @Override
                                    public void error(CitrusError error) {
                                        // NOOP
                                        // Do nothing
                                    }
                                });

                                // Activate user's prepaid account, if not already.
                                activatePrepaidUser(new Callback<Amount>() {
                                    @Override
                                    public void success(Amount amount) {
                                        RetroFitClient.setInterCeptor();
                                        EventBus.getDefault().register(CitrusClient.this);
                                        retrofitClient.getCookie(mobileNo, password, "true", new retrofit.Callback<String>() {
                                            @Override
                                            public void success(String s, Response response) {
                                                // NOOP
                                                // This method will never be called.
                                            }

                                            @Override
                                            public void failure(RetrofitError error) {
                                                if (prepaidCookie != null) {
                                                    cookieManager = CookieManager.getInstance();
                                                    PersistentConfig config = new PersistentConfig(mContext);
                                                    if (config.getCookieString() != null) {
                                                        cookieManager.getInstance().removeSessionCookie();
                                                    }
                                                    CookieSyncManager.createInstance(mContext);
                                                    config.setCookie(prepaidCookie);
                                                } else {
                                                    Logger.d("PREPAID LOGIN UNSUCCESSFUL");
                                                }
                                                EventBus.getDefault().unregister(CitrusClient.this);

                                                // Since we have a got the cookie, we are giving the callback.
                                                sendResponse(callback, new CitrusResponse(ResponseMessages.SUCCESS_MESSAGE_SIGNIN, Status.SUCCESSFUL));
                                            }
                                        });
                                    }

                                    @Override
                                    public void error(CitrusError error) {
                                        sendError(callback, error);
                                    }
                                });
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Logger.d("SIGN IN RESPONSE ERROR **" + error.getMessage());
                            sendError(callback, error);
                        }
                    });

                }
            }

            @Override
            public void failure(RetrofitError error) {
                sendError(callback, error);
            }
        });
    }

    public synchronized void getCookie(String email, String password, final Callback<CitrusResponse> callback) {
        RetroFitClient.setInterCeptor();
        EventBus.getDefault().register(CitrusClient.this);
        retrofitClient.getCookie(email, password, "true", new retrofit.Callback<String>() {
            @Override
            public void success(String s, Response response) {
                // NOOP
                // This method will never be called.
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.getDefault().unregister(CitrusClient.this);

                if (error.getResponse().getStatus() == HttpStatus.SC_INTERNAL_SERVER_ERROR) { //Invalid Password for COOKIE
                    CitrusError citrusError = new CitrusError(ResponseMessages.ERROR_MESSAGE_INVALID_PASSWORD, Status.FAILED);
                    callback.error(citrusError);
                } else {
                    if (prepaidCookie != null) {
                        cookieManager = CookieManager.getInstance();
                        PersistentConfig config = new PersistentConfig(mContext);
                        if (config.getCookieString() != null) {
                            cookieManager.getInstance().removeSessionCookie();
                        }
                        CookieSyncManager.createInstance(mContext);
                        config.setCookie(prepaidCookie);
                    } else {
                        Logger.d("PREPAID LOGIN UNSUCCESSFUL");
                    }

                    // Since we have a got the cookie, we are giving the callback.
                    sendResponse(callback, new CitrusResponse(ResponseMessages.SUCCESS_COOKIE_SIGNIN, Status.SUCCESSFUL));
                }
            }
        });
    }

    /**
     * Signout the existing logged in user.
     */
    public synchronized void signOut(Callback<CitrusResponse> callback) {
        if (validate()) {
            if (User.logoutUser(mContext)) {
                CitrusResponse citrusResponse = new CitrusResponse("User Logged Out Successfully.", Status.SUCCESSFUL);
                sendResponse(callback, citrusResponse);
            } else {
                CitrusError citrusError = new CitrusError("Failed to logout.", Status.FAILED);
                callback.error(citrusError);
            }
        }
    }

    /**
     * Set the user password.
     *
     * @param emailId
     * @param mobileNo
     * @param password
     * @param callback
     */
    public synchronized void signUp(final String emailId, String mobileNo, final String password, final Callback<CitrusResponse> callback) {

        if (validate()) {
            OauthToken token = new OauthToken(mContext, SIGNIN_TOKEN);
            JSONObject jsontoken = token.getuserToken();
            try {
                String header = "Bearer " + jsontoken.getString("access_token");
                RandomPassword pwd = new RandomPassword();
                String random_pass = pwd.generate(emailId, mobileNo);
                retrofitClient.setPasswordResponse(header, random_pass, password, new retrofit.Callback<ResponseCallback>() {
                    @Override
                    public void success(ResponseCallback responseCallback, Response response) {
                        Logger.d("SIGNUP PASSWORD RESPONSE **" + String.valueOf(response.getStatus()));
                        signIn(emailId, password, new Callback<CitrusResponse>() {
                            @Override
                            public void success(CitrusResponse citrusResponse) {
                                activatePrepaidUser(new Callback<Amount>() {
                                    @Override
                                    public void success(Amount amount) {
                                        sendResponse(callback, new CitrusResponse(ResponseMessages.SUCCESS_MESSAGE_SET_PASSWORD, Status.SUCCESSFUL));
                                    }

                                    @Override
                                    public void error(CitrusError error) {
                                        sendError(callback, error);
                                    }
                                });
                            }

                            @Override
                            public void error(CitrusError error) {
                                sendError(callback, error);
                            }
                        });
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Logger.d("SIGNUP PASSWORD ERROR **" + error.getMessage());
                        sendError(callback, error);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reset the user password. The password reset link will be sent to the user.
     *
     * @param emailId
     * @param callback
     */
    public synchronized void resetPassword(final String emailId, @NonNull final Callback<CitrusResponse> callback) {

        oauthToken.getSignUpToken(new Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken) {
                if (accessToken != null) {

                    retrofitClient.resetPassword(accessToken.getHeaderAccessToken(), emailId, new retrofit.Callback<JsonElement>() {
                        @Override
                        public void success(JsonElement element, Response response) {
                            sendResponse(callback, new CitrusResponse(ResponseMessages.SUCCESS_MESSAGE_RESET_PASSWORD, Status.SUCCESSFUL));
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            sendError(callback, error);
                        }
                    });
                } else {
                    sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_RESET_PASSWORD, Status.FAILED));
                }
            }

            @Override
            public void error(CitrusError error) {
                sendError(callback, error);
            }
        });
    }

    /**
     * Get the user saved payment options.
     *
     * @param callback - callback
     */
    public synchronized void getWallet(final Callback<List<PaymentOption>> callback) {
        /*
         * Get the saved payment options of the user.
         */
        if (validate()) {

            oauthToken.getSignInToken(new Callback<AccessToken>() {
                @Override
                public void success(AccessToken accessToken) {

                    retrofitClient.getWallet(accessToken.getHeaderAccessToken(), new retrofit.Callback<JsonElement>() {
                        @Override
                        public void success(JsonElement element, Response response) {
                            if (element != null) {
                                ArrayList<PaymentOption> walletList = new ArrayList<>();
                                try {

                                    JSONObject jsonObject = new JSONObject(element.toString());
                                    JSONArray paymentOptions = jsonObject.optJSONArray("paymentOptions");

                                    if (paymentOptions != null) {
                                        for (int i = 0; i < paymentOptions.length(); i++) {
                                            PaymentOption option = PaymentOption.fromJSONObject(paymentOptions.getJSONObject(i));

                                            // Check whether the merchant supports the user's payment option and then only add this payment option.
                                            if (merchantPaymentOption != null) {
                                                Set<CardOption.CardScheme> creditCardSchemeSet = merchantPaymentOption.getCreditCardSchemeSet();
                                                Set<CardOption.CardScheme> debitCardSchemeSet = merchantPaymentOption.getDebitCardSchemeSet();
                                                List<NetbankingOption> netbankingOptionList = merchantPaymentOption.getNetbankingOptionList();

                                                if (option instanceof CreditCardOption && creditCardSchemeSet != null &&
                                                        creditCardSchemeSet.contains(((CreditCardOption) option).getCardScheme())) {
                                                    walletList.add(option);
                                                } else if (option instanceof DebitCardOption && debitCardSchemeSet != null &&
                                                        debitCardSchemeSet.contains(((DebitCardOption) option).getCardScheme())) {
                                                    walletList.add(option);
                                                } else if (option instanceof NetbankingOption && netbankingOptionList != null &&
                                                        netbankingOptionList.contains(option)) {
                                                    NetbankingOption netbankingOption = (NetbankingOption) option;

                                                    if (pgHealthMap != null) {
                                                        netbankingOption.setPgHealth(pgHealthMap.get(netbankingOption.getBankCID()));
                                                    }

                                                    walletList.add(netbankingOption);
                                                }
                                            } else {
                                                // If the merchant payment options are not found, save all the options.
                                                walletList.add(option);
                                            }
                                        }
                                    }

                                    sendResponse(callback, walletList);

                                } catch (JSONException e) {
                                    e.printStackTrace();

                                    sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INVALID_JSON, Status.FAILED));
                                }
                            } else {
                                sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INVALID_JSON, Status.FAILED));
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            sendError(callback, new CitrusError(error.getMessage(), Status.FAILED));
                        }
                    });
                }

                @Override
                public void error(CitrusError error) {
                    sendError(callback, error);
                }
            });
        }
    }

    /**
     * Activate the prepaid user.
     *
     * @param callback
     */
    private synchronized void activatePrepaidUser(final Callback<Amount> callback) {
        if (validate()) {
            oauthToken.getPrepaidToken(new Callback<AccessToken>() {
                @Override
                public void success(AccessToken accessToken) {

                    retrofitClient.activatePrepaidUser(accessToken.getHeaderAccessToken(), new retrofit.Callback<Amount>() {
                        @Override
                        public void success(Amount amount, Response response) {
                            sendResponse(callback, amount);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            sendError(callback, error);
                        }
                    });
                }

                @Override
                public void error(CitrusError error) {
                    sendError(callback, error);
                }
            });
        }
    }

    /**
     * @param callback
     */
    public synchronized void getProfileInfo(final Callback<CitrusUser> callback) {
        if (validate()) {
            if (citrusUser == null) {
                getPrepaidToken(new Callback<AccessToken>() {
                    @Override
                    public void success(AccessToken accessToken) {
                        retrofitClient.getProfileInfo(accessToken.getHeaderAccessToken(), new retrofit.Callback<JsonElement>() {
                            @Override
                            public void success(JsonElement jsonElement, Response response) {
                                String profileInfo = jsonElement.toString();
                                citrusUser = CitrusUser.fromJSON(profileInfo);

                                sendResponse(callback, citrusUser);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                sendError(callback, error);
                            }
                        });
                    }

                    @Override
                    public void error(CitrusError error) {
                        sendError(callback, error);
                    }
                });
            }
        }
    }

    /**
     * Get the balance of the user.
     *
     * @param callback
     */
    public synchronized void getBalance(final Callback<Amount> callback) {
        if (validate()) {
//            oauthToken.getSignInToken(new Callback<AccessToken>() {
//                @Override
//                public void success(AccessToken accessToken) {
//
//                    retrofitClient.getBalance(accessToken.getHeaderAccessToken(), new retrofit.Callback<Amount>() {
//                        @Override
//                        public void success(Amount amount, Response response) {
//                            sendResponse(callback, amount);
//                        }
//
//                        @Override
//                        public void failure(RetrofitError error) {
//                            sendError(callback, error);
//                        }
//                    });
//                }
//
//                @Override
//                public void error(CitrusError error) {
//                    sendError(callback, error);
//                }
//            });

            oauthToken.getSignInToken(new Callback<AccessToken>() {
                @Override
                public void success(AccessToken accessToken) {
                    new GetBalanceAsync(accessToken.getHeaderAccessToken(), new GetBalanceListener() {
                        @Override
                        public void success(Amount amount) {
                            sendResponse(callback, amount);
                        }

                        @Override
                        public void error(CitrusError error) {
                            sendError(callback, error);
                        }
                    }).execute();
                }

                @Override
                public void error(CitrusError error) {
                    sendError(callback, error);
                }
            });
        }
    }

    /**
     * Save the paymentOption.
     *
     * @param paymentOption - PaymentOption to be saved.
     * @param callback
     */

    public synchronized void savePaymentOption(final PaymentOption paymentOption, final Callback<CitrusResponse> callback) {
        if (validate()) {

            if (paymentOption != null) {

                // If the CardOption is invalid, check what is incorrect and respond with proper message.
                if (paymentOption instanceof CardOption && !((CardOption) paymentOption).validateForSaveCard()) {
                    StringBuilder builder = new StringBuilder();
                    if (!((CardOption) paymentOption).validateCardNumber()) {
                        builder.append(" Invalid Card Number. ");
                    }

                    if (!((CardOption) paymentOption).validateExpiryDate()) {
                        builder.append(" Invalid Expiry Date. ");
                    }

                    sendError(callback, new CitrusError(builder.toString(), Status.FAILED));
                    return;
                }

                oauthToken.getSignInToken(new Callback<AccessToken>() {
                    @Override
                    public void success(AccessToken accessToken) {
                        retrofitClient.savePaymentOption(accessToken.getHeaderAccessToken(), new TypedString(paymentOption.getSavePaymentOptionObject()), new retrofit.Callback<CitrusResponse>() {
                            @Override
                            public void success(CitrusResponse citrusResponse, Response response) {
                                sendResponse(callback, new CitrusResponse(ResponseMessages.SUCCESS_MESSAGE_SAVED_PAYMENT_OPTIONS, Status.SUCCESSFUL));
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                sendError(callback, error);
                            }
                        });
                    }

                    @Override
                    public void error(CitrusError error) {
                        sendError(callback, error);
                    }
                });
            } else {
                sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_NULL_PAYMENT_OPTION, Status.FAILED));
            }
        }
    }

    /**
     * Deletes the saved Payment Option
     *
     * @param paymentOption
     * @param callback
     */
    public synchronized void deletePaymentOption(final PaymentOption paymentOption, final Callback<CitrusResponse> callback) {
        if (validate()) {

            if (paymentOption != null) {
                oauthToken.getSignInToken(new Callback<AccessToken>() {
                    @Override
                    public void success(AccessToken accessToken) {
                        retrofit.Callback<CitrusResponse> deleteCallback = new retrofit.Callback<CitrusResponse>() {
                            @Override
                            public void success(CitrusResponse citrusResponse, Response response) {
                                sendResponse(callback, new CitrusResponse(ResponseMessages.SUCCESS_MESSAGE_DELETE_PAYMENT_OPTIONS, Status.SUCCESSFUL));
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                sendError(callback, error);
                            }
                        };


                        if (paymentOption instanceof NetbankingOption) {
                            retrofitClient.deleteBank(accessToken.getHeaderAccessToken(), paymentOption.getToken(), deleteCallback);

                        } else if (paymentOption instanceof CardOption) {
                            String last4Digits = ((CardOption) paymentOption).getLast4Digits();
                            CardOption.CardScheme cardScheme = ((CardOption) paymentOption).getCardScheme();
                            String scheme = null;
                            if (cardScheme != null) {
                                scheme = cardScheme.getName();
                            }

                            retrofitClient.deleteCard(accessToken.getHeaderAccessToken(), last4Digits, scheme, deleteCallback);
                        }
                    }

                    @Override
                    public void error(CitrusError error) {
                        sendError(callback, error);
                    }
                });
            } else {
                sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_NULL_PAYMENT_OPTION, Status.FAILED));
            }
        }
    }

    /**
     * Get the payment bill for the transaction.
     *
     * @param amount   - Transaction amount
     * @param callback
     */
    public synchronized void getBill(String billUrl, Amount amount, final Callback<PaymentBill> callback) {
        // Get the bill from the merchant server.

        new GetJSONBill(billUrl, amount, new retrofit.Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {
                Logger.d("GETBILL RESPONSE **" + jsonElement.toString());
                PaymentBill paymentBill = PaymentBill.fromJSON(jsonElement.toString());
                if (paymentBill != null) {
                    callback.success(paymentBill);
                } else {
                    sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INVALID_BILL, Status.FAILED));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                sendError(callback, error);

            }
        }).getJSONBill();

    }


    /**
     * Send money to your friend.
     *
     * @param amount   - Amount to be sent
     * @param toUser   - The user detalis. Enter emailId if send by email or mobileNo if send by mobile.
     * @param message  - Optional message
     * @param callback - Callback
     * @deprecated Use {@link CitrusClient#sendMoneyToMoblieNo(Amount, String, String, Callback)} instead.
     */
    public synchronized void sendMoney(final Amount amount, final CitrusUser toUser, final String message, final Callback<PaymentResponse> callback) {
        if (validate()) {

            if (amount == null || TextUtils.isEmpty(amount.getValue())) {
                sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_BLANK_AMOUNT, Status.FAILED));
                return;
            }

            if (toUser == null || (TextUtils.isEmpty(toUser.getEmailId()) && TextUtils.isEmpty(toUser.getMobileNo()))) {
                sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_BLANK_EMAIL_ID_MOBILE_NO, Status.FAILED));
                return;
            }

            final retrofit.Callback<PaymentResponse> callbackSendMoney = new retrofit.Callback<PaymentResponse>() {
                @Override
                public void success(PaymentResponse paymentResponse, Response response) {
                    sendResponse(callback, paymentResponse);
                }

                @Override
                public void failure(RetrofitError error) {
                    sendError(callback, error);
                }
            };

            getPrepaidToken(new Callback<AccessToken>() {
                @Override
                public void success(AccessToken accessToken) {
                    if (!TextUtils.isEmpty(toUser.getEmailId())) {
                        retrofitClient.sendMoneyByEmail(accessToken.getHeaderAccessToken(), amount.getValue(), amount.getCurrency(), message, toUser.getEmailId(), callbackSendMoney);
                    } else {
                        long mobileNo = com.citrus.card.TextUtils.isValidMobileNumber(toUser.getMobileNo());
                        if (mobileNo != -1) {
                            retrofitClient.sendMoneyByMobile(accessToken.getHeaderAccessToken(), amount.getValue(), amount.getCurrency(), message, String.valueOf(mobileNo), callbackSendMoney);
                        } else {
                            sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INVALID_MOBILE_NO, Status.FAILED));
                        }
                    }
                }

                @Override
                public void error(CitrusError error) {
                    sendError(callback, error);
                }
            });
        }
    }

    /**
     * @param amount
     * @param mobileNo
     * @param message
     * @param callback
     */
    public synchronized void sendMoneyToMoblieNo(final Amount amount, final String mobileNo, final String message, final Callback<PaymentResponse> callback) {
        if (validate()) {

            if (amount == null || TextUtils.isEmpty(amount.getValue())) {
                sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_BLANK_AMOUNT, Status.FAILED));
                return;
            }

            if (TextUtils.isEmpty(mobileNo)) {
                sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_BLANK_MOBILE_NO, Status.FAILED));
                return;
            }

            getPrepaidToken(new Callback<AccessToken>() {
                @Override
                public void success(AccessToken accessToken) {

                    long validMobileNo = com.citrus.card.TextUtils.isValidMobileNumber(mobileNo);
                    if (validMobileNo != -1) {
                        retrofitClient.sendMoneyByMobile(accessToken.getHeaderAccessToken(), amount.getValue(), amount.getCurrency(), message, String.valueOf(validMobileNo), new retrofit.Callback<PaymentResponse>() {
                            @Override
                            public void success(PaymentResponse paymentResponse, Response response) {
                                sendResponse(callback, paymentResponse);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                sendError(callback, error);
                            }
                        });
                    } else {
                        sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INVALID_MOBILE_NO, Status.FAILED));
                    }
                }

                @Override
                public void error(CitrusError error) {
                    sendError(callback, error);
                }
            });
        }
    }

    /**
     * Returns the access token of the currently logged in user.
     */
    public void getPrepaidToken(final Callback<AccessToken> callback) {

        oauthToken.getPrepaidToken(new Callback<com.citrus.sdk.classes.AccessToken>() {
            @Override
            public void success(AccessToken accessToken) {
                sendResponse(callback, accessToken);
            }

            @Override
            public void error(CitrusError error) {
                sendError(callback, error);
            }
        });
    }


    /**
     * Get the merchant available payment options. You need to show the user available payment option in your app.
     *
     * @param callback
     */
    public synchronized void getMerchantPaymentOptions(final Callback<MerchantPaymentOption> callback) {
        if (validate()) {

            if (merchantPaymentOption == null) {
                retrofitClient.getMerchantPaymentOptions(vanity, new retrofit.Callback<JsonElement>() {
                    @Override
                    public void success(JsonElement element, Response response) {

                        MerchantPaymentOption merchantPaymentOption;

                        if (element.isJsonObject()) {
                            JsonObject paymentOptionObj = element.getAsJsonObject();
                            if (paymentOptionObj != null) {
                                merchantPaymentOption = MerchantPaymentOption.getMerchantPaymentOptions(paymentOptionObj);

                                // Store merchant payment options locally.
                                setMerchantPaymentOption(merchantPaymentOption);

                                sendResponse(callback, merchantPaymentOption);

                            } else {
                                sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_FAILED_MERCHANT_PAYMENT_OPTIONS, Status.FAILED));
                            }
                        } else {
                            sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INVALID_JSON, Status.FAILED));
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        sendError(callback, error);
                    }
                });
            } else {
                sendResponse(callback, merchantPaymentOption);
            }
        }
    }


    /**
     * Get the payment options available for load money. You need to show the user available payment option in your app.
     *
     * @param callback
     */
    public synchronized void getLoadMoneyPaymentOptions(final Callback<MerchantPaymentOption> callback) {
        if (validate()) {
            retrofitClient.getMerchantPaymentOptions(Constants.PREPAID_VANITY, new retrofit.Callback<JsonElement>() {
                @Override
                public void success(JsonElement element, Response response) {

                    MerchantPaymentOption merchantPaymentOption;

                    if (element.isJsonObject()) {
                        JsonObject paymentOptionObj = element.getAsJsonObject();
                        if (paymentOptionObj != null) {
                            merchantPaymentOption = MerchantPaymentOption.getMerchantPaymentOptions(paymentOptionObj);

                            sendResponse(callback, merchantPaymentOption);

                        } else {
                            sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_FAILED_MERCHANT_PAYMENT_OPTIONS, Status.FAILED));
                        }
                    } else {
                        sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INVALID_JSON, Status.FAILED));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    sendError(callback, error);
                }
            });
        }
    }

    public synchronized void isUserSignedIn(final Callback<Boolean>
                                                    callback) {
        oauthToken.getPrepaidToken(new Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken) {
                callback.success(true);
            }

            @Override
            public void error(CitrusError error) {
                callback.success(false);
            }
        });
    }

    public synchronized void loadMoney(final PaymentType.LoadMoney loadMoney, final Callback<TransactionResponse> callback) {

        // Validate the card details before forwarding transaction.
        if (loadMoney != null) {
            PaymentOption paymentOption = loadMoney.getPaymentOption();
            // If the CardOption is invalid, check what is incorrect and respond with proper message.
            if (paymentOption instanceof CardOption && !((CardOption) paymentOption).validateCard()) {

                sendError(callback, new CitrusError(((CardOption) paymentOption).getCardValidityFailureReasons(), Status.FAILED));
                return;
            }
        }

        registerReceiver(callback, new IntentFilter(loadMoney.getIntentAction()));

        startCitrusActivity(loadMoney);
    }

    public synchronized void pgPayment(final PaymentType.PGPayment pgPayment, final Callback<TransactionResponse> callback) {

        // Validate the card details before forwarding transaction.
        if (pgPayment != null) {
            PaymentOption paymentOption = pgPayment.getPaymentOption();
            // If the CardOption is invalid, check what is incorrect and respond with proper message.
            if (paymentOption instanceof CardOption && !((CardOption) paymentOption).validateCard()) {

                sendError(callback, new CitrusError(((CardOption) paymentOption).getCardValidityFailureReasons(), Status.FAILED));
                return;
            }
        }

        registerReceiver(callback, new IntentFilter(pgPayment.getIntentAction()));

        startCitrusActivity(pgPayment);
    }

    public synchronized void payUsingCitrusCash(final PaymentType.CitrusCash citrusCash, final Callback<TransactionResponse> callback) {

        String cookieExpiryDate = "";
        PersistentConfig persistentConfig = new PersistentConfig(mContext);
        String sessionCookie = persistentConfig.getCookieString();
        // Extract the cookie expiry date
        int start = sessionCookie.indexOf("Expires=");
        int end = sessionCookie.indexOf("GMT;");
        if (start != -1 && end != -1 && sessionCookie.length() > start + 13 && sessionCookie.length() > end) {
            cookieExpiryDate = sessionCookie.substring(start + 13, end);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
        Date expiryDate = new Date();
        Date currentDate = new Date(System.currentTimeMillis());
        try {
            expiryDate = dateFormat.parse(cookieExpiryDate);

            Logger.d("Expiry date : %s, Current Date : %s", expiryDate, currentDate);

            if (currentDate.before(expiryDate)) {

                // Check whether the balance in the wallet is greater than the transaction amount.
                getBalance(new Callback<Amount>() {
                    @Override
                    public void success(Amount balanceAmount) {
                        // If the balance amount is greater than equal to the transaction amount, proceed with the payment.
                        if (balanceAmount.getValueAsDouble() >= citrusCash.getAmount().getValueAsDouble()) {
                            registerReceiver(callback, new IntentFilter(citrusCash.getIntentAction()));

                            startCitrusActivity(citrusCash);
                        } else {
                            sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INSUFFICIENT_BALANCE, Status.FAILED));
                        }
                    }

                    @Override
                    public void error(CitrusError error) {
                        sendError(callback, error);
                    }
                });
            } else {
                Logger.d("User's cookie has expired. Please signin");
                sendError(callback, new CitrusError("User's cookie has expired. Please signin.", Status.FAILED));
            }
        } catch (ParseException e) {
            e.printStackTrace();

            // In the worst case, it will try to redirect user to the Citrus Page.

            // Check whether the balance in the wallet is greater than the transaction amount.
            getBalance(new Callback<Amount>() {
                @Override
                public void success(Amount balanceAmount) {
                    // If the balance amount is greater than equal to the transaction amount, proceed with the payment.
                    if (balanceAmount.getValueAsDouble() >= citrusCash.getAmount().getValueAsDouble()) {
                        registerReceiver(callback, new IntentFilter(citrusCash.getIntentAction()));

                        startCitrusActivity(citrusCash);
                    } else {
                        sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INSUFFICIENT_BALANCE, Status.FAILED));
                    }
                }

                @Override
                public void error(CitrusError error) {
                    sendError(callback, error);
                }
            });
        }
    }

    // Cashout Related APIs
    public synchronized void cashout(@NonNull final CashoutInfo cashoutInfo, final Callback<PaymentResponse> callback) {

        if (cashoutInfo != null && cashoutInfo.validate()) {
            // Check whether the balance in the wallet is greater than the transaction amount.
            getBalance(new Callback<Amount>() {
                @Override
                public void success(Amount balanceAmount) {
                    // If the balance amount is greater than equal to the transaction amount, proceed with the payment.
                    if (balanceAmount.getValueAsDouble() >= cashoutInfo.getAmount().getValueAsDouble()) {
                        oauthToken.getPrepaidToken(new Callback<AccessToken>() {
                            @Override
                            public void success(AccessToken accessToken) {
                                // Since we have access Token, withdraw the money.
                                retrofitClient.cashout(accessToken.getHeaderAccessToken(), cashoutInfo.getAmount().getValue(), cashoutInfo.getAmount().getCurrency(), cashoutInfo.getAccountHolderName(), cashoutInfo.getAccountNo(), cashoutInfo.getIfscCode(), new retrofit.Callback<PaymentResponse>() {
                                    @Override
                                    public void success(PaymentResponse paymentResponse, Response response) {
                                        sendResponse(callback, paymentResponse);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        sendError(callback, error);
                                    }
                                });
                            }

                            @Override
                            public void error(CitrusError error) {
                                sendError(callback, error);
                            }
                        });
                    } else {
                        sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INSUFFICIENT_BALANCE, Status.FAILED));
                    }
                }

                @Override
                public void error(CitrusError error) {
                    sendError(callback, error);
                }
            });
        } else {
            sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INVALID_CASHOUT_INFO, Status.FAILED));
        }
    }

    public synchronized void getCashoutInfo(final Callback<CashoutInfo> callback) {
        oauthToken.getPrepaidToken(new Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken) {
                retrofitClient.getCashoutInfo(accessToken.getHeaderAccessToken(), new retrofit.Callback<JsonElement>() {
                    @Override
                    public void success(JsonElement jsonElement, Response response) {
                        CashoutInfo cashoutInfo = CashoutInfo.fromJSON(jsonElement.toString());

                        sendResponse(callback, cashoutInfo);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        sendError(callback, error);
                    }
                });
            }

            @Override
            public void error(CitrusError error) {
                sendError(callback, error);
            }
        });

    }

    public synchronized void saveCashoutInfo(final CashoutInfo cashoutInfo, final Callback<CitrusResponse> callback) {
        oauthToken.getPrepaidToken(new Callback<AccessToken>() {
            @Override
            public void success(AccessToken accessToken) {
                if (cashoutInfo != null) {
                    retrofitClient.saveCashoutInfo(accessToken.getHeaderAccessToken(), new TypedString(CashoutInfo.toJSON(cashoutInfo)), new retrofit.Callback<CitrusResponse>() {
                        @Override
                        public void success(CitrusResponse citrusResponse, Response response) {
                            sendResponse(callback, new CitrusResponse(ResponseMessages.SUCCESS_MESSAGE_SAVED_CASHOUT_OPTIONS, Status.SUCCESSFUL));
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            sendError(callback, error);
                        }
                    });
                } else {
                    sendError(callback, new CitrusError(ResponseMessages.ERROR_MESSAGE_INVALID_CASHOUT_INFO, Status.FAILED));
                }
            }

            @Override
            public void error(CitrusError error) {
                sendError(callback, error);
            }
        });

    }

    // PG Health.

    /**
     * It returns {@link com.citrus.sdk.classes.PGHealth} which denotes the health of the PG for in
     * If the health is bad merchants can warn user to use another payment method.
     *
     * @param paymentOption
     * @param callback
     */
    public synchronized void getPGHealth(PaymentOption paymentOption, final Callback<PGHealthResponse> callback) {

        // Currently PG health supports netbanking only. So in case of any other payment Options it will return GOOD by default.
        if (!(paymentOption instanceof NetbankingOption)) {
            sendResponse(callback, new PGHealthResponse(PGHealth.GOOD, "All Good"));
        } else {
            // If the paymentOption is netbanking call the api.
            citrusBaseUrlClient.getPGHealth(vanity, ((NetbankingOption) paymentOption).getBankCID(), new retrofit.Callback<PGHealthResponse>() {
                @Override
                public void success(PGHealthResponse pgHealthResponse, Response response) {
                    sendResponse(callback, pgHealthResponse);
                }

                @Override
                public void failure(RetrofitError error) {
                    sendError(callback, error);
                }
            });
        }

    }

    public synchronized String getUserEmailId() {
        return oauthToken.getEmailId();
    }


    public synchronized String getUserMobileNumber() {
        return oauthToken.getMobileNumber();
    }

    public synchronized CitrusUser getCitrusUser() {
        return citrusUser;
    }

    // Public APIS end

    private void unregisterReceiver(BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);
    }

    private void startCitrusActivity(PaymentType paymentType) {
        Intent intent = new Intent(mContext, CitrusActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.INTENT_EXTRA_PAYMENT_TYPE, paymentType);

        mContext.startActivity(intent);
    }

    private <T> void registerReceiver(final Callback<T> callback, IntentFilter intentFilter) {
        paymentEventReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unregisterReceiver(this);

                TransactionResponse transactionResponse = intent.getParcelableExtra(Constants.INTENT_EXTRA_TRANSACTION_RESPONSE);
                if (transactionResponse != null) {
                    TransactionResponse.TransactionStatus transactionStatus = transactionResponse.getTransactionStatus();
                    Status status = null;

                    if (transactionStatus != null) {
                        switch (transactionStatus) {

                            case SUCCESSFUL:
                                status = Status.SUCCESSFUL;
                                break;
                            case FAILED:
                                status = Status.FAILED;
                                break;
                            case CANCELLED:
                                status = Status.CANCELLED;
                                break;
                            case PG_REJECTED:
                                status = Status.PG_REJECTED;
                                break;
                        }
                    }
                    if (transactionStatus == TransactionResponse.TransactionStatus.SUCCESSFUL) {
                        sendResponse(callback, transactionResponse);
                    } else {
                        sendError(callback, new CitrusError(transactionResponse.getMessage(), status));
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver(paymentEventReceiver, intentFilter);
    }


    private synchronized boolean validate() {
        if (!TextUtils.isEmpty(signinId) && !TextUtils.isEmpty(signinSecret)
                && !TextUtils.isEmpty(signupId) && !TextUtils.isEmpty(signupSecret)
                && !TextUtils.isEmpty(vanity)) {
            return true;
        } else {
            throw new IllegalArgumentException(ResponseMessages.ERROR_MESSAGE_BLANK_CONFIG_PARAMS);
        }
    }

    private <T> void sendResponse(Callback callback, T t) {
        if (callback != null) {
            callback.success(t);
        }
    }

    private void sendError(Callback callback, CitrusError error) {
        if (callback != null) {
            callback.error(error);
        }
    }

    private void sendError(Callback callback, RetrofitError error) {
        if (callback != null) {
            String message = null;
            CitrusError citrusError;

            // Check whether the error is network error.
            if (error.getKind() == RetrofitError.Kind.NETWORK) {
                citrusError = new CitrusError(ResponseMessages.ERROR_NETWORK_CONNECTION, Status.FAILED);
            } else {
                if (error.getResponse() != null && error.getResponse().getBody() != null) {
                    message = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                }

                if (message != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        // If the response does not contain error_description then look for errorMessage.
                        String errorMessage = null;
                        if (!TextUtils.isEmpty(jsonObject.optString("error_description"))) {
                            errorMessage = jsonObject.optString("error_description");
                        } else if (!TextUtils.isEmpty(jsonObject.optString("errorMessage"))) {
                            errorMessage = jsonObject.optString("errorMessage");
                        } else {
                            errorMessage = message;
                        }

                        citrusError = new CitrusError(errorMessage, Status.FAILED);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        citrusError = new CitrusError(message, Status.FAILED);
                    }
                } else {
                    citrusError = new CitrusError(error.getMessage(), Status.FAILED);
                }
            }

            sendError(callback, citrusError);
        }
    }

    // Getters and setters.
    public String getSigninId() {
        return signinId;
    }

    public void setSigninId(String signinId) {
        this.signinId = signinId;
    }

    public String getSigninSecret() {
        return signinSecret;
    }

    public void setSigninSecret(String signinSecret) {
        this.signinSecret = signinSecret;
    }

    public String getSignupId() {
        return signupId;
    }

    public void setSignupId(String signupId) {
        this.signupId = signupId;
    }

    public String getSignupSecret() {
        return signupSecret;
    }

    public void setSignupSecret(String signupSecret) {
        this.signupSecret = signupSecret;
    }

    public String getVanity() {
        return vanity;
    }

    public void setVanity(String vanity) {
        this.vanity = vanity;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Amount getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(Amount balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public MerchantPaymentOption getMerchantPaymentOption() {
        return merchantPaymentOption;
    }

    public void setMerchantPaymentOption(MerchantPaymentOption merchantPaymentOption) {
        this.merchantPaymentOption = merchantPaymentOption;
    }


    //this event is triggered from ReceivedCookiesInterceptor
    public void onEvent(CookieEvents cookieEvents) {
        Logger.d("COOKIE IN CITRUS CLIENT  ****" + cookieEvents.getCookie());
        prepaidCookie = cookieEvents.getCookie();
    }

    private class GetBalanceAsync extends AsyncTask<String, Void, Amount> {
        private GetBalanceListener listener = null;
        private String accessToken = null;

        public GetBalanceAsync(String accessToken, GetBalanceListener listener) {
            this.accessToken = accessToken;
            this.listener = listener;
        }

        @Override
        protected Amount doInBackground(String... strings) {
            Amount amount = null;

            String getBalanceUrl = environment.getBaseUrl() + "/service/v2/mycard/balance";
            try {
                URL url = new URL(getBalanceUrl);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", accessToken);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the response and get the amount object.
                amount = Amount.fromJSON(response.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return amount;
        }

        @Override
        protected void onPostExecute(Amount amount) {
            super.onPostExecute(amount);

            if (amount != null) {
                listener.success(amount);
            } else {
                listener.error(new CitrusError(ResponseMessages.ERROR_FAILED_TO_GET_BALANCE, CitrusResponse.Status.FAILED));
            }
        }
    }

    private interface GetBalanceListener {
        void success(Amount amount);

        void error(CitrusError error);
    }
}
