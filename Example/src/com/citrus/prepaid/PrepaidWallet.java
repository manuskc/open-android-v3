package com.citrus.prepaid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.citrus.asynch.CashOutAsynch;
import com.citrus.asynch.SendMoneyAsync;
import com.citrus.card.Card;
import com.citrus.cash.LoadMoney;
import com.citrus.cash.Prepaid;
import com.citrus.cash.PrepaidPg;
import com.citrus.mobile.Callback;
import com.citrus.mobile.Config;
import com.citrus.mobile.User;
import com.citrus.netbank.Bank;
import com.citrus.netbank.BankPaymentType;
import com.citrus.payment.Bill;
import com.citrus.payment.PG;
import com.citrus.payment.UserDetails;
import com.citrus.sample.GetBill;
import com.citrus.sample.R;
import com.citrus.sample.Utils;
import com.citrus.sample.WebPage;
import com.citrus.sdk.CitrusActivity;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.Constants;
import com.citrus.sdk.Environment;
import com.citrus.sdk.PaymentParams;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.BillGeneratorPOJO;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.classes.Month;
import com.citrus.sdk.classes.Year;
import com.citrus.sdk.payment.DebitCardOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusLogger;
import com.citrus.sdk.response.CitrusResponse;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import static com.citrus.sdk.CitrusClient.getInstance;

public class PrepaidWallet extends Activity {

    private static final String bill_url = "https://salty-plateau-1529.herokuapp.com/billGenerator.sandbox.php?amount=3.0";

    Button isSignedin, linkuser, setpass, forgot, signin, getbalance, card_load, card_loadWebView, token_load, bank_load, token_bank_Load, citrus_cashpay, citruscashWebView, get_prepaidToken, withdrawMoney, sendMoneyByEmail, sendMoneyByMobile, getMerchantPaymentOptions, getWallet;

    Button btnlogoutUser;
    Callback callback;

    String prepaid_bill;

    JSONObject customer;


    CitrusClient citrusClient;

    private final String emailID = "developercitrus@mailinator.com";
    private final String mobileNo = "9769507476";
    private final String password = "Citrus@123";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepaid);

        isSignedin = (Button) this.findViewById(R.id.issignedin);

        linkuser = (Button) this.findViewById(R.id.linkuser);

        setpass = (Button) this.findViewById(R.id.setpassword);

        forgot = (Button) this.findViewById(R.id.forgot);

        signin = (Button) this.findViewById(R.id.signin);

        getbalance = (Button) this.findViewById(R.id.getbalance);

        card_load = (Button) this.findViewById(R.id.cardload);

        card_loadWebView = (Button) this.findViewById(R.id.cardloadWebView);

        token_load = (Button) this.findViewById(R.id.tokenload);

        bank_load = (Button) this.findViewById(R.id.bankload);

        token_bank_Load = (Button) this.findViewById(R.id.tokenbankload);

        citrus_cashpay = (Button) this.findViewById(R.id.citruscash);

        citruscashWebView = (Button) this.findViewById(R.id.citruscashWebView);

        withdrawMoney = (Button) this.findViewById(R.id.withdraw_money);
        sendMoneyByEmail = (Button) this.findViewById(R.id.send_money_by_email);
        sendMoneyByMobile = (Button) this.findViewById(R.id.send_money_by_mobile);
