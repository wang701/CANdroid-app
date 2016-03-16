package com.example.yang.candroid;

import org.json.JSONException;
import org.json.JSONObject;

public class OADAConfiguration {
	public String mAuthorizationEndpoint;
	public String mRegistrationEndpoint;

	public OADAConfiguration(JSONObject config) throws JSONException {
		mAuthorizationEndpoint = config.getString("authorization_endpoint");
		mRegistrationEndpoint = config.getString("registration_endpoint");
	}
}
