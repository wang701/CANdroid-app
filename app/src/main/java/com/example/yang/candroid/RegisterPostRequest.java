package com.example.yang.candroid;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterPostRequest extends JsonObjectRequest {

	private static final String TAG = "RegisterPostRequest";
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
	private Response.Listener<OADARegistration> mListener;

	public RegisterPostRequest(OADAConfiguration wellKnown,
		Response.Listener<OADARegistration> listener) {
		super(Method.POST, wellKnown.mRegistrationEndpoint,
			makeJson().toString(),
			null, new ErrorListener());
			mListener = listener;
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(
		NetworkResponse response) {

		Response<JSONObject> JSONResponse =
			super.parseNetworkResponse(response);

		if (JSONResponse.isSuccess() && mListener != null) {
			try {
				mListener.onResponse(
					new OADARegistration(JSONResponse.result));
			} catch (JSONException e) {
				JSONResponse.error(new VolleyError(e));
			}
		}

		return JSONResponse;
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
}
