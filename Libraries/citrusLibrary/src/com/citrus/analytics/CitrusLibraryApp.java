/*
 *
 *    Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 */

package com.citrus.analytics;

import android.content.Context;

import com.citrus.mobile.Config;
import com.citrus.sdk.response.CitrusLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created by MANGESH KADAM on 4/21/2015.
 */
public class CitrusLibraryApp  {
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    private final String LOG_TAG = "CITRUSLIBRARY";

    static HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public CitrusLibraryApp() {
        super();
    }

    synchronized static Tracker getTracker(TrackerName trackerId, Context context) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            if(CitrusLogger.isEnableLogs()) {
                analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            }

            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(Config.getAnalyticsID()):null;
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

}
