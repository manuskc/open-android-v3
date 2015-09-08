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

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.citrus.mobile.Callback;
import com.citrus.wallet.Wallet;

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.CitrusClient#getWallet(com.citrus.sdk.Callback)} instead.
 */
@Deprecated
public class GetWallet extends AsyncTask<Void, Void, Void> {
    String stringWallet;
    Activity activity;
    Callback callback;

    public GetWallet(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Wallet wallet = new Wallet();
        stringWallet = wallet.getWallet(activity);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (TextUtils.isEmpty(stringWallet)){
            callback.onTaskexecuted("", "Could not get wallet");
        }
        else {
            callback.onTaskexecuted(stringWallet, "");
        }
    }
}
