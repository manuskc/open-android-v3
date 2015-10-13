package com.citrus.sdk.ui.activities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.events.FragmentCallbacks;
import com.citrus.sdk.ui.utils.AnimationType;
import com.citrus.sdk.ui.utils.UIConstants;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity  implements FragmentCallbacks {

    public Toolbar toolbar;
    public static final String  TAG = "BaseActivity$";
    protected String email = "" ,mobile = "", payAmount = "0";
    protected ArrayList<Integer> screenStack = new ArrayList<Integer>();
    protected ProgressDialog mProgressDialog = null;
    public boolean retry = false;
    int  theme = -1;
    public boolean isNewUser = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setBackButtonColor();
    }

    public void setBackButtonColor(){
        final Drawable upArrow = ContextCompat.getDrawable(this,R.drawable
                .abc_ic_ab_back_mtrl_am_alpha );
        upArrow.setColorFilter(UIConstants.actionBarItemColor,
                PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    protected abstract int getLayoutResource();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setSpannableTitle(String title) {
        SpannableString s = new SpannableString(title);
//        s.setSpan(new TypefaceSpan(this, "Raleway-SemiBold.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        s.setSpan(new ForegroundColorSpan(UIConstants.actionBarItemColor),0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
// Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);
    }

    public void animateActionbarColor(AnimationType animationType){
        final float[] from = new float[3],
                to =   new float[3];
        TypedValue typedValue = new  TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        final  int color = typedValue.data;
        if (animationType == AnimationType.ANIM_PLAY) {
            Color.colorToHSV(color, from);
            Color.colorToHSV(Color.parseColor("#414A5A"), to);
        }else{
            Color.colorToHSV(color, to);
            Color.colorToHSV(Color.parseColor("#414A5A"), from);
        }

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(300);                              // for 300 ms

        final float[] hsv  = new float[3];                  // transition color
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

                toolbar.setBackgroundColor(Color.HSVToColor(hsv));
            }
        });

        anim.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void navigateTo(Fragment fragment, int screenName) {
        screenStack.add(screenName);
        setActionBarTitle(screenName);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public String getEmail() {
        if (!TextUtils.isEmpty(email)) {
            return email;
        }else{
            return "";
        }
    }

    @Override
    public String getMobile() {
        if (!TextUtils.isEmpty(mobile)) {
            return mobile;
        }else{
            return "";
        }
    }

    @Override
    public String getAmount() {
        if (!TextUtils.isEmpty(payAmount)) {
            return payAmount;
        }else{
            return "";
        }
    }

    @Override
    public void showProgressDialog(boolean cancelable, String message) {
        if (mProgressDialog != null) {
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(cancelable);
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void makePayment(PaymentOption paymentOption){

    }

    @Override
    public void makeCardPayment(CardOption cardOption) {

    }

    protected void setActionBarTitle(int screenShopping) {
        Logger.d(TAG + " setActionBarTitle" + screenShopping);
        toggleAmountVisibility(View.VISIBLE);
        switch (screenShopping){
            case UIConstants.SCREEN_SHOPPING:
                setSpannableTitle(getString(R.string.text_shopmatic_tores));
                break;
            case UIConstants.SCREEN_CVV:
                setSpannableTitle("");
                animateActionbarColor(AnimationType.ANIM_PLAY);
                toggleAmountVisibility(View.GONE);
                break;
            case UIConstants.SCREEN_ADD_CARD:
                setSpannableTitle(getString(R.string.text_add_card));
                break;
            case UIConstants.SCREEN_BANK_LIST:
                setSpannableTitle(getString(R.string.text_bank_list));
                break;
            case UIConstants.SCREEN_WALLET_LOGIN:
                setSpannableTitle(getString(R.string.text_my_wallet));
                break;
            case UIConstants.SCREEN_OTP_CONFIRMATION:
                setSpannableTitle(getString(R.string.text_sign_in));
                break;
            case UIConstants.SCREEN_SIGNUP:
                setSpannableTitle(getString(R.string.text_sign_up));
                break;
            case UIConstants.SCREEN_ADD_MONEY:
                setSpannableTitle(getString(R.string.text_my_wallet));
                toggleAmountVisibility(View.GONE);
                showWalletBalance("");
                break;
            case UIConstants.SCREEN_WALLET:
                setSpannableTitle(getString(R.string.text_my_wallet));
                toggleAmountVisibility(View.GONE);
                break;
            case UIConstants.SCREEN_MONEY_OPTION:
                setSpannableTitle(getString(R.string.text_add_money));
                break;
            case UIConstants.SCREEN_CARD_LIST:
                setSpannableTitle(getString(R.string.text_manage_cards));
                toggleAmountVisibility(View.GONE);
                break;
            case UIConstants.SCREEN_ACCOUNT_DETAILS:
                setSpannableTitle(getString(R.string.text_withdraw_money));
                toggleAmountVisibility(View.GONE);
                break;

        }
    }

    public void displayTerms(Activity activity)
    {
        String uri= Uri.parse(UIConstants.TERMS_COND_URL).toString();
        Dialog dialog = new Dialog(activity,R.style.Base_Theme_AppCompat_Dialog);
        dialog.setContentView(R.layout.layout_terms_condition);
        WebView wb = (WebView) dialog.findViewById(R.id.webview);
        wb.getSettings().setJavaScriptEnabled(true);
        wb.loadUrl(uri);
        wb.setWebViewClient(new newWebViewClient());
        dialog.setCancelable(true);
        dialog.show();
    }

    private class newWebViewClient extends WebViewClient {
        @Override

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }



    @Override
    public int getStyle() {
        return theme;
    }

    @Override
    public void onBackPressed() {
        Logger.d(TAG+" onBackPressed");
        if (screenStack.size()>1) {
            Logger.d(TAG+"stack top = "+screenStack.get(screenStack.size()-1));
            Logger.d(TAG+"stack bot = "+screenStack.get(0));
            if (screenStack.get(screenStack.size()-1) == UIConstants.SCREEN_RESULT && !retry && screenStack.get(0) == UIConstants.SCREEN_SHOPPING){
                finish();
            }else{
                super.onBackPressed();
                if(screenStack.get(screenStack.size()-1) == UIConstants.SCREEN_CVV){
                    animateActionbarColor(AnimationType.ANIM_REVERSE);
                }
                screenStack.remove(screenStack.size()-1);
                setActionBarTitle(screenStack.get(screenStack.size()-1));
            }
        }else{
            finish();
        }
    }
}