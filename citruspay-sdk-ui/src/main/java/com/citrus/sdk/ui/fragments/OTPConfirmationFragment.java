package com.citrus.sdk.ui.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.events.FragmentCallbacks;
import com.citrus.sdk.ui.utils.Utils;
import com.orhanobut.logger.Logger;

public class OTPConfirmationFragment extends Fragment {
    private static final String ARG_EMAIL = "email_param";
    private static final String ARG_MOBILE = "mobile_param";


    private String userEmail;
    private String userMobile;
    private android.widget.EditText mobileOtpEt;
    private android.widget.LinearLayout mobileOtpContainer;
    private android.widget.EditText emailOtpEt;
    private android.widget.EditText citrusPassEt;
    private android.widget.TextView secondaryText;
    private android.widget.TextView primaryText;
    private android.widget.TextView forgotPasswordView;
    private android.widget.LinearLayout emailOtpContainer;
    View root;
    private FragmentCallbacks mListener;


    public static OTPConfirmationFragment newInstance(String email, String mobile) {
        OTPConfirmationFragment fragment = new OTPConfirmationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_MOBILE, mobile);
        fragment.setArguments(args);
        return fragment;
    }

    public OTPConfirmationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userEmail = getArguments().getString(ARG_EMAIL);
            userMobile = getArguments().getString(ARG_MOBILE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_otpconfirmation, container, false);
        this.emailOtpContainer = (LinearLayout) root.findViewById(R.id.email_otp_container);
        this.citrusPassEt = (EditText) root.findViewById(R.id.citrus_pass_et);
        this.emailOtpEt = (EditText) root.findViewById(R.id.email_otp_et);
        this.mobileOtpContainer = (LinearLayout) root.findViewById(R.id.mobile_otp_container);
        this.mobileOtpEt = (EditText) root.findViewById(R.id.mobile_otp_et);
        this.primaryText = (TextView) root.findViewById(R.id.primary_text);
        this.forgotPasswordView = (TextView) root.findViewById(R.id.forgot_password_view);
        this.secondaryText = (TextView) root.findViewById(R.id.secondary_text);
        mobileOtpContainer.setVisibility(View.GONE);
        primaryText.setText(userEmail);
        secondaryText.setText(userMobile);
        root.findViewById(R.id.button_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });


        citrusPassEt.setLongClickable(false);
        citrusPassEt.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

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


        citrusPassEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (citrusPassEt.getRight() - citrusPassEt
                            .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        if (citrusPassEt.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD
                                || citrusPassEt.getInputType() == 129) {
                            citrusPassEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType
                                    .TYPE_TEXT_VARIATION_NORMAL);
                            citrusPassEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable
                                    .ic_visibility_off, 0);
                        } else {

                            citrusPassEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType
                                    .TYPE_TEXT_VARIATION_PASSWORD);
                            citrusPassEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable
                                    .ic_visibility_on, 0);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        citrusPassEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Logger.d("Enter pressed");
                    signin();
                }
                return false;
            }
        });
        if (forgotPasswordView != null) {
            this.forgotPasswordView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(userEmail)) {
                        mListener.showProgressDialog(false,getString(R.string.text_processing));
                        CitrusClient.getInstance(getActivity()).resetPassword(userEmail, new Callback<CitrusResponse>() {


                            @Override
                            public void success(CitrusResponse citrusResponse) {
                                mListener.dismissProgressDialog();
                                new AlertDialog.Builder(getActivity())
                                        .setTitle(R.string.text_password_reset_title)
                                        .setMessage(String.format(getString(R.string.text_password_reset_link),userEmail ))
                                        .setPositiveButton(R.string.ok_got_it, new DialogInterface
                                                .OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // continue with delete
                                            }
                                        })
                                        .show();
                            }

                            @Override
                            public void error(CitrusError error) {
                                mListener.dismissProgressDialog();
                                Snackbar.make(root, error.getMessage(), Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }
                }
            });
        }
        Utils.openKeyboard(citrusPassEt);
        return root;
    }

    private void signin() {
        if (validate()) {
            mListener.showProgressDialog(false, getString(R.string.text_signing_in));
            CitrusClient.getInstance(getActivity()).signIn(userEmail, citrusPassEt
                    .getText().toString().trim(), new
                    Callback<CitrusResponse>() {

                        @Override
                        public void success(CitrusResponse citrusResponse) {
                            mListener.dismissProgressDialog();
                            if (isAdded()) {
                                getActivity().setResult(getActivity().RESULT_OK, new
                                        Intent());
                                getActivity().finish();
                            }

                        }

                        @Override
                        public void error(CitrusError error) {
                            mListener.dismissProgressDialog();
                            Snackbar.make(root, getString(R.string.err_mismatch_password), Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(citrusPassEt.getText().toString().trim())) {
            citrusPassEt.requestFocus();
            Snackbar.make(root, getString(R.string.err_enter_password), Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        } else {
            return true;
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


}
