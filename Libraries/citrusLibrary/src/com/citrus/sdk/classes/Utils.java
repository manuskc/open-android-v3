package com.citrus.sdk.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
}
