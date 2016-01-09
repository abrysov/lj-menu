package com.fosslabs.android.view;

import com.fosslabs.android.jsbase.R;
import com.fosslabs.android.utils.Funs;
import com.fosslabs.android.utils.JSLog;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class JSGalleryAdapter extends BaseAdapter {
	private static final String TAG = "JSGalleryAdapter";

	private Context mContext;
	private JSGallery mGallery = null;
	
	public void setGallery(JSGallery gallery) {
		mGallery = gallery;
	}

	public JSGalleryAdapter(Context context){
		mContext = context;
	}

	private final Integer [] mImage ={
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
	};
	
	@Override
	public int getCount() {
		return mImage.length;
	}

	@Override
	public Object getItem(int position) {
		return mImage[position];
	}

	@Override
	public long getItemId(int position) {
		
		return mImage[position];
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv = new ImageView(mContext);
		iv.setImageResource(mImage[position]);
		iv.setPadding(20, 20, 20, 20);
		iv.setLayoutParams(new JSGallery.LayoutParams(140, 190));
		iv.setScaleType(ImageView.ScaleType.FIT_XY);
		iv.setBackgroundColor(Color.GRAY);
		iv.setLayoutParams(new JSGallery.LayoutParams(50,50));
		return iv;
	}
	

}

