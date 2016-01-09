package com.sqiwy.menu.server;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.fosslabs.android.utils.JSFileWorker;
import com.fosslabs.android.utils.JSLog;

/**
 * Created by abrysov
 */

public class StorageQuery {
	private static final String TAG = "StorageQuery";

	public static Drawable getLockWindowBG(Context context){
		//return ResourcesManager.getDrawableResource(context, "bg_window_lock.jpg", Category.DASHBOARD);
		return JSFileWorker.loadDrawableFromAsset(context, "bg_window_lock.jpg");
	}
}
