package com.fosslabs.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class JSNoFlingGallery extends JSGallery {
    public JSNoFlingGallery(Context context) {
        super(context);
    }

    public JSNoFlingGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JSNoFlingGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }
}
