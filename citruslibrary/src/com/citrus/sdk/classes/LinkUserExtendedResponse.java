package com.citrus.sdk.classes;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gautam on 17/11/15.
 */
public final class LinkUserExtendedResponse {

//      When mobile is verified, Password is NOT-System generated,
//      then m-OTP will be sent to mobile.
//            "responseCode": "R-211-01",
//            "responseMessage": "Sign in with m-OTP OR Password",
//
//      When mobile is verified, Password is System generated,
//      then m-OTP will be sent to mobile.
//            "responseCode": "R-211-02",
//            "responseMessage": "Sign-in with m-OTP",
//
//      When Mobile is NOT-Verified,
//      Email is NOT present into System,
//      Then Fresh Signup Flow.
//            "responseCode": "R-211-03",
//            "responseMessage": "Mobile verify code sent. Verify And Signin",
//
//      When Mobile m(i) is NOT-Verified,
//      Email e(i) is present into system,
//      mobile m(e) is NOT verified,
//      Password is system generated ,
//      Then send mobile verification code on m(e).
//            "responseCode": "R-211-04",
//            "responseMessage":"Mobile verify code sent. Verify And Signin",
//
//      When  Mobile m(i) is NOT-Verified,
//      Email e(i) is present into system,
//      mobile m(e) is NOT verified,
//      Password is NOT system generated ,
//      Then send mobile verification code on m(e).
//            "responseCode": "R-211-05",
//            "responseMessage":"Mobile verify code sent. Verify And Signin OR Password",
//
//      When Mobile m(i) is NOT-Verified,
//      Email e(i) is present into system,
//      mobile m(e) is verified,
//      Password is NOT system generated ,
//      Then send m-OTP on m(e).
//            "responseCode": "R-211-06",
//            "responseMessage":"Sign-in with m-OTP OR Password",
//
//      When  Mobile m(i) is NOT-Verified,
//      Email e(i) is present into system,
//      mobile m(e) is verified,
//      Password is system generated ,
//      Then send m-OTP on m(e).
//            "responseCode": "R-211-07",
//            "responseMessage":"Sign-in with m-OTP”,
//
//      When Mobile m(i) is NOT-Verified,
//      Email e(i) is present into system,
//      mobile m(e) is NOT verified,
//      Password is NOT system generated ,
//      Then send e-OTP on e(i).
//            "responseCode": "R-211-08",
//            "responseMessage":"Sign in with e-OTP OR Password”,
//
//      When  Mobile m(i) is NOT-Verified,
//      Email e(i) is present into system,
//      mobile m(e) is NOT verified,
//      Password is system generated ,
//      Then send e-OTP on e(i).
//            "responseCode": "R-211-09",
//            "responseMessage":"Sign in with e-OTP”,

//    public enum LinkUserSignInType {
//        SignInTypeMOtp,
//        SignInTypeEOtp,
//        SignInTypePassword,
//        SignInTypeMOtpOrPassword,
//        SignInTypeEOtpOrPassword,
//        None
//    }

    private LinkUserSignInType linkUserSignInType = null;
    private String linkUserMessage = null;
    private String responseCode = null;
    private String linkUserEmail = null;
    private String linkUserMobile = null;
    private int emailVerified = -1;
    private int emailVerifiedDate = -1;
    private int mobileVerified = -1;
    private int mobileVerifiedDate = -1;
    private String linkUserFirstName = null;
    private String linkUserLastName = null;
    private String linkUserUUID = null;
    private String requestedMobile = null;

    private static final String MESSAGE_LOGIN_MOTP_PASSWORD = "Please Sign in with OTP sent on above Mobile Number or by using your Citrus Password";
    private static final String MESSAGE_LOGIN_MVERIFICATION_CODE_PASSWORD = "Please Sign in with Verification Code sent on above Mobile Number or by using your Citrus Password";
    private static final String MESSAGE_LOGIN_EOTP_PASSWORD = "Please Sign in with OTP sent on above Email Id or by using your Citrus Password";
    private static final String MESSAGE_LOGIN_MOTP = "Please Sign in with OTP sent on above Mobile Number";
    private static final String MESSAGE_LOGIN_MVERIFICATION_CODE = "Please Sign in with Verification Code sent on above Mobile Number";
    private static final String MESSAGE_LOGIN_EOTP = "Please Sign in with OTP sent on above Email Id";
    private static final String MESSAGE_SOME_ERROR_OCCURRED = "Some Error Occurred";


    public LinkUserExtendedResponse(String responseCode, String responseMessage, JSONObject responseData) {
        this.responseCode = responseCode;
        this.linkUserMessage = responseMessage;
        parseResponseData(responseData);
    }

