package com.sqiwy.dashboard.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fosslabs.android.utils.AnimFragment;
import com.fosslabs.android.utils.JSImageWorker;
import com.fosslabs.android.utils.JSLog;
import com.fosslabs.android.view.MosaicAdapter;
import com.sqiwy.dashboard.R;
import com.sqiwy.dashboard.model.TileData;
import com.sqiwy.dashboard.model.TileData.TileDataType;

/**
 * Created by abrysov
 */

public class TileViewMosaicAdapter extends MosaicAdapter{
	private static final String TAG = "TileViewMosaicAdapter";
	
	private static int mResId = R.layout.tile_item;
	private static int mResLandId = R.layout.tile_item_land;
	private static int mResCommercialId = R.layout.tile_item_commercial;
	private static int mResCommercialVideoId = R.layout.tile_item_commercial_video;
	private static int mResClockId = R.layout.tile_item_clock;
	private static int mResMagasinId = R.layout.tile_item_magasin;
	
	private AnimFragment mAnimFragment = null;
	private Context mContext;

	public TileViewMosaicAdapter(Context context, int columnCount) {
		super(context, mResId, columnCount);
		mContext = context;
	}
	
	public AnimFragment getAnimFragment() {
		return mAnimFragment;
	}

	public void setAnimFragment(AnimFragment animFragment) {
		mAnimFragment = animFragment;
	}
	
	@Override
	public View createView(int position, View convertView,
			ViewGroup parent, ArrayList<Object> objects) {
		JSLog.d(TAG, "CREATEvIEW");
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// .getLayoutInflater();
		TileData td = (TileData) objects.get(position);
		JSLog.d(TAG, "td.getType() " + td.getType());
		
		
		switch(td.getType()){
		case TileDataType.TYPE_GALLERY_CLOCK:{
			View v = inflater.inflate(mResClockId, null);
			View child =  v.findViewById(R.id.fragment_gallery_clock);
			Drawable d =  null;
			
			if (!TextUtils.isEmpty(td.getBackgroundPath()) && null != (d = td.getBackground(mContext))) {
				JSImageWorker.setBackground(v, d);
			}
			//else JSImageWorker.setBackground(v, new ColorDrawable(JSImageWorker.getRandomColor(255)));
			child.setTag(td);
			return v;
		}
		
		case TileDataType.TYPE_GALLERY_IMAGE:{

			View v = inflater.inflate(mResCommercialId, null);
			View child = v.findViewById(R.id.fragment_commercial_gallery);
			Drawable d =  null;
			
			if (!TextUtils.isEmpty(td.getBackgroundPath()) && null != (d = td.getBackground(mContext))) {
				JSImageWorker.setBackground(v, d);
			}
			child.setTag(td);
			return v;
		}
		case TileDataType.TYPE_GALLERY_MAGASIN:{
			View v = inflater.inflate(mResMagasinId, null);
			View child = v.findViewById(R.id.fragment_magasin_gallery);
			Drawable d =  null;
			
			if (!TextUtils.isEmpty(td.getBackgroundPath()) && null != (d = td.getBackground(mContext))) {
				JSImageWorker.setBackground(v, d);
			}
			child.setTag(td);
			return v;
		}
		case TileDataType.TYPE_VIDEO:{
			View v = inflater.inflate(mResCommercialVideoId, null);
			v.setTag(td);
			return v;
		}
		case TileDataType.TYPE_STATIC:{
			boolean land = td.isLandLayoutOrientation();
			View v = inflater.inflate(land ? mResLandId : mResId, null);
			
			TextView tv = (TextView) v.findViewById(R.id.tv_td);
			tv.setText(td.getName());
			
			if (0 < td.getTextSize()) {
				tv.setTextSize(td.getTextSize() * mContext.getResources().getDisplayMetrics().density);
			} else {
				tv.setVisibility(View.GONE);
			}
			
			Drawable d = null;
			
			if (!TextUtils.isEmpty(td.getBackgroundPath()) && null != (d = td.getBackground(mContext))) {
				JSImageWorker.setBackground(v, d);
			}

			ImageView iv = (ImageView) v.findViewById(R.id.iv_td);
			d =  td.getColorLogo(mContext);

			if (d!= null) {
				iv.setImageDrawable(d);
				if (td.getIvMetrica() > 0) {

					if (View.VISIBLE != tv.getVisibility()) {
						
						iv.setLayoutParams(new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.MATCH_PARENT,
									LinearLayout.LayoutParams.MATCH_PARENT));
						
						iv.setScaleType(ScaleType.CENTER_CROP);
						
					} else {
					
						if (!land) {
							
							iv.setLayoutParams(new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.WRAP_CONTENT, td.getIvMetrica()));
						} else {
							
							iv.setLayoutParams(new LinearLayout.LayoutParams(
									td.getIvMetrica(), LinearLayout.LayoutParams.WRAP_CONTENT));
						}
					}
				}
			} else {
				iv.setVisibility(View.GONE);
			}
			
			v.setTag(td);
			return v;
		}
		default: return null;
		}		
	}
	@Override
	protected void setChildBg(View v, int position, ArrayList<Object> objects, int reqWidth, int reqHeight) {
		JSLog.d(TAG, "getChildBg " + position);
	}
}
