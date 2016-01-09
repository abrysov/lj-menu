package com.sqiwy.dashboard;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.fosslabs.android.utils.JSLog;
import com.sqiwy.dashboard.model.ActionTile;
import com.sqiwy.dashboard.model.CommercialData;
import com.sqiwy.dashboard.model.action.Action;
import com.sqiwy.dashboard.util.AnimUtils;
import com.sqiwy.dashboard.view.DBGalleryCommercialAdapter;

/**
 * Created by abrysov
 */

/**
 * TODO: this is only temporary fix for the magazines issue, though it seems to work better
 * than original <code>DBGalleryCommercialFragment</code> for magazines.
 */
public class DBGalleryCommercialTempFragment extends ActionFragment {
	private static final String TAG = "DBGalleryCommercialFragment";
	private static final String EXTRA_GALLERY_WIDTH = "EXTRA_GALLERY_WIDTH";
	private static final String EXTRA_GALLERY_HEIGHT = "EXTRA_GALLERY_HEIGHT";
	private static final String EXTRA_COMMERCIAL_DATA = "EXTRA_COMMERCIAL_DATA";
	private static final String EXTRA_COUNT_VISIBLE = "EXTRA_COUNT_VISIBLE";
	private static final String EXTRA_PROPORCIONAL_IV = "EXTRA_PROPORCIONAL_IV";
	private static final String EXTRA_GALLERY_TOUCH = "EXTRA_GALLERY_TOUCH";
	private static final String EXTRA_GALLERY_UPDATE = "EXTRA_GALLERY_UPDATE";
	private static final String ACTION_NAME = "com.sqiwy.dashboard.dbcommercialbroadcastreceiver";
	private int mGalleryWidth;
	private int mGalleryHeight;
	private int mGallerySpacing;
	private int mCountVisible;
	private float mProporcional;

	public static DBGalleryCommercialTempFragment newInstance(int gallery_width,
			int gallery_height, ArrayList<CommercialData> list,
			int count_visible, float proporcional_iv, boolean gallery_touch,
			boolean gallery_update) {
		Bundle args = new Bundle();
		args.putInt(EXTRA_GALLERY_WIDTH, gallery_width);
		args.putInt(EXTRA_GALLERY_HEIGHT, gallery_height);
		args.putSerializable(EXTRA_COMMERCIAL_DATA, list);
		args.putInt(EXTRA_COUNT_VISIBLE, count_visible);
		args.putFloat(EXTRA_PROPORCIONAL_IV, proporcional_iv);
		args.putBoolean(EXTRA_GALLERY_TOUCH, gallery_touch);
		args.putBoolean(EXTRA_GALLERY_UPDATE, gallery_update);

		DBGalleryCommercialTempFragment fragment = new DBGalleryCommercialTempFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mGalleryWidth = getArguments().getInt(EXTRA_GALLERY_WIDTH, 0);
		mGalleryHeight = getArguments().getInt(EXTRA_GALLERY_HEIGHT, 0);
		ArrayList<CommercialData> list = (ArrayList<CommercialData>) getArguments()
				.getSerializable(EXTRA_COMMERCIAL_DATA);
		mCountVisible = getArguments().getInt(EXTRA_COUNT_VISIBLE, 1);
		mProporcional = getArguments().getFloat(EXTRA_PROPORCIONAL_IV);

		DBGalleryCommercialAdapter adapter = createDataForAdapter(list);
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
		layout.setDividerDrawable(new ColorDrawable(Color.parseColor("#35000000")));
		layout.setDividerPadding(0);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(params);
		
		for (int i = 0; i < adapter.getCount(); i++) {
			View view = adapter.getView(i, null, layout);
			view.setTag(adapter.getItem(i));
			view.setOnClickListener(getDefaultActionViewClickListener());
			layout.addView(view);
		}
		
		return layout;
	}
	
	private DBGalleryCommercialAdapter createDataForAdapter(ArrayList<CommercialData> list) {

		return new DBGalleryCommercialAdapter(getActivity(),
				R.layout.gallery_item_commercial_temp_image, list, mGalleryWidth,
				mGalleryHeight, mProporcional, mCountVisible);
	}

}
