package com.sqiwy.menu.view;

import java.lang.reflect.Field;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;

/**
 * Created by abrysov
 */

public class FullScreenDrawerLayout extends DrawerLayout {

	public FullScreenDrawerLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public FullScreenDrawerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FullScreenDrawerLayout(Context context) {
		super(context);
		init();
	}
	
	/**
	 * Make DrawerLayout to take the whole screen.
	 */
	protected void init() {
		try {
			
			Field field = getClass().getSuperclass().getDeclaredField("mMinDrawerMargin");
			field.setAccessible(true);
			field.set(this, Integer.valueOf(0));
			
		} catch (Exception e) {
			throw new IllegalStateException("android.support.v4.widget.DrawerLayout has changed and you have to fix this class.", e);
		}
	}

}
