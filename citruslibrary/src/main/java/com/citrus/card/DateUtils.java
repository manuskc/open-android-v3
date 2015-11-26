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
import java.util.Locale;

/**
 * @deprecated in v3
 * <p/>
 * This class is no longer supported.
 */
@Deprecated
public class DateUtils {
    public static boolean hasYearPassed(int year) {
        int normalized = normalizeYear(year);
        Calendar now = Clock.getCalendarInstance();
        return normalized < now.get(Calendar.YEAR);
    }

    public static boolean hasMonthPassed(int year, int month) {
        Calendar now = Clock.getCalendarInstance();
        return hasYearPassed(year) || normalizeYear(year) == now.get(Calendar.YEAR) && month < (now.get(Calendar.MONTH) + 1);
    }

    private static int normalizeYear(int year) {
        if (year < 100 && year >= 0) {
            Calendar now = Clock.getCalendarInstance();
            String currentYear = String.valueOf(now.get(Calendar.YEAR));
            String prefix = currentYear.substring(0, currentYear.length() - 2);
            year = Integer.parseInt(String.format(Locale.US, "%s%02d", prefix, year));
        }
        return year;
    }
}
