package com.sqiwy.dashboard.model.action;

import org.json.JSONException;
import org.json.JSONObject;

import com.fosslabs.android.utils.Funs;
import com.sqiwy.dashboard.CancelableToast;
import com.sqiwy.dashboard.R;
import com.sqiwy.dashboard.CancelableToast.VerticalAlignment;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by abrysov
 */

@SuppressWarnings("serial")
class ActionShowToast extends ActionBase {

	private String mToastText;
	
	@Override
	public void execute(ActionContext actionContext) {
		super.execute(actionContext);
		
		Context context = actionContext.getContext();
		FragmentManager fm=((Activity)context).getFragmentManager();
		
//		CancelableToast.showNotificationInCenter(fm, mToastText);
		// Funs.getLongToast(context, mToastText);
		new CancelableToast.Config()
			.setText(mToastText)
			.setVerticalAlignment(VerticalAlignment.CENTER)
			.show(fm);
		
        // notify action is done
        onActionDone();
	}

	@Override
	public String getType() {
		return Action.TYPE_SHOW_TOAST;
	}

	@Override
	public void init(JSONObject data) throws JSONException {
		super.init(data);
		
		mToastText = data.getString("toast_text");
	}

}
