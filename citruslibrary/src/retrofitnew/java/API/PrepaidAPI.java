package API;

/**
 * Created by salil on 27/11/15.
 */

import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.sdk.response.PaymentResponse;
import com.google.gson.JsonElement;
import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;

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
    Call<Amount> getBalance(@Header("Authorization") String header, @Field("dummy") String dummyObject);

    /**
     * Get Cookie for the user. This cookie is used to make older Pay Using Citrus Cash API.
     *
     * @param email
     * @param password
     * @param rmCookie
     */
    @FormUrlEncoded
    @POST("/prepaid/pg/_verify")
    Call<String> getCookie(@Field("email") String email, @Field("password") String password, @Field("rmcookie") String rmCookie);

    /**
     * Pay Using Citrus Cash API call. This is newer prepayment api.
     *
     * @param header
     * @param body
     */
    @Headers("Content-Type: application/json")
    @POST("/service/v2/prepayment/prepaid_pay")
    Call<JsonElement> payUsingCitrusCash(@Header("Authorization") String header, @Body ResponseBody body);

    /**
     * This will return the validity for the token. Mostly used while making new pay using Citrus Cash API.
     *
     * @param signupToken
     * @param prepaymentToken
     * @param scope
     */
    @GET("/service/v2/token/validate")
    Call<JsonElement> getPrepaymentTokenValidity(@Header("Authorization") String signupToken, @Header("OwnerAuthorization") String prepaymentToken, @Header("OwnerScope") String scope);

    /**
     * Activate user's prepaid account. If the account is not active the user's default balance is -1.
     * On activation the balance will become 0.
     * <p/>
     * This API will activate user's prepaid account and return the balance as zero, if the user's
     * prepaid account is already active it will return its wallet balance.
     *
     * @param header
     */
    @GET("/service/v2/prepayment/balance")
    Call<Amount> activatePrepaidUser(@Header("Authorization") String header);

    /**
     * Send money from sender's wallet to the receiver's wallet using email Id.
     * Do not use this API, use {@link PrepaidAPI#sendMoneyByMobile(String, String, String, String, String)}
     *
     * @param header
     * @param amount
     * @param currency
     * @param message
     * @param emailId
     */
    @FormUrlEncoded
    @POST("/service/v2/prepayment/transfer")
    Call<PaymentResponse> sendMoneyByEmail(@Header("Authorization") String header, @Field("amount") String amount, @Field("currency") String currency, @Field("message") String message, @Field("to") String emailId);

    /**
     * Send money from sender's wallet to the receiver's wallet using mobile no.
     *
     * @param header
     * @param amount
     * @param currency
     * @param message
     * @param mobileNo
     */
    @FormUrlEncoded
    @POST("/service/v2/prepayment/transfer/extended")
    Call<PaymentResponse> sendMoneyByMobile(@Header("Authorization") String header, @Field("amount") String amount, @Field("currency") String currency, @Field("message") String message, @Field("to") String mobileNo);

    /**
     * Withdraw money from user's wallet to the bank account.
     *
     * @param header
     * @param amount
     * @param currency
     * @param owner
     * @param accountNo
     * @param ifscCode
     */
    @FormUrlEncoded
    @POST("/service/v2/prepayment/cashout")
    Call<PaymentResponse> cashout(@Header("Authorization") String header, @Field("amount") String amount, @Field("currency") String currency, @Field("owner") String owner, @Field("account") String accountNo, @Field("ifsc") String ifscCode);

    /**
     * Get the cashout information i.e. bank account details used for withdrawal.
     *
     * @param header
     */
    @GET("/service/v2/profile/me/prepaid")
    Call<JsonElement> getCashoutInfo(@Header("Authorization") String header);

    /**
     * Save the cashout information i.e. bank account details used for withdrawal.
     *
     * @param header
     * @param body
     */
    @Headers("Content-Type: application/json")
    @PUT("/service/v2/profile/me/prepaid")
    /** {"cashoutAccount":{"owner":"Yadnesh Wankhede","branch":"HSBC0000123","number":"123456789987654"},"type":"prepaid","currency":"INR"} */
    Call<CitrusResponse> saveCashoutInfo(@Header("Authorization") String header, @Body ResponseBody body);
}
