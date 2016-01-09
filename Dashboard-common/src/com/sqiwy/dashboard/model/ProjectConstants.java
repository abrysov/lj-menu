package com.sqiwy.dashboard.model;

import android.os.Environment;

/**
 * Created by abrysov
 */

public class ProjectConstants {
	public static final String PROJECT_BASE_PATH = "/SQIWY/";
	public static final String PRODUCTS = "PRODUCTS/";
	public static final String DASHBOARD = "DASHBOARD/";
	public static final String META = "META/";

	public static final String[] createOnStart = new String[] {
			Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_MOVIES).getAbsolutePath()
					+ PROJECT_BASE_PATH + DASHBOARD,
			Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_PICTURES).getAbsolutePath()
					+ PROJECT_BASE_PATH,

			Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
					+ PROJECT_BASE_PATH,
			Environment.getExternalStorageDirectory().getAbsolutePath()
					+ PROJECT_BASE_PATH };
	
	public static String getPathToDBMovies(){
		return Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_MOVIES).getAbsolutePath()
				+ PROJECT_BASE_PATH + DASHBOARD;
	}
	
	
	public static final String EXTRA_COMMERCIAL_NAME = "EXTRA_COMMERCIAL_NAME";
	public static final String EXTRA_COMMERCIAL_TIME_SHOW = "EXTRA_TIME_SHOW";
	public static final String EXTRA_COMMECIAL_IS_CLICKED = "EXTRA_IS_CLICKED";
	public static final String ACTION_NAME = "com.sqiwy.dashboard.dbcommercialbroadcastreceiver";
	public static final String GALLERY_CLOCK_PATH = "/Android/data/com.sqiwy.dashboard/ClockGallery";
	
}
