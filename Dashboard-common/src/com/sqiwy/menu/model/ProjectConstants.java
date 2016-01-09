package com.sqiwy.menu.model;

import android.content.Context;

/**
 * Created by abrysov
 */

public class ProjectConstants {
	private static final long ANIM_DURATION_BASE = 200;
	
	public static long getAnimDuration(Context context) {
		//context.getSharedPreferences(name, mode);
		return ANIM_DURATION_BASE;
	}
}
