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

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.isoblue.can.CanSocketJ1939.J1939Message;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class MessageRequest extends JsonRequest<NetworkResponse> {

    public OADAAccessToken mToken;
    public OADAConfiguration mConfig;
	public String mMsg;
	public String mBaseUri;

    private static final String TAG = "MessageRequest";

    public MessageRequest(int method, OADAConfiguration config, OADAAccessToken token,
							String msg, String resUrl, Response.ErrorListener errorListener,
							Response.Listener listener) {

        super(method, config.mOadaBaseUri + resUrl, makeJson(msg).toString(), listener,
				errorListener);
        mToken = token;
        mConfig = config;
		mMsg = msg;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {

        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", mToken.mTokenType + " " + mToken
                .mAccessToken);
        return headers;
    }

    public static JSONObject makeJson(String msg) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", msg.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

	public String buildResUrl(J1939Message msg) {

        int pgn = msg.pgn;
        int timeHash7 = (int) (msg.timestamp - (msg.timestamp % 10000000L));
        int timeHash3 = (int) (msg.timestamp - msg.timestamp % 1000L);

        String resUrl = "bookmarks/" + "candroid/" +
                "pgn/" + Integer.toString(pgn) +
                "/timehash-7/" + Integer.toString(timeHash7) +
                "/timehash-3/" + Integer.toString(timeHash3) + "/";

        return resUrl;
    }

    private static class ErrorListener implements Response.ErrorListener {

		@Override
		public void onErrorResponse(VolleyError error) {

			String json = null;
			String trimmedString = null;

			/* error when network is down*/
			if (error.getCause() != null && error.getCause() instanceof
					UnknownHostException) {
				Log.d(TAG, "no internet connection");
/*					if (mMsgBuffer == null) {
					mMsgBuffer = new MsgAdapter(1000 * 60 * 60 * 24);
				}
				if (mCan0Msg != null) {
					mMsgBuffer.add(mCan0Msg.toString());
				}
*/
			}

			/* this part should parse out error codes from server */
			NetworkResponse response = error.networkResponse;
			if (response != null && response.data != null) {
				json = new String(response.data);
				try {
					JSONObject obj = new JSONObject(json);
					trimmedString = obj.getString("message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			if (json != null) {
				VolleyLog.d("Error: " + trimmedString);
			}
		}
	}

    private static class Listener implements Response.Listener<NetworkResponse> {

        @Override
        public void onResponse(NetworkResponse response) {
            VolleyLog.d("onResponse: " + response.toString());

        }
    }
}
