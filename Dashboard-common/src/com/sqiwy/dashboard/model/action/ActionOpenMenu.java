
package com.sqiwy.dashboard.model.action;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.sqiwy.dashboard.CancelableToast;
import com.sqiwy.dashboard.R;
import com.sqiwy.dashboard.CancelableToast.VerticalAlignment;
import com.sqiwy.menu.cm.CMProductListActivity;
import com.sqiwy.menu.util.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abrysov
 */

@SuppressWarnings("serial")
class ActionOpenMenu extends ActionBase {

	private String mCategory="";
	
	@Override
	public void execute(ActionContext actionContext) {
		super.execute(actionContext);
		
		Context context = actionContext.getContext();
		Intent intent = new Intent(context, CMProductListActivity.class);
		intent.putExtra(CMProductListActivity.EXTRA_CATEGORY, mCategory);
		
		ActivityOptions options = ActivityOptions
				.makeCustomAnimation(context, R.anim.move_down_enter, R.anim.move_down_exit);
		
		if (context instanceof Activity) {
			
			if(PreferencesUtils.isMenuLoaded(context)) {
        	
				((Activity) context).startActivityForResult(intent, REQUEST_CODE, options.toBundle());
			}
			else {
				
				new CancelableToast.Config()
				.setText(R.string.menu_not_loaded_message)
				.setVerticalAlignment(VerticalAlignment.CENTER)
				.show(((Activity) context).getFragmentManager());
			}
		} 
		else {
			
			if(PreferencesUtils.isMenuLoaded(context)) {
			
				context.startActivity(intent, options.toBundle());
			}
			else {
				
				Toast.makeText(context, R.string.menu_not_loaded_message, Toast.LENGTH_LONG).show();
			}
		}
		
        // notify action is done
        onActionDone();
	}

	@Override
	public String getType() {
		return Action.TYPE_OPEN_MENU;
	}

	@Override
	public void init(JSONObject data) throws JSONException {
		super.init(data);
		
		if (!data.isNull("category")) {
			mCategory = data.getString("category");
		}
		
	}

}
