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

import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.events.BalanceUpdateEvent;
import com.citrus.sdk.ui.events.FragmentCallbacks;
import com.citrus.sdk.ui.utils.UIConstants;
import com.citrus.sdk.ui.utils.Utils;
import com.orhanobut.logger.Logger;

import de.greenrobot.event.EventBus;


public class AddMoneyFragment extends Fragment {

    private boolean addMoneyNPay = false;
    public static final String TAG = "AddMoneyFragment$";
    EditText amountEt;
    private FragmentCallbacks mListener;
//    TextView walletTextView;
    String walletAmount;
    double walletBalance = 0F;
    View root;
    public static AddMoneyFragment newInstance(boolean addMoneyNPay,String walletAmount) {
        AddMoneyFragment fragment = new AddMoneyFragment();
        Bundle args = new Bundle();
        args.putBoolean(UIConstants.ARG_IS_ADD_MONEY_AND_PAY, addMoneyNPay);
        args.putString(UIConstants.ARG_WALLET_BALANCE, walletAmount);
        fragment.setArguments(args);
        return fragment;
    }

    public AddMoneyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.showWalletBalance(Double.toString(walletBalance));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            addMoneyNPay = getArguments().getBoolean(UIConstants.ARG_IS_ADD_MONEY_AND_PAY);
            walletAmount = getArguments().getString(UIConstants.ARG_WALLET_BALANCE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_add_money, container, false);
        EventBus.getDefault().register(this);
        amountEt = (EditText) root.findViewById(R.id.amount_et);

        getBalance();
//        walletTextView = (TextView)root.findViewById(R.id.text_balance_amount);
//        walletTextView.setText(getString(R.string.rs) + " " + walletAmount);
        mListener.showWalletBalance(walletAmount);
        root.findViewById(R.id.button_add_100).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAmount(100);
            }
        });
        root.findViewById(R.id.button_add_500).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setAmount(500);
            }
        });
        root.findViewById(R.id.button_add_1000).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAmount(1000);
            }
        });
        root.findViewById(R.id.button_add_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO use citrusClient.LoadMoney Here
                addMoney();
            }
        });

        amountEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Logger.d("Enter pressed");
                    addMoney();
                }
                return false;
            }
        });

        if(addMoneyNPay){
            amountEt.setText(mListener.getAmount());
        }

        return root;

    }

    private void addMoney() {
        String amount = amountEt.getText().toString().trim();
        double amountDouble = 0F;
        double payAmount = 0F;
        if (!TextUtils.isEmpty(amount)) {
            if (Utils.isValidWithdrawAmount(amount)) {
                try {
                    amountDouble = Double.parseDouble(amount);
                    if (!TextUtils.isEmpty(mListener.getAmount())) {
                        payAmount = Double.parseDouble(mListener.getAmount());
                    }
                    if (addMoneyNPay) {
                        if (amountDouble < (payAmount-walletBalance)) {
                            Snackbar.make(root, getString(R.string.less_amount_msg), Snackbar
                                    .LENGTH_SHORT).show();
                            amountEt.requestFocus();
                        }else{
                            mListener.navigateTo(AddMoneyOptionsFragment.newInstance(addMoneyNPay, amount), UIConstants.SCREEN_MONEY_OPTION);
                        }
                    }else{
                        mListener.navigateTo(AddMoneyOptionsFragment.newInstance(addMoneyNPay, amount), UIConstants.SCREEN_MONEY_OPTION);
                    }
                } catch (NumberFormatException e) {
                    Logger.e("NumberFormatException" + e.getStackTrace());
                    Snackbar.make(root, getString(R.string.valid_amount_msg), Snackbar.LENGTH_SHORT).show();
                    amountEt.requestFocus();
                }
            }else{
                Snackbar.make(root, R.string.excess_amount_msg, Snackbar.LENGTH_SHORT).show();
                amountEt.requestFocus();
            }
        }else{
            Snackbar.make(root, "Please enter amount first.", Snackbar.LENGTH_SHORT).show();
            amountEt.requestFocus();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void getBalance() {
        if (isAdded()) {
            mListener.showProgressDialog(false, getString(R.string.load_wallet_balance));
            Utils.getBalance(getActivity());
        }
    }

    private void showWalletDetails(Amount amount) {
//        walletTextView.setText(getString(R.string.rs) + " " + amount.getValue());
        mListener.showWalletBalance(amount.getValue());
        try {
            walletBalance = Double.parseDouble(amount.getValue());
        } catch (NumberFormatException e) {
            walletBalance = 0F;
            Logger.e(TAG + " NumberFormatException ", e);
        }
        if(addMoneyNPay){
            if (Double.parseDouble(mListener.getAmount())>walletBalance) {
                amountEt.setText(Double.toString(Double.parseDouble(mListener.getAmount()) -
                        walletBalance));
            }
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    public void onEvent(BalanceUpdateEvent event) {
        if(isAdded()){
            mListener.dismissProgressDialog();
            if (event.isSuccess()) {
                showWalletDetails(event.getAmount());
            }
        }
    }

    private void setAmount(int additionalAmount) {
        String orignalAmount = amountEt.getText().toString().trim();
        double orgAmount = 0F;
        if (!TextUtils.isEmpty(orignalAmount)) {
            try {
                orgAmount = Double.parseDouble(orignalAmount);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Snackbar.make(root, getString(R.string.valid_amount_msg), Snackbar.LENGTH_SHORT).show();
                amountEt.requestFocus();
            }
        }
        if (additionalAmount > 0) {
            double totalAmount = orgAmount + additionalAmount;
            if (totalAmount > UIConstants.MAX_WALLET_AMOUNT_RECHARGE) {
                totalAmount = UIConstants.MAX_WALLET_AMOUNT_RECHARGE;
            }
            amountEt.setText(Double.toString(totalAmount));
            amountEt.setSelection(amountEt.getText().length());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mListener.showAmount("");
    }
}
