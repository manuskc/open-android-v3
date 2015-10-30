package com.citrus.sample;

import com.citrus.sdk.Environment;

/**
 * Created by salil on 13/6/15.
 */
public interface Constants {

    String BILL_URL = "https://salty-plateau-1529.herokuapp.com/billGenerator.sandbox.php";
    String RETURN_URL_LOAD_MONEY = "https://salty-plateau-1529.herokuapp.com/redirectUrlLoadCash.php";

    String SIGNUP_ID = "81el60uyh9-signup";
    String SIGNUP_SECRET = "f78ff04da4779a5c47bd087f5e910a7d";
    String SIGNIN_ID = "81el60uyh9-signin";
    String SIGNIN_SECRET = "f66e1821ddf5c01e77f720c81a9fb7bd";
    String VANITY = "81el60uyh9";
    Environment environment = Environment.SANDBOX;
<<<<<<< HEAD

    boolean enableLogging = false;
=======
    boolean enableLogging = true;
>>>>>>> origin/automation

    String colorPrimaryDark = "#E7961D";
    String colorPrimary = "#F9A323";
    String textColor = "#ffffff";
}
