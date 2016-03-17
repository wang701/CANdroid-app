package com.example.yang.candroid;

import org.json.JSONException;
import org.json.JSONObject;

public class OADARegistration {
	public String mClientId;
	public String mRedirectUri;

	public OADARegistration(JSONObject reg) throws JSONException {
		mClientId = reg.getString("client_id");
		mRedirectUri = reg.getJSONObject("redirect_uris")
			.names().get(0).toString();
	}
}
