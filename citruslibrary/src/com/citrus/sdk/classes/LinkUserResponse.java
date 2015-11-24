package com.citrus.sdk.classes;

/**
 * Created by salil on 16/7/15.
 */
public final class LinkUserResponse extends UMResponse {
    /**
     * Login user with mOTP received on the given mobile no.
     */
    public static final int LOGIN_WITH_mOTP = 1;
    /**
     * Login user with eOTP sent on the given emailId.
     */
    public static final int LOGIN_WITH_eOTP = 2;
    /**
     * Login user with eOTP sent on the given emailId and ask user to verify the mobile no using verification code sent on the mobile no.
     */
    public static final int LOGIN_WITH_eOTP_AND_VERIFY_MOBILE = 3;
    /**
     * User is a new user. User's account is created and eOTP sent on the emailId.
     * Ask user to verify the mobile no using verification code sent on the mobile no.
     * The verification of mobileNo in case of new user is mandatory.
     */
    public static final int NEW_USER_LOGIN_WITH_eOTP_AND_VERIFY_MOBILE = 4;

    private static final String MESSAGE_LOGIN_WITH_mOTP = "Please login with OTP sent on the mobile no.";
    private static final String MESSAGE_LOGIN_WITH_eOTP = "Please login with OTP sent on email id";
    private static final String MESSAGE_LOGIN_WITH_eOTP_VERIFY_MOBILE = "Please login with OTP sent on email id and verify mobile no using verification code.";
    private static final String MESSAGE_NEW_USER_LOGIN_WITH_eOTP_AND_VERIFY_MOBILE = "User's account is created. Please login with OTP sent on email id and verify mobile no using verification code.";

    /**
     * The response is in following format.
     * {
     * "responseCode": "R-002-1",
     * "responseMessage": "m-otp login",
     * "responseData": {}
     * }
     * The responseCode will either be R-002-1 or R-002-2 or R-002-3 or R-002-4
     */

    public LinkUserResponse(String responseCode, String responseMessage) {
        super(responseCode, responseMessage);
    }

    @Override
    public int getResponseCode() {
        if ("R-002-1".equals(responseCode)) {
            return LOGIN_WITH_mOTP;
        } else if ("R-002-2".equals(responseCode)) {
            return LOGIN_WITH_eOTP;
        } else if ("R-002-3".equals(responseCode)) {
            return LOGIN_WITH_eOTP_AND_VERIFY_MOBILE;
        } else if ("R-002-4".equals(responseCode)) {
            return NEW_USER_LOGIN_WITH_eOTP_AND_VERIFY_MOBILE;
        } else {
            return ERROR_CODE_SOME_ERROR_OCCURRED;
        }
    }

    @Override
    public String getResponseMessage() {
        String message;
        switch (getResponseCode()) {
            case LOGIN_WITH_mOTP:
                message = MESSAGE_LOGIN_WITH_mOTP;
                break;
            case LOGIN_WITH_eOTP:
                message = MESSAGE_LOGIN_WITH_eOTP;
                break;
            case LOGIN_WITH_eOTP_AND_VERIFY_MOBILE:
                message = MESSAGE_LOGIN_WITH_eOTP_VERIFY_MOBILE;
                break;
            case NEW_USER_LOGIN_WITH_eOTP_AND_VERIFY_MOBILE:
                message = MESSAGE_NEW_USER_LOGIN_WITH_eOTP_AND_VERIFY_MOBILE;
                break;
            default:
                message = ERROR_MESSAGE_SOME_ERROR_OCCURRED;
                break;
        }

        return message;
    }
}
