package com.example.yang.candroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OADAConfiguration {

	public String mAuthorizationEndpoint;
	public String mRegistrationEndpoint;
	public String mWellKnownVersion;
	public String mOadaBaseUri;
	public String mTokenEndpoint;
	public String mTokenEndpointAuthSigningAlgValuesSupported;
	ScopesSupported mScopesSupported;

//	private static final String TAG = "OADAConfiguration";

	public OADAConfiguration(JSONObject config) throws JSONException {

		mAuthorizationEndpoint = config.getString("authorization_endpoint");
		mRegistrationEndpoint = config.getString("registration_endpoint");
		mWellKnownVersion = config.getString("well_known_version");
		// mOadaBaseUri = config.getString("oada_base_uri");
		mOadaBaseUri = "https://vip4.ecn.purdue.edu:3000/";
		mTokenEndpoint = config.getString("token_endpoint");
		mTokenEndpointAuthSigningAlgValuesSupported =
			config.getString("token_endpoint_auth_signing_alg_values_supported");
		JSONArray scopesArray = config.getJSONArray("scopes_supported");
		mScopesSupported = new ScopesSupported(scopesArray);
	}

	public class ScopesSupported {

		public String mName;
		public boolean mReadWrite;

		public ScopesSupported(JSONArray scopesArray) throws JSONException {
			mName = scopesArray.getJSONObject(0).getString("name");
			mReadWrite = scopesArray.getJSONObject(0).getBoolean("read+write");
		}
	}
}
