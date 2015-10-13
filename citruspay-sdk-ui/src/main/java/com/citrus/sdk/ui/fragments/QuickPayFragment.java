package com.citrus.sdk.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.citrus.sdk.ui.activities.BaseActivity;
import com.citrus.sdk.ui.activities.CitrusUIActivity;
import com.citrus.sdk.ui.activities.LoginFlowActivity;
import com.citrus.sdk.ui.events.BalanceUpdateEvent;
import com.citrus.sdk.ui.events.CardSavedEvent;
import com.citrus.sdk.ui.events.FragmentCallbacks;
import com.citrus.sdk.ui.utils.CitrusFlowManager;
import com.citrus.sdk.ui.utils.ResultModel;
import com.citrus.sdk.ui.utils.UIConstants;
import com.citrus.sdk.ui.utils.Utils;
import com.citrus.sdk.ui.widgets.CitrusTextView;
import com.orhanobut.logger.Logger;

import java.util.List;

import de.greenrobot.event.EventBus;

public class QuickPayFragment extends Fragment {

    private static final String ARG_EMAIL = "email";
    private static final String ARG_MOBILE = "mobile";
    private static final String ARG_AMOUNT = "amount";
    private static final String TAG = "QuickPayFragment$";
    View layout;
    private String email;
    private String mobile;
    private String amount;
    private String walletAmount = "";
    CitrusClient citrusClient;
    RelativeLayout walletContainer;
    CitrusTextView setupWalletText;
    TextView walletBalance;
    TextView showMoreToggle;
    TextView payWithCardNew;
    TextView payWithBankNew;
    TextView otherBanksText;
    LinearLayout paymentModesContainer;
    RelativeLayout walletDetailsContainer;
    LinearLayout payWithCard;
    LinearLayout payWithBank,payWithNew;
    LinearLayout paymentModeList;
    LinearLayout layoutPayWithCard;
    LinearLayout brandingLayout;
    Button setupWalletButton, payNowButton, addMoneyNPay;
    FragmentCallbacks mListener;
    boolean walletPaymentComplete = false;
    ResultModel walletResultModel;
//    List<CardOption> savedCardListComplete;
//    List<CardOption> savedCardListLess;
//    List<NetbankingOption> savedBankListComplete;
//    List<NetbankingOption> savedBankListLess;
    List<PaymentOption> paymentModesListComplete;
    List<PaymentOption> paymentModesListLess;
    public static QuickPayFragment newInstance(String email, String mobile, String amount) {
        QuickPayFragment fragment = new QuickPayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_MOBILE, mobile);
        args.putString(ARG_AMOUNT, amount);
        fragment.setArguments(args);
        return fragment;
    }

    public QuickPayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(ARG_EMAIL);
            mobile = getArguments().getString(ARG_MOBILE);
            amount = getArguments().getString(ARG_AMOUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_quick_pay, container, false);
        EventBus.getDefault().register(this);
        setupWalletText = (CitrusTextView) layout.findViewById(R.id.setup_wallet_text);
        walletContainer = (RelativeLayout) layout.findViewById(R.id.wallet_container);
        walletBalance = (TextView) layout.findViewById(R.id.wallet_balance);
        showMoreToggle = (TextView) layout.findViewById(R.id.show_more_text);
        otherBanksText = (TextView) layout.findViewById(R.id.other_banks_text);
        paymentModesContainer = (LinearLayout) layout.findViewById(R.id.payment_mode_container);
        walletDetailsContainer = (RelativeLayout) layout.findViewById(R.id
                .wallet_details_container);
        payWithCard = (LinearLayout) layout.findViewById(R.id.pay_with_card);
        payWithBank = (LinearLayout) layout.findViewById(R.id.pay_with_bank);
        payWithCardNew = (TextView) layout.findViewById(R.id.text_pay_with_card_new);
        payWithBankNew = (TextView) layout.findViewById(R.id.other_banks_text_new);
        payWithNew = (LinearLayout) layout.findViewById(R.id.pay_with_new);
        paymentModeList = (LinearLayout) layout.findViewById(R.id.payment_mode_list);
        setupWalletButton = (Button) layout.findViewById(R.id.setup_wallet_button);
        payNowButton = (Button) layout.findViewById(R.id.pay_now);
        addMoneyNPay = (Button) layout.findViewById(R.id.add_money_pay);
        layoutPayWithCard = (LinearLayout) layout.findViewById(R.id.pay_with_card);
        brandingLayout = (LinearLayout) layout.findViewById(R.id.branding_layout);
        citrusClient = CitrusClient.getInstance(getActivity());
