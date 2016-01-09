package com.sqiwy.menu.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by abrysov
 */

public class PreferencesUtils {
    public static final String BACKEND_CONFIG_COMPLETED = "back-end-completed";
    public static final String MENU_LOADED = "menu-loaded";
    public static final String CLIENT_ID = "client_id";
    public static final String PASSWORD = "password";

    public static final String API_URL = "api_url";
    public static final String WEB_URL = "web_url";

    public static final String HOST_NAME = "host_name";
    public static final String HOST_PORT = "host_port";
    public static final String MAX_PACKET_SIZE = "packet_size";
    public static final String POLLING_DELAY = "polling_delay";
    public static final String SOCKET_TIMEOUT = "socket_timeout";
    public static final String TABLE_PASSWORD = "table_password";
    public static final String DEMO_MODE = "pref_enable_demo_mode"; // must correspond with android:key in frag_pref_settings.xml

    public static boolean isBackendConfigCompleted(Context context) {
        return getPrefs(context).getBoolean(BACKEND_CONFIG_COMPLETED, false);
    }

    public static void setBackendConfigCompleted(Context context, boolean completed) {
        getPrefs(context).edit().putBoolean(BACKEND_CONFIG_COMPLETED, completed).commit();
    }

    public static void setMenuLoaded(Context context, boolean loaded) {
        getPrefs(context).edit().putBoolean(MENU_LOADED, loaded).commit();
    }

    public static boolean isMenuLoaded(Context context) {
        return getPrefs(context).getBoolean(MENU_LOADED, false);
    }

    public static String getTablePassword(Context context) {
        String mDefaultTablePassword = context.getResources().getString(com.sqiwy.dashboard.R.string.defaultTablePassword);

        return getPrefs(context).getString(TABLE_PASSWORD, mDefaultTablePassword);
    }

    public static void setTablePassword(Context context, String tablePassword) {
        getPrefs(context).edit().putString(TABLE_PASSWORD, tablePassword).commit();
    }

    private static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    /*
    public static void setDemoMode(Context context, boolean isInDemoMode) {
    	getPrefs(context).edit().putBoolean(DEMO_MODE, isInDemoMode).commit();
    }
    */

    public static boolean isApplicationInDemoMode(Context context) {
        return getPrefs(context).getBoolean(DEMO_MODE, false);
    }
}
