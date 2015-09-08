package com.citrus.retrofit;

import com.citrus.sdk.Environment;
import com.citrus.sdk.response.CitrusLogger;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by MANGESH KADAM on 5/7/2015.
 */
public class
        RetroFitClient {
    private static API RETROFIT_CLIENT;
    private static String CITRUS_ROOT = null;
    private static OkHttpClient okHttpClient = null;
    private static CitrusEndPoint citrusEndPoint;


    private RetroFitClient() {}

    public static API getCitrusRetroFitClient() {
        return RETROFIT_CLIENT;
    }

    public static void initRetroFitClient(Environment environment) {
        if (environment != null) {
            CITRUS_ROOT = environment.getBaseUrl();
        }
        setupCitrusRetroFitClient();
    }

    private static void setupCitrusRetroFitClient() {
        citrusEndPoint = new CitrusEndPoint(CITRUS_ROOT);
        okHttpClient = new OkHttpClient();
        okHttpClient.setFollowRedirects(false);
        RestAdapter builder = new RestAdapter.Builder()
                .setEndpoint(citrusEndPoint)
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(CitrusLogger.isEnableLogs()?RestAdapter.LogLevel.FULL: RestAdapter.LogLevel.NONE)
                .build();

        RETROFIT_CLIENT = builder.create(API.class);

    }

    public static API getBillGeneratorClient(String baseHost){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(baseHost)
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(CitrusLogger.isEnableLogs()? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
        API billGeneratorClient = restAdapter.create(API.class);
        return billGeneratorClient;
    }

    public static API getCitrusBaseUrlClient(String baseHost){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(baseHost)
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(CitrusLogger.isEnableLogs()? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
        API citrusBaseUrlClient = restAdapter.create(API.class);
        return citrusBaseUrlClient;
    }

    public static void  setInterCeptor() {
        okHttpClient.interceptors().add(new ReceivedCookiesInterceptor());
    }

    public static void  removeInterCeptor() {
        okHttpClient.interceptors().clear();
    }

   /* public static CitrusEndPoint getCitrusEndPoint() {
        return citrusEndPoint;
    }*/

    public static void resetEndPoint() {
        citrusEndPoint.setUrl(CITRUS_ROOT);
    }

     public static void setEndPoint(String url) {
        citrusEndPoint.setUrl(url);
    }
}
