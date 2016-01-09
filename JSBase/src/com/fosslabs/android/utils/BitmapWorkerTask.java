package com.fosslabs.android.utils;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;

import com.fosslabs.android.view.AsyncDrawable;

@SuppressLint("NewApi")
public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
	private static final String TAG = "BitmapWorkerTask";
	
    private final WeakReference<View> viewReference;
    private Resources mRes;
    private Bitmap mBackground;
    private String mFileName;
    private int mPos;
    private int mMaxPos;
    private AnimFragment mAnimFragment;

    public BitmapWorkerTask(View view, Resources res, AnimFragment animFragment, 
    		Bitmap bg, String fileName, int maxPos) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        viewReference = new WeakReference<View>(view);
        mRes = res;
        mBackground = bg;
        mFileName = fileName;
        mAnimFragment = animFragment;
        mMaxPos = maxPos;
    }
    
    public BitmapWorkerTask(View view, Resources res, Bitmap bg, String fileName) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        viewReference = new WeakReference<View>(view);
        mRes = res;
        mBackground = bg;
        mFileName = fileName;
        mAnimFragment = null;
        mMaxPos = 0;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer ...params) {
    	mPos = params [0];
    	if(params.length !=3) return null;
    	Bitmap bg = JSImageWorker.loadFromFile(mFileName);
		if(bg == null) return JSImageWorker.cropFromBottomAndSave(
				mBackground, params[1], params[2], mFileName);
		else return bg;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
    	if (isCancelled()) {
            bitmap = null;
        }
    	
        if (viewReference != null && bitmap != null) {
            final View view = viewReference.get();
            final BitmapWorkerTask bitmapWorkerTask =
                    getBitmapWorkerTask(view);
            if (this == bitmapWorkerTask && view != null) {
				if(android.os.Build.VERSION.SDK_INT >= 16){//android.os.Build.VERSION_CODES.JELLY_BEAN){
					view.setBackground(new BitmapDrawable(mRes, bitmap));//mChildBackground));
				}
				else{
					view.setBackgroundDrawable(new BitmapDrawable(bitmap));//mChildBackground));
				}
            }
            JSLog.d(TAG, "onPostExecute " + mPos + mMaxPos);
            if(mPos == mMaxPos - 1 && mAnimFragment != null)
            	mAnimFragment.startAnimChild();
        }
    }
      
   
    public static boolean cancelPotentialWork(int data, View view) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(view);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.mPos;
            if (bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }
    
    private static BitmapWorkerTask getBitmapWorkerTask(View view) {
    	   if (view != null) {
    	       final Drawable drawable = view.getBackground();//imageView.getDrawable();
    	       if (drawable instanceof AsyncDrawable) {
    	           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
    	           return asyncDrawable.getBitmapWorkerTask();
    	       }
    	    }
    	    return null;
    	}
}
