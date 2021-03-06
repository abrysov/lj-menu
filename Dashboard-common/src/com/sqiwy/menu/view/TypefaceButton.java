package com.sqiwy.menu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import com.sqiwy.dashboard.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abrysov
 */

public class TypefaceButton extends Button {
	/*
	 * Caches typefaces to prevent bug in pre-4.0 Android that doesn't free up TypeFaces properly. Described here:
	 * http://stackoverflow.com/questions/2376250/custom-fonts-and-xml-layouts-android#comment11263047_7197867 and here:
	 * http://code.google.com/p/android/issues/detail?id=9904
	 */
	private static final Map<String, Typeface> sTypefaces = new HashMap<String, Typeface>();

	public TypefaceButton(Context context) {
		this(context, null);
	}

	public TypefaceButton(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.buttonStyle);
	}

    @SuppressWarnings("ConstantConditions")
	public TypefaceButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (isInEditMode()) {
            return;
		}
        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefaceButton);
        String customTypeface = styledAttrs.getString(R.styleable.TypefaceButton_customTypeface);
        if (customTypeface != null) {
            Typeface typeface = sTypefaces.get(customTypeface);
            if (typeface == null) {
                typeface = Typeface.createFromAsset(context.getAssets(), customTypeface);
                sTypefaces.put(customTypeface, typeface);
            }
            setTypeface(typeface);
        }
        styledAttrs.recycle();
	}
}
