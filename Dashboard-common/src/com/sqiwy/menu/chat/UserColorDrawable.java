package com.sqiwy.menu.chat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by abrysov
 */

public class UserColorDrawable extends Drawable {
	
	private Paint mPaint;
	private int mSize;

	public UserColorDrawable(String color, Rect bounds, int size) {
		mPaint = new Paint();
		mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		this.setBounds(bounds);
		mSize = size;
		mPaint.setColor(Color.parseColor(color));
	}
	
	
	@Override
	public void draw(Canvas canvas) {
		canvas.drawCircle(mSize, mSize, mSize, mPaint);
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

}
