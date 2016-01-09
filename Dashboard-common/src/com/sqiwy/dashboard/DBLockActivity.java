package com.sqiwy.dashboard;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.fosslabs.android.utils.JSLog;
import com.sqiwy.menu.MenuApplication;

/**
 * Created by abrysov
 */

public class DBLockActivity extends Activity implements OnClickListener, DrawerListener {
	
	private final static String TAG = "DBLockActivity";
	
	private View mLeftDrawer;
	private DrawerLayout mDrawerLayout;
	private boolean mClose = false;
	private boolean mDrawerStateChanged = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_dblock);
		
		JSLog.d(TAG, "Lock Activity");
		
		ImageView lockBack = (ImageView) findViewById(R.id.iv);
		
		Bitmap back = ((MenuApplication) getApplication()).getLockBackground();
		lockBack.setImageBitmap(back);
		
		findViewById(R.id.btn_unlock_screen).setOnClickListener(this);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setScrimColor(0x00000000);
        mDrawerLayout.setDrawerListener(this);

        mLeftDrawer = findViewById(R.id.left_drawer);
		
        mDrawerLayout.post(new Runnable() {
        	public void run() {
        		mDrawerLayout.openDrawer(mLeftDrawer);
        	}
        });
        
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		unlock();
	}
	
	@Override
	public void onClick(View v) {
		unlock();
	}
	
	protected void unlock() {
		mDrawerLayout.closeDrawer(mLeftDrawer);
	}
	
	@Override
	public void onDrawerClosed(View arg0) {
		if (!isFinishing()) {
			finish();
		}
	}
	
	/**
	 * This method is used in order to fix the following issue:
	 * Sometimes, if drawer is slided left and quickly slided right, drawer
	 * is hidden but <code>onDrawerClosed</code> doesn't get called and user
	 * can't do nothing until it slides right and then left again. 
	 */
	@Override
	public void onDrawerStateChanged(int newState) {
		if (DrawerLayout.STATE_IDLE == newState && mClose && !isFinishing()) {
			finish();
		}
		mDrawerStateChanged = true;
	}
	
	@Override
	public void onDrawerSlide(View drawerView, float slideOffset) {
		if (mDrawerStateChanged) {
			mClose = 0 == slideOffset;
		}
	}
	@Override
	public void onDrawerOpened(View drawerView) {}
	
}
