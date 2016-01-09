package com.sqiwy.menu.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fosslabs.android.utils.JSImageWorker;
import com.fosslabs.android.utils.JSLog;
import com.sqiwy.dashboard.R;
import com.sqiwy.menu.model.ProductCategory;

import java.util.ArrayList;

/**
 * Created by abrysov
 */

public class ProductCategoryAdapter extends ArrayAdapter<ProductCategory> {
	private static final String TAG = " ProductCategoryAdapter";
	private static final int ANIMATION_DURATION_BASE = 150;
	
	private Context mContext;
	private int mResId;
	private ArrayList<ProductCategory> mList = new ArrayList<ProductCategory>();
	private int mSelectedId = -1;
	private View mSelectedView = null;
	
	private class ViewHolder{
		LinearLayout ll_pc;
		TextView tv_pc;
	}
	
	public ProductCategoryAdapter(Context context, int resource,
			ArrayList<ProductCategory> objects, int selected_id) {
		super(context, resource, objects);
		mContext = context;
		mResId = resource;
		mList = objects;
		mSelectedId = selected_id;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			JSLog.d(TAG, "rowView == null");

			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// .getLayoutInflater();
			rowView = inflater.inflate(mResId, null);
			final ViewHolder vh = new ViewHolder();
			vh.ll_pc = (LinearLayout) rowView.findViewById(R.id.layout_category);
			vh.tv_pc = (TextView) rowView.findViewById(R.id.tv_category);
			rowView.setTag(vh);
			
		} else {
			JSLog.d(TAG, "rowView != null");
			
		}
				
		ViewHolder vh = (ViewHolder) rowView.getTag();
		ProductCategory pc = mList.get(position);
		vh.tv_pc.setText(pc.getName());

		Resources res = mContext.getResources();
		rowView.setLayoutParams(new ListView.LayoutParams(
				res.getDimensionPixelSize(R.dimen.cm_fragment_product_category_list_width),
				res.getDimensionPixelSize(R.dimen.cm_fragment_product_category_list_item_height)));
		if(pc.getId() == mSelectedId) {
			vh.ll_pc = (LinearLayout) JSImageWorker.setBackground(vh.ll_pc, mContext.getResources().getDrawable(R.drawable.menu_list_active));
			vh.tv_pc.setTextColor(Color.WHITE);
		}
		else{
			
			vh.ll_pc = (LinearLayout) JSImageWorker.setBackground(vh.ll_pc, mContext.getResources().getDrawable(R.drawable.menu_list));
			vh.tv_pc.setTextColor(Color.BLACK);
		}
		rowView.setTag(vh);
		
		if(pc.getId() == mSelectedId) mSelectedView = rowView;
		return rowView;
	}
	

	
	@Override
	public int getCount() {
		return mList.size();
	}

	public void removePos (int pos){
		JSLog.d(TAG, "removePos " + pos + " " + mList.size());
		mList.remove(pos);
	}

	public int getSelectedPos() {
		return mSelectedId;
	}

	public void setSelected(int selectedPos, View view) {
		mSelectedId = selectedPos;
		setSelectedView(view);
	}

	public View getSelectedView() {
		return mSelectedView;
	}

	private void setSelectedView(View selectedView) {
		if(mSelectedView != null){
			ViewHolder vh = (ViewHolder) mSelectedView.getTag();
			vh.ll_pc = (LinearLayout) JSImageWorker.setBackground(vh.ll_pc, mContext.getResources().getDrawable(R.drawable.menu_list));
			vh.tv_pc.setTextColor(Color.BLACK);
		}
		mSelectedView = selectedView;
		if(mSelectedView != null){
			ViewHolder vh = (ViewHolder) mSelectedView.getTag();
			vh.ll_pc = (LinearLayout) JSImageWorker.setBackground(vh.ll_pc, mContext.getResources().getDrawable(R.drawable.menu_list_active));
			vh.tv_pc.setTextColor(Color.WHITE);
		}
	}
	
	
	

	
}

