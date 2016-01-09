package com.fosslabs.android.view;

import com.fosslabs.android.utils.JSLog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;

import android.view.View;

public class JSGalleryAnimatorListener implements AnimatorListener{
	private static final String TAG = "JSGalleryAnimatorListener";

	private View mView;
	
	public JSGalleryAnimatorListener (View v){
		mView = v;
	}
	@Override
	public void onAnimationCancel(Animator animator) {
		// TODO Auto-generated method stub
		//import android.support.v4.view.ViewCompat;
	}

	@Override
	public void onAnimationEnd(Animator animator) {
		//ViewCompat.setHasTransientState(mView, true);
		
	}

	@Override
	public void onAnimationRepeat(Animator animator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animator animator) {
		//ViewCompat.setHasTransientState(mView, true);
		
	}

	
}
