package com.fosslabs.android.view;

import com.fosslabs.android.utils.JSLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;

public class JSLinearLayout extends LinearLayout {
	private static final String TAG = "JSLinearLayout";

	private int mJSWidth = -1;

	private int mJSHeight = -1;

	public int getJSWidth() {
		return mJSWidth;
	}

	public void setJSWidth(int jSWidth) {
		mJSWidth = jSWidth;
	}

	public int getJSHeight() {
		return mJSHeight;
	}

	public void setJSHeight(int jSHeight) {
		mJSHeight = jSHeight;
	}

	public JSLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public JSLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public JSLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		f2(widthMeasureSpec, heightMeasureSpec);
	}
	private void f1(int widthMeasureSpec, int heightMeasureSpec) {

		final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		boolean resizeWidth = widthSpecMode != MeasureSpec.EXACTLY;
		boolean resizeHeight = heightSpecMode != MeasureSpec.EXACTLY;

		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int res_width = this.getWidth();
		int res_height = this.getHeight();
		
		if (JSLog.isDebug()) {
			JSLog.d(TAG, "onMeasure " + res_width + " " + res_height + resizeWidth
					+ resizeHeight);
		}
		
		if (res_width != 0 && res_height != 0) {
			
			if (JSLog.isDebug()) {
				JSLog.d(TAG, "onMeasure" + res_width + " " + res_height);
			}

			if (resizeWidth && mJSWidth > 0) {
				res_width = mJSWidth;
			}
			if (resizeHeight && mJSHeight > 0) {
				res_height = mJSHeight;
			}
			
			if (JSLog.isDebug()) {
				JSLog.d(TAG, "result " + res_width + " " + res_height);
			}
			
			this.setMeasuredDimension(res_width, res_height);

		}
	}
	private void f2(int widthMeasureSpec, int heightMeasureSpec) {
		int res_width = this.getMeasuredWidth();
		int res_height = this.getMeasuredHeight();
		
		if (JSLog.isDebug()) {
			JSLog.d(TAG, "onMeasure " + res_width + " " + res_height);
		}
		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;
		if (mJSWidth > 0) {
			// Measure Width
			if (widthMode == MeasureSpec.EXACTLY) {
				// Must be this size
				width = this.getMeasuredWidth();
			} else if (widthMode == MeasureSpec.AT_MOST) {
				// Can't be bigger than...
				width = Math.min(mJSWidth, widthSize);
			} else {
				// Be whatever you want
				width = mJSWidth;
			}
		} else {
			width = this.getMeasuredWidth();
		}

		if (mJSHeight > 0) {
			// Measure Height
			if (heightMode == MeasureSpec.EXACTLY) {
				// Must be this size
				height = this.getMeasuredHeight();
			} else if (heightMode == MeasureSpec.AT_MOST) {
				// Can't be bigger than...
				height = Math.min(mJSHeight, heightSize);
			} else {
				// Be whatever you want
				height = mJSHeight;
			}

		} else {
			height = this.getMeasuredHeight();
		}
		
		if (JSLog.isDebug()) {
			JSLog.d(TAG, "result " + width + " " + height);
		}
		// MUST CALL THIS
		setMeasuredDimension(width, height);
	}
}