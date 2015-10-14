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

package com.citrus.retrofit;

import com.citrus.sdk.classes.AccessToken;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.BindPOJO;
import com.citrus.sdk.classes.CitrusPrepaidBill;
import com.citrus.sdk.classes.PGHealthResponse;
import com.citrus.sdk.classes.StructResponsePOJO;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.sdk.response.PaymentResponse;
import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.ResponseCallback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.mime.TypedString;

/**
 * Created by MANGESH KADAM on 5/7/2015.
 */
public interface API {


    @FormUrlEncoded
    @POST("/oauth/token")
    void getSignUpToken(@Field("client_id") String client_ID, @Field("client_secret") String client_Secret, @Field("grant_type") String grantType, Callback<AccessToken> accessTokenPOJOCallback);


    @FormUrlEncoded
    @POST("/service/v2/identity/bind")
    void getBindResponse(@Header("Authorization") String header, @Field("email") String email, @Field("mobile") String mobile, Callback<BindPOJO> bindPOJOCallback);

    //sign in
    @FormUrlEncoded
    @POST("/oauth/token")
    void getSignInToken(@Field("client_id") String client_ID, @Field("client_secret") String client_Secret, @Field("username") String username, @Field("grant_type") String grantType, Callback<AccessToken> accessTokenPOJOCallback);

    @FormUrlEncoded
    @POST("/oauth/token")
    void getSignInWithPasswordResponse(@Field("client_id") String client_ID, @Field("client_secret") String client_Secret, @Field("username") String username, @Field("password") String password, @Field("grant_type") String grantType, Callback<AccessToken> accessTokenPOJOCallback);

    // Get signin with password token, mostly used for pay using citrus cash.
    @GET("/service/v2/token/validate")
    void getPrepaymentTokenValidity(@Header("Authorization") String signupToken, @Header("OwnerAuthorization") String prepaymentToken, @Header("OwnerScope") String scope, Callback<JsonElement> callback);

    //getCookie
    @FormUrlEncoded
    @POST("/prepaid/pg/_verify")
    void getCookie(@Field("email") String email, @Field("password") String password, @Field("rmcookie") String rmCookie, Callback<String> cookie);

    //set Password
    @FormUrlEncoded
    @PUT("/service/v2/identity/me/password")
    void setPasswordResponse(@Header("Authorization") String header, @Field("old") String oldPassword, @Field("new") String newPassword, Callback<ResponseCallback> callback);

    @FormUrlEncoded
    @POST("/oauth/token")
    AccessToken getRefreshToken(@Field("client_id") String client_ID, @Field("client_secret") String client_Secret, @Field("grant_type") String grantType, @Field("refresh_token") String refreshToken);

    @FormUrlEncoded
    @POST("/oauth/token")
    void getRefreshTokenAsync(@Field("client_id") String client_ID, @Field("client_secret") String client_Secret, @Field("grant_type") String grantType, @Field("refresh_token") String refreshToken, Callback<AccessToken> accessTokenPOJOCallback);

    // BindUser by mobile
    @FormUrlEncoded
    @POST("/service/um/identity/bind/mobile")
    void bindUserByMobile(@Header("Authorization") String header, @Field("email") String email, @Field("mobile") String mobile, Callback<BindPOJO> bindPOJOCallback);

    // MemberInfo API
    @Headers("Content-Type: application/json")
    @POST("/service/um/profile/memberInfo")
    void getMemberInfo(@Header("Authorization") String header, @Body TypedString body, Callback<JsonElement> callback);

    //payment options of merchant
    @FormUrlEncoded
    @POST("/service/v2/identity/passwords/reset")
    void resetPassword(@Header("Authorization") String header, @Field("username") String username, Callback<JsonElement> callback);

    //Pay Using Citrus Cash API call
    @Headers("Content-Type: application/json")
    @POST("/service/v2/prepayment/prepaid_pay")
    void payUsingCitrusCash(@Header("Authorization") String header, @Body TypedString body, Callback<JsonElement> callback);

    //return url from citrus
    @Headers("Content-Type: application/json")
    @POST("/service/moto/authorize/struct/payment")
    void getPaymentResponse(@Body TypedString body, Callback<StructResponsePOJO> structResponseCallback);

