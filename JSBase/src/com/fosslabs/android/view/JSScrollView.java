package com.fosslabs.android.view;

import com.fosslabs.android.context.ActivityWithDragView;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class JSScrollView extends ScrollView{
	private ViewGroup mVg = null;
	private ActivityWithDragView mADV = null;
	
	public JSScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}
	
	public JSScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	

	public JSScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	
	public ViewGroup getVg() {
		return mVg;
	}

	public void setVg(ViewGroup vg) {
		mVg = vg;
	}

	
	public ActivityWithDragView getADV() {
		return mADV;
	}

	public void setADV(ActivityWithDragView aDV) {
		mADV = aDV;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		if(mVg != null && mVg.getChildCount() > 0) mVg.removeAllViews();
		if(mADV != null) mADV.hideDragContainer();
	}

	
}
