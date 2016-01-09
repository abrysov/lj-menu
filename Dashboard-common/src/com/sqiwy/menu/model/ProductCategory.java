package com.sqiwy.menu.model;

import java.util.ArrayList;

/**
 * Created by abrysov
 */

public class ProductCategory extends Category {
	private static final String TAG = "ProductCategory";
	
	public ProductCategory(Category category) {
		super(category);
	}

	protected ArrayList<WeightBlock> mListProduct = new ArrayList<WeightBlock>();
	
	public ArrayList<WeightBlock> getListWeightBlock() {
		return mListProduct;
	}

	public void setListWeightBlock(ArrayList<WeightBlock> listProduct) {
		mListProduct = listProduct;
	}
}
