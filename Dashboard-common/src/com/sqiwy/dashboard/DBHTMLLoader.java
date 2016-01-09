package com.sqiwy.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.fosslabs.android.utils.Funs;

/**
 * Created by abrysov
 */

public class DBHTMLLoader extends Activity {
    private static final String EXTRA_URL = "com.sqiwy.dashboard.Url";

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, DBHTMLLoader.class);
        intent.putExtra(EXTRA_URL, url);
        context.startActivity(intent);
    }

	@SuppressLint("SetJavaScriptEnabled")
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		Funs.hiddeTitleBar(this, false);
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.move_down_enter, R.anim.move_down_exit);
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getIntent().getStringExtra(EXTRA_URL));
    }
}
