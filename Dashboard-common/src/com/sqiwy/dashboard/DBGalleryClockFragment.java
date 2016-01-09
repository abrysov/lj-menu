package com.sqiwy.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fosslabs.android.context.TestClockShowActivity.JSTimerTask;
import com.fosslabs.android.model.Clock;
import com.fosslabs.android.jsbase.R;
import com.fosslabs.android.utils.Funs;
import com.fosslabs.android.utils.JSLog;
import com.fosslabs.android.view.JSClock;
import com.fosslabs.android.view.JSGallery;
import com.fosslabs.android.view.JSGalleryClockAdapter;
import com.sqiwy.dashboard.view.DBGalleryClockAdapter;

/**
 * Created by abrysov
 */

public class DBGalleryClockFragment extends Fragment {
	private static final String TAG = "TestClockShowFragment";
	private static final String EXTRA_GALLERY_WIDTH = "EXTRA_GALLERY_WIDTH";
	private static final String EXTRA_GALLERY_HEIGHT = "EXTRA_GALLERY_HEIGHT";
	private static final String EXTRA_TOWN_IDS = "EXTRA_TOWN_IDS";
	private static final String EXTRA_TOWN_NAMES = "EXTRA_TOWN_NAMES";
	private static final String EXTRA_TOWN_DIFFERENCES = "EXTRA_TOWN_DIFFERENCES";
	private int mGalleryWidth;
	private int mGalleryHeight;
	private int mGallerySpacing;
	private Drawable mBG;
	private String[] mTownIds;
	private String[] mTownNames;
	private int[] mTownDifferences;
	
	private class ViewHolder {
		JSGallery gallery;
		DBGalleryClockAdapter gca;
	}

	private ViewHolder vh = new ViewHolder();
	private Timer mTimer;
	public static DBGalleryClockFragment newInstance(int gallery_width, int gallery_height, String [] townIds, String[] townNames, int[] townDifferences) {
		Bundle args = new Bundle();
		args.putInt(EXTRA_GALLERY_WIDTH, gallery_width);
		args.putInt(EXTRA_GALLERY_HEIGHT, gallery_height);
		args.putStringArray(EXTRA_TOWN_IDS, townIds);
		args.putStringArray(EXTRA_TOWN_NAMES, townNames);
		args.putIntArray(EXTRA_TOWN_DIFFERENCES, townDifferences);
		DBGalleryClockFragment fragment = new DBGalleryClockFragment();
		fragment.setArguments(args);
		return fragment;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mGalleryWidth = getArguments().getInt(EXTRA_GALLERY_WIDTH, 0);
		mGalleryHeight = getArguments().getInt(EXTRA_GALLERY_HEIGHT, 0);
		mTownIds = getArguments().getStringArray(EXTRA_TOWN_IDS);
		mTownNames = getArguments().getStringArray(EXTRA_TOWN_NAMES);
		mTownDifferences = getArguments().getIntArray(EXTRA_TOWN_DIFFERENCES);
		
		JSLog.d(TAG, EXTRA_GALLERY_WIDTH + mGalleryWidth + " " + EXTRA_GALLERY_HEIGHT + mGalleryHeight );
		
		vh.gallery = new JSGallery(getActivity());
		mGallerySpacing = 0;//Funs.dpToPx(getActivity(), 2);
		vh.gallery.setSpacing(mGallerySpacing);
		
		createDataForAdapter();
		
		return vh.gallery;
	}
	
	private void createDataForAdapter(){
		if(mTownIds == null) return;
		ArrayList<Clock> list = new ArrayList<Clock>();
		for (int i = 0; i < mTownIds.length ; i++) {
			list.add(new Clock(mTownIds[i], mTownNames[i], mTownDifferences[i]));
		}
		
		vh.gca = new DBGalleryClockAdapter (getActivity(), 
				R.layout.gallery_item_clock_fragment, list, mGalleryWidth - 2*mGallerySpacing, mGalleryHeight);
		vh.gallery.setAdapter(vh.gca);
		vh.gallery.setSelection(vh.gca.getCount() / 2);
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		updateTime();
	}
	
	private void updateTime() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		mTimer = new Timer();
		JSTimerTask tt = new JSTimerTask(getActivity());
		mTimer.schedule(tt, 1, 1000);
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
			vh.gca.notifyDataSetChanged();
		}
	};

}

