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
package com.citrus.widgets;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.citrus.sdk.classes.Month;
import com.citrus.sdk.classes.Year;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExpiryDate extends EditText {
    private String mLastInput = "";
    private TextWatcher customWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String input = s.toString();
            SimpleDateFormat formatter = new SimpleDateFormat("MM/yy", Locale.GERMANY);
            Calendar expiryDateDate = Calendar.getInstance();

            try {
                expiryDateDate.setTime(formatter.parse(input));
            } catch (ParseException e) {
                if (s.length() == 2 && !mLastInput.endsWith("/")) {
                    int month = Integer.parseInt(input);
                    if (month <= 12) {
                        setText(getText().toString() + "/");
                        setSelection(getText().toString().length());
                    } else {
                        setText("");
                        setSelection(getText().toString().length());
                    }
                } else if (s.length() == 2 && mLastInput.endsWith("/")) {
                    int month = Integer.parseInt(input);
                    if (month <= 12) {
                        setText(getText().toString().substring(0, 1));
                        setSelection(getText().toString().length());
                    } else {
                        setText("");
                        setSelection(getText().toString().length());
                    }
                } else if (s.length() == 1) {
                    int month = Integer.parseInt(input);
                    if (month > 1) {
                        setText("0" + getText().toString() + "/");
                        setSelection(getText().toString().length());
                    }
                } else {

                }

                mLastInput = getText().toString();
                return;

            }
        }
    };

    public ExpiryDate(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }

    public ExpiryDate(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpiryDate(Context context) {
        super(context);
        init();
    }

    public Month getMonth() {
        String cardExpiry = getText().toString();
        Month month = null;

        if (!TextUtils.isEmpty(cardExpiry)) {
            String[] expiry = cardExpiry.split("/");
            if (expiry.length == 2) {
                month = Month.getMonth(expiry[0]);
            }
        }

        return month;
    }

    public Year getYear() {
        String cardExpiry = getText().toString();
        Year year = null;

        if (!TextUtils.isEmpty(cardExpiry)) {
            String[] expiry = cardExpiry.split("/");
            if (expiry.length == 2) {
                year = Year.getYear(expiry[1]);
            }
        }

        return year;
    }

    private void init() {
        addTextChangedListener(customWatcher);
    }

}
