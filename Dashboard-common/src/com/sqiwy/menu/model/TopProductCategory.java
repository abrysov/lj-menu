package com.sqiwy.menu.model;

import java.util.ArrayList;

/**
 * Created by abrysov
 */

public class TopProductCategory extends Category{
	
    protected ArrayList<ProductCategory> mList = new ArrayList<ProductCategory>();
    
    public TopProductCategory(Category category) {
    	super(category);
	}
    
	public ArrayList<ProductCategory> getList() {
		return mList;
	}

	public void setList(ArrayList<ProductCategory> list) {
		if(list == null) mList = new ArrayList<ProductCategory>();
		mList = list;
	}

}
