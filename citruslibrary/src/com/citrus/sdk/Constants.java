/*
 *
 *    Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 */

package com.citrus.sdk;

/**
 * Created by salil on 24/4/15.
 */
public interface Constants {
    String SDK_VERSION = "3.1.0";

    int SDK_VERSION_CODE = 3;

    String INTENT_EXTRA_TRANSACTION_RESPONSE = "INTENT_EXTRA_TRANSACTION_RESPONSE";

    String INTENT_EXTRA_DYNAMIC_PRICING_RESPONSE = "INTENT_EXTRA_DYNAMIC_PRICING_RESPONSE";

    /**
     * Use this constant to pass the payment type. {@link com.citrus.sdk.payment.PaymentType}
     */
    String INTENT_EXTRA_PAYMENT_TYPE = "INTENT_EXTRA_PAYMENT_TYPE";

    String INTENT_EXTRA_REQUEST_CODE_PAYMENT = "INTENT_EXTRA_REQUEST_CODE_PAYMENT";

    @Deprecated
    String INTENT_EXTRA_PAYMENT_PARAMS = "INTENT_EXTRA_PAYMENT_PARAMS";

    int REQUEST_CODE_PAYMENT = 10000;

    String JS_INTERFACE_NAME = "CitrusResponse";
    String HEADER_PREPAID_COOKIE = "prepaiduser-payauth";
    String CITRUS_PREPAID_COOKIE = "prepaiduser-payauth=''";

    String SIGNIN_TOKEN = "signin_token";
    String SIGNUP_TOKEN = "signup_token";
    String PREPAID_TOKEN = "prepaid_token";
    String PAY_USING_CITRUS_CASH_TOKEN = "pay_using_citrus_cash_token";

    String LOGOUT_SUCCESS_MESSAGE = "User Logged Out Successfully.";

    String LOGOUT_FAIL_MESSAGE = "Failed to LogOut!!!";

    // Colors
    String colorPrimaryDark = "#E7961D";
    String colorPrimary = "#F9A323";
    String textColor = "#ffffff";
    String accentColor = "";

    String ACTION_LOAD_MONEY = "ACTION_LOAD_MONEY";
    String ACTION_PAY_USING_CASH = "ACTION_PAY_USING_CASH";
    String ACTION_PG_PAYMENT = "ACTION_PG_PAYMENT";

    String PREPAID_VANITY = "prepaid";
}