    private void parseResponseData(JSONObject responseData) {
        try {
            this.linkUserEmail = responseData.getString("email");

            if (!responseData.isNull("emailVerified"))
                this.emailVerified = responseData.getInt("emailVerified");
            if (!responseData.isNull("emailVerifiedDate"))
                this.emailVerifiedDate = responseData.getInt("emailVerifiedDate");
            if (!responseData.isNull("mobile"))
                this.linkUserMobile = responseData.getString("mobile");
            if (!responseData.isNull("mobileVerified"))
                this.mobileVerified = responseData.getInt("mobileVerified");
            if (!responseData.isNull("mobileVerifiedDate"))
                this.mobileVerifiedDate = responseData.getInt("mobileVerifiedDate");

            this.linkUserFirstName = responseData.getString("firstName");
            this.linkUserLastName = responseData.getString("lastName");
            this.linkUserUUID = responseData.getString("uuid");
            if (!responseData.isNull("requestedMobile") && responseData.has("requestedMobile")) {
                this.requestedMobile = responseData.getString("requestedMobile");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.d("Link User = " + toString());
    }

    public static LinkUserExtendedResponse fromJSON(String jsonString) {
        LinkUserExtendedResponse linkUserExtendedResponse = null;

        if (!TextUtils.isEmpty(jsonString)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject responseData = jsonObject.getJSONObject("responseData");
                String responseCode = jsonObject.getString("responseCode");
                String responseMessage = jsonObject.getString("responseMessage");

                linkUserExtendedResponse = new LinkUserExtendedResponse(responseCode, responseMessage, responseData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return linkUserExtendedResponse;
    }

    /**
     * The response is in following format.
     * {
     * "responseCode": "R-211-01",
     * "responseMessage": "Sign in with m-OTP OR Password",
     * }
     * The responseCode will either be R-211-01 or R-211-02 or R-211-03 or R-211-04 or R-211-05 or R-211-06 or R-211-07 or R-211-08 or R-211-09
     */
    public LinkUserSignInType getLinkUserSignInType() {

        if ("R-211-01".equals(responseCode)) {
            linkUserSignInType = LinkUserSignInType.SignInTypeMOtpOrPassword;
            linkUserMessage = MESSAGE_LOGIN_MOTP_PASSWORD;
        } else if ("R-211-02".equals(responseCode)) {
//            linkUserSignInType = LinkUserSignInType.SignInTypeMOtpOrPassword;
//            linkUserMessage = MESSAGE_LOGIN_MOTP_PASSWORD;
            linkUserSignInType = LinkUserSignInType.SignInTypeMOtp;
            linkUserMessage = MESSAGE_LOGIN_MOTP;
        } else if ("R-211-03".equals(responseCode)) {
            linkUserSignInType = LinkUserSignInType.SignInTypeMOtp;
//            linkUserMessage = MESSAGE_LOGIN_MOTP;
            linkUserMessage = MESSAGE_LOGIN_MVERIFICATION_CODE;
        } else if ("R-211-04".equals(responseCode)) {
            linkUserSignInType = LinkUserSignInType.SignInTypeMOtp;
//            linkUserMessage = MESSAGE_LOGIN_MOTP;
            linkUserMessage = MESSAGE_LOGIN_MVERIFICATION_CODE;
        } else if ("R-211-05".equals(responseCode)) {
            linkUserSignInType = LinkUserSignInType.SignInTypeMOtpOrPassword;
//            linkUserMessage = MESSAGE_LOGIN_MOTP_PASSWORD;
            linkUserMessage = MESSAGE_LOGIN_MVERIFICATION_CODE_PASSWORD;
        } else if ("R-211-06".equals(responseCode)) {
            linkUserSignInType = LinkUserSignInType.SignInTypeMOtpOrPassword;
            linkUserMessage = MESSAGE_LOGIN_MOTP_PASSWORD;
        } else if ("R-211-07".equals(responseCode)) {
            linkUserSignInType = LinkUserSignInType.SignInTypeMOtp;
            linkUserMessage = MESSAGE_LOGIN_MOTP;
//            linkUserSignInType = LinkUserSignInType.SignInTypeMOtpOrPassword;
//            linkUserMessage = MESSAGE_LOGIN_MOTP_PASSWORD;
        } else if ("R-211-08".equals(responseCode)) {
            linkUserSignInType = LinkUserSignInType.SignInTypeEOtpOrPassword;
            linkUserMessage = MESSAGE_LOGIN_EOTP_PASSWORD;
        } else if ("R-211-09".equals(responseCode)) {
            linkUserSignInType = LinkUserSignInType.SignInTypeEOtp;
            linkUserMessage = MESSAGE_LOGIN_EOTP;
        } else {
            linkUserSignInType = LinkUserSignInType.None;
            linkUserMessage = MESSAGE_SOME_ERROR_OCCURRED;
        }

        return linkUserSignInType;
    }

    public JSONObject getJSON() {
        final Gson gson = new Gson();
        String json = gson.toJson(this);
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            return null;
        }
    }

    public int formatResponseCode() {
        char code = this.responseCode.charAt(responseCode.length() - 1);
        return Integer.parseInt(String.valueOf(code));
    }

    public String getLinkUserMessage() {
        return linkUserMessage;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public int getEmailVerifiedDate() {
        return emailVerifiedDate;
    }

    public int getMobileVerified() {
        return mobileVerified;
    }

    public int getMobileVerifiedDate() {
        return mobileVerifiedDate;
    }

    public String getLinkUserFirstName() {
        return linkUserFirstName;
    }

    public String getLinkUserLastName() {
        return linkUserLastName;
    }

    public String getLinkUserUUID() {
        return linkUserUUID;
    }

    public String getRequestedMobile() {
        return requestedMobile;
    }

    public String getLinkUserEmail() {
        return linkUserEmail;
    }

    public String getLinkUserMobile() {
        return linkUserMobile;
    }

    @Override
    public String toString() {
        return "Link User Extended Response{" +
                "responseCode='" + responseCode + '\'' +
                ", linkUserEmail='" + linkUserEmail + '\'' +
                ", emailVerified='" + emailVerified + '\'' +
                ", emailVerifiedDate='" + emailVerifiedDate + '\'' +
                ", mobile='" + linkUserMobile + '\'' +
                ", mobileVerified='" + mobileVerified + '\'' +
                ", mobileVerifiedDate='" + mobileVerifiedDate + '\'' +
                ", linkUserFirstName='" + linkUserFirstName + '\'' +
                ", linkUserLastName='" + linkUserLastName + '\'' +
                ", linkUserUUID='" + linkUserUUID + '\'' +
                ", requestedMobile='" + requestedMobile + '\'' +
                '}';
    }

}
