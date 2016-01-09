package com.fosslabs.android.context;

import com.fosslabs.android.jsbase.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class TestClockPreferenceActivity extends Activity {
	private static final String TAG = "TestClockPreferenceActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		// Hide the status bar and other OS-level chrome
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_clock_preference);
		
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragment_test_clock_preference);
		if (fragment == null) {
			fragment = new TestClockPreferenceFragment();
			fm.beginTransaction().add(R.id.fragment_test_clock_preference, fragment)
					.commit();
		}
	}
}
