package com.sqiwy.menu.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by abrysov
 */

public class DBHelper extends SQLiteOpenHelper {
	
	public static final String DB_NAME = "Menu";

    public static final int PRODUCT_SIGNIFICANCE_TEXT = -1;
    public static final int PRODUCT_SIGNIFICANCE_STANDARD = 0;
    public static final int PRODUCT_SIGNIFICANCE_BIG = 1;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}

	
	/* Common fields*/
	public static final String F_ID = "_ID";
	public static final String F_NAME = "name";
	public static final String F_PRICE = "price";
//	public static final String F_REAL_PRICE = "real_price";
	public static final String F_ENABLED = "enabled";
	public static final String F_IMG_URL = "img_url";
	public static final String F_WEIGHT = "weight";
	public static final String F_TYPE = "type";
	public static final String F_FULL_NAME = "full_name";
	public static final String F_DESCRIPTION = "description";
	public static final String F_PARENT_ID = "parent_id";
	public static final String F_SORT_INDEX = "sort_index";
	public static final String F_TIME = "time";
	public static final String F_STAFF_ID = "staff_id";
//	public static final String F_TOTAL = "total";

	
	/* TABLE CATEGORY */
	/**
	 * Product-category table name.
	 * <br/><br/>
	 * Table schema is:
	 * <br/><br/>
	 * <code>
	 * CREATE TABLE [product_category] (<br/>
	 * [_ID] INTEGER  NOT NULL PRIMARY KEY,<br/>
 	 * [parent_id] INTEGER  NULL,<br/>
 	 * [enabled] BOOLEAN DEFAULT '0' NOT NULL,<br/>
 	 * [name] VARCHAR(250) DEFAULT '' NOT NULL,<br/>
 	 * [sort_index] INTEGER DEFAULT '0' NOT NULL,<br/>
 	 * [img_url] VARCHAR(250) DEFAULT '' NOT NULL,<br/>
 	 * [simple_list] INTEGER  NOT NULL);<br/>
 	 * </code>
 	 * <br/><br/>
 	 * Field name can be retrieved through DBHelper class constants:
 	 * <br/><br/>
 	 * <code>
 	 * F_ID<br/>
 	 * F_PARENT_ID<br/>
 	 * F_ENABLED<br/>
 	 * F_NAME<br/>
 	 * F_SORT_INDEX<br/>
 	 * F_IMG_URL<br/>
 	 * F_SIMPLE_LIST<br/>
 	 * </code>
	 */
	public static final String T_PRODUCT_CATEGORY = "product_category";
	public static final String F_SIMPLE_LIST = "simple_list";
	
	/**
	 * Product table name.
	 * <br/><br/>
	 * Table schema is:<br/><br/>
	 * <code>
	 * CREATE TABLE [product] (<br/>
	 * [_ID] INTEGER  NOT NULL PRIMARY KEY,<br/>
	 * [product_category_id] INTEGER DEFAULT '0' NOT NULL,<br/>
	 * [enabled] BOOLEAN DEFAULT '1' NOT NULL,<br/>
	 * [name] VARCHAR(250) DEFAULT '' NOT NULL,<br/>
	 * [price] FLOAT DEFAULT '0.00' NOT NULL,<br/>
	 * [sort_index] INTEGER DEFAULT '0' NOT NULL,<br/>
	 * [img_url] VARCHAR(250) DEFAULT '' NOT NULL,<br/>
	 * [weight] VARCHAR(250) DEFAULT '0' NOT NULL,<br/>
	 * [type] TEXT  NOT NULL,<br/>
	 * [fullname] TEXT  NOT NULL,<br/>
     * [description] TEXT  NOT NULL,<br/>
	 * [significance] INTEGER  NOT NULL)<br/>
 	 * </code>
 	 * <br/><br/>
 	 * Field name can be retrieved through DBHelper class constants:
 	 * <br/><br/>
 	 * <code>
 	 * F_ID<br/>
 	 * F_PRODUCT_CATEGORY_ID<br/>
 	 * F_ENABLED<br/>
 	 * F_NAME<br/>
 	 * F_PRICE
 	 * F_SORT_INDEX
 	 * F_IMG_URL
 	 * F_WEIGHT
 	 * F_TYPE
 	 * F_FULLNAME
     * F_DESCRIPTION
 	 * F_SIGNIFICANCE
 	 * </code>
	 */
	public static final String T_PRODUCT = "product";
	public static final String F_PRODUCT_CATEGORY_ID = "product_category_id";
    public static final String F_SIGNIFICANCE = "significance";

    /**
	 * Modifier group table name.
	 * <br/><br/>
	 * Table schema is:<br/><br/>
	 * <code>
	 * CREATE TABLE [modifier_group] (<br/>
	 * [_ID] INTEGER  NOT NULL PRIMARY KEY,<br/>
	 * [parent_id] INTEGER  NULL,<br/>
	 * [enabled] BOOLEAN DEFAULT '1' NOT NULL,<br/>
	 * [type] INTEGER DEFAULT '0' NOT NULL,<br/>
	 * [name] VARCHAR(250) DEFAULT '' NOT NULL,<br/>
	 * [img_url] VARCHAR(250) DEFAULT '' NOT NULL,<br/>
	 * [sort_index] INTEGER DEFAULT '0' NOT NULL)<br/>
 	 * </code>
 	 * <br/><br/>
 	 * Field name can be retrieved through DBHelper class constants:
 	 * <br/><br/>
 	 * <code>
 	 * F_ID<br/>
 	 * F_PARENT_ID<br/>
 	 * F_ENABLED<br/>
 	 * F_TYPE<br/>
 	 * F_NAME<br/>
 	 * F_IMG_URL<br/>
 	 * F_SORT_INDEX<br/>
 	 * </code>
	 */
	public static final String T_MODIFIER_GROUP = "modofier_group";
	
	/**
	 * Modifier table name.
	 * <br/><br/>
	 * Table schema is:<br/><br/>
	 * <code>
	 * CREATE TABLE [modifier] (<br/>
	 * [_ID] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,<br/>
	 * [modifier_group_id] INTEGER DEFAULT '0' NOT NULL,<br/>
	 * [enabled] BOOLEAN DEFAULT '1' NOT NULL,<br/>
	 * [name] VARCHAR(250) DEFAULT '' NOT NULL,<br/>
	 * [price] FLOAT DEFAULT '0.00' NOT NULL,<br/>
	 * [sort_index] INTEGER DEFAULT '0' NOT NULL,<br/>
	 * [img_url] VARCHAR(250) DEFAULT '' NOT NULL,<br/>
	 * [weight] VARCHAR(250) DEFAULT '' NOT NULL)
 	 * </code>
 	 * <br/><br/>
 	 * Field name can be retrieved through DBHelper class constants:
 	 * <br/><br/>
 	 * <code>
 	 * F_ID<br/>
 	 * F_MODIFIER_GROUP_ID<br/>
 	 * F_MODIFIER_GROUP_ID<br/>
 	 * F_ENABLED<br/>
 	 * F_NAME<br/>
 	 * F_PRICE<br/>
 	 * F_SORT_INDEX<br/>
 	 * F_IMG_URL<br/>
 	 * F_WEIGHT<br/>
 	 * </code>
	 */
	public static final String T_MODIFIER = "modifier";
	public static final String F_MODIFIER_GROUP_ID = "modifier_group_id";
	
	/**
	 * Product-modifier-group table name.
	 * <br/><br/>
	 * Table schema is:<br/><br/>
	 * <code>
	 * CREATE TABLE [product_modifier_group] (<br/>
	 * [_ID] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,<br/>
	 * [product_id] INTEGER DEFAULT '0' NOT NULL,<br/>
	 * [modifier_group_id] INTEGER DEFAULT '0' NOT NULL,<br/>
	 * [sort_index] INTEGER DEFAULT '0' NOT NULL)
 	 * </code>
 	 * <br/><br/>
 	 * Field name can be retrieved through DBHelper class constants:
 	 * <br/><br/>
 	 * <code>
 	 * F_ID<br/>
 	 * F_PRODUCT_ID<br/>
 	 * F_MODIFIER_GROUP_ID<br/>
 	 * F_SORT_INDEX<br/>
 	 * F_REQUIRED<br/>
 	 * </code>
	 */
	public static final String T_PRODUCT_MODIFIER_GROUP = "product_modifier_group";
	public static final String F_PRODUCT_ID = "product_id";
	public static final String F_REQUIRED = "required";

	/**
	 * Order table name.
	 * <br/><br/>
	 * Table schema is:<br/><br/>
	 * <code>
	 * CREATE TABLE [order] (<br/>
	 * [_ID] INTEGER NOT NULL PRIMARY KEY,<br/>
	 * [table_session_id] INTEGER DEFAULT '0' NOT NULL,<br/>
	 * [time] DATETIME DEFAULT '0' NOT NULL,<br/>
	 * [complete] BOOLEAN DEFAULT '0' NOT NULL,<br/>
	 * [staff_id] INTEGER);
 	 * </code>
 	 * <br/><br/>
 	 * Field name can be retrieved through DBHelper class constants:
 	 * <br/><br/>
 	 * <code>
 	 * F_ID<br/>
 	 * F_TABLE_SESSION_ID<br/>
 	 * F_TIME<br/>
 	 * F_COMPLETE<br/>
 	 * F_STAFF_ID<br/>
 	 * </code>
	 */
	public static final String T_ORDER = "orders";
	public static final String F_TABLE_SESSION_ID = "table_session_id";
	public static final String F_COMPLETE = "complete";
	
	
	/**
	 * Order-product table name.
	 * <br/><br/>
	 * Table schema is:<br/><br/>
	 * <code>
	 * CREATE TABLE [order_product] (<br/>
	 * [_ID] INTEGER NOT NULL PRIMARY KEY,<br/>
	 * [order_id] INTEGER DEFAULT '0' NOT NULL,<br/>
	 * [product_id] INTEGER DEFAULT '0' NOT NULL,<br/>
	 * [price] FLOAT DEFAULT '0.00' NOT NULL,<br/>
	 * [modified] BOOLEAN DEFAULT '0' NOT NULL,<br/>
	 * [count] INTEGER DEFAULT '0' NOT NULL);
 	 * </code>
 	 * <br/><br/>
 	 * Field name can be retrieved through DBHelper class constants:
 	 * <br/><br/>
 	 * <code>
 	 * F_ID<br/>
 	 * F_ORDER_ID<br/>
 	 * F_PRODUCT_ID<br/>
 	 * F_PRICE<br/>
 	 * F_MODIFIED<br/>
 	 * F_COUNT<br/>
 	 * </code>
	 */
	public static final String T_ORDER_PRODUCT = "order_product";
	public static final String F_ORDER_ID = "order_id";
	public static final String F_COUNT = "count";
	public static final String F_MODIFIED = "modified";
	
	/**
	 * Order-product-modifier table name.
	 * <br/><br/>
	 * Table schema is:<br/><br/>
	 * <code>
	 * CREATE TABLE [order_product_modifier] (<br/>
	 * [_ID] INTEGER NOT NULL PRIMARY KEY,<br/>
	 * [order_product_id] INTEGER DEFAULT '0' NOT NULL,<br/>
	 * [modifier_id] INTEGER DEFAULT '0' NOT NULL,<br/>
	 * [price] FLOAT DEFAULT '0.00' NOT NULL,<br/>
	 * [count] INTEGER DEFAULT '0' NOT NULL);
 	 * </code>
 	 * <br/><br/>
 	 * Field name can be retrieved through DBHelper class constants:
 	 * <br/><br/>
 	 * <code>
 	 * F_ID<br/>
 	 * F_ORDER_PRODUCT_ID<br/>
 	 * F_MODIFIER_ID<br/>
 	 * F_PRICE<br/>
 	 * F_COUNT<br/>
 	 * </code>
	 */
	public static final String T_ORDER_PRODUCT_MODIFIER = "order_product_modifier";
	public static final String F_ORDER_PRODUCT_ID = "order_product_id";
	public static final String F_MODIFIER_ID = "modifier_id";
	
	/**
	 * Resource table name.
	 * <br/><br/>
	 * Table schema is:<br/><br/>
	 * <code>
	 * CREATE TABLE [resource] (<br/>
	 * [_ID] INTEGER NOT NULL PRIMARY KEY ,<br/>
	 * [big_img_url] VARCHAR(250) DEFAULT '' NOT NULL,<br/>
	 * [small_img_url] VARCHAR(250) DEFAULT '' NOT NULL,<br/>
	 * [info] VARCHAR(250) DEFAULT '' NOT NULL);<br/>
 	 * </code>
 	 * <br/><br/>
 	 * Field name can be retrieved through DBHelper class constants:
 	 * <br/><br/>
 	 * <code>
 	 * F_ID<br/>
 	 * F_BIG_IMG_URL<br/>
 	 * F_SMALL_IMG_URL<br/>
 	 * F_INFO<br/>
 	 * </code>
	 */
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
			+ "." + F_PRODUCT_ID + "=" + T_PRODUCT+ "." + F_ID ;

	public static final String T_PRODUCT_MODIFIER_GROUP_JOIN_MODIFIER_GROUP =
            T_PRODUCT_MODIFIER_GROUP + " INNER JOIN " + T_MODIFIER_GROUP + " ON "
            + T_PRODUCT_MODIFIER_GROUP + "." + F_MODIFIER_GROUP_ID + "="
            + T_MODIFIER_GROUP + "." + F_ID ;

	
	/*
	 * Tables schemas
	 */
	
	private static final String CREATE_PRODUCT_CATEGORY =
			"CREATE TABLE [" + T_PRODUCT_CATEGORY +"] ("
			+ "[" + F_ID + "] INTEGER  NOT NULL PRIMARY KEY,"
			+ "[" + F_PARENT_ID + "] INTEGER  NULL,"
			+ "[" + F_ENABLED + "] BOOLEAN DEFAULT '0' NOT NULL,"
			+ "[" + F_NAME + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_SORT_INDEX + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_IMG_URL + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_SIMPLE_LIST + "] INTEGER  NOT NULL); ";
			
	private static final String CREATE_PRODUCT = 
			"CREATE TABLE [" + T_PRODUCT + "] ("
			+ "[" + F_ID + "] INTEGER  NOT NULL PRIMARY KEY,"
			+ "[" + F_PRODUCT_CATEGORY_ID + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_ENABLED + "] BOOLEAN DEFAULT '1' NOT NULL,"
			+ "[" + F_NAME + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_PRICE + "] FLOAT DEFAULT '0.00' NOT NULL,"
			+ "[" + F_SORT_INDEX + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_IMG_URL + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_WEIGHT + "] VARCHAR(250) DEFAULT '0' NOT NULL,"
			+ "[" + F_TYPE + "] TEXT  NOT NULL,"
			+ "[" + F_FULL_NAME + "] TEXT  NOT NULL,"
			+ "[" + F_DESCRIPTION + "] TEXT  NOT NULL,"
			+ "[" + F_SIGNIFICANCE + "] INTEGER  NOT NULL);";
			
	private static final String CREATE_MODIFIER_GROUP =
			"CREATE TABLE [" + T_MODIFIER_GROUP + "] ("
			+ "[" + F_ID + "] INTEGER  NOT NULL PRIMARY KEY,"
			+ "[" + F_PARENT_ID + "] INTEGER  NULL,"
			+ "[" + F_ENABLED + "] BOOLEAN DEFAULT '1' NOT NULL,"
			+ "[" + F_TYPE + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_NAME + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_IMG_URL + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_SORT_INDEX + "] INTEGER DEFAULT '0' NOT NULL);";
			
	private static final String CREATE_MODIFIER =
			"CREATE TABLE [" + T_MODIFIER + "] ("
			+ "[" + F_ID + "] INTEGER  NOT NULL PRIMARY KEY,"
			+ "[" + F_MODIFIER_GROUP_ID + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_ENABLED + "] BOOLEAN DEFAULT '1' NOT NULL,"
			+ "[" + F_NAME + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_PRICE + "] FLOAT DEFAULT '0.00' NOT NULL,"
			+ "[" + F_SORT_INDEX + "] INTEGER DEFAULT '0' NOT NULL,"
			+ "[" + F_IMG_URL + "] VARCHAR(250) DEFAULT '' NOT NULL,"
			+ "[" + F_WEIGHT + "] VARCHAR(250) DEFAULT '' NOT NULL);";
	
	private static final String CREATE_PRODUCT_MODIFIER_GROUP = 
			"CREATE TABLE [" + T_PRODUCT_MODIFIER_GROUP + "] ("
			+ "[" + F_ID + "] INTEGER  NOT NULL PRIMARY KEY,"
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
			+ "[" + F_ID + "] INTEGER NOT NULL PRIMARY KEY," // <= SHould be autoincrement
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
		
	}

	
}
