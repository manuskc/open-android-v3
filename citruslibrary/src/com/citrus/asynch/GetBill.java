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
package com.citrus.asynch;

import android.os.AsyncTask;

import com.citrus.mobile.Callback;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

/**
 * @deprecated in v3
 * <p/>
 * This class is not longer supported.
 */
@Deprecated
public class GetBill extends AsyncTask<Void, Void, Void> {
    String billurl;

    JSONObject billJSONObject;
    String error;

    Callback callback;

    public GetBill(String url, Callback callback) {
        this.billurl = url;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        URLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        try {
            int responseCode;
            if (billurl.startsWith("https")) {
                urlConnection = (HttpsURLConnection) new URL(billurl).openConnection();
                responseCode = ((HttpsURLConnection) urlConnection).getResponseCode();
            } else {
                urlConnection = (HttpURLConnection) new URL(billurl).openConnection();
                responseCode = ((HttpURLConnection) urlConnection).getResponseCode();
            }

            if (responseCode == HttpStatus.SC_OK) {


                bufferedReader = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = bufferedReader.readLine()) != null) {
                    response.append(inputLine);
                }

                billJSONObject = new JSONObject(response.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
            error = "Is your internet working?";
        } catch (JSONException e) {
            e.printStackTrace();
            error = "Is your billing url correct?";
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (error != null) {
            callback.onTaskexecuted(null, error);
        } else {
            callback.onTaskexecuted(billJSONObject.toString(), null);
        }
    }
}
