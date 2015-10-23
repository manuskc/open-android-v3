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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.citrus.library.R;

/**
 * TODO: document your custom view class.
 */
public class OTPPopupView extends LinearLayout implements View.OnClickListener{

    private Context context;
    private ImageButton btnEnterPassword;
    private ImageButton btnSendOTP;
    private Button btnCancelTransaction;
    private OTPViewListener listener;

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

        inflate(context, R.layout.otp_txn_options, this);
        this.btnEnterPassword = (ImageButton) findViewById(R.id.enterPasswordImgViewId);
        this.btnSendOTP = (ImageButton) findViewById(R.id.sendOtpImgViewId);
        this.btnEnterPassword.setOnClickListener(this);
        this.btnSendOTP.setOnClickListener(this);
    }

    public void setOTP(String otp) {

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
            listener.onSendOtpClicked();
        }
    }
}
