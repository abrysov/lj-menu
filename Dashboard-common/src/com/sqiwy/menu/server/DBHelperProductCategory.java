package com.sqiwy.menu.server;

import android.database.Cursor;

import com.sqiwy.menu.model.Category;
import com.sqiwy.menu.provider.DBHelper;

/**
 * Created by abrysov
 */

public class DBHelperProductCategory {

	public static Category getCategory(Cursor cursor) {
		if (cursor.isBeforeFirst() || cursor.isAfterLast())
			return null;
		Category category = new Category();
		category.setId(cursor.getInt(cursor.getColumnIndex(DBHelper.F_SERVER_ID)));
		category.setParentId(cursor.getInt(cursor.getColumnIndex(DBHelper.F_PARENT_SERVER_ID)));
		category.setEnabled(cursor.getInt(cursor.getColumnIndex(DBHelper.F_ENABLED)) == 1);
		category.setName(cursor.getString(cursor.getColumnIndex(DBHelper.F_NAME)));
		category.setSortIndex(cursor.getInt(cursor.getColumnIndex(DBHelper.F_SORT_INDEX)));
		category.setImgUrl(cursor.getString(cursor.getColumnIndex(DBHelper.F_IMG_URL)));
		category.setSimpleList(cursor.getInt(cursor.getColumnIndex(DBHelper.F_SIMPLE_LIST)) == 1);
		return category;
	}

}
