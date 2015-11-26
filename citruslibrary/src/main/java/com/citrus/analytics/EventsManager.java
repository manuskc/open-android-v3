package com.citrus.analytics;

import android.content.Context;
import android.os.Build;

import com.citrus.retrofit.API;
import com.citrus.retrofit.RetroFitClient;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.Constants;
import com.citrus.sdk.Environment;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by MANGESH KADAM on 4/24/2015.
 */
public class EventsManager {


    private final static String WEBVIEW_EVENTS = "WEBVIEW_EVENTS";

    private final static String PAYMENT_EVENTS = "PAYMENT_EVENTS";


    private final static String INIT_EVENTS = "SDK_VERSION";

    /**
     * This function will be called to log WebView related events
     *
     * @param context
     * @param webViewEvents
     * @param paymentType
     */
    public static void logWebViewEvents(Context context, WebViewEvents webViewEvents, PaymentType paymentType) {

        CitrusClient citrusClient = CitrusClient.getInstance(context);

        ConnectionType connectionType = ConnectionManager.getNetworkClass(context);
        Tracker t = (CitrusLibraryApp.getTracker(
                CitrusLibraryApp.TrackerName.APP_TRACKER, context));

        t.send(new HitBuilders.EventBuilder().setCategory(citrusClient.getVanity())
                .setAction(WEBVIEW_EVENTS).setLabel(getWebViewEventLabel(webViewEvents, connectionType, paymentType))
                .setValue(getWebViewEventValue(webViewEvents, connectionType, paymentType)).build());

//        t.send(new HitBuilders.EventBuilder().setCategory(Config.getVanity())
//                .setAction(WEBVIEW_EVENTS).setLabel(getWebViewEventLabel(webViewEvents, connectionType, paymentType))
//                .setValue(getWebViewEventValue(webViewEvents, connectionType, paymentType)).build());
        //WebViewEvent*ConnectionType*PaymentType*BuildVersion
    }

    /**
     * This function will be called to log payment related events
     *
     * @param context
     * @param paymentType
     * @param transactionType
     */
    public static void logPaymentEvents(Context context, PaymentType paymentType, TransactionType transactionType) {

        CitrusClient citrusClient = CitrusClient.getInstance(context);

        ConnectionType connectionType = ConnectionManager.getNetworkClass(context);

        Tracker t = CitrusLibraryApp.getTracker(CitrusLibraryApp.TrackerName.APP_TRACKER, context);
        t.send(new HitBuilders.EventBuilder().setCategory(citrusClient.getVanity())
                .setAction(PAYMENT_EVENTS).setLabel(getPaymentEventLabel(connectionType, paymentType, transactionType))
                .setValue(getPaymentEventValue(connectionType, paymentType, transactionType)).build());

//        Tracker t = CitrusLibraryApp.getTracker(CitrusLibraryApp.TrackerName.APP_TRACKER, context);
//        t.send(new HitBuilders.EventBuilder().setCategory(Config.getVanity())
//                .setAction(PAYMENT_EVENTS).setLabel(getPaymentEventLabel(connectionType, paymentType, transactionType))
//                .setValue(getPaymentEventValue(connectionType, paymentType, transactionType)).build());
        //ConnectionType*PaymentType*BuildVersion*TransactionType
    }

    public static void logPaymentEvents(Context context, PaymentType paymentType, String failureReason) {

        CitrusClient citrusClient = CitrusClient.getInstance(context);

        ConnectionType connectionType = ConnectionManager.getNetworkClass(context);

        Tracker t = CitrusLibraryApp.getTracker(CitrusLibraryApp.TrackerName.APP_TRACKER, context);
        t.send(new HitBuilders.EventBuilder().setCategory(citrusClient.getVanity())
                .setAction(PAYMENT_EVENTS).setLabel(getPaymentEventLabel(connectionType, paymentType, failureReason))
                .setValue(getPaymentEventValue(connectionType, paymentType, TransactionType.FAIL)).build());

//        Tracker t = CitrusLibraryApp.getTracker(CitrusLibraryApp.TrackerName.APP_TRACKER, context);
//        t.send(new HitBuilders.EventBuilder().setCategory(Config.getVanity())
//                .setAction(PAYMENT_EVENTS).setLabel(getPaymentEventLabel(connectionType, paymentType, failureReason))
//                .setValue(getPaymentEventValue(connectionType, paymentType, TransactionType.FAIL)).build());
        //ConnectionType*PaymentType*BuildVersion*TransactionType
    }

