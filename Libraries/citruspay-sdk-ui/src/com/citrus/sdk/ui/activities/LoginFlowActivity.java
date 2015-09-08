package com.citrus.sdk.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.citrus.sdk.classes.CashoutInfo;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.fragments.WalletSignInFragment;
import com.citrus.sdk.ui.utils.CitrusFlowManager;
import com.citrus.sdk.ui.utils.ResultModel;
import com.citrus.sdk.ui.utils.UIConstants;

public class LoginFlowActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        theme = getIntent().getIntExtra(CitrusFlowManager.KEY_STYLE,-1);
        if(theme != -1) {
            setTheme(theme);
        }
        super.onCreate(savedInstanceState);
        email = getIntent().getStringExtra(CitrusFlowManager.KEY_EMAIL);
        mobile = getIntent().getStringExtra(CitrusFlowManager.KEY_MOBILE);
        payAmount = getIntent().getStringExtra(CitrusFlowManager.KEY_AMOUNT);
        mProgressDialog = new ProgressDialog(LoginFlowActivity.this);
        showLoginFragment();
    }

    private void showLoginFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container,new WalletSignInFragment()).addToBackStack
                (null).commit();
        screenStack.add(UIConstants.SCREEN_WALLET_LOGIN);
        setActionBarTitle(UIConstants.SCREEN_WALLET_LOGIN);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login_flow;
    }

    @Override
    public void onWalletTransactionComplete(ResultModel resultModel) {

    }

    @Override
    public void withdrawMoney(CashoutInfo cashoutInfo) {

    }

    @Override
    public void showAmount(String amount) {

    }

    @Override
    public void showWalletBalance(String amount) {

    }
    @Override
    public void toggleAmountVisibility(int visibility) {

    }


}
