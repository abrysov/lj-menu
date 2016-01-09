package com.sqiwy.dashboard.model;

import java.io.Serializable;

import com.sqiwy.dashboard.model.action.Action;

/**
 * Created by abrysov
 */

/**
 * To execute action:
 * <p>
 * <pre><code>
 * ...
 * Action action = tile.getAction();
 * ActionContext actionContext = new ActionContext();
 * actionContext.setContext(...);
 * action.execute(actionContext);
 * </code></pre> 
 */
@SuppressWarnings("serial")
public class ActionTile implements Serializable {

	protected Action mAction;
	
	public ActionTile(Action action) {
		mAction = action;
	}

	public Action getAction() {
		return mAction;
	}

	public void setAction(Action action) {
		this.mAction = action;
	}
	
}
