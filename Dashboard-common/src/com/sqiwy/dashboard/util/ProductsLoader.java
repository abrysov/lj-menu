package com.sqiwy.dashboard.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.model.Product;
import com.sqiwy.menu.provider.DBHelper;
import com.sqiwy.menu.provider.MenuProvider;
import com.sqiwy.menu.server.DBHelperProductCM;

/**
 * Created by abrysov
 * Custom loader to load a product with modifiers.
 */
public class ProductsLoader extends BaseLoader<List<Product>> {
    
	private final int mCategoryId;

	public ProductsLoader(Context context, int categoryId) {
		super(context);
        mCategoryId = categoryId;
	}
	
	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		
		ContentResolver contentResolver = getContext().getContentResolver();
		contentResolver.registerContentObserver(MenuProvider.URI_PRODUCT, true, getObserver());
		contentResolver.registerContentObserver(
        		MenuProvider.URI_PRODUCT_MODIFIER_GROUP_JOIN_MODIFIER_GROUP, true, getObserver());
		contentResolver.registerContentObserver(MenuProvider.URI_MODIFIER, true, getObserver());
	}

	@Override
    @SuppressWarnings("ConstantConditions")
	public List<Product> loadInBackground() {
        
		// Get all products with its modifiers
		List<Product> result = new ArrayList<Product>();
		
		ContentResolver resolver = getContext().getContentResolver();
		
		// CURSOR get
		Cursor productsCursor = resolver.query(MenuProvider.URI_PRODUCT, null,
                DBHelper.F_PRODUCT_CATEGORY_ID + " = " + mCategoryId + " AND "
                        + DBHelper.F_ENABLED + " = 1"
                        , null, DBHelper.F_SORT_INDEX);
		
		while (productsCursor.moveToNext()) {
        
			//Product product = Product.fromCursor(productsCursor);
			Product product = DBHelperProductCM.getProduct(productsCursor);
	        product.setModifierGroups(DataBaseUtils.getProductModifierGroups(product.getId(), resolver));
	        result.add(product);
		}
		
		// CURSOR close
		productsCursor.close();
		
		return result;
	}
	
	@Override
	protected void onReset() {
		super.onReset();
	}

}
