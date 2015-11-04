package com.citrus.sdk.otp;

/**
 * Created by salil on 20/10/15.
 */
public interface OTPViewListener {

    void onSendOtpClicked();
    void onEnterPasswordClicked();
    void onCancelClicked();
    void onProcessTransactionClicked(String otp);
    void onResendOTPClicked();

    void startOtpReadTimer();
}