//        getMerchantPaymentOptions = (Button) this.findViewById(R.id.get_merchant_payment_options);
//        getWallet = (Button) this.findViewById(R.id.get_wallet);

        btnlogoutUser = (Button) this.findViewById(R.id.logoutUser);

        customer = new JSONObject();
        citrusClient = getInstance(this);


        callback = new Callback() {

            @Override
            public void onTaskexecuted(String success, String error) {
                showToast(success, error);
            }
        };

        init();

        initconfig();

        initcustdetails();

    }

    private void init() {


        isSignedin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                citrusClient.isUserSignedIn(new com.citrus.sdk.Callback<Boolean>() {
                    @Override
                    public void success(Boolean aBoolean) {
                        Logger.d("USER SIGIN IN*****" + aBoolean);
                    }

                    @Override
                    public void error(CitrusError error) {
                        Logger.d("USER SIGNIN FAIL RESPONSE ***" + error.getMessage());
                    }
                });
            }
        });

        linkuser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                citrusClient.isCitrusMember(emailID, mobileNo, new com.citrus.sdk.Callback<Boolean>() {
                    @Override
                    public void success(Boolean aBoolean) {
                        Logger.d("isUSER LINKED ****" + String.valueOf(aBoolean));
                    }

                    @Override
                    public void error(CitrusError error) {

                    }
                });
            }
        });

        setpass.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                citrusClient.signUp(emailID, mobileNo, password, new com.citrus.sdk.Callback<CitrusResponse>() {
                    @Override
                    public void success(CitrusResponse citrusResponse) {
                        Logger.d("SignUp Response ****" + citrusResponse.getMessage());
                    }

                    @Override
                    public void error(CitrusError error) {
                        Logger.d("SignUp Fail Response ****" + error.getMessage());
                    }
                });
            }
        });

        forgot.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                citrusClient.resetPassword(emailID, new com.citrus.sdk.Callback<CitrusResponse>() {
                    @Override
                    public void success(CitrusResponse citrusResponse) {
                        Logger.d("Reset Password Response ****" + citrusResponse.getMessage());
                    }

                    @Override
                    public void error(CitrusError error) {
                        Logger.d("Reset Password Fail Response ****" + error.getMessage());
                    }
                });
            }
        });

        signin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
             /*   new SignIn(PrepaidWallet.this, callback)
                        .execute(new String[]{emailID, password});*/

                citrusClient.signIn(emailID, password, new com.citrus.sdk.Callback<CitrusResponse>() {
                    @Override
                    public void success(CitrusResponse citrusResponse) {
                        Logger.d("SIGNIN SUCCESSFUL *** " + citrusResponse.getMessage());
                    }

                    @Override
                    public void error(CitrusError error) {
                        Logger.d("SIGNIN Fail Response *** " + error.getMessage());
                    }
                });
            }
        });

        getbalance.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                citrusClient.getBalance(new com.citrus.sdk.Callback<Amount>() {
                    @Override
                    public void success(Amount amount) {
                        Logger.d("Get Balance Success Response **" + amount.toString());
                    }

                    @Override
                    public void error(CitrusError error) {
                        Logger.d("Get Balance Faile Response **" + error.getMessage());
                    }
                });
            }
        });

        card_load.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Card card = new Card("4111111111111111", "04", "21", "778", "Bruce Banner", "debit");

                LoadMoney load = new LoadMoney("5", "https://salty-plateau-1529.herokuapp.com/redirectUrlLoadCash.php");

                UserDetails userDetails = new UserDetails(customer);

                PG paymentgateway = new PG(card, load, userDetails);

                paymentgateway.load(PrepaidWallet.this, new Callback() {
                    @Override
                    public void onTaskexecuted(String success, String error) {
                        processresponse(success, error);
                    }
                });

            }
        });

        card_loadWebView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CitrusUser citrusUser = new CitrusUser("mangesh.kadam@citruspay.com", "8692862420");

                Amount amount = new Amount("5");
                PaymentType paymentType = null;
                try {
                    paymentType = new PaymentType.LoadMoney(amount, "https://salty-plateau-1529.herokuapp.com/redirectUrlLoadCash.php");
                } catch (CitrusException e) {
                    e.printStackTrace();

                    Utils.showToast(PrepaidWallet.this, e.getMessage());
                }
                DebitCardOption debitCardOption = new DebitCardOption("My Debit Card", "4111111111111111", "123", Month.getMonth("05"), Year.getYear("17"));
                PaymentParams paymentParams = PaymentParams.builder(amount, paymentType, debitCardOption)
                        .environment(PaymentParams.Environment.SANDBOX)
                        .user(citrusUser)
                        .build();

                startCitrusActivity(paymentParams);

            }
        });

        token_load.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Card card = new Card("94a4def03fdac35749bfd2746e5cd6f9", "808");

                LoadMoney load = new LoadMoney("5", "https://salty-plateau-1529.herokuapp.com/redirectUrlLoadCash.php");

                UserDetails userDetails = new UserDetails(customer);

                PG paymentgateway = new PG(card, load, userDetails);

                paymentgateway.load(PrepaidWallet.this, new Callback() {
                    @Override
                    public void onTaskexecuted(String success, String error) {
                        processresponse(success, error);
                    }
                });

            }
        });

        bank_load.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Bank netbank = new Bank("CID002");

                LoadMoney load = new LoadMoney("5", "https://salty-plateau-1529.herokuapp.com/redirectUrlLoadCash.php");

                UserDetails userDetails = new UserDetails(customer);

                PG paymentgateway = new PG(netbank, load, userDetails);

                paymentgateway.load(PrepaidWallet.this, new Callback() {
                    @Override
                    public void onTaskexecuted(String success, String error) {
                        processresponse(success, error);
                    }
                });

            }
        });

        token_bank_Load.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                Bank netbank = new Bank("b66352b2d465699d6fa7cfb520ba27b5", BankPaymentType.TOKEN);


                LoadMoney load = new LoadMoney("1", "https://salty-plateau-1529.herokuapp.com/redirectUrlLoadCash.php");

                UserDetails userDetails = new UserDetails(customer);

                PG paymentgateway = new PG(netbank, load, userDetails);

                paymentgateway.load(PrepaidWallet.this, new Callback() {
                    @Override
                    public void onTaskexecuted(String success, String error) {
                        processresponse(success, error);
                    }
                });

            }
        });

        citrus_cashpay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new GetBill(bill_url, new Callback() {

                    @Override
                    public void onTaskexecuted(String bill, String error) {
                        if (!TextUtils.isEmpty(bill))
                            walletpay(bill);

                        showToast(bill, error);
                    }
                })
                        .execute();
            }
        });


        citruscashWebView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CitrusUser citrusUser = new CitrusUser(emailID, "");

                Amount amount = new Amount("5");

                PaymentType paymentType = null;
                try {
                    paymentType = new PaymentType.CitrusCash(amount, "https://salty-plateau-1529.herokuapp.com/billGenerator.sandbox.php?" + "amount=" + amount.getValue());
                } catch (CitrusException e) {
                    e.printStackTrace();

                    Utils.showToast(PrepaidWallet.this, e.getMessage());
                }


                PaymentParams paymentParams = PaymentParams.builder(amount, paymentType, null)
                        .environment(PaymentParams.Environment.SANDBOX)
                        .user(citrusUser)
                        .build();

                startCitrusActivity(paymentParams);


            }
        });

        withdrawMoney.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new CashOutAsynch(PrepaidWallet.this, 10, "Salil Godbole", "042401523201", "ICIC0000424", callback).execute();

            }
        });

        sendMoneyByEmail.setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Amount amount = new Amount("37");
                                                    CitrusUser user = new CitrusUser("salil.godbole@citruspay.com", "");

                                                    new SendMoneyAsync(PrepaidWallet.this, amount, user, "My contribution", callback).execute();
                                                }
                                            }
        );

        sendMoneyByMobile.setOnClickListener(new OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     Amount amount = new Amount("30");
                                                     CitrusUser user = new CitrusUser("", "9970950374");

                                                     new SendMoneyAsync(PrepaidWallet.this, amount, user, "My contribution", callback).execute();
                                                 }
                                             }
        );

        btnlogoutUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.logoutUser(PrepaidWallet.this))
                    Toast.makeText(getApplicationContext(), Constants.LOGOUT_SUCCESS_MESSAGE, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), Constants.LOGOUT_FAIL_MESSAGE, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void processresponse(String response, String error) {

        if (!TextUtils.isEmpty(response)) {
            try {

                JSONObject redirect = new JSONObject(response);
                Intent i = new Intent(PrepaidWallet.this, WebPage.class);

                if (!TextUtils.isEmpty(redirect.getString("redirectUrl"))) {

                    i.putExtra("url", redirect.getString("redirectUrl"));
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
        }

    }


    private void initconfig() {


        Config.setEnv("sandbox"); //replace it with "production" when you are ready
        
        /*Replace following details with oauth details provided to you*/
        Config.setupSignupId("test-signup");
        Config.setupSignupSecret("c78ec84e389814a05d3ae46546d16d2e");

        Config.setSigninId("test-signin");
        Config.setSigninSecret("52f7e15efd4208cf5345dd554443fd99");

        CitrusLogger.enableLogs();

            citrusClient.init("test-signup", "c78ec84e389814a05d3ae46546d16d2e", "test-signin", "52f7e15efd4208cf5345dd554443fd99", "prepaid", Environment.SANDBOX);



    }

    private void initcustdetails() {
        /*All the below mentioned parameters are mandatory - missing anyone of them may create errors
         * Do not change the key in the json below - only change the values*/

        try {
            customer.put("firstName", "Tester");
            customer.put("lastName", "Citrus");
            customer.put("email", emailID);
            customer.put("mobileNo", mobileNo);
            customer.put("street1", "streetone");
            customer.put("street2", "streettwo");
            customer.put("city", "Mumbai");
            customer.put("state", "Maharashtra");
            customer.put("country", "India");
            customer.put("zip", "400052");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void walletpay(String bill_string) {
        Bill bill = new Bill(bill_string);

        Prepaid prepaid = new Prepaid(emailID);

        UserDetails userDetails = new UserDetails(customer);

        PG paymentgateway = new PG(prepaid, bill, userDetails);

        paymentgateway.charge(new Callback() {
            @Override
            public void onTaskexecuted(String success, String error) {
                prepaidPayment(success, error);
            }
        });
    }


    private void walletpay(BillGeneratorPOJO billGeneratorPOJO) {
        Bill bill = new Bill(billGeneratorPOJO);

        Prepaid prepaid = new Prepaid(emailID);

        UserDetails userDetails = new UserDetails(customer);

        PG paymentgateway = new PG(prepaid, bill, userDetails);

        paymentgateway.charge(new Callback() {
            @Override
            public void onTaskexecuted(String success, String error) {
                prepaidPayment(success, error);
            }
        });
    }


    private void prepaidPayment(String response, String error) {

        if (TextUtils.isEmpty(response.toString())) {
            return;
        }

        Callback prepaidCb = new Callback() {

            @Override
            public void onTaskexecuted(String success, String error) {
                showToast(success, error);
            }
        };

        PrepaidPg paymentPg = new PrepaidPg(PrepaidWallet.this);

        paymentPg.pay(prepaidCb, response, error);
    }

    private void showToast(String message, String error) {
        if (!TextUtils.isEmpty(message))
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        if (!TextUtils.isEmpty(error))
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }


    private void startCitrusActivity(PaymentParams paymentParams) {
        Intent intent = new Intent(PrepaidWallet.this, CitrusActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_PAYMENT_PARAMS, paymentParams);
        startActivityForResult(intent, Constants.REQUEST_CODE_PAYMENT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TransactionResponse transactionResponse = data.getParcelableExtra(Constants.INTENT_EXTRA_TRANSACTION_RESPONSE);
        if (transactionResponse != null) {
            Toast.makeText(getApplicationContext(), transactionResponse.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


  /*  citrus_cashpay.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
            new GetJSONBill("https://salty-plateau-1529.herokuapp.com/billGenerator.sandbox.php", "3.0", new retrofit.Callback<BillGeneratorPOJO>() {
                @Override
                public void success(BillGeneratorPOJO billGeneratorPOJO, Response response) {
                    Log.d("BILLPOJO**", billGeneratorPOJO.getAmount().getValue());

                    walletpay(billGeneratorPOJO);
                }

                @Override
                public void failure(RetrofitError error) {

                }
            }).getJSONBill();
        }
    });*/

    /*public void onEvent(CookieEvents cookieEvents) {
        // Logger.d("COOKIE IN CITRUS CLIENT  ****" + cookieEvents.getCookie());
       // prepaidCookie = cookieEvents.getCookie();
    }*/


}
