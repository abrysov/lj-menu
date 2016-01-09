package com.sqiwy.dashboard.model.action;

import org.json.JSONException;
import org.json.JSONObject;

import com.sqiwy.menu.util.SystemControllerHelper;

/**
 * Created by abrysov
 * Parsed from: ь
 * <pre>
 * 	"action": {
 *      ...
 * 		"system_ui_mode": "2"
 *      ...
 *	}
 * </pre>
 *
 * <code>system_ui_mode</code> - see {@link com.sqiwy.menu.util.SystemControllerHelper} for possible values
 */
@SuppressWarnings("serial")
public abstract class ActionBase implements Action {

	private int mSystemUiMode = -1;
	private OnActionDoneListener mOnActionDoneListener = null;
	
	@Override
	public void execute(ActionContext actionContext) {
		if (-1 != mSystemUiMode) {
			SystemControllerHelper.setSystemUiMode(mSystemUiMode);
		}
	}

	@Override
	public String getType() {
		return Action.TYPE_OPEN_URL;
	}

	@Override
	public void init(JSONObject data) throws JSONException {
		mSystemUiMode = data.optInt("system_ui_mode", -1);
	}
	
	/**
	 * 
	 */
	@Override
	public Action setOnActionDoneListener(OnActionDoneListener listener) {
		
		mOnActionDoneListener = listener;
		
		return this;
	}
	
	/**
	 * 
	 */
	protected void onActionDone() {
		
		if(null != mOnActionDoneListener) {
			
			mOnActionDoneListener.onActionDone(this);
		}
	}
}
