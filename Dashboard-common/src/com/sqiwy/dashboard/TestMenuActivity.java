package com.sqiwy.dashboard;

import com.fosslabs.android.utils.JSLog;
import com.sqiwy.menu.provider.DBHelper;
import com.sqiwy.menu.provider.MenuProvider;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;

/**
 * Created by abrysov
 */

public class TestMenuActivity extends Activity{
	private static final String TAG="TestMenuActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cafe);
		JSLog.d(TAG, " onCreate ");
		 String [] mContent = new String [] {
				   DBHelper.F_SERVER_ID,
				   DBHelper.F_PARENT_SERVER_ID,
				   DBHelper.F_ENABLED,
				   DBHelper.F_NAME,
				   DBHelper.F_SORT_INDEX,
				   DBHelper.F_IMG_URL,
				   DBHelper.F_SIMPLE_LIST
				  };
		Cursor c = getContentResolver().query(MenuProvider.URI_PRODUCT_CATEGORY, null, null,null, null);
		JSLog.d(TAG, "c.getCount() " + c.getCount());
	}
}
