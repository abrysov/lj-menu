package com.fosslabs.android.view;

import com.fosslabs.android.utils.Funs;
import com.fosslabs.android.utils.JSLog;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.nfc.Tag;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class JSMapImageViewTouch implements OnTouchListener {
	private static final String TAG = "JSMapImageViewTouch";
	
	public interface OnDoubleClickListener {
        void doubleClick(boolean start, float x, float y, JSMapImageView iv, float x2, float y2);
    }
	private OnDoubleClickListener onDoubleClickListener;
	
	public JSMapImageViewTouch( OnDoubleClickListener onDoubleClickListener) {
		this.onDoubleClickListener = onDoubleClickListener;
	}
	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;


	PointF remember = null;
	float eps = 20;
	boolean b_prepair = false;
	boolean last_motion_event = false;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		JSMapImageView view = (JSMapImageView) v;
		// Dump touch event to log
		dumpEvent(event);

		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			last_motion_event = true;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
			if(Math.abs(event.getX() - start.x) < eps && Math.abs(event.getY() - start.y) < eps && last_motion_event){
				 b_prepair = true;
				/*if(remember == null) remember = new PointF(start.x, start.y);
				else{
					if(Math.abs(remember.x - start.x) < eps && Math.abs(remember.y - start.y) < eps){
						
						prepair(remember.x, remember.y, view);
						remember = null;
					}
						
				}*/
			}
			else{
				remember = null;
				b_prepair = false;
				last_motion_event = false;
			}
				
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				// ...
				matrix.set(savedMatrix);
				Log.i("&&&&&&&&&&&&&", "" + (event.getX() - start.x) + ", "+  (event.getY()
						- start.y));
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
				
				float[] f = new float[9];
				matrix.getValues(f);
				 Log.i("&&&&&&&&&&&&&", "TRANSFORM " + f[Matrix.MTRANS_X] + " " + f[Matrix.MTRANS_Y]); 
				 
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;	
					matrix.postScale(scale, scale, mid.x, mid.y);
					float[] f = new float[9];
					matrix.getValues(f);
	                Log.i("&&&&&&&&&&&&&", "scale " + f[Matrix.MSCALE_X] + " " + f[Matrix.MSCALE_Y]);
	                Log.i("&&&&&&&&&&&&&", "TRANSFORM " + f[Matrix.MTRANS_X] + " " + f[Matrix.MTRANS_Y]);
	                
				}
			}
			break;
		}
		
		if(!checkMinScale(matrix)) view.setImageMatrix(matrix);
		if(b_prepair) prepair(start.x , start.y, view);
		 Log.i("&&&&&&&&&&&&&", "view.getWidth() " + view.getDrawable().getIntrinsicWidth() + " "
		+ view.getDrawable().getIntrinsicHeight()); 
		return true; // indicate event was handled
	}

	private boolean checkMinScale(Matrix matrix){
		float[] values = new float[9];
		matrix.getValues(values);
		
		return ((values[0] < 0.5) || (values[4] < 0.5));// || (values[8] < 1.0));
		
		
	}
	private void prepair(float x, float y, JSMapImageView iv){
		last_motion_event = false;
		b_prepair = false;
		Log.i("&&&&&&&&&&&&&", "Prepair()");
		float[] f = new float[9];
		matrix.getValues(f);
        Log.i("&&&&&&&&&&&&&", "scale " + f[Matrix.MSCALE_X] + " " + f[Matrix.MSCALE_Y]);
        Log.i("&&&&&&&&&&&&&", "TRANSFORM " + f[Matrix.MTRANS_X] + " " + f[Matrix.MTRANS_Y]); 
        float res_x = (x) / f[Matrix.MSCALE_X];
        float res_y = (y) / f[Matrix.MSCALE_Y];
      
        float res_x2 = (0 + f[Matrix.MTRANS_X])/ f[Matrix.MSCALE_X];
        float res_y2 = (0 + f[Matrix.MTRANS_Y])/ f[Matrix.MSCALE_Y];
        
        float res_x3 = (x-f[Matrix.MTRANS_X]) / f[Matrix.MSCALE_X];
        float res_y3 = (y-f[Matrix.MTRANS_Y]) / f[Matrix.MSCALE_Y];
        
        Log.i("&&&&&&&&&&&&&", "RES " + res_x + " " + res_y); 
        Log.i("&&&&&&&&&&&&&", "RES " + res_x2 + " " + res_y2); 
        Log.i("&&&&&&&&&&&&&", "RES " + res_x3 + " " + res_y3); 
		onDoubleClickListener.doubleClick(false, res_x, res_y, iv, -res_x2, -res_y2);
	}
	/** Show an event in the LogCat view, for debugging */
	private void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
				"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
		Log.i("yyyy", new String(sb));
	}

	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

}
