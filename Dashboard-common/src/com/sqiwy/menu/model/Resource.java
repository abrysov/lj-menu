package com.sqiwy.menu.model;

/**
 * Created by abrysov
 */

public class Resource {
	private static final String TAG = "Resource";
	
	protected int mId;
    protected String mBigImgUrl;
    protected String mSmallImgUrl;
    protected String mInfo;

    public Resource() {
    }

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public String getBigImgUrl() {
		return mBigImgUrl;
	}

	public void setBigImgUrl(String bigImgUrl) {
		mBigImgUrl = bigImgUrl;
	}

	public String getSmallImgUrl() {
		return mSmallImgUrl;
	}

	public void setSmallImgUrl(String smallImgUrl) {
		mSmallImgUrl = smallImgUrl;
	}

	public String getInfo() {
		return mInfo;
	}

	public void setInfo(String info) {
		mInfo = info;
	}
    
    
}
