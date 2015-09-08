package com.citrus.mobile;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.response.CitrusError} instead.
 */
@Deprecated
public class Errorclass {

    public static JSONObject addErrorFlag(String message, JSONObject object) {
        if (object == null) {
            object = new JSONObject();
        }

        try {
            if (!object.has("error"))
                object.put("error", "600");

            if (!object.has("message"))
                object.put("message", message);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

}
