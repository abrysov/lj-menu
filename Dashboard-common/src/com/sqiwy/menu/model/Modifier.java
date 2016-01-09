package com.sqiwy.menu.model;

import android.database.Cursor;

import com.sqiwy.dashboard.util.ProductLoader.ModifiersQuery;

/**
 * Created by abrysov
 */

public class Modifier {
	private static final String TAG ="Modifier";
	
	protected int mId;
    protected int mModifierGroupId;
    protected String mName;
    protected float mPrice;
    protected String mImgUrl;
    protected String mWeight;
    protected int mSortIndex;
    protected String mDescription;
    protected boolean mEnabled;
    
    protected boolean mIsChecked = false;

    public Modifier() {
    }

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public int getModifierGroupId() {
		return mModifierGroupId;
	}

	public void setModifierGroupId(int modifierGroupId) {
		mModifierGroupId = modifierGroupId;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public float getPrice() {
		return mPrice;
	}

	public void setPrice(float price) {
		mPrice = price;
	}

	public String getImgUrl() {
		return mImgUrl;
	}

	public void setImgUrl(String imgUrl) {
		mImgUrl = imgUrl;
	}

	public String getWeight() {
		return mWeight;
	}

	public void setWeight(String weight) {
		mWeight = weight;
	}

	public int getSortIndex() {
		return mSortIndex;
	}

	public void setSortIndex(int sortIndex) {
		mSortIndex = sortIndex;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
	}

	public boolean isEnabled() {
		return mEnabled;
	}

	public void setEnabled(boolean enabled) {
		mEnabled = enabled;
	}

	public boolean isChecked() {
		return mIsChecked;
	}

	public void setChecked(boolean isChecked) {
		this.mIsChecked = isChecked;
	}

    public static Modifier fromCursor(Cursor cursor) {
        Modifier modifier = new Modifier();
        modifier.mEnabled = true;
        modifier.mId = cursor.getInt(ModifiersQuery.SERVER_ID);
        modifier.mModifierGroupId = cursor.getInt(ModifiersQuery.MODIFIER_GROUP_ID);
        modifier.mName = cursor.getString(ModifiersQuery.NAME);
        modifier.mPrice = cursor.getFloat(ModifiersQuery.PRICE);
        modifier.mSortIndex = cursor.getInt(ModifiersQuery.SORT_INDEX);
        modifier.mImgUrl = cursor.getString(ModifiersQuery.IMG_URL);
        modifier.mWeight = cursor.getString(ModifiersQuery.WEIGHT);
        return modifier;
    }
}
