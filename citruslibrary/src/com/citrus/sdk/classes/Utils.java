package com.citrus.sdk.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.citrus.sdk.otp.NetBankForOTP;

/**
 * Created by salil on 8/7/15.
 */
public class Utils {

    public static boolean isNetworkConnected(Context context) {

        boolean connected = false;

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        connected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        cm = null;
        return connected;
    }

    public static String removeSpecialCharacters(String input) {
        String str = null;
        if (!TextUtils.isEmpty(input)) {
            str = input.replaceAll("[^\\w\\s-]", "").replaceAll("[-_]", "");
        }

        return str;
    }

    public static String getOTP(String message, NetBankForOTP netBankForOTP) {
        String otp = "";
        int length = netBankForOTP.getOTPLength();

        String[] nbs = message.split("\\D+");

        if (nbs.length != 0) {
            for (String number : nbs) {
                if (number.length() == length) {
                    return number;
                }
            }
        }

        return otp;
    }
}
