package com.fosslabs.android.view;

import com.fosslabs.android.jsbase.R;
import com.fosslabs.android.utils.JSImageWorker;
import com.fosslabs.android.utils.JSLog;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;

public class MosaicLayout extends ViewGroup {

	private static final String TAG = "MosaicLayout";

	private final int DEFAULT_ROW_HEIGHT = 48;
	private final int DEFAULT_COLUMN_WIDTH = 48;

	private float mColumnWidth = 0;
	private float mRowHeight = DEFAULT_ROW_HEIGHT;

	private float mSpacingVertical = 0;
	private float mSpacingHorizontal = 0;
	
	private float mCellWidthToHeightRatio = 1.0f;
	
	private MosaicAdapter mAdapter = null;

	public MosaicAdapter getAdapter() {
		return mAdapter;
	}

	public void setAdapter(MosaicAdapter adapter) {
		mAdapter = adapter;
		for (int i = 0; i < mAdapter.getCount(); i++) {
			View v = mAdapter.getView(i, null, null);
			addView(v);
		}
	}

	public MosaicLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MosaicLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.MosaicLayout);
		try {
			mSpacingVertical = a.getDimension(
					R.styleable.MosaicLayout_spacing_vertical, 0f);
			mSpacingHorizontal = a.getDimension(
					R.styleable.MosaicLayout_spacing_horizontal, 0f);
			mRowHeight = a.getDimension(R.styleable.MosaicLayout_row_height,
					DEFAULT_ROW_HEIGHT);
			
			JSLog.d(TAG, "mRowHeight Mosaic Layout = " + mRowHeight);

		} finally {
			a.recycle();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		//JSLog.e(TAG, "onLayout " + changed + " " + l + " " + t + " " + r + " "
		//		+ b);
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);

			MosaicLayoutParams p = (MosaicLayoutParams) child.getLayoutParams();

			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();

			int childLeft = (int) (p.left * (mColumnWidth + mSpacingHorizontal) + getPaddingLeft());
			int childTop = (int) (p.top * (mRowHeight + mSpacingVertical) + getPaddingTop());

			child.layout(childLeft, childTop, childLeft + childWidth, childTop
					+ childHeight);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		int measuredWidth = 0;
		int measuredHeight = 0;

		boolean measureWidthByChildren = false;
		boolean measureHeightByChildren = false;

		int columns = 0;

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			MosaicLayoutParams p = (MosaicLayoutParams) child
					.getLayoutParams();
			columns = Math.max(p.left + p.width, columns);
		}

		measuredWidth = width;
		/*JSLog.d(TAG,
				"measuredWidth  " + measuredWidth + " "
						+ this.getPaddingLeft() + " "
						+ this.getPaddingRight());
		*/
		int vr_measuredWidth = measuredWidth;
		vr_measuredWidth -= (this.getPaddingLeft() + this.getPaddingRight());
		vr_measuredWidth -= mSpacingHorizontal * (columns - 1);
		mColumnWidth = (float) vr_measuredWidth / columns;
		
		mRowHeight = mColumnWidth / mCellWidthToHeightRatio;

		JSLog.d(TAG, "onMeasure + mColumnCount " + columns + " "
				+ mColumnWidth + " " + measuredWidth + vr_measuredWidth);

		measuredHeight = height;
		
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);

			MosaicLayoutParams p = (MosaicLayoutParams) child.getLayoutParams();

			int childWidth = (int) (p.width * mColumnWidth + (p.width - 1)* mSpacingHorizontal);
			int childHeight = (int) (p.height * mRowHeight + (p.height - 1)* mSpacingVertical);

			int widthSpec = MeasureSpec.makeMeasureSpec(childWidth,
					MeasureSpec.EXACTLY);
			int heightSpec = MeasureSpec.makeMeasureSpec(childHeight,
					MeasureSpec.EXACTLY);

			child.measure(widthSpec, heightSpec);
			if(child.getBackground() == null){
				JSLog.d(TAG, "onMeasure child.getBackground() == null");
				mAdapter.setChildBg(child, i, childWidth, childHeight);
			}
		}
		JSLog.e(TAG, "onMeasure end " + measuredWidth + " " + measuredHeight);
		setMeasuredDimension(measuredWidth, measuredHeight);
	}
	/*
	private void f(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		int measuredWidth = 0;
		int measuredHeight = 0;

		boolean measureWidthByChildren = false;
		boolean measureHeightByChildren = false;

		int columns = 0;

		if (widthMode == MeasureSpec.AT_MOST
				|| widthMode == MeasureSpec.EXACTLY) {
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				LayoutParams p = (LayoutParams) child.getLayoutParams();
				columns = Math.max(p.left + p.width, columns);
			}

			measuredWidth = width;

			columnWidth = (float) measuredWidth / columns;
		} else {
			measureWidthByChildren = true;
		}

		if (heightMode == MeasureSpec.AT_MOST
				|| heightMode == MeasureSpec.EXACTLY) {
			measuredHeight = height;
		} else {
			measureHeightByChildren = true;
		}

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);

			LayoutParams p = (LayoutParams) child.getLayoutParams();

			int childWidth = (int) (p.width * columnWidth + (p.width - 1)
					* spacingHorizontal);
			int childHeight = (int) (p.height * rowHeight + (p.height - 1)
					* spacingVertical);

			int widthSpec = MeasureSpec.makeMeasureSpec(childWidth,
					MeasureSpec.EXACTLY);
			int heightSpec = MeasureSpec.makeMeasureSpec(childHeight,
					MeasureSpec.EXACTLY);

			child.measure(widthSpec, heightSpec);

			if (measureWidthByChildren) {
				int childLeft = (int) (p.left
						* (columnWidth + spacingHorizontal) + spacingHorizontal);
				measuredWidth = Math.max(measuredWidth, childLeft + childWidth);
			}

			if (measureHeightByChildren) {
				int childTop = (int) (p.top * (rowHeight + spacingVertical) + spacingVertical);
				measuredHeight = Math.max(measuredHeight, childTop
						+ childHeight);
			}

		}

		setMeasuredDimension(measuredWidth, measuredHeight);
	}*/

	/*public float getColumnWidth() {
		return mColumnWidth;
	}

	public void setColumnWidth(float columnWidth) {
		mColumnWidth = columnWidth;
	}

	public float getRowHeight() {
		return mRowHeight;
	}*/

	public void setRowHeight(float rowHeight) {
		mRowHeight = rowHeight;
	}

	public float getSpacingVertical() {
		return mSpacingVertical;
	}

	/*public void setSpacingVertical(float spacingVertical) {
		mSpacingVertical = spacingVertical;
	}*/

	public float getSpacingHorizontal() {
		return mSpacingHorizontal;
	}

	/*public void setSpacingHorizontal(float spacingHorizontal) {
		mSpacingHorizontal = spacingHorizontal;
	}*/
	
	public void setCellWidthToHeightRatio(float cellWidthToHeightRatio) {
		mCellWidthToHeightRatio = cellWidthToHeightRatio;
	}
	
	public float getCellWidthToHeightRatio() {
		return mCellWidthToHeightRatio;
	}
	
	public static class MosaicLayoutParams extends ViewGroup.LayoutParams {

		int left = 0;
		int top = 0;
		int width = 1;
		int height = 1;

		public MosaicLayoutParams(int width, int height) {
			super(width, height);
		}

		public MosaicLayoutParams(int left, int top, int width, int height) {
			super(WRAP_CONTENT, WRAP_CONTENT);
			this.left = left;
			this.top = top;
			this.width = width;
			this.height = height;
		}

		public MosaicLayoutParams(Context c, AttributeSet attrs) {
			super(WRAP_CONTENT, WRAP_CONTENT);

			TypedArray a = c.obtainStyledAttributes(attrs,
					R.styleable.MosaicLayoutChild);
			try {
				left = a.getInt(R.styleable.MosaicLayoutChild_left, 0);
				top = a.getInt(R.styleable.MosaicLayoutChild_top, 0);
				width = a.getInt(R.styleable.MosaicLayoutChild_width, 1);
				height = a.getInt(R.styleable.MosaicLayoutChild_height, 1);
			} finally {
				a.recycle();
			}

		}

		public MosaicLayoutParams(android.view.ViewGroup.LayoutParams source) {
			super(source);

			if (source instanceof MosaicLayoutParams) {
				MosaicLayoutParams p = (MosaicLayoutParams) source;

				this.left = p.left;
				this.top = p.top;
				this.width = p.width;
				this.height = p.height;
			}
		}
	}

	@Override
	protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new MosaicLayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}

	@Override
	protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
		return p instanceof MosaicLayoutParams;
	}

	@Override
	public android.view.ViewGroup.LayoutParams generateLayoutParams(
			AttributeSet attrs) {
		return new MosaicLayoutParams(getContext(), attrs);
	}

	@Override
	protected android.view.ViewGroup.LayoutParams generateLayoutParams(
			android.view.ViewGroup.LayoutParams p) {
		return new MosaicLayoutParams(p);
	}
}
