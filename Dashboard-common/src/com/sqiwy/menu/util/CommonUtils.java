package com.sqiwy.menu.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.sqiwy.dashboard.BuildConfig;
import com.sqiwy.dashboard.R;
import com.sqiwy.menu.MenuApplication;
import java.util.regex.Pattern;

/**
 * Created by abrysov
 */

public final class CommonUtils {
    private static final String TAG = CommonUtils.class.getSimpleName();
    private static Pattern PATTERN = Pattern.compile("[\\d\\w&&[^_]]*_");
    
    private CommonUtils() {
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isAppInstalled(String packageName) {
        try {
            MenuApplication.getContext().getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void runOrInstallApp(String packageName) {
        Context context = MenuApplication.getContext();
        if (isAppInstalled(packageName)) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            try {
                Intent game = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
                game.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(game);
//                context.startActivity(new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("market://details?id=" + packageName)));
            } catch (ActivityNotFoundException e) {
                // Google play is not installed, open a browser.
//                context.startActivity(new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("http://play.google.com/store/apps/details?id=" + packageName))
//                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

                Intent game = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName));
                game.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(game);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static String getDefaultBrowserPackage() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com"));
        ResolveInfo ri = MenuApplication.getContext().getPackageManager().resolveActivity(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        String packageName = ri != null ? ri.activityInfo.packageName : null;
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "Default browser package is " + packageName);
        }
        return packageName;
    }
    
    public static String extractMenuProductImageName(String imageUrl) {
    	String result = null;
    	
    	if (!TextUtils.isEmpty(imageUrl)) {
	    	// Parsing can be potientially slow
	    	String name = Uri.parse(imageUrl).getLastPathSegment();
	    	
	    	if (!TextUtils.isEmpty(name)) {
	    		result = PATTERN.matcher(name).replaceFirst("");
	    	}
    	}
    	return result;
    }

    public static String formatPrice(float price) {
    	String r = MenuApplication.getContext().getResources().getString(R.string._small_r);
        return String.format("%.0f", price) + r;
    }
    
}
