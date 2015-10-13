package com.citrus.sdk.ui.fragments;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.classes.PGHealth;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.activities.CitrusUIActivity;
import com.citrus.sdk.ui.events.FragmentCallbacks;
import com.citrus.sdk.ui.utils.CitrusFlowManager;
import com.citrus.sdk.ui.utils.ResultModel;
import com.citrus.sdk.ui.utils.UIConstants;
import com.citrus.sdk.ui.utils.Utils;
import com.citrus.sdk.ui.widgets.CitrusTextView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;


public class AddMoneyOptionsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    LinearLayout payWithCard;
    LinearLayout payWithBank;
    LinearLayout bankListContainer;
    boolean walletPaymentComplete = false;
    ResultModel walletPaymentModel;
    boolean loadMoneyComplete = false;
    ResultModel loadMoneyModel;
    TextView showMoreToggle;
    //    List<CardOption> savedCardListComplete;
//    List<CardOption> savedCardListLess;
//    List<NetbankingOption> savedBankListComplete;
//    List<NetbankingOption> savedBankListLess;
    List<PaymentOption> paymentModesListComplete;
    List<PaymentOption> paymentModesListLess;
    View root;
    // TODO: Rename and change types of parameters
    private boolean addMoneyNPay = false;
    private String addMoneyAmount;

    private FragmentCallbacks mListener;
    public static final String TAG = "AddMoneyOptionsFragment$";
    private LinearLayout paymentModeList;
    private LinearLayout paymentModesContainer;


    public static AddMoneyOptionsFragment newInstance(boolean addMoneyNPay, String addMoneyAmount) {
        AddMoneyOptionsFragment fragment = new AddMoneyOptionsFragment();
        Bundle args = new Bundle();
        args.putBoolean(UIConstants.ARG_IS_ADD_MONEY_AND_PAY, addMoneyNPay);
        args.putString(UIConstants.ARG_ADD_MONEY_AMOUNT, addMoneyAmount);
        fragment.setArguments(args);
        return fragment;
    }

    public AddMoneyOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            addMoneyNPay = getArguments().getBoolean(UIConstants.ARG_IS_ADD_MONEY_AND_PAY);
            addMoneyAmount = getArguments().getString(UIConstants.ARG_ADD_MONEY_AMOUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_add_money_options, container, false);
        paymentModesContainer = (LinearLayout) root.findViewById(R.id.payment_mode_container);
        payWithCard = (LinearLayout) root.findViewById(R.id.pay_with_card);
        payWithBank = (LinearLayout) root.findViewById(R.id.pay_with_bank);
        paymentModeList = (LinearLayout) root.findViewById(R.id.payment_mode_list);
        bankListContainer = (LinearLayout) root.findViewById(R.id.bank_list_container);
        showMoreToggle = (TextView) root.findViewById(R.id.show_more_text);
        payWithCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addMoneyNPay) {
                    mListener.navigateTo(AddCardFragment.newInstance(UIConstants
                            .TRANS_ADD_MONEY_N_PAY, addMoneyAmount), UIConstants.SCREEN_ADD_CARD);
                } else {
                    mListener.navigateTo(AddCardFragment.newInstance(UIConstants.TRANS_ADD_MONEY,
                            addMoneyAmount), UIConstants.SCREEN_ADD_CARD);
                }
            }
        });
        payWithBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addMoneyNPay) {
                    mListener.navigateTo(BankListFragment.newInstance(UIConstants.TRANS_ADD_MONEY_N_PAY,addMoneyAmount), UIConstants.SCREEN_BANK_LIST);
                }else{
                    mListener.navigateTo(BankListFragment.newInstance(UIConstants.TRANS_ADD_MONEY,addMoneyAmount), UIConstants.SCREEN_BANK_LIST);
                }
            }
        });
        showMoreToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paymentModesListLess != null) {
                    if (showMoreToggle.getText().toString().equals(getString(R.string
                            .text_show_more))) {
                        showPaymentModes(paymentModesListComplete);
                        showMoreToggle.setText(getString(R.string.text_show_less));
                    } else if (showMoreToggle.getText().toString().equals(getString(R.string
                            .text_show_less))) {
                        showPaymentModes(paymentModesListLess);
                        showMoreToggle.setText(getString(R.string.text_show_more));
                    }
                }
            }
        });


