package com.sqiwy.dashboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by abrysov
 */

public class MaxWidthLinearLayout extends LinearLayout {

	/**
	 * 
	 */
	private int mMaxWidth = Integer.MAX_VALUE;

	/**
	 * 
	 * @param context
	 */
	public MaxWidthLinearLayout(Context context) {

		super(context);
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public MaxWidthLinearLayout(Context context, AttributeSet attrs) {

		super(context, attrs);

//		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaxWidthLinearLayout);
//		mMaxWidth = a.getDimensionPixelSize(R.styleable.MaxWidthLinearLayout_maxWidth, Integer.MAX_VALUE);
	}

	/**
	 * 
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		// get measured height
		if (getMeasuredWidth() > mMaxWidth) {
			
			setMeasuredDimension(mMaxWidth, getMeasuredHeight());
		}
	}
	
	/**
	 * 
	 * @param maxWidth
	 */
	public void setMaxWidth(int maxWidth) {
		
		mMaxWidth = maxWidth;
		requestLayout();
	}
}
