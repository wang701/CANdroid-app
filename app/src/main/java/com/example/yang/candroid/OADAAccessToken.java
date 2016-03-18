package com.example.yang.candroid;

import android.net.Uri;

public class OADAAccessToken {

	protected String mAccessToken;
	protected String mState;
	protected String mTokenType;
	protected long mExpireIn; // seconds
	public long mExpireTimestamp; // seconds

//	private static final String TAG = "OADAAccessToken";

	public OADAAccessToken(String url) {
		
		Uri uri = Uri.parse(url);
		String[] fragment = uri.getFragment().split("&");
		mState = fragment[1].split("=")[1];
		mAccessToken = fragment[0].split("=")[1];
		mTokenType = fragment[2].split("=")[1];
		mExpireIn = Long.parseLong(fragment[3].split("=")[1]);
		long currentTimestamp = System.currentTimeMillis() / 1000L;
		mExpireTimestamp = currentTimestamp + mExpireIn;
	}

	/*
	*  check if response state matches state in
 	*  in the auth GET request
 	*/
	public boolean isStateValid(String origState) {
		
		if (origState.equals(mState)) {
			return true;
		} else {
			return false;
		}
	}
}
