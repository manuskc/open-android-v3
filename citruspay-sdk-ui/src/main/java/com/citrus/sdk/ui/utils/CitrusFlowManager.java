package com.citrus.sdk.ui.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.Environment;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.sdk.ui.R;
import com.citrus.sdk.ui.activities.CitrusUIActivity;

/**
 * Created by akshaykoul on 5/28/15.
 */
public class CitrusFlowManager {

    public static final String KEY_EMAIL = "key_email";
    public static final String KEY_MOBILE = "key_mobile";
    public static final String KEY_FLOW = "key_flow";
    public static final String KEY_AMOUNT = "key_amount";
    public static final String KEY_STYLE = "key_style";
    public static String billGenerator = "";
    public static String returnURL = "";

    /**
     * Start the shopping flow with style parameter.
     *
     * @param context context of your activity.
     * @param email   users Email.
     * @param phone   users phoneNo.
     * @param amount  amount to be paid.
     * @param style   add custom Style to the flow.
     */
    public static void startShoppingFlowStyle(Context context, String email, String phone, String
            amount, int style) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(context, context.getString(R.string.err_email_empty), Toast
                    .LENGTH_SHORT).show();
        }
        else if(!Utils.isValidEmail(email)){
            Toast.makeText(context, "Email address is not valid", Toast
                    .LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone) || phone.length() != 10) {
            Toast.makeText(context, context.getString(R.string.err_phone_empty), Toast
                    .LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(amount) || Double.parseDouble(amount) == 0) {
            Toast.makeText(context, context.getString(R.string.err_amount_empty), Toast
                    .LENGTH_SHORT).show();

        } else if (context != null) {
            Intent intent = new Intent(context, CitrusUIActivity.class);
            intent.putExtra(KEY_EMAIL, email);
            intent.putExtra(KEY_MOBILE, phone);
            intent.putExtra(KEY_FLOW, UIConstants.QUICK_PAY_FLOW);
            intent.putExtra(KEY_AMOUNT, amount);
            intent.putExtra(KEY_STYLE, style);
            context.startActivity(intent);
        }

    }

    /**
     * Start the Shopping(Quick pay) flow.
     *
     * @param context context of your activity.
     * @param email   users Email.
     * @param phone   users phoneNo.
     * @param amount  amount to be paid.
     */
    public static void startShoppingFlow(Context context, String email, String phone, String
            amount) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(context, context.getString(R.string.err_email_empty), Toast
                    .LENGTH_SHORT).show();
        }
        else if(!Utils.isValidEmail(email)){
            Toast.makeText(context, "Email address is not valid", Toast
                    .LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone) || phone.length() != 10) {
            Toast.makeText(context, context.getString(R.string.err_phone_empty), Toast
                    .LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(amount) || Double.parseDouble(amount) == 0) {
            Toast.makeText(context, context.getString(R.string.err_amount_empty), Toast
                    .LENGTH_SHORT).show();

        } else if (context != null) {
            Intent intent = new Intent(context, CitrusUIActivity.class);
            intent.putExtra(KEY_EMAIL, email);
            intent.putExtra(KEY_MOBILE, phone);
            intent.putExtra(KEY_FLOW, UIConstants.QUICK_PAY_FLOW);
            intent.putExtra(KEY_AMOUNT, amount);
            context.startActivity(intent);
        }

    }

    /**
     * Start the wallet flow with the given details.
     *
     * @param context context of your activity
     * @param email   users Email.
     * @param phone   users phoneNo.
     */
    public static void startWalletFlow(Context context, String email, String phone) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(context, context.getString(R.string.err_email_empty), Toast
                    .LENGTH_SHORT).show();
        }
        else if(!Utils.isValidEmail(email)){
            Toast.makeText(context, "Email address is not valid", Toast
                    .LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone) || phone.length() != 10) {
            Toast.makeText(context, context.getString(R.string.err_phone_empty), Toast
                    .LENGTH_SHORT).show();
        } else if (context != null) {
            Intent intent = new Intent(context, CitrusUIActivity.class);
            intent.putExtra(KEY_EMAIL, email);
            intent.putExtra(KEY_MOBILE, phone);
            intent.putExtra(KEY_FLOW, UIConstants.WALLET_FLOW);
            context.startActivity(intent);
        }
    }

    /**
     * Initialize the Citrus client with the required authentication Keys and
     * other required parameters
     *
     * @param signupId           The signup Id you can get this from your Citrus Merchant account
     *                           online.
     * @param signupSecret       The signup Secret you can get this from your Citrus Merchant
     *                           account online.
     * @param signinId           The signin Id you can get this from your Citrus Merchant account
     *                           online.
     * @param signinSecret       The signin Secret you can get this from your Citrus Merchant
     *                           account online.
     * @param actionBarItemColor The color of the components of the ActionBar.
     * @param environment        The environment of the citrus Client Can be Environment.SANDBOX
     *                           or Environment.PRODUCTION
     * @param billGenerator      The bill generator URL merchant needs to Host online.
     * @param returnURL          The return URL for updating Wallet transactions needed to
     */
    public static void initCitrusConfig(String signupId, String signupSecret,
                                        String signinId, String signinSecret, int
                                                actionBarItemColor, Context context, Environment
                                                environment,String vanity, String billGenerator, String
                                                returnURL) {
//        Config.setEnv(environment); //replace it with "production" when you are ready

        /*Replace following details with oauth details provided to you*/
//        Config.setupSignupId(signupId);
//        Config.setupSignupSecret(signupSecret);
//
//        Config.setSigninId(signinId);
//        Config.setSigninSecret(signinSecret);
        UIConstants.actionBarItemColor = actionBarItemColor;
        CitrusClient citrusClient;
        citrusClient = CitrusClient.getInstance(context);
        citrusClient.enableLog(true);
        citrusClient.init(signupId, signupSecret, signinId, signinSecret, vanity , environment);
        CitrusFlowManager.billGenerator = billGenerator;
        CitrusFlowManager.returnURL = returnURL;
    }

    public static void logoutUser(final Context context) {

        final ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Logging user out...");
        mProgressDialog.show();
        CitrusClient.getInstance(context).signOut(new Callback<CitrusResponse>() {
            @Override
            public void success(CitrusResponse citrusResponse) {
                mProgressDialog.dismiss();
                Toast.makeText(context, "User is successfully logged out", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void error(CitrusError error) {
                mProgressDialog.dismiss();
                Toast.makeText(context, "User could not be logged out", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
