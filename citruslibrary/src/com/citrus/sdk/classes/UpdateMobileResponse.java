package com.citrus.sdk.classes;

/**
 * Created by salil on 16/7/15.
 */
public class UpdateMobileResponse extends UMResponse {
    /**
     * Verification Code Sent Successfully.
     */
    public static final int VERIFICATION_CODE_SENT_SUCCESSFULLY = 1;
    /**
     * Blank Or Null Mobile No.
     */
    public static final int ERROR_CODE_BLANK_MOBILE_NO = 2;
    /**
     * Invalid Mobile No.
     */
    public static final int ERROR_CODE_INVALID_MOBILE_NO = 3;
    /**
     * Mobile No is already verified.
     */
    public static final int ERROR_CODE_MOBILE_NO_ALREADY_VERIFIED = 4;

    private static final String MESSAGE_VERIFICATIN_CODE_SENT_SUCCESSFULLY = "Verification Code Sent Successfully On Mobile No.";
    private static final String ERROR_MESSAGE_BLANK_MOBILE_NO = "You did not enter Mobile No.";
    private static final String ERROR_MESSAGE_INVALID_MOBILE_NO = "Please enter valid Mobile No.";
    private static final String ERROR_MESSAGE_MOBILE_NO_ALREADY_VERIFIED = "The Mobile No is already verified.";

    /**
     * The response is in following format.
     * {
     * "responseCode": "R-002-1",
     * "responseMessage": "m-otp login",
     * "responseData": {}
     * }
     * The responseCode will either be R-002-1 or R-002-2 or R-002-3 or R-002-4
     */

    public UpdateMobileResponse(String responseCode, String responseMessage, String responseCode1) {
        super(responseCode, responseMessage);
        responseCode = responseCode1;
    }

    public int getResponseCode() {
        if ("R-100-0".equals(responseCode)) {
            return VERIFICATION_CODE_SENT_SUCCESSFULLY;
        } else if ("R-100-1".equals(responseCode)) {
            return ERROR_CODE_BLANK_MOBILE_NO;
        } else if ("R-100-2".equals(responseCode)) {
            return ERROR_CODE_INVALID_MOBILE_NO;
        } else if ("R-100-3".equals(responseCode)) {
            return ERROR_CODE_MOBILE_NO_ALREADY_VERIFIED;
        } else {
            return ERROR_CODE_SOME_ERROR_OCCURRED;
        }
    }

    public String getResponseMessage() {
        String message;
        switch (getResponseCode()) {
            case VERIFICATION_CODE_SENT_SUCCESSFULLY:
                message = MESSAGE_VERIFICATIN_CODE_SENT_SUCCESSFULLY;
                break;
            case ERROR_CODE_BLANK_MOBILE_NO:
                message = ERROR_MESSAGE_BLANK_MOBILE_NO;
                break;
            case ERROR_CODE_INVALID_MOBILE_NO:
                message = ERROR_MESSAGE_INVALID_MOBILE_NO;
                break;
            case ERROR_CODE_MOBILE_NO_ALREADY_VERIFIED:
                message = ERROR_MESSAGE_MOBILE_NO_ALREADY_VERIFIED;
                break;
            default:
                message = ERROR_MESSAGE_SOME_ERROR_OCCURRED;
                break;
        }

        return message;
    }
}
