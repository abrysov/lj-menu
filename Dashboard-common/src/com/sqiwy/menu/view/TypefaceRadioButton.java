package com.sqiwy.menu.view;

import java.util.HashMap;
import java.util.Map;

import com.sqiwy.dashboard.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * Created by abrysov
 */

public class TypefaceRadioButton extends RadioButton {
	private static final Map<String, Typeface> sTypefaces = new HashMap<String, Typeface>();

	public TypefaceRadioButton(Context context) {
		this(context,null);
	}

	public TypefaceRadioButton(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.radioButtonStyle);
	}

	public TypefaceRadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (isInEditMode()) {
            return;
		}
        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefaceRadioButton);
        String customTypeface = styledAttrs.getString(R.styleable.TypefaceRadioButton_customTypeface);
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
