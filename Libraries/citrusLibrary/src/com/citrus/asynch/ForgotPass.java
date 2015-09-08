package com.citrus.asynch;

import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;

import com.citrus.citrususer.ResetPassword;
import com.citrus.mobile.Callback;

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.CitrusClient#resetPassword(String, com.citrus.sdk.Callback)} instead.
 */
@Deprecated
public class ForgotPass extends AsyncTask<Void, Void, JSONObject>{
	Callback callback;	
	String email;
	
	JSONObject headers, params, response;
	
	Activity activity;
	
	public ForgotPass(Activity activity, String email, Callback callback) {
		this.email = email;
		this.callback = callback;
		this.activity = activity;
	}
	
	@Override
	protected JSONObject doInBackground(Void... params) {
		ResetPassword reset = new ResetPassword(activity, email);
		JSONObject results = reset.sendresetEmail();
		return results;
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
