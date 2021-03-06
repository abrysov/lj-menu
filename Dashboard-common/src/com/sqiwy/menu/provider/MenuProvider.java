package com.sqiwy.menu.provider;

import com.sqiwy.menu.MenuApplication;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.*;

/**
 * Created by abrysov
 */

public class MenuProvider extends ContentProvider {

	private static final String SCHEMA = "content://";
	
	public static final String AUTHORITY = "com.sqiwy.menu.provider";
	
	/**
	 * This Uri leads to <code>{@link com.sqiwy.menu.provider.DBHelper#T_PRODUCT_CATEGORY product_category}</code> table.
	 */
	public static final Uri URI_PRODUCT_CATEGORY = 
			Uri.parse(SCHEMA + AUTHORITY + "/" + DBHelper.T_PRODUCT_CATEGORY);
	
	/**
	 * This Uri leads to <code>{@link com.sqiwy.menu.provider.DBHelper#T_PRODUCT product}</code> table.
	 */
	public static final Uri URI_PRODUCT = 
			Uri.parse(SCHEMA + AUTHORITY + "/" +  DBHelper.T_PRODUCT);
	
	/**
	 * This Uri leads to <code>{@link com.sqiwy.menu.provider.DBHelper#T_PRODUCT_MODIFIER_GROUP product_modifier_group}</code> table.
	 */
	public static final Uri URI_PRODUCT_MODIFIER_GROUP = 
			Uri.parse(SCHEMA + AUTHORITY + "/" +  DBHelper.T_PRODUCT_MODIFIER_GROUP);
	
	/**
	 * This Uri leads to <code>{@link com.sqiwy.menu.provider.DBHelper#T_MODIFIER_GROUP modifier_group}</code> table.
	 */
	public static final Uri URI_MODIFIER_GROUP = 
			Uri.parse(SCHEMA + AUTHORITY + "/" +  DBHelper.T_MODIFIER_GROUP);

	
	/**
	 * This Uri leads to <code>{@link com.sqiwy.menu.provider.DBHelper#T_MODIFIER modifier}</code> table.
	 */
	public static final Uri URI_MODIFIER = 
			Uri.parse(SCHEMA + AUTHORITY + "/" +  DBHelper.T_MODIFIER);
	
	/**
	 * This Uri leads to <code>{@link com.sqiwy.menu.provider.DBHelper#T_ORDER order}</code> table.
	 */
	public static final Uri URI_ORDER = 
			Uri.parse(SCHEMA + AUTHORITY + "/" +  DBHelper.T_ORDER);
	
	/**
	 * This Uri leads to <code>{@link com.sqiwy.menu.provider.DBHelper#T_ORDER_PRODUCT order_product}</code> table.
	 */
	public static final Uri URI_ORDER_PRODUCT = 
			Uri.parse(SCHEMA + AUTHORITY + "/" +  DBHelper.T_ORDER_PRODUCT);
	
	/**
	 * This Uri leads to <code>{@link com.sqiwy.menu.provider.DBHelper#T_ORDER_PRODUCT_MODIFIER order_product_modifier}</code> table.
	 */
	public static final Uri URI_ORDER_PRODUCT_MODIFIER = 
			Uri.parse(SCHEMA + AUTHORITY + "/" +  DBHelper.T_ORDER_PRODUCT_MODIFIER);
	
	/**
	 * This Uri leads to <code>{@link com.sqiwy.menu.provider.DBHelper#T_RESOURCE resource}</code> table.
	 */
	public static final Uri URI_RESOURCE = 
			Uri.parse(SCHEMA + AUTHORITY + "/" +  DBHelper.T_RESOURCE);
	
	public static final Uri URI_ORDER_PRODUCT_JOIN_ORDER_PRODUCT_MODIFIER = 
			Uri.parse(SCHEMA + AUTHORITY + "/" + DBHelper.T_ORDER_PRODUCT_JOIN_ORDER_PRODUCT_MODIFIER);
	
	public static final Uri URI_ORDER_JOIN_ORDER_PRODUCT = 
			Uri.parse(SCHEMA + AUTHORITY + "/" + DBHelper.T_ORDER_JOIN_ORDER_PRODUCT);
	
