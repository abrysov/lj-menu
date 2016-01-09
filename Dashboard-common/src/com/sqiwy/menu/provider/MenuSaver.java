package com.sqiwy.menu.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.sqiwy.restaurant.api.BackendException;
import com.sqiwy.restaurant.api.ClientConfig.SupportedLanguage;
import com.sqiwy.restaurant.api.data.Menu;
import com.sqiwy.restaurant.api.data.Modifier;
import com.sqiwy.restaurant.api.data.ModifierGroup;
import com.sqiwy.restaurant.api.data.Product;
import com.sqiwy.restaurant.api.data.ProductCategory;
import com.sqiwy.restaurant.api.data.ProductModifierGroup;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.util.CommonUtils;
import com.sqiwy.menu.util.PreferencesUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by abrysov
 */


/**
 * 
 * Use this class to save Menu data retrieved from OperationService.getMenu().
 * Data that previously was saved will be deleted.
 * New data will be inserted trough bulk insertion.
 * MenuProvider observers will receive two notification for each Uri related with menu data.<br/><br/>
 * Url list related with menu data:<br/><br/>
 * <code>
 * MenuProvider.{@link com.sqiwy.menu.provider.MenuProvider#URI_PRODUCT_CATEGORY URI_PRODUCT_CATEGORY}<br/>
 * MenuProvider.{@link com.sqiwy.menu.provider.MenuProvider#URI_PRODUCT URI_PRODUCT}<br/>
 * MenuProvider.{@link com.sqiwy.menu.provider.MenuProvider#URI_PRODUCT_MODIFIER_GROUP URI_PRODUCT_MODIFIER_GROUP}<br/>
 * MenuProvider.{@link com.sqiwy.menu.provider.MenuProvider#URI_MODIFIER_GROUP URI_MODIFIER_GROUP}<br/>
 * MenuProvider.{@link com.sqiwy.menu.provider.MenuProvider#URI_MODIFIER URI_MODIFIER}<br/>
 * </code>
 */
public class MenuSaver {
	private final String TAG = getClass().getSimpleName();
	
	// Should be static to avoid mem leaks
	private static MenuApplication app;
	
	
	
	/**
	 * 
	 * @param app
	 */
	public MenuSaver(MenuApplication app) {
		MenuSaver.app = app;
	}
	

	
	/**
	 * Implement this interface if you need to be notified
	 * about Menu saving completion.
	 */
	public interface OnMenuSavedListener {
		void onMenuSaved(boolean isSuccess);
	}

    /**
	 * 
	 */
	private class SaveTask extends AsyncTask<Void, Void, Boolean> {
		private OnMenuSavedListener mListener;
		
		public SaveTask(OnMenuSavedListener listener) {
			mListener=listener;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
            Menu menu = null;
            try {
            	if (!PreferencesUtils.isApplicationInDemoMode(MenuApplication.getContext())) {
            		SupportedLanguage appLang = MenuApplication.getLanguage();
            		for (SupportedLanguage current: SupportedLanguage.values()) {
	            		MenuApplication.setLanguage(current);
	            		menu = MenuApplication.getOperationService().getMenu();
	            		if (menu != null && !isEmptyMenu(menu)) {
	            			saveMenu(menu, current);
	                    }
            		}
            		MenuApplication.setLanguage(appLang);
            	} else {
            		try {
            			Thread.sleep(20000);
            		} catch (InterruptedException e) {
            			e.printStackTrace();
            		}
            		return !isEmptyMenuStorage();
            	}
            } catch (Exception e) {
            	e.printStackTrace();
            }
            return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
            if (mListener != null) {
                mListener.onMenuSaved(result);
            }
        }
		
		private boolean isEmptyMenu(Menu menu) {
			return (menu.getModifierGroupList()==null || menu.getModifierGroupList().size()==0) &&
					(menu.getModifierList()==null || menu.getModifierList().size()==0) &&
					(menu.getProductCategoryList()==null || menu.getProductCategoryList().size()==0) &&
					(menu.getProductList()==null || menu.getProductList().size()==0) &&
					(menu.getProductModifierGroupList()==null || menu.getProductModifierGroupList().size()==0);
		}
		
