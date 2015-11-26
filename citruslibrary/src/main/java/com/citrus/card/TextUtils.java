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

/**
 * @deprecated in v3
 * <p/>
 * This class is no longer supported.
 */
@Deprecated
public class TextUtils {
    public static boolean hasAnyPrefix(String number, String... prefixes) {
        if (number == null) {
            return false;
        }
        number = number.replaceAll("\\s+", "");

        for (String prefix : prefixes) {
            if (number.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWholePositiveNumber(String value) {
        if (value == null) {
            return false;
        }
        for (char c : value.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static String nullIfBlank(String value) {
        if (isBlank(value)) {
            return null;
        }
        return value;
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static long isValidMobileNumber(String mobileNumber) {
        if (mobileNumber.length() < 10) {
//            CitrusLogging.logDebug("Invalid ***");
            return -1;
        }

        mobileNumber = mobileNumber.replaceAll("-", "");
        mobileNumber = mobileNumber.replaceAll("\\(", "");
        mobileNumber = mobileNumber.replaceAll("\\)", "");
        mobileNumber = mobileNumber.replaceAll("\\s+", "");
        String startOne = "^0091.*"; //Welcome(.*)
        String startTwo = "^\\+91.*";
        String startThree = "^91.*";
        String startFour = "^0.*";
        String startFive = "^[7-9][0-9]{9}$";
        //"^(?:0091|\\+91||91|0)[7-9][0-9-]{9}$";

        if (mobileNumber.matches(startFive) && mobileNumber.length() == 10) {
            return Long.parseLong(mobileNumber);
        } else if (mobileNumber.matches(startOne)) {
            mobileNumber = mobileNumber.substring(4);
        } else if (mobileNumber.matches(startTwo)) {
            mobileNumber = mobileNumber.substring(3);
        } else if (mobileNumber.matches(startThree)) {
            mobileNumber = mobileNumber.substring(2);
        } else if (mobileNumber.matches(startFour)) {
            mobileNumber = mobileNumber.substring(1);
        }
        if (mobileNumber.length() < 10) {
//            CitrusLogging.logDebug("Invalid ***");
            return -1;
        }
        if (mobileNumber.matches(startFive)) {
//            CitrusLogging.logDebug("Trimmed NUMBER **** " + mobileNumber);
            return Long.parseLong(mobileNumber);
        } else {
//            CitrusLogging.logDebug("Invalid ***");
           return -1;
        }

    }
}
