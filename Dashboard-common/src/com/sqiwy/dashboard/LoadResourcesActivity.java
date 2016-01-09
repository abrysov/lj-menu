package com.sqiwy.dashboard;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sqiwy.dashboard.CancelableToast.VerticalAlignment;
import com.sqiwy.dashboard.util.FlowUtils;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.util.PreferencesUtils;

/**
 * Created by abrysov
 */


public class LoadResourcesActivity extends Activity implements ResourcesFragment.Listener {

	public static final String EXTRA_NEXT_ACTIVITY = "extra-next-activity";
	private static final String FRAGMENT_TAG = "tag-resources";
	private static final String EXTRA_FORCE_LOAD_ALL_ITEMS = "force_load_all_items";
	
	/**
	 * 
	 */
	private boolean mIsForceLoadAllItems = true;
	
	/**
	 * 
	 * @param context
	 * @param forceLoadAllResources
	 * @return
	 */
	public static Intent getLaunchIntent(Context context, boolean forceLoadAllItems) {
		
		Intent intent = new Intent(context, LoadResourcesActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(EXTRA_FORCE_LOAD_ALL_ITEMS, forceLoadAllItems);
		return intent;
	}
	
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mIsForceLoadAllItems = getIntent().getBooleanExtra(EXTRA_FORCE_LOAD_ALL_ITEMS, true);
		
		if (null == getFragmentManager().findFragmentByTag(FRAGMENT_TAG)) {
			getFragmentManager().beginTransaction().add(android.R.id.content, ResourcesFragment.newInstance(mIsForceLoadAllItems), FRAGMENT_TAG).commit();
		}
	}

	@Override
	public void onBackPressed() {
		// Do nothing
	}

	@Override
	public void onSettings() {
		
		PreferencesUtils.setBackendConfigCompleted(this, false);

        MenuApplication.mIsCheckResources = false;
        FlowUtils.continueSetupFlow(this);
				
		finish();
	}

	@Override
	public void onContinue(boolean errorState) {
		
		if (mIsForceLoadAllItems) {
			if (!errorState) {
					ResourcesManager.setResourcesLoaded(this, true);
			} else {
				
				PreferencesUtils.setBackendConfigCompleted(this, false);
				ResourcesManager.setResourcesLoaded(this, false);
			}
		}

        MenuApplication.mIsCheckResources = false;
        FlowUtils.continueSetupFlow(this);
		
		finish();
	}
	
	public void onError() {
		PreferencesUtils.setBackendConfigCompleted(LoadResourcesActivity.this, false);
        MenuApplication.mIsCheckResources = false;
		FlowUtils.continueSetupFlow(LoadResourcesActivity.this);

		FragmentManager fm= getFragmentManager();
		new CancelableToast.Config()
			.setText(R.string.error_retrieving_menu)
			.setVerticalAlignment(VerticalAlignment.BOTTOM)
			.setOnCloseListener(new CancelableToast.IOnCloseListener() {
				public void onClose() {
					LoadResourcesActivity.this.finish();
				}
			})
			.show(fm);		
	}
}
