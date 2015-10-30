package com.citrus.sample;

import com.citrus.sdk.Environment;

/**
 * Created by salil on 13/6/15.
 */
public interface Constants {

    String BILL_URL = "https://salty-plateau-1529.herokuapp.com/billGenerator.stg15.php";
    String RETURN_URL_LOAD_MONEY = "https://salty-plateau-1529.herokuapp.com/redirectUrlLoadCash.php";

    String SIGNUP_ID = "44bhnwz541-signup";
    String SIGNUP_SECRET = "b53307a79df74440b79fe79d6d3be668";
    String SIGNIN_ID = "44bhnwz541-signin";
    String SIGNIN_SECRET = "a17bd7db65f7e280fdaf50cb7c5d96e9";
    String VANITY = "TestAutomationVanity";
    Environment environment = Environment.STG15;
    boolean enableLogging = true;

    String colorPrimaryDark = "#E7961D";
    String colorPrimary = "#F9A323";
    String textColor = "#ffffff";
}
