package com.sqiwy.dashboard;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.DateTime;

import com.fosslabs.android.model.Clock;
import com.fosslabs.android.utils.JSImageWorker;
import com.fosslabs.android.utils.JSLog;
import com.fosslabs.android.view.JSClock;
import com.fosslabs.android.view.JSGallery;
import com.sqiwy.dashboard.DBGalleryClockFragment.JSTimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by abrysov
 */

public class DBClockFragment extends Fragment {
	private static final String TAG = "DBClockFragment";
	private static final String EXTRA_GALLERY_WIDTH = "EXTRA_GALLERY_WIDTH";
	private static final String EXTRA_GALLERY_HEIGHT = "EXTRA_GALLERY_HEIGHT";
	private static final String EXTRA_TOWN_IDS = "EXTRA_TOWN_IDS";
	private static final String EXTRA_TOWN_NAMES = "EXTRA_TOWN_NAMES";
	private static final String EXTRA_TOWN_DIFFERENCES = "EXTRA_TOWN_DIFFERENCES";

	private ArrayList<Clock> mList = new ArrayList<Clock>();
	private Activity mContext;
	private int mGalleryWidth;
	private int mGalleryHeight;
	private Drawable mBg;
	private Typeface font;

//	private String[] mTownIds = { "Europe/Moscow", "Europe/Kiev",
//			"Europe/Berlin" };
	private String[] mTownIds;
	private String[] mTownNames;
	private int[] mTownDifferences;
	
	private JSClock[] jsClocks;

	private Timer mTimer;
	
	private DateTime dtNow;

	private class ViewHolder {
		LinearLayout ll_clock_parent;
	}

	private ViewHolder vh = new ViewHolder();

	private class ViewHolderItem {
		FrameLayout fl_clock;
		TextView tv_city;
		ImageView iv_clock_divider;
	}

	public static DBClockFragment newInstance(int gallery_width,
			int gallery_height, String[] townIds, String[] townNames,
			int[] townDifferences) {
		Bundle args = new Bundle();
		args.putInt(EXTRA_GALLERY_WIDTH, gallery_width);
		args.putInt(EXTRA_GALLERY_HEIGHT, gallery_height);
		args.putStringArray(EXTRA_TOWN_IDS, townIds);
		args.putStringArray(EXTRA_TOWN_NAMES, townNames);
		args.putIntArray(EXTRA_TOWN_DIFFERENCES, townDifferences);
		DBClockFragment fragment = new DBClockFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mGalleryWidth = getArguments().getInt(EXTRA_GALLERY_WIDTH);
		mGalleryHeight = getArguments().getInt(EXTRA_GALLERY_HEIGHT);
		mTownNames = getArguments().getStringArray(EXTRA_TOWN_NAMES);
		mTownDifferences = getArguments().getIntArray(EXTRA_TOWN_DIFFERENCES);
		mBg = mContext.getResources().getDrawable(R.drawable.logo_clock);
		mTownIds = getArguments().getStringArray(EXTRA_TOWN_IDS);
		jsClocks = new JSClock[mTownIds.length];
		dtNow = DateTime.now();
		for (int i = 0; i < mTownIds.length; i++) {
			mList.add(new Clock(mTownIds[i], mTownNames[i], mTownDifferences[i]));
		}

		vh.ll_clock_parent = (LinearLayout) inflater.inflate(
				R.layout.fragment_clock, null);
		for (int i = 0; i < mList.size(); i++) {
			vh.ll_clock_parent.addView(createView(i));
		}
		
		return vh.ll_clock_parent;
	}

	private View createView(int position) {
		if (mList.size() == 0)
			return null;

		ViewHolderItem vh;

		LayoutInflater inflater = mContext.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.gallery_item_clock_fragment,
				null);
		vh = new ViewHolderItem();
		vh.fl_clock = (FrameLayout) rowView.findViewById(R.id.fl_clock);
		vh.iv_clock_divider = (ImageView) rowView.findViewById(R.id.iv_clock_divider);
		if(position == 3){
			vh.iv_clock_divider.setVisibility(View.INVISIBLE);
		}
		vh.tv_city = (TextView) rowView.findViewById(R.id.tv_city);
		// vh.tv_time = (TextView) rowView.findViewById(R.id.tv_time);

		// vh.tv_time.setText(mList.get(position).getTime());
		font = Typeface.createFromAsset(mContext.getAssets(), "fonts/robotolight.ttf");
		vh.tv_city.setText(mList.get(position).getCity(mContext));
		vh.tv_city.setTypeface(font);
		vh.tv_city.setTextColor(Color.parseColor("#b5a5a0"));
		vh.fl_clock.removeAllViews();
		// Clock_Widget_height to Clock_City_Title_height = 3 to 1;

		int size = mGalleryHeight * 3 / 5;
		JSLog.d(TAG, "SIZE FOR CLOCK " + size);
		jsClocks[position] = new JSClock(mContext, size / 2, size / 2, mList.get(
				position).getDateTime(dtNow), size / 2 * 8 / 15);
		vh.fl_clock.addView(jsClocks[position]);
		vh.fl_clock = (FrameLayout) JSImageWorker.setBackground(vh.fl_clock,
				mBg);
		vh.fl_clock.setLayoutParams(new LinearLayout.LayoutParams(size, size));

		/*
		 * ImageView iv = new ImageView(mContext);
		 * iv.setImageResource(R.drawable.ic_launcher); iv.setPadding(20, 20,
		 * 20, 20); iv.setLayoutParams(new JSGallery.LayoutParams(140, 190));
		 * iv.setScaleType(ImageView.ScaleType.FIT_XY);
		 * iv.setBackgroundColor(Color.GRAY); iv.setLayoutParams(new
		 * JSGallery.LayoutParams(50,50)); return iv;
		 */
		rowView.setLayoutParams(new JSGallery.LayoutParams(mGalleryWidth / 3,
				mGalleryHeight));
		return rowView;
	}

	public class JSTimerTask extends TimerTask {

		public JSTimerTask(Context context) {
		}

		@Override
		public void run() {
			try {
				mHandler.sendEmptyMessage(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			dtNow = DateTime.now();
			for (int i = 0; i < mList.size(); i++) {
				jsClocks[i].setTimeArray(mList.get(i).getDateTime(dtNow));
				jsClocks[i].invalidate();
			}
		}
	};

	private void updateTime() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		mTimer = new Timer();
		JSTimerTask tt = new JSTimerTask(getActivity());
		mTimer.schedule(tt, 1, 60000);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mTimer != null){
			mTimer.cancel();
			mTimer = null;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateTime();
	}

}
