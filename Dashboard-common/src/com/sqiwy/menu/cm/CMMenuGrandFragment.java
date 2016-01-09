package com.sqiwy.menu.cm;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sqiwy.dashboard.ActionFragment;
import com.sqiwy.dashboard.R;
import com.sqiwy.dashboard.model.action.Action;
import com.sqiwy.dashboard.model.action.Action.OnActionDoneListener;
import com.sqiwy.dashboard.util.ExecutorUtils;
import com.sqiwy.menu.model.ProjectConstants;

/**
 * Created by abrysov
 */

public class CMMenuGrandFragment extends ActionFragment {
	private static final String TAG = "CMMenuGrandFragment";
	
	private static class ViewHolder {
		View ll_parent;
		View ll_invis;
		View ll_vis;
		ImageView iv_home;
		LinearLayout ll_menu;
		LinearLayout btn_call_waiter;
		LinearLayout tv_rotate;
		TextView tv_call_waiter;
		TextView tv_menu;
		
	}

	private ViewHolder vh = new ViewHolder();
	private Callbacks mCallbacks;
	private volatile boolean mIsCallingWaiter = false;

	public interface Callbacks {
		void onConfigurationChanged();
		
	}

	@Override
	public void onResume() {

		super.onResume();
		
		mIsCallingWaiter = false;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.cm_fragment_menu_grand, null);
		createView(v);
		return v;
	}

	private void createView(View v) {
		vh.iv_home = (ImageView) v.findViewById(R.id.iv_home);
		vh.iv_home.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				anim_item_menu_click(v);
				getActivity().finish();
				
			}
		});
		
		vh.ll_menu = (LinearLayout) v.findViewById(R.id.ll_menu);
		vh.ll_menu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//anim_item_menu_click(v);
				//mCallbacks.showProductCategoryList();
			}
		});
		vh.tv_rotate = (LinearLayout) v.findViewById(R.id.btn_rotate_screen);
		vh.tv_rotate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				anim_item_menu_click(v);
				mCallbacks.onConfigurationChanged();
				
			}
		});
		vh.tv_menu = (TextView) v.findViewById(R.id.tv_menu);
		vh.btn_call_waiter = (LinearLayout) v.findViewById(R.id.btn_call_waiter);
		vh.btn_call_waiter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				anim_item_menu_click(v);
				if(!mIsCallingWaiter) {
					mIsCallingWaiter = true;
					
					Action callWaiterAction = Action.Resolver.create(Action.TYPE_CALL_WAITER).setOnActionDoneListener(new OnActionDoneListener() {
						
						@Override
						public void onActionDone(Action action) {

							ExecutorUtils.executeOnUiThreadDelayed(new Runnable() {
								
								@Override
								public void run() {

									mIsCallingWaiter = false;
								}	
							}, 1000);
						}
					});
					
					getActionHelper().executeDelayed(callWaiterAction, getActivity());
				}
			}
		});
	}

	
	@Override
	public void onStart() {
		super.onStart();
	}

	
	private void anim_item_menu_click(View v) {	
		ObjectAnimator animAlpha = ObjectAnimator.ofFloat(v, "alpha", 0, 1);
		animAlpha.setDuration(ProjectConstants.getAnimDuration(getActivity()));
		animAlpha.start();
	}
	
}
