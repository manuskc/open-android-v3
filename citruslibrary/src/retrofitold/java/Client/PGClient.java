package Client;

import com.citrus.sdk.Callback;
import com.citrus.sdk.Environment;
import com.citrus.sdk.payment.PaymentOption;
import com.google.gson.JsonElement;
import com.squareup.okhttp.OkHttpClient;

import API.PGAPI;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by salil on 27/11/15.
 */
public class PGClient {
    private static PGClient instance = null;
    private String signupId = null;
    private String signupSecret = null;
    private String signinId = null;
    private String signinSecret = null;
    private String vanity = null;
    private Environment environment = null;
    private RestAdapter builder = null;
    private PGAPI pgapi = null;

    public static PGClient getInstance() {
        if (instance == null) {
            synchronized (PGClient.class) {
                if (instance == null) {
                    instance = new PGClient();
                }
            }
        }

        return instance;
    }

    private PGClient() {
    }

    private void init(String signupId, String signupSecret, String signinId, String signinSecret, String vanity, Environment environment) {
        this.signupId = signupId;
        this.signupSecret = signupSecret;
        this.signinId = signinId;
        this.signinSecret = signinSecret;
        this.vanity = vanity;
        this.environment = environment;

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setFollowRedirects(false);

        builder = new RestAdapter.Builder()
                .setEndpoint(environment.getBaseUrl())
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        pgapi = builder.create(PGAPI.class);
    }

    public synchronized void getMerchantPaymentOptions(final Callback<JsonElement> callback) {
        pgapi.getMerchantPaymentOptions(vanity, new retrofit.Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public synchronized void getLoadMoneyPaymentOptions(final Callback<JsonElement> callback) {

    }

}
