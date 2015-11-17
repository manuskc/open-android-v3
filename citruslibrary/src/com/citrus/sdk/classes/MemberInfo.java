package com.citrus.sdk.classes;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by salil on 14/7/15.
 */
public class MemberInfo {

    private final AccountProfile profileByEmail;
    private final AccountProfile profileByMobile;

    public String getResponseCode() {
        return responseCode;
    }

    private String responseCode;
    public MemberInfo(AccountProfile profileByEmail, AccountProfile profileByMobile) {
        this.profileByEmail = profileByEmail;
        this.profileByMobile = profileByMobile;
    }

    public MemberInfo(AccountProfile profileByEmail, AccountProfile profileByMobile, String responseCode) {
        this.profileByEmail = profileByEmail;
        this.profileByMobile = profileByMobile;
        this.responseCode = responseCode;
    }
    public AccountProfile getProfileByEmail() {
        return profileByEmail;
    }

    public AccountProfile getProfileByMobile() {
        return profileByMobile;
    }

    public static MemberInfo fromJSON(String jsonString) {
        MemberInfo memberInfo = null;

        if (!TextUtils.isEmpty(jsonString)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject responseData = jsonObject.getJSONObject("responseData");
                AccountProfile profileByEmail = AccountProfile.fromJSONObject(responseData.optJSONObject("profileByEmail"));
                AccountProfile profileByMobile = AccountProfile.fromJSONObject(responseData.optJSONObject("profileByMobile"));
                String responseCode = jsonObject.getString("responseCode");

                memberInfo = new MemberInfo(profileByEmail, profileByMobile, responseCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return memberInfo;
    }

    @Override
    public String toString() {
        return "MemberInfo{" +
                "profileByEmail=" + profileByEmail +
                ", profileByMobile=" + profileByMobile +
                '}';
    }
}
