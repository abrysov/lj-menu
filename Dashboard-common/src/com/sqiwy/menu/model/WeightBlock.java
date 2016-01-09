package com.sqiwy.menu.model;

import java.util.List;

/**
 * Created by abrysov
 */

public class WeightBlock {
	private int mType;
	private List<WeightItem> mItems;

	public WeightBlock(int type, List<WeightItem> items) {
		mType = type;
		mItems = items;
	}

	public int getType() {
		return mType;  
	}

	public void setType(int type) {
		mType = type;
	}

	public List<WeightItem> getItems() {
		return mItems;
	}

	public void setItems(List<WeightItem> items) {
		mItems = items;
	}

    @Override
    public String toString() {
        return mType + ":" + mItems.toString();
    }
}
