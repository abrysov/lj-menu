package com.sqiwy.menu.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by abrysov
 */

public class PreferencesUtils {
	public static final String BACKEND_CONFIG_COMPLETED = "back-end-completed";
	public static final String CLIENT_ID = "client_id";
	public static final String PASSWORD = "password";
	public static final String HOST_NAME = "host_name";
	public static final String HOST_PORT = "host_port";
	public static final String MAX_PACKET_SIZE = "packet_size";
	public static final String POLLING_DELAY = "polling_delay";
	public static final String SOCKET_TIMEOUT = "socket_timeout";
	
    public static boolean isBackendConfigCompleted(Context context) {
    	return getPrefs(context).getBoolean(BACKEND_CONFIG_COMPLETED, false);
    }
    
    public static void setBackendConfigCompleted(Context context, boolean completed) {
    	getPrefs(context).edit().putBoolean(BACKEND_CONFIG_COMPLETED, completed).commit();
    }
    
    private static SharedPreferences getPrefs(Context context) {
    	return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