	public static final Uri URI_ORDER_PRODUCT_JOIN_PRODUCT = 
			Uri.parse(SCHEMA + AUTHORITY + "/" + DBHelper.T_ORDER_PRODUCT_JOIN_PRODUCT);

	public static final Uri URI_PRODUCT_MODIFIER_GROUP_JOIN_MODIFIER_GROUP =
			Uri.parse(SCHEMA + AUTHORITY + "/" + DBHelper.T_PRODUCT_MODIFIER_GROUP_JOIN_MODIFIER_GROUP);

	public static final Uri URI_ORDER_PRODUCT_MODIFIER_JOIN_MODIFIER =
			Uri.parse(SCHEMA + AUTHORITY + "/" + DBHelper.T_ORDER_PRODUCT_MODIFIER_JOIN_MODIFIER);
	
	
	/**
	 * This Uri leads to <code>{@link com.sqiwy.menu.provider.DBHelper#T_FUCKING_TABLE resource}</code> table,
	 * and should be used only with the <code>query()</code> operations.
	 * Other operations on this table such as insert, update and delete will be ignored.
	 */
	public static final Uri URI_FUCKING_TABLE = 
			Uri.parse(SCHEMA + AUTHORITY + "/" + DBHelper.T_FUCKING_TABLE);
	
	public static final int PRODUCT_CATEGORY = 0;
	public static final int PRODUCT = 1;
	public static final int PRODUCT_MODIFIER_GROUP = 2;
	public static final int MODIFIER_GROUP = 3;
	public static final int MODIFIER = 4;
	public static final int ORDER = 5;
	public static final int ORDER_PRODUCT = 6;
	public static final int ORDER_PRODUCT_MODIFIER = 7;
	public static final int RESOURCE = 8;
	public static final int ORDER_PRODUCT_JOIN_ORDER_PRODUCT_MODIFIER = 9;
	public static final int ORDER_JOIN_ORDER_PRODUCT = 10;
	public static final int ORDER_PRODUCT_JOIN_PRODUCT = 11;
	public static final int PRODUCT_MODIFIER_GROUP_JOIN_MODIFIER_GROUP = 12;
	public static final int FUCKING_TABLE = 13; // <= Lucky number for fucking table :)
	public static final int ORDER_PRODUCT_MODIFIER_JOIN_MODIFIER = 14;

	
	public static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static Map<String,String> joinedTable = new HashMap<String, String>();
    private static Set<String> withoutLang = new HashSet<String>();
	
	
	static {
		uriMatcher.addURI(AUTHORITY, DBHelper.T_PRODUCT_CATEGORY, PRODUCT_CATEGORY);
		uriMatcher.addURI(AUTHORITY, DBHelper.T_PRODUCT, PRODUCT);
		uriMatcher.addURI(AUTHORITY, DBHelper.T_PRODUCT_MODIFIER_GROUP, PRODUCT_MODIFIER_GROUP);
		uriMatcher.addURI(AUTHORITY, DBHelper.T_MODIFIER_GROUP, MODIFIER_GROUP);
		uriMatcher.addURI(AUTHORITY, DBHelper.T_MODIFIER, MODIFIER);
		uriMatcher.addURI(AUTHORITY, DBHelper.T_ORDER, ORDER);
		uriMatcher.addURI(AUTHORITY, DBHelper.T_ORDER_PRODUCT, ORDER_PRODUCT);
		uriMatcher.addURI(AUTHORITY, DBHelper.T_ORDER_PRODUCT_MODIFIER, ORDER_PRODUCT_MODIFIER);
		uriMatcher.addURI(AUTHORITY, DBHelper.T_RESOURCE, RESOURCE);
		uriMatcher.addURI(AUTHORITY, DBHelper.T_ORDER_PRODUCT_JOIN_ORDER_PRODUCT_MODIFIER, ORDER_PRODUCT_JOIN_ORDER_PRODUCT_MODIFIER);
		uriMatcher.addURI(AUTHORITY, DBHelper.T_ORDER_JOIN_ORDER_PRODUCT, ORDER_JOIN_ORDER_PRODUCT);
		uriMatcher.addURI(AUTHORITY, DBHelper.T_ORDER_PRODUCT_JOIN_PRODUCT, ORDER_PRODUCT_JOIN_PRODUCT);
		uriMatcher.addURI(AUTHORITY, DBHelper.T_PRODUCT_MODIFIER_GROUP_JOIN_MODIFIER_GROUP, PRODUCT_MODIFIER_GROUP_JOIN_MODIFIER_GROUP);
		uriMatcher.addURI(AUTHORITY, DBHelper.T_FUCKING_TABLE, FUCKING_TABLE);
		uriMatcher.addURI(AUTHORITY, DBHelper.T_ORDER_PRODUCT_MODIFIER_JOIN_MODIFIER, ORDER_PRODUCT_MODIFIER_JOIN_MODIFIER);

        joinedTable.put(DBHelper.T_ORDER_PRODUCT_JOIN_PRODUCT, DBHelper.T_PRODUCT);
        joinedTable.put(DBHelper.T_PRODUCT_MODIFIER_GROUP_JOIN_MODIFIER_GROUP, DBHelper.T_MODIFIER_GROUP);
        joinedTable.put(DBHelper.T_ORDER_PRODUCT_MODIFIER_JOIN_MODIFIER, DBHelper.T_MODIFIER);

        withoutLang.add(DBHelper.T_ORDER_PRODUCT_JOIN_ORDER_PRODUCT_MODIFIER);
        withoutLang.add(DBHelper.T_ORDER_JOIN_ORDER_PRODUCT);
        withoutLang.add(DBHelper.T_ORDER);
        withoutLang.add(DBHelper.T_ORDER_PRODUCT);
        withoutLang.add(DBHelper.T_ORDER_PRODUCT_MODIFIER);
	}
	
	
	private SQLiteDatabase db;
	
	
	/*
	 * 
	 */
	@Override
	public boolean onCreate() {
		DBHelper helper = new DBHelper(getContext());
		this.db = helper.getWritableDatabase();
		return this.db == null ? false : true;
	}
	
	
	
