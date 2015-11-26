package com.citrus.analytics;

/**
 * Created by MANGESH KADAM on 4/21/2015.
 */
public enum ConnectionType {
    MOBILE_2G(23),
    MOBILE_3G(29),
    MOBILE_4G(31),
    WIFI(37),
    UNKNOWN(41),
    NOT_CONNECTED(43);

    private final int connectionType;

    ConnectionType(int connectionType) {
        this.connectionType = connectionType;
    }

    public int getValue() {
        return  connectionType;
    }
}
