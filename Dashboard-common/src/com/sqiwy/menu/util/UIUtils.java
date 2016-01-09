package com.sqiwy.menu.util;

/**
 * Created by abrysov
 */

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Point;
import android.provider.Settings;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public final class UIUtils {
    private UIUtils() {}


    public static void setViewTemporarilyUnclickable(View view) {
        setViewTemporarilyUnclickable(view, 1000);
    }

    public static void setViewTemporarilyUnclickable(View view, long delayMillis) {
        final WeakReference<View> viewRef = new WeakReference<View>(view);
        view.setClickable(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                View view = viewRef.get();
                if (view != null) {
                    view.setClickable(true);
                }
            }
        }, delayMillis);
    }
    
    public static void toggleOrientation(Context context) {
    	setLandscapeOrientation(context, true);
	}
    
    public static void setLandscapeOrientation(Context context, boolean performReverse) {
    	
    	ContentResolver contentResolver = context.getContentResolver();
    	// Whether accelerometer rotation is enabled
		final int accelerometerRotation = Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, 1);
    	
		// Get current rotation and screen size
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		int screenRotation = display.getRotation();
		Point outSize = new Point();
		display.getSize(outSize);
		
		// Determine what would be "landscape"(wide screen) rotation in degrees for this device
		// (under landscape we mean width > height)
		final int wideScreenRotationDegrees;
		if (outSize.x < outSize.y) {
			wideScreenRotationDegrees = surfaceRotationToDegrees(screenRotation) + 90;
		} else {
			wideScreenRotationDegrees = surfaceRotationToDegrees(screenRotation);
		}
		
		// Get current user rotation (rotation which is used when accelerometer rotation is disabled
		// and application doesn't use custom orientation settings)
		int userRotation = Settings.System.getInt(contentResolver, Settings.System.USER_ROTATION,
				degreesToSurfaceRotation(wideScreenRotationDegrees));
    	int userRotationDegrees = surfaceRotationToDegrees(userRotation);
		
    	// Fix current user rotation (it should be "landscape" wide screen
		if (wideScreenRotationDegrees % 180 != userRotationDegrees % 180) {
			userRotationDegrees += 90;
		}
		
		if (performReverse) {
			// Reverse current orientation
			userRotationDegrees += 180;
		}
    	
    	Settings.System.putInt(contentResolver, Settings.System.USER_ROTATION,
    			degreesToSurfaceRotation(userRotationDegrees));
    	
    	// Disable orientation
    	if (1 == accelerometerRotation) {
			Settings.System.putInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0);
		}
    	/*} else {
    		boolean reverse;
    		if (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE == activity.getRequestedOrientation()) {
    			reverse = true;
    		} else {
    			reverse = false;
    		}
    		Funs.setRequestedOrientation(activity, reverse);
    	}*/
    	
	}
    
    private static int surfaceRotationToDegrees(int surfaceRotation) {
    	switch (surfaceRotation) {
    	case Surface.ROTATION_0:
    		return 0;
    	case Surface.ROTATION_90:
    		return 90;
    	case Surface.ROTATION_180:
    		return 180;
    	case Surface.ROTATION_270:
    		return 270;
    	default:
    		return 0;
    	}
    }
    
    private static int degreesToSurfaceRotation(int degrees) {
    	switch (degrees % 360) {
    	case 0:
    		return Surface.ROTATION_0;
    	case 90:
    		return Surface.ROTATION_90;
    	case 180:
    		return Surface.ROTATION_180;
    	case 270:
    		return Surface.ROTATION_270;
    	default:
    		return Surface.ROTATION_0;
    	}
    }
    
    public static void hideTitleBar(Activity activity){
		// Hide the window title.
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Let's display the progress in the activity title bar, like the
		// browser app does.				
		activity.getWindow().requestFeature(Window.FEATURE_PROGRESS);
		// Hide the status bar and other OS-level chrome
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
}
