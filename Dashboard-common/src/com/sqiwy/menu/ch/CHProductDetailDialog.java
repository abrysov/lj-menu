package com.sqiwy.menu.ch;

import com.sqiwy.menu.model.Product;
import com.sqiwy.menu.provider.DBHelper;
import com.sqiwy.menu.provider.MenuProvider;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by abrysov
 */

public class CHProductDetailDialog extends DialogFragment implements 
	LoaderManager.LoaderCallbacks<Cursor>{
	private static final String TAG = "CMProductDetailDialog";
	private static final String EXTRA_ID_PRODUCT = "EXTRA_ID_PRODUCT";
	private static final int LOAD_PRODUCT = 0x00;
	private Product mProduct;
	
	private class ViewHolder{
		Button btn_close;
		TextView tv_name;
		ImageView iv;
		LinearLayout ll_modifiers;
		TextView tv_modifiers;
		ListView lv_modifiers;
		TextView tv_content_title;
		TextView tv_content;
		TextView tv_weight_calories;
		Button btn_add;
	}
	private ViewHolder vh = new ViewHolder();
	
	public static CHProductDetailDialog newInstance(int id_product) {
		Bundle args = new Bundle();
		args.putInt(EXTRA_ID_PRODUCT, id_product);
		CHProductDetailDialog fragment = new CHProductDetailDialog();
		fragment.setArguments(args);
		return fragment;
		}
		
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int id_product = getArguments().getInt(EXTRA_ID_PRODUCT);
		Bundle bundle = new Bundle();
		bundle.putInt(EXTRA_ID_PRODUCT, id_product);
		getLoaderManager().restartLoader(LOAD_PRODUCT, bundle, this);
		
		return super.onCreateDialog(savedInstanceState);
	}
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		return null;
	}
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
}
