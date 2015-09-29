package com.citrus.sample;

import com.citrus.sdk.Environment;

/**
 * Created by salil on 13/6/15.
 */
public interface Constants {

    String BILL_URL = "https://salty-plateau-1529.herokuapp.com/billGenerator.stg5.php";
    String RETURN_URL_LOAD_MONEY = "https://salty-plateau-1529.herokuapp.com/redirectUrlLoadCash.php";

    String SIGNUP_ID = "nig8xii6uf-signup";
    String SIGNUP_SECRET = "82718fe2b606468f48f180402b4c4589";
    String SIGNIN_ID = "nig8xii6uf-signin";
    String SIGNIN_SECRET = "b32107a0b424ffc1442913cfebcb1492";
    String VANITY = "nig8xii6uf";
    Environment environment = Environment.STG5;
    boolean enableLogging = true;

    String colorPrimaryDark = "#E7961D";
    String colorPrimary = "#F9A323";
    String textColor = "#ffffff";
}
