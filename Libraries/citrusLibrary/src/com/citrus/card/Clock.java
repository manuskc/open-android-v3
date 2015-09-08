/*
   Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.citrus.card;

import java.util.Calendar;

/**
 * @deprecated in v3
 * <p/>
 * This class is no longer supported.
 */
@Deprecated
public class Clock {
    private static Clock instance;
    protected Calendar calendarInstance;

    protected static Clock getInstance() {
        if (instance == null) {
            instance = new Clock();
        }
        return instance;
    }

    private Calendar _calendarInstance() {
        return calendarInstance != null ? (Calendar) calendarInstance.clone() : Calendar.getInstance();
    }

    public static Calendar getCalendarInstance() {
        return getInstance()._calendarInstance();
    }
}
