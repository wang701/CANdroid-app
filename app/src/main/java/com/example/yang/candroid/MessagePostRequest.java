package com.example.yang.candroid;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MessagePostRequest extends JsonRequest<Void> {

    public OADAAccessToken mToken;

    public MessagePostRequest(OADAConfiguration config, OADAAccessToken token,
                              String msg) {
        super(Request.Method.POST, config.mOadaBaseUri, makeJson(msg)
                        .toString(), new Listener(), new ErrorListener());
        mToken = token;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", mToken.mTokenType + " " + mToken
				.mAccessToken);
        return headers;
    }

    @Override
    protected Response<Void> parseNetworkResponse(NetworkResponse response) {
        // Don't Care about response
        return null;
    }

    private static JSONObject makeJson(String msg) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", msg);
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
