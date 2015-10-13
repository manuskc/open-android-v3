package com.citrus.sample;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.citrus.sdk.ui.utils.CitrusFlowManager;
import com.citrus.sdk.ui.utils.Utils;

public class CustomDetailsActivity extends BaseActivity {

    EditText emailEt;
    EditText mobileEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emailEt = (EditText)findViewById(R.id.email_et);
        mobileEt = (EditText)findViewById(R.id.mobile_et);
        findViewById(R.id.quick_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateDetails()){
                    String mobile = mobileEt.getText().toString().trim();
                    String email = emailEt.getText().toString().trim();
                    CitrusFlowManager.startShoppingFlow(CustomDetailsActivity.this, email, mobile, "5");
                }
            }
        });
        findViewById(R.id.wallet_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateDetails()){
                    String mobile = mobileEt.getText().toString().trim();
                    String email = emailEt.getText().toString().trim();
                    CitrusFlowManager.startWalletFlow(CustomDetailsActivity.this, email, mobile);
                }
            }
        });
    }

    private boolean validateDetails() {
        String mobile = mobileEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        if(TextUtils.isEmpty(mobile)){
            Toast.makeText(this,"Mobile Empty",Toast.LENGTH_SHORT).show();
            mobileEt.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Email Empty",Toast.LENGTH_SHORT).show();
            emailEt.requestFocus();
            return false;
        }else if(!Utils.validate(email)) {
            Toast.makeText(this,"Email address is not valid",Toast.LENGTH_SHORT).show();
            emailEt.requestFocus();
            return false;
        }
        else{

            return true;
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_custom_details;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_custom_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
