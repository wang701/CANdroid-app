package com.example.yang.candroid;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MessageRequest extends JsonRequest<NetworkResponse> {

    public OADAAccessToken mToken;

    public MessageRequest(int method, OADAConfiguration config,
                          OADAAccessToken token, String msg) {
        super(method, config.mOadaBaseUri + "bookmarks/", makeJson(msg)
                .toString(), new Listener(), new ErrorListener());
        mToken = token;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse
                                                                     response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders
                (response));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", mToken.mTokenType + " " + mToken
                .mAccessToken);
        return headers;
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

    private static class Listener implements Response
            .Listener<NetworkResponse> {
        @Override
        public void onResponse(NetworkResponse response) {

        }
    }
}
