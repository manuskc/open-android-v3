package com.citrus.sdk.response;

/**
 * Created by salil on 22/7/15.
 */
public class BindUserResponse {

    private final int responseCode;
    private final String message;

    public static int RESPONSE_CODE_EXISTING_USER_BOUND = 0;
    public static int RESPONSE_CODE_NEW_USER_BOUND = 1;

    public static String ERROR_RESPONSE_CODE_EXISTING_USER_BOUND = "Exising User Bound";
    public static String ERROR_RESPONSE_CODE_NEW_USER_BOUND = "New User Bound";

    public BindUserResponse(int responseCode) {
        this.responseCode = responseCode;

        if (responseCode == RESPONSE_CODE_EXISTING_USER_BOUND) {
            message = ERROR_RESPONSE_CODE_EXISTING_USER_BOUND;
        } else {
            message = ERROR_RESPONSE_CODE_NEW_USER_BOUND;
        }
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getMessage() {
        return message;
    }
}
