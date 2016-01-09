package com.sqiwy.menu.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.sqiwy.backend.api.BackendException;
import com.sqiwy.backend.data.Menu;
import com.sqiwy.backend.data.Modifier;
import com.sqiwy.backend.data.ModifierGroup;
import com.sqiwy.backend.data.Product;
import com.sqiwy.backend.data.ProductCategory;
import com.sqiwy.backend.data.ProductModifierGroup;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.util.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;


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

/**
 * Created by abrysov
 */

public class MenuSaver {

	
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
	public interface OnMenuSavedListener{
		void onMenuSaved(boolean isSuccess);
	}
	
	
	
	/**
	 * 
	 */
	private OnMenuSavedListener listener;
	
	
	
	
	/**
	 * Saves menu data retrieved from OperationService.getMenu() into 
	 * application MenuProvider <b>asynchronously</b>.
	 * 
	 * @param listener
	 * @return
	 */
	public boolean saveMenu(OnMenuSavedListener listener){
		
		this.listener = listener;
		
		class SaveTask extends AsyncTask<Void, Void, Boolean>{
			@Override
			protected Boolean doInBackground(Void... params) {
				Menu menu = null;
				try {
					menu = MenuSaver.app.getOperationService().getMenu();
				} catch (BackendException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return menu == null ? false : saveMenu(menu);
				
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
                if (MenuSaver.this.listener != null) {
                    MenuSaver.this.listener.onMenuSaved(result);
                }
            }
		}
		
		SaveTask task = new SaveTask();
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
	 * @see ContentProvider
	 */
	private boolean saveMenu(final Menu menu){
		
		if(menu == null) return false;
		
		ContentValues cv;
		ContentValues[] cvs;
		int insertedRows;
		boolean result = true;
		/* SAVE PRODUCT CATEGORY */
		
		app.getContentResolver().delete(MenuProvider.URI_PRODUCT_CATEGORY, null, null);
		
		List<ProductCategory> productCategoryList = menu.getProductCategoryList();
		cvs = new ContentValues[productCategoryList.size()];
		for(int i = 0; i < cvs.length; i++){
			cv = new ContentValues();
			cv.put(DBHelper.F_ID, productCategoryList.get(i).getId());
			cv.put(DBHelper.F_PARENT_ID, productCategoryList.get(i).getParentId());
			cv.put(DBHelper.F_ENABLED, productCategoryList.get(i).isEnabled());
			cv.put(DBHelper.F_NAME, productCategoryList.get(i).getName());
			cv.put(DBHelper.F_SORT_INDEX, productCategoryList.get(i).getSortIndex());
			cv.put(DBHelper.F_IMG_URL, productCategoryList.get(i).getImgUrl());
			cv.put(DBHelper.F_SIMPLE_LIST, productCategoryList.get(i).getSimpleList());
			cvs[i] = cv;
		}
		
		insertedRows = app.getContentResolver().bulkInsert(MenuProvider.URI_PRODUCT_CATEGORY, cvs);
		
		Log.e("ProductCategory", String.valueOf(insertedRows));
		
		if(cvs.length > insertedRows) {
			Log.e("ProductCategory", "some data was not saved");
			result = false;
		}
		
		
		
		/* SAVE PRODUCTS */
		
		app.getContentResolver().delete(MenuProvider.URI_PRODUCT, null, null);
		
		List<Product> productList = menu.getProductList();

        // Check the product images.
        for (Product product : productList) {
            checkProductImage(product);
        }

        cvs = new ContentValues[productList.size()];
		
		for(int i = 0; i < cvs.length; i++){
			cv = new ContentValues();
			cv.put(DBHelper.F_ID, productList.get(i).getId());
			cv.put(DBHelper.F_PRODUCT_CATEGORY_ID, productList.get(i).getProductCategoryId());
			cv.put(DBHelper.F_ENABLED, productList.get(i).isEnabled());
			cv.put(DBHelper.F_NAME, productList.get(i).getName());
			cv.put(DBHelper.F_PRICE, productList.get(i).getPrice().toString()); // <= XXX: CHECK IF NO PROBLEM HERE
			cv.put(DBHelper.F_SORT_INDEX, productList.get(i).getSortIndex());
			cv.put(DBHelper.F_IMG_URL, productList.get(i).getImgUrl());
			cv.put(DBHelper.F_WEIGHT, productList.get(i).getWeight());
			cv.put(DBHelper.F_TYPE, productList.get(i).getType());
			cv.put(DBHelper.F_FULL_NAME, productList.get(i).getFullname());
			cv.put(DBHelper.F_DESCRIPTION, productList.get(i).getDescription());
			cv.put(DBHelper.F_SIGNIFICANCE, productList.get(i).getSignificance());
			cvs[i] = cv;
		}
		
		insertedRows = app.getContentResolver().bulkInsert(MenuProvider.URI_PRODUCT, cvs);
		
		Log.e("Product", String.valueOf(insertedRows));
		
		if(cvs.length > insertedRows) {
			Log.e("Product", "some data was not saved");
			result = false;
		}
		
		
		/* SAVE PRODUCT MODIFIER GROUP LIST*/
		
		app.getContentResolver().delete(MenuProvider.URI_PRODUCT_MODIFIER_GROUP, null, null);
		
		List<ProductModifierGroup> productModifierGroupList = menu.getProductModifierGroupList();
		
		cvs = new ContentValues[productModifierGroupList.size()];
		
		for(int i = 0; i < cvs.length; i++){
			cv = new ContentValues();
			cv.put(DBHelper.F_ID, productModifierGroupList.get(i).getId());
			cv.put(DBHelper.F_PRODUCT_ID, productModifierGroupList.get(i).getProductId());
			cv.put(DBHelper.F_MODIFIER_GROUP_ID, productModifierGroupList.get(i).getModifierGroupId());
			cv.put(DBHelper.F_SORT_INDEX, productModifierGroupList.get(i).getSortIndex());
			cv.put(DBHelper.F_REQUIRED, productModifierGroupList.get(i).isRequired() ? 1 : 0);
			cvs[i] = cv;
		}
		
		insertedRows = app.getContentResolver().bulkInsert(MenuProvider.URI_PRODUCT_MODIFIER_GROUP, cvs);

		Log.e("ProductModifierGroup", String.valueOf(insertedRows));
		
		if(cvs.length > insertedRows) {
			Log.e("ProductModifierGroup", "some data was not saved");
			result = false;
		}
		
		/* SAVE MODIFIER GROUP LIST */
		
		app.getContentResolver().delete(MenuProvider.URI_MODIFIER_GROUP, null, null);
		
		List<ModifierGroup> modifierGroupList = menu.getModifierGroupList();
		
		cvs = new ContentValues[modifierGroupList.size()];
		
		
		for(int i = 0; i < cvs.length; i++){
			cv = new ContentValues();
			cv.put(DBHelper.F_ID, modifierGroupList.get(i).getId());
			cv.put(DBHelper.F_PARENT_ID, modifierGroupList.get(i).getParentId());
			cv.put(DBHelper.F_ENABLED, modifierGroupList.get(i).isEnabled());
			cv.put(DBHelper.F_TYPE, modifierGroupList.get(i).getType());
			cv.put(DBHelper.F_NAME, modifierGroupList.get(i).getName());
			cv.put(DBHelper.F_IMG_URL, modifierGroupList.get(i).getImgUrl());
			cv.put(DBHelper.F_SORT_INDEX, modifierGroupList.get(i).getSortIndex());
			cvs[i] = cv;
		}

		insertedRows = app.getContentResolver().bulkInsert(MenuProvider.URI_MODIFIER_GROUP, cvs);
		
		Log.e("ModifierGroup", String.valueOf(insertedRows));
		
		if(cvs.length > insertedRows) {
			Log.e("ModifierGroup", "some data was not saved");
			result = false;
		}
		
		/* SAVE <ODIFIER LIST */
		
		app.getContentResolver().delete(MenuProvider.URI_MODIFIER, null, null);
		
		List<Modifier> modifierList = menu.getModifierList();
		
		cvs = new ContentValues[modifierList.size()];
		
		for(int i = 0; i < cvs.length; i++){
			cv = new ContentValues();
			
			cv.put(DBHelper.F_ID, modifierList.get(i).getId());
			cv.put(DBHelper.F_MODIFIER_GROUP_ID, modifierList.get(i).getModifierGroupId());
			cv.put(DBHelper.F_ENABLED, modifierList.get(i).isEnabled());
			cv.put(DBHelper.F_NAME, modifierList.get(i).getName());
			cv.put(DBHelper.F_PRICE, modifierList.get(i).getPrice().toString()); // <= XXX: CHECK IF NO PROBLEM HERE
			cv.put(DBHelper.F_SORT_INDEX, modifierList.get(i).getSortIndex());
			cv.put(DBHelper.F_IMG_URL, modifierList.get(i).getImgUrl());
			cv.put(DBHelper.F_WEIGHT, modifierList.get(i).getWeight());
			
			cvs[i] = cv;
		}
		
		insertedRows = app.getContentResolver().bulkInsert(MenuProvider.URI_MODIFIER, cvs);
		
		Log.e("Modifier", String.valueOf(insertedRows));
		
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
            File imageFile = ResourcesManager.getResourcePath(imageName, imageCategory);
            if (!imageFile.exists()) {
                product.setImgUrl("");
                product.setSignificance(DBHelper.PRODUCT_SIGNIFICANCE_TEXT);
            }
        }
    }


}
