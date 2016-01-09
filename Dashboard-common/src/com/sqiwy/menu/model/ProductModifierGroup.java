package com.sqiwy.menu.model;

/**
 * Created by abrysov
 */

public class ProductModifierGroup {
	private static final String TAG = "ProductModifierGroup";
	
	protected int mId;
	protected int mProductId;
    protected int mModifierGroupId;
    protected int mSortIndex;
    protected boolean mRequired;

    public ProductModifierGroup() {
    	
    }

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public int getProductId() {
		return mProductId;
	}

	public void setProductId(int productId) {
		mProductId = productId;
	}

	public int getModifierGroupId() {
		return mModifierGroupId;
	}

	public void setModifierGroupId(int modifierGroupId) {
		mModifierGroupId = modifierGroupId;
	}

	public int getSortIndex() {
		return mSortIndex;
	}

	public void setSortIndex(int sortIndex) {
		mSortIndex = sortIndex;
	}

	public boolean isRequired() {
		return mRequired;
	}

	public void setRequired(boolean required) {
		mRequired = required;
	}
    
    
}
