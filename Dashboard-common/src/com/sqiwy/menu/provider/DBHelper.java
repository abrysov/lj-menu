package com.sqiwy.menu.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.sqiwy.menu.resource.ResourcesManager;

/**
 * Created by abrysov
 */

public class DBHelper extends SQLiteOpenHelper {
	
	public static final String DB_NAME = "Menu";

    public static final int PRODUCT_SIGNIFICANCE_TEXT = -1;
    public static final int PRODUCT_SIGNIFICANCE_STANDARD = 0;
    public static final int PRODUCT_SIGNIFICANCE_BIG = 1;
    
    private Context mContext;
    
	public DBHelper(Context context) {
		super(context, DB_NAME, null, 2);
		mContext = context;
	}
	
	/* Common fields*/
	public static final String F_ID = "_ID";
	public static final String F_SERVER_ID = "server_id";
	public static final String F_LANG = "lang";
	public static final String F_NAME = "name";
	public static final String F_PRICE = "price";
	public static final String F_ENABLED = "enabled";
	public static final String F_IMG_URL = "img_url";
	public static final String F_WEIGHT = "weight";
	public static final String F_TYPE = "type";
	public static final String F_FULL_NAME = "full_name";
	public static final String F_DESCRIPTION = "description";
	public static final String F_PARENT_SERVER_ID = "parent_id";
	public static final String F_SORT_INDEX = "sort_index";
	public static final String F_TIME = "time";
	public static final String F_STAFF_ID = "staff_id";
	public static final String F_CALORICITY="caloricity";
	public static final String T_PRODUCT_CATEGORY = "product_category";
	public static final String F_SIMPLE_LIST = "simple_list";
	public static final String T_PRODUCT = "product";
	public static final String F_PRODUCT_CATEGORY_ID = "product_category_id";
    public static final String F_SIGNIFICANCE = "significance";
	public static final String T_MODIFIER_GROUP = "modofier_group";
	public static final String T_MODIFIER = "modifier";
	public static final String F_MODIFIER_GROUP_ID = "modifier_group_id";
	public static final String T_PRODUCT_MODIFIER_GROUP = "product_modifier_group";
	public static final String F_PRODUCT_ID = "product_id";
	public static final String F_REQUIRED = "required";
	public static final String T_ORDER = "orders";
	public static final String F_TABLE_SESSION_ID = "table_session_id";
	public static final String F_COMPLETE = "complete";
	public static final String T_ORDER_PRODUCT = "order_product";
	public static final String F_ORDER_ID = "order_id";
	public static final String F_COUNT = "count";
	public static final String F_MODIFIED = "modified";
	public static final String T_ORDER_PRODUCT_MODIFIER = "order_product_modifier";
	public static final String F_ORDER_PRODUCT_ID = "order_product_id";
	public static final String F_MODIFIER_ID = "modifier_id";
	public static final String T_RESOURCE = "resource";
	public static final String F_PACKAGE_URL = "package_url";
	public static final String F_CODE = "code";
	
	/*
	 * Joined tables
	 */
	public static final String T_ORDER_PRODUCT_JOIN_ORDER_PRODUCT_MODIFIER = 
			T_ORDER_PRODUCT + " INNER JOIN " + T_ORDER_PRODUCT_MODIFIER + " ON " + T_ORDER_PRODUCT 
			+ "." + F_ID + "=" + T_ORDER_PRODUCT_MODIFIER + "." + F_ORDER_PRODUCT_ID;
	
	public static final String T_ORDER_JOIN_ORDER_PRODUCT = 
			T_ORDER + " INNER JOIN " + T_ORDER_PRODUCT + " ON " + T_ORDER 
			+ "." + F_ID + "=" + T_ORDER_PRODUCT+ "." + F_ORDER_ID;

	public static final String T_ORDER_PRODUCT_JOIN_PRODUCT = 
			T_ORDER_PRODUCT + " INNER JOIN " + T_PRODUCT + " ON " + T_ORDER_PRODUCT 
			+ "." + F_PRODUCT_ID + "=" + T_PRODUCT+ "." + F_SERVER_ID;

	public static final String T_PRODUCT_MODIFIER_GROUP_JOIN_MODIFIER_GROUP =
            T_PRODUCT_MODIFIER_GROUP + " INNER JOIN " + T_MODIFIER_GROUP + " ON "
            + T_PRODUCT_MODIFIER_GROUP + "." + F_MODIFIER_GROUP_ID + "="
            + T_MODIFIER_GROUP + "." + F_SERVER_ID 
            + " AND " + T_PRODUCT_MODIFIER_GROUP 
			+ "." + F_LANG + "=" + T_MODIFIER_GROUP + "." + F_LANG ;
	
