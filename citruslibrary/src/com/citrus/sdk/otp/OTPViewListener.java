package com.citrus.sdk.otp;

/**
 * Created by salil on 20/10/15.
 */
public interface OTPViewListener {

    void onSendOtpClicked();
    void onGeneratePasswordClicked();
    void onCancelClicked();
    void onProcessTransactionClicked();
    void onResendOTPClicked();
}
