package com.citrus.sdk.classes;

/**
 * Created by salilgodbole on 10/07/15.
 */
public enum PGHealth {
    GOOD, BAD, UNKNOWN;

    public static PGHealth getPGHealth(String health) {
        try {
            int healthInInt = Integer.parseInt(health);
            if (healthInInt > 50) {
                return GOOD;
            } else {
                return BAD;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return UNKNOWN;
        }
    }
}
