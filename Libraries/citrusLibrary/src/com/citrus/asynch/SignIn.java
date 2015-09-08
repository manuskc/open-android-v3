package com.citrus.asynch;

import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;

import com.citrus.citrususer.LoginUser;
import com.citrus.mobile.Callback;

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.CitrusClient#signIn(String, String, com.citrus.sdk.Callback)} instead.
 */
@Deprecated
public class SignIn extends AsyncTask<String, Void, JSONObject> {
	Activity activity;
	Callback callback;
	
	public SignIn(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}
	
	@Override
	protected JSONObject doInBackground(String... params) {
		String email = params[0];
		String password = params[1];
		LoginUser login = new LoginUser(activity, email, password);
		JSONObject result = login.login();		
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
