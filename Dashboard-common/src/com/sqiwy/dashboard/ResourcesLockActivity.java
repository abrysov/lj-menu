package com.sqiwy.dashboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.sqiwy.restaurant.api.BackendException;
import com.sqiwy.dashboard.util.ExecutorUtils;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.util.PreferencesUtils;


/**
 * Created by abrysov
 */

public class ResourcesLockActivity extends BaseActivity {

	private GestureLibrary mGestureLib = null;
	private GestureOverlayView mGestureOverlay = null;
	private HandlerThread mConnTestThread;
	private Handler mConnTestHandler, mUIHandler;

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static Intent getLaunchIntent(Context context) {

		Intent intent = new Intent(context, ResourcesLockActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		return intent;
	}

	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_resources_lock);

		// set the background
		findViewById(R.id.root).setBackgroundDrawable(new BitmapDrawable(((MenuApplication) getApplication()).getLockBackground()));
		
		// load gestures
		mGestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
		
		if (!mGestureLib.load()) {
			throw new RuntimeException("unable to laod geustres");
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
					if (prediction.score > 0.75) {

						if (prediction.name.equals("app")) {

							onConnectionToServiceRestored(false);
						}
					}
				}
			}
		});
	}

	/**
	 * 
	 */
	@Override
	protected void onResume() {
		Looper looper;

		super.onResume();
		mConnTestThread=new HandlerThread("NetConnCheck"); mConnTestThread.start();
		looper=mConnTestThread.getLooper();
		mConnTestHandler=new Handler(looper); mUIHandler=new Handler();
		mConnTestHandler.postDelayed(mConnTestRunnable,1000);
		/*
		mExecutor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
		mPingCallback.onFailure(new Error());
		*/
	}
	
	private Runnable mConnTestRunnable=new Runnable() {
		public void run() {
			boolean success=false;
			
			try {
				if (!PreferencesUtils.isApplicationInDemoMode(MenuApplication.getContext())) {
					MenuApplication.getOperationService().ping();
				}
				success=true;
			} catch (Exception e) { }
			if (!success) {
				mConnTestHandler.postDelayed(mConnTestRunnable,1000);
			} else {
				mUIHandler.post(new Runnable() {
					public void run() {
						onConnectionToServiceRestored(true);
					}
				});
			}
		}
	};
	
	/**
	 * 
	 */
	@Override
	protected void onPause() {
		mConnTestHandler.removeCallbacks(mConnTestRunnable); mConnTestThread.quit();
		super.onPause();
		/*
		mExecutor.shutdownNow();
		*/
	}

	/**
	 * 
	 */
	@Override
	public void onBackPressed() {

	}

	/**
	 * 
	 */
	private void onConnectionToServiceRestored(boolean restored) {
		setResult(restored ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
		finish();
	}
}
