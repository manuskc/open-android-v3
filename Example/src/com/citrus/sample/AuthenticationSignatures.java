package com.citrus.sample;

/**
 * Created by Gautam on 29/9/15.
 */
public class AuthenticationSignatures {

    String BILL_URL = "";
    String RETURN_URL_LOAD_MONEY = "";

    String SIGNUP_ID = "";
    String SIGNUP_SECRET = "";
    String SIGNIN_ID = "";
    String SIGNIN_SECRET = "";
    String VANITY = "";


    public String getBILL_URL() {
        return BILL_URL;
    }

    public void setBILL_URL(String BILL_URL) {
        this.BILL_URL = BILL_URL;
    }

    public String getRETURN_URL_LOAD_MONEY() {
        return RETURN_URL_LOAD_MONEY;
    }

    public void setRETURN_URL_LOAD_MONEY(String RETURN_URL_LOAD_MONEY) {
        this.RETURN_URL_LOAD_MONEY = RETURN_URL_LOAD_MONEY;
    }

    public String getSIGNUP_ID() {
        return SIGNUP_ID;
    }

    public void setSIGNUP_ID(String SIGNUP_ID) {
        this.SIGNUP_ID = SIGNUP_ID;
    }

    public String getSIGNUP_SECRET() {
        return SIGNUP_SECRET;
    }

    public void setSIGNUP_SECRET(String SIGNUP_SECRET) {
        this.SIGNUP_SECRET = SIGNUP_SECRET;
    }

    public String getSIGNIN_ID() {
        return SIGNIN_ID;
    }

    public void setSIGNIN_ID(String SIGNIN_ID) {
        this.SIGNIN_ID = SIGNIN_ID;
    }

    public String getSIGNIN_SECRET() {
        return SIGNIN_SECRET;
    }

    public void setSIGNIN_SECRET(String SIGNIN_SECRET) {
        this.SIGNIN_SECRET = SIGNIN_SECRET;
    }

    public String getVANITY() {
        return VANITY;
    }

    public void setVANITY(String VANITY) {
        this.VANITY = VANITY;
    }


}