    public static void logInitSDKEvents(final Context context) {

        final CitrusClient client = CitrusClient.getInstance(context);
        Environment environment = client.getEnvironment();
        if (environment != null) {
            API citrusBaseURLClient = RetroFitClient.getClientWithUrl(client.getEnvironment().getBaseCitrusUrl());

            citrusBaseURLClient.getMerchantName(client.getVanity(), new Callback<Response>() {
                @Override
                public void success(Response s, Response response) {

                    String merchantName = new String(((TypedByteArray) response.getBody()).getBytes());
                    Tracker t = CitrusLibraryApp.getTracker(CitrusLibraryApp.TrackerName.APP_TRACKER, context);
                    t.send(new HitBuilders.EventBuilder().setCategory(merchantName)
                            .setAction(INIT_EVENTS).setLabel(String.valueOf(Constants.SDK_VERSION_CODE))
                            .setValue(Long.valueOf(Constants.SDK_VERSION_CODE)).build());

                }

                @Override
                public void failure(RetrofitError error) {

                    Tracker t = CitrusLibraryApp.getTracker(CitrusLibraryApp.TrackerName.APP_TRACKER, context);
                    t.send(new HitBuilders.EventBuilder().setCategory(client.getVanity())
                            .setAction(INIT_EVENTS).setLabel(String.valueOf(Constants.SDK_VERSION_CODE))
                            .setValue(Long.valueOf(Constants.SDK_VERSION_CODE)).build());

                }
            });

//            citrusBaseURLClient.getMerchantName(Config.getVanity(), new Callback<Response>() {
//                @Override
//                public void success(Response s, Response response) {
//
//                    String merchantName = new String(((TypedByteArray) response.getBody()).getBytes());
//                    Tracker t = CitrusLibraryApp.getTracker(CitrusLibraryApp.TrackerName.APP_TRACKER, context);
//                    t.send(new HitBuilders.EventBuilder().setCategory(merchantName)
//                            .setAction(INIT_EVENTS).setLabel(String.valueOf(Constants.SDK_VERSION))
//                            .setValue(Long.valueOf(Constants.SDK_VERSION)).build());
//
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//
//                    Tracker t = CitrusLibraryApp.getTracker(CitrusLibraryApp.TrackerName.APP_TRACKER, context);
//                    t.send(new HitBuilders.EventBuilder().setCategory(Config.getVanity())
//                            .setAction(INIT_EVENTS).setLabel(String.valueOf(Constants.SDK_VERSION))
//                            .setValue(Long.valueOf(Constants.SDK_VERSION)).build());
//
//                }
//            });
        }
    }

    /**
     * This function will return value for webview events
     *
     * @param webViewEvents
     * @param connectionType
     * @param paymentType
     * @return WebViewEvent*ConnectionType*PaymentType*BuildVersion
     */
    private static long getWebViewEventValue(WebViewEvents webViewEvents, ConnectionType connectionType, PaymentType paymentType) {
        //long value = webViewEvents.getValue()*connectionType.getValue()*paymentType.getValue()*APILevel.getValue(Build.VERSION.SDK_INT);
        //  return 5L;
        switch (webViewEvents) {
            case OPEN:
                return 1L;
            case BACK_KEY:
                return 2L;
            case CLOSE:
                return 3L;
            default:
                return 0L;
        }

    }

    /**
     * This function will return value for Payment events
     *
     * @param connectionType
     * @param paymentType
     * @param transactionType
     * @return ConnectionType*PaymentType*BuildVersion*TransactionType
     */
    private static long getPaymentEventValue(ConnectionType connectionType, PaymentType paymentType, TransactionType transactionType) {
        //long value = connectionType.getValue()*paymentType.getValue()*APILevel.getValue(Build.VERSION.SDK_INT)*transactionType.getValue();
        switch (transactionType) {
            case SUCCESS:
                return 4L;
            case FAIL:
                return 5L;
            default:
                return 0L;
        }
    }

