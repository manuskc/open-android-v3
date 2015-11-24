package com.citrus.sdk.otp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;

import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.Constants;
import com.citrus.sdk.classes.Utils;

public class SMSReceiver extends BroadcastReceiver {
    private CitrusClient citrusClient = null;

    public SMSReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        citrusClient = CitrusClient.getInstance(context);
        NetBankForOTP netBankForOTP = citrusClient.getNetBankForOTP();

        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String otp = "";

        if (bundle != null) {

            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            for (int i = 0; i < msgs.length; i++) {

                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                // Search for the SMS of the current bank.
                if (msgs[i].getOriginatingAddress().contains(netBankForOTP.getBankNameForParsing())) {
                    String message = msgs[i].getMessageBody();
                    otp = Utils.getOTP(message, netBankForOTP);

                    break;
                }
            }

            Intent messageIntent = new Intent(Constants.ACTION_AUTO_READ_OTP);
            messageIntent.putExtra(Constants.INTENT_EXTRA_AUTO_OTP, otp);
            LocalBroadcastManager.getInstance(context).sendBroadcast(messageIntent);
        }
    }
}
