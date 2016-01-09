package com.sqiwy.dashboard;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import com.fosslabs.android.utils.JSLog;
import com.sqiwy.dashboard.DBGrandFragment.TopMenuCallbacks;
import com.sqiwy.dashboard.DBGrandFragment.DBGrandFragmentType;
import com.sqiwy.menu.util.UIUtils;

/**
 * Created by abrysov
 */

public class DBCafeActivity extends Activity implements
		TopMenuCallbacks {
	private static final String TAG = "DBCafeActivity";

	LayoutTransition mTransitioner = new LayoutTransition();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// JSTODO
		// SystemUiHider ??????
		UIUtils.hideTitleBar(this);
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.move_down_enter, R.anim.move_down_exit);
		setContentView(R.layout.activity_cafe);

		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragment_grand_db);
		if (fragment == null) {
			fragment = DBGrandFragment
					.newInstance(DBGrandFragmentType.EXTRA_TYPE_MAIN);
			fm.beginTransaction().add(R.id.fragment_grand_db, fragment)
					.commit();
		}

		fragment = fm.findFragmentById(R.id.fragment_cafe);
		if (fragment == null) {
			fragment = new DBCafeFragment();
			fm.beginTransaction().add(R.id.fragment_cafe, fragment).commit();
		}
	}

	@Override
	public void onScreenRotate() {
		UIUtils.toggleOrientation(DBCafeActivity.this);
	}

	@Override
	public void onLock() {
		Intent i = new Intent(DBCafeActivity.this, DBLockActivity.class);
		startActivity(i);

	}

	@Override
	public void onBack() {
		finish();

	}

	protected void onPause() {
		JSLog.d(TAG, "onPause()");
		super.onPause();
		System.gc();
	}
}
