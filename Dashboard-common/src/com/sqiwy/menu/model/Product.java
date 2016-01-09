package com.sqiwy.menu.model;

import android.database.Cursor;

import com.sqiwy.dashboard.util.ProductLoader.ProductQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abrysov
 */

public class Product extends WeightItem {
    protected int mId;
    protected int mIdProductCategory;
    protected String mName;
    protected int mSortIndex;
    protected String mImgUrl;
    protected boolean mEnabled;
    protected float mPrice;
    protected String mDishWeight;
    protected String mCaloricity;
    protected String mType;
    protected String mFullname;
    protected String mDesc;
    protected int mSignificance;
    protected int mCount = 1;
    protected List<ModifierGroup> mModifierGroups = new ArrayList<ModifierGroup>();
		
	public Product() {
		super("", 1);
	}
	
	public Product(String name, String desc,float price){
		super(name, 1);
		mName = name;
		mDesc = desc;
		mPrice = price;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public int getIdProductCategory() {
		return mIdProductCategory;
	}

	public void setIdProductCategory(int idProductCategory) {
		mIdProductCategory = idProductCategory;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getImgUrl() {
		return mImgUrl;
	}

	public float getPrice() {
		return mPrice;
	}
	
	/**
	 * Computes default price for menu considering modifiers.
	 * @return
	 */
	public float getDefaultPrice() {
		float result = mPrice;
		
		List<Modifier> modifiers = getDefaultModifiers();
		for (int i = modifiers.size() - 1; 0 <= i; i--) {
			result += modifiers.get(i).getPrice();
		}
		
		return result;
	}

	public void setPrice(float price) {
		mPrice = price;
	}

	public void setImgUrl(String imgUrl) {
		mImgUrl = imgUrl;
	}

	public int getSortIndex() {
		return mSortIndex;
	}

	public void setSortIndex(int sortIndex) {
		mSortIndex = sortIndex;
	}

	public String getDishWeight() {
		return mDishWeight;
	}

	public void setDishWeight(String dishWeight) {
		mDishWeight = dishWeight;
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		mType = type;
	}

	public String getFullname() {
		return mFullname;
	}

	public void setFullname(String fullname) {
		mFullname = fullname;
	}

	public String getDesc() {
		return mDesc;
	}

	public void setDesc(String desc) {
		mDesc = desc;
	}

	public int getSignificance() {
		return mSignificance;
	}

	public void setSignificance(int significance) {
		
		// TODO: remove me when new types of IMPORTANCE IN MENU will be implemented
		// that is fix for a bug https://bitbucket.org/sqiwy/lj-menu-demo/issue/59/
		// for an unsupported types of IMPORTANCE IN MENU we set importance to 1
		if(significance > 1) {
		
			significance = 1;
		}
		
		mSignificance = significance;
        setItemWeight(significance);
	}
	
	public void setCaloricity(String caloricity) {
		mCaloricity=caloricity;
	}
	
	public String getCaloricity() {
		return mCaloricity;
	}

	public boolean isEnabled() {
		return mEnabled;
	}

	public void setEnabled(boolean enabled) {
		mEnabled = enabled;
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int count) {
		mCount = count;
	}
	
	public void increaseCount(){
		mCount++;
	}
	
	public List<ModifierGroup> getModifierGroups() {
		return mModifierGroups;
	}

	public void setModifierGroups(List<ModifierGroup> modifierGroups) {
        mModifierGroups = modifierGroups != null ? modifierGroups : new ArrayList<ModifierGroup>();
	}

    public boolean hasModifiers() {
        return !mModifierGroups.isEmpty();
    }

    /**
     * Gets default modifiers.
     * @return default modifiers.
     */
    public List<Modifier> getDefaultModifiers() {
    	List<Modifier> defaultModifiers = new ArrayList<Modifier>();
    	
    	List<ModifierGroup> modifierGroups = getModifierGroups();
		if (null != modifierGroups) {
			
			final int count = modifierGroups.size();
			ModifierGroup modifierGroup;
			List<Modifier> modifiers;
			
			for (int i = 0; i < count; i++) {
				modifierGroup = modifierGroups.get(i);
				modifiers = modifierGroup.getModifiers();
				
				if ((modifierGroup.isOrType() || modifierGroup.isRequired())
						&& 0 < modifiers.size()) {
					
					// TODO: should we check sort index
					defaultModifiers.add(modifiers.get(0));
				}
			}
			
		}
		
		return defaultModifiers;
    }
    
    public boolean equals(Product product){
        return mId == product.getId();
    }

    @Override
    public String toString(){
        return mName + ":" + mDishWeight;
    }

    public static Product fromCursor(Cursor cursor) {
        Product product = new Product();
        product.mEnabled = true;
        product.mId = cursor.getInt(ProductQuery.SERVER_ID);
        product.mIdProductCategory = cursor.getInt(ProductQuery.PRODUCT_CATEGORY_ID);
        product.mName = cursor.getString(ProductQuery.NAME);
        product.mPrice = cursor.getFloat(ProductQuery.PRICE);
        product.mSortIndex = cursor.getInt(ProductQuery.SORT_INDEX);
        product.mImgUrl = cursor.getString(ProductQuery.IMG_URL);
        product.mDishWeight = cursor.getString(ProductQuery.WEIGHT);
        product.mType = cursor.getString(ProductQuery.TYPE);
        product.mFullname = cursor.getString(ProductQuery.FULL_NAME);
        product.mDesc = cursor.getString(ProductQuery.DESCRIPTION);
        product.mSignificance = cursor.getInt(ProductQuery.SIGNIFICANCE);
        product.mCaloricity=cursor.getString(ProductQuery.CALORICITY);
        return product;
    }
}
