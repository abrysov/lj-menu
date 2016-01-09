package com.sqiwy.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sqiwy.menu.util.SystemControllerHelper;

/**
 * Created by abrysov
 */

/**
 * Disables system ui elements in <code>onResume</code>
 */
public abstract class BaseActivity extends Activity {

	private boolean mIsFinishing = false;
	private boolean mIsLaunchingActivity = false;
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SystemControllerHelper.setSystemUiMode(SystemControllerHelper.SYSTEM_UI_MODE_DISABLE_ALL);
		
		// 
		mIsFinishing = false;
		mIsLaunchingActivity = false;
	}
	
	@Override
	public void finish() {

		super.finish();
		
		mIsFinishing = true;
	}

	@Override
	public void startActivity(Intent intent, Bundle options) {

		super.startActivity(intent, options);
		
		mIsLaunchingActivity = true;
	}
	
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {

		super.startActivityForResult(intent, requestCode);
		
		mIsLaunchingActivity = true;
	}
	
	/**
	 * Check if activity is finishing or launching another activity and depending on that report if
	 * UI interaction is enabled or disabled. Very useful in click listeners to check if we're launching 
	 * activity twice.
	 * @return True if activity is not finishing or launching another activity, false otherwise
	 */
	protected boolean isUiInteractionAllowed() {
		
		return ((false == mIsFinishing) && (false == mIsLaunchingActivity));
	}
}
