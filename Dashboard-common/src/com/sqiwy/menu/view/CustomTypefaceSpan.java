package com.sqiwy.menu.view;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

/**
 * Created by abrysov
 */

public class CustomTypefaceSpan extends TypefaceSpan {
    private final Typeface mTypeface;

    public CustomTypefaceSpan(Typeface typeface) {
        super("serif");
        mTypeface = typeface;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(ds, mTypeface);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, mTypeface);
    }

    private static void applyCustomTypeFace(Paint paint, Typeface typeface) {
        Typeface oldTypeface = paint.getTypeface();
        int oldStyle = oldTypeface != null ? oldTypeface.getStyle() : 0;
        int fake = oldStyle & ~typeface.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }
        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }
        paint.setTypeface(typeface);
    }
}
