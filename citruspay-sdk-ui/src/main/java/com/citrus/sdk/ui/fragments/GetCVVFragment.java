package com.citrus.sdk.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.events.FragmentCallbacks;
import com.citrus.sdk.ui.utils.CitrusFlowManager;
import com.citrus.sdk.ui.utils.ResultModel;
import com.citrus.sdk.ui.utils.UIConstants;
import com.citrus.sdk.ui.utils.Utils;
import com.citrus.sdk.ui.widgets.CustomEditText;
import com.orhanobut.logger.Logger;

public class GetCVVFragment extends Fragment {
    private static final String ARG_CARDOPTION = "KEY_CARD_OPTION";
    public static final String TAG = "GetCVVFragment$";
    private CardOption cardOption;
    CustomEditText cvvEditText;
    LinearLayout checkboxContainer;
    RelativeLayout cardDetailsContainer;
    int previousTextCount = 0;
    private TextView textCardNumber;
    private TextView cardHolderName;
    private TextView cardExpiry;
    FragmentCallbacks mListener;
    ImageView checkView;
    String transactionType =UIConstants.TRANS_QUICK_PAY;
    String addMoneyAmount;
    boolean walletPaymentComplete = false;
    ResultModel walletPaymentModel;
    boolean loadMoneyComplete = false;
    ResultModel loadMoneyModel;
    int cvvLength = 3;
    private TextView enterCVVLabel;
    ObjectAnimator animChecboxContainer;
    ObjectAnimator animCVVLabel;


