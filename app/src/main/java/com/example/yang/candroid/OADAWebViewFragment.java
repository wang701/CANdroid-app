package com.example.yang.candroid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.RandomStringUtils;

public class OADAWebViewFragment extends WebViewFragment {

    public WebView mWebView;
    public RequestQueue mQueue;
    public ConfigGetRequest mConfigReq;
    public RegisterPostRequest mRegisterReq;
    public OADARegistration mReg;
    public OADAConfiguration mConfig;
    public OADAAccessToken mOADAToken;
    public OADAError mOADAError;
    public String mRandState;
    public boolean mIsWebViewAvailable;

    private Listener<OADAAccessToken> mSuccessListener;
    private Listener<OADAError> mErrorListener;

    private static final String TAG = "OADAWebViewFragment";

    public OADAWebViewFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "in onAttach()");
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "in onCreateView()");
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.oada_fragment, container,
                false);

        mWebView = getWebView();

        if (mWebView != null) {
            mWebView.destroy();
        }

        mWebView = (WebView) view.findViewById(R.id.grantPage);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setVisibility(View.INVISIBLE);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if ((url != null) && (url.contains(mReg.mRedirectUris[0]))) {
                    Log.i(TAG, "in wvClient");
                    mWebView.stopLoading();
                    mWebView.setVisibility(View.INVISIBLE);
                    mOADAToken = new OADAAccessToken(url);
                    if (mOADAToken.isStateValid(mRandState)) {
                        Log.i(TAG, "token is valid");
                        Log.i(TAG, "token: " + mOADAToken.mAccessToken);

                        if (mSuccessListener != null) {
                            mSuccessListener.onResponse(mOADAToken);
                        }
                    } else {
                        Log.e(TAG, "state mismatch, token is invalid");

                        if (mErrorListener != null) {
                            mErrorListener.onResponse(mOADAError);
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        mIsWebViewAvailable = true;

        return view;

    }

    public void getAccessToken(String domain, Context context,
                               Listener<OADAAccessToken> successListener,
                               Listener<OADAError> errorListener) {
        mSuccessListener = successListener;
        mErrorListener = errorListener;

        mQueue = Volley.newRequestQueue(context);
        mConfigReq = new ConfigGetRequest(domain,
                new Response.Listener<OADAConfiguration>() {

                    @Override
                    public void onResponse(OADAConfiguration wellKnown) {
                        mConfig = wellKnown;
                        mRegisterReq = new RegisterPostRequest(mConfig,
                                new Response.Listener<OADARegistration>() {

                                    @Override
                                    public void onResponse(OADARegistration
                                                                   reg) {
                                        mReg = reg;
                                        Log.i(TAG, mConfig
                                                .mAuthorizationEndpoint);
                                        Log.i(TAG, mConfig
                                                .mRegistrationEndpoint);
                                        Log.i(TAG, mReg.mRedirectUris[0]);
                                        mWebView.setVisibility(View.VISIBLE);
                                        startAuthorize(mConfig, mReg);
                                    }
                                });
                        mQueue.add(mRegisterReq);
                    }
                });
        mQueue.add(mConfigReq);
    }

    interface Listener<T> {

        void onResponse(T response);
    }

    private String getRandomState() {

        String randState = new RandomStringUtils().random(10);
        mRandState = randState;
        return mRandState;
    }

    private void startAuthorize(OADAConfiguration config, OADARegistration
            reg) {

        Uri authUri = Uri.parse(config.mAuthorizationEndpoint)
                .buildUpon()
                .appendQueryParameter("response_type", "token")
                .appendQueryParameter("client_id", reg.mClientId)
                .appendQueryParameter("state", getRandomState())
                .appendQueryParameter("redirect_uri", reg.mRedirectUris[0])
                .appendQueryParameter("scope", "all")
                .build();

        final String AuthUrl = authUri.toString();

        (new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return null;
            }

            @Override
            protected void onPostExecute(String url) {
                Log.i(TAG, "in onPostExecute()");
                url = AuthUrl;
                Log.i(TAG, url);
                mWebView.loadUrl(url);

            }
        }).execute();
    }

/*    private WebViewClient wvClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if ((url != null) && (url.contains(mReg.mRedirectUris[0]))) {
                mWebView.stopLoading();
                mOADAToken = new OADAAccessToken(url);
                if (mOADAToken.isStateValid(mRandState)) {
                    Log.i(TAG, "token is valid");
                    Log.i(TAG, "token: " + mOADAToken.mAccessToken);

                    if(mSuccessListener != null) {
                        mSuccessListener.onResponse(mOADAToken);
                    }
                } else {
                    Log.e(TAG, "state mismatch, token is invalid");

                    if(mErrorListener != null) {
                        mErrorListener.onResponse(mOADAError);
                    }
                }
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }
    };

		@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if ((url != null) && (url.contains(mReg.mRedirectUris[0]))) {
                Log.i(TAG, "in wvClient");
                mWebView.stopLoading();
                mWebView.setVisibility(View.INVISIBLE);
                mOADAToken = new OADAAccessToken(url);
                if (mOADAToken.isStateValid(mRandState)) {
                    Log.i(TAG, "token is valid");
                    Log.i(TAG, "token: " + mOADAToken.mAccessToken);

                    if(mSuccessListener != null) {
                        mSuccessListener.onResponse(mOADAToken);
                    }
                } else {
                    Log.e(TAG, "state mismatch, token is invalid");

                    if(mErrorListener != null) {
                        mErrorListener.onResponse(mOADAError);
                    }
                }
                return true;
            }
            return false;
        }
    };
*/
}
