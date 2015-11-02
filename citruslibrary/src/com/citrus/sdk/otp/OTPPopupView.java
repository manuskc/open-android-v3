package com.citrus.sdk.otp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citrus.library.R;

/**
 * TODO: document your custom view class.
 */
public class OTPPopupView extends LinearLayout implements View.OnClickListener {

    private Context context;
    private Button btnCancelTransaction;
    private OTPViewListener listener;

    private TextView otpAutoDetectHeaderTxtView = null;
    private ProgressBar otpAutoDetectProgressBar = null;
    private EditText enterOtpEditTxt = null;
    private Button otpResendBtn = null;
    private Button otpConfirmBtn = null;
    private TextView cancelTransactionTxtView = null;
    private boolean otpViewToggleStatus = false;
    private boolean otpDetectedStatus = false;

    public OTPPopupView(Context context) {
        super(context);
        this.context = context;
        init(null, 0);
    }

    public OTPPopupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs, 0);
    }

    private void init(AttributeSet attrs, int defStyle) {

        setOrientation(LinearLayout.HORIZONTAL);
        inflate(context, R.layout.otp_txn_options, this);
        ImageButton btnEnterPassword = (ImageButton) findViewById(R.id.enterPasswordImgViewId);
        ImageButton btnSendOTP = (ImageButton) findViewById(R.id.sendOtpImgViewId);
        this.cancelTransactionTxtView = (TextView) findViewById(R.id.cancelTransactionTxtId);
        btnEnterPassword.setOnClickListener(this);
        btnSendOTP.setOnClickListener(this);
        this.cancelTransactionTxtView.setOnClickListener(this);
    }

    public void setOTP(String otp) {
        this.otpDetectedStatus = true;
        this.enterOtpEditTxt.setText(otp);
        this.otpConfirmBtn.setBackgroundResource(R.drawable.btn_confirm);
        this.otpResendBtn.setBackgroundResource(R.drawable.btn_resend_disabled);
        this.otpResendBtn.setClickable(false);
        this.otpConfirmBtn.setClickable(true);
        this.otpConfirmBtn.setEnabled(true);
        this.otpAutoDetectProgressBar.setVisibility(View.GONE);
        this.otpAutoDetectHeaderTxtView.setText(R.string.otp_detection_success_text);

    }

    public void otpReadTimeout() {
        if(!this.otpDetectedStatus){
            this.otpConfirmBtn.setClickable(false);
            this.otpConfirmBtn.setEnabled(false);
            this.otpAutoDetectProgressBar.setVisibility(View.GONE);
            this.otpAutoDetectHeaderTxtView.setText(R.string.otp_detection_failed_text);
        }
    }

    public void setOtpViewToggleStatus(boolean toggle){
        this.otpViewToggleStatus = toggle;
    }

    public boolean getOtpViewToggleStatus(){
        return otpViewToggleStatus;
    }

    public void setListener(OTPViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.enterPasswordImgViewId) {
            listener.onGeneratePasswordClicked();
        } else if (i == R.id.sendOtpImgViewId) {
            displayOtpAutoDetectPopup();
            listener.onSendOtpClicked();
            listener.startOtpReadTimer();
        } else if (i == R.id.otpConfirmBtnId) {
            listener.onProcessTransactionClicked();
        } else if (i == R.id.otpResendBtnId) {
            listener.onResendOTPClicked();
        } else if (i == R.id.cancelTransactionTxtId) {
            listener.onCancelClicked();
        }
    }

    public void displayOtpAutoDetectPopup() {
        removeAllViews();
        inflate(context, R.layout.otp_txn_auto_detect, this);

        this.otpAutoDetectHeaderTxtView = (TextView) findViewById(R.id.otpAutoDetectHeaderTxtId);
        this.otpAutoDetectProgressBar = (ProgressBar) findViewById(R.id.otpAutoDetectProgressBarId);
        this.enterOtpEditTxt = (EditText) findViewById(R.id.enterOtpEditTxtId);
        this.otpResendBtn = (Button) findViewById(R.id.otpResendBtnId);
        this.otpConfirmBtn = (Button) findViewById(R.id.otpConfirmBtnId);
        this.cancelTransactionTxtView = (TextView) findViewById(R.id.cancelTransactionTxtId);

        this.otpResendBtn.setOnClickListener(this);
        this.otpConfirmBtn.setOnClickListener(this);
        this.cancelTransactionTxtView.setOnClickListener(this);

    }

}
