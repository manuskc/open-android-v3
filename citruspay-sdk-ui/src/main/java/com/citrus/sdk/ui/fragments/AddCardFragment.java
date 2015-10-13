package com.citrus.sdk.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.classes.Month;
import com.citrus.sdk.classes.Year;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.CreditCardOption;
import com.citrus.sdk.payment.DebitCardOption;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.events.CardSavedEvent;
import com.citrus.sdk.ui.events.FragmentCallbacks;
import com.citrus.sdk.ui.utils.UIConstants;
import com.citrus.sdk.ui.widgets.CardNumberEditText;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;


public class AddCardFragment extends Fragment {
    public static final String TAG = "AddCardFragment$";

    private String addMoneyAmount;
    private String transactionType = UIConstants.TRANS_QUICK_PAY;
    AppCompatSpinner yearSpinner, monthSpinner;
    Button makePayment;
    String selectedMonth = "";
    String selectedYear = "";
    String creditCardNumber = "";
    String nameOnCard = "";

    RadioButton debitcardCheck;
    RadioButton creditCardCheck;
//    SwitchCompat saveCardSwitch;

    CardNumberEditText cardNoET;
    EditText ownerNameET;
    String[] monthList;
    View parentLayout;
    Map<String, String> monthListMap;
    ArrayList<String> yearList = new ArrayList<String>();
    FragmentCallbacks mListener;

    public static AddCardFragment newInstance(String transactionType, String addMoneyAmount) {
        AddCardFragment fragment = new AddCardFragment();
        Bundle args = new Bundle();
        args.putString(UIConstants.ARG_TRANSACTION_TYPE, transactionType);
        args.putString(UIConstants.ARG_ADD_MONEY_AMOUNT, addMoneyAmount);
        fragment.setArguments(args);
        return fragment;
    }