	/*
	 * INSERTI
	 */
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		String table = getTableNameByUri(uri);
		
		if(table == null || table.equals(DBHelper.T_FUCKING_TABLE)) return null;
		
		long id = this.db.insert(table, null, values);
		
		if(id > -1){
            Uri newUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
		} else if(id < 0){
			throw new SQLException("Fail to add a new record into " + uri);
		}
		
		return null;
	}
	
	
	
	/*
	 * SELECT
	 */
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		Cursor result = null;
		
		String table = getTableNameByUri(uri);
		
		if(table == null) return result;
		
		if (table.equals(DBHelper.T_FUCKING_TABLE)) {
			try {
				table = String.format(DBHelper.T_FUCKING_TABLE_SCHEMA, (Object[])selectionArgs);
				result = this.db.rawQuery(table, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
            if (!withoutLang.contains(table)) {
                // add filter for current language
                String selectedTable = joinedTable.get(table) != null ? joinedTable.get(table) : table;

                if (selection != null) {
                    selection = "(" + selection + ") AND " + selectedTable + "." + DBHelper.F_LANG + " = '" + MenuApplication.getLanguage().getCode()+"'";
                } else {
                    selection = selectedTable + "." + DBHelper.F_LANG + " = '" + MenuApplication.getLanguage().getCode()+"'";
                }
            }

			Log.d("MenuProvider:query", table + " :: " + selection);
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder.setTables(table);
			try {
				result = queryBuilder.query(this.db, projection, selection, selectionArgs, null, null, sortOrder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
		
	}
	
	/*
	 * UPDATE
	 */
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		String table = getTableNameByUri(uri);

        if (!withoutLang.contains(table)) {
            if (selection != null) {
                selection = "(" + selection + ") AND " + table + "." + DBHelper.F_LANG + " = '" + MenuApplication.getLanguage().getCode()+"'";
            } else {
                selection = table + "." + DBHelper.F_LANG + " = '" + MenuApplication.getLanguage().getCode()+"'";
            }
        }

		int affectedRows = 0;
		
		Log.d("MenuProvider:update", table + " :: " + selection);
		if(table == null || table.equals(DBHelper.T_FUCKING_TABLE)) return affectedRows;
		affectedRows = this.db.update(table, values, selection, selectionArgs);
		if(affectedRows > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return affectedRows;
	}
	
	
	
	/*
	 * DELETE
	 */
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		String table = getTableNameByUri(uri);

        if (!withoutLang.contains(table)) {
            if (selection != null) {
                selection = "(" + selection + ") AND " + table + "." + DBHelper.F_LANG + " = '" + MenuApplication.getLanguage().getCode()+"'";
            } else {
                selection = table + "." + DBHelper.F_LANG + " = '" + MenuApplication.getLanguage().getCode()+"'";
            }
        }

		int affectedRows = 0;
		
		
		Log.d("MenuProvider:delete", table + " :: " + selection);
		
		if(table == null || table.equals(DBHelper.T_FUCKING_TABLE)) return affectedRows;
			
		affectedRows = this.db.delete(table, selection, selectionArgs);
			
		if(affectedRows > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		return affectedRows;
	}

	
	
	/*
	 * BULK INSERT 
	 */
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		
		String table = getTableNameByUri(uri);
		
		if(table == null || table.equals(DBHelper.T_FUCKING_TABLE)) return -1;
		
		int insertedRows = 0;
		
		for(int i = 0; i<values.length; i++){
			long res = this.db.insert(table, null, values[i]);
			insertedRows += res > -1 ? 1 : 0;
		}
		
		if(insertedRows > 0){
			getContext().getContentResolver().notifyChange(uri, null);
		} else if(values.length > 0 && insertedRows == 0){
			throw new SQLException("Fail to perform bulk insert into " + uri);
		}
		
		return insertedRows;
	}
	
	
	
	
	/*
	 *  
	 */
	private String getTableNameByUri(Uri uri){
		
		int match = uriMatcher.match(uri);
		
		String table = null;
		
		switch (match) {
		
		case PRODUCT_CATEGORY:
			table = DBHelper.T_PRODUCT_CATEGORY;
			break;
			
		case PRODUCT:
			table = DBHelper.T_PRODUCT;
			break;
			
		case PRODUCT_MODIFIER_GROUP:
			table = DBHelper.T_PRODUCT_MODIFIER_GROUP;
			break;
			
		case MODIFIER_GROUP:
			table = DBHelper.T_MODIFIER_GROUP;
			break;
			
		case MODIFIER:
			table = DBHelper.T_MODIFIER;
			break;
			
		case ORDER:
			table = DBHelper.T_ORDER;
			break;
			
		case ORDER_PRODUCT:
			table = DBHelper.T_ORDER_PRODUCT;
			break;

		case ORDER_PRODUCT_MODIFIER:
			table = DBHelper.T_ORDER_PRODUCT_MODIFIER;
			break;

		case RESOURCE:
			table = DBHelper.T_RESOURCE;
			break;

		case ORDER_PRODUCT_JOIN_ORDER_PRODUCT_MODIFIER:
			table = DBHelper.T_ORDER_PRODUCT_JOIN_ORDER_PRODUCT_MODIFIER;
			break;
			
		case ORDER_JOIN_ORDER_PRODUCT:
			table = DBHelper.T_ORDER_JOIN_ORDER_PRODUCT;
			break;
			
		case ORDER_PRODUCT_JOIN_PRODUCT:
			table = DBHelper.T_ORDER_PRODUCT_JOIN_PRODUCT;
			break;

		case PRODUCT_MODIFIER_GROUP_JOIN_MODIFIER_GROUP:
			table = DBHelper.T_PRODUCT_MODIFIER_GROUP_JOIN_MODIFIER_GROUP;
			break;
		
		case FUCKING_TABLE:
			table = DBHelper.T_FUCKING_TABLE;
			break;
			
		case ORDER_PRODUCT_MODIFIER_JOIN_MODIFIER:
			table = DBHelper.T_ORDER_PRODUCT_MODIFIER_JOIN_MODIFIER;
			break;
		}
		
		return table;
	}
	
	@Override
	public String getType(Uri uri) {
		return null;
	}
	
}
