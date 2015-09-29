package com.citrus.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.citrus.card.Card;
import com.citrus.mobile.CType;
import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.classes.Month;
import com.citrus.sdk.classes.Year;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.CreditCardOption;
import com.citrus.sdk.payment.DebitCardOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.widgets.CardNumberEditText;
import com.citrus.widgets.ExpiryDate;

public class CreditDebitCardFragment extends Fragment implements View.OnClickListener {

    private CardNumberEditText editCardNumber = null;
    private ExpiryDate editExpiryDate = null;
    private EditText editCVV = null, editCardHolderName = null, cardHolderNickName = null, cardHolderNumber = null;
    private TextView submitButton = null;
    private Card card;
    private CType cType = CType.DEBIT;
    private Utils.PaymentType paymentType = null;
    private Amount amount = null;

    public CreditDebitCardFragment() {
    }

    public static CreditDebitCardFragment newInstance(Utils.PaymentType paymentType, CType cType, Amount amount) {
        CreditDebitCardFragment fragment = new CreditDebitCardFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("paymentType", paymentType);
        bundle.putSerializable("cType", cType);
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
            cType = (CType) bundle.getSerializable("cType");
            amount = bundle.getParcelable("amount");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_credit_debit_card, container,
                false);

        editCardNumber = (CardNumberEditText) returnView
                .findViewById(R.id.cardHolderNumber);
        editExpiryDate = (ExpiryDate) returnView.findViewById(R.id.cardExpiry);
        editCardHolderName = (EditText) returnView.findViewById(R.id.cardHolderName);
        cardHolderNickName = (EditText) returnView.findViewById(R.id.cardHolderNickName);
        editCVV = (EditText) returnView.findViewById(R.id.cardCvv);
        submitButton = (TextView) returnView.findViewById(R.id.load);
        submitButton.setOnClickListener(this);

        switch (paymentType) {
            case LOAD_MONEY:
                submitButton.setText("Load");
                break;
            case CITRUS_CASH:
            case PG_PAYMENT:
                submitButton.setText("Pay");
                break;
        }

        return returnView;
    }

    @Override
    public void onClick(View v) {

        String cardHolderName = editCardHolderName.getText().toString();
        String cardNumber = editCardNumber.getText().toString();
        String cardCVV = editCVV.getText().toString();
        Month month = editExpiryDate.getMonth();
        Year year = editExpiryDate.getYear();

        CardOption cardOption;
        if (cType == CType.DEBIT) {
            cardOption = new DebitCardOption(cardHolderName, cardNumber, cardCVV, month, year);
        } else {
            cardOption = new CreditCardOption(cardHolderName, cardNumber, cardCVV, month, year);
        }

        PaymentType paymentType;
        CitrusClient client = CitrusClient.getInstance(getActivity());

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

            if (this.paymentType == Utils.PaymentType.LOAD_MONEY) {
                paymentType = new PaymentType.LoadMoney(amount, authenticationSignatures.getRETURN_URL_LOAD_MONEY(), cardOption);
//                paymentType = new PaymentType.LoadMoney(amount, Constants.RETURN_URL_LOAD_MONEY, cardOption);
                client.loadMoney((PaymentType.LoadMoney) paymentType, callback);
            } else if (this.paymentType == Utils.PaymentType.PG_PAYMENT) {
                paymentType = new PaymentType.PGPayment(amount, authenticationSignatures.getBILL_URL(), cardOption, new CitrusUser(client.getUserEmailId(), client.getUserMobileNumber()));
//                paymentType = new PaymentType.PGPayment(amount, Constants.BILL_URL, cardOption, new CitrusUser(client.getUserEmailId(), client.getUserMobileNumber()));
                client.pgPayment((PaymentType.PGPayment) paymentType, callback);
            }
        } catch (CitrusException e) {
            e.printStackTrace();

            Utils.showToast(getActivity(), e.getMessage());
        }
    }
}