	public static final String T_ORDER_PRODUCT_MODIFIER_JOIN_MODIFIER = 
			T_ORDER_PRODUCT_MODIFIER + " INNER JOIN " + T_MODIFIER + " ON "
			+ T_ORDER_PRODUCT_MODIFIER + "." + F_MODIFIER_ID + "="
			+ T_MODIFIER + "." + F_SERVER_ID;

	public static final String T_FUCKING_TABLE = "fucking_table";
	public static final String T_FUCKING_TABLE_SCHEMA = 
			"SELECT " + F_ORDER_PRODUCT_ID + " AS " + F_SERVER_ID + " FROM (SELECT *, sum(ncopm.cnt)"
			+ " as scnt "
			+ " FROM (" + T_ORDER_PRODUCT + " INNER JOIN " + T_ORDER
			+ " ON " + T_ORDER_PRODUCT + "." + F_ORDER_ID + "=" + T_ORDER + "." + F_ID + ")"
			+ " as op, "
			+ " (SELECT *,  count(" + F_ID + ") "
			+ " as cnt "
			+ " FROM " + T_ORDER_PRODUCT_MODIFIER + " as opm1 "
			+ " WHERE opm1." + F_MODIFIER_ID + " in ( %1$s ) GROUP BY " + F_ORDER_PRODUCT_ID
			+ " ) as copm LEFT OUTER JOIN "
			+ " (SELECT *,  count(" + F_ID + ") as cnt "
			+ " FROM " + T_ORDER_PRODUCT_MODIFIER + " as opm2 "
			+ " WHERE NOT(opm2." + F_MODIFIER_ID + " in ( %2$s )) GROUP BY " + F_ORDER_PRODUCT_ID
			+ " ) as ncopm "
			+ " ON op." + F_ID + " = ncopm." + F_ORDER_PRODUCT_ID
			+ " WHERE op." + F_COMPLETE + "=0 AND copm." + F_ORDER_PRODUCT_ID + " =op." + F_ID
            + " AND op." + F_PRODUCT_ID + "= %3$s and copm.cnt= %4$s GROUP BY op." + F_ID + ")"
			+ " as cs"
			+ " WHERE  typeof(scnt) = 'null' ";
	
	/*
	 * Tables schemas
	 */
	
	private static final String CREATE_PRODUCT_CATEGORY =
			"CREATE TABLE [" + T_PRODUCT_CATEGORY +"] ("
			+ "[" + F_ID + "] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "[" + F_SERVER_ID + "] INTEGER  NOT NULL,"
			+ "[" + F_PARENT_SERVER_ID + "] INTEGER  NULL,"
			+ "[" + F_ENABLED + "] BOOLEAN DEFAULT '0' NOT NULL,"
			+ "[" + F_NAME + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_LANG + "] VARCHAR(2) DEFAULT 'EN' NOT NULL,"
			+ "[" + F_SORT_INDEX + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_IMG_URL + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_SIMPLE_LIST + "] INTEGER  NOT NULL); ";
			
	private static final String CREATE_PRODUCT = 
			"CREATE TABLE [" + T_PRODUCT + "] ("
			+ "[" + F_ID + "] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "[" + F_SERVER_ID + "] INTEGER  NOT NULL,"
			+ "[" + F_PRODUCT_CATEGORY_ID + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_ENABLED + "] BOOLEAN DEFAULT '1' NOT NULL,"
			+ "[" + F_NAME + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_LANG + "] VARCHAR(2) DEFAULT 'EN' NOT NULL,"
			+ "[" + F_PRICE + "] FLOAT DEFAULT '0.00' NOT NULL,"
			+ "[" + F_SORT_INDEX + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_IMG_URL + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_WEIGHT + "] VARCHAR(250) DEFAULT '0' NOT NULL,"
			+ "[" + F_TYPE + "] TEXT  NOT NULL,"
			+ "[" + F_FULL_NAME + "] TEXT  NOT NULL,"
			+ "[" + F_DESCRIPTION + "] TEXT  NOT NULL,"
			+ "[" + F_SIGNIFICANCE + "] INTEGER  NOT NULL,"
			+ "[" + F_CALORICITY + "] TEXT DEFAULT '' NOT NULL);";
			
	private static final String CREATE_MODIFIER_GROUP =
			"CREATE TABLE [" + T_MODIFIER_GROUP + "] ("
			+ "[" + F_ID + "] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "[" + F_SERVER_ID + "] INTEGER  NOT NULL,"
			+ "[" + F_PARENT_SERVER_ID + "] INTEGER  NULL,"
			+ "[" + F_ENABLED + "] BOOLEAN DEFAULT '1' NOT NULL,"
			+ "[" + F_TYPE + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_NAME + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_LANG + "] VARCHAR(2) DEFAULT 'EN' NOT NULL,"
			+ "[" + F_IMG_URL + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_SORT_INDEX + "] INTEGER DEFAULT '0' NOT NULL);";
			
