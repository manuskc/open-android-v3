package com.citrus.sdk.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.activities.CitrusUIActivity;
import com.citrus.sdk.ui.events.FragmentCallbacks;
import com.citrus.sdk.ui.utils.CitrusFlowManager;
import com.citrus.sdk.ui.utils.ResultModel;
import com.citrus.sdk.ui.widgets.CitrusTextView;
import com.orhanobut.logger.Logger;


public class ResultFragment extends Fragment {

    private static final String ARG_IS_SUCCESS = "is_success";
    private static final String ARG_RESULT = "result";
    private static final String TAG = "ResultFragment$";
    ImageView paymentResultImage;
    CitrusTextView paymentResultText;
    CitrusTextView withdrawalResultText;
    TextView transactionIdLabel;
    TextView transactionIdText;
    TextView amountPaidLabel;
    TextView amountPaidText;
    LinearLayout paymentFailureActions;
    LinearLayout noWalletLayout;
    LinearLayout walletAvailableLayout;
    Button retryButton;
    Button dismissButton;
    boolean success = false;
    ResultModel resultModel;

    private FragmentCallbacks mListener;


    public static ResultFragment newInstance(boolean success, ResultModel resultModel) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_SUCCESS, success);
        args.putParcelable(ARG_RESULT, resultModel);
        fragment.setArguments(args);
        return fragment;
    }

    public ResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            success = getArguments().getBoolean(ARG_IS_SUCCESS);
            resultModel = getArguments().getParcelable(ARG_RESULT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mListener.toggleAmountVisibility(View.GONE);
        View layout = inflater.inflate(R.layout.fragment_result, container, false);
        paymentResultImage = (ImageView) layout.findViewById(R.id.payment_result_image);
        paymentResultText = (CitrusTextView) layout.findViewById(R.id.payment_result_text);
        transactionIdLabel = (TextView) layout.findViewById(R.id.transaction_id_label);
        transactionIdText = (TextView) layout.findViewById(R.id.transaction_id_text);
        amountPaidLabel = (TextView) layout.findViewById(R.id.amount_paid_label);
        amountPaidText = (TextView) layout.findViewById(R.id.amount_paid_text);
        withdrawalResultText = (CitrusTextView) layout.findViewById(R.id.withdrawal_result_text);
        paymentFailureActions = (LinearLayout) layout.findViewById(R.id.payment_failure_actions);
        noWalletLayout = (LinearLayout) layout.findViewById(R.id.no_wallet_layout);
        walletAvailableLayout = (LinearLayout) layout.findViewById(R.id.wallet_available_layout);
        retryButton = (Button) layout.findViewById(R.id.retry_transaction_button);
        dismissButton = (Button) layout.findViewById(R.id.dismiss_transaction_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CitrusUIActivity) getActivity()).retry = true;
                ((CitrusUIActivity) getActivity()).onBackPressed();
            }
        });
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CitrusUIActivity) getActivity()).finish();
            }
        });
        if (!resultModel.isWithdraw()) {
            if (resultModel.getTransactionResponse() != null) {
                if (resultModel.getTransactionResponse().getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    paymentResultImage.setImageResource(R.drawable.img_checkmark_green);
                    paymentResultText.setText(getString(R.string.text_payment_success));
                    transactionIdLabel.setText(getString(R.string.text_transaction_id));
                    amountPaidLabel.setText(getString(R.string.text_amount_paid));
                    paymentFailureActions.setVisibility(View.GONE);
                    noWalletLayout.setVisibility(View.VISIBLE);
                    walletAvailableLayout.setVisibility(View.GONE);
                    amountPaidText.setText(getString(R.string.rs)+resultModel.getTransactionResponse().getTransactionAmount()
                            .getValue());
                    transactionIdText.setText(resultModel.getTransactionResponse().getTransactionDetails
                            ().getTransactionId());
                    walletLoggedIn();
                }else{
                    paymentResultImage.setImageResource(R.drawable.img_cross_red);
                    paymentResultText.setText(getString(R.string.text_payment_failure));
                    transactionIdLabel.setText(getString(R.string.text_error_transaction_id));
                    amountPaidLabel.setText(getString(R.string.text_error_message));
                    amountPaidLabel.setVisibility(View.GONE);
                    paymentFailureActions.setVisibility(View.VISIBLE);
                    noWalletLayout.setVisibility(View.GONE);
                    walletAvailableLayout.setVisibility(View.GONE);
                    amountPaidText.setText(resultModel.getTransactionResponse().getMessage());
                    Logger.d(TAG + " Transaction Error " + resultModel.getTransactionResponse().getMessage());
                }
            } else {
                paymentResultImage.setImageResource(R.drawable.img_cross_red);
                paymentResultText.setText(getString(R.string.text_payment_failure));
                transactionIdLabel.setText(getString(R.string.text_error_transaction_id));
                amountPaidLabel.setText(getString(R.string.text_error_message));
                paymentFailureActions.setVisibility(View.VISIBLE);
                noWalletLayout.setVisibility(View.GONE);
                transactionIdText.setVisibility(View.GONE);
                transactionIdLabel.setVisibility(View.GONE);
                walletAvailableLayout.setVisibility(View.GONE);

                if (resultModel.getError() != null) {
                    amountPaidText.setText(resultModel.getError().getMessage());
                    Logger.d(TAG + " Transaction Error " + resultModel.getError().getMessage());
    //                transactionIdText.setText(resultModel.getError().);
                }
            }
        }else{
            if(resultModel.getPaymentResponse() !=null){
                paymentResultImage.setImageResource(R.drawable.img_checkmark_green);
                paymentResultText.setText(getString(R.string.text_withdraw_success));
                transactionIdLabel.setText(getString(R.string.text_transaction_id));
                amountPaidLabel.setText(getString(R.string.text_amount_paid));
                paymentFailureActions.setVisibility(View.GONE);
                noWalletLayout.setVisibility(View.VISIBLE);
                walletAvailableLayout.setVisibility(View.GONE);
                withdrawalResultText.setVisibility(View.VISIBLE);
                withdrawalResultText.setMovementMethod(LinkMovementMethod.getInstance());
//                withdrawalResultText.setText(getString(R.string.text_withdraw_success_message));
                amountPaidText.setText(getString(R.string.rs)+Double.toString(resultModel.getPaymentResponse()
                        .getTransactionAmount()
                        .getValueAsDouble()));
                transactionIdText.setText(resultModel.getPaymentResponse().getTransactionId());
                walletLoggedIn();
            }else{
                paymentResultImage.setImageResource(R.drawable.img_cross_red);
                paymentResultText.setText(getString(R.string.text_withdraw_failure));
                transactionIdLabel.setText(getString(R.string.text_error_transaction_id));
                amountPaidLabel.setText(getString(R.string.text_error_message));
                paymentFailureActions.setVisibility(View.VISIBLE);
                noWalletLayout.setVisibility(View.GONE);
                transactionIdText.setVisibility(View.GONE);
                transactionIdLabel.setVisibility(View.GONE);
                walletAvailableLayout.setVisibility(View.GONE);

                if (resultModel.getError() != null) {
                    amountPaidText.setText(resultModel.getError().getMessage());
                    Logger.d(TAG + " Transaction Error " + resultModel.getError().getMessage());
                    //                transactionIdText.setText(resultModel.getError().);
                }
            }
        }

        layout.findViewById(R.id.setup_wallet_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CitrusFlowManager.startWalletFlow(getActivity(),mListener.getEmail(),mListener.getMobile());
                getActivity().finish();
            }
        });
        layout.findViewById(R.id.go_to_wallet_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CitrusFlowManager.startWalletFlow(getActivity(),mListener.getEmail(),mListener.getMobile());
                getActivity().finish();
            }
        });

        return layout;
    }


    private void walletLoggedIn(){
        CitrusClient.getInstance(getActivity()).isUserSignedIn(new Callback<Boolean>() {
            @Override
            public void success(Boolean success) {
                if (success) {
                    noWalletLayout.setVisibility(View.GONE);
                    walletAvailableLayout.setVisibility(View.VISIBLE);
                } else {
                    noWalletLayout.setVisibility(View.VISIBLE);
                    walletAvailableLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void error(CitrusError error) {

                noWalletLayout.setVisibility(View.VISIBLE);
                walletAvailableLayout.setVisibility(View.GONE);
            }
        });
    }


//
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


}
