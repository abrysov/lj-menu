package com.sqiwy.dashboard.model.action;

import android.content.Context;
import android.os.Bundle;


/**
 * Created by abrysov
 */

public class ActionContext {
	
	private Context mContext;
	private Bundle bundle;
	
	public Context getContext() {
		return mContext;
	}

	public void setContext(Context context) {
		this.mContext = context;
	}

	public Bundle getBundle() {
		return bundle;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}
	
}
