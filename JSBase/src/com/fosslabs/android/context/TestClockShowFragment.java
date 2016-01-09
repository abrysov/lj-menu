package com.fosslabs.android.context;

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

public class TestClockShowFragment extends Fragment {
	private static final String TAG = "TestClockShowFragment";
	private static final String EXTRA_GALLERY_WIDTH = "EXTRA_GALLERY_WIDTH";
	private static final String EXTRA_GALLERY_HEIGHT = "EXTRA_GALLERY_HEIGHT";
	private static final String EXTRA_CLOCK_BG = "EXTRA_CLOCK_BG";
	private int mGalleryWidth;
	private int mGalleryHeight;
	private int mGallerySpacing;
	
	private class ViewHolder {
		JSGallery gallery;
		JSGalleryClockAdapter gca;
	}

	private ViewHolder vh = new ViewHolder();
	private Timer mTimer;
	public static TestClockShowFragment newInstance(int gallery_width, int gallery_height) {
		Bundle args = new Bundle();
		args.putInt(EXTRA_GALLERY_WIDTH, gallery_width);
		args.putInt(EXTRA_GALLERY_HEIGHT, gallery_height);
		TestClockShowFragment fragment = new TestClockShowFragment();
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

        try{
            mGalleryWidth = getArguments().getInt(EXTRA_GALLERY_WIDTH, 0);
        }catch (NullPointerException e){
            e.printStackTrace();
        }


		JSLog.d(TAG, EXTRA_GALLERY_WIDTH + mGalleryWidth + " " + EXTRA_GALLERY_HEIGHT + mGalleryHeight );
		vh.gallery = new JSGallery(getActivity());
		mGallerySpacing = Funs.dpToPx(getActivity(), 2);
		vh.gallery.setSpacing(mGallerySpacing);
		createDataForAdapter();
		
		return vh.gallery;
	}
	
	private void createDataForAdapter(){
		/*String[] ids = TimeZone.getAvailableIDs();
		//JSTODO 
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());*/
		ArrayList<Clock> list = new ArrayList<Clock>();
		
		/*for (int i = 0; i < ids.length; i++) {
			if(prefs.getBoolean(ids[i], false)) list.add(new Clock(ids[i]));
		}*/
		/*for (int i = 0; i < 7; i++) {
			list.add(new Clock(ids[i]));
		}*/
		
		// We need to display only Kiev, Moscow and Berlin for now
		list.add(new Clock("Europe/Kiev"));
		list.add(new Clock("Europe/Moscow"));
		list.add(new Clock("Europe/Berlin"));
		list.add(new Clock("Europe/Amsterdam"));
		
		vh.gca = new JSGalleryClockAdapter(getActivity(), 
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

