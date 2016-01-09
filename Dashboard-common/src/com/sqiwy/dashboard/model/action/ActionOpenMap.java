package com.sqiwy.dashboard.model.action;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;

import com.sqiwy.dashboard.DBMapActivity;
import com.sqiwy.dashboard.R;

/**
 * Created by abrysov
 */

@SuppressWarnings("serial")
class ActionOpenMap extends ActionBase {

	@Override
	public void execute(ActionContext actionContext) {
		super.execute(actionContext);

		Context context = actionContext.getContext();
		Intent intent = new Intent(context, DBMapActivity.class);
		ActivityOptions options = ActivityOptions.makeCustomAnimation(context, R.anim.move_down_enter, R.anim.move_down_exit);
		
		if (context instanceof Activity) {
        	((Activity) context).startActivityForResult(intent, REQUEST_CODE, options.toBundle());
		} 
		else {
			context.startActivity(intent, options.toBundle());
		}
		
        //DBHTMLLoader.start(actionContext.getContext(), "http://europe-tc.ru/shem.php?floor=1");
		
        // notify action is done
        onActionDone();
	}

	@Override
	public String getType() {
		return Action.TYPE_OPEN_MAP;
	}

	@Override
	public void init(JSONObject data) throws JSONException {
		super.init(data);
		
		// TODO: define interface
		// position, floor?
	}

}
