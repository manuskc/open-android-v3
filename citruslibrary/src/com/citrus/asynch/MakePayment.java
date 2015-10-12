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
import com.citrus.mobile.Config;
import com.citrus.mobile.RESTclient;

import org.json.JSONObject;

/**
 * @deprecated in v3
 * <p/>
 * This class is no longer supported.
 */
@Deprecated
public class MakePayment extends AsyncTask<Void, Void, Void> {
    JSONObject payment, headers, response;
    Callback callback;

    public MakePayment(JSONObject payment, JSONObject headers, Callback callback) {
        this.payment = payment;
        this.headers = headers;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        RESTclient resTclient = new RESTclient("struct", Config.getEnv(), null, null);
        response = resTclient.postPayment(payment);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (response == null) {
            callback.onTaskexecuted("", "Some error occurred.");
        } else if (response.has("error")) {
            callback.onTaskexecuted("", response.toString());
        } else {
            callback.onTaskexecuted(response.toString(), "");
        }
    }
}
