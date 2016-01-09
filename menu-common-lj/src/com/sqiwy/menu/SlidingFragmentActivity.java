package com.sqiwy.menu;

import android.support.v4.app.FragmentActivity;

/**
 * Created by abrysov
 * Stub class to get rid of SlidingMenu library.
 */
public class SlidingFragmentActivity extends FragmentActivity {

	public static class SlidingMenu {
		
		public static final int TOP = 0;
		
		public void setBehindScrollScale(float doNothing) {
			// Do nothing
		}
		
		public void setMode(int doNothing) {
			// Do nothing
		}
	}
	
	public SlidingMenu getSlidingMenu() {
		return new SlidingMenu();
	}
	
	public void setBehindContentView(int doNothing) {
		// Do nothing
	}
	
	public void toggle() {
		// Do nothing
	}
	
}
