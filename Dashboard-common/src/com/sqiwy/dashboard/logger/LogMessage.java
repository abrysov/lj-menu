package com.sqiwy.dashboard.logger;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import com.google.gson.annotations.SerializedName;

/**
 * Created by abrysov
 */

public final class LogMessage implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2035203063799175349L;

	/**
	 *
	 */
	public static enum Status {

		SUCCESS,
		FAILED
	}
	
	/**
	 *
	 */
	public static enum Stage {

		DOWNLOAD,
		UPDATE,
		// api call to backend - getMenu
		GET_MENU,
		LOAD_RESOURCES, // resources.json  - list of resources for downloading
		LOAD_DASHBOARD,  // assets
        LOAD_ADVERTISEMENT, // adv.company description + banner pictures
        LOAD_VIDEO,  // all video files
        LOAD_MENU_PICTURES, // pictures of products
        LOAD_MAP,  // map of shop mall
        OPEN_SESSION,
        CREATE_ORDER,
        CLOSE_SESSION,
        RESTART_SESSION
	}
	
	/**
	 * 
	 */
	@SerializedName("date")
	private final long mDate;
	
	@SerializedName("message")
	private final String mMessage;
	
	@SerializedName("status")
	private final Status mStatus;

	@SerializedName("stage")
	private final Stage mStage;
	
	/**
	 * 
	 * @param date
	 * @param message
	 */
	public LogMessage(long date, Stage stage, Status status, String message) {
		
		mDate = date;
		mMessage = (null != message) ? message : "<null>";
		mStatus = status;
		mStage = stage;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMessage() {
		
		return mMessage;
	}
	
	/**
	 * 
	 * @return
	 */
	public Stage getStage() {
		
		return mStage;
	}
	
	/**
	 * 
	 * @return
	 */
	public Status getStatus() {
		
		return mStatus;
	}
	
	/**
	 * 
	 */
	@SuppressLint("SimpleDateFormat")
	@Override
	public String toString() {

		return String.format("LogMessage(Date = '%s', Stage = '%s', Status = '%s', Message = '%s')", 
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(mDate)),
				(null != mStage) ? mStage.name() : "<stage not set>",
				(null != mStatus) ? mStatus.name() : "<status not set>",
				mMessage);
	}
}
