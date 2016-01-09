package com.sqiwy.menu.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.fosslabs.android.utils.Funs;
import com.sqiwy.restaurant.api.BackendException;
import com.sqiwy.dashboard.StartActivity;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.util.SystemControllerHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by abrysov
 */

public class UpdateService extends IntentService {
    private static final String TAG = UpdateService.class.getSimpleName();
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 30000;

    // TODO: Replace with actual values.
    private final String LAST_VERSION_URL;
    private final String APK_URL;
    public static final String UPDATE_APPLICATION = "UPDATE_APPLICATION";

    public UpdateService() throws Throwable {
        super("UpdateService");

        String baseURL = ResourcesManager.getBaseResourcesUrl();
        LAST_VERSION_URL = baseURL + "last_version.txt";
        APK_URL = baseURL + "sqiwy-table.apk";
    }

    public static void start() {
        Context context = MenuApplication.getContext();
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra(UPDATE_APPLICATION, true);
        context.startService(intent);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onHandleIntent(Intent intent) {
        // If the app update is available, download and install it. Otherwise, restart the app.
        boolean isUpdate = intent.getExtras().getBoolean(UPDATE_APPLICATION, true);

        if (isUpdate && isUpdateAvailable()) {
            String apkPath = downloadApk();
            if (apkPath != null) {
                SystemControllerHelper.installPackage(Uri.fromFile(new File(apkPath)));
            }
        } else {
			try {
				MenuApplication.getOperationService().closeTableSession();
			} catch (BackendException e) {
				Log.e(TAG,"Close table session error before SOFT RESET",e);
			} catch (IOException e) {
				Log.e(TAG,"Close table session error before SOFT RESET",e);
			}

			Context appContext=MenuApplication.getContext();
			
			Intent appStartIntent=new Intent(appContext,StartActivity.class);
        	int pendingIntentId=654321;
        	PendingIntent pi=PendingIntent.getActivity(appContext, pendingIntentId, appStartIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        	AlarmManager aMgr=(AlarmManager)appContext.getSystemService(Context.ALARM_SERVICE);
        	
        	aMgr.set(AlarmManager.RTC, System.currentTimeMillis()+5000, pi);
        	android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private boolean isUpdateAvailable() {
        if (Funs.isNetworkAvailable(this)) {
            int versionCode = getVersionCode();
            if (versionCode >= 0) {
                try {
                    int lastVersionCode = Integer.parseInt(IOUtils.toString(new URL(LAST_VERSION_URL)));
                    return lastVersionCode > versionCode;
                } catch (IOException e) {
                    Log.w(TAG, "Failed to get the version code from " + LAST_VERSION_URL, e);
                } catch (NumberFormatException ex) {
                	return false;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    private int getVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Failed to get the version code from the package", e);
            return -1;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private String downloadApk() {
        if (Funs.isNetworkAvailable(this)
                && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String filePath = getExternalFilesDir(null).getPath() + "/update.apk";
            try {
                FileUtils.copyURLToFile(new URL(APK_URL), new File(filePath), CONNECTION_TIMEOUT,
                        READ_TIMEOUT);
                return filePath;
            } catch (IOException e) {
                Log.w(TAG, "Failed to download apk from " + APK_URL, e);
            }
        }
        return null;
    }
}