    AnimatorSet set;
    public static GetCVVFragment newInstance(CardOption cardOption,String transactionType,String addMoneyAmount) {
        GetCVVFragment fragment = new GetCVVFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CARDOPTION, cardOption);
        args.putString(UIConstants.ARG_TRANSACTION_TYPE, transactionType);
        args.putString(UIConstants.ARG_ADD_MONEY_AMOUNT, addMoneyAmount);
        fragment.setArguments(args);
        return fragment;
    }

    public GetCVVFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cardOption = getArguments().getParcelable(ARG_CARDOPTION);
            transactionType = getArguments().getString(UIConstants.ARG_TRANSACTION_TYPE);
            addMoneyAmount = getArguments().getString(UIConstants.ARG_ADD_MONEY_AMOUNT);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the parentLayout for this fragment

        mListener.toggleAmountVisibility(View.GONE);
        View layout =  inflater.inflate(R.layout.fragment_get_cvv, container, false);
        cvvEditText = (CustomEditText)layout.findViewById(R.id.cvv_edit_text);
        checkboxContainer = (LinearLayout)layout.findViewById(R.id.checkbox_container);
        cardDetailsContainer = (RelativeLayout)layout.findViewById(R.id.card_details_container);
        textCardNumber = (TextView) layout.findViewById(R.id.card_number_text);
        cardHolderName = (TextView)layout.findViewById(R.id.text_card_holder_name);
        cardExpiry = (TextView)layout.findViewById(R.id.text_card_validity);
        checkView = (ImageView)layout.findViewById(R.id.go_button);
        cvvEditText.requestFocus();
        enterCVVLabel =(TextView)layout.findViewById(R.id.cvv_lable);
        checkboxContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openKeyboard(cvvEditText);
            }
        });
        cardDetailsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openKeyboard(cvvEditText);
            }
        });
        if(cardOption == null){
            Logger.d(TAG + " cardOption is null");
        }else{
            Logger.d(TAG + " cardOption is not null");
        }
        if(cardOption.getCVVLength()>3){
            cvvLength = cardOption.getCVVLength();
        }

        for(int i= 0;i<cvvLength;i++){
            View radioButton = inflater.inflate(R.layout.layout_cvv_radio,null);
            checkboxContainer.addView(radioButton);
        }




        checkboxContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                enterCVVLabel.setVisibility(View.VISIBLE);
                if (cvvEditText.length() == cvvLength && animChecboxContainer != null) {

                    animChecboxContainer.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                            checkView.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            cvvEditText.setText("");
                            Utils.openKeyboard(cvvEditText);
                            for (int i = 0; i < cvvLength; i++) {
                                ((RadioButton) checkboxContainer.getChildAt(i))
                                        .setChecked(false);
                            }
                            animator.removeListener(this);

                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                    animChecboxContainer.reverse();
                    animCVVLabel.reverse();

                } else {
                    Utils.openKeyboard(cvvEditText);
                }
            }

        });

        textCardNumber.setText(Utils.getFormattedCardNumber(cardOption.getCardNumber()));
        cardHolderName.setText(Utils.CapitalizeWords(cardOption.getCardHolderName()));
        cardExpiry.setText(cardOption.getCardExpiryMonth() + "/" + cardOption.getCardExpiryYear());
        cvvEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        cvvEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int textCount = s.length();

                if (textCount > cvvLength) {
                    cvvEditText.setText(s.subSequence(0, cvvLength));
                } else {
                    Logger.d(TAG + " new =  " + textCount);
                    Logger.d(TAG + " previous =  " + previousTextCount);
                    if (textCount <= cvvLength) {
                        if (previousTextCount == textCount - 1) {
                            ((RadioButton) checkboxContainer.getChildAt(textCount - 1)).setChecked
                                    (true);
                        } else if (previousTextCount == textCount + 1) {
                            ((RadioButton) checkboxContainer.getChildAt(previousTextCount - 1))
                                    .setChecked(false);
                        }
                    }

                    if (textCount == cvvLength && previousTextCount != cvvLength) {
                        checkboxContainer.bringToFront();
                        startAnimation();
                    }
                    previousTextCount = textCount;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cvvEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Logger.d("Enter pressed");
                    if (cvvEditText.getText().toString().trim().length() == cvvLength) {
                        cardOption.setCardCVV(cvvEditText.getText().toString().trim());
                        if (transactionType.equals(UIConstants.TRANS_QUICK_PAY)) {
                            mListener.makeCardPayment(cardOption);
                        } else {
                            addMoneyToWallet();
                        }
                        closeKeyBoard();
                    } else {

                        Logger.d("addMoney failed if condition ");
                    }
                }
                return false;
            }
        });
        Utils.openKeyboard(cvvEditText);


        checkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cvvEditText.getText().toString().trim().length() == cvvLength) {
                    cardOption.setCardCVV(cvvEditText.getText().toString().trim());
                    if (transactionType.equals(UIConstants.TRANS_QUICK_PAY)) {
                        mListener.makeCardPayment(cardOption);
                    } else {
                        addMoneyToWallet();
                    }
                    closeKeyBoard();
                }else{

                    Logger.d("addMoney failed if condition ");
                }

            }
        });
        return layout;
    }




    private void addMoneyToWallet() {
        Logger.d("addMoneyToWallet " + transactionType);
        Amount walletLoadAmount = new Amount(addMoneyAmount);
        try {
            CitrusClient.getInstance(getActivity()).loadMoney(new PaymentType.LoadMoney(walletLoadAmount, CitrusFlowManager.returnURL, cardOption), new Callback<TransactionResponse>() {


                @Override
                public void success(TransactionResponse transactionResponse) {
                    Logger.d(TAG + " Success wallet Load" + transactionResponse
                            .getMessage());
                    loadMoneyComplete =true;
                    loadMoneyModel = new ResultModel(null,transactionResponse);
                }

                @Override
                public void error(CitrusError error) {
                    Logger.d(TAG + " Could not process wallet Load" + error.getMessage());
                    loadMoneyComplete =true;
                    loadMoneyModel = new ResultModel(error,null);
                }
            });
        } catch (CitrusException e) {
            Logger.e(TAG + " CitrusException" + e.getMessage());
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
//
    @Override
    public void onDetach() {
        closeKeyBoard();
        mListener = null;
        super.onDetach();
    }

    private void closeKeyBoard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(cvvEditText.getWindowToken(), 0);
    }

//    public interface FragmentCallbacks {
//        public void onFragmentInteraction(Uri uri);
//    }

    private void payUsingCitrusCash(){
        Amount amount = new Amount(mListener.getAmount());

        try {
            CitrusClient.getInstance(getActivity()).payUsingCitrusCash(new PaymentType.CitrusCash
                            (amount,CitrusFlowManager.billGenerator),
                    new Callback<TransactionResponse>() {


                        @Override
                        public void success(TransactionResponse transactionResponse) {
                            Logger.d(TAG + " Success wallet payment" + transactionResponse
                                    .getMessage());
                            walletPaymentComplete = true;
                            walletPaymentModel = new ResultModel(null,transactionResponse);
                        }

                        @Override
                        public void error(CitrusError error) {
                            Logger.d(TAG + " Could not process wallet payment" + error.getMessage());
                            walletPaymentComplete = true;
                            walletPaymentModel = new ResultModel(error,null);
                        }
                    });
        } catch (CitrusException e) {
            Log.e(TAG,"CitrusException"+e.getMessage());
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
        }else if (transactionType.equals(UIConstants.TRANS_ADD_MONEY_N_PAY)){

            if (loadMoneyComplete && loadMoneyModel != null) {
                loadMoneyComplete = false;
                if (loadMoneyModel.getTransactionResponse() !=null) {
                    payUsingCitrusCash();
                }else{
                    mListener.onWalletTransactionComplete(loadMoneyModel);
                }
            }else if(walletPaymentComplete &&walletPaymentModel != null){
                walletPaymentComplete = false;
                mListener.onWalletTransactionComplete(walletPaymentModel);
            }
        }
    }

    public void startAnimation(){
        animChecboxContainer = ObjectAnimator.ofFloat(checkboxContainer, "translationX", 0, enterCVVLabel.getX() - checkboxContainer.getX());
        animCVVLabel = ObjectAnimator.ofFloat(enterCVVLabel, "alpha", 0);

        set = new AnimatorSet();
        set.playTogether(animChecboxContainer, animCVVLabel);
        set.setDuration(500);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                Logger.i("onAnimationStart");
                checkView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

                Logger.i("onAnimationEnd");
                closeKeyBoard();
                set.removeListener(this);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

                Logger.i("onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

                Logger.i("onAnimationRepeat");
            }
        });
        set.start();

    }


}
