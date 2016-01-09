package com.sqiwy.menu.view;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.util.AttributeSet;

/**
 * Created by abrysov
 */

public class PriceTextView extends TypefaceTextView {
    private static CustomTypefaceSpan sCustomTypefaceSpan;

	public PriceTextView(Context context) {
		this(context, null);
	}

	public PriceTextView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.textViewStyle);
	}

	public PriceTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(isInEditMode() ? text : getPriceText(getContext(), text), type);
    }

    @SuppressWarnings("ConstantConditions")
    public static CharSequence getPriceText(Context context, CharSequence text) {
        if (sCustomTypefaceSpan == null) {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/rouble.otf");
            sCustomTypefaceSpan = new CustomTypefaceSpan(typeface);
        }
        SpannableString priceText = new SpannableString(text + " i");
        int length = priceText.length();
        priceText.setSpan(sCustomTypefaceSpan, length - 1, length, 0);
        return priceText;
    }
}
