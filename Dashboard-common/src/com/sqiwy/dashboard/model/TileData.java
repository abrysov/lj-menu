package com.sqiwy.dashboard.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.sqiwy.dashboard.model.action.Action;
import com.sqiwy.dashboard.server.StorageQuery;

/**
 * Created by abrysov
 */

@SuppressWarnings("serial")
public class TileData extends ActionTile {
	
	private static final String TAG = "TileData";

	private String mUUID;
	private String mName;
	private String mColorLogoPath;
	private String mMonoLogoPath;
	private int mTextSize;
	private boolean mLandLayoutOrientation;
	private String mBackgroundPath;
	private int mType = 0;
	private int mIvMetrica = 0; // if(mLandLayoutOrientation) = width ? height
	
	private Drawable mColorLogo = null;
	private Drawable mMonoLogo = null;
	private Drawable mBackground = null;
	
	public static class TileDataType{
		public static final int TYPE_STATIC = 0;
		public static final int TYPE_STATIC_PRODUCT_GROUP = 1;
		public static final int TYPE_STATIC_PRODUCT = 2;
		public static final int TYPE_VIDEO = 3;
		public static final int TYPE_GALLERY_IMAGE = 4;
		public static final int TYPE_GALLERY_CLOCK = 5;
		public static final int TYPE_GALLERY_MAGASIN = 6;
		public static final int TYPE_GALLERY_WEB = 7;
	}
	
	public TileData(Action action) {
		super(action);
	}

	public Drawable getColorLogo(Context context){
		if(mColorLogo != null) return mColorLogo;		
		if(mColorLogoPath != null && ! mColorLogoPath.equalsIgnoreCase(""))
			return StorageQuery.getTileDataRes(context, mColorLogoPath);
		return null;
	}
	
	public Drawable getMonoLogo(Context context){
		if(mMonoLogo != null) return mMonoLogo;		
		if(mMonoLogoPath != null && !mMonoLogoPath.equalsIgnoreCase(""))
			return StorageQuery.getTileDataRes(context, mMonoLogoPath);
		return null;
	}
	
	public Drawable getBackground(Context context){
		if(mBackground != null) return mBackground;		
		if(mBackgroundPath != null && !mBackgroundPath.equalsIgnoreCase(""))
			return StorageQuery.getTileDataRes(context, mBackgroundPath);
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

	public String getColorLogoPath() {
		return mColorLogoPath;
	}

	public void setColorLogoPath(String colorLogoPath) {
		mColorLogoPath = colorLogoPath;
	}

	public String getMonoLogoPath() {
		return mMonoLogoPath;
	}

	public void setMonoLogoPath(String monoLogoPath) {
		mMonoLogoPath = monoLogoPath;
	}

	public int getTextSize() {
		return mTextSize;
	}

	public void setTextSize(int textSize) {
		mTextSize = textSize;
	}

	public boolean isLandLayoutOrientation() {
		return mLandLayoutOrientation;
	}

	public void setLandLayoutOrientation(boolean landLayoutOrientation) {
		mLandLayoutOrientation = landLayoutOrientation;
	}

	public int getType() {
		return mType;
	}

	public void setType(int type) {
		mType = type;
	}

	public String getBackgroundPath() {
		return mBackgroundPath;
	}

	public void setBackgroundPath(String mBackgroundPath) {
		this.mBackgroundPath = mBackgroundPath;
	}

	public int getIvMetrica() {
		return mIvMetrica;
	}

	public void setIvMetrica(int ivMetrica) {
		mIvMetrica = ivMetrica;
	}

	

	
}
