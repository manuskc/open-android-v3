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
package com.citrus.sdk.ui.widgets;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.citrus.card.CardType;
import com.citrus.widgets.FilterLength;

import java.util.regex.Pattern;

public class CardNumberEditText extends AppCompatEditText {
	
public static final Pattern CODE_PATTERN = Pattern.compile("([0-9]{0,4})|([0-9]{4}-)+|([0-9]{4}-[0-9]{0,4})+");
public static final Pattern EXP_PATTERN = Pattern.compile("^((0[1-9])|(1[0-2]))//((2009)|(20[1-2][0-9]))$");

public Context context;

private TextWatcher customTextWatcher = new TextWatcher() {
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String testString="";
		if (s.toString().contains(" ")){
			testString = s.toString().replace(" ", "");
		}
		else {
			testString = s.toString();
		}
		
		try {
			String cardType = CardType.typeOf(testString).toString();
			
			String length = FilterLength.valueOf(cardType).getLength();
			
			setFilters(new InputFilter[] { new InputFilter.LengthFilter(Integer.valueOf(length))});
			
			int id = context.getResources().getIdentifier(cardType.toLowerCase(), "drawable", context.getPackageName());
			
			setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(id), null);
			
			setError(null);
		} catch (Exception e) {
			setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		}
		
		
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	
	}
	@Override
	public void afterTextChanged(Editable s) {
		if (s.length() > 0 && !CODE_PATTERN.matcher(s).matches()) {
			String input = s.toString();
			String numbersOnly = keepNumbersOnly(input);
			String code = formatNumbersAsCode(numbersOnly);
			removeTextChangedListener(this);
			setText(code);
			setSelection(code.length());
			addTextChangedListener(this);
		}
	}
};

public CardNumberEditText(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	this.context = context;
	init();
	}
public CardNumberEditText(Context context, AttributeSet attrs) {
	super(context, attrs);
	this.context = context;
	init();
}
public CardNumberEditText(Context context) {
	super(context);
	this.context = context;
	init();
}
private void init() {
	addTextChangedListener(customTextWatcher);
}
private String keepNumbersOnly(CharSequence s) {
	return s.toString().replaceAll("[^0-9]", "");
}

private String formatNumbersAsCode(CharSequence s) {
	int groupDigits = 0;
	String tmp = "";
	for (int i = 0; i < s.length(); ++i) {
		tmp += s.charAt(i);
		++groupDigits;
		if (groupDigits == 4) {
			tmp += " ";
			groupDigits = 0;
		}
	}
	try {
		if (tmp.charAt(tmp.length() - 1) == ' ') {
			tmp = tmp.substring(0, tmp.length() - 1);
		}
	} catch (Exception e) {
		tmp = "";
	}
	
	return tmp;
}

}
