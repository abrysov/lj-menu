package com.sqiwy.dashboard;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.sqiwy.dashboard.model.ActionTile;
import com.sqiwy.dashboard.model.action.Action;
import com.sqiwy.dashboard.model.action.ActionHelper;
import com.sqiwy.dashboard.util.AnimUtils;
import com.sqiwy.menu.util.UIUtils;

/**
 * Created by abrysov
 */

/**
 * Provides action helper to nested classes.
 */
public class ActionFragment extends Fragment {
	
	private ActionHelper mActionHelper;
	private OnClickListener mActionViewClickListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActionHelper = new ActionHelper();
		mActionHelper.register(getActivity());
		
		mActionViewClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
                UIUtils.setViewTemporarilyUnclickable(v);
				AnimUtils.animateViewInset(v);
				
				ActionTile actionTile = (ActionTile) v.getTag();
				Action action;
				if (null != actionTile && null != (action = actionTile.getAction())) {
					getActionHelper().executeDelayed(action, getActivity());						
				}
			}
		};
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mActionHelper.unregister(getActivity());
	}
	
	public ActionHelper getActionHelper() {
		return mActionHelper;
	}
	
	public OnClickListener getDefaultActionViewClickListener() {
		return mActionViewClickListener;
	}
}
