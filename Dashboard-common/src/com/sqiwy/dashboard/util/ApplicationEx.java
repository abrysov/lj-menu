package com.sqiwy.dashboard.util;

import android.app.Application;
import android.preference.PreferenceManager;

import com.fosslabs.android.utils.JSLog;
import com.sqiwy.dashboard.R;

/**
 * Created by abrysov
 */

public class ApplicationEx extends Application {
	private static final String TAG = "ApplicationEx";


	@Override
	public void onCreate() {
		super.onCreate();

		try {
			PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		} catch (Exception e) {
			JSLog.d(TAG, e.getMessage());
			e.printStackTrace();
		}
	}

}
