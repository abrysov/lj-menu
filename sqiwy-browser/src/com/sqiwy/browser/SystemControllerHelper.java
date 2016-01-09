package com.sqiwy.browser;

import android.content.Context;
import android.content.Intent;

/**
 * Created by abrysov
 */

public final class SystemControllerHelper {
    private static final String EXTRA_SYSTEM_UI_MODE = "com.sqiwy.controller.extra.SYSTEM_UI_MODE";
    private static final String ACTION_SET_SYSTEM_UI_MODE = "com.sqiwy.controller.action.SET_SYSTEM_UI_MODE";

    public static final int SYSTEM_UI_MODE_DISABLE_ALL = 0;
    public static final int SYSTEM_UI_MODE_ENABLE_ALL = 1;
    public static final int SYSTEM_UI_MODE_ENABLE_NAVIGATION = 2;
    
    public static void setSystemUiMode(Context context, int mode) {
        Intent intent = new Intent(ACTION_SET_SYSTEM_UI_MODE);
        intent.putExtra(EXTRA_SYSTEM_UI_MODE, mode);
        context.startService(intent);
    }
}