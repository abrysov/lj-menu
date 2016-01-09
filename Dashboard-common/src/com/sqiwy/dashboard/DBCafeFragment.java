package com.sqiwy.dashboard;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.fosslabs.android.utils.JSLog;


/**
 * Created by abrysov
 */

public class DBCafeFragment extends Fragment{
	private static final String TAG = "DBGrandFragment";
	private static final int ANIMATION_DURATION_BASE = 300;

	private static class ViewHolder {
		LinearLayout ll_top;
		LinearLayout ll_bottom;
		LinearLayout ll_parent;
	}
	private boolean mStart = true;
	private ViewHolder vh = new ViewHolder();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_cafe, null);
		createView(v);
		return v;
	}
	
	private void createView(View v){
		vh.ll_top = (LinearLayout) v.findViewById(R.id.ll_top);
		vh.ll_bottom = (LinearLayout) v.findViewById(R.id.ll_bottom);
		vh.ll_parent = (LinearLayout) v.findViewById(R.id.ll_parent);
		int d0 = getResources().getDimensionPixelSize(R.dimen.mosaic_layout_spacing);
		int d1 = getResources().getDimensionPixelSize(R.dimen.fragment_grand_height)
				+ getResources().getDimensionPixelSize(R.dimen.fragment_grand_padding);
		vh.ll_parent.setPadding(d0, d0+d1, d0, d0);
		
		final View child = vh.ll_top;
		child.setAlpha(0);
		child.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				//Funs.getToast(getActivity(), "onGlobalLayout() " + child.getHeight());
				animTop(child);			
				if(android.os.Build.VERSION.SDK_INT >= 16){//android.os.Build.VERSION_CODES.JELLY_BEAN){
					child.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}
				else
					child.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				
			}
		});
		final View child2 = vh.ll_bottom;
		child.setAlpha(0);
		child.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				//Funs.getToast(getActivity(), "onGlobalLayout() " + child.getHeight());
				animBottom(child2);			
				if(android.os.Build.VERSION.SDK_INT >= 16){//android.os.Build.VERSION_CODES.JELLY_BEAN){
					child2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}
				else
					child2.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				
			}
		});
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//animTop(vh.ll_top);
		//animBottom(vh.ll_bottom);
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mStart = false;
	}
	private void animTop(View v){
		
		ObjectAnimator animator_x = ObjectAnimator
				.ofFloat(v, View.ALPHA, 0f, 1f);
		animator_x.setDuration(ANIMATION_DURATION_BASE*2);
		
		int y= v.getTop();
		if(mStart ) y += vh.ll_parent.getPaddingTop();
		ObjectAnimator animator_y = ObjectAnimator
				.ofFloat(v, "y", -1400, y);
		animator_y.setDuration(ANIMATION_DURATION_BASE);
				
		AnimatorSet set = new AnimatorSet();
		set.setInterpolator(new LinearInterpolator());
		set.playTogether(animator_x, animator_y);
		set.start();
	}
	

	private void animBottom(View v){
		ObjectAnimator animator_x = ObjectAnimator
				.ofFloat(v, View.ALPHA, 0f, 1f);
		animator_x.setDuration(ANIMATION_DURATION_BASE*2);
		int y= v.getTop();
		if(mStart ) y += vh.ll_parent.getPaddingTop();
		ObjectAnimator animator_y = ObjectAnimator
				.ofFloat(v, "y", +1400, y);
		animator_y.setDuration(ANIMATION_DURATION_BASE);
				
		AnimatorSet set = new AnimatorSet();
		set.setInterpolator(new LinearInterpolator());
		set.playTogether(animator_x, animator_y);
		set.start();
	}
}

