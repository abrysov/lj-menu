package com.sqiwy.menu.view;

import java.util.HashMap;
import java.util.Map;

import com.sqiwy.dashboard.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Created by abrysov
 */

public class TypefaceCheckbox extends CheckBox {
	private static final Map<String, Typeface> sTypefaces = new HashMap<String, Typeface>();
	
	public TypefaceCheckbox(Context context) {
		this(context,null);
	}

	public TypefaceCheckbox(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.checkboxStyle);
	}

	public TypefaceCheckbox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		if (isInEditMode()) {
            return;
		}
        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefaceCheckbox);
        String customTypeface = styledAttrs.getString(R.styleable.TypefaceCheckbox_customTypeface);
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
