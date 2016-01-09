package com.sqiwy.menu.model;

/**
 * Created by abrysov
 */

public class Category {
	private static final String TAG = "Category";
	
	protected int mId;
    protected Integer mParentId;
	protected boolean mEnabled;
	protected String mName;
    protected int mSortIndex;
    protected String mImgUrl;
    protected boolean mSimpleList;
   
	public Category(){
	}
	
	public Category(Category category) {
		super();
		mId = category.mId;
		mParentId = category.mParentId;
		mEnabled = category.mEnabled;
		mName = category.mName;
		mSortIndex = category.mSortIndex;
		mImgUrl = category.mImgUrl;
		mSimpleList = category.mSimpleList;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public Integer getParentId() {
		return mParentId;
	}

	public void setParentId(Integer parentId) {
		mParentId = parentId;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public int getSortIndex() {
		return mSortIndex;
	}

	public void setSortIndex(int sortIndex) {
		mSortIndex = sortIndex;
	}

	public String getImgUrl() {
		return mImgUrl;
	}

	public void setImgUrl(String imgUrl) {
		mImgUrl = imgUrl;
	}

	public boolean isSimpleList() {
		return mSimpleList;
	}

	public void setSimpleList(boolean simpleList) {
		mSimpleList = simpleList;
	}

	public boolean isEnabled() {
		return mEnabled;
	}

	public void setEnabled(boolean enabled) {
		mEnabled = enabled;
	}
	
	
}
