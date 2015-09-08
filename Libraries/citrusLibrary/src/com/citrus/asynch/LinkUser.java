package com.citrus.asynch;

import android.app.Activity;
import android.os.AsyncTask;

import com.citrus.citrususer.SignupUser;
import com.citrus.mobile.Callback;

import org.json.JSONObject;

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.CitrusClient#isCitrusMember(String, String, com.citrus.sdk.Callback)} instead.
 */
@Deprecated
public class LinkUser extends AsyncTask<String, Void, JSONObject>{
	private Activity activity;
	
	private Callback callback;
	
	private JSONObject signup_result;
	
	public LinkUser(Activity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}
	
	@Override
	protected JSONObject doInBackground(String... params) {
		SignupUser user = new SignupUser(activity);
		this.signup_result = user.register(params[0], params[1]);
		return signup_result;
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
