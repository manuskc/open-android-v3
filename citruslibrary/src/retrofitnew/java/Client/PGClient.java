package Client;

import com.citrus.sdk.Environment;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

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
    }
}
