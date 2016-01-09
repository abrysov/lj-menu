package com.fosslabs.android.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import com.fosslabs.android.utils.ProjectConstants;

import android.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class Funs {
	private static final String TAG = "Funs";
	public static void hiddeTitleBar(Activity context, boolean reveseLandscape){
		// Hide the window title.
		context.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Let's display the progress in the activity title bar, like the
		// browser app does.				
		context.getWindow().requestFeature(Window.FEATURE_PROGRESS);
		// Hide the status bar and other OS-level chrome
		context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(context,reveseLandscape);
	}
	
	public static void setRequestedOrientation(Activity context, boolean reveseLandscape) {
		context.setRequestedOrientation(reveseLandscape ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
				: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	
	
	public static DisplayMetrics getDisplayMetrics(Activity context) {
		DisplayMetrics metrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		//JSLog.d(TAG, "getResources().getDisplayMetrics() "
		//		+ metrics.widthPixels + " " + metrics.heightPixels);

		DisplayMetrics metrics2 = context.getResources().getDisplayMetrics();
		//JSLog.d(TAG, "getResources().getDisplayMetrics() "
		//		+ metrics2.widthPixels + " " + metrics2.heightPixels);

		return metrics2;
	}

	public static int getWidthRatioMetrics(Activity context, View v, float ratio) {
		int width;

		if (v == null) {
			DisplayMetrics metrics = getDisplayMetrics(context);
			width = metrics.widthPixels;
		} else {
			width = v.getWidth();

		}
		return (int) (width * ratio);

	}

	public static int getHeightRatioMetrics(Activity context, View v,
			float ratio) {
		int height;

		if (v == null) {
			DisplayMetrics metrics = getDisplayMetrics(context);
			height = metrics.heightPixels;
		} else {
			height = v.getHeight();
		}
		return (int) (height * ratio);

	}

	public static int dpToPx(Context context, int dp) {
		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		int px = Math.round(dp
				* (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}

	public static int pxToDp(Context context, int px) {
		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		int dp = Math.round(px
				/ (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return dp;
	}

	private static void getToastWithDuration(Context context, String s, int toastDuration) {
		Toast t=new Toast(context);
		LayoutInflater li=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View toastView=li.inflate(com.fosslabs.android.jsbase.R.layout.toast, null);
		TextView toasText=(TextView)toastView.findViewById(com.fosslabs.android.jsbase.R.id.errorMessageView);
		
		t.setDuration(toastDuration);
		t.setGravity(Gravity.CENTER, 0, 0);
		toasText.setText(s);
		t.setView(toastView);
		t.show();
	}
	

	public static void getToast(Context context, String s) {
		getToastWithDuration(context,s,Toast.LENGTH_SHORT);
	}

	public static void getLongToast(Context context, String s) {
		getToastWithDuration(context,s,Toast.LENGTH_LONG);
	}

	public static String customLenght (int count, String value, String uppend_value, boolean before){
		String res = value;
		for(int i = 0; i < count - value.length(); i++){
			if(before) res = uppend_value + res;
			else res += uppend_value;
		}
		return res;
	}
	
	
	
}