    //payment options of merchant
    @FormUrlEncoded
    @POST("/service/v1/merchant/pgsetting")
    void getMerchantPaymentOptions(@Field("vanity") String vanity, Callback<JsonElement> callback);

    // Get wallet of the user.
    @GET("/service/v2/profile/me/payment")
    void getWallet(@Header("Authorization") String header, Callback<JsonElement> callback);

    // Get the prepaid balance of the user. -this is old method to get Balance
//    @GET("/service/v2/mycard")
//    void getBalance(@Header("Authorization") String header, Callback<Amount> callback);

    // Activate the prepaid user.
    @GET("/service/v2/prepayment/balance")
    void activatePrepaidUser(@Header("Authorization") String header, Callback<Amount> callback);

    // Get the prepaid balance of the user. this is new method to get Balance
    @POST("/service/v2/mycard/balance")
    void getBalance(@Header("Authorization") String header, Callback<Amount> callback);

    //bill generator response
    @GET("/{path}")
    void getBill(@Path(value = "path", encode = false) String path, Callback<JsonElement> callback);

    // Dynamic Pricing
    @Headers("Content-Type: application/json")
    @POST("/dynamicpricing/performDynamicPricing")
    void performDynamicPricing(@Body TypedString body, Callback<JsonElement> callback);

    // Save payment option
    @Headers("Content-Type: application/json")
    @PUT("/service/v2/profile/me/payment")
    void savePaymentOption(@Header("Authorization") String header, @Body TypedString body, Callback<CitrusResponse> callback);

    // The response is 200 Ok.
    @DELETE("/service/v2/profile/me/deletepayment/{token}")
    void deletePaymentOption(@Header("Authorization") String header, @Path("token") String token, Callback<Response> callback);

    // Send money by email
    @FormUrlEncoded
    @POST("/service/v2/prepayment/transfer")
    void sendMoneyByEmail(@Header("Authorization") String header, @Field("amount") String amount, @Field("currency") String currency, @Field("message") String message, @Field("to") String emailId, Callback<PaymentResponse> callback);

    // Send money by mobile
    @FormUrlEncoded
    @POST("/service/v2/prepayment/transfer/extended")
    void sendMoneyByMobile(@Header("Authorization") String header, @Field("amount") String amount, @Field("currency") String currency, @Field("message") String message, @Field("to") String mobileNo, Callback<PaymentResponse> callback);

    @FormUrlEncoded
    @POST("/service/v2/prepayment/load")
    void getPrepaidBill(@Header("Authorization") String header, @Field("amount") String amount, @Field("redirect") String redirectURL, @Field("currency") String currency, Callback<CitrusPrepaidBill> callback);

    //get merchant name by vanity  --     //"utility/nagama/merchantName";
    @GET("/utility/{path}/merchantName")
    void getMerchantName(@Path("path") String path, Callback<Response> callback);

    // Cashout APIs.
    @FormUrlEncoded
    @POST("/service/v2/prepayment/cashout")
    void cashout(@Header("Authorization") String header, @Field("amount") String amount, @Field("currency") String currency, @Field("owner") String owner, @Field("account") String accountNo, @Field("ifsc") String ifscCode, Callback<PaymentResponse> callback);

    @GET("/service/v2/profile/me/prepaid")
    void getCashoutInfo(@Header("Authorization") String header, Callback<JsonElement> callback);

    @Headers("Content-Type: application/json")
    @PUT("/service/v2/profile/me/prepaid")
    /** {"cashoutAccount":{"owner":"Yadnesh Wankhede","branch":"HSBC0000123","number":"123456789987654"},"type":"prepaid","currency":"INR"} */
    void saveCashoutInfo(@Header("Authorization") String header, @Body TypedString body, Callback<CitrusResponse> callback);

    // PG Health API
    @FormUrlEncoded
    @POST("/utility/{path}/pgHealth")
    void getPGHealth(@Path("path") String path, @Field("bankCode") String bankCode, Callback<PGHealthResponse> callback);

    // PG Health API
    @FormUrlEncoded
    @POST("/utility/{path}/pgHealth")
    void getPGHealthForAllBanks(@Path("path") String path, @Field("bankCode") String bankCode, Callback<JsonElement> callback);

    @GET("/service/um/profile/profileInfo")
    void getProfileInfo(@Header("Authorization") String token, Callback<JsonElement> callback);
}