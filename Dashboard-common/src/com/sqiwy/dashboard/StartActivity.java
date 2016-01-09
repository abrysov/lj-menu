package com.sqiwy.dashboard;

import android.app.Activity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.sqiwy.dashboard.util.FlowUtils;
import com.sqiwy.menu.util.SystemControllerHelper;

/**
 * Created by abrysov
 */

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Crashlytics.start(this);

		// Disable controls
		SystemControllerHelper.setSystemUiMode(SystemControllerHelper.SYSTEM_UI_MODE_DISABLE_ALL);

		FlowUtils.continueSetupFlow(this);

		finish();
	}
	
}
