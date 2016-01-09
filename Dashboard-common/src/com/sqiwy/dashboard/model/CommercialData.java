package com.sqiwy.dashboard.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.sqiwy.dashboard.model.action.Action;
import com.sqiwy.dashboard.server.StorageQuery;

/**
 * Created by abrysov
 */

@SuppressWarnings("serial")
public class CommercialData extends ActionTile {

	private static final String TAG = "CommercialData";
	
	private String mUUID = null;
	private String mName = null;
	private String mBackgroundPath = null;
	private long mTimeShow = 0;
	private long mTimeStart;
	private boolean mIsClicked = false;
	
	public CommercialData(Action action) {
		super(action);
	}

	public Drawable getBackground(Context context){
		
		if(mBackgroundPath != null && mBackgroundPath != "") {
			return StorageQuery.getTileDataRes(context, mBackgroundPath);
		}
		
		return null;
	}
	
	public String getUUID() {
		return mUUID;
	}

	public void setUUID(String uUID) {
		mUUID = uUID;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public long getTimeShow() {
		return mTimeShow;
	}

	public void setTimeShow(long timeShow) {
		mTimeShow = timeShow;
	}

	public long getTimeStart() {
		return mTimeStart;
	}

	public void setTimeStart(long timeStart) {
		mTimeStart = timeStart;
	}

	
	
	public String getBackgroundPath() {
		return mBackgroundPath;
	}

	public void setBackgroundPath(String backgroundPath) {
		mBackgroundPath = backgroundPath;
	}

	private static final String DIVIDER = ":";
	@Override
	public String toString() {
		String s = getUUID() + DIVIDER + getName() + DIVIDER +  getBackgroundPath() + DIVIDER + getTimeShow();
		return s;
	}
	
	public boolean isClicked() {
		return mIsClicked;
	}

	public void setIsClicked(boolean isClicked) {
		mIsClicked = isClicked;
	}
}
