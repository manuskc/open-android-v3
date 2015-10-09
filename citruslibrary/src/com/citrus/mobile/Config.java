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

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.CitrusClient} instead.
 */
@Deprecated
public class Config {
    private static String env, signinId, signinSecret, signupId, signupSecret, prepaid_cookie = "", vanity;


    private static final String GA_SANDBOX_ID = "UA-33514461-4";

    private static final String GA_PRODUCTION_ID = "UA-33514461-5";

    public static void setEnv(String sip) {
        env = sip;
    }

    public static void setSigninId(String id) {
        signinId = id;
    }

    public static void setSigninSecret(String secret) {
        signinSecret = secret;
    }

    public static void setupSignupId(String id) {
        signupId = id;
    }

    public static void setupSignupSecret(String secret) {
        signupSecret = secret;
    }

    public static void setupPrepaidCookie(String cookie) {
        prepaid_cookie = cookie;
    }

    public static String getEnv() {
        return env;
    }

    public static String getSigninId() {
        return signinId;
    }

    public static String getSigninSecret() {
        return signinSecret;
    }

    public static String getSignupId() {
        return signupId;
    }

    public static String getSignupSecret() {
        return signupSecret;
    }

    public static String getPrepaidCookie() {
        return prepaid_cookie;
    }

    public static String getVanity() {
        return vanity;
    }

    public static void setVanity(String vanity) {
        Config.vanity = vanity;
    }


    public static String getAnalyticsID() {
        if (env.equalsIgnoreCase("sandbox"))
            return GA_SANDBOX_ID;
        else
            return GA_PRODUCTION_ID;
    }

    public static String getBaseURL() {
        if ("sandbox".equalsIgnoreCase(env))
            return "https://sandboxadmin.citruspay.com";
        else if ("staging".equalsIgnoreCase(env))
            return "https://stg1admin.citruspay.com";
        else
            return "https://admin.citruspay.com";
    }
}