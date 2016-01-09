package com.sqiwy.menu.util;

import android.content.Intent;
import android.net.Uri;

import com.sqiwy.menu.MenuApplication;

import java.util.List;

/**
 * Created by abrysov
 */

public final class SystemControllerHelper {
    private static final String ACTION_CLEAR_APP_DATA = "com.sqiwy.controller.action.CLEAR_APP_DATA";
    private static final String ACTION_INSTALL_PACKAGE = "com.sqiwy.controller.action.INSTALL_PACKAGE";
    private static final String ACTION_SET_SYSTEM_UI_MODE = "com.sqiwy.controller.action.SET_SYSTEM_UI_MODE";
    private static final String ACTION_SET_CHROME_TO_DESKTOP_MODE = "com.sqiwy.controller.action.SET_CHROME_TO_DESKTOP_MODE";
    private static final String ACTION_REBOOT = "com.sqiwy.controller.action.REBOOT";
    private static final String ACTION_ENABLE_INSTALL_APPS = "com.sqiwy.controller.action.ENABLE_INSTALL_APPS";

    private static final String EXTRA_PACKAGE_NAMES = "com.sqiwy.controller.extra.PACKAGE_NAMES";
    private static final String EXTRA_PACKAGE_URI = "com.sqiwy.controller.extra.PACKAGE_URI";
    private static final String EXTRA_LAUNCH_APP = "com.sqiwy.controller.extra.LAUNCH_APP";
    private static final String EXTRA_SYSTEM_UI_MODE = "com.sqiwy.controller.extra.SYSTEM_UI_MODE";
    private static final String EXTRA_IS_APP_INSTALLATION_ENABLED = "com.sqiwy.controller.extra.IS_APP_INSTALLATION_ENABLED";

    public static final int SYSTEM_UI_MODE_DISABLE_ALL = 0;
    public static final int SYSTEM_UI_MODE_ENABLE_ALL = 1;
    public static final int SYSTEM_UI_MODE_ENABLE_NAVIGATION = 2;

    private SystemControllerHelper() {
    }

    public static void clearAppData(List<String> packageNames) {
        clearAppData(packageNames.toArray(new String[packageNames.size()]));
    }

    public static void clearAppData(String[] packageNames) {
        Intent intent = new Intent(ACTION_CLEAR_APP_DATA);
        intent.putExtra(EXTRA_PACKAGE_NAMES, packageNames);
        MenuApplication.getContext().startService(intent);
    }

    public static void installPackage(Uri packageUri) {
        installPackage(packageUri, true);
    }

    public static void installPackage(Uri packageUri, boolean launchApp) {
        Intent intent = new Intent(ACTION_INSTALL_PACKAGE);
        intent.putExtra(EXTRA_PACKAGE_URI, packageUri);
        intent.putExtra(EXTRA_LAUNCH_APP, launchApp);
        MenuApplication.getContext().startService(intent);
    }

    public static void setSystemUiMode(int mode) {
        Intent intent = new Intent(ACTION_SET_SYSTEM_UI_MODE);
        intent.putExtra(EXTRA_SYSTEM_UI_MODE, mode);
        MenuApplication.getContext().startService(intent);
    }
    
    public static void setChromeToDesktopMode() {
    	Intent intent = new Intent(ACTION_SET_CHROME_TO_DESKTOP_MODE);
    	MenuApplication.getContext().startService(intent);
    }
    
    public static void reboot() {
    	Intent intent = new Intent(ACTION_REBOOT);
    	MenuApplication.getContext().startService(intent);
    }
    
    public static void enableInstallApps(boolean enable) {
        Intent intent = new Intent(ACTION_ENABLE_INSTALL_APPS);
        intent.putExtra(EXTRA_IS_APP_INSTALLATION_ENABLED, enable);
        MenuApplication.getContext().startService(intent);
    }
}
