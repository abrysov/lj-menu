package com.sqiwy.dashboard;

import java.io.IOException;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import com.fosslabs.android.utils.JSFileWorker;
import com.fosslabs.android.utils.JSLog;
import com.sqiwy.restaurant.api.BackendException;
import com.sqiwy.dashboard.CancelableToast.VerticalAlignment;
import com.sqiwy.dashboard.DBGrandFragment.TopMenuCallbacks;
import com.sqiwy.dashboard.DBGrandFragment.DBGrandFragmentType;
import com.sqiwy.dashboard.logger.LogMessage;
import com.sqiwy.dashboard.logger.LoggerService;
import com.sqiwy.dashboard.model.ProjectConstants;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.util.PreferencesUtils;
import com.sqiwy.menu.util.UIUtils;

/**
 * Created by abrysov
 */

public class DBStartActivity extends BaseActivity implements
		TopMenuCallbacks{
	private static final int REQUEST_CODE_LOCK_SCREEN = 1;
	private static final String TAG = "DBStartActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.move_down_enter, R.anim.move_down_exit);
		setContentView(R.layout.activity_dbstart);
		JSLog.d(TAG, "on Create DBSTARTActivity()");
		
		if (!JSFileWorker.checkDirsOnStart(ProjectConstants.createOnStart)) {
			
//			CancelableToast.showNotificationInCenter(getFragmentManager(), R.string.error_not_dir_for_work);
			
			new CancelableToast.Config()
				.setText(R.string.error_not_dir_for_work)
				.setVerticalAlignment(VerticalAlignment.CENTER)
				.show(getFragmentManager());
			
			/*
			Funs.getToast(getApplicationContext(),
					getResources().getString(R.string.error_not_dir_for_work));
			*/
			return;
		}
		
		Bitmap back = ((MenuApplication) getApplication()).getDashboardBackground();
		ImageView iv = (ImageView) findViewById(R.id.iv_db_back);
		iv.setImageBitmap(back);
		
		/* mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
	        if (!mLibrary.load()) {
	            throw new RuntimeException("unable to laod geustres");
	        }

	       vh.gestures = (GestureOverlayView) findViewById(R.id.gestures);
	       vh.gestures.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {

	            @Override
	            public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
	                ArrayList predictions = mLibrary.recognize(gesture);

	                // We want at least one prediction
	                if (predictions.size() > 0) {
	                    Prediction prediction = (Prediction) predictions.get(0);
	                    // We want at least some confidence in the result
	                    if (prediction.score > 1.0) {
	            
	                        Funs.getToast(DBStartActivity.this,prediction.name);
	                        if (prediction.name.equals("app")) {
	                            //startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
	                        	Intent i = new Intent(DBStartActivity.this, DBPreferenceActivity.class);
	                    		//i.putExtra(ProductListActivity.EXTRA_ID_MENU_CATEGORY, id);
	                    		startActivity(i);
	                        }

	                    }
	                }
	            }
	        });*/
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragment_grand_db);
		if (fragment == null) {
			fragment = DBGrandFragment.newInstance(DBGrandFragmentType.EXTRA_TYPE_MAIN);
			fm.beginTransaction().add(R.id.fragment_grand_db, fragment)
					.commit();
		}

		fragment = fm.findFragmentById(R.id.fragment_content_db);
		if (fragment == null) {
			fragment = new DBContentFragment();
			fm.beginTransaction().add(R.id.fragment_content_db, fragment)
					.commit();
		}
		new OpenTableSessionTask().execute(this);
	}

	@Override
	public void onScreenRotate() {
		UIUtils.toggleOrientation(DBStartActivity.this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (REQUEST_CODE_LOCK_SCREEN == requestCode) {
			/*DBContentFragment fragment = (DBContentFragment) getFragmentManager().findFragmentById(R.id.fragment_content_db);
			if (null != fragment) {
				fragment.skipNextAnimation();
			}*/
		} else {
			overridePendingTransition(R.anim.move_down_enter, R.anim.move_down_exit);
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onDestroy() {
		MenuApplication.resetTableSessionOpenFlag();
		super.onDestroy();
	}

	@Override
	public void onLock() {
		
		if(isUiInteractionAllowed()) {
		
			Intent i = new Intent(DBStartActivity.this, DBLockActivity.class);
			startActivityForResult(i, REQUEST_CODE_LOCK_SCREEN);
		}
	}

	@Override
	public void onBack() {
		//not visible button in grand fragment
	}
	
	public static class OpenTableSessionTask extends AsyncTask<Context,Void,Void> {
		protected Void doInBackground(Context... arg0) {
			Context ctx=arg0[0];
			
			if (!MenuApplication.isTableSessionAlreadyOpen()) {
				try {
					if (!PreferencesUtils.isApplicationInDemoMode(MenuApplication.getContext())) {
						MenuApplication.getOperationService().openTableSession();
					}
					LoggerService.sendMessage(ctx,
						new LogMessage(System.currentTimeMillis(),LogMessage.Stage.OPEN_SESSION,LogMessage.Status.SUCCESS,"Table session opened"));
				} catch (BackendException e) {
					e.printStackTrace();
					MenuApplication.resetTableSessionOpenFlag();
					LoggerService.sendMessage(ctx,
							new LogMessage(System.currentTimeMillis(),LogMessage.Stage.OPEN_SESSION,LogMessage.Status.FAILED,"Table session open failed, BackendException"));
				}
				catch (IOException e) {
					MenuApplication.resetTableSessionOpenFlag();
					LoggerService.sendMessage(ctx,
							new LogMessage(System.currentTimeMillis(),LogMessage.Stage.OPEN_SESSION,LogMessage.Status.FAILED,"Table session open failed, IOException"));
				}
			}
			return null;
		}
	}
}
