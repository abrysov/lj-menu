package com.fosslabs.android.context;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.fosslabs.android.jsbase.R;

public class TestClockGalleryActivity extends Activity {
	private static final String TAG = "TestClockGalleryActivity";
	private boolean mReveseLandscape = false;

	private class ViewHolder {
	}

	private ViewHolder vh = new ViewHolder();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Let's display the progress in the activity title bar, like the
		// browser app does.

		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		// Hide the status bar and other OS-level chrome
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation();
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_test_clock_gallery);
		
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragment_test_clock_show);
		if (fragment == null) {
			fragment = new TestClockShowFragment();
			fm.beginTransaction().add(R.id.fragment_test_clock_show, fragment)
					.commit();
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen for landscape and portrait and
		// set portrait mode always
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation();
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			setRequestedOrientation();
		}
	}

	private void setRequestedOrientation() {
		setRequestedOrientation(mReveseLandscape ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
				: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
}
