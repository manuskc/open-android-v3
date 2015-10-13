package com.citrus.sdk.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.activities.CitrusUIActivity;
import com.citrus.sdk.ui.events.BalanceUpdateEvent;
import com.citrus.sdk.ui.events.FragmentCallbacks;
import com.citrus.sdk.ui.utils.UIConstants;
import com.citrus.sdk.ui.utils.Utils;

import java.text.DecimalFormat;

import de.greenrobot.event.EventBus;


public class WalletScreenFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    public static final String TAG = "WalletScreenFragment$";
    TextView walletTextView;
    String walletAmount ="";

    private FragmentCallbacks mListener;


    public static WalletScreenFragment newInstance(String param1, String param2) {
        WalletScreenFragment fragment = new WalletScreenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WalletScreenFragment() {
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
        View root = inflater.inflate(R.layout.fragment_wallet_screen, container, false);
        EventBus.getDefault().register(this);
        walletTextView = (TextView)root.findViewById(R.id.text_balance_amount);
        root.findViewById(R.id.add_money_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.navigateTo(AddMoneyFragment.newInstance(false,walletAmount), UIConstants
                        .SCREEN_ADD_MONEY);
            }
        });
        root.findViewById(R.id.manage_cards).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.navigateTo(new CardListFragment(), UIConstants
                        .SCREEN_CARD_LIST);
            }
        });
        root.findViewById(R.id.withdraw_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.navigateTo(new GetAccountDetailFragment(), UIConstants
                        .SCREEN_ACCOUNT_DETAILS);
            }
        });
        getBalance();
        ((CitrusUIActivity)getActivity()).showAmount("");
        return root;
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

    private void showWalletDetails(Amount amount) {

        walletAmount = amount.getValue();
        if(walletAmount.equals(".0"))
        {
            walletAmount="0";
            Log.d(TAG,walletAmount);
            walletTextView.setText(getString(R.string.rs) + " " + walletAmount);
        }
        else{
            Log.d(TAG,walletAmount);
            walletTextView.setText(getString(R.string.rs) + " " + walletAmount.replace(".0", ""));
        }

    }
}
