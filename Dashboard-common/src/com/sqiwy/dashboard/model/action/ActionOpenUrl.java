package com.sqiwy.dashboard.model.action;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.fosslabs.android.utils.JSLog;
import com.sqiwy.dashboard.R;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.util.CommonUtils;

/**
 * Created by abrysov
 */

@SuppressWarnings("serial")
class ActionOpenUrl extends ActionBase {
	
	private final static String TAG = "ActionOpenUrl";

	private String mUrl;
	
	@Override
	public void execute(ActionContext actionContext) {
		super.execute(actionContext);
		
		JSLog.d(TAG, "execute = " + mUrl);
		
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
        Context context = actionContext.getContext();
		
        ActivityOptions options = ActivityOptions
				.makeCustomAnimation(context, R.anim.move_down_enter, R.anim.move_down_exit);
		context.startActivity(intent, options.toBundle());

        MenuApplication.trackLaunchedApp(CommonUtils.getDefaultBrowserPackage());
        
        // notify action is done
        onActionDone();
	}

	@Override
	public String getType() {
		return Action.TYPE_OPEN_URL;
	}

	@Override
	public void init(JSONObject data) throws JSONException {
		super.init(data);
		
		mUrl = data.getString("url");
		
	}

}
