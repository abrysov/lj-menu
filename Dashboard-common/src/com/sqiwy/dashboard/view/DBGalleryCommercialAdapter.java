package com.sqiwy.dashboard.view;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fosslabs.android.utils.JSLog;
import com.fosslabs.android.view.JSGallery;
import com.sqiwy.dashboard.R;
import com.sqiwy.dashboard.model.CommercialData;

/**
 * Created by abrysov
 */

public class DBGalleryCommercialAdapter extends ArrayAdapter<CommercialData> {
	private static final String TAG = "DBGalleryCommercialAdapter";
	
	private Activity mContext;
	private ArrayList<CommercialData> mList = new ArrayList<CommercialData>();
	private int mRes;
	private int mGalleryWidth;
	private int mGalleryHeight;
	private float mProportion; //iv height to gallery height
	private int mCountVisibleItem;
	
	private class ViewHolder{
		ImageView iv_com;
		TextView tv_com;
	}
	
	public DBGalleryCommercialAdapter(Activity context, int resource,
			ArrayList<CommercialData> objects, int gallery_width, int gallery_height, 
			float proportion, int countVisibleItem) {
		super(context, resource, objects);
		mContext = context;
		mList = objects;
		mRes = resource; //we use R.layout.gallery_item_commercial_image
		mGalleryWidth = gallery_width;
		mGalleryHeight = gallery_height;
		mProportion = proportion;
		mCountVisibleItem = countVisibleItem > 0 ? countVisibleItem : 1;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	
	public CommercialData getItem(int position) {
		return mList.get(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(mList.size() == 0) return null;
		
		View rowView = convertView;
		ViewHolder vh;
		if(rowView == null || rowView.getTag() == null){
			LayoutInflater inflater = mContext.getLayoutInflater();
			rowView = inflater.inflate(mRes, null);
			vh = new ViewHolder();
			vh.iv_com = (ImageView) rowView.findViewById(R.id.iv_com);
			vh.tv_com = (TextView) rowView.findViewById(R.id.tv_com);
			rowView.setTag(vh);
		}
		else{
			vh = (ViewHolder) rowView.getTag();
		}
		
		CommercialData cd = mList.get(position);
		
		if(mProportion == 1){
			vh.tv_com.setVisibility(View.GONE);
			vh.iv_com.setLayoutParams(new 
					LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT));
			vh.iv_com.setScaleType(ScaleType.FIT_XY);
			
		}
		else{
			int size = (int)( mGalleryHeight * mProportion);
			
			JSLog.d(TAG, mGalleryHeight + " " + mProportion + " " + size );
			vh.iv_com.setLayoutParams(new 
					LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
							size));
			vh.iv_com.setScaleType(ScaleType.FIT_CENTER);
			vh.tv_com.setText(cd.getName());
			vh.tv_com.setTextColor(Color.DKGRAY);
			vh.tv_com.setTextSize(16);
		}
		vh.iv_com.setImageDrawable(cd.getBackground(mContext));
		rowView.setLayoutParams(new JSGallery.LayoutParams(mGalleryWidth / mCountVisibleItem, mGalleryHeight));
		
		return rowView;
	}

}
