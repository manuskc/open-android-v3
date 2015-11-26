package com.citrus.cash;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * @deprecated in v3
 * <p/>
 * This class is no longer supported.
 */
@Deprecated
public class PersistentConfig {
    private static final String PREFS_NAME = "prefs_file";

    private SharedPreferences settings;

    public PersistentConfig(Context context) {

        settings = context.getSharedPreferences(PREFS_NAME, 0);

    }

    public String getCookieString() {

        return settings.getString("my_cookie", "");

    }

    public void setCookie(String cookie) {

        SharedPreferences.Editor editor = settings.edit();

        editor.putString("my_cookie", cookie);

        editor.commit();

    }

    public boolean clearToken() {
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        return editor.commit();


    }
}