//        showMoreBanksToggle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (savedBankListLess != null) {
//                    if (showMoreBanksToggle.getText().toString().equals(getString(R.string
//                            .text_show_more))) {
//                        showNetBankingList(savedBankListComplete);
//                        showMoreBanksToggle.setText(getString(R.string.text_show_less));
//                    } else if (showMoreBanksToggle.getText().toString().equals(getString(R.string
//                            .text_show_less))) {
//                        showNetBankingList(savedBankListLess);
//                        showMoreBanksToggle.setText(getString(R.string.text_show_more));
//                    }
//                }
//            }
//        });
        getWallet();
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


    private void getWallet() {
        if (isAdded()) {
            mListener.showProgressDialog(false, getString(R.string.load_wallet_details));
            CitrusClient.getInstance(getActivity()).getWallet(new com.citrus.sdk
                    .Callback<List<PaymentOption>>
                    () {
                @Override
                public void success(List<PaymentOption> paymentOptions) {

                    List<CardOption> cardOptions = new ArrayList<CardOption>();
                    List<NetbankingOption> netbankingOptions = new ArrayList<NetbankingOption>();
                    Logger.d(TAG + " getWallet success ");
                    Logger.d(TAG + " getWallet success ");
                    if (isAdded()) {
                        mListener.dismissProgressDialog();
                        paymentModesListComplete = paymentOptions;
                        setShowMoreSavedOptionsState();
                        payWithCard.setVisibility(View.VISIBLE);
                        payWithBank.setVisibility(View.VISIBLE);
                    }


                }

                @Override
                public void error(CitrusError citrusError) {

                    Logger.d(TAG + " getWallet failure " + citrusError.getMessage());
                    if (isAdded()) {
                        mListener.dismissProgressDialog();
                    }
                }
            });
        }
    }

    private void setShowMoreSavedOptionsState() {
        if (paymentModesListComplete.size() <= 2) {
            showPaymentModes(paymentModesListComplete);
            showMoreToggle.setVisibility(View.GONE);
        } else {
            showMoreToggle.setVisibility(View.VISIBLE);
            paymentModesListLess = paymentModesListComplete.subList(0, 2);
            showPaymentModes(paymentModesListLess);
        }
    }

    private void showPaymentModes(List<PaymentOption> paymentOptions) {
        int totalSize = paymentOptions.size();
        paymentModeList.removeAllViews();
        if(totalSize>0){
            paymentModesContainer.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < totalSize; i++) {
            if (paymentOptions.get(i) instanceof NetbankingOption) {
                final NetbankingOption netbankingOption = (NetbankingOption)paymentOptions.get(i);
                if (isAdded()) {
                    LinearLayout bankItem = (LinearLayout) getActivity().getLayoutInflater().inflate
                            (R.layout.layout_bank_item, paymentModeList, false);
                    TextView bankName = (TextView) bankItem.findViewById(R.id.bank_name);
                    ImageView bankIcon = (ImageView) bankItem.findViewById(R.id.bank_icon);
                    ImageView warnIcon = (ImageView) bankItem.findViewById(R.id.warn_icon);
                    bankIcon.setImageDrawable(netbankingOption.getOptionIcon(getActivity()));
                    if (i == totalSize - 1) {
                        bankItem.findViewById(R.id.separator).setVisibility(View.GONE);
                    } else {
                        bankItem.findViewById(R.id.separator).setVisibility(View.VISIBLE);
                    }
                    if (netbankingOption.getPgHealth()!= null) {
                        if (netbankingOption.getPgHealth().equals(PGHealth.BAD)) {
                            warnIcon.setVisibility(View.VISIBLE);
                        } else {
                            warnIcon.setVisibility(View.GONE);
                        }
                    }
                    warnIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Snackbar snackbar = Snackbar.make(root, R.string.bank_caution, Snackbar.LENGTH_LONG);
                            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.citrus_warn_color));
                            snackbar.show();
                        }

                    });
                    bankName.setText(netbankingOption.getBankName());
                    bankItem.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    mListener.makePayment(netbankingOption);

                                }
                            }

                    );
                    paymentModeList.addView(bankItem);
                }
            }else if(paymentOptions.get(i) instanceof CardOption) {
                if (isAdded()) {
                    final CardOption cardOption =(CardOption) paymentOptions.get(i);
                    LinearLayout savedCardItem = (LinearLayout) getActivity().getLayoutInflater()
                            .inflate(R.layout.layout_saved_card_item, paymentModeList, false);
                    CitrusTextView cardNumber = (CitrusTextView) savedCardItem.findViewById(R.id
                            .card_number);
                    TextView cardType = (TextView) savedCardItem.findViewById(R.id.card_type);
                    ImageView cardImage = (ImageView) savedCardItem.findViewById(R.id.card_image);
                    cardNumber.setText(Utils.getFormattedCardNumber(cardOption.getCardNumber()));
                    if(cardOption.getCardScheme()==null){
                        cardType.setText(cardOption.getCardType());
                    }
                    else
                    {
                        cardType.setText(cardOption.getCardType() + " - " + cardOption.getCardScheme());
                    }
                    Drawable cardDrawable = cardOption.getOptionIcon(getActivity());
                    if (cardDrawable != null) {
                        Logger.d(TAG + " Card image found");
                        cardImage.setImageDrawable(cardDrawable);
                    } else {
                        Logger.d(TAG + " Card image not found");
                    }
                    savedCardItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (addMoneyNPay) {
                                mListener.navigateTo(GetCVVFragment.newInstance(cardOption, UIConstants
                                        .TRANS_ADD_MONEY_N_PAY, addMoneyAmount), UIConstants
                                        .SCREEN_CVV);
                            }else{
                                mListener.navigateTo(GetCVVFragment.newInstance(cardOption, UIConstants
                                        .TRANS_ADD_MONEY, addMoneyAmount), UIConstants
                                        .SCREEN_CVV);
                            }
                        }
                    });
                    paymentModeList.addView(savedCardItem);
                }
            }
        }

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
                            //                                            mListener
                            // .onWalletTransactionComplete
                            // (transactionResponse);
                            walletPaymentComplete = true;
                            walletPaymentModel = new ResultModel(null, transactionResponse);
                        }

                        @Override
                        public void error(CitrusError error) {
                            Logger.d(TAG + " Could not process " + error.getMessage());
                            walletPaymentComplete = true;
                            walletPaymentModel = new ResultModel(error, null);
                        }
                    });
        } catch (CitrusException e) {
            Logger.e(TAG + "CitrusException" + e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.showAmount(addMoneyAmount);
        if (!addMoneyNPay) {
            if (loadMoneyComplete && loadMoneyModel != null) {
                loadMoneyComplete = false;
                mListener.onWalletTransactionComplete(loadMoneyModel);
            }
        } else {

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
    public void onPause() {
        ((CitrusUIActivity) getActivity()).showAmount("");
        super.onPause();
    }
}
