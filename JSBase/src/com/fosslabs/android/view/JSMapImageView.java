package com.fosslabs.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class JSMapImageView extends ImageView{

	public interface OnDrawFirstTime {
        void draw();
    }
	
	private boolean mDrawFirstTime = true;
	private OnDrawFirstTime odft;
	
	public void setOnMeasure(OnDrawFirstTime odft){
		this.odft = odft;
	}
	public JSMapImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public JSMapImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public JSMapImageView(Context context) {
		super(context);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(mDrawFirstTime){
			mDrawFirstTime = false;
			odft.draw();
		}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//if(widthMeasureSpec > 0 && heightMeasureSpec > 0) 
	}
	
	

}
