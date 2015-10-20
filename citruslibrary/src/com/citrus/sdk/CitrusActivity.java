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

package com.citrus.sdk;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.citrus.analytics.EventsManager;
import com.citrus.analytics.WebViewEvents;
import com.citrus.cash.LoadMoney;
import com.citrus.cash.PersistentConfig;
import com.citrus.cash.Prepaid;
import com.citrus.library.R;
import com.citrus.mobile.Callback;
import com.citrus.mobile.Config;
import com.citrus.payment.Bill;
import com.citrus.payment.PG;
import com.citrus.payment.UserDetails;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusConfig;
import com.citrus.sdk.classes.Utils;
import com.citrus.sdk.dynamicPricing.DynamicPricingResponse;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.payment.PaymentBill;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class CitrusActivity extends ActionBarActivity {

    private final int WAIT_TIME = 200;
    private final String WAIT_MESSAGE = "Processing Payment. Please Wait...";
    private final String CANCEL_MESSAGE = "Cancelling Transaction. Please Wait...";

    private WebView mPaymentWebview = null;
    private Context mContext = this;
    private ProgressDialog mProgressDialog = null;
    @Deprecated
    private PaymentParams mPaymentParams = null;
    private PaymentType mPaymentType = null;
    private PaymentOption mPaymentOption = null;
    private String mTransactionId = null;
    private ActionBar mActionBar = null;
    private String mColorPrimary = null;
    private String mColorPrimaryDark = null;
    private String mTextColorPrimary = null;
    private CitrusConfig mCitrusConfig = null;
    private CitrusUser mCitrusUser = null;
    private String sessionCookie;
    private CookieManager cookieManager;
    private String mpiServletUrl = null;
    private Map<String, String> customParametersOriginalMap = null;
    private CitrusClient mCitrusClient = null;
    private String mActivityTitle = null;
    private int mRequestCode = -1;
    private CountDownTimer mTimer = null;
    private boolean mLoading = false;
    private boolean mShowingDialog = false;
    private boolean isBackKeyPressedByUser = false;
    private boolean passwordPromptShown = false;
    private DynamicPricingResponse dynamicPricingResponse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mPaymentType = getIntent().getParcelableExtra(Constants.INTENT_EXTRA_PAYMENT_TYPE);
        mRequestCode = getIntent().getIntExtra(Constants.INTENT_EXTRA_REQUEST_CODE_PAYMENT, -1);

        if (!(mPaymentType instanceof PaymentType.CitrusCash)) {
            setTheme(R.style.Base_Theme_AppCompat_Light_DarkActionBar);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_citrus);

        dynamicPricingResponse = getIntent().getParcelableExtra(Constants.INTENT_EXTRA_DYNAMIC_PRICING_RESPONSE);
//        mPaymentParams = getIntent().getParcelableExtra(Constants.INTENT_EXTRA_PAYMENT_PARAMS);
        mCitrusConfig = CitrusConfig.getInstance();
        mActivityTitle = mCitrusConfig.getCitrusActivityTitle();

        // Timer to dismiss dialog after specific time once the url loading is complete.
        mTimer = new CountDownTimer(WAIT_TIME, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (!mLoading) {
                    dismissDialog();
                }
            }
        };

        mCitrusClient = CitrusClient.getInstance(mContext);

        // Set payment Params
        if (mPaymentParams != null) {
            mPaymentType = mPaymentParams.getPaymentType();
            mPaymentOption = mPaymentParams.getPaymentOption();
            mCitrusUser = mPaymentParams.getUser();

            mColorPrimary = mPaymentParams.getColorPrimary();
            mColorPrimaryDark = mPaymentParams.getColorPrimaryDark();
            mTextColorPrimary = mPaymentParams.getTextColorPrimary();
        } else if (mPaymentType != null) {
            mPaymentOption = mPaymentType.getPaymentOption();
            mCitrusUser = mPaymentType.getCitrusUser();

            mColorPrimary = mCitrusConfig.getColorPrimary();
            mColorPrimaryDark = mCitrusConfig.getColorPrimaryDark();
            mTextColorPrimary = mCitrusConfig.getTextColorPrimary();
        } else {
            throw new IllegalArgumentException("Payment Type Should not be null");
        }

        String emailId = mCitrusClient.getUserEmailId();
        String mobileNo = mCitrusClient.getUserMobileNumber();

        // Set the citrusUser.
        // Use details from the token in case of load money
        if (mPaymentType instanceof PaymentType.LoadMoney || mPaymentType instanceof PaymentType.CitrusCash) {
            if (mCitrusClient.getCitrusUser() != null) {
                mCitrusUser = mCitrusClient.getCitrusUser();
            } else if (mCitrusUser == null) {
                mCitrusUser = new CitrusUser(emailId, mobileNo);
            }
        } else {
            // In case of PG Payment, send the merchant values.
            if (mCitrusUser == null) {
                mCitrusUser = new CitrusUser(emailId, mobileNo);
            }
        }

        mActionBar = getSupportActionBar();
        mProgressDialog = new ProgressDialog(mContext);
        mPaymentWebview = (WebView) findViewById(R.id.payment_webview);
