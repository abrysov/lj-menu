package com.sqiwy.dashboard.model.action;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sqiwy.dashboard.R;
import com.sqiwy.menu.MenuApplication;

/**
 * Created by abrysov
 */

/**
 * Parsed from: 
 * <pre>
 * 	"action": {
 * 		"type": "open_app",
 * 		"app_package": "com.sqiwy.menu",
 * 		"app_activity": ""
 *	}
 * </pre>
 *
 * <code>app_package</code> - you can specify it on its own and action will try to find launching activity for the app<br/><br/>
 * <code>app_activity</code> - you can specify it on its own and action will use current activity package, otherwise specified package<br/><br/>
 */
@SuppressWarnings("serial")
class ActionOpenApp extends ActionBase {
	
	private String mAppPackage;
	private String mAppActivity;
	
	@Override
	public void execute(ActionContext actionContext) {
		super.execute(actionContext);
		Context context = actionContext.getContext();
		Intent intent = null;
		if (!TextUtils.isEmpty(mAppActivity)) {
			intent = new Intent();
			if (!TextUtils.isEmpty(mAppPackage)) {
				intent.setClassName(mAppPackage, mAppActivity);
			} else {
				intent.setClassName(context, mAppActivity);
			}
		} else if (null != mAppPackage) {
			try {
				intent = context.getPackageManager().getLaunchIntentForPackage(mAppPackage);
			} catch (Exception e) {
				e.printStackTrace();			
			}
		} else {
			throw new IllegalStateException("Not enough info to start app, please check that JSON is correct.");
		}
		if (intent != null) {
			ActivityOptions options = ActivityOptions
					.makeCustomAnimation(context, R.anim.move_down_enter, R.anim.move_down_exit);
			if (options != null) {
				context.startActivity(intent, options.toBundle());
		        MenuApplication.trackLaunchedApp(intent.getComponent().getPackageName());
		        // notify action is done
		        onActionDone();
			}
		}
	}

	@Override
	public String getType() {
		return Action.TYPE_OPEN_APP;
	}

	@Override
	public void init(JSONObject data) throws JSONException {
		super.init(data);
		
		mAppPackage = data.optString("app_package");
		mAppActivity = data.optString("app_activity");
		
		if (TextUtils.isEmpty(mAppPackage) && TextUtils.isEmpty(mAppActivity)) {
			throw new IllegalStateException("App package and activity cannot be empty both: " + data);
		}
	}

}
