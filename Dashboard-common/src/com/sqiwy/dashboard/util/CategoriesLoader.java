package com.sqiwy.dashboard.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.sqiwy.menu.model.Category;
import com.sqiwy.menu.model.ProductCategory;
import com.sqiwy.menu.model.TopProductCategory;
import com.sqiwy.menu.provider.DBHelper;
import com.sqiwy.menu.provider.MenuProvider;
import com.sqiwy.menu.server.DBHelperProductCategory;

/**
 * Created by abrysov
 * Custom loader to load categories and sub-categories.
 */
public class CategoriesLoader extends BaseLoader<List<TopProductCategory>> {

	public CategoriesLoader(Context context) {
		super(context);
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		getContext().getContentResolver().registerContentObserver(MenuProvider.URI_PRODUCT_CATEGORY, true, getObserver());
	}
	
	@Override
	public List<TopProductCategory> loadInBackground() {
		
		final ContentResolver resolver = getContext().getContentResolver();
		
		List<TopProductCategory> topCategories = new ArrayList<TopProductCategory>();

		// Get cursor.
		Cursor topCategoryCursor = resolver.query(MenuProvider.URI_PRODUCT_CATEGORY, null, 
					"coalesce(" + DBHelper.F_PARENT_SERVER_ID+ ",'')='' AND " + DBHelper.F_ENABLED + "= 1", 
					null, DBHelper.F_SORT_INDEX);
		
		
		Category category = null;
		while (topCategoryCursor.moveToNext()) {
		
			category = DBHelperProductCategory.getCategory(topCategoryCursor);
			if (null == category) continue;
			
			TopProductCategory topCategory = new TopProductCategory(category);
			
			int id = topCategory.getId();
		
			ArrayList<ProductCategory> subCategories = new ArrayList<ProductCategory>();
			
			// Get cursor.
			Cursor subCategoryCursor = resolver.query(MenuProvider.URI_PRODUCT_CATEGORY, null, 
					DBHelper.F_PARENT_SERVER_ID+ "=" + id + " AND " + DBHelper.F_ENABLED + "= 1",  
					null, DBHelper.F_SORT_INDEX);

			while (subCategoryCursor.moveToNext()) {
				category = DBHelperProductCategory.getCategory(subCategoryCursor);
				if (null == category) continue;
				
				ProductCategory subCategory = new ProductCategory(category);
				subCategories.add(subCategory);
			}
			// Close cursor.
			subCategoryCursor.close();
			
			if (!subCategories.isEmpty()) {
				topCategory.setList(subCategories);
			}
			
			topCategories.add(topCategory);
		}
		// Close cursor.
		topCategoryCursor.close();
		
		return topCategories;
	}

}
