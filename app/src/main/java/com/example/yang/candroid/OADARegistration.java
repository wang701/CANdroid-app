package com.example.yang.candroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OADARegistration {

    public int mClientIdIssueTimestamp;
    public String mClientId;
    public String mSoftwareVersion;
    public String mScopes;
    public String mTokenEndpointAuthMethod;
    public String mClientName;
    public String mClientUri;
    public String mLogoUri;
    public String mTosUri;
    public String mPolicyUri;
    public String mSoftwareId;
    public String mRegistrationProvider;
    public String[] mRedirectUris;
    public String[] mGrantTypes;
    public String[] mResponseTypes;
    public String[] mContacts;

//	private static final String TAG = "OADARegistration";

    public OADARegistration(JSONObject reg) throws JSONException {

        mClientId = reg.getString("client_id");
        mClientIdIssueTimestamp = reg.getInt("iat");
//		mSoftwareVersion = reg.getString("software_version");
//		mScopes = reg.getString("scopes");
        mTokenEndpointAuthMethod = reg.getString("token_endpoint_auth_method");
        mClientName = reg.getString("client_name");
//		mClientUri = reg.getString("client_uri");
//		mLogoUri = reg.getString("logo_uri");
//		mTosUri = reg.getString("tos_uri");
//		mPolicyUri = reg.getString("policy_uri");
        mSoftwareId = reg.getString("software_id");
        mRegistrationProvider = reg.getString("registration_provider");
        mRedirectUris = JSONArrayToStrArray(reg.getJSONArray("redirect_uris"));
        mContacts = JSONArrayToStrArray(reg.getJSONArray("contacts"));
        mGrantTypes = JSONArrayToStrArray(reg.getJSONArray("grant_types"));
        mResponseTypes = JSONArrayToStrArray(reg.getJSONArray
                ("response_types"));
    }

    private String[] JSONArrayToStrArray(JSONArray jsonArray)
            throws JSONException {

        String[] strArray = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            strArray[i] = jsonArray.get(i).toString();
        }

        return strArray;
    }
}
