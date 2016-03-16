package com.example.yang.candroid;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RegisterPostRequest extends JsonRequest<Void> {

	private static final String softwareVersion = "1.0-ga";
	private static final String scopes = "oada.all.1";
	private static final String softwareStatement = "something";
	private static final String TAG = "RegisterPostRequest";
	protected String mClientId;
	protected String mRedirectUri;

	public RegisterPostRequest(String url) {
		super(Method.POST, url, makeJson().toString(),
			new Listener(), new ErrorListener());
	}

	@Override
	protected Response<Void> parseNetworkResponse(
		NetworkResponse response) {
		Log.i(TAG, response.toString());
        try {
            String jsonString = new String(response.data,
				HttpHeaderParser.parseCharset(response.headers,
						PROTOCOL_CHARSET));
			Log.i(TAG, jsonString);
			JSONObject jsonObject = new JSONObject(jsonString);
			mClientId = jsonObject.getString("client_id");
			JSONObject uris = jsonObject.getJSONObject("redirect_uris");
			mRedirectUri = uris.names().get(0).toString();
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
        } catch (JSONException je) {
			je.printStackTrace();
        }
		return null;
	}

	private static JSONObject makeJson() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("software_version", softwareVersion);
			jsonObject.put("scopes", scopes);
			jsonObject.put("software_statement", softwareStatement);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	private static class ErrorListener implements Response.ErrorListener {
		@Override
		public void onErrorResponse(VolleyError error) {
			VolleyLog.d("Error: " + error.getMessage());
		}
	}

	private static class Listener implements Response.Listener<Void> {
		@Override
		public void onResponse(Void v) {

		}
	}
}
