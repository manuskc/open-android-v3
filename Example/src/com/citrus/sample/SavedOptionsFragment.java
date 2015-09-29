package com.citrus.sample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;

import java.util.ArrayList;
import java.util.List;

public class SavedOptionsFragment extends Fragment {

    private Utils.PaymentType paymentType = null;
    private Amount amount = null;
    private ArrayList<PaymentOption> walletList = null;
    private CitrusClient citrusClient = null;

    public SavedOptionsFragment() {

    }

    public static SavedOptionsFragment newInstance(Utils.PaymentType paymentType, Amount amount) {
        SavedOptionsFragment fragment = new SavedOptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("paymentType", paymentType);
        bundle.putParcelable("amount", amount);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            paymentType = (Utils.PaymentType) bundle.getSerializable("paymentType");
            amount = bundle.getParcelable("amount");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        citrusClient = CitrusClient.getInstance(getActivity());

        View returnView = inflater.inflate(R.layout.fragment_saved_cards, container, false);

        final SavedOptionsAdapter savedOptionsAdapter = new SavedOptionsAdapter(walletList);

        RecyclerView recylerViewNetbanking = (RecyclerView) returnView.findViewById(R.id.recycler_view_saved_options);
        recylerViewNetbanking.setAdapter(savedOptionsAdapter);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recylerViewNetbanking.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recylerViewNetbanking.setLayoutManager(mLayoutManager);

        recylerViewNetbanking.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new OnItemClickListener()));

        citrusClient.getWallet(new Callback<List<PaymentOption>>() {
            @Override
            public void success(List<PaymentOption> paymentOptionList) {
                walletList = (ArrayList<PaymentOption>) paymentOptionList;

                savedOptionsAdapter.setWalletList(walletList);
                savedOptionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void error(CitrusError error) {
                Utils.showToast(getActivity(), error.getMessage());
            }
        });

        return returnView;
    }

    private PaymentOption getItem(int position) {
        PaymentOption paymentOption = null;

        if (walletList != null && walletList.size() > position && position >= -1) {
            paymentOption = walletList.get(position);
        }

        return paymentOption;
    }

    private class OnItemClickListener extends RecyclerItemClickListener.SimpleOnItemClickListener {

        @Override
        public void onItemClick(View childView, int position) {
            PaymentOption paymentOption = getItem(position);

            if (paymentOption instanceof CardOption) {
                showPrompt(paymentType, paymentOption);
            } else {
                proceedToPayment(paymentType, paymentOption);
            }

        }
    }

    private void proceedToPayment(Utils.PaymentType paymentType, PaymentOption paymentOption) {

        PaymentType paymentType1;

        Callback<TransactionResponse> callback = new Callback<TransactionResponse>() {
            @Override
            public void success(TransactionResponse transactionResponse) {
                ((UIActivity) getActivity()).onPaymentComplete(transactionResponse);
            }

            @Override
            public void error(CitrusError error) {
                Utils.showToast(getActivity(), error.getMessage());
            }
        };

        try {

            AuthenticationSignatures authenticationSignatures = Utils.getAuthenticationSignatures(getActivity());

            if (paymentType == Utils.PaymentType.LOAD_MONEY) {
                paymentType1 = new PaymentType.LoadMoney(amount, authenticationSignatures.getRETURN_URL_LOAD_MONEY(), paymentOption);
//                paymentType1 = new PaymentType.LoadMoney(amount, Constants.RETURN_URL_LOAD_MONEY, paymentOption);
                citrusClient.loadMoney((PaymentType.LoadMoney) paymentType1, callback);
            } else if (paymentType == Utils.PaymentType.PG_PAYMENT) {
                paymentType1 = new PaymentType.PGPayment(amount, authenticationSignatures.getBILL_URL(), paymentOption, new CitrusUser(citrusClient.getUserEmailId(), citrusClient.getUserMobileNumber()));
//                paymentType1 = new PaymentType.PGPayment(amount, Constants.BILL_URL, paymentOption, new CitrusUser(citrusClient.getUserEmailId(), citrusClient.getUserMobileNumber()));
                citrusClient.pgPayment((PaymentType.PGPayment) paymentType1, callback);
            }
        } catch (CitrusException e) {
            e.printStackTrace();

            Utils.showToast(getActivity(), e.getMessage());
        }
    }

    private void showPrompt(final Utils.PaymentType paymentType, final PaymentOption paymentOption) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        String message = "Please enter CVV?";
        String positiveButtonText = "OK";
        alert.setTitle("CVV");
        alert.setMessage(message);
        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                String cvv = input.getText().toString();
                input.clearFocus();
                // Hide the keyboard.
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                // Set the OTP
                ((CardOption) paymentOption).setCardCVV(cvv);
                proceedToPayment(paymentType, paymentOption);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        input.requestFocus();
        alert.show();
    }
}
