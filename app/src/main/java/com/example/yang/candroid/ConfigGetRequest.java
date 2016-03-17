package com.example.yang.candroid;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfigGetRequest extends JsonObjectRequest {

	private static final String TAG = "ConfigGetRequest";
	private Response.Listener<OADAConfiguration> mListener;

	public ConfigGetRequest(String domain,
		Response.Listener<OADAConfiguration> listener) {
		super(Method.GET, "https://"
			+ domain
			+ ":3000/.well-known/oada-configuration", null,
			new ErrorListener());
		mListener = listener;
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(
		NetworkResponse response) {

		Response<JSONObject> JSONResponse =
			super.parseNetworkResponse(response);

		return JSONResponse;
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		try {
			mListener.onResponse(
					new OADAConfiguration(response));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static class ErrorListener implements Response.ErrorListener {

		@Override
		public void onErrorResponse(VolleyError error) {
			VolleyLog.d("Error: " + error.getMessage());
		}
	}
}
