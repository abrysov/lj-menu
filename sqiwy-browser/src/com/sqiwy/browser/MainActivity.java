package com.sqiwy.browser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.*;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.sqiwi.browser.R;

import java.util.ArrayList;

/**
 * Created by abrysov
 */

public class MainActivity extends Activity {

    public static final String DEFAULT_HOME_PAGE = "file:///android_asset/default.html";
    private GestureLibrary mGestureLib = null;
    private GestureOverlayView mGestureOverlay = null;
    private WebView mWebView;
    private SharedPreferences mPrefs;
    
    @SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        mWebView = (WebView)findViewById(R.id.webview);
        
        WebSettings webSettings = mWebView.getSettings();
        
        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT < 18) {
        	webSettings.setRenderPriority(RenderPriority.HIGH);
        }
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        mWebView.setWebViewClient(new WebViewClient());
        
        mGesturesLoader.start();
    }
    
    private Thread mGesturesLoader = new Thread() {
        @Override
        public void run() {
        	mGestureLib = GestureLibraries.fromRawResource(MainActivity.this, R.raw.gestures);
            if (!mGestureLib.load()) {
                throw new RuntimeException("unable to load gestures");
            }
            mGestureOverlay = (GestureOverlayView) findViewById(R.id.gestures);
            mGestureOverlay.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
                @Override
                public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
                    ArrayList<Prediction> predictions = mGestureLib.recognize(gesture);
                    // We want at least one prediction
                    if (predictions.size() > 0) {
                        Prediction prediction = (Prediction) predictions.get(0);
                        // We want at least some confidence in the result
                        if (prediction.score > 1.8) {
                            if (prediction.name.equals("app")) {
                            	Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                }
            });
        }
    };
    
    private String getHomePageUrl() {
        String url = mPrefs.getString("pref_home_page", DEFAULT_HOME_PAGE);
    	return url;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
        	mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public void onResume() {
    	SystemControllerHelper.setSystemUiMode(getApplicationContext(),
    			SystemControllerHelper.SYSTEM_UI_MODE_DISABLE_ALL);
    	if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
        	mWebView.loadUrl(getIntent().getData().toString());
        } else {
        	mWebView.loadUrl(getHomePageUrl());
        }
        super.onResume();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
