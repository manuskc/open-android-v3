package API;

import com.citrus.sdk.classes.PGHealthResponse;
import com.citrus.sdk.classes.StructResponsePOJO;
import com.citrus.sdk.response.CitrusResponse;
import com.google.gson.JsonElement;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
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

/**
 * Created by salil on 27/11/15.
 */
public interface PGAPI {

//    /**
//     * Get bill from merchant's server.
//     *
//     * @param path
//     * @param callback
//     */
//    @GET("/{path}")
//    void getBill(@Path(value = "path", encode = false) String path, Callback<JsonElement> callback);

    /**
     * @param body - JSON Body
     */
    @Headers("Content-Type: application/json")
    @POST("/service/moto/authorize/struct/payment")
    Call<StructResponsePOJO> makeMOTOPayment(@Body RequestBody body);

    /**
     * Use the newer make payment api. Where it will directly give the html which can be directly opened in the webview.
     * Thereby reducing an extra hop.
     *
     * @param body - JSON Body
     */
    @Headers("Content-Type: application/json")
    @POST("/moto/makePayment")
    Call<JsonElement> makePayment(@Body RequestBody body);

    /**
     * Return the available paymentoptions for the merchant.
     * i.e. Enabled Credit/Debit schemes and list of banks enabled.
     *
     * @param vanity
     */
    @FormUrlEncoded
    @POST("/service/v1/merchant/pgsetting")
    Call<JsonElement> getMerchantPaymentOptions(@Field("vanity") String vanity);

    /**
     * Get wallet of the user. i.e. Saved Payment Options of the user. Send user token in the header.
     *
     * @param header
     */
    @GET("/service/v2/profile/me/payment")
    Call<JsonElement> getWallet(@Header("Authorization") String header);

    /**
     * Apply Dynamic Pricing.
     *
     * @param body
     */
    @Headers("Content-Type: application/json")
    @POST("/dynamicpricing/performDynamicPricing")
    Call<JsonElement> performDynamicPricing(@Body ResponseBody body);

    /**
     * Set Default Option in user's wallet.
     *
     * @param header
     * @param body
     */
    @Headers("Content-Type: application/json")
    @PUT("/service/v2/profile/me/payment")
    Call<CitrusResponse> setDefaultPaymentOption(@Header("Authorization") String header, @Body ResponseBody body);

    /**
     * Save Payment Option in particular user's wallet.
     *
     * @param header
     * @param body
     */
    @Headers("Content-Type: application/json")
    @PUT("/service/v2/profile/me/payment")
    Call<CitrusResponse> savePaymentOption(@Header("Authorization") String header, @Body ResponseBody body);

    /**
     * Delete the Payment Option from the user's wallet.
     *
     * @param header
     * @param token
     */
    // The response is 200 Ok.
    @DELETE("/service/v2/profile/me/deletepayment/{token}")
    Call<JsonElement> deletePaymentOption(@Header("Authorization") String header, @Path("token") String token);

    /**
     * Get the Card Details such as scheme, bank name using the first 6 digits of the card number.
     *
     * @param first6Digits
     */
    @GET("/binservice/v2/bin/{first6Digits}")
    Call<JsonElement> getCardType(@Path("first6Digits") String first6Digits);

    /**
     * Get the Card Details such as scheme, bank name using the first 6 digits of the card number.
     *
     * @param first6Digits
     */
    @GET("/binservice/v2/bin/{first6Digits}")
    Call<JsonElement> getBinInfo(@Path("first6Digits") String first6Digits);

    /**
     * Get the Card Details such as scheme, bank name using the card token saved in the wallet.
     *
     * @param token
     */
    @GET("/cards/metadata/{token}")
    Call<JsonElement> getBinInfoUsingToken(@Path("token") String token);


    //get merchant name by vanity  --     //"utility/nagama/merchantName";
    @GET("/utility/{path}/merchantName")
    Call<JsonElement> getMerchantName(@Path("path") String path);

    // PG Health API
    @FormUrlEncoded
    @POST("/utility/{path}/pgHealth")
    Call<PGHealthResponse> getPGHealth(@Path("path") String path, @Field("bankCode") String bankCode);

    // PG Health API
    @FormUrlEncoded
    @POST("/utility/{path}/pgHealth")
    Call<JsonElement> getPGHealthForAllBanks(@Path("path") String path, @Field("bankCode") String bankCode);
}
