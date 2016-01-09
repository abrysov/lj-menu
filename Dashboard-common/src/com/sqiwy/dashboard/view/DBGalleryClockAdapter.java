package com.sqiwy.dashboard.view;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fosslabs.android.model.Clock;
import com.fosslabs.android.utils.JSImageWorker;
import com.fosslabs.android.utils.JSLog;
import com.fosslabs.android.view.JSClock;
import com.fosslabs.android.view.JSGallery;
import com.sqiwy.dashboard.R;

/**
 * Created by abrysov
 */

public class DBGalleryClockAdapter extends ArrayAdapter<Clock> {
	private static final String TAG = "JSGalleryClockAdapter";
	
	private Activity mContext;
	private ArrayList<Clock> mList = new ArrayList<Clock>();
	private int mRes;
	private int mGalleryWidth;
	private int mGalleryHeight;
	private Drawable mBg;
	
	private class ViewHolder{
		FrameLayout fl_clock;
		TextView tv_city;
		//TextView tv_time;
	}
	
	public DBGalleryClockAdapter(Activity context, int resource,
			ArrayList<Clock> objects, int gallery_width, int gallery_height) {
		super(context, resource, objects);
		mContext = context;
		mList = objects;
		mRes = resource; //we use R.layout.gallery_item_clock_fragment
		mGalleryWidth = gallery_width;
		mGalleryHeight = gallery_height;
		mBg = mContext.getResources().getDrawable(R.drawable.logo_clock);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	
	public Clock getItem(int position) {
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
			vh.fl_clock = (FrameLayout) rowView.findViewById(R.id.fl_clock);
			vh.tv_city = (TextView) rowView.findViewById(R.id.tv_city);
			//vh.tv_time = (TextView) rowView.findViewById(R.id.tv_time);
			rowView.setTag(vh);
		}
		else{
			vh = (ViewHolder) rowView.getTag();
		}
		
		//vh.tv_time.setText(mList.get(position).getTime());
		vh.tv_city.setText(mList.get(position).getCity(mContext));
		vh.tv_city.setTextColor(Color.WHITE);
		vh.fl_clock.removeAllViews();
		//Clock_Widget_height to Clock_City_Title_height = 3 to 1;
		
		int size = mGalleryHeight * 3 / 5 ;
		JSLog.d(TAG, "SIZE FOR CLOCK " + size);
		JSClock clock = new JSClock(mContext, size/2, size/2,
				mList.get(position).getDateTime(), size/2 * 8/15);
		vh.fl_clock.addView(clock);
		vh.fl_clock = (FrameLayout) JSImageWorker.setBackground(vh.fl_clock, 
				mBg);
		vh.fl_clock.setLayoutParams(new LinearLayout.LayoutParams(size, size));
		
		/*ImageView iv = new ImageView(mContext);
		iv.setImageResource(R.drawable.ic_launcher);
		iv.setPadding(20, 20, 20, 20);
		iv.setLayoutParams(new JSGallery.LayoutParams(140, 190));
		iv.setScaleType(ImageView.ScaleType.FIT_XY);
		iv.setBackgroundColor(Color.GRAY);
		iv.setLayoutParams(new JSGallery.LayoutParams(50,50));
		return iv;*/
		rowView.setLayoutParams(new JSGallery.LayoutParams(mGalleryWidth / 3, mGalleryHeight));
		return rowView;
	}

	public Drawable getBg() {
		return mBg;
	}

	public void setBg(Drawable bg) {
		mBg = bg;
	}
	

}
