package com.fosslabs.android.view;

import java.util.Date;

import org.joda.time.DateTime;

import com.fosslabs.android.utils.Funs;
import com.fosslabs.android.utils.JSTimeWorker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

public class JSClock extends View{
	private static final String TAG = "JSClock";
	private final float mX;
    private final float mY;
    private int [] mTimeArray;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap mArrowHour = null;
    private Bitmap mArrowMinute = null;
    private Bitmap mArrowSecond = null;
    private int mArrowColor = Color.DKGRAY;
    
    private final float mRadius;
    
	public JSClock(Context context, float x, float y, DateTime date, float minuteArrowLenght) {
		super(context);
		mX = x;
		mY= y;
		mTimeArray = JSTimeWorker.getTimeArray(date);
		mRadius = minuteArrowLenght; 
	}
	
	public JSClock(Context context, float x, float y, Date date, float minuteArrowLenght) {
		super(context);
		mX = x;
		mY= y;
		mTimeArray = JSTimeWorker.getTimeArray(date);
		mRadius = minuteArrowLenght; 
	}
	
	public void setTimeArray(DateTime date){
		mTimeArray = JSTimeWorker.getTimeArray(date);
	}

	public void setArrowColor(int arrowColor) {
		mArrowColor = arrowColor;
	}


	public void setArrowHour(Bitmap arrowHour) {
		mArrowHour = arrowHour;
	}


	public void setArrowMinute(Bitmap arrowMinute) {
		mArrowMinute = arrowMinute;
	}


	public void setArrowSecond(Bitmap arrowSecond) {
		mArrowSecond = arrowSecond;
	}

	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		customClock(canvas);
	}
		
	private void customClock(Canvas canvas){
		//canvas.drawCircle(x, y, r, mPaint);
        float sec=(float) mTimeArray [2];
        float min=(float)mTimeArray[1];
        float hour=(float)mTimeArray [0]+min/60.0f;
        
        Matrix matrix_hour = new Matrix();
        Matrix matrix_min = new Matrix();
        Matrix matrix_sec = new Matrix();
        
        float phi_hour = (float) (Math.toRadians((hour / 12.0f * 360.0f)-90f));
        float phi_min = (float) (Math.toRadians((min / 60.0f * 360.0f)-90f));
        float phi_sec = (float) (Math.toRadians((sec / 60.0f * 360.0f)-90f));
         
        float pl15 = mRadius / 3;
        float pl10 = mRadius * 2 /9;
        if(mArrowHour != null){
        	matrix_hour.setRotate(90 - phi_hour, mArrowHour.getWidth()/2, mArrowHour.getHeight()/2);
        	//matrix_hour.postRotate(45);
            canvas.drawBitmap(mArrowHour, matrix_hour, new Paint());
            canvas.save();
        }
        else{
        	 //mPaint.setColor(mArrowColor);//0xFFFF0000);
        	 mPaint.setARGB(255, 224, 222, 221);
        	 RectF rect = new RectF(mX - 5, (float) (mY - 1.5), mX+(mRadius + 2), (float) (mY + 2));
             canvas.rotate((float) Math.toDegrees(phi_hour), mX, mY);
             canvas.drawRoundRect(rect, 2, 5, mPaint);
             canvas.restore();
             canvas.save();
        }
        if(mArrowMinute != null){
        	matrix_min.setRotate(90 - phi_min, mArrowMinute.getWidth()/2, mArrowMinute.getHeight()/2);
            canvas.drawBitmap(mArrowMinute, matrix_min, new Paint());
            canvas.save();
        }
        else{
        	 //mPaint.setColor(mArrowColor);//(0xFF0000FF);
        	 mPaint.setARGB(255, 224, 222, 221);
        	 RectF rect = new RectF(mX - 5, (float) (mY - 0.75), (mX + mRadius + 11), (float) (mY + 0.75));
        	 canvas.rotate((float) Math.toDegrees(phi_min), mX, mY);
        	 canvas.drawRoundRect(rect, 2, 5, mPaint);
        	 canvas.restore();
             canvas.save();
        }
//        Add second hand
//        if(mArrowSecond != null){
//        	matrix_sec.setRotate(90-phi_sec, mArrowSecond.getWidth()/2, mArrowSecond.getHeight()/2);
//            canvas.drawBitmap(mArrowSecond, matrix_sec, new Paint());
//            canvas.save();
//        }
//        else{
//        	 mPaint.setColor(mArrowColor);//(0xFFA2BC13);
//        	 mPaint.setStrokeWidth(1);
//             canvas.drawLine(mX, mY, (float)(mX+(mRadius+pl10)*Math.cos(phi_sec)), 
//             		(float)(mY+(mRadius+pl15)*Math.sin(phi_sec)), mPaint);
//             canvas.save();
//        }
        }
   
}
