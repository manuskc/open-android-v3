package com.citrus.sdk.classes;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by salil on 14/7/15.
 */
public class AccountProfile {

    private final String emailId;
    private final boolean emailVerified;
    private final String mobile;
    private final boolean mobileVerified;
    private final String firstName;
    private final String lastName;

    public AccountProfile(String emailId, boolean emailVerified, String mobile, boolean mobileVerified, String firstName, String lastName) {
        this.emailId = emailId;
        this.emailVerified = emailVerified;
        this.mobile = mobile;
        this.mobileVerified = mobileVerified;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public String getMobile() {
        return mobile;
    }

    public boolean isMobileVerified() {
        return mobileVerified;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public static AccountProfile fromJSON(String jsonString) {
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                return fromJSONObject(new JSONObject(jsonString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static AccountProfile fromJSONObject(JSONObject jsonObject) {
        AccountProfile accountProfile = null;

        if (jsonObject != null && jsonObject.length() != 0) {
            String emailId = jsonObject.optString("email");
            int emailVerifiedInt = jsonObject.optInt("emailVerified", 0);
            String mobileNo = jsonObject.optString("mobile");
            int mobileVerifiedInt = jsonObject.optInt("mobileVerified", 0);
            String firstName = jsonObject.optString("firstName");
            String lastName = jsonObject.optString("lastName");
            boolean emailVerified = emailVerifiedInt == 1;
            boolean mobileVerified = mobileVerifiedInt == 1;

            accountProfile = new AccountProfile(emailId, emailVerified, mobileNo, mobileVerified, firstName, lastName);
        }

        return accountProfile;
    }

    @Override
    public String toString() {
        return "AccountProfile{" +
                "emailId='" + emailId + '\'' +
                ", emailVerified=" + emailVerified +
                ", mobile='" + mobile + '\'' +
                ", mobileVerified=" + mobileVerified +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
