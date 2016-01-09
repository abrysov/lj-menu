package com.sqiwy.dashboard.util;

import android.content.Context;
import android.content.Intent;

import android.util.Log;
import com.sqiwy.dashboard.*;
import com.sqiwy.dashboard.server.ClockManager;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.util.PreferencesUtils;

/**
 * Created by abrysov
 */

public class FlowUtils {

    private static final String TAG = FlowUtils.class.getName();

    public static void continueSetupFlow(Context context) {

	    if (MenuApplication.mIsAppStarted) {

            Intent intent = new Intent(context, DBStartActivity.class);
            context.startActivity(intent);

        }else{

            if (!PreferencesUtils.isBackendConfigCompleted(context) || MenuApplication.getOperationService() == null) {

                // Configure or re-configure the backend api
                Intent intent = new Intent(context, PreferencesActivity.class);
                context.startActivity(intent);

            } else if (!ResourcesManager.areResourcesLoaded(context)) {

                // Initial data has not been loaded successfully.
                // Launch activity to load data.
                Intent intent = LoadResourcesActivity.getLaunchIntent(context, true);
                context.startActivity(intent);

            } else if (MenuApplication.mIsCheckResources) {

                Intent intent = LoadResourcesActivity.getLaunchIntent(context, false);
                context.startActivity(intent);

            } else if (!ClockManager.isInitialized()) {

                Intent intent = new Intent(context, DBInitializeGalleryClock.class);
                context.startActivity(intent);

            } else {

                // Everything is initialized, just display ads
                MenuApplication.mIsAppStarted = true;

                Log.v(TAG, "Display initial Ads Screen");
                Intent intent = new Intent(context, DBMediaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        }
	}
}
