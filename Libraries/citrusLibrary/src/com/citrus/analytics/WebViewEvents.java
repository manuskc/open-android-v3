package com.citrus.analytics;

/**
 * Created by MANGESH KADAM on 4/24/2015.
 */
public enum WebViewEvents {

    OPEN(53),
    CLOSE(59),
    BACK_KEY(61);

    private int eventType;

    WebViewEvents(int eventType) {
        this.eventType = eventType;
    }

    public int getValue() {
        return eventType;
    }

}
