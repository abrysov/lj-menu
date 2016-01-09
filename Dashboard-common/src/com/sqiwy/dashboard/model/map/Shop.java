package com.sqiwy.dashboard.model.map;

import java.util.Comparator;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

import android.annotation.SuppressLint;

/**
 * Created by abrysov
 */

public class Shop {

	@SerializedName("id")
	private int mId = -1;

	@SerializedName("name")
	private String mName = null;

	@SerializedName("floor")
	private int mFloor = -1;

	@SerializedName("logo")
	private String mLogo = null;

	@SerializedName("xyr")
	private int[] mCoordinates = new int[] { 0, 0, 0 };

	@SerializedName("descr")
	private String mDescription = null;

	/**
	 * 
	 */
	public Shop() {

		mId = -1;
		mName = null;
		mFloor = -1;
		mLogo = null;
		mCoordinates[0] = 0;
		mCoordinates[1] = 0;
		mCoordinates[2] = 0;
		mDescription = null;
	}

	/**
	 * 
	 * @param id
	 * @param name
	 * @param floor
	 * @param logo
	 * @param x
	 * @param y
	 * @param description
	 */
	public Shop(int id, String name, int floor, String logo, int x, int y,
			int snapDistance, String description) {

		mId = id;
		mName = name;
		mFloor = floor;
		mLogo = logo;
		mCoordinates[0] = x;
		mCoordinates[1] = y;
		mCoordinates[2] = snapDistance;
		mDescription = description;
	}

	/**
	 * 
	 * @return
	 */
	public int getId() {

		return mId;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {

		return mName;
	}

	/**
	 * 
	 * @return
	 */
	public int getFloor() {

		return mFloor;
	}

	/**
	 * 
	 * @return
	 */
	public String getLogo() {

		return mLogo;
	}

	/**
	 * 
	 * @return
	 */
	public int getX() {

		return mCoordinates[0];
	}

	/**
	 * 
	 * @return
	 */
	public int getY() {

		return mCoordinates[1];
	}

	/**
	 * 
	 * @return
	 */
	public int getSnapDistance() {

		return mCoordinates[2];
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

		return String
				.format("Shop(id = %d, name = '%s', floor = %d, logo = '%s', x = %d, y = %d, snap = %d, desription = '%s')",
						mId, mName, mFloor, mLogo, mCoordinates[0],
						mCoordinates[1], mCoordinates[2], mDescription);
	}

	/**
	 * 
	 */
	@Override
	public boolean equals(Object other) {

		if ((null == other) || (getClass() != other.getClass())) {

			return false;
		}

		Shop otherShop = (Shop) other;

		return ((mId == otherShop.mId)
				&& (Objects.equal(this.mName, otherShop.mName))
				&& (mFloor == otherShop.mFloor)
				&& (Objects.equal(this.mLogo, otherShop.mLogo))
				&& (mCoordinates[0] == otherShop.mCoordinates[0])
				&& (mCoordinates[1] == otherShop.mCoordinates[1])
				&& (mCoordinates[2] == otherShop.mCoordinates[2]) && (Objects
					.equal(this.mDescription, otherShop.mDescription)));
	}

	/**
	 *
	 */
	public static class ShopsAscendingNameComparator implements	Comparator<Shop> {

		@Override
		public int compare(Shop lhs, Shop rhs) {

			return lhs.getName().compareTo(rhs.getName());
		}
	}
}
