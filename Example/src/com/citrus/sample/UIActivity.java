/*
 *
 *    Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 */

package com.citrus.sample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.Environment;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CashoutInfo;
import com.citrus.sdk.classes.CitrusConfig;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.sdk.response.PaymentResponse;


public class UIActivity extends ActionBarActivity implements UserManagementFragment.UserManagementInteractionListener, WalletFragmentListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private FragmentManager fragmentManager = null;
    private Context mContext = this;
    private CitrusClient citrusClient = null;
    private CitrusConfig citrusConfig = null;
    AuthenticationSignatures authenticationSignatures = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);

        fragmentManager = getSupportFragmentManager();

        citrusClient = CitrusClient.getInstance(mContext);
        citrusClient.enableLog(Constants.enableLogging);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        initCitrusClient();


        citrusConfig = CitrusConfig.getInstance();
        citrusConfig.setColorPrimary(Constants.colorPrimary);
        citrusConfig.setColorPrimaryDark(Constants.colorPrimaryDark);
        citrusConfig.setTextColorPrimary(Constants.textColor);

        showUI();
    }

    private void initCitrusClient() {

         authenticationSignatures = Utils.getAuthenticationSignatures(this);

        if (Utils.getPreferredEnvironment(this).equalsIgnoreCase(Utils.getResourceString(this, R.string.environment_preference_default_value))) {
            Toast.makeText(this, "UI ACTIVITY ENV = SANDBOX", Toast.LENGTH_SHORT).show();
//            citrusClient.init(Constants.SIGNUP_ID, Constants.SIGNUP_SECRET, Constants.SIGNIN_ID, Constants.SIGNIN_SECRET, Constants.VANITY, Environment.SANDBOX);
            citrusClient.init(authenticationSignatures.getSIGNUP_ID(), authenticationSignatures.getSIGNUP_SECRET(), authenticationSignatures.getSIGNIN_ID(), authenticationSignatures.getSIGNIN_SECRET(), authenticationSignatures.getVANITY(), Environment.SANDBOX);
        }else{
            Toast.makeText(this, "UI ACTIVITY ENV = PRODUCTION", Toast.LENGTH_SHORT).show();
//            citrusClient.init(Constants.SIGNUP_ID, Constants.SIGNUP_SECRET, Constants.SIGNIN_ID, Constants.SIGNIN_SECRET, Constants.VANITY, Environment.PRODUCTION);
            citrusClient.init(authenticationSignatures.getSIGNUP_ID(), authenticationSignatures.getSIGNUP_SECRET(), authenticationSignatures.getSIGNIN_ID(), authenticationSignatures.getSIGNIN_SECRET(), authenticationSignatures.getVANITY(), Environment.PRODUCTION);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if ((Utils.getResourceString(UIActivity.this,R.string.prefs_environment_key).equals(key))
                || (Utils.getResourceString(UIActivity.this,R.string.prefs_signin_id_key).equals(key))
                || (Utils.getResourceString(UIActivity.this,R.string.prefs_signin_secret_key).equals(key))
                || (Utils.getResourceString(UIActivity.this,R.string.prefs_signup_id_key).equals(key))
                || (Utils.getResourceString(UIActivity.this,R.string.prefs_signup_secret_key).equals(key))
                || (Utils.getResourceString(UIActivity.this,R.string.prefs_vanity_key).equals(key))){

            showAlertPrompt();
        }
    }

    private void showAlertPrompt() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(UIActivity.this);
        String message = "This change will log you out. Do you want to continue?";
        String positiveButtonText = "Yes";

        alert.setTitle("Alert");
        alert.setMessage(message);
        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(UIActivity.this, "UI ACTIVITY REFRESH CLIENT", Toast.LENGTH_SHORT).show();

                citrusClient.destroyVariables();

                initCitrusClient();

                citrusClient.signOut(new Callback<CitrusResponse>() {
                    @Override
                    public void success(CitrusResponse citrusResponse) {
                        Utils.showToast(UIActivity.this, citrusResponse.getMessage());

                    }

                    @Override
                    public void error(CitrusError error) {
                        Utils.showToast(UIActivity.this, error.getMessage());
                    }
                });
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();
    }


    private void showUI() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.container, UIActivityFragment.newInstance());

        fragmentTransaction.commit();
    }

    public void onUserManagementClicked(View view) {
        UserManagementFragment fragment = UserManagementFragment.newInstance(this);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.container, fragment);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void onGuestPaymentClicked(View view) {
    }

    @Override
    public void onShowWalletScreen() {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.container, WalletPaymentFragment.newInstance());

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onPaymentComplete(TransactionResponse transactionResponse) {
        if (transactionResponse != null) {
            Utils.showToast(mContext, transactionResponse.getMessage());
        }
    }

    @Override
    public void onPaymentTypeSelected(Utils.PaymentType paymentType, Amount amount) {
        if (paymentType == Utils.PaymentType.CITRUS_CASH) {

            try {

                citrusClient.payUsingCitrusCash(new PaymentType.CitrusCash(amount, authenticationSignatures.getBILL_URL()), new Callback<TransactionResponse>() {
//                citrusClient.payUsingCitrusCash(new PaymentType.CitrusCash(amount, Constants.BILL_URL), new Callback<TransactionResponse>() {
                    @Override
                    public void success(TransactionResponse transactionResponse) {
                        Utils.showToast(getApplicationContext(), transactionResponse.getMessage());
                    }

                    @Override
                    public void error(CitrusError error) {
                        Utils.showToast(getApplicationContext(), error.getMessage());
                    }
                });
            } catch (CitrusException e) {
                e.printStackTrace();

                Utils.showToast(UIActivity.this, e.getMessage());
            }
        } else {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.container, CardPaymentFragment.newInstance(paymentType, amount));

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onCashoutSelected(CashoutInfo cashoutInfo) {
        citrusClient.saveCashoutInfo(cashoutInfo, new Callback<CitrusResponse>() {
            @Override
            public void success(CitrusResponse citrusResponse) {
                Utils.showToast(getApplicationContext(), citrusResponse.getMessage());
            }

            @Override
            public void error(CitrusError error) {
                Utils.showToast(getApplicationContext(), error.getMessage());
            }
        });

        citrusClient.cashout(cashoutInfo, new Callback<PaymentResponse>() {
            @Override
            public void success(PaymentResponse paymentResponse) {
                Utils.showToast(getApplicationContext(), paymentResponse.toString());
            }

            @Override
            public void error(CitrusError error) {
                Utils.showToast(getApplicationContext(), error.getMessage());
            }
        });
    }

    @Override
    public void onFragmentChangeEvent(Bundle bundle, int fragmentType) {

        switch (fragmentType) {
            case SETTINGS_FRAGMENT:

                Intent settingsIntent = new Intent(UIActivity.this,SettingsActivity.class);
                startActivity(settingsIntent);

                break;
        }

    }

    public void onWalletPaymentClicked(View view) {
        onShowWalletScreen();
    }
}
