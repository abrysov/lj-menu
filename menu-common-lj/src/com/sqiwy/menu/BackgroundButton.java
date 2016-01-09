package com.sqiwy.menu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sqiwy.menu.R;

/**
 * Created by abrysov
 */

public class BackgroundButton extends ImageView {

    Drawable mCustomImage;

    public BackgroundButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCustomImage = context.getResources().getDrawable(R.drawable.bg_wood_repeat);

        setBackgroundColor(0x00000000);
    }

    @Override
    public void draw(Canvas canvas) {
        mCustomImage.setBounds(canvas.getClipBounds());
        mCustomImage.draw(canvas);

        super.draw(canvas);
    }

}
