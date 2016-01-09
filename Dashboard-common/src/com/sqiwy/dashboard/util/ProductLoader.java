package com.sqiwy.dashboard.util;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.model.Product;
import com.sqiwy.menu.provider.DBHelper;
import com.sqiwy.menu.provider.MenuProvider;

/**
 * Created by abrysov
 * Custom loader to load a product with modifiers.
 */
public class ProductLoader extends AsyncTaskLoader<Product> {
    private final int mProductId;
	private Product mResult;

	public ProductLoader(Context context, int productId) {
		super(context);
        mProductId = productId;
	}

	@Override
    @SuppressWarnings("ConstantConditions")
	public Product loadInBackground() {
        
		// Get the product data.
        ContentResolver resolver = getContext().getContentResolver();
        
        Cursor productCursor = resolver.query(
                MenuProvider.URI_PRODUCT, ProductQuery.PROJECTION,
                DBHelper.F_SERVER_ID + " = " + mProductId, null, null);
        
        if (!productCursor.moveToFirst()) {
            return null;
        }
        
        Product product = Product.fromCursor(productCursor);
        productCursor.close();
        product.setModifierGroups(DataBaseUtils.getProductModifierGroups(product.getId(), resolver));
        
		return product;
	}

	@Override
	public void deliverResult(Product data) {
		if (isReset()) {
			mResult = null;
		} else {
            mResult = data;
            if (isStarted()) {
                super.deliverResult(data);
            }
        }
	}
	
	@Override
	protected void onStartLoading() {
		if (null != mResult) {
			deliverResult(mResult);
		}
		if (takeContentChanged() || null == mResult) {
			forceLoad();
		}
	}
	
	@Override
	protected void onStopLoading() {
		super.onStopLoading();
		cancelLoad();
	}
	
	@Override
	protected void onReset() {
		super.onReset();
		onStopLoading();
		mResult = null;
	}

    public interface ProductQuery {
        String[] PROJECTION = {
                DBHelper.F_SERVER_ID,
                DBHelper.F_PRODUCT_CATEGORY_ID,
                DBHelper.F_NAME,
                DBHelper.F_PRICE,
                DBHelper.F_SORT_INDEX,
                DBHelper.F_IMG_URL,
                DBHelper.F_WEIGHT,
                DBHelper.F_TYPE,
                DBHelper.F_FULL_NAME,
                DBHelper.F_DESCRIPTION,
                DBHelper.F_SIGNIFICANCE,
                DBHelper.F_CALORICITY
        };
        int SERVER_ID = 0;
        int PRODUCT_CATEGORY_ID = 1;
        int NAME = 2;
        int PRICE = 3;
        int SORT_INDEX = 4;
        int IMG_URL = 5;
        int WEIGHT = 6;
        int TYPE = 7;
        int FULL_NAME = 8;
        int DESCRIPTION = 9;
        int SIGNIFICANCE = 10;
        int CALORICITY=11;
    }

    public interface ModifierGroupsQuery {
        String[] PROJECTION = {
                DBHelper.T_MODIFIER_GROUP + "." + DBHelper.F_SERVER_ID,
                DBHelper.F_PARENT_SERVER_ID,
                DBHelper.F_TYPE,
                DBHelper.F_NAME,
                DBHelper.F_IMG_URL,
                DBHelper.T_MODIFIER_GROUP + "." + DBHelper.F_SORT_INDEX,
                DBHelper.F_REQUIRED,
                DBHelper.T_MODIFIER_GROUP + "." + DBHelper.F_LANG,
        };
        int SERVER_ID = 0;
        int PARENT_SERVER_ID = 1;
        int TYPE = 2;
        int NAME = 3;
        int IMG_URL = 4;
        int SORT_INDEX = 5;
        int REQUIRED = 6;
        int LANG = 7;
    }

    public interface ModifiersQuery {
        String[] PROJECTION = {
                DBHelper.F_SERVER_ID,
                DBHelper.F_MODIFIER_GROUP_ID,
                DBHelper.F_NAME,
                DBHelper.F_PRICE,
                DBHelper.F_SORT_INDEX,
                DBHelper.F_IMG_URL,
                DBHelper.F_WEIGHT,
                DBHelper.F_CALORICITY
        };
        int SERVER_ID = 0;
        int MODIFIER_GROUP_ID = 1;
        int NAME = 2;
        int PRICE = 3;
        int SORT_INDEX = 4;
        int IMG_URL = 5;
        int WEIGHT = 6;
    }
}
