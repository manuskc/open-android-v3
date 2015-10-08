package com.citrus.asynch;

import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;

import com.citrus.citrususer.BindtoPrepaid;
import com.citrus.mobile.Callback;

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.CitrusClient#signUp(String, String, String, com.citrus.sdk.Callback)} instead.
 */
@Deprecated
public class SetPassword extends AsyncTask<String, Void, JSONObject> {
	Activity activity;
	
	Callback callback;
	
	String email, mobile, password;
		
	public SetPassword(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}
	
	@Override
	protected JSONObject doInBackground(String... params) {
		email = params[0];
		
		mobile = params[1];
		
		password = params[2];
		
		BindtoPrepaid pwd = new BindtoPrepaid(activity, email, mobile, password);
		
		JSONObject result = pwd.setPassword();
		
		return result;
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		
		if (result.has("error")) {
			callback.onTaskexecuted("", result.toString());
		}
		
		else {
			callback.onTaskexecuted(result.toString(), "");
		}
	}
}
