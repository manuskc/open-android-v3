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

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CashoutInfo;
import com.citrus.sdk.classes.CitrusConfig;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.sdk.response.PaymentResponse;
import com.orhanobut.logger.Logger;


public class UIActivity extends ActionBarActivity implements UserManagementFragment.UserManagementInteractionListener, WalletFragmentListener {

    private FragmentManager fragmentManager = null;
    private Context mContext = this;
    private CitrusClient citrusClient = null;
    private CitrusConfig citrusConfig = null;
    private FrameLayout frameLayout = null;
    private View snackBarParent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);

        frameLayout = (FrameLayout) findViewById(R.id.container);

        fragmentManager = getSupportFragmentManager();

        citrusClient = CitrusClient.getInstance(mContext);
        citrusClient.enableLog(Constants.enableLogging);

        citrusClient.init(Constants.SIGNUP_ID, Constants.SIGNUP_SECRET, Constants.SIGNIN_ID, Constants.SIGNIN_SECRET, Constants.VANITY, Constants.environment);

        citrusConfig = CitrusConfig.getInstance();
        citrusConfig.setColorPrimary(Constants.colorPrimary);
        citrusConfig.setColorPrimaryDark(Constants.colorPrimaryDark);
        citrusConfig.setTextColorPrimary(Constants.textColor);

        snackBarParent = new View(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 200);
        layoutParams.gravity = Gravity.BOTTOM;
        snackBarParent.setLayoutParams(layoutParams);
        frameLayout.addView(snackBarParent);

        showUI();
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
            showSnackBar(transactionResponse.getMessage());
        }
    }

    @Override
    public void onPaymentTypeSelected(Utils.PaymentType paymentType, Amount amount) {
        if (paymentType == Utils.PaymentType.CITRUS_CASH) {

            try {
                citrusClient.payUsingCitrusCash(new PaymentType.CitrusCash(amount, Constants.BILL_URL), new Callback<TransactionResponse>() {
                    @Override
                    public void success(TransactionResponse transactionResponse) {
                        showSnackBar(transactionResponse.getMessage());
                    }

                    @Override
                    public void error(CitrusError error) {
                        showSnackBar(error.getMessage());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
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
       citrusClient.cashout(cashoutInfo, new Callback<PaymentResponse>() {
            @Override
            public void success(PaymentResponse paymentResponse) {
                showSnackBar((paymentResponse.getStatus() == CitrusResponse.Status.SUCCESSFUL) ? "Withdraw Successful" : "Error While Withdrawing" );
            }

            @Override
            public void error(CitrusError error) {
                showSnackBar(error.getMessage());
            }
        });
    }

    @Override
    public void showSnackBar(String message) {
        Snackbar.make(snackBarParent, message, Snackbar.LENGTH_LONG).show();
    }

    public void onWalletPaymentClicked(View view) {
        onShowWalletScreen();
    }
}
