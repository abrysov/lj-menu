package com.fosslabs.android.model;

import com.fosslabs.android.jsbase.R;
import com.fosslabs.android.utils.JSImageWorker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

public class BitmapHolder {
	private static BitmapHolder mBitmapHolder;
	private Context mContext;
	private Bitmap mBitmap;
	
	public BitmapHolder(Context context){
		mContext = context;
		mBitmap = JSImageWorker.addShadowLine(mContext, R.drawable.menu_category_bg, 4, Color.BLACK);
	}
		
	public static BitmapHolder get(Context c) {
		if (mBitmapHolder == null) {
            mBitmapHolder = new BitmapHolder(c.getApplicationContext());
        }
        return mBitmapHolder;
	}
	
	public Bitmap getBitmap() {
        return mBitmap;
    }
}
