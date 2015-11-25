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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.InputType;
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
import android.widget.ImageView;

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
import com.citrus.sdk.classes.BinServiceResponse;
import com.citrus.sdk.classes.CitrusConfig;
import com.citrus.sdk.classes.Utils;
import com.citrus.sdk.dynamicPricing.DynamicPricingResponse;
import com.citrus.sdk.otp.NetBankForOTP;
import com.citrus.sdk.otp.OTPPopupView;
import com.citrus.sdk.otp.OTPViewListener;
import com.citrus.sdk.otp.SMSReceiver;
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

public class CitrusActivity extends ActionBarActivity implements OTPViewListener {

    private final int WAIT_TIME = 300;
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
    private PaymentBill mPaymentBill = null;

    // Auto OTP
    private SMSReceiver mSMSReceiver = null;
    private BroadcastReceiver mAutoOtpSMSReceiveListener = null;
    private OTPPopupView mOTPPopupView = null;
    private String otpProcessTransactionJS = null;
    private ImageView otpPopupCancelImgView = null;
    private boolean autoOTPEnabled = false;
    private NetBankForOTP netBankForOTP = NetBankForOTP.UNKNOWN;
    private String otp;
    private static final long OTP_READ_TIMEOUT = 45000;
    private boolean transactionProcessed = false;
    private boolean mMultipartSendOTPJS = false;
    private boolean mMultipartEnterPasswordJS = false;
    private boolean useNewAPI = false;
    private boolean otpPopupDismissed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mPaymentType = getIntent().getParcelableExtra(Constants.INTENT_EXTRA_PAYMENT_TYPE);
        mRequestCode = getIntent().getIntExtra(Constants.INTENT_EXTRA_REQUEST_CODE_PAYMENT, -1);
        useNewAPI = getIntent().getBooleanExtra(Constants.INTENT_EXTRA_USE_NEW_API, false);

        // Initialize CitrusClient.
        mCitrusClient = CitrusClient.getInstance(mContext);

        // Initialize things.
        autoOTPEnabled = mCitrusClient.isAutoOtpReading();

        if (!(mPaymentType instanceof PaymentType.CitrusCash)) {
            setTheme(R.style.Base_Theme_AppCompat_Light_DarkActionBar);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_citrus);

