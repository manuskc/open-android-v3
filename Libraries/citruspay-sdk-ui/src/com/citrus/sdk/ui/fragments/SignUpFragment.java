package com.citrus.sdk.ui.fragments;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.citrus.analytics.ConnectionManager;
import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.events.FragmentCallbacks;

import com.orhanobut.logger.Logger;


public class SignUpFragment extends Fragment {

    private static final String ARG_EMAIL = "email_param";
    private static final String ARG_MOBILE = "mobile_param";

    private String userEmail;
    private String userMobile;
    TextView emailText, termsView;
    TextView mobileText;
    EditText citrusPassEt;
    View root;
    public static final String TAG = "SignUpFragment$";
    private FragmentCallbacks mListener;

    public static SignUpFragment newInstance(String email, String mobile) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_MOBILE, mobile);
        fragment.setArguments(args);
        return fragment;
    }

    public SignUpFragment() {
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
        root = inflater.inflate(R.layout.fragment_sign_up, container, false);
        emailText = (TextView) root.findViewById(R.id.email_text);
        termsView = (TextView) root.findViewById(R.id.terms_view);
        mobileText = (TextView) root.findViewById(R.id.mobile_text);
        citrusPassEt = (EditText) root.findViewById(R.id.citrus_pass_et);
        emailText.setText(userEmail);
        mobileText.setText(userMobile);
        citrusPassEt.requestFocus();
        root.findViewById(R.id.button_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    mListener.showProgressDialog(false, getString(R.string.text_signing_up));
                    CitrusClient.getInstance(getActivity()).signUp(userEmail, userMobile,
                            citrusPassEt.getText().toString().trim(), new
                                    Callback<CitrusResponse>() {

                                        @Override
                                        public void success(CitrusResponse citrusResponse) {

                                            Logger.d(TAG + " Sign up complete" + citrusResponse
                                                    .getMessage());
                                            if (isAdded()) {
                                                mListener.dismissProgressDialog();
                                                getActivity().setResult(getActivity().RESULT_OK, new
                                                        Intent());
                                                getActivity().finish();
                                            }

                                        }

                                        @Override
                                        public void error(CitrusError error) {
                                            Logger.d(TAG + " Could not sign up " + error
                                                    .getMessage());
                                            mListener.dismissProgressDialog();
                                        }
                                    });
                }
            }
        });
        root.findViewById(R.id.credential_change_button).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
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
        termsView.setOnClickListener(new View.OnClickListener() {
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
        return root;
    }

    private boolean validate() {
        if (TextUtils.isEmpty(citrusPassEt.getText().toString().trim())) {
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
