package com.citrus.sdk.ui.utils;

import android.graphics.Color;

/**
 * Created by akshaykoul on 5/29/15.
 */
public class UIConstants {
    public static final java.lang.String PHONE_NUM_PREFIX_UI_FORMATTED = "+91- ";
    public static final java.lang.String PHONE_NUM_PREFIX = "+91";
    public static final int PHONE_NUM_MIN_LENGTH_INDIA = 10;
    public static final int DISPLAY_PAYMENT_OPTIONS = 1;
    public static int actionBarItemColor = Color.BLACK;

    public static int WALLET_FLOW = 0;
    public static int QUICK_PAY_FLOW = 1;
    public static String PO_DEBIT = "debit";
    public static String PO_CREDIT = "credit";
    public static String PO_NET_BANKING = "netbanking";
    public static final String ARG_ADD_MONEY_AMOUNT = "add_money_amount";
    public static final String ARG_IS_ADD_MONEY_AND_PAY = "is_add_money_and_pay";
    public static final String ARG_TRANSACTION_TYPE = "transaction_type";
    public static final String ARG_WALLET_BALANCE = "wallet_balance";
    public static final int REQ_CODE_LOGIN = 12120;
    public static final int REQ_CODE_LOGIN_PAY = 12121;
    public static final int REQ_CODE_LOGIN_ADD_PAY = 12122;
    public static int MAX_WALLET_AMOUNT_RECHARGE = 9999;

//    Screens

    public static final int SCREEN_SHOPPING = 0;
    public static final int SCREEN_CVV = 1;
    public static final int SCREEN_ADD_CARD = 2;
    public static final int SCREEN_RESULT = 3;
    public static final int SCREEN_BANK_LIST = 4;
    public static final int SCREEN_WALLET_LOGIN = 5;
    public static final int SCREEN_OTP_CONFIRMATION = 6;
    public static final int SCREEN_SIGNUP = 7;
    public static final int SCREEN_ADD_MONEY = 8;
    public static final int SCREEN_WALLET = 9;
    public static final int SCREEN_MONEY_OPTION = 10;
    public static final int SCREEN_CARD_LIST = 11;
    public static final int SCREEN_ACCOUNT_DETAILS = 12;


    public static final String TRANS_QUICK_PAY = "trans_quick_pay";
    public static final String TRANS_ADD_MONEY = "trans_add_money";
    public static final String TRANS_ADD_MONEY_N_PAY = "trans_add_money_n_pay";
    public static final String TERMS_COND_URL = "http://www.citruspay.com/citrusbanktnc-lite.html";

}
