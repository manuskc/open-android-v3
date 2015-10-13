package com.citrus.sdk.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.ui.events.BalanceUpdateEvent;
import com.orhanobut.logger.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * Created by akshaykoul on 6/1/15.
 */
public class Utils {
    public static final String TAG = "Utils$";
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String IFSC_PATTERN="[A-Z|a-z]{4}[0][\\d|\\w]{6}$";

    public static String getFormattedCardNumber(String cardNumber) {
        if (!TextUtils.isEmpty(cardNumber) && cardNumber.length() == 16) {
            String generatedNumber = "";
            for (int i = 0; i < 4; i++) {
                generatedNumber = generatedNumber + cardNumber.substring(4 * i, 4 * (i + 1)) + " ";
            }
            return generatedNumber.trim();
        } else {
            return cardNumber;
        }
    }

    public static boolean isValidEmail(String strEmail) {
        if (strEmail == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches();
        }
    }

    public static boolean validate(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }


    public static void getBalance(Context context) {

        CitrusClient.getInstance(context).getBalance(new Callback<Amount>() {
            @Override
            public void success(Amount amount) {
                Logger.d(TAG + " get Balance success " + amount.getValue());

                EventBus.getDefault().post(new BalanceUpdateEvent(true, amount));

            }

            @Override
            public void error(CitrusError citrusError) {
                Logger.d(TAG + " get Balance failure " + citrusError.getMessage());
                EventBus.getDefault().post(new BalanceUpdateEvent(false, null));
            }
        });
    }

    public static int dipsToPixels(Context ctx, float dips) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        int px = (int) (dips * scale + 0.5f);
        return px;
    }

    public static String CapitalizeWords(String source){
        StringBuffer res = new StringBuffer();

        String[] strArr = source.split(" ");
        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            str = new String(stringArray);

            res.append(str).append(" ");
        }
        return res.toString();
    }

    public static void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public static void openKeyboard(final EditText editText){
        (new Handler()).postDelayed(new Runnable() {

            public void run() {

                editText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                editText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));

            }
        }, 100);
    }
    public static String formatPhoneNumber(String phoneNumber){
        return  phoneNumber.replace(UIConstants.PHONE_NUM_PREFIX_UI_FORMATTED,"");
    }

    public static boolean isValidIFSC(String stringIFSC)
    {
        Pattern pattern = Pattern.compile(IFSC_PATTERN);
        Matcher matcher = pattern.matcher(stringIFSC);
        return matcher.matches();
    }

    public static boolean isValidWithdrawAmount(String stringWithdrawAmount)

    {
        int amt=Integer.parseInt(stringWithdrawAmount);
        return amt <= 5000 && amt >= 1;
    }
}
