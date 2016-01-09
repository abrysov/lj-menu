package com.sqiwy.menu.ch;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sqiwy.dashboard.R;
import com.sqiwy.menu.cm.OnOrderTotalSumChangeListener;
import com.sqiwy.menu.model.ProjectConstants;

/**
 * Created by abrysov
 */

public class CHMenuGrandFragment extends Fragment implements OnOrderTotalSumChangeListener{
	private static final String TAG = "CMMenuGrandFragment";
	
	private static class ViewHolder {
		View ll_parent;
		View ll_invis;
		View ll_vis;
		ImageView iv_home;
		LinearLayout ll_menu;
		ImageView iv_rotate;
		TextView tv_sum;
		ImageView iv_lock;
		
	}

	private ViewHolder vh = new ViewHolder();
	private Callbacks mCallbacks;

	public interface Callbacks {
		void onConfigurationChanged();
		void onLock();
		void showProductCategoryList();
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
		View v = inflater.inflate(R.layout.ch_fragment_menu_grand, null);
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
				anim_item_menu_click(v);
				mCallbacks.showProductCategoryList();
			}
		});
		vh.iv_rotate = (ImageView) v.findViewById(R.id.iv_rotate);
		vh.iv_rotate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				anim_item_menu_click(v);
				mCallbacks.onConfigurationChanged();
				
			}
		});
	
		vh.tv_sum = (TextView) v.findViewById(R.id.tv_sum);
		
		vh.iv_lock = (ImageView) v.findViewById(R.id.iv_lock);
		vh.iv_lock.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				anim_item_menu_click(v);
				mCallbacks.onLock();
				
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
	
	@Override
	public void setOrderTotalSum(String sum) {
		vh.tv_sum.setText(sum);
		
	}
	
	
}
