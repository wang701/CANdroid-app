package com.example.yang.candroid;

import android.net.Uri;
import android.util.Log;

import java.io.Serializable;

public class OADAAccessToken {

	public String mAccessToken;
	public String mState;
	public String mTokenType;
	public long mExpireIn; // seconds
	public long mExpireTimestamp; // seconds

//	private static final String TAG = "OADAAccessToken";

	public OADAAccessToken(String url) {
		
		Uri uri = Uri.parse(url);
		String[] fragment = uri.getFragment().split("&");
		mAccessToken = fragment[0].split("=")[1];
		mExpireIn = Long.parseLong(fragment[1].split("=")[1]);
		mTokenType = fragment[2].split("=")[1];
		mState = fragment[3].split("=")[1];
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
