package com.citrus.sdk.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.payment.MerchantPaymentOption;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.adapters.BankListAdapter;
import com.citrus.sdk.ui.events.FragmentCallbacks;
import com.citrus.sdk.ui.utils.CitrusFlowManager;
import com.citrus.sdk.ui.utils.ResultModel;
import com.citrus.sdk.ui.utils.UIConstants;
import com.orhanobut.logger.Logger;

import java.util.List;


public class BankListFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "BankListFragment$";

    private ListView bankList;
    String transactionType = UIConstants.TRANS_QUICK_PAY;
    String addMoneyAmount;
    private BankListAdapter bankListAdapter;
    List<NetbankingOption> netbankingOptionList;
    private FragmentCallbacks mListener;
    boolean walletPaymentComplete = false;
    ResultModel walletPaymentModel;
    boolean loadMoneyComplete = false;
    ResultModel loadMoneyModel;


    public static BankListFragment newInstance(String transactionType, String addMoneyAmount) {
        BankListFragment fragment = new BankListFragment();
        Bundle args = new Bundle();
        args.putString(UIConstants.ARG_TRANSACTION_TYPE, transactionType);
        args.putString(UIConstants.ARG_ADD_MONEY_AMOUNT, addMoneyAmount);
        fragment.setArguments(args);
        return fragment;
    }

    public BankListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transactionType = getArguments().getString(UIConstants.ARG_TRANSACTION_TYPE);
            addMoneyAmount = getArguments().getString(UIConstants.ARG_ADD_MONEY_AMOUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_list, container, false);
        bankList = (ListView) layout.findViewById(R.id.list_view);
        getMerchantPaymentOptions();
        return layout;
    }

    private void getMerchantPaymentOptions() {
        mListener.showProgressDialog(false, getString(R.string.text_getting_bank_list));
        CitrusClient.getInstance(getActivity()).getMerchantPaymentOptions(new Callback<MerchantPaymentOption>() {

            @Override
            public void success(MerchantPaymentOption merchantPaymentOption) {
                mListener.dismissProgressDialog();
                Logger.d(TAG + " got payment Options");
                netbankingOptionList = merchantPaymentOption.getNetbankingOptionList();
                setUpBankList();
            }

            @Override
            public void error(CitrusError error) {
                mListener.dismissProgressDialog();
                Logger.d(TAG + " Got error " + error.getMessage());
            }
        });
    }

    private void setUpBankList() {
        bankListAdapter = new BankListAdapter(getActivity(), netbankingOptionList);
        bankList.setAdapter(bankListAdapter);
        bankList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (transactionType.equals(UIConstants.TRANS_QUICK_PAY)) {
                    mListener.makePayment(bankListAdapter.getItem(position));
                } else {
                    addMoneyToWallet(bankListAdapter.getItem(position));
                }

            }
        });
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

    private void payUsingCitrusCash() {
        Amount amount = new Amount(mListener.getAmount());

        try {
            CitrusClient.getInstance(getActivity()).payUsingCitrusCash(new PaymentType.CitrusCash
                            (amount, CitrusFlowManager.billGenerator),
                    new Callback<TransactionResponse>() {


                        @Override
                        public void success(TransactionResponse transactionResponse) {
                            Logger.d(TAG + " Success wallet payment" + transactionResponse
                                    .getMessage());
                            walletPaymentComplete = true;
                            walletPaymentModel = new ResultModel(null, transactionResponse);
                        }

                        @Override
                        public void error(CitrusError error) {
                            Logger.d(TAG + " Could not process wallet payment" + error.getMessage
                                    ());
                            walletPaymentComplete = true;
                            walletPaymentModel = new ResultModel(error, null);
                        }
                    });
        } catch (CitrusException e) {
            Log.e(TAG, "CitrusException" + e.getMessage());
        }
    }

    private void addMoneyToWallet(PaymentOption paymentOption) {
        Amount walletLoadAmount = new Amount(addMoneyAmount);
        try {
            CitrusClient.getInstance(getActivity()).loadMoney(new PaymentType.LoadMoney
                    (walletLoadAmount, CitrusFlowManager.returnURL, paymentOption), new
                    Callback<TransactionResponse>() {


                @Override
                public void success(TransactionResponse transactionResponse) {
                    Logger.d(TAG + " Success wallet Load" + transactionResponse
                            .getMessage());
                    loadMoneyComplete = true;
                    loadMoneyModel = new ResultModel(null, transactionResponse);
                }

                @Override
                public void error(CitrusError error) {
                    Logger.d(TAG + " Could not process wallet Load" + error.getMessage());
                    loadMoneyComplete = true;
                    loadMoneyModel = new ResultModel(error, null);
                }
            });
        } catch (CitrusException e) {
            Logger.e(TAG + " CitrusException" + e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (transactionType.equals(UIConstants.TRANS_ADD_MONEY)) {
            if (loadMoneyComplete && loadMoneyModel != null) {
                loadMoneyComplete = false;
                mListener.onWalletTransactionComplete(loadMoneyModel);
            }
        } else if (transactionType.equals(UIConstants.TRANS_ADD_MONEY_N_PAY)) {

            if (loadMoneyComplete && loadMoneyModel != null) {
                loadMoneyComplete = false;
                if (loadMoneyModel.getTransactionResponse() != null) {
                    payUsingCitrusCash();
                } else {
                    mListener.onWalletTransactionComplete(loadMoneyModel);
                }
            } else if (walletPaymentComplete && walletPaymentModel != null) {
                walletPaymentComplete = false;
                mListener.onWalletTransactionComplete(walletPaymentModel);
            }
        }
    }
}
