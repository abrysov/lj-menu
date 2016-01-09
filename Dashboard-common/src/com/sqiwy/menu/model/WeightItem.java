package com.sqiwy.menu.model;

/**
 * Created by abrysov
 */

public class WeightItem implements Comparable<WeightItem>{
	String mName;
	private int mItemWeight;
	
	public WeightItem(String name, int weight) {
		mName = name;
		mItemWeight = weight;
	}

	public int getItemWeight() {
		return mItemWeight;
	}

	public void setItemWeight(int weight) {
		this.mItemWeight = weight;
	}

	@Override
	public int compareTo(WeightItem arg0) {
		if (arg0.getItemWeight() < this.mItemWeight) {
			return 1;
		}
		else if (arg0.getItemWeight() > this.mItemWeight) {
			return -1;
		}
		else 
			return 0;
		
	}
	
	public String toString(){
		return mName + ":" + mItemWeight;
	}

}
