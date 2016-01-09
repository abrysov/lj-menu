package com.sqiwy.dashboard.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by abrysov
 */

public final class AnimUtils {
	
	private AnimUtils() {};
	
	public static void animateViewInset(View v) {

		float defaultScale = 1.0f;
		float minScale = 0.9f;
		
		v.setPivotX(v.getWidth() / 2);
        v.setPivotY(v.getHeight() / 2);
		
		ObjectAnimator scaleDownAnimator = ObjectAnimator.ofPropertyValuesHolder(v,
				PropertyValuesHolder.ofFloat(View.SCALE_X, defaultScale, minScale),
				PropertyValuesHolder.ofFloat(View.SCALE_Y, defaultScale, minScale));
		scaleDownAnimator.setInterpolator(new AccelerateInterpolator());
		
		ObjectAnimator scaleUpAnimator = ObjectAnimator.ofPropertyValuesHolder(v,
				PropertyValuesHolder.ofFloat(View.SCALE_X, minScale, defaultScale),
				PropertyValuesHolder.ofFloat(View.SCALE_Y, minScale, defaultScale));
		scaleUpAnimator.setInterpolator(new OvershootInterpolator(4f));
	
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setDuration(100).playSequentially(scaleDownAnimator, scaleUpAnimator);
		animatorSet.start();
	}
}