//        mPaymentWebview.getSettings().setUseWideViewPort(true);
//        mPaymentWebview.getSettings().setLoadWithOverviewMode(true);
        mPaymentWebview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        mPaymentWebview.getSettings().setJavaScriptEnabled(true);

        // This is done to have horizontal scroll for 2 banks whose page renders improperly in the webview
        if (mPaymentOption instanceof NetbankingOption) {

            if ("CID032".equalsIgnoreCase(((NetbankingOption) mPaymentOption).getBankCID()) // Karur Vyasa
                    || "CID051".equalsIgnoreCase(((NetbankingOption) mPaymentOption).getBankCID())) // Canara Bank
            {
                mPaymentWebview.getSettings().setUseWideViewPort(true);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /*
            This setting is required to enable redirection of urls from https to http or vice-versa.
            This redirection is blocked by default from Lollipop (Android 21).
             */
            mPaymentWebview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mPaymentWebview.addJavascriptInterface(new JsInterface(), Constants.JS_INTERFACE_NAME);

        mPaymentWebview.setWebChromeClient(new WebChromeClient());
        mPaymentWebview.setWebViewClient(new CitrusWebClient());

        // Make the webview visible only in case of PGPayment or LoadMoney.
        if (mPaymentType instanceof PaymentType.CitrusCash) {
            mPaymentWebview.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(mActivityTitle)) {
            mActivityTitle = "Processing...";
        }

        if (mCitrusClient.isShowDummyScreenWhilePayments()) {
            setTitle(Html.fromHtml("<font color=\"" + mTextColorPrimary + "\">" + mActivityTitle + "</font>"));
        }

        setActionBarBackground();

        /*
         * Validations and Process payments
         */
        // Check whether the request is coming directly for payment without validation. Do validation.
        if (mRequestCode == Constants.REQUEST_CODE_PAYMENT) {
            if (mPaymentOption instanceof CardOption && !((CardOption) mPaymentOption).validateCard()) {
                sendResult(new TransactionResponse(TransactionResponse.TransactionStatus.FAILED, ((CardOption) mPaymentOption).getCardValidityFailureReasons(), null));

                return;
            }
        }

        // For PG Payment or Pay Using Citrus Cash
        if (mPaymentType instanceof PaymentType.PGPayment || mPaymentType instanceof PaymentType.CitrusCash) {
            if (mPaymentType.getPaymentBill() != null) {
                // TODO Need to refactor the code.
                if (PaymentBill.toJSONObject(mPaymentType.getPaymentBill()) != null) {
                    proceedToPayment(PaymentBill.toJSONObject(mPaymentType.getPaymentBill()).toString());
                }
            } else {
                // Show text while processing payments
                if (mCitrusClient.isShowDummyScreenWhilePayments()) {
                    mPaymentWebview.loadData("<html><body><h5><center>Processing, please wait...<center></h5></body></html>", "text/html", "utf-8");
                }

                showDialog(WAIT_MESSAGE, true);
                fetchBill();
            }
        } else {
            //load cash does not requires Bill Generator
            Amount amount = mPaymentType.getAmount();

            LoadMoney loadMoney = new LoadMoney(amount.getValue(), mPaymentType.getUrl());
            PG paymentgateway = new PG(mPaymentOption, loadMoney, new UserDetails(CitrusUser.toJSONObject(mCitrusUser)));

            showDialog(WAIT_MESSAGE, true);

            paymentgateway.load(CitrusActivity.this, new Callback() {
                @Override
                public void onTaskexecuted(String success, String error) {
                    processresponse(success, error);
                }
            });
        }

        if (TextUtils.isEmpty(mActivityTitle)) {
            mActivityTitle = "Processing...";
        }

        setTitle(Html.fromHtml("<font color=\"" + mTextColorPrimary + "\">" + mActivityTitle + "</font>"));
        setActionBarBackground();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setActionBarBackground() {
        // Set primary color
        if (mColorPrimary != null && mActionBar != null) {
            mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(mColorPrimary)));
        }

        // Set action bar color. Available only on android version Lollipop or higher.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mColorPrimaryDark != null) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(mColorPrimaryDark));
        }
    }

    private void fetchBill() {
        String billUrl = mPaymentType.getUrl();

        if (billUrl.contains("?")) {
            billUrl = billUrl + "&amount=" + mPaymentType.getAmount().getValue();
        } else {
            billUrl = billUrl + "?amount=" + mPaymentType.getAmount().getValue();
        }

        mCitrusClient.getBill(billUrl, mPaymentType.getAmount(), new com.citrus.sdk.Callback<PaymentBill>() {
            @Override
            public void success(PaymentBill paymentBill) {
                customParametersOriginalMap = paymentBill.getCustomParametersMap();
                JSONObject billJson = PaymentBill.toJSONObject(paymentBill);
                if (billJson != null) {
                    proceedToPayment(billJson.toString());
                } else {
                    TransactionResponse transactionResponse = new TransactionResponse(TransactionResponse.TransactionStatus.FAILED, ResponseMessages.ERROR_MESSAGE_INVALID_BILL, mTransactionId);
                    sendResult(transactionResponse);
                }
            }

            @Override
            public void error(CitrusError error) {
                TransactionResponse transactionResponse = new TransactionResponse(TransactionResponse.TransactionStatus.FAILED, error.getMessage(), mTransactionId);
                sendResult(transactionResponse);
            }
        });
    }

    private void proceedToPayment(String billJSON) {

        if (mPaymentType instanceof PaymentType.CitrusCash) { //pay using citrus cash

            UserDetails userDetails = new UserDetails(CitrusUser.toJSONObject(mCitrusUser));
            Prepaid prepaid = new Prepaid(userDetails.getEmail());
            Bill bill = new Bill(billJSON);
            mTransactionId = bill.getTxnId();
            PG paymentgateway = new PG(prepaid, bill, userDetails);
            if (bill.getCustomParameters() != null) {
                paymentgateway.setCustomParameters(bill.getCustomParameters());
            }
            paymentgateway.charge(new Callback() {
                @Override
                public void onTaskexecuted(String success, String error) {
                    prepaidPayment(success, error);
                }
            });
        } else {
            UserDetails userDetails = new UserDetails(CitrusUser.toJSONObject(mCitrusUser));
            Bill bill = new Bill(billJSON);
            mTransactionId = bill.getTxnId();

            PG paymentgateway = new PG(mPaymentOption, bill, userDetails, dynamicPricingResponse);

            paymentgateway.charge(new Callback() {
                @Override
                public void onTaskexecuted(String success, String error) {
                    processresponse(success, error);
                }
            });
        }
    }

    private void processresponse(String response, String error) {

        TransactionResponse transactionResponse = null;
        if (!android.text.TextUtils.isEmpty(response)) {
            try {

                JSONObject redirect = new JSONObject(response);
                mpiServletUrl = redirect.optString("redirectUrl");

                if (!android.text.TextUtils.isEmpty(mpiServletUrl)) {

                    mPaymentWebview.loadUrl(mpiServletUrl);
                    if (mPaymentOption != null) {
                        EventsManager.logWebViewEvents(CitrusActivity.this, WebViewEvents.OPEN, mPaymentOption.getAnalyticsPaymentType()); //analytics event - WebView Event
                    }
                } else {
                    transactionResponse = new TransactionResponse(TransactionResponse.TransactionStatus.FAILED, response, mTransactionId);
                    sendResult(transactionResponse);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            transactionResponse = new TransactionResponse(TransactionResponse.TransactionStatus.FAILED, error, mTransactionId);
            sendResult(transactionResponse);
        }
    }

    private void prepaidPayment(String response, String error) {

        TransactionResponse transactionResponse = null;
        if (!android.text.TextUtils.isEmpty(response)) {
            try {

                JSONObject redirect = new JSONObject(response);
                if (!android.text.TextUtils.isEmpty(redirect.getString("redirectUrl"))) {
                    setCookie();

                    mPaymentWebview.loadUrl(redirect.getString("redirectUrl"));
                    if (mPaymentOption != null) {
                        EventsManager.logWebViewEvents(CitrusActivity.this, WebViewEvents.OPEN, mPaymentOption.getAnalyticsPaymentType()); //analytics event
                    }
                } else {
                    transactionResponse = new TransactionResponse(TransactionResponse.TransactionStatus.FAILED, response, mTransactionId);
                    sendResult(transactionResponse);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            transactionResponse = new TransactionResponse(TransactionResponse.TransactionStatus.FAILED, error, mTransactionId);
            sendResult(transactionResponse);
        }
    }

    private void showDialog(String message, boolean cancelable) {
        if (mProgressDialog != null) {
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(cancelable);
            mProgressDialog.setMessage(message);
            mProgressDialog.show();

            mShowingDialog = true;
        }
    }

    private void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();

            mShowingDialog = false;
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                isBackKeyPressedByUser = true;

                // If the PaymentType is CitrusCash or network is not available, finish the activity and mark the status as cancelled.
                // else load the url again so that Citrus can cancel the transaction and return the control to app normal way.
                if (mPaymentType instanceof PaymentType.CitrusCash || !Utils.isNetworkConnected(mContext)) {
                    TransactionResponse transactionResponse = new TransactionResponse(TransactionResponse.TransactionStatus.CANCELLED, "Cancelled By User", mTransactionId);
                    sendResult(transactionResponse);
                } else {
                    mPaymentWebview.loadUrl(mpiServletUrl);
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        // Set other dialog properties
        builder.setMessage("Do you want to cancel the transaction?")
                .setTitle("Cancel Transaction?");
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void setCookie() {
        cookieManager = CookieManager.getInstance();
        sessionCookie = new PersistentConfig(CitrusActivity.this).getCookieString();
        cookieManager.setCookie(Config.getBaseURL(), sessionCookie);
    }

    private static void removeCookies() {
        String setCookie = CookieManager.getInstance().getCookie(Config.getBaseURL());
        CookieManager.getInstance().setCookie(Config.getBaseURL(), Constants.CITRUS_PREPAID_COOKIE);
    }

    private void sendResult(TransactionResponse transactionResponse) {
        // Log the events
        if (mPaymentOption != null) {
            if (isBackKeyPressedByUser) {
                EventsManager.logWebViewEvents(CitrusActivity.this, WebViewEvents.BACK_KEY, mPaymentOption.getAnalyticsPaymentType()); //analytics event
            } else {
                EventsManager.logWebViewEvents(CitrusActivity.this, WebViewEvents.CLOSE, mPaymentOption.getAnalyticsPaymentType());//WebView close event
            }

            if (transactionResponse.getTransactionStatus() == TransactionResponse.TransactionStatus.FAILED) {
                EventsManager.logPaymentEvents(CitrusActivity.this, mPaymentOption.getAnalyticsPaymentType(), transactionResponse.getMessage());//Payment Events
            } else {
                EventsManager.logPaymentEvents(CitrusActivity.this, mPaymentOption.getAnalyticsPaymentType(), transactionResponse.getAnalyticsTransactionType());//Payment Events
            }
        }

        // Send the response to the caller.
        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_EXTRA_TRANSACTION_RESPONSE, transactionResponse);

        // According new implementation, finish the activity and post the event to citrusClient.
        intent.setAction(mPaymentType.getIntentAction());
        // Send the broadcast for normal requets.
        if (mRequestCode != Constants.REQUEST_CODE_PAYMENT) {
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        dismissDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mPaymentWebview != null) {
            mPaymentWebview.stopLoading();
            mPaymentWebview.destroy();
        }
        mPaymentWebview = null;
        mPaymentType = null;
        mPaymentParams = null;
        mCitrusConfig = null;
        mCitrusUser = null;
        mTransactionId = null;

        dismissDialog();
        mProgressDialog = null;
        mPaymentOption = null;
        mActivityTitle = null;
    }

    /**
     * Handle all the Webview loading in custom webview client.
     */
    private class CitrusWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.contains("/prepaid/pg/verify/")) {

                showPrompt();
                passwordPromptShown = true;

                return false;
            }

            // Let this webview handle all the urls loaded inside. Return false to denote that.
            view.loadUrl(url);

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // Display the message in case of cancelled transaction.
            if (isBackKeyPressedByUser) {
                // Show cancel message.
                showDialog(CANCEL_MESSAGE, true);
            } else if (!mShowingDialog) {
                // Show dialog is not already shown. Applies when the user clicks on 3DS page.
                showDialog(WAIT_MESSAGE, true);
            }

            mTimer.cancel();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            mTimer.start();
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // Dismiss Dialog
            dismissDialog();
            // Cancelling loading of the page.
            handler.cancel();
        }
    }

    /**
     * This class will be loaded as JSInterface and the methods of this class will be called from
     * the javascript loaded inside webview.
     * <p/>
     * Handle the payment response and take actions accordingly.
     */
    private class JsInterface {

        @JavascriptInterface
        public void pgResponse(String response) {

            Logger.d("PG Response :: " + response);

            if (mPaymentType instanceof PaymentType.CitrusCash) {
                removeCookies();
            }
            TransactionResponse transactionResponse = TransactionResponse.fromJSON(response, customParametersOriginalMap);
            sendResult(transactionResponse);
        }

        /**
         * This method will be called by returnURL when Cash is loaded in user's account
         *
         * @param response post parameters sent by Citrus
         */
        @JavascriptInterface
        public void loadWalletResponse(String response) {

            Logger.d("Wallet response :: " + response);

            TransactionResponse transactionResponse = TransactionResponse.parseLoadMoneyResponse(response);
            sendResult(transactionResponse);
        }

        @JavascriptInterface
        public void rawPGResponse(String response) {

            Logger.d("rawPGResponse :: " + response);

            TransactionResponse transactionResponse = new TransactionResponse(TransactionResponse.TransactionStatus.SUCCESSFUL, "", null);
            transactionResponse.setJsonResponse(response);
            sendResult(transactionResponse);
        }
    }

    private void showPrompt() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        String message = null;
        String title = null;

        if (passwordPromptShown) {
            message = "Incorrect Password.";
            title = "Please Enter Password Again.";
        } else {
            message = "Please Enter Your Password For Citrus Account.";
            title = "Enter Password";
        }

        String positiveButtonText = "Pay";

        alert.setTitle(title);
        alert.setMessage(message);
        // Set an EditText view to get user input
        final EditText input = new EditText(mContext);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        alert.setView(input);
        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                String password = input.getText().toString();

                if (!TextUtils.isEmpty(password)) {
                    mPaymentWebview.loadUrl("javascript:(function() { " +
                            "document.getElementById('password').value='" + password + "';" +
                            "document.getElementById(\"verify\").submit();" +
                            "}) ()");
                    input.clearFocus();
                    // Hide the keyboard.
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                    String emailId = mCitrusClient.getUserEmailId();

                    getCookie(emailId, password);

                    dialog.dismiss();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                onBackPressed();
            }
        });

        input.requestFocus();
        alert.show();
    }

    /**
     * Get the cookie and set the cookie so that password will not be asked next time using Pay Using Citrus Cash
     *
     * @return
     */
    private void getCookie(String emailId, String password) {

        mCitrusClient.getCookie(emailId, password, new com.citrus.sdk.Callback<CitrusResponse>() {
            @Override
            public void success(CitrusResponse citrusResponse) {

            }

            @Override
            public void error(CitrusError error) {

            }
        });
    }
}
