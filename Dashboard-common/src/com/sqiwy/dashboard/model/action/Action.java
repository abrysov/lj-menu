package com.sqiwy.dashboard.model.action;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Created by abrysov
 * Representation of action which can be executed on tile click/touch.
 */
public interface Action extends Serializable {
	
	public static final int REQUEST_CODE = 10000;
	
	public static final String TYPE_CALL_WAITER = "call_waiter";
	public static final String TYPE_OPEN_APP = "open_app";
	public static final String TYPE_OPEN_CHAT = "open_chat";
	public static final String TYPE_OPEN_MAP = "open_map";
	public static final String TYPE_OPEN_MENU = "open_menu";
	public static final String TYPE_OPEN_URL = "open_url";
	public static final String TYPE_SHOW_TOAST = "show_toast";
	public static final String TYPE_CLOSE_SESSION = "close_session";
    public static final String TYPE_OPEN_GAMES = "open_games";
	
	static final String JSON_ACTION = "action";
	static final String JSON_TYPE = "type";
	
	public static interface OnActionDoneListener {
		
		void onActionDone(Action action);
	}
	
	public void execute(ActionContext actionContext);
	
	public void init(JSONObject data) throws JSONException; 
	
	public String getType();
	
	public Action setOnActionDoneListener(OnActionDoneListener listener);
	
	/**
	 * Special class that will help us to resolve actions from JSON. 
	 */
	public static class Resolver {
		
		private static final String TAG = Resolver.class.getName();
		
		public static Action resolve(JSONObject json) throws JSONException {
			Action result = null;

			if (!json.isNull("action")) {
				
				JSONObject action = json.getJSONObject("action");
				String type = action.getString("type");
				
				result = create(type);
				
				if (null != result) {
					result.init(action);
				}
			}
			
			return result;
		}
		
		public static Action create(String type) {
			Action result = null;
			if (Action.TYPE_CALL_WAITER.equals(type)) {
				result = new ActionCallWaiter();
			} else if (Action.TYPE_OPEN_APP.equals(type)) {
				result = new ActionOpenApp();
			} else if (Action.TYPE_OPEN_MAP.equals(type)) {
				result = new ActionOpenMap();
			} else if (Action.TYPE_OPEN_MENU.equals(type)) {
				result = new ActionOpenMenu();
			} else if (Action.TYPE_OPEN_URL.equals(type)) {
				result = new ActionOpenUrl();
			} else if (Action.TYPE_SHOW_TOAST.equals(type)) {
				result = new ActionShowToast();
			} else if (Action.TYPE_CLOSE_SESSION.equals(type)) {
				result = new ActionClearUserData();
			} else if (Action.TYPE_OPEN_CHAT.equals(type)) {
				result = new ActionOpenChat();
            } else if (Action.TYPE_OPEN_GAMES.equals(type)) {
                result = new ActionOpenGames();
			}
			else {
				Log.w(TAG, "It seems we have incorrect action type: " + type);
			}
			
			return result;
		}

		public static Action resolve(String json) throws JSONException {
			Action result = null;

			result = resolve(new JSONObject(json));

			return result;
		}
	}
}
