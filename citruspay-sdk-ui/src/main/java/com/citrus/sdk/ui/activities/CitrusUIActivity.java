package com.citrus.sdk.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.Constants;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CashoutInfo;
import com.citrus.sdk.classes.CitrusConfig;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.CreditCardOption;
import com.citrus.sdk.payment.DebitCardOption;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.PaymentResponse;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.fragments.QuickPayFragment;
import com.citrus.sdk.ui.fragments.ResultFragment;
import com.citrus.sdk.ui.fragments.WalletScreenFragment;
import com.citrus.sdk.ui.utils.AnimationType;
import com.citrus.sdk.ui.utils.CitrusFlowManager;
import com.citrus.sdk.ui.utils.ResultModel;
import com.citrus.sdk.ui.utils.UIConstants;
import com.citrus.sdk.ui.utils.Utils;
import com.orhanobut.logger.Logger;


public class CitrusUIActivity extends BaseActivity  {
    private int flow = 0;
    private TextView amountText;
    String balanceAmount = "";
    public TextView balanceAmountText;
    public LinearLayout balanceContainer;
    public static final String TAG = "CitrusActivity$";
    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        theme = getIntent().getIntExtra(CitrusFlowManager.KEY_STYLE,-1);
        if(theme != -1) {
            setTheme(theme);
        }
        super.onCreate(savedInstanceState);
        setSpannableTitle("Citrus UI");
        context = CitrusUIActivity.this;
        email = getIntent().getStringExtra(CitrusFlowManager.KEY_EMAIL);
        mobile = getIntent().getStringExtra(CitrusFlowManager.KEY_MOBILE);
        payAmount = getIntent().getStringExtra(CitrusFlowManager.KEY_AMOUNT);
        flow = getIntent().getIntExtra(CitrusFlowManager.KEY_FLOW, 0);
        amountText =  (TextView)findViewById(R.id.amount_text);
        balanceAmountText =  (TextView)findViewById(R.id.balance_amount);
        balanceContainer =  (LinearLayout)findViewById(R.id.balance_container);
        TypedValue typedValue = new  TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        final  int color = typedValue.data;
        amountText.setTextColor(color);
        amountText.setBackgroundResource(R.drawable.amount_background);
        mProgressDialog = new ProgressDialog(CitrusUIActivity.this);
        showAmount(payAmount);
        if (flow == UIConstants.QUICK_PAY_FLOW) {
            startQuickPayFlow();
        }else if(flow == UIConstants.WALLET_FLOW){
            startWalletFlow();
        }
        TypedValue typedValue_primary = new  TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue_primary, true);
        final  int primaryColor = typedValue_primary.data;
        String colorPrimary = String.format("#%06X", (primaryColor));

        TypedValue typedValue_dark = new  TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue_dark, true);
        final  int darkColor = typedValue_dark.data;
        String colorDark = String.format("#%06X", (darkColor));

        TypedValue typedValue_text = new  TypedValue();
        context.getTheme().resolveAttribute(UIConstants.actionBarItemColor, typedValue_text, true);
        final  int textColor = typedValue_dark.data;
        String colorText = String.format("#%06X", (textColor));

        CitrusConfig citrusConfig = CitrusConfig.getInstance();
        citrusConfig.setColorPrimary(colorPrimary);
        citrusConfig.setColorPrimaryDark(colorDark);
        citrusConfig.setTextColorPrimary(colorText);

    }


    private void startWalletFlow() {
        CitrusClient citrusClient = CitrusClient.getInstance(CitrusUIActivity.this);
        citrusClient.isUserSignedIn(new Callback<Boolean>() {
            @Override
            public void success(Boolean success) {
                if(success){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container,new WalletScreenFragment()).addToBackStack
                            (null).commit();
                    screenStack.add(UIConstants.SCREEN_WALLET);
                    setActionBarTitle(UIConstants.SCREEN_WALLET);
                }else{
                    Intent intent = new Intent(context, LoginFlowActivity.class);
                    intent.putExtra(CitrusFlowManager.KEY_EMAIL,email);
                    intent.putExtra(CitrusFlowManager.KEY_MOBILE,mobile);
                    intent.putExtra(CitrusFlowManager.KEY_STYLE,theme);
                    if (!TextUtils.isEmpty(payAmount)) {
                        intent.putExtra(CitrusFlowManager.KEY_AMOUNT,payAmount);
                    }
                    startActivityForResult(intent, UIConstants.REQ_CODE_LOGIN);
                }
            }

            @Override
            public void error(CitrusError error) {
                if (context!=null) {
                    Snackbar.make(findViewById(R.id.container), "could not get login status " + error.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void startQuickPayFlow() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, QuickPayFragment.newInstance(email,mobile, payAmount)).addToBackStack
                (null).commit();
        screenStack.add(UIConstants.SCREEN_SHOPPING);
//        setActionBarTitle(UIConstants.SCREEN_SHOPPING);
        setSpannableTitle(getString(R.string.text_shopmatic_tores));
    }


    @Override
    public void onWalletTransactionComplete(ResultModel resultModel) {
        Utils.getBalance(context);
        processAndShowResult(resultModel);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_citrus_ui;
    }

    public String getCustomerEmail(){
        return email;
    }
    public String getCustomerMobile(){
        return mobile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK) {
            TransactionResponse transactionResponse = data.getParcelableExtra(Constants.INTENT_EXTRA_TRANSACTION_RESPONSE);
            processAndShowResult(new ResultModel(null, transactionResponse));
        }else if(requestCode == UIConstants.REQ_CODE_LOGIN){
            if (resultCode == RESULT_OK) {
                Logger.d(TAG+" Login successful");
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container,new WalletScreenFragment()).addToBackStack
                        (null).commit();
                screenStack.add(UIConstants.SCREEN_WALLET);
                setActionBarTitle(UIConstants.SCREEN_WALLET);
            }else{
                finish();
            }
        }
    }

    private void processAndShowResult(ResultModel resultModel) {
        if (screenStack.get(screenStack.size() - 1) != UIConstants.SCREEN_SHOPPING
                || screenStack.get(screenStack.size() - 1) != UIConstants.SCREEN_WALLET) {
            if (!screenStack.isEmpty()) {
                if (screenStack.size() > 1) {
                    Logger.d(TAG+" Screen Size = " + screenStack.size());
                    for (int i = screenStack.size(); i > 1; i--) {
                        onBackPressed();
                    }
                }else{
                    Logger.d(TAG+" Screen Size = " + screenStack.size());
                }
            }
        }else{

        }
        if (!resultModel.isWithdraw()) {
            if (resultModel.getTransactionResponse() != null) {

                showResult(resultModel.getTransactionResponse().getTransactionStatus() ==
                        TransactionResponse
                        .TransactionStatus.SUCCESSFUL, resultModel);

            }else if(resultModel.getError() != null){
                showResult(false,resultModel);
            }
        }else{
            if (resultModel.getPaymentResponse() != null) {

                showResult(true, resultModel);

            }
        }
    }
    @Override
    public void showAmount(String amount){
        amountText.setVisibility(View.VISIBLE);
        balanceContainer.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(amount)) {
            if (amount.equals("0")) {
                amountText.setVisibility(View.GONE);
            }else{
                amountText.setText(getString(R.string.rs)+" "+ amount);
            }
        }else{

            if (!TextUtils.isEmpty(payAmount)) {
                if (payAmount.equals("0")) {
                    amountText.setVisibility(View.GONE);
                }else{
                    amountText.setText(getString(R.string.rs)+" "+ payAmount);
                }
            }else{
                amountText.setVisibility(View.GONE);
            }
        }
    }
    @Override
    public void showWalletBalance(String amount){
        if (!TextUtils.isEmpty(amount)) {
            amountText.setVisibility(View.GONE);
            balanceAmount = amount;
            balanceAmountText.setText(getString(R.string.rs) + " " + amount);
            balanceContainer.setVisibility(View.VISIBLE);
        }else{
            if(!TextUtils.isEmpty(balanceAmount)){
                balanceAmountText.setText(getString(R.string.rs) + " " + balanceAmount);
                balanceContainer.setVisibility(View.VISIBLE);
            }else{

                balanceContainer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void toggleAmountVisibility(int visibility) {
        if (amountText!=null) {
            amountText.setVisibility(visibility);
        }
    }

    public String getPayAmount() {
        return payAmount;
    }



    private void showResult(boolean result,ResultModel resultModel){
        if(screenStack.get(screenStack.size()-1) == UIConstants.SCREEN_CVV){
            animateActionbarColor(AnimationType.ANIM_REVERSE);
        }
        screenStack.add(UIConstants.SCREEN_RESULT);
//        setActionBarTitle(UIConstants.SCREEN_RESULT);
        if(result){
            payAmount = "0";
            showAmount(payAmount);
            setSpannableTitle(getString(R.string.text_all_done));
        }else{
            setSpannableTitle(getString(R.string.text_error));
        }
        navigateTo(ResultFragment.newInstance(result, resultModel),UIConstants.SCREEN_RESULT);
    }



    @Override
    public void makePayment(PaymentOption paymentOption){
        CitrusUser citrusUser = new CitrusUser(email,mobile);
        Amount amount = new Amount(payAmount);
        try {
            PaymentType paymentType = new PaymentType.PGPayment(amount, CitrusFlowManager.billGenerator,paymentOption,citrusUser);
            Intent intent = new Intent(this, com.citrus.sdk.CitrusActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_PAYMENT_TYPE, paymentType);
            startActivityForResult(intent, Constants.REQUEST_CODE_PAYMENT);
        } catch (CitrusException e) {
            Log.e(TAG,"CitrusException"+e.getMessage());
        }
    }

    @Override
    public void withdrawMoney(CashoutInfo cashoutInfo) {
        if(cashoutInfo!=null){
            showProgressDialog(false, "Requesting...");
            CitrusClient.getInstance(CitrusUIActivity.this).cashout(cashoutInfo, new Callback<PaymentResponse>() {


                @Override
                public void success(PaymentResponse paymentResponse) {
                    dismissProgressDialog();
                    Utils.getBalance(CitrusUIActivity.this);
                    processAndShowResult(new ResultModel(paymentResponse,
                            null, true));
                }

                @Override
                public void error(CitrusError error) {
                    dismissProgressDialog();
                    Snackbar.make(findViewById(R.id.container),"error "+error.getMessage(),Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void makePayment(PaymentOption paymentOption,int requestCode){
        CitrusUser citrusUser = new CitrusUser(email,mobile);
        Amount amount = new Amount(payAmount);
        try {
            PaymentType paymentType = new PaymentType.PGPayment(amount, CitrusFlowManager.billGenerator,paymentOption,citrusUser);
            Intent intent = new Intent(this, com.citrus.sdk.CitrusActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_PAYMENT_TYPE, paymentType);
            startActivityForResult(intent, requestCode);
        } catch (CitrusException e) {
            Log.e(TAG,"CitrusException"+e.getMessage());
        }
    }
    @Override
    public void makeCardPayment(CardOption cardOption) {
        if(cardOption instanceof DebitCardOption){
            Logger.d(TAG+" is Debit card");
            DebitCardOption debitCardOption = (DebitCardOption)cardOption;
            makePayment(debitCardOption);


        }else if (cardOption instanceof CreditCardOption){
            Logger.d(TAG+" is Credit card");
            CreditCardOption creditCardOption = (CreditCardOption)cardOption;
            makePayment(creditCardOption);

        }
    }
}
