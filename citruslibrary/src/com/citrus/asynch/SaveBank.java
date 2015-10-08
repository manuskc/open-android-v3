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
import com.citrus.netbank.Bank;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.wallet.Wallet;

/**
 * Created by Salil on 30/01/2015.
 */
/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.CitrusClient#savePaymentOption(PaymentOption, com.citrus.sdk.Callback)} instead.
 */
@Deprecated
public class SaveBank extends AsyncTask<Bank, Void, Void> {
    String saveBankResult, error;
    Activity activity;
    Callback callback;

    public SaveBank(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Bank... params) {
        Bank bank = params[0];
        if (bank == null
                || TextUtils.isEmpty(bank.getBankName())) {
            saveBankResult = "";
            error = "Invalid Bank!";
            return null;
        }
        Wallet wallet = new Wallet(bank);
        saveBankResult = wallet.saveBank(activity);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        callback.onTaskexecuted(saveBankResult, error);
    }
}
