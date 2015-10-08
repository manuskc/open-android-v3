package com.citrus.mobile;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @deprecated in v3.
 * <p/>
 * This class is no longer supported.
 */
@Deprecated
public class CitrusWebViewClient extends WebViewClient {

    Callback callback;
    JSONObject responsejson;

    public CitrusWebViewClient(Callback callback) {
        this.callback = callback;
        responsejson = new JSONObject();
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        if (url.contains("#")) {
            String[] response = url.split("#");

            if (response.length == 2 && response[1].contains("SUCC")) {
                try {
                    responsejson.put("status", 200);
                    responsejson.put("message", response[1]);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onTaskexecuted(responsejson.toString(), "");
                return false;
            } else {
                try {
                    responsejson.put("status", 600);
                    responsejson.put("message", response[1]);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onTaskexecuted("", responsejson.toString());
                return false;
            }

        }

        return super.shouldOverrideUrlLoading(view, url);

    }
}	
