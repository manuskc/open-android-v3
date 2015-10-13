package com.citrus.sdk.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CashoutInfo;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.events.FragmentCallbacks;
import com.citrus.sdk.ui.utils.Utils;
import com.citrus.sdk.ui.widgets.CitrusButton;
import com.orhanobut.logger.Logger;


public class GetAccountDetailFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "GetAccountDetailFragment$";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View layout;
    private FragmentCallbacks mListener;
    private android.widget.EditText ownerNameEt;
    private android.widget.EditText accountNoEt;
    private android.widget.EditText ifscCodeEt;
    private android.widget.EditText amountEt;
    private com.citrus.sdk.ui.widgets.CitrusButton buttonWithdrawMoney;
    CashoutInfo cashoutInfo;
    String ownerName,accountNumber,ifscCode,withdrawAmount;


    public static GetAccountDetailFragment newInstance(String param1, String param2) {
        GetAccountDetailFragment fragment = new GetAccountDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public GetAccountDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_get_account_detail, container, false);
        this.buttonWithdrawMoney = (CitrusButton) layout.findViewById(R.id.button_withdraw_money);
        this.amountEt = (EditText) layout.findViewById(R.id.amount_et);
        this.ifscCodeEt = (EditText) layout.findViewById(R.id.ifsc_code_et);
        this.accountNoEt = (EditText) layout.findViewById(R.id.account_no_et);
        this.ownerNameEt = (EditText) layout.findViewById(R.id.owner_name_et);
        buttonWithdrawMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateDetails()) {
                    saveAccountInfo();
                }
            }
        });
        amountEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Logger.d(TAG + " Enter pressed");
                    if (validateDetails()) {
                        saveAccountInfo();
                    }
                }
                return false;
            }
        });
        autofillDetails();
        return layout;
    }

    private void autofillDetails() {
        mListener.showProgressDialog(true, getString(R.string.getting_account_info));
        CitrusClient.getInstance(getActivity()).getCashoutInfo(new Callback<CashoutInfo>() {
            @Override
            public void success(CashoutInfo cashoutInfo) {
                mListener.dismissProgressDialog();
                Logger.d(TAG + " Got account Info");
                ifscCodeEt.setText(cashoutInfo.getIfscCode());
                accountNoEt.setText(cashoutInfo.getAccountNo());
                ownerNameEt.setText(cashoutInfo.getAccountHolderName());

                if (TextUtils.isEmpty(cashoutInfo.getAccountHolderName())) {
                    Utils.openKeyboard(ownerNameEt);
                } else {

                    Utils.openKeyboard(amountEt);
                }
            }

            @Override
            public void error(CitrusError error) {
                mListener.dismissProgressDialog();
                Logger.d(TAG + " Account info error " + error.getMessage());
                Utils.openKeyboard(ownerNameEt);
            }
        });
    }

    private void saveAccountInfo() {
        Logger.d(TAG+" saving Account Info");
        cashoutInfo =  new CashoutInfo(new Amount(withdrawAmount),accountNumber,ownerName, ifscCode);
        mListener.showProgressDialog(false, getString(R.string.saving_account_info));
        CitrusClient.getInstance(getActivity()).saveCashoutInfo(cashoutInfo, new
                Callback<CitrusResponse>() {

                    @Override
                    public void success(CitrusResponse citrusResponse) {
                        mListener.dismissProgressDialog();
                        withdrawMoney();
                    }

                    @Override
                    public void error(CitrusError error) {
                        mListener.dismissProgressDialog();
                        Snackbar.make(layout, "error " + error.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void withdrawMoney() {
        Logger.d(TAG+" Withdrawing money");
        mListener.withdrawMoney(cashoutInfo);
    }

    private boolean validateDetails() {
        ownerName = ownerNameEt.getText().toString().trim();
        accountNumber = accountNoEt.getText().toString().trim();
        ifscCode = ifscCodeEt.getText().toString().trim();
        withdrawAmount = amountEt.getText().toString().trim();
        if(TextUtils.isEmpty(ownerName)){
            Snackbar.make(layout,R.string.err_account_holder_name_empty,Snackbar.LENGTH_SHORT).show();
            ownerNameEt.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(accountNumber)){
            Snackbar.make(layout,R.string.err_account_number_empty,Snackbar.LENGTH_SHORT).show();
            accountNoEt.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(ifscCode)){
            Snackbar.make(layout,R.string.err_ifsc_code_empty,Snackbar.LENGTH_SHORT).show();
            ifscCodeEt.requestFocus();
            return false;
        }else if(!isValidIfscCode(ifscCode)){
            Snackbar.make(layout,R.string.err_ifsc_code_wrong,Snackbar.LENGTH_SHORT).show();
            ifscCodeEt.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(withdrawAmount)){
            Snackbar.make(layout,R.string.valid_amount_msg,Snackbar.LENGTH_SHORT).show();
            amountEt.requestFocus();
            return false;
        }
        else if(!Utils.isValidWithdrawAmount(withdrawAmount)) {
            Snackbar.make(layout, R.string.excess_amount_msg, Snackbar.LENGTH_SHORT).show();
            amountEt.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isValidIfscCode(String ifscCode) {
        Logger.d(TAG + " checking bank Code validity");
        return ifscCode.length() <= 11 && Utils.isValidIFSC(ifscCode);
    }
    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideKeyboard(getActivity());
    }
}