		private boolean isEmptyMenuStorage() {
            ContentResolver resolver=app.getContentResolver(); 
            Cursor cr=resolver.query(MenuProvider.URI_PRODUCT_CATEGORY,new String[] { "count()" }, null, null, null);
            int n=0;
            cr.moveToFirst(); n+=cr.getInt(0); cr.close();
            cr=resolver.query(MenuProvider.URI_PRODUCT, new String[] { "count()" }, null, null, null);
            cr.moveToFirst(); n+=cr.getInt(0); cr.close();
            cr=resolver.query(MenuProvider.URI_PRODUCT_MODIFIER_GROUP, new String[] { "count()" }, null, null, null);
            cr.moveToFirst(); n+=cr.getInt(0); cr.close();
            cr=resolver.query(MenuProvider.URI_MODIFIER_GROUP, new String[] { "count()" }, null, null, null);
            cr.moveToFirst(); n+=cr.getInt(0); cr.close();
            cr=resolver.query(MenuProvider.URI_MODIFIER, new String[] { "count()" }, null, null, null);
            cr.moveToFirst(); n+=cr.getInt(0); cr.close();
            return n==0;
		}
	}

	
	/**
	 * Saves menu data retrieved from OperationService.getMenu() into 
	 * application MenuProvider <b>asynchronously</b>.
	 * 
	 * @param listener
	 * @return
	 */
	public boolean saveMenu(OnMenuSavedListener listener){
		SaveTask task = new SaveTask(listener);
		task.execute();
		
		return false;
	}
	
