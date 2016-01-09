package com.sqiwy.menu.server;

import android.database.Cursor;

import com.sqiwy.menu.model.Product;
import com.sqiwy.menu.provider.DBHelper;

/**
 * Created by abrysov
 */

public class DBHelperProductCM {
	
	public static Product getProduct(Cursor cursor){
		Product pc = new Product();
		pc.setId(cursor.getInt(cursor.getColumnIndex(DBHelper.F_SERVER_ID)));
		pc.setIdProductCategory(cursor.getInt(cursor.getColumnIndex(DBHelper.F_PRODUCT_CATEGORY_ID)));
		pc.setEnabled(cursor.getInt(cursor.getColumnIndex(DBHelper.F_ENABLED)) == 1);
		pc.setName(cursor.getString(cursor.getColumnIndex(DBHelper.F_NAME)));
		pc.setPrice((float) cursor.getDouble(cursor.getColumnIndex(DBHelper.F_PRICE)));
		pc.setSortIndex(cursor.getInt(cursor.getColumnIndex(DBHelper.F_SORT_INDEX)));
		pc.setImgUrl(cursor.getString(cursor.getColumnIndex(DBHelper.F_IMG_URL)));
		pc.setDishWeight(cursor.getString(cursor.getColumnIndex(DBHelper.F_WEIGHT)));
		pc.setType(cursor.getString(cursor.getColumnIndex(DBHelper.F_TYPE)));
		pc.setFullname(cursor.getString(cursor.getColumnIndex(DBHelper.F_FULL_NAME)));
		pc.setDesc(cursor.getString(cursor.getColumnIndex(DBHelper.F_DESCRIPTION)));
		pc.setSignificance(cursor.getInt(cursor.getColumnIndex(DBHelper.F_SIGNIFICANCE)));
		pc.setCaloricity(cursor.getString(cursor.getColumnIndex(DBHelper.F_CALORICITY)));
		return pc;
	}
	
}
