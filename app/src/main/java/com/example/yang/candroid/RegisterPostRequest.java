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

	private static final String softwareVersion = BuildConfig.VERSION_NAME;
	private static final String softwareStatement =
		"eyJqa3UiOiJodHRwczovL2lkZW50aXR5Lm9hZGEtZGV2LmNvbS9jZXJ0cyIsImtpZCI6" +
		"ImtqY1NjamMzMmR3SlhYTEpEczNyMTI0c2ExIiwidHlwIjoiSldUIiwiYWxnIjoiUlMy" +
		"NTYifQ.eyJyZWRpcmVjdF91cmlzIjpbImh0dHBzOi8vZ2l0aHViLmNvbS9PQVRTLUdyb" +
		"3VwL0NBTmRyb2lkIl0sInRva2VuX2VuZHBvaW50X2F1dGhfbWV0aG9kIjoidXJuOmlld" +
		"GY6cGFyYW1zOm9hdXRoOmNsaWVudC1hc3NlcnRpb24tdHlwZTpqd3QtYmVhcmVyIiwiZ" +
		"3JhbnRfdHlwZXMiOlsiaW1wbGljaXQiXSwicmVzcG9uc2VfdHlwZXMiOlsidG9rZW4iL" +
		"CJpZF90b2tlbiIsImlkX3Rva2VuIHRva2VuIl0sImNsaWVudF9uYW1lIjoiQ0FOZHJva" +
		"WQgTG9nZ2VyIiwiY2xpZW50X3VyaSI6Imh0dHBzOi8vZ2l0aHViLmNvbS9PQVRTLUdyb" +
		"3VwL0NBTmRyb2lkLWxvZ2dlciIsImNvbnRhY3RzIjpbIkNBTkRyb2lkIEluZm8gPGluZ" +
		"m9AaXNvYmx1ZS5vcmc-Il0sInNvZnR3YXJlX2lkIjoiOWJjZTQ4OWUtNGM4NS00MjE4L" +
		"WI4ZTYtNWI2ZGM4NzFlZmE2IiwicmVnaXN0cmF0aW9uX3Byb2l2ZGVyIjoiaHR0cHM6L" +
		"y9pZGVudGl0eS5vYWRhLWRldi5jb20iLCJpYXQiOjE0NTgxNTI0Nzl9.yNOUz8N5DU4c" +
		"6Mjm6vLjvUuE21isneFFCPmROhNr2HaowHRpFx0_xwA34iLTsV_8fx9-AZEdFtMRAs1y" +
		"ZNjVmiIL4LoUbxkitq_s3_1vHB9NNhR77FKixsUD4KZqiFp0K1T5KlAsUksZk3cBbuxy" +
		"IJww0-dea9aOXDPbEQyfX3Q";
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
