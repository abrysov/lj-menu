package com.sqiwy.dashboard.model.map;

/**
 * Created by abrysov
 */

import android.annotation.SuppressLint;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

public class Floor {

	@SerializedName("map")
	protected String mMapImage = null;
	
	@SerializedName("descr")
	protected String mDescription = null;
	
	/**
	 * 
	 */
	public Floor() {
		
		mMapImage = null;
		mDescription = null;
	}
	
	/**
	 * 
	 * @param mapImage
	 * @param description
	 */
	public Floor(String mapImage, String description) {
		
		mMapImage = mapImage;
		mDescription = description;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMapImage() {
		
		return mMapImage;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		
		return mDescription;
	}
	
	/**
	 * 
	 */
	@SuppressLint("DefaultLocale")
	@Override
	public String toString() {
		
		return String.format("Floor(map = '%s', descr = '%s')", 
				mMapImage, mDescription);
	}
	
	/**
	 * 
	 */
	@Override
	public boolean equals(Object other) {

		if( (null == other) ||
			(getClass() != other.getClass()) ) {
				
		    return false;  
		}  

		Floor otherFloor = (Floor) other;  
		        
		return ( (Objects.equal(this.mMapImage, otherFloor.mMapImage)) &&
				 (Objects.equal(this.mDescription, otherFloor.mDescription)));
	}
}
