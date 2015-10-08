/*
   Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.citrus.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.citrus.asynch.Binduser;
import com.citrus.asynch.GetWallet;
import com.citrus.asynch.Savecard;
import com.citrus.card.Card;
import com.citrus.mobile.Callback;
import com.citrus.mobile.Config;

public class MainActivity extends Activity {

    Button bind, savecard, getWallet, paybutton, widgets;

    Callback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        callback = new Callback() {
            @Override
            public void onTaskexecuted(String success, String error) {
                showToast(success, error);
            }
        };

        bind = (Button) this.findViewById(R.id.bind);

        savecard = (Button) this.findViewById(R.id.savecard);

        getWallet = (Button) this.findViewById(R.id.getWallet);

        paybutton = (Button) this.findViewById(R.id.paybutton);
        
        widgets = (Button) this.findViewById(R.id.widgets);

        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Binduser(MainActivity.this, callback).execute("shardullavekar123233443545@mailinator.com", "9873432991");
            }
        });

        savecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card card = new Card("4242424242424242", "12", "20", "123", "Bruce Wayne", "debit");

                new Savecard(MainActivity.this, callback).execute(card);
            }
        });

        getWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetWallet(MainActivity.this, callback).execute();
            }
        });

        paybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PaymentPage.class);
                startActivity(intent);
            }
        });
        
        widgets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Widgets.class);
                startActivity(intent);
            }
        });
        
    }

    private void init() {
        Config.setEnv("sandbox"); //replace it with "production" when you are ready
        
        /*Replace following details with oauth details provided to you*/
        Config.setupSignupId("xu86orijrw-signup");
        Config.setupSignupSecret("9552eba0d78c38a77544e5c891a47838");

        Config.setSigninId("xu86orijrw-signin");
        Config.setSigninSecret("6c7e48168c5e7ed96fecdd3bb98f7ad7");
    }

    private void showToast(String message, String error) {
        if (!TextUtils.isEmpty(message))
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        if (!TextUtils.isEmpty(error))
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }

}