    /**
     * This function will return Label for WebViewEvents
     *
     * @param webViewEvents
     * @param connectionType
     * @param paymentType
     * @return consolidated String to log into events
     */
    public static String getWebViewEventLabel(WebViewEvents webViewEvents, ConnectionType connectionType, PaymentType paymentType) {
        String eventLabel = null;
        if (paymentType == PaymentType.NET_BANKING) {
            if (paymentType.getName() != null) {
                eventLabel = webViewEvents.toString() + "_" + connectionType.toString() + "_" + paymentType.toString() + "_" + paymentType.getName() + "_" + String.valueOf(Build.VERSION.SDK_INT) + "_" + Constants.SDK_VERSION_CODE;
            } else {
                eventLabel = webViewEvents.toString() + "_" + connectionType.toString() + "_" + paymentType.toString() + "_" + String.valueOf(Build.VERSION.SDK_INT) + "_" + Constants.SDK_VERSION_CODE;
            }

        } else {
            eventLabel = webViewEvents.toString() + "_" + connectionType.toString() + "_" + paymentType.toString() + "_" + String.valueOf(Build.VERSION.SDK_INT) + "_" + Constants.SDK_VERSION_CODE;
        }

        return eventLabel;
    }

    /**
     * This function will return Label for PaymentEvents
     *
     * @param connectionType
     * @param paymentType
     * @param transactionType
     * @return consolidated String to log into events
     */
    public static String getPaymentEventLabel(ConnectionType connectionType, PaymentType paymentType, TransactionType transactionType) {
        String eventLabel = null;
        if (paymentType == PaymentType.NET_BANKING) {
            if (paymentType.getName() != null) {
                eventLabel = connectionType.toString() + "_" + paymentType.toString() + "_" + paymentType.getName() + "_" + String.valueOf(Build.VERSION.SDK_INT) + "_" + transactionType.toString() + "_" + Constants.SDK_VERSION_CODE;
            } else {
                eventLabel = connectionType.toString() + "_" + paymentType.toString() + "_" + String.valueOf(Build.VERSION.SDK_INT) + "_" + transactionType.toString() + "_" + Constants.SDK_VERSION_CODE;
            }

        } else {
            eventLabel = connectionType.toString() + "_" + paymentType.toString() + "_" + String.valueOf(Build.VERSION.SDK_INT) + "_" + transactionType.toString() + "_" + Constants.SDK_VERSION_CODE;
        }
        return eventLabel;
    }

    /**
     * This function will return Label for PaymentEvents
     *
     * @param connectionType
     * @param paymentType
     * @param failureReason
     * @return consolidated String to log into events
     */
    public static String getPaymentEventLabel(ConnectionType connectionType, PaymentType paymentType, String failureReason) {
        String eventLabel = null;
        if (paymentType == PaymentType.NET_BANKING) {
            if (paymentType.getName() != null) {
                eventLabel = connectionType.toString() + "_" + paymentType.toString() + "_" + paymentType.getName() + "_" + String.valueOf(Build.VERSION.SDK_INT) + "_" + TransactionType.FAIL.toString() + "_" + failureReason + "_" + Constants.SDK_VERSION_CODE;
            } else {
                eventLabel = connectionType.toString() + "_" + paymentType.toString() + "_" + String.valueOf(Build.VERSION.SDK_INT) + "_" + TransactionType.FAIL.toString() + "_" + failureReason + "_" + Constants.SDK_VERSION_CODE;
            }

        } else {
            eventLabel = connectionType.toString() + "_" + paymentType.toString() + "_" + String.valueOf(Build.VERSION.SDK_INT) + "_" + TransactionType.FAIL.toString() + "_" + failureReason + "_" + Constants.SDK_VERSION_CODE;
        }
        return eventLabel;
    }


}
