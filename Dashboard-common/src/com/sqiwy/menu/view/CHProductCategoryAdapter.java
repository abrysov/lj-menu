package com.sqiwy.menu.view;

import java.util.ArrayList;

import com.fosslabs.android.utils.JSLog;
import com.sqiwy.menu.model.ProductCategory;
import com.sqiwy.menu.model.WeightBlock;
import com.sqiwy.menu.model.WeightBlockCH;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by abrysov
 */

public class CHProductCategoryAdapter extends BaseExpandableListAdapter{
	private static final String TAG = "CHProductCategoryAdapter";
	private ArrayList<ProductCategory> mList;
	private Context mContext;
		
	public CHProductCategoryAdapter(Context context, ArrayList<ProductCategory> list){
		mContext = context;
		if(list == null) mList = new ArrayList<ProductCategory>();
		else mList = list;
	}
	
	@Override
	public int getChildTypeCount() {
		return WeightBlockCH.WeightBlockType.values().length;
	}
	
	@Override
	public int getChildType(int groupPosition, int childPosition) {
		return mList.get(groupPosition).getListWeightBlock().get(childPosition).getType();
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mList.get(groupPosition).getListWeightBlock().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		View rowView = convertView;
		
		final int type = getChildType(groupPosition, childPosition);
		
		if (null == rowView || type != ((CHWeightBlockViewHolder) rowView.getTag()).getType()) {
			rowView = CHWeightBlockViewHolder.createView(mContext, childPosition, type, parent);
		}
		
		// Get data to populate
		WeightBlock wb = (WeightBlock) getChild(groupPosition, childPosition);

		// Populate data (concrete holder knows how to populate)
		((CHWeightBlockViewHolder) rowView.getTag()).populate(wb);
		rowView.setFocusable(false);
		return rowView;
		//TextView tv = new TextView(mContext);
		//tv.setText(mList.get(groupPosition).getListWeightBlock().get(childPosition).toString());
		//return tv;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mList.get(groupPosition).getListWeightBlock().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		JSLog.d(TAG, "getGroupView " +groupPosition);
		CheckedTextView tv = new CheckedTextView(mContext);
		tv.setText(mList.get(groupPosition).getName());
		tv.setChecked(isExpanded);
		tv.setGravity(Gravity.CENTER);
		tv.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, 60));
		return tv;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
