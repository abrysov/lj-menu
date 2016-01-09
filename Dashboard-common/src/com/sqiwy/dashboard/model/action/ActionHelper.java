package com.sqiwy.dashboard.model.action;

import android.content.Context;
import android.os.Handler;

/**
 * Created by abrysov
 * Simple class to execute delayed actions.
 */
public class ActionHelper {
	
	public static final long DEFAULT_DELAY = 200;
	
	private Handler mHandler;
	
	public void register(Context context) {
		mHandler = new Handler();
	}
	
	public void unregister(Context context) {
		mHandler.removeCallbacksAndMessages(null);
		mHandler = null;
	}
	
	public void executeDelayed(Action action, Context context) {
		if (action != null && context != null) {
			ActionContext actionContext = new ActionContext();
			actionContext.setContext(context);
			executeDelayed(action, actionContext);
		}
	}
	
	private void executeDelayed(Action action, ActionContext actionContext) {
		executeDelayed(action, actionContext, DEFAULT_DELAY);
	}
	
	private void executeDelayed(final Action action, final ActionContext actionContext, long delay) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				action.execute(actionContext);
			}
		}, delay);
	}
}
