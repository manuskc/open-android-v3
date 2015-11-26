package com.citrus.sdk.response;

import com.orhanobut.logger.LogLevel;

/**
 * Created by MANGESH KADAM on 6/9/2015.
 */
public class CitrusLogger {

    private static boolean ENABLE_LOGS = false;

    private static final String LOG_TAG = "CITRUSSDK";

    public static void enableLogs() {
        ENABLE_LOGS = true;
        com.orhanobut.logger.Logger.init(LOG_TAG)
                .setMethodCount(2)
                .hideThreadInfo()
                .setLogLevel(LogLevel.FULL);
    }

    public static void disableLogs() {
        ENABLE_LOGS = false;
        com.orhanobut.logger.Logger.init(LOG_TAG)
                .setMethodCount(0)
                .hideThreadInfo()
                .setLogLevel(LogLevel.NONE);
    }

    public static boolean isEnableLogs() {
        return ENABLE_LOGS;
    }
}
