package com.citrus.cash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.citrus.mobile.Callback;
import com.citrus.mobile.Config;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @deprecated in v3
 * <p/>
 * This class is no longer supported.
 */
@Deprecated
public class PrepaidPg {
    Activity activity;
    Callback cb;

    String sessionCookie;

    CookieManager cookieManager;

    public PrepaidPg(Activity activity) {
        this.activity = activity;
    }

    public void pay(Callback cb, String response, String error) {
        this.cb = cb;

        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject redirect = new JSONObject(response);

                if (!TextUtils.isEmpty(redirect.getString("redirectUrl"))) {
                    processwebflow(redirect.getString("redirectUrl"));
                } else {
                    Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void processwebflow(String url) {
        WebView webview = new WebView(activity);
        final WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.contains("/prepaid/pg/verify/")) {
                    cb.onTaskexecuted("", "User is not signed in");
                    return false;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        cookieManager = CookieManager.getInstance();

        sessionCookie = new PersistentConfig(activity.getApplicationContext()).getCookieString();

        if (TextUtils.isEmpty(sessionCookie.toString()) || sessionCookie == null) {
            cb.onTaskexecuted("", "User not signed in!");
            return;
        }

        if (TextUtils.equals(Config.getEnv().toString(), "sandbox")) {
            cookieManager.setCookie("https://sandboxadmin.citruspay.com", sessionCookie);
        } else {
            cookieManager.setCookie("https://admin.citruspay.com", sessionCookie);
        }

        webview.addJavascriptInterface(new JsInterface(), "CitrusResponse");

        webview.loadUrl(url);
    }

    private class JsInterface {

        @JavascriptInterface
        public void pgResponse(String response) {
            cb.onTaskexecuted(response, "");
        }

    }
}
