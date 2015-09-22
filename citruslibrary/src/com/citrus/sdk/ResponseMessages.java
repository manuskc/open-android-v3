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
 * Created by MANGESH KADAM on 5/20/2015.
 */
public interface ResponseMessages {

    String SUCCESS_MESSAGE_SET_PASSWORD = "User is signed up successfully.";
    String SUCCESS_MESSAGE_SIGNIN = "User Signed In Successfully.";

    String SUCCESS_COOKIE_SIGNIN = "User Cookie Sign In Suceesfully.";


    String SUCCESS_MESSAGE_USER_BIND = "User Bind Successfully.";

    String SUCCESS_MESSAGE_LOAD_MONEY = "Citrus Cash Wallet loaded successfully";
    String ERROR_MESSAGE_LOAD_MONEY = "Failed to load money into Citrus Cash";

    String SUCCESS_MESSAGE_RESET_PASSWORD = "Reset password link has been on your email.";
    String ERROR_MESSAGE_RESET_PASSWORD = "Error: Reset password failed";

    String ERROR_MESSAGE_BLANK_EMAIL_ID_MOBILE_NO = "Please enter emaild id or the mobile no of your friend to send the money";
    String ERROR_MESSAGE_BLANK_MOBILE_NO = "Please enter the mobile no of your friend to send the money";
    String ERROR_MESSAGE_BLANK_AMOUNT = "Please enter the amount to be sent.";
    String ERROR_MESSAGE_INVALID_JSON = "ERROR: Invlid json received.";
    String ERROR_MESSAGE_FAILED_MERCHANT_PAYMENT_OPTIONS = "ERROR: Unable to fetch merchant payment options";
    String ERROR_MESSAGE_BLANK_CONFIG_PARAMS = "Please make sure SignIn Id, SignIn Secret, SignUp Id, SignUp Secret & Vanity";
    String ERROR_MESSAGE_INVALID_MOBILE_NO = "Invalid Mobile No";
    String ERROR_MESSAGE_NULL_PAYMENT_OPTION = "ERROR: PaymentOption is null.";
    String ERROR_SIGNUP_TOKEN_NOT_FOUND = "Have you done SignUp? Token not found.!!!";
    String ERROR_SIGNIN_TOKEN_NOT_FOUND = "Have you done SignIn? Token not found.!!!";

    String ERROR_MESSAGE_LINK_USER = "ERROR: Unable to Link User";
    String ERROR_MESSAGE_SIGNUP_TOKEN = "ERROR: Unable to fetch Signup token.";

    String SUCCESS_MESSAGE_SAVED_PAYMENT_OPTIONS = "Payment Option Saved Successfully.";
    String SUCCESS_MESSAGE_SAVED_CASHOUT_OPTIONS = "Cashout Information Saved Successfully.";
    String SUCCESS_MESSAGE_DELETE_PAYMENT_OPTIONS = "Payment Option Deleted Successfully.";

    String ERROR_NETWORK_CONNECTION = "Please check your internet connection.";
    String ERROR_FAILED_TO_GET_BALANCE = "Failed to get the balance.";

    String ERROR_MESSAGE_INVALID_BILL = "Invalid bill received from server.";
    String ERROR_MESSAGE_BIND_USER = "Failed to bind User!!!";
    String ERROR_MESSAGE_MEMBER_INFO = "Unable to fetch member info";
    String ERROR_MESSAGE_INSUFFICIENT_BALANCE = "The balance in your Citrus Cash account is insufficient. Please load money.";
    String ERROR_MESSAGE_INVALID_CASHOUT_INFO = "Please make sure amount, accoutNo, accountHolderName and ifscCode are not null or empty.";

    String ERROR_MESSAGE_INVALID_PASSWORD = "Invalid Credentials!!! Please check your passsword.";

}