//        payNowButton.setBackground(getButtonDrawableColor());
//        setupWalletButton.setBackground(getButtonDrawableColor());
        layoutPayWithCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mListener.getAmount())&& Double.parseDouble(mListener.getAmount())!= 0) {
                    mListener.navigateTo(AddCardFragment.newInstance(UIConstants.TRANS_QUICK_PAY, "")
                            , UIConstants.SCREEN_ADD_CARD);
                }else{
                    Snackbar.make(layout, getString(R.string.transaction_complete), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        payWithCardNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mListener.getAmount()) && Double.parseDouble(mListener.getAmount()) != 0) {
                    mListener.navigateTo(AddCardFragment.newInstance(UIConstants.TRANS_QUICK_PAY, "")
                            , UIConstants.SCREEN_ADD_CARD);
                } else {
                    Snackbar.make(layout, getString(R.string.transaction_complete), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        payWithBankNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mListener.getAmount())&& Double.parseDouble(mListener.getAmount())!= 0) {
                    mListener.navigateTo(new BankListFragment(), UIConstants.SCREEN_BANK_LIST);
                }else{
                    Snackbar.make(layout, getString(R.string.transaction_complete), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        payWithBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mListener.getAmount())&& Double.parseDouble(mListener.getAmount())!= 0) {
                mListener.navigateTo(new BankListFragment(), UIConstants.SCREEN_BANK_LIST);
                }else{
                    Snackbar.make(layout, getString(R.string.transaction_complete), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        addMoneyNPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mListener.navigateTo(new WalletSignInFragment(), UIConstants.SCREEN_WALLET_LOGIN);
                if (!TextUtils.isEmpty(mListener.getAmount())&& Double.parseDouble(mListener.getAmount())!= 0) {
                    citrusClient.isUserSignedIn(new Callback<Boolean>() {
                        @Override
                        public void success(Boolean success) {

                            if (success) {

                                mListener.navigateTo(AddMoneyFragment.newInstance(true, walletAmount)
                                        , UIConstants
                                        .SCREEN_ADD_MONEY);
                            } else {

                                Intent intent = new Intent(getActivity(), LoginFlowActivity.class);
                                intent.putExtra(CitrusFlowManager.KEY_EMAIL, email);
                                intent.putExtra(CitrusFlowManager.KEY_MOBILE, mobile);
                                intent.putExtra(CitrusFlowManager.KEY_STYLE, mListener.getStyle());
                                if (!TextUtils.isEmpty(amount)) {
                                    intent.putExtra(CitrusFlowManager.KEY_AMOUNT, amount);
                                }
                                startActivityForResult(intent, UIConstants.REQ_CODE_LOGIN_ADD_PAY);
                            }
                        }

                        @Override
                        public void error(CitrusError error) {
                            Snackbar.make(layout, "could not get login status " + error
                                    .getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Snackbar.make(layout, getString(R.string.transaction_complete), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                mListener.navigateTo(new SignUpFragment(), UIConstants.SCREEN_SIGNUP);
//                mListener.navigateTo(new AddMoneyFragment(), UIConstants.SCREEN_ADD_MONEY);
                if (!TextUtils.isEmpty(mListener.getAmount())&& Double.parseDouble(mListener.getAmount())!= 0) {
                    citrusClient.isUserSignedIn(new Callback<Boolean>() {
                        @Override
                        public void success(Boolean success) {
                            if (success) {
    //                  TODO pay with wallet here
    //                            Toast.makeText(getActivity(), "User Alreay signed in", Toast
    // .LENGTH_SHORT)
    //                                    .show();
                                Amount amount = new Amount(mListener.getAmount());

                                try {
                                    citrusClient.payUsingCitrusCash(new PaymentType.CitrusCash(amount,
                                                    CitrusFlowManager.billGenerator),
                                            new Callback<TransactionResponse>() {


                                                @Override
                                                public void success(TransactionResponse
                                                                            transactionResponse) {
                                                    Logger.d(TAG + " Success wallet payment" +
                                                            transactionResponse
                                                                    .getMessage());
                                                    //
                                                    // mListener.onWalletTransactionComplete
                                                    // (transactionResponse);
                                                    walletPaymentComplete = true;
                                                    walletResultModel = new ResultModel(null,
                                                            transactionResponse);
                                                }

                                                @Override
                                                public void error(CitrusError error) {
                                                    Logger.d(TAG + " Could not process " + error
                                                            .getMessage());
                                                    walletPaymentComplete = true;
                                                    walletResultModel = new ResultModel(error, null);
                                                }
                                            });
                                } catch (CitrusException e) {
                                    Log.e(TAG, "CitrusException" + e.getMessage());
                                }
                            } else {
    //                    TODO start wallet login flow here
    //                    mListener.navigateTo(WalletSignInFragment.newInstance(true), UIConstants
    //                            .SCREEN_WALLET_LOGIN);
                                Intent intent = new Intent(getActivity(), LoginFlowActivity.class);
                                intent.putExtra(CitrusFlowManager.KEY_EMAIL, email);
                                intent.putExtra(CitrusFlowManager.KEY_MOBILE, mobile);
                                intent.putExtra(CitrusFlowManager.KEY_STYLE, mListener.getStyle());
                                if (!TextUtils.isEmpty(amount)) {
                                    intent.putExtra(CitrusFlowManager.KEY_AMOUNT, amount);
                                }
                                startActivityForResult(intent, UIConstants.REQ_CODE_LOGIN_PAY);

                            }
                        }

                        @Override
                        public void error(CitrusError error) {
                            Snackbar.make(layout, "could not get login status " + error
                                    .getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Snackbar.make(layout, getString(R.string.transaction_complete), Snackbar.LENGTH_SHORT).show();
                }


            }
        });
//        citrusClient.init("test-signup", "c78ec84e389814a05d3ae46546d16d2e", "test-signin",
// "52f7e15efd4208cf5345dd554443fd99", "prepaid", CitrusClient.Environment.SANDBOX);
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(amount)) {
            Toast.makeText(getActivity().getApplicationContext(), "Please Supply these feilds " +
                    "email, mobile, amount", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            startLoginProcess();
        }

        setupWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginFlowActivity.class);
                intent.putExtra(CitrusFlowManager.KEY_EMAIL, email);
                intent.putExtra(CitrusFlowManager.KEY_MOBILE, mobile);
                intent.putExtra(CitrusFlowManager.KEY_STYLE, mListener.getStyle());
                if (!TextUtils.isEmpty(amount)) {
                    intent.putExtra(CitrusFlowManager.KEY_AMOUNT, amount);
                }
                startActivityForResult(intent, UIConstants.REQ_CODE_LOGIN);
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

        return layout;
    }

    private void startLoginProcess() {
        Logger.d(TAG + " startLoginProcess");
        citrusClient.isUserSignedIn(new Callback<Boolean>() {
            @Override
            public void success(Boolean success) {
                if (success) {
                    getWallet(true);
                } else {
                    linkUser();
                }
            }

            @Override
            public void error(CitrusError error) {
                Snackbar.make(layout, "could not get login status " + error
                        .getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UIConstants.REQ_CODE_LOGIN && resultCode == getActivity().RESULT_OK) {
            Logger.d(TAG + " Login successful");
            getBalance();
        }
        if (requestCode == UIConstants.REQ_CODE_LOGIN_PAY && resultCode == getActivity()
                .RESULT_OK) {
            Logger.d(TAG + " Login successful");
            Logger.d(TAG + " making payment");
            Amount amount = new Amount(mListener.getAmount());

            try {
                citrusClient.payUsingCitrusCash(new PaymentType.CitrusCash(amount,
                                CitrusFlowManager.billGenerator),
                        new Callback<TransactionResponse>() {


                            @Override
                            public void success(TransactionResponse transactionResponse) {
                                Logger.d(TAG + " Success wallet payment" + transactionResponse
                                        .getMessage());
                                walletPaymentComplete = true;
                                walletResultModel = new ResultModel(null, transactionResponse);
                            }

                            @Override
                            public void error(CitrusError error) {
                                Logger.d(TAG + " Could not process " + error.getMessage());
                                walletPaymentComplete = true;
                                walletResultModel = new ResultModel(error, null);
                            }
                        });
            } catch (CitrusException e) {
                Log.e(TAG, "CitrusException" + e.getMessage());
            }
        }
        if (requestCode == UIConstants.REQ_CODE_LOGIN_ADD_PAY && resultCode == getActivity()
                .RESULT_OK) {
            Logger.d(TAG + " Login successful");
            Logger.d(TAG + " Adding money");
            mListener.navigateTo(AddMoneyFragment.newInstance(true, walletAmount), UIConstants
                    .SCREEN_ADD_MONEY);
        }
    }

    private void getWallet(final boolean showLoading) {
        if (isAdded()) {
            if (showLoading) {
                showDialog(getString(R.string.load_wallet_details), false);
            }
            citrusClient.getWallet(new com.citrus.sdk.Callback<List<PaymentOption>>() {
                @Override
                public void success(List<PaymentOption> paymentOptions) {

                    Logger.d(TAG + " getWallet success ");
                    if (isAdded()) {
                        if (showLoading) {
                            dismissDialog();
                        }
                        paymentModesListComplete = paymentOptions;
                        setShowMoreSavedOptionsState();

                        if (showLoading) {
                            getBalance();
                        }
//                        payWithCard.setVisibility(View.VISIBLE);
//                        payWithBank.setVisibility(View.VISIBLE);
                    }


                }

                @Override
                public void error(CitrusError citrusError) {

                    Logger.d(TAG + " getWallet failure " + citrusError.getMessage());
                    if (isAdded()) {
                        if (showLoading) {
                            dismissDialog();
                            getBalance();
                        }
                    }
                }
            });
        }
    }

    private void setShowMoreSavedOptionsState() {
        if (paymentModesListComplete.size() <= UIConstants.DISPLAY_PAYMENT_OPTIONS) {
            showPaymentModes(paymentModesListComplete);
            showMoreToggle.setVisibility(View.GONE);
        } else {
            showMoreToggle.setVisibility(View.VISIBLE);
            paymentModesListLess = paymentModesListComplete.subList(0, UIConstants.DISPLAY_PAYMENT_OPTIONS);
            showPaymentModes(paymentModesListLess);
        }
    }

    private void showPaymentModes(List<PaymentOption> paymentOptions) {
        int totalSize = paymentOptions.size();
        paymentModeList.removeAllViews();
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
                    if (netbankingOption.getPgHealth()!=null) {
                        if (netbankingOption.getPgHealth().equals(PGHealth.BAD)) {
                            warnIcon.setVisibility(View.VISIBLE);
                        } else {
                            warnIcon.setVisibility(View.GONE);
                        }
                    }
                    warnIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Snackbar snackbar = Snackbar.make(layout, R.string.bank_caution, Snackbar.LENGTH_LONG);
                            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.citrus_warn_color));
                            snackbar.show();
                        }

                    });
                    bankName.setText(netbankingOption.getBankName());
                    bankItem.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (!TextUtils.isEmpty(mListener.getAmount())&& Double.parseDouble(mListener.getAmount())!= 0) {
                                        mListener.makePayment(netbankingOption);
                                    } else {
                                        Snackbar.make(layout, getString(R.string.transaction_complete), Snackbar.LENGTH_SHORT).show();
                                    }

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

                            if (!TextUtils.isEmpty(mListener.getAmount())&& Double.parseDouble(mListener.getAmount())!= 0) {
                                mListener.navigateTo(GetCVVFragment.newInstance(cardOption, UIConstants
                                        .TRANS_QUICK_PAY, ""), UIConstants
                                        .SCREEN_CVV);
                            }else{
                                Snackbar.make(layout, getString(R.string.transaction_complete), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                    paymentModeList.addView(savedCardItem);
                }
            }
        }

        if(totalSize>0){
            paymentModesContainer.setVisibility(View.VISIBLE);
            otherBanksText.setText(getString(R.string.text_other_banks));

            payWithBank.setVisibility(View.VISIBLE);
            payWithCard.setVisibility(View.VISIBLE);
        }else{
            payWithBank.setVisibility(View.GONE);
            payWithCard.setVisibility(View.GONE);
            payWithNew.setVisibility(View.VISIBLE);
            otherBanksText.setText(getString(R.string.text_select_bank));
        }
        if (brandingLayout != null) {
            brandingLayout.setVisibility(View.VISIBLE);
        }else{
            brandingLayout = (LinearLayout) layout.findViewById(R.id.branding_layout);
            if(brandingLayout !=null){
                brandingLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void linkUser() {
        Logger.d(TAG + " linkUser");
        showDialog(getString(R.string.linking_user), false);
        citrusClient.isCitrusMember(email, mobile, new com.citrus.sdk.Callback<Boolean>() {

            @Override
            public void success(Boolean aBoolean) {
                if (isAdded()) {
                    dismissDialog();
                    Logger.d(TAG + " Link User success " + aBoolean);
                    getWallet(true);
                    ((BaseActivity) getActivity()).isNewUser = false;
                }
            }

            @Override
            public void error(CitrusError citrusError) {
                if (isAdded()) {
                    dismissDialog();
                    ((BaseActivity) getActivity()).isNewUser = false;
                    Logger.d(TAG + " Link User failure " + citrusError.getMessage());
                    Snackbar.make(layout, "Link User failure " + citrusError.getMessage(),
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getBalance() {

        citrusClient.isUserSignedIn(new Callback<Boolean>() {
            @Override
            public void success(Boolean success) {

                if (success) {

                    if (isAdded()) {
                        showDialog(getString(R.string.load_wallet_balance), false);
                        Utils.getBalance(getActivity());
                    }
                }else{
                    walletDetailsContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void error(CitrusError error) {
                Snackbar.make(layout, "could not get login status " + error
                        .getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void showWalletDetails(Amount amount) {
        setupWalletButton.setVisibility(View.GONE);
        float payAmount = Float.parseFloat(((CitrusUIActivity) getActivity())
                .getPayAmount());
        float balance = Float.parseFloat(amount.getValue());
        if (payAmount < balance) {
            payNowButton.setVisibility(View.VISIBLE);
        } else {
            payNowButton.setVisibility(View.GONE);
        }
        addMoneyNPay.setVisibility(View.VISIBLE);
        walletAmount = amount.getValue();
        if(walletAmount.equals(".0")){
            walletAmount="0";
            walletBalance.setText(getString(R.string.rs) + " " + walletAmount);
        }
        else{
            walletBalance.setText(getString(R.string.rs) + " " + walletAmount.replace(".0",""));
        }

        walletContainer.setVisibility(View.VISIBLE);
        setupWalletText.setVisibility(View.GONE);
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


    public void onEvent(CardSavedEvent event) {
        getWallet(false);
    }

    public void onEvent(BalanceUpdateEvent event) {
        if (isAdded()) {
            dismissDialog();
            walletDetailsContainer.setVisibility(View.VISIBLE);
            if (event.isSuccess()) {
                showWalletDetails(event.getAmount());
            }
        }
    }

    private void showDialog(String message, boolean cancelable) {
        mListener.showProgressDialog(cancelable, message);
    }

    private void dismissDialog() {
        mListener.dismissProgressDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (walletPaymentComplete && walletResultModel != null) {
            walletPaymentComplete = false;
            mListener.onWalletTransactionComplete(walletResultModel);
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }


//    private Drawable getButtonDrawableColor() {
//        TypedValue typedValue = new  TypedValue();
//        getActivity().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
//        final  int color = typedValue.data;
//        Log.i("BaseActivity","Color primary "+color);
//        final Drawable buttonDrawable = ContextCompat.getDrawable(getActivity(), R.drawable
//                .button_foreground);
//        buttonDrawable.setColorFilter(Color.RED,
//                PorterDuff.Mode.SRC_ATOP);
//        Drawable[] layersNormal = new Drawable[2];
//        layersNormal[0] = ContextCompat.getDrawable(getActivity(), R.drawable
//                .shadowlayout);
//        layersNormal[1] = buttonDrawable;
//        LayerDrawable buttonBackgroundNormal = new LayerDrawable(layersNormal);
//        buttonBackgroundNormal.setLayerInset(1,Utils.dipsToPixels(getActivity(),2),Utils
// .dipsToPixels(getActivity(),1),Utils.dipsToPixels(getActivity(),2),Utils.dipsToPixels
// (getActivity(),3));
//
//        Drawable[] layersPressed = new Drawable[1];
//        layersPressed[0] = buttonDrawable;
//        LayerDrawable buttonBackgroundPressed = new LayerDrawable(layersPressed);
//        buttonBackgroundPressed.setLayerInset(0,Utils.dipsToPixels(getActivity(),2),Utils
// .dipsToPixels(getActivity(),1),Utils.dipsToPixels(getActivity(),2),Utils.dipsToPixels
// (getActivity(),3));
//
//
//        StateListDrawable states = new StateListDrawable();
//        states.addState(new int[] {android.R.attr.state_pressed},buttonBackgroundPressed);
//        states.addState(new int[] {android.R.attr.state_focused},buttonBackgroundPressed);
//        states.addState(new int[] { },buttonBackgroundNormal);
//        return buttonDrawable;
//
//    }
}