	private static final String CREATE_MODIFIER =
			"CREATE TABLE [" + T_MODIFIER + "] ("
			+ "[" + F_ID + "] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "[" + F_SERVER_ID + "] INTEGER  NOT NULL,"
			+ "[" + F_MODIFIER_GROUP_ID + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_ENABLED + "] BOOLEAN DEFAULT '1' NOT NULL,"
			+ "[" + F_NAME + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_LANG + "] VARCHAR(2) DEFAULT 'EN' NOT NULL,"
			+ "[" + F_PRICE + "] FLOAT DEFAULT '0.00' NOT NULL,"
			+ "[" + F_SORT_INDEX + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_IMG_URL + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_WEIGHT + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_CALORICITY + "] TEXT DEFAULT '' NOT NULL);";
	
	private static final String CREATE_PRODUCT_MODIFIER_GROUP = 
			"CREATE TABLE [" + T_PRODUCT_MODIFIER_GROUP + "] ("
			+ "[" + F_ID + "] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "[" + F_SERVER_ID + "] INTEGER  NOT NULL,"
			+ "[" + F_LANG + "] VARCHAR(2) DEFAULT 'EN' NOT NULL,"
			+ "[" + F_PRODUCT_ID + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_MODIFIER_GROUP_ID + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_SORT_INDEX + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_REQUIRED + "] INTEGER DEFAULT '0' NOT NULL);";

	private static final String CREATE_ORDER = 
			"CREATE TABLE [" + T_ORDER +"] ("
			+ "[" + F_ID + "] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "[" + F_TABLE_SESSION_ID + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_TIME + "] DATETIME DEFAULT '0' NOT NULL,"
			+ "[" + F_COMPLETE + "] BOOLEAN DEFAULT '0' NOT NULL,"
			+ "[" + F_STAFF_ID +"] INTEGER);";
	
	private static final String CREATE_ORDER_PRODUCT = 
			"CREATE TABLE [" + T_ORDER_PRODUCT + "] ("
			+ "[" + F_ID + "] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "[" + F_ORDER_ID + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_PRODUCT_ID + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_PRICE + "] FLOAT DEFAULT '0.00' NOT NULL,"
			+ "[" + F_MODIFIED + "] BOOLEAN DEFAULT '0' NOT NULL,"
			+ "[" + F_COUNT + "] INTEGER DEFAULT '0' NOT NULL);";
			
	private static final String CREATE_ORDER_PRODUCT_MODIFIER =
			"CREATE TABLE [" + T_ORDER_PRODUCT_MODIFIER +"] ("
			+ "[" + F_ID + "] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "[" + F_ORDER_PRODUCT_ID + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_MODIFIER_ID + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_PRICE + "] FLOAT DEFAULT '0.00' NOT NULL,"
			+ "[" + F_COUNT + "] INTEGER DEFAULT '0' NOT NULL);";
			
	private static final String CREATE_RESOURCE = 
			"CREATE TABLE [" + T_RESOURCE + "] ("
			+ "[" + F_ID + "] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "[" + F_SERVER_ID + "] INTEGER  NOT NULL,"
			+ "[" + F_LANG + "] VARCHAR(2) DEFAULT 'EN' NOT NULL,"
			+ "[" + F_PACKAGE_URL + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_CODE + "] VARCHAR(250) DEFAULT '' NOT NULL);";
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_PRODUCT_CATEGORY);
		database.execSQL(CREATE_PRODUCT);
		database.execSQL(CREATE_MODIFIER_GROUP);
		database.execSQL(CREATE_MODIFIER);
		database.execSQL(CREATE_PRODUCT_MODIFIER_GROUP);
		database.execSQL(CREATE_ORDER);
		database.execSQL(CREATE_ORDER_PRODUCT);
		database.execSQL(CREATE_ORDER_PRODUCT_MODIFIER);
		database.execSQL(CREATE_RESOURCE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		ResourcesManager.setResourcesLoaded(mContext, false);
		if (oldVersion == 1 && newVersion >= 2) {
			try { 
				database.execSQL("ALTER TABLE "+T_PRODUCT+" ADD COLUMN ["+F_CALORICITY+"] TEXT DEFAULT '' NOT NULL"); 
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try { 
				database.execSQL("ALTER TABLE "+T_MODIFIER+" ADD COLUMN ["+F_CALORICITY+"] TEXT DEFAULT '' NOT NULL"); 
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		database.beginTransaction();
		database.delete(T_PRODUCT_CATEGORY, null, null);
		database.delete(T_PRODUCT, null, null);
		database.delete(T_MODIFIER_GROUP, null, null);
		database.delete(T_MODIFIER, null, null);
		database.delete(T_PRODUCT_MODIFIER_GROUP, null, null);
		database.setTransactionSuccessful(); database.endTransaction();
	}
}
