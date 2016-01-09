package com.sqiwy.dashboard.model.map;

import java.util.Comparator;

import android.annotation.SuppressLint;

import com.google.common.base.Objects;

/**
 * Created by abrysov
 */

public class FloorEx extends Floor {

	/**
	 * 
	 */
	private int mFloor;
	
	/**
	 * 
	 * @param floorOrig
	 * @param floor
	 * @return
	 */
	public static FloorEx create(Floor floorOrig, int floor) {
		
		return new FloorEx(floorOrig.getMapImage(), floorOrig.getDescription(), floor);
	}
	
	/**
	 * 
	 * @param mapImage
	 * @param description
	 * @param floor
	 */
	public FloorEx(String mapImage, String description, int floor) {
		
		super(mapImage, description);
		
		mFloor = floor;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getFloor() {
		
		return mFloor;
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public String toString() {
		
		return String.format("FloorEx(floor = %d, map = '%s', descr = '%s')", 
				mFloor, mMapImage, mDescription);
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

		FloorEx otherFloor = (FloorEx) other;  
		        
		return ( (mFloor == otherFloor.mFloor) &&
				 (Objects.equal(this.mMapImage, otherFloor.mMapImage)) &&
				 (Objects.equal(this.mDescription, otherFloor.mDescription)));
	}
	
	/**
	 *
	 */
	public static class FloorAscendingComparator implements Comparator<FloorEx> {

		@Override
		public int compare(FloorEx lhs, FloorEx rhs) {
			
			if(lhs.mFloor < rhs.mFloor) {
				
				return -1;
			}
			
			if(lhs.mFloor > rhs.mFloor) {
				
				return 1;
			}

			return 0;
		}
	}
	
	/**
	 *
	 */
	public static class FloorDescendingComparator implements Comparator<FloorEx> {

		@Override
		public int compare(FloorEx lhs, FloorEx rhs) {
			
			if(lhs.mFloor > rhs.mFloor) {
				
				return -1;
			}
			
			if(lhs.mFloor < rhs.mFloor) {
				
				return 1;
			}

			return 0;
		}
	}
}
