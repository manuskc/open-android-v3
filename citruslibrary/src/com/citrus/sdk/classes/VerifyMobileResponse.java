package com.citrus.sdk.classes;

/**
 * Created by salil on 16/7/15.
 */
public class VerifyMobileResponse extends UMResponse {
    /**
     * Mobile number is verified successfully
     */
    public static final int MOBILE_VERIFIED_SUCCESSFULLY = 1;
    /**
     * Blank Or Null Verification Code.
     */
    public static final int ERROR_CODE_BLANK_VERIFICATION_CODE = 2;
    /**
     * Incorrect Verification Code.
     */
    public static final int ERROR_CODE_INCORRECT_VERFICATION_CODE = 3;
    /**
     * Verification Code Expired.
     */
    public static final int ERROR_CODE_VERIFICATION_CODE_EXPIRED = 4;

    private static final String MESSAGE_VERIFICATIN_CODE_SENT_SUCCESSFULLY = "Mobile Verified Successfully.";
    private static final String ERROR_MESSAGE_BLANK_VERIFICATION_CODE = "You did not enter Verification Code.";
    private static final String ERROR_MESSAGE_INCORRECT_VERIFICATION_CODE = "Please enter valid verification code.";
    private static final String ERROR_MESSAGE_VERIFICATION_CODE_EXPIRED = "Verification Code Expired.";

    /**
     * The response is in following format.
     * {
     * "responseCode": "R-002-1",
     * "responseMessage": "m-otp login",
     * "responseData": {}
     * }
     * The responseCode will either be R-002-1 or R-002-2 or R-002-3 or R-002-4
     */

    public VerifyMobileResponse(String responseCode, String responseMessage) {
        super(responseCode, responseMessage);
    }

    public int getResponseCode() {
        if ("R-110-0".equals(responseCode)) {
            return MOBILE_VERIFIED_SUCCESSFULLY;
        } else if ("R-110-1".equals(responseCode)) {
            return ERROR_CODE_BLANK_VERIFICATION_CODE;
        } else if ("R-110-2".equals(responseCode)) {
            return ERROR_CODE_INCORRECT_VERFICATION_CODE;
        } else if ("R-110-3".equals(responseCode)) {
            return ERROR_CODE_VERIFICATION_CODE_EXPIRED;
        } else {
            return ERROR_CODE_SOME_ERROR_OCCURRED;
        }
    }

    public String getResponseMessage() {
        String message;
        switch (getResponseCode()) {
            case MOBILE_VERIFIED_SUCCESSFULLY:
                message = MESSAGE_VERIFICATIN_CODE_SENT_SUCCESSFULLY;
                break;
            case ERROR_CODE_BLANK_VERIFICATION_CODE:
                message = ERROR_MESSAGE_BLANK_VERIFICATION_CODE;
                break;
            case ERROR_CODE_INCORRECT_VERFICATION_CODE:
                message = ERROR_MESSAGE_INCORRECT_VERIFICATION_CODE;
                break;
            case ERROR_CODE_VERIFICATION_CODE_EXPIRED:
                message = ERROR_MESSAGE_VERIFICATION_CODE_EXPIRED;
                break;
            default:
                message = ERROR_MESSAGE_SOME_ERROR_OCCURRED;
                break;
        }

        return message;
    }
}