    public AddCardFragment() {
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
        // Inflate the parentLayout for this fragment
        parentLayout = inflater.inflate(R.layout.fragment_add_card, container, false);
        yearSpinner = (AppCompatSpinner) parentLayout.findViewById(R.id.year_spinner);
        monthSpinner = (AppCompatSpinner) parentLayout.findViewById(R.id.month_spinner);
        makePayment = (Button) parentLayout.findViewById(R.id.make_payment_button);
        cardNoET = (CardNumberEditText) parentLayout.findViewById(R.id.card_no_et);
        ownerNameET = (EditText) parentLayout.findViewById(R.id.owner_name_et);
        debitcardCheck = (RadioButton) parentLayout.findViewById(R.id.check_debit_card);
        creditCardCheck = (RadioButton) parentLayout.findViewById(R.id.check_credit_card);
//        saveCardSwitch = (SwitchCompat) parentLayout.findViewById(R.id.save_card_switch);
        makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performPayment();
            }
        });
        ownerNameET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Logger.d("Enter pressed");
                    performPayment();
                }
                return false;
            }
        });
        setupYearSpinner();
        setupMonthSpinner();
        return parentLayout;
    }

    private void performPayment() {
        if (validateDetails()) {
            CardOption cardOption;
            Logger.d(TAG + " selected month" + selectedMonth);
            Logger.d(TAG + " selected year" + selectedYear);
            if (debitcardCheck.isChecked()) {
                Logger.d(TAG, "Debit card Selected");
                cardOption = new DebitCardOption(nameOnCard, creditCardNumber, "123",
                        Month.getMonth(monthListMap.get(selectedMonth)), Year.getYear
                        (getFormattedYear(selectedYear)));
            } else if (creditCardCheck.isChecked()) {
                Logger.d(TAG + " Credit card Selected");
                cardOption = new CreditCardOption(nameOnCard, creditCardNumber, "123",
                        Month.getMonth(monthListMap.get(selectedMonth)), Year.getYear
                        (getFormattedYear(selectedYear)));
            } else {
                cardOption = null;
            }

            if (cardOption != null) {

                if(cardOption.getCardScheme()!=null) {
                    saveCardOptionAndMakePayment(cardOption);
                }else{
                    Snackbar
                            .make(parentLayout, getString(R.string.err_invalid_card), Snackbar
                                    .LENGTH_SHORT)
                            .show();
                    cardNoET.requestFocus();
                }

            } else {

            }
        }
    }

    private void saveCardOptionAndMakePayment(final CardOption cardOption) {
        Logger.d(TAG + " saving card");
        mListener.showProgressDialog(false, getString(R.string
                .text_saving_card));
        CitrusClient.getInstance(getActivity()).savePaymentOption(cardOption, new
                Callback<CitrusResponse>() {

                    @Override
                    public void success(CitrusResponse citrusResponse) {
                        Logger.d(TAG + " Saved card");
                        EventBus.getDefault().post(new CardSavedEvent());
                        mListener.dismissProgressDialog();
                        mListener.navigateTo(GetCVVFragment.newInstance(cardOption,
                                        transactionType, addMoneyAmount),
                                UIConstants.SCREEN_CVV);
                    }

                    @Override
                    public void error(CitrusError error) {
                        Logger.d(TAG + " could not save Card " + error.getMessage());
                        mListener.dismissProgressDialog();
                    }
                });
    }

    private boolean validateDetails() {
        creditCardNumber = cardNoET.getText().toString().trim().replace(" ", "");
        nameOnCard = ownerNameET.getText().toString().trim();
        if (TextUtils.isEmpty(creditCardNumber)) {
            Snackbar
                    .make(parentLayout, R.string.err_card_no_empty, Snackbar.LENGTH_SHORT)
//                    .setAction(R.string.snackbar_action, myOnClickListener)
                    .show();
            cardNoET.requestFocus();
            return false;
        } else if (creditCardNumber.length() < 15) {

            Snackbar
                    .make(parentLayout, getString(R.string.err_card_no), Snackbar.LENGTH_SHORT)
                    .show();
            cardNoET.requestFocus();

            return false;
        } else if (TextUtils.isEmpty(nameOnCard)) {
            Snackbar
                    .make(parentLayout, R.string.err_owner_name_empty, Snackbar.LENGTH_SHORT)
//                    .setAction(R.string.snackbar_action, myOnClickListener)
                    .show();
            ownerNameET.requestFocus();

            return false;
        } else if (TextUtils.isEmpty(selectedMonth)) {
            Snackbar
                    .make(parentLayout, R.string.err_select_month, Snackbar.LENGTH_SHORT)
//                    .setAction(R.string.snackbar_action, myOnClickListener)
                    .show();
            monthSpinner.requestFocus();

            return false;
        } else if (TextUtils.isEmpty(selectedYear)) {
            Snackbar
                    .make(parentLayout, R.string.err_select_year, Snackbar.LENGTH_SHORT)
//                    .setAction(R.string.snackbar_action, myOnClickListener)
                    .show();
            yearSpinner.requestFocus();

            return false;
        } else if (Integer.parseInt(selectedYear) == Calendar.getInstance().get(Calendar.YEAR)) {
            if (Integer.parseInt(monthListMap.get(selectedMonth)) < Calendar.getInstance().get
                    (Calendar.MONTH)) {
                Snackbar
                        .make(parentLayout, getString(R.string.err_select_month_future), Snackbar
                                .LENGTH_SHORT)
                        .show();
                monthSpinner.requestFocus();
                return false;
            }


        }
        return true;
    }

    private void setupYearSpinner() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);


        for (int i = 0; i <= 80; i++) {
            yearList.add(Integer.toString(i + year));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, yearList);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = yearList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void setupMonthSpinner() {
        monthList = new String[]{
                "JAN", "FEB", "MAR", "APR", "MAY", "JUNE",
                "JULY", "AUG", "SEPT", "OCT", "NOV",
                "DEC",
        };

        monthListMap = new HashMap<String, String>();
        monthListMap.put("JAN", "01");
        monthListMap.put("FEB", "02");
        monthListMap.put("MAR", "03");
        monthListMap.put("APR", "04");
        monthListMap.put("MAY", "05");
        monthListMap.put("JUNE", "06");
        monthListMap.put("JULY", "07");
        monthListMap.put("AUG", "08");
        monthListMap.put("SEPT", "09");
        monthListMap.put("OCT", "10");
        monthListMap.put("NOV", "11");
        monthListMap.put("DEC", "12");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, monthList);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = monthList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private String getFormattedYear(String year) {
        return year.substring(Math.max(year.length() - 2, 0));
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
}