	/**
	 * Saves menu data retrieved from OperationService.getMenu() into 
	 * application MenuProvider. This method does not provide async operation.
	 * If You need to perform saving asynchronously use saveMenu(Menu, OnMenuSavedListener),
	 * or implement own asynchronously mechanism.   
	 * @param menu - the com.sqiwy.backend.data.Menu
	 * @return true if all insertion was successful, false otherwise.
	 * @see android.content.ContentProvider
	 */
	private boolean saveMenu(final Menu menu, SupportedLanguage lang) {

        Log.d("SAVE_MENU", lang.getCode());

		if(menu == null) return false;
		
		ContentValues cv;
		ContentValues[] cvs;
		int insertedRows;
		boolean result = true;
		/* SAVE PRODUCT CATEGORY */
		
		app.getContentResolver().delete(MenuProvider.URI_PRODUCT_CATEGORY, null, null);
		
		List<ProductCategory> productCategoryList = menu.getProductCategoryList();
        Log.d("ProductCategory", String.valueOf(productCategoryList.size()));

        cvs = new ContentValues[productCategoryList.size()];
		for (int i = 0; i < cvs.length; i++) {
			cv = new ContentValues();
			cv.put(DBHelper.F_SERVER_ID, productCategoryList.get(i).getId());
			cv.put(DBHelper.F_LANG, lang.getCode());
			cv.put(DBHelper.F_PARENT_SERVER_ID, productCategoryList.get(i).getParentId());
			cv.put(DBHelper.F_ENABLED, productCategoryList.get(i).getEnabled());
			cv.put(DBHelper.F_NAME, productCategoryList.get(i).getName());
			cv.put(DBHelper.F_SORT_INDEX, productCategoryList.get(i).getSortIndex());
			cv.put(DBHelper.F_IMG_URL, productCategoryList.get(i).getImgUrl());
			cv.put(DBHelper.F_SIMPLE_LIST, productCategoryList.get(i).getSimpleList());
			cvs[i] = cv;
		}
		
		insertedRows = app.getContentResolver().bulkInsert(MenuProvider.URI_PRODUCT_CATEGORY, cvs);
		
		Log.d("ProductCategory", String.valueOf(insertedRows));
		
		if(cvs.length > insertedRows) {
			Log.e("ProductCategory", "some data was not saved");
			result = false;
		}
		
		/* SAVE PRODUCTS */
		
		app.getContentResolver().delete(MenuProvider.URI_PRODUCT, null, null);
		
		List<Product> productList = menu.getProductList();
        Log.d("Product", String.valueOf(productList.size()));

        // Check the product images.
        for (Product product : productList) {
            checkProductImage(product);
        }

        cvs = new ContentValues[productList.size()];
		
		for(int i = 0; i < cvs.length; i++){
			cv = new ContentValues();
			
			Product curProduct=productList.get(i);
			
			cv.put(DBHelper.F_SERVER_ID, curProduct.getId());
			cv.put(DBHelper.F_LANG, lang.getCode());
			cv.put(DBHelper.F_PRODUCT_CATEGORY_ID, curProduct.getProductCategoryId());
			cv.put(DBHelper.F_ENABLED, curProduct.isEnabled());
			cv.put(DBHelper.F_NAME, curProduct.getName());
			cv.put(DBHelper.F_PRICE, curProduct.getPrice().toString()); // <= XXX: CHECK IF NO PROBLEM HERE
			cv.put(DBHelper.F_SORT_INDEX, curProduct.getSortIndex());
			cv.put(DBHelper.F_IMG_URL, curProduct.getImgUrl());
			cv.put(DBHelper.F_WEIGHT, curProduct.getWeight());
			cv.put(DBHelper.F_TYPE, curProduct.getType());
			cv.put(DBHelper.F_FULL_NAME, curProduct.getFullname());
			cv.put(DBHelper.F_DESCRIPTION, curProduct.getDescription());
			cv.put(DBHelper.F_SIGNIFICANCE, curProduct.getSignificance());
			cv.put(DBHelper.F_CALORICITY, Integer.toString(curProduct.getCaloricity()));
			cvs[i] = cv;
		}
		
		insertedRows = app.getContentResolver().bulkInsert(MenuProvider.URI_PRODUCT, cvs);
		
		Log.d("Product", String.valueOf(insertedRows));
		
		if(cvs.length > insertedRows) {
			Log.e("Product", "some data was not saved");
			result = false;
		}
		
		
		/* SAVE PRODUCT MODIFIER GROUP LIST*/
		
		app.getContentResolver().delete(MenuProvider.URI_PRODUCT_MODIFIER_GROUP, null, null);
		
		List<ProductModifierGroup> productModifierGroupList = menu.getProductModifierGroupList();
        Log.d("ProductModifierGroup", String.valueOf(productModifierGroupList.size()));

		cvs = new ContentValues[productModifierGroupList.size()];
		
		for(int i = 0; i < cvs.length; i++){
			cv = new ContentValues();
			cv.put(DBHelper.F_SERVER_ID, productModifierGroupList.get(i).getId());
			cv.put(DBHelper.F_LANG, lang.getCode());
			cv.put(DBHelper.F_PRODUCT_ID, productModifierGroupList.get(i).getProductId());
			cv.put(DBHelper.F_MODIFIER_GROUP_ID, productModifierGroupList.get(i).getModifierGroupId());
			cv.put(DBHelper.F_SORT_INDEX, productModifierGroupList.get(i).getSortIndex());
			cv.put(DBHelper.F_REQUIRED, productModifierGroupList.get(i).getRequired() ? 1 : 0);
			cvs[i] = cv;
		}
		
		insertedRows = app.getContentResolver().bulkInsert(MenuProvider.URI_PRODUCT_MODIFIER_GROUP, cvs);

		Log.d("ProductModifierGroup", String.valueOf(insertedRows));
		
		if (cvs.length > insertedRows) {
			Log.e("ProductModifierGroup", "some data was not saved");
			result = false;
		}
		
		/* SAVE MODIFIER GROUP LIST */
		
		app.getContentResolver().delete(MenuProvider.URI_MODIFIER_GROUP, null, null);
		
		List<ModifierGroup> modifierGroupList = menu.getModifierGroupList();
        Log.d("ModifierGroup", String.valueOf(modifierGroupList.size()));

		cvs = new ContentValues[modifierGroupList.size()];
		
		
		for(int i = 0; i < cvs.length; i++){
			cv = new ContentValues();
			cv.put(DBHelper.F_SERVER_ID, modifierGroupList.get(i).getId());
			cv.put(DBHelper.F_LANG, lang.getCode());
			cv.put(DBHelper.F_PARENT_SERVER_ID, modifierGroupList.get(i).getParentId());
			cv.put(DBHelper.F_ENABLED, modifierGroupList.get(i).isEnabled());
			cv.put(DBHelper.F_TYPE, modifierGroupList.get(i).getType());
			cv.put(DBHelper.F_NAME, modifierGroupList.get(i).getName());
			cv.put(DBHelper.F_IMG_URL, modifierGroupList.get(i).getImgUrl());
			cv.put(DBHelper.F_SORT_INDEX, modifierGroupList.get(i).getSortIndex());
			cvs[i] = cv;
		}

		insertedRows = app.getContentResolver().bulkInsert(MenuProvider.URI_MODIFIER_GROUP, cvs);
		
		Log.d("ModifierGroup", String.valueOf(insertedRows));
		
		if(cvs.length > insertedRows) {
			Log.e("ModifierGroup", "some data was not saved");
			result = false;
		}
		
		/* SAVE MODIFIER LIST */
		
		app.getContentResolver().delete(MenuProvider.URI_MODIFIER, null, null);
		
		List<Modifier> modifierList = menu.getModifierList();
        Log.d("Modifier", String.valueOf(modifierList.size()));

		cvs = new ContentValues[modifierList.size()];
		
		for(int i = 0; i < cvs.length; i++){
			cv = new ContentValues();
			
			cv.put(DBHelper.F_SERVER_ID, modifierList.get(i).getId());
			cv.put(DBHelper.F_LANG, lang.getCode());
			cv.put(DBHelper.F_MODIFIER_GROUP_ID, modifierList.get(i).getModifierGroupId());
			cv.put(DBHelper.F_ENABLED, modifierList.get(i).isEnabled());
			cv.put(DBHelper.F_NAME, modifierList.get(i).getName());
			cv.put(DBHelper.F_PRICE, modifierList.get(i).getPrice().toString()); // <= XXX: CHECK IF NO PROBLEM HERE
			cv.put(DBHelper.F_SORT_INDEX, modifierList.get(i).getSortIndex());
			cv.put(DBHelper.F_IMG_URL, modifierList.get(i).getImgUrl());
			cv.put(DBHelper.F_WEIGHT, modifierList.get(i).getWeight());
			// TODO: cv.put(DBHelper.F_CALORICITY, modifierList.get(i).getCaloricity());
			
			cvs[i] = cv;
		}
		
		insertedRows = app.getContentResolver().bulkInsert(MenuProvider.URI_MODIFIER, cvs);
		
		Log.d("Modifier", String.valueOf(insertedRows));
		
		if(cvs.length > insertedRows) {
			Log.e("Modifier", "some data was not saved");
			result = false;
		}
		
		return result;
		
	}

    private static void checkProductImage(Product product) {
        ResourcesManager.Category imageCategory = null;
        int significance = product.getSignificance();
        if (significance == DBHelper.PRODUCT_SIGNIFICANCE_STANDARD) {
            imageCategory = ResourcesManager.Category.MENU_SMALL;
        } else if (significance == DBHelper.PRODUCT_SIGNIFICANCE_BIG) {
            imageCategory = ResourcesManager.Category.MENU_BIG;
        }
        if (imageCategory != null) {
            // If the product image doesn't exist, reset its url and set significance to text.
            String imageName = CommonUtils.extractMenuProductImageName(product.getImgUrl());

            if (imageName != null) {
                File imageFile = ResourcesManager.getResourcePath(imageName, imageCategory);
                if (!imageFile.exists()) {
                    product.setImgUrl("");
                    product.setSignificance(DBHelper.PRODUCT_SIGNIFICANCE_TEXT);
                }
            } else {
                product.setImgUrl("");
                product.setSignificance(DBHelper.PRODUCT_SIGNIFICANCE_TEXT);
            }
        }
    }


}
