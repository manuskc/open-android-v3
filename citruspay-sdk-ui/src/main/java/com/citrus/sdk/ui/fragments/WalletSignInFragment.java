package com.citrus.sdk.ui.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrus.analytics.ConnectionManager;
import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.events.FragmentCallbacks;
import com.citrus.sdk.ui.utils.UIConstants;
import com.citrus.sdk.ui.utils.Utils;
import com.orhanobut.logger.Logger;

public class WalletSignInFragment extends Fragment {


    EditText emailAddressET;
    EditText phoneNoET;
    ImageView termsImage;
    public static final String TAG = "WalletSignInFragment$";
    View root;

    private final String password = "Citrus@123";
    private FragmentCallbacks mListener;
    private CitrusClient citrusClient;
    private Callback<Boolean> signinCallback;


    public WalletSignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_wallet_sign_in, container, false);
        emailAddressET = (EditText) root.findViewById(R.id.email_address_et);
        termsImage = (ImageView) root.findViewById(R.id.terms_image);
        phoneNoET = (EditText) root.findViewById(R.id.phone_no_et);
        citrusClient = CitrusClient.getInstance(getActivity());
        getActivity().setResult(getActivity().RESULT_CANCELED, new Intent());
        root.findViewById(R.id.info_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.text_login_dialog_title)
                        .setMessage(R.string.text_login_dialog_message)
                        .setPositiveButton(R.string.ok_got_it, new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .show();
            }
        });
        root.findViewById(R.id.button_get_started).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mListener.navigateTo(new OTPConfirmationFragment(), UIConstants
// .SCREEN_OTP_CONFIRMATION);

                signIn();
            }
        });
        emailAddressET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Logger.d("Enter pressed");
                    signIn();
                }
                return false;
            }
        });
        setPhoneNumPrefix(UIConstants.PHONE_NUM_PREFIX_UI_FORMATTED);
        phoneNoET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith(UIConstants.PHONE_NUM_PREFIX_UI_FORMATTED)) {
                    setPhoneNumPrefix(s.toString());
                    Selection.setSelection(phoneNoET.getText(), phoneNoET
                            .getText().length());
                }
            }
        });
        termsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UIConstants
                        .TERMS_COND_URL));
                startActivity(browserIntent);*/
                String status = String.valueOf(ConnectionManager.getNetworkClass(getActivity()));
                if(status.equals("NOT_CONNECTED")||status.equals("UNKNOWN"))
                {
                    Snackbar.make(root, "Unable to connect to the network", Snackbar.LENGTH_SHORT)
                            .show();
                }
                else
                {
                    mListener.displayTerms(getActivity());
                }

            }
        });
        emailAddressET.setText(mListener.getEmail());
        phoneNoET.setText(mListener.getMobile());
        return root;
    }

    private void signIn() {
        if (validateDetails()) {
            String email = emailAddressET.getText().toString().trim();
            String mobile = phoneNoET.getText().toString().trim();
//            mobile = mobile.replace(UIConstants.PHONE_NUM_PREFIX_UI_FORMATTED,"");
            mListener.showProgressDialog(false, getString(R.string.text_searching_user));
            citrusClient.isCitrusMember(email, mobile, getSigninCallback(email,mobile));
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

    private boolean validateDetails() {
        String mobile = phoneNoET.getText().toString().trim();
        String email = emailAddressET.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            Snackbar.make(root, "Phone number is required", Snackbar.LENGTH_SHORT).show();
            phoneNoET.requestFocus();
            return false;
        } else if (mobile.length() < (UIConstants.PHONE_NUM_MIN_LENGTH_INDIA + UIConstants
                .PHONE_NUM_PREFIX_UI_FORMATTED.length())) {
            Snackbar.make(root, "Please enter correct phone number", Snackbar.LENGTH_SHORT).show();
            phoneNoET.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(email)) {
            Snackbar.make(root, "Email address is required", Snackbar.LENGTH_SHORT).show();
            emailAddressET.requestFocus();
            return false;
        } else if (!Utils.isValidEmail(email)) {
            Snackbar.make(root, "Email address is not valid", Snackbar.LENGTH_SHORT).show();
            emailAddressET.requestFocus();
            return false;
        } else {

            return true;
        }
    }


    private void setPhoneNumPrefix(String phoneNum) {
        Spannable spannable;
        if (phoneNum != null && phoneNum.startsWith(UIConstants.PHONE_NUM_PREFIX_UI_FORMATTED)) {
            spannable = new SpannableString(phoneNum);
        } else if (phoneNum != null && phoneNum.length() == UIConstants
                .PHONE_NUM_MIN_LENGTH_INDIA &&
                TextUtils.isDigitsOnly(phoneNum)) {
            spannable = new SpannableString(UIConstants.PHONE_NUM_PREFIX_UI_FORMATTED + phoneNum);
        } else {
            spannable = new SpannableString(UIConstants.PHONE_NUM_PREFIX_UI_FORMATTED);
        }
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color
                .citrus_label_color)), 0, UIConstants.PHONE_NUM_PREFIX_UI_FORMATTED.length(),
                Spannable
                .SPAN_EXCLUSIVE_EXCLUSIVE);
        Selection.setSelection(phoneNoET.getText(), phoneNoET.getText().length());
        phoneNoET.setText(spannable);
    }

    public Callback<Boolean> getSigninCallback(final String email, final String mobile) {

        return new Callback<Boolean>() {
            @Override
            public void success(Boolean success) {
                if (success) {
                    Logger.d(TAG + " User already exists");
                    mListener.dismissProgressDialog();
                    mListener.navigateTo(OTPConfirmationFragment.newInstance(email,
                                    Utils.formatPhoneNumber(mobile)),
                            UIConstants.SCREEN_OTP_CONFIRMATION);
                } else {
                    Logger.d(TAG + " New user");
                    mListener.dismissProgressDialog();
                    mListener.navigateTo(SignUpFragment.newInstance(email,
                            mobile), UIConstants.SCREEN_SIGNUP);
                }
            }

            @Override
            public void error(CitrusError error) {

            }
        };
    }

}
