package API;

import com.citrus.sdk.classes.PGHealthResponse;
import com.citrus.sdk.classes.StructResponsePOJO;
import com.citrus.sdk.response.CitrusResponse;
import com.google.gson.JsonElement;

import retrofit.Callback;
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
 * Created by salil on 27/11/15.
 * This class will be used in case of retrofit 1.9.
 */
public interface PGAPI {

    /**
     * Get bill from merchant's server.
     *
     * @param path
     * @param callback
     */
    @GET("/{path}")
    void getBill(@Path(value = "path", encode = false) String path, Callback<JsonElement> callback);

    /**
     * @param body     - JSON Body
     * @param callback
     */
    @Headers("Content-Type: application/json")
    @POST("/service/moto/authorize/struct/payment")
    void makeMOTOPayment(@Body TypedString body, Callback<StructResponsePOJO> callback);

    /**
     * Use the newer make payment api. Where it will directly give the html which can be directly opened in the webview.
     * Thereby reducing an extra hop.
     *
     * @param body     - JSON Body
     * @param callback
     */
    @Headers("Content-Type: application/json")
    @POST("/moto/makePayment")
    void makePayment(@Body TypedString body, Callback<Response> callback);

    /**
     * Return the available paymentoptions for the merchant.
     * i.e. Enabled Credit/Debit schemes and list of banks enabled.
     *
     * @param vanity
     * @param callback
     */
    @FormUrlEncoded
    @POST("/service/v1/merchant/pgsetting")
    void getMerchantPaymentOptions(@Field("vanity") String vanity, Callback<JsonElement> callback);

    /**
     * Get wallet of the user. i.e. Saved Payment Options of the user. Send user token in the header.
     *
     * @param header
     * @param callback
     */
    @GET("/service/v2/profile/me/payment")
    void getWallet(@Header("Authorization") String header, Callback<JsonElement> callback);

    /**
     * Apply Dynamic Pricing.
     *
     * @param body
     * @param callback
     */
    @Headers("Content-Type: application/json")
    @POST("/dynamicpricing/performDynamicPricing")
    void performDynamicPricing(@Body TypedString body, Callback<JsonElement> callback);

    /**
     * Set Default Option in user's wallet.
     *
     * @param header
     * @param body
     * @param callback
     */
    @Headers("Content-Type: application/json")
    @PUT("/service/v2/profile/me/payment")
    void setDefaultPaymentOption(@Header("Authorization") String header, @Body TypedString body, Callback<CitrusResponse> callback);

    /**
     * Save Payment Option in particular user's wallet.
     *
     * @param header
     * @param body
     * @param callback
     */
    @Headers("Content-Type: application/json")
    @PUT("/service/v2/profile/me/payment")
    void savePaymentOption(@Header("Authorization") String header, @Body TypedString body, Callback<CitrusResponse> callback);

    /**
     * Delete the Payment Option from the user's wallet.
     *
     * @param header
     * @param token
     * @param callback
     */
    // The response is 200 Ok.
    @DELETE("/service/v2/profile/me/deletepayment/{token}")
    void deletePaymentOption(@Header("Authorization") String header, @Path("token") String token, Callback<Response> callback);

    /**
     * Get the Card Details such as scheme, bank name using the first 6 digits of the card number.
     *
     * @param first6Digits
     * @param callback
     */
    @GET("/binservice/v2/bin/{first6Digits}")
    void getCardType(@Path("first6Digits") String first6Digits, Callback<JsonElement> callback);

    /**
     * Get the Card Details such as scheme, bank name using the first 6 digits of the card number.
     *
     * @param first6Digits
     * @param callback
     */
    @GET("/binservice/v2/bin/{first6Digits}")
    void getBinInfo(@Path("first6Digits") String first6Digits, Callback<Response> callback);

    /**
     * Get the Card Details such as scheme, bank name using the card token saved in the wallet.
     *
     * @param token
     * @param callback
     */
    @GET("/cards/metadata/{token}")
    void getBinInfoUsingToken(@Path("token") String token, Callback<Response> callback);


    //get merchant name by vanity  --     //"utility/nagama/merchantName";
    @GET("/utility/{path}/merchantName")
    void getMerchantName(@Path("path") String path, Callback<Response> callback);

    // PG Health API
    @FormUrlEncoded
    @POST("/utility/{path}/pgHealth")
    void getPGHealth(@Path("path") String path, @Field("bankCode") String bankCode, Callback<PGHealthResponse> callback);

    // PG Health API
    @FormUrlEncoded
    @POST("/utility/{path}/pgHealth")
    void getPGHealthForAllBanks(@Path("path") String path, @Field("bankCode") String bankCode, Callback<JsonElement> callback);
}
