package com.sqiwy.dashboard.model.action;

/**
 * Created by abrysov
 */

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;

import com.sqiwy.dashboard.R;
import com.sqiwy.menu.chat.ActivityChat;

@SuppressWarnings("serial")
class ActionOpenChat extends ActionBase {

	@Override
	public void execute(ActionContext actionContext) {
		super.execute(actionContext);

		Context context = actionContext.getContext();
		Intent intent = new Intent(context, ActivityChat.class);
		ActivityOptions options = ActivityOptions.makeCustomAnimation(context, R.anim.move_down_enter, R.anim.move_down_exit);
		
		if (context instanceof Activity) {
        	((Activity) context).startActivityForResult(intent, REQUEST_CODE, options.toBundle());
		} 
		else {
			context.startActivity(intent, options.toBundle());
		}
		
        onActionDone();
	}

	@Override
	public String getType() {
		return Action.TYPE_OPEN_CHAT;
	}

	@Override
	public void init(JSONObject data) throws JSONException {
		super.init(data);
	}

}
