package com.citrus.sdk.ui.events;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.citrus.sdk.classes.CashoutInfo;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.ui.utils.ResultModel;

/**
 * Created by akshaykoul on 6/10/15.
 */
public interface FragmentCallbacks {
    public void navigateTo(Fragment fragment,int screenName);
    public void makePayment(PaymentOption paymentOption);
    public void withdrawMoney(CashoutInfo cashoutInfo);
    public void makeCardPayment(CardOption cardOption);
    public void showProgressDialog(boolean cancelable, String message);
    public void dismissProgressDialog();
    public void onWalletTransactionComplete(ResultModel resultModel);
    public void showAmount(String amount);
    public void showWalletBalance(String amount);
    public String getEmail();
    public String getMobile();
    public String getAmount();
    public int getStyle();
    public void toggleAmountVisibility(int visibility);
    public void displayTerms(Activity activity);
}
