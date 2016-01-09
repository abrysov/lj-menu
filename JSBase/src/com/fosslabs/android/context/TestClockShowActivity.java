package com.fosslabs.android.context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fosslabs.android.jsbase.R;
import com.fosslabs.android.utils.Funs;
import com.fosslabs.android.utils.JSImageWorker;
import com.fosslabs.android.utils.JSLog;
import com.fosslabs.android.utils.JSTimeWorker;
import com.fosslabs.android.view.JSClock;
import com.fosslabs.android.view.JSGallery;
import com.fosslabs.android.view.JSGalleryAdapter;
import com.fosslabs.android.view.JSGalleryClockAdapter;

public class TestClockShowActivity extends Activity implements
		RadioGroup.OnCheckedChangeListener {
	private static final String TAG = "TestTimeShowActivity";

	private class ViewHolder {
		TextView tv;
		RadioGroup rg;
		RadioButton rb_0;
		RadioButton rb_1;
		RadioButton rb_2;
		FrameLayout fl;
		JSGallery gallery;
	}

	private ViewHolder vh = new ViewHolder();

	private ViewGroup.LayoutParams mLP;
	private Timer mTimer;
	private Date mDate;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_clock_show);
		mDate = null;
		mContext = this;
		
		vh.tv = (TextView) findViewById(R.id.tv);
		vh.rg = (RadioGroup) findViewById(R.id.rg);
		vh.rb_0 = (RadioButton) findViewById(R.id.rb_0);
		vh.rb_1 = (RadioButton) findViewById(R.id.rb_1);
		vh.rb_2 = (RadioButton) findViewById(R.id.rb_2);
		vh.fl = (FrameLayout) findViewById(R.id.fl);
		vh.gallery = (JSGallery) findViewById(R.id.gallery);
		
		vh.rg.setOnCheckedChangeListener(this);
		vh.fl = (FrameLayout) JSImageWorker.setBackground(vh.fl, getResources()
				.getDrawable(R.drawable.logo_clock));		
		vh.gallery.setSpacing(Funs.dpToPx(mContext, 4));
				
	}

	
	private void updateTime(String str) {
		Funs.getToast(mContext, str);
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		mTimer = new Timer();
		JSTimerTask tt = new JSTimerTask(this, str);
		mTimer.schedule(tt, 1, 1000);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		updateTime("Asia/Karachi");/*
		switch (checkedId) {
		case R.id.rb_0:{
			updateTime("Asia/Kolkata");
			break;
		}
		case R.id.rb_1:{
			updateTime("Etc/GMT-6");
			break;
		}
		case R.id.rb_2:{
			updateTime("Asia/Karachi");
			break;
		}

		default:;
		}*/
	}

	public class JSTimerTask extends TimerTask {
		String tz;

		public JSTimerTask(Context context, String str) {
			tz = str;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Date date = new Date();
				date = getDateInTimeZone(date, tz);
				// System.out.println(date.toLocaleString());
				mDate = date;
				mHandler.sendEmptyMessage(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private Date getDateInTimeZone(Date currentDate, String timeZoneId) {
			TimeZone tz = TimeZone.getTimeZone(timeZoneId);
			Calendar mbCal = new GregorianCalendar(tz);
			mbCal.setTimeInMillis(currentDate.getTime());
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, mbCal.get(Calendar.YEAR));
			cal.set(Calendar.MONTH, mbCal.get(Calendar.MONTH));
			cal.set(Calendar.DAY_OF_MONTH, mbCal.get(Calendar.DAY_OF_MONTH));
			cal.set(Calendar.HOUR_OF_DAY, mbCal.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, mbCal.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, mbCal.get(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, mbCal.get(Calendar.MILLISECOND));

			return cal.getTime();
		}
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			vh.tv.setText(JSTimeWorker.getSimpleDateFormat(mDate,
					JSTimeWorker.SimpleDateConst.TIME));
			// mDate.toLocaleString());
			// System.out.println(result.getHours()+" "+result.getMinutes());
			vh.fl.removeAllViews();
			mLP = vh.fl.getLayoutParams();
			JSClock clock = new JSClock(mContext, mLP.height / 2, mLP.width / 2,
					mDate, 48);
	
			//clock.setArrowHour(JSImageWorker.loadFromResource(mContext, R.drawable.clock1));
			//clock.setArrowMinute(JSImageWorker.loadFromResource(mContext, R.drawable.clock2));
			//clock.setArrowSecond(JSImageWorker.loadFromResource(mContext, R.drawable.clock2));
			vh.fl.addView(clock);
		}
	};
}
