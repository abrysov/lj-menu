package com.fosslabs.android.view;

import android.view.MotionEvent;
import android.view.View;

import com.fosslabs.android.utils.JSImageWorker;

public class ShadowTouchListener implements View.OnTouchListener{

	@Override
	public boolean onTouch(View v, MotionEvent event) {	
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			JSImageWorker.setColorFilterBackGround(v, -1, null);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
        	JSImageWorker.setNullColorFilterBackGround(v);
            return true;
        }
		 return false;
	}

}
