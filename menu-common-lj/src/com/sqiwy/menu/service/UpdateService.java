package com.sqiwy.menu.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.fosslabs.android.utils.Funs;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.util.MenuControllerHelper;

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
    private static final String LAST_VERSION_URL = "https://docs.google.com/uc?export=download&id=0B7Aa09LIaH-NQVU3ckkxblVKc3M";
    private static final String APK_URL = "https://docs.google.com/uc?export=download&id=0B7Aa09LIaH-NYzlqR0h5enMweEE";

    public UpdateService() {
        super("UpdateService");
    }

    public static void start() {
        Context context = MenuApplication.getContext();
        Intent intent = new Intent(context, UpdateService.class);
        context.startService(intent);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onHandleIntent(Intent intent) {
        // If the app update is available, download and install it. Otherwise, restart the app.
        if (isUpdateAvailable()) {
            String apkPath = downloadApk();
            if (apkPath != null) {
                MenuControllerHelper.installPackage(Uri.fromFile(new File(apkPath)));
            }
        } else {
            Intent restartIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(restartIntent);
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
