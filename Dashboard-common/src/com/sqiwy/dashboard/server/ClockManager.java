package com.sqiwy.dashboard.server;

import android.content.Context;
import android.os.Environment;
import com.fosslabs.android.utils.JSLog;
import com.fosslabs.android.utils.JSTimeWorker;
import com.sqiwy.dashboard.model.ProjectConstants;
import org.joda.time.DateTime;

import java.io.File;

/**
 * Created by abrysov
 */

public class ClockManager {
	
	private static final String TAG = "ClockManager";

	public static String[] getTownNames(Context context, String[] townIds) {
		int length = townIds.length;
		String[] townNames = new String[length];
		for (int i = 0; i < length; i++) {
			townNames[i] = JSTimeWorker.getCity(context, townIds[i]);
		}
		return townNames;
	}

	public static int[] getTownDifferences(Context context, String[] townIds) {
		int length = townIds.length;
		int[] townDifferences = new int[length];
		int currentTime = DateTime.now().getHourOfDay();
		JSLog.d(TAG, "current time = " + currentTime);
		for (int i = 0; i < length; i++) {
			townDifferences[i] = JSTimeWorker.timeOfTown(townIds[i]).getHourOfDay() - currentTime;
			JSLog.d(TAG, "JSTimeWorker.timeOfTown = " + JSTimeWorker.timeOfTown(townIds[i]).getHourOfDay());
		}
		return townDifferences;
	}
	
	public static boolean isInitialized(){
		if(new File(Environment.getExternalStorageDirectory() + ProjectConstants.GALLERY_CLOCK_PATH + "/clock_gallery.txt").exists()){
			JSLog.d(TAG, "file exists");
			return true;
		} else {
			JSLog.d(TAG, "file not exists");
		}
		return false;
	}

}
