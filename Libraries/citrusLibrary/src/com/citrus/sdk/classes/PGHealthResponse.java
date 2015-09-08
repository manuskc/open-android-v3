package com.citrus.sdk.classes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by salil on 26/6/15.
 */
public final class PGHealthResponse {

    @SerializedName("responseCode")
    private int responseCode = 1;
    @SerializedName("message")
    private String message = null;

    public PGHealthResponse(PGHealth pgHealth, String message) {
        this.message = message;

        responseCode = pgHealth.ordinal();
    }

    public PGHealth getPgHealth() {
        if (responseCode == 0) {
            return PGHealth.GOOD;
        } else {
            return PGHealth.BAD;
        }
    }

    public String getMessage() {
        return message;
    }
}