        mOTPPopupView = (OTPPopupView) findViewById(R.id.otpPopupViewId);
        otpPopupCancelImgView = (ImageView) findViewById(R.id.otpPopupCancelImgViewId);
        otpPopupCancelImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOTPPopupView.getOtpViewToggleStatus()) {
                    mOTPPopupView.setVisibility(View.VISIBLE);
                    mOTPPopupView.setOtpViewToggleStatus(false);
                    otpPopupCancelImgView.setBackgroundResource(R.drawable.arrow_down_icon);
                    findViewById(R.id.otpPopupSeparatorId).setVisibility(View.VISIBLE);
                    // Show the OTP Popup Overlay i.e. dark grey portion on the back
                    findViewById(R.id.otpPopupOverlayId).setVisibility(View.VISIBLE);

                } else {
                    mOTPPopupView.setVisibility(View.GONE);
                    mOTPPopupView.setOtpViewToggleStatus(true);
                    otpPopupCancelImgView.setBackgroundResource(R.drawable.arrow_up_icon);
                    findViewById(R.id.otpPopupSeparatorId).setVisibility(View.GONE);
                    // Hide the OTP Popup Overlay i.e. dark grey portion on the back
                    findViewById(R.id.otpPopupOverlayId).setVisibility(View.GONE);
                }

            }
        });
        mOTPPopupView.setListener(this);
        mSMSReceiver = new SMSReceiver();
        mAutoOtpSMSReceiveListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                autoOtpReceived(intent);
            }
        };

        dynamicPricingResponse = getIntent().getParcelableExtra(Constants.INTENT_EXTRA_DYNAMIC_PRICING_RESPONSE);
        mPaymentParams = getIntent().getParcelableExtra(Constants.INTENT_EXTRA_PAYMENT_PARAMS);
        mCitrusConfig = CitrusConfig.getInstance();
        mActivityTitle = mCitrusConfig.getCitrusActivityTitle();

        initializeTimer();

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

        registerSMSReceivers();

        CitrusUser citrusUser = mCitrusClient.getCitrusUser();
        String emailId = mCitrusClient.getUserEmailId();
        String mobileNo = mCitrusClient.getUserMobileNumber();

        // Set the citrusUser.
        // Use details from the token in case of load money
        if (mPaymentType instanceof PaymentType.LoadMoney || mPaymentType instanceof PaymentType.CitrusCash) {
            if (citrusUser != null) {
                mCitrusUser = citrusUser;
            } else if (mCitrusUser == null) {
                mCitrusUser = new CitrusUser(emailId, mobileNo);
            }
        } else {
            // In case of PG Payment, send the merchant values.
            if (mCitrusUser == null) {
                // If the user details from token are available use those, put the details sent by the merchant while bind.
                if (citrusUser != null) {
                    mCitrusUser = citrusUser;
                } else {
                    mCitrusUser = new CitrusUser(emailId, mobileNo);
                }
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

        // Get BIN Details required for autoOTP
        if (autoOTPEnabled && mPaymentOption instanceof CardOption) {
            fetchBinRequestData((CardOption) mPaymentOption);
        }

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
                mPaymentBill = mPaymentType.getPaymentBill();

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
            PG paymentgateway = new PG(mCitrusClient.getEnvironment(), mPaymentOption, loadMoney, new UserDetails(CitrusUser.toJSONObject(mCitrusUser)));

            showDialog(WAIT_MESSAGE, true);

            paymentgateway.load(CitrusActivity.this, new Callback() {
                @Override
                public void onTaskexecuted(String success, String error) {
                    processresponse(success, error);
                }
            });
        }

        // Set the title for the activity
        if (TextUtils.isEmpty(mActivityTitle)) {
            mActivityTitle = "Processing...";
        }

        setTitle(Html.fromHtml("<font color=\"" + mTextColorPrimary + "\">" + mActivityTitle + "</font>"));
        setActionBarBackground();

        // Enable webContentDebugging, only for apps in debug mode.
        enableWebContentDebugging();
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerSMSReceivers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAutoOtpSMSReceiveListener != null)
            unregisterSMSReceivers();
    }

    private void initializeTimer() {
        // Timer to dismiss dialog after specific time once the url loading is complete.
        mTimer = new CountDownTimer(WAIT_TIME, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (!mLoading) {
                    dismissDialog();
                    if (!transactionProcessed && !mMultipartEnterPasswordJS && !isBackKeyPressedByUser) {
                        displayOtpPopup();
                    }

                    // If the sendOTP js is multipart, load the js.
                    if (mMultipartSendOTPJS) {
                        mPaymentWebview.loadUrl(netBankForOTP.getMultiPartSendOTPJS());
                        mMultipartSendOTPJS = false;
                    }

                    // If the sendOTP js is multipart, load the js.
                    if (mMultipartEnterPasswordJS) {
                        mPaymentWebview.loadUrl(netBankForOTP.getMultiPartEnterPasswordJS());
                        mMultipartEnterPasswordJS = false;
                    }

                    // Set the title since the transaction is done.
                    setTitle(Html.fromHtml("<font color=\"" + mTextColorPrimary + "\"> 3D Secure </font>"));
                }
            }
        };
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
                // PaymentBill required for the later use.
                mPaymentBill = paymentBill;

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
            PG paymentgateway = new PG(mCitrusClient.getEnvironment(), prepaid, bill, userDetails);
            if (bill.getCustomParameters() != null) {
                paymentgateway.setCustomParameters(bill.getCustomParameters());
            }
            paymentgateway.charge(new Callback() {
                @Override
                public void onTaskexecuted(String success, String error) {
                    prepaidPayment(success, error);
                }
            }, false);
        } else {
            UserDetails userDetails = new UserDetails(CitrusUser.toJSONObject(mCitrusUser));
            Bill bill = new Bill(billJSON);
            mTransactionId = bill.getTxnId();

            PG paymentgateway = new PG(mCitrusClient.getEnvironment(), mPaymentOption, bill, userDetails, dynamicPricingResponse);

            paymentgateway.charge(new Callback() {
                @Override
                public void onTaskexecuted(String success, String error) {
                    processresponse(success, error);
                }
            }, useNewAPI);
        }
    }

    private void processresponse(String response, String error) {

        TransactionResponse transactionResponse = null;
        if (!android.text.TextUtils.isEmpty(response)) {
            if (useNewAPI) {
                // Loading html directly
                mPaymentWebview.loadDataWithBaseURL(mCitrusClient.getEnvironment().getBaseCitrusUrl(), response, "text/html", "utf-8", null);
            } else {
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
            } // else useNewAPI ends.
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

    private void registerSMSReceivers() {
        // Register receivers only if the autoOTP is enabled and payment mode is Credit/Debit Card.
        if (autoOTPEnabled && mPaymentOption instanceof CardOption) {
            Logger.d("Registering SMS receivers");

            if (mSMSReceiver == null) {
                mSMSReceiver = new SMSReceiver();
            }

            if (mAutoOtpSMSReceiveListener == null) {
                mAutoOtpSMSReceiveListener = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        autoOtpReceived(intent);
                    }
                };
            }

            IntentFilter intentFilter = new IntentFilter(Constants.ACTION_SMS_RECEIVED);
            intentFilter.setPriority(Constants.SMS_RECEIVER_PRIORITY);
            registerReceiver(mSMSReceiver, intentFilter);
            LocalBroadcastManager.getInstance(this).registerReceiver(mAutoOtpSMSReceiveListener, new IntentFilter(Constants.ACTION_AUTO_READ_OTP));

        }

    }

    private void unregisterSMSReceivers() {

        // Unregister receivers only if the autoOTP is enabled and payment mode is Credit/Debit Card.
        if (autoOTPEnabled && mPaymentOption instanceof CardOption) {

            Logger.d("Unregistering SMS receivers");

            if (mSMSReceiver != null) {
                unregisterReceiver(mSMSReceiver);
                mSMSReceiver = null;
            }

            if (mAutoOtpSMSReceiveListener != null) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mAutoOtpSMSReceiveListener);
                mAutoOtpSMSReceiveListener = null;
            }
        }
    }

    private void autoOtpReceived(Intent intent) {
        otp = intent.getStringExtra(Constants.INTENT_EXTRA_AUTO_OTP);
        otpProcessTransactionJS = String.format(netBankForOTP.getTransactionJS(), otp);

        Logger.d("OTP : %s, js : %s", otp, otpProcessTransactionJS);
        // This is done to avoid,
        // when user cancels transaction, so OTP Dialog is dismissed.
        // After this, OTP is received and trying to set on a Null reference field.
        if (!otpPopupDismissed) {
            mOTPPopupView.setOTP(otp);
        }
    }

    private void displayOtpPopup() {
        // Display popup only if the autoOTP is enabled and payment mode is Credit/Debit Card.
        if (autoOTPEnabled && mPaymentOption instanceof CardOption && netBankForOTP != NetBankForOTP.UNKNOWN) {

            // Prevent the activity from sleeping.
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            // Show the Overlay on the otp popup i.e. dark gray screen
            findViewById(R.id.otpPopupOverlayId).setVisibility(View.VISIBLE);

            // Show OTP Popup Layout
            findViewById(R.id.otp_popup_layout).setVisibility(View.VISIBLE);

            // If in case of few banks there is no option to enter password, only otp is directly triggered, so hide the enter password button.
            if (netBankForOTP.isBypassEnterPasswordButton()) {
                mOTPPopupView.enableEnterPasswordButton(false);
            }

            // If in case of few banks the otp is directly triggered, so hide the send OTP button.
            if (netBankForOTP.isBypassSendOTPButton()) {
                mOTPPopupView.displayOtpAutoDetectPopup();
                startOtpReadTimer();
            }
        }
    }

    private void dismissOtpPopup() {
        // Hide the Overlay on the otp popup i.e. dark gray screen
        findViewById(R.id.otpPopupOverlayId).setVisibility(View.GONE);

        // Hide the OTP Popup.
        findViewById(R.id.otp_popup_layout).setVisibility(View.GONE);

        otpPopupDismissed = true;
    }

    private void fetchBinRequestData(CardOption cardOption) {
        mCitrusClient.getBINDetails(cardOption, new com.citrus.sdk.Callback<BinServiceResponse>() {
            @Override
            public void success(BinServiceResponse binServiceResponse) {
                netBankForOTP = binServiceResponse.getNetBankForOTP();
                Logger.d("netbankForOTP : " + netBankForOTP);

                mOTPPopupView.setNetBankForOTP(netBankForOTP);
            }

            @Override
            public void error(CitrusError error) {
                // NOOP
            }
        });
    }

    private void enableWebContentDebugging() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        handleCancelTransaction();
    }

    private void handleCancelTransaction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                isBackKeyPressedByUser = true;

                // Set the title since the transaction is done.
                setTitle(Html.fromHtml("<font color=\"" + mTextColorPrimary + "\"> Cancelling... </font>"));

                if (useNewAPI) {
                    String vanity = mCitrusClient.getVanity();
                    String postData = Utils.getURLEncodedParamsForCancelTransaction(mCitrusUser, mPaymentBill, mPaymentOption, dynamicPricingResponse, vanity);
                    Environment environment = mCitrusClient.getEnvironment();
                    mPaymentWebview.postUrl(environment.getCancelUrl(vanity), postData.getBytes());

                    dismissOtpPopup();
                } else {
                    // If the PaymentType is CitrusCash or network is not available, finish the activity and mark the status as cancelled.
                    // else load the url again so that Citrus can cancel the transaction and return the control to app normal way.
                    if (mPaymentType instanceof PaymentType.CitrusCash || !Utils.isNetworkConnected(mContext)) {
                        TransactionResponse transactionResponse = new TransactionResponse(TransactionResponse.TransactionStatus.CANCELLED, "Cancelled By User", mTransactionId);
                        sendResult(transactionResponse);
                    } else {
                        mPaymentWebview.loadUrl(mpiServletUrl);

                        dismissOtpPopup();
                    }
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
        mPaymentBill = null;
        mPaymentWebview = null;
        mPaymentType = null;
        mPaymentParams = null;
        mCitrusConfig = null;
        mCitrusUser = null;
        mTransactionId = null;

        dismissDialog();
        netBankForOTP = NetBankForOTP.UNKNOWN;
        mProgressDialog = null;
        mPaymentOption = null;
        mActivityTitle = null;
        transactionProcessed = false;
        mMultipartSendOTPJS = false;
    }

    @Override
    public void onSendOtpClicked() {
        mMultipartSendOTPJS = netBankForOTP.isMultipartSendOTPJS();

        mPaymentWebview.loadUrl(netBankForOTP.getSendOTPJS());
        mOTPPopupView.displayOtpAutoDetectPopup();
        startOtpReadTimer();
    }

    @Override
    public void onEnterPasswordClicked() {

        mMultipartEnterPasswordJS = netBankForOTP.isMultipartEnterPasswordJS();

        String enterPwdJS = netBankForOTP.getEnterPasswordJS();
        mPaymentWebview.loadUrl(enterPwdJS);

        // Hide the OTP PopUp View.
        dismissOtpPopup();
    }

    @Override
    public void onCancelClicked() {
        handleCancelTransaction();
    }

    @Override
    public void onProcessTransactionClicked(String otp) {
        // Set OTP on bank's page.
        mPaymentWebview.loadUrl(netBankForOTP.getSetOTPJS(otp));

        String js = String.format(netBankForOTP.getTransactionJS(), otp);
        mPaymentWebview.loadUrl(js);

        transactionProcessed = true;

        // Hide the popup since proceeding with transaction.
        dismissOtpPopup();
    }

    @Override
    public void onResendOTPClicked() {
        mPaymentWebview.loadUrl(netBankForOTP.getReSendOTPJS());

        // Register sms receivers
        registerSMSReceivers();
        startOtpReadTimer();
    }

    @Override
    public void startOtpReadTimer() {
        mOTPPopupView.handleResendOTP();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                unregisterSMSReceivers();
                mOTPPopupView.otpReadTimeout();
            }
        }, OTP_READ_TIMEOUT);
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

            // If the data is loaded for the dummy screen, then do not dismiss the dialog, i.e. do not start the timer to dismiss the dialog.
            // This case will occur only once, and if citrusClient.isShowDummyScreenWhilePayments is true.
            // Not inverting the condition for code readability and understanding.
            if (mCitrusClient.isShowDummyScreenWhilePayments() && url.startsWith("data:text/html")) {
                // Do nothing i.e. do not start the timer.
            } else {
                mTimer.start();
            }
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
                input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

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
