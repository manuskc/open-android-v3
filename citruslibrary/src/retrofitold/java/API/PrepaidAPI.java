package API;

import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.sdk.response.PaymentResponse;
import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.mime.TypedString;

/**
 * Created by salil on 27/11/15.
 */
public interface PrepaidAPI {

    /**
     * Get the prepaid balance of the user. this is new method to get Balance.
     * The dummy parameter is sent for some versions of okhttp, where it throws error for empty body.
     */
    @POST("/service/v2/mycard/balance")
    @FormUrlEncoded
    void getBalance(@Header("Authorization") String header, @Field("dummy") String dummyObject, Callback<Amount> callback);

    /**
     * Get Cookie for the user. This cookie is used to make older Pay Using Citrus Cash API.
     *
     * @param email
     * @param password
     * @param rmCookie
     * @param cookie
     */
    @FormUrlEncoded
    @POST("/prepaid/pg/_verify")
    void getCookie(@Field("email") String email, @Field("password") String password, @Field("rmcookie") String rmCookie, Callback<String> cookie);

    /**
     * Pay Using Citrus Cash API call. This is newer prepayment api.
     *
     * @param header
     * @param body
     * @param callback
     */
    @Headers("Content-Type: application/json")
    @POST("/service/v2/prepayment/prepaid_pay")
    void payUsingCitrusCash(@Header("Authorization") String header, @Body TypedString body, Callback<JsonElement> callback);

    /**
     * This will return the validity for the token. Mostly used while making new pay using Citrus Cash API.
     *
     * @param signupToken
     * @param prepaymentToken
     * @param scope
     * @param callback
     */
    @GET("/service/v2/token/validate")
    void getPrepaymentTokenValidity(@Header("Authorization") String signupToken, @Header("OwnerAuthorization") String prepaymentToken, @Header("OwnerScope") String scope, Callback<JsonElement> callback);

    /**
     * Activate user's prepaid account. If the account is not active the user's default balance is -1.
     * On activation the balance will become 0.
     * <p/>
     * This API will activate user's prepaid account and return the balance as zero, if the user's
     * prepaid account is already active it will return its wallet balance.
     *
     * @param header
     * @param callback
     */
    @GET("/service/v2/prepayment/balance")
    void activatePrepaidUser(@Header("Authorization") String header, Callback<Amount> callback);

    /**
     * Send money from sender's wallet to the receiver's wallet using email Id.
     * Do not use this API, use {@link PrepaidAPI#sendMoneyByMobile(String, String, String, String, String, Callback)}
     *
     * @param header
     * @param amount
     * @param currency
     * @param message
     * @param emailId
     * @param callback
     */
    @FormUrlEncoded
    @POST("/service/v2/prepayment/transfer")
    void sendMoneyByEmail(@Header("Authorization") String header, @Field("amount") String amount, @Field("currency") String currency, @Field("message") String message, @Field("to") String emailId, Callback<PaymentResponse> callback);

    /**
     * Send money from sender's wallet to the receiver's wallet using mobile no.
     *
     * @param header
     * @param amount
     * @param currency
     * @param message
     * @param mobileNo
     * @param callback
     */
    @FormUrlEncoded
    @POST("/service/v2/prepayment/transfer/extended")
    void sendMoneyByMobile(@Header("Authorization") String header, @Field("amount") String amount, @Field("currency") String currency, @Field("message") String message, @Field("to") String mobileNo, Callback<PaymentResponse> callback);

    /**
     * Withdraw money from user's wallet to the bank account.
     *
     * @param header
     * @param amount
     * @param currency
     * @param owner
     * @param accountNo
     * @param ifscCode
     * @param callback
     */
    @FormUrlEncoded
    @POST("/service/v2/prepayment/cashout")
    void cashout(@Header("Authorization") String header, @Field("amount") String amount, @Field("currency") String currency, @Field("owner") String owner, @Field("account") String accountNo, @Field("ifsc") String ifscCode, Callback<PaymentResponse> callback);

    /**
     * Get the cashout information i.e. bank account details used for withdrawal.
     *
     * @param header
     * @param callback
     */
    @GET("/service/v2/profile/me/prepaid")
    void getCashoutInfo(@Header("Authorization") String header, Callback<JsonElement> callback);

    /**
     * Save the cashout information i.e. bank account details used for withdrawal.
     *
     * @param header
     * @param body
     * @param callback
     */
    @Headers("Content-Type: application/json")
    @PUT("/service/v2/profile/me/prepaid")
    /** {"cashoutAccount":{"owner":"Yadnesh Wankhede","branch":"HSBC0000123","number":"123456789987654"},"type":"prepaid","currency":"INR"} */
    void saveCashoutInfo(@Header("Authorization") String header, @Body TypedString body, Callback<CitrusResponse> callback);
}
