package com.sqiwy.dashboard.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.sqiwy.dashboard.BuildConfig;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.util.PreferencesUtils;
import com.squareup.tape.FileObjectQueue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by abrysov
 */

public class StatsUtils {

	private static final String TAG = StatsUtils.class.getName();
	private static final boolean DEBUG = BuildConfig.DEBUG;
	private static int STATS_DELAY=200; // ms
	
	private static ExecutorService mStatsExecutor=null;
	
	public static enum StatsContext {
		MAP(2),
		DASHBOARD(1),
		VIDEOSCREEN(0);
		
		private final int mIdentifier;
		
		private StatsContext(int contextIdentifier) {
			mIdentifier = contextIdentifier;
		}
		
		public int getIdentifier() {
			return mIdentifier;
		}
	}
	
	public static void reportAdvertisementStats(int adId, long durationPlayed, int click, StatsContext context) {
		Map<String, Integer> stats = new HashMap<String, Integer>();
		stats.put("ad_id", adId);
		stats.put("play_time", (int) durationPlayed);
		stats.put("click", click);
		stats.put("context", context.getIdentifier());
		
		reportStats(stats);
	}
	
	private static void reportStats(Map<String, Integer> stats) {
		StatsQueue.add(stats);
		if (mStatsExecutor==null) {
			mStatsExecutor=Executors.newSingleThreadExecutor();
		}
		new SendReportTask().executeOnExecutor(mStatsExecutor, (Void)null);
		new DelayTask().executeOnExecutor(mStatsExecutor, (Void)null);
	}
	
	private static class DelayTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(STATS_DELAY);
			} catch (InterruptedException ex) {}
			return null;
		}
	}
	
	private static class SendReportTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... params) {
			Map<String, Integer> stats;
			
			if (!isConnectedToNetwork()) {
				return null;
			}
			while (true) {
				try {
					stats = StatsQueue.peek();
				} catch (Throwable ex) {
					break;
				}
				if (stats==null) {
					break;
				}
				if (DEBUG) {
					Log.d(TAG, "Reporting " + stats);
				}
				
				try {
					if (!PreferencesUtils.isApplicationInDemoMode(MenuApplication.getContext())) {
						MenuApplication.getOperationService().statData(stats);
					}
					
					// Remove stats from queue.
					StatsQueue.remove();
					
				} catch (Exception e) {
					Log.d(TAG, "Failed to report stats", e);
					
					// Just exit, we'll try to report stats next time.
					break;
				}
			}

			return null;
		}
	}
	
	private static boolean isConnectedToNetwork() {
		ConnectivityManager connMan;
		NetworkInfo ni;
		boolean isConnected;
		
		connMan=(ConnectivityManager)MenuApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		ni=connMan.getActiveNetworkInfo(); isConnected=false;
		if (ni!=null) {
			isConnected=ni.isConnected();
		}
		return isConnected;
	}
	
	public static final class StatsQueue {
		
		private static FileObjectQueue<Wrapper> sStatsQueue;
		
		private static synchronized FileObjectQueue<Wrapper> get() {
			if (null == sStatsQueue) {
				try {
					sStatsQueue = new FileObjectQueue<Wrapper>(new File(ResourcesManager.getRootDir(), ".stats"),
							new GsonConverter<Wrapper>(new Gson(), Wrapper.class));
				} catch (IOException e) {
					Log.e(TAG, "Couldn't create queue file for stats.", e);
				}
			}
			
			return sStatsQueue;
		}
		
		public synchronized static void add(Map<String, Integer> stats) {
			get().add(new Wrapper(stats));
		}
		
		public synchronized static Map<String, Integer> peek() {
			Map<String, Integer> value = null;
			Wrapper wrapper = get().peek();
			if (null != wrapper) {
				value = wrapper.value;
			}
			return value;
		}
		
		public synchronized static void remove() {
			get().remove();
		}
		
		private static class Wrapper {
			Map<String, Integer> value;
			
			public Wrapper(Map<String, Integer> value) {
				this.value = value;
			}
		}
		
		/**
		 * Use GSON to serialize stats.
		 */
		static class GsonConverter<T> implements FileObjectQueue.Converter<T> {
			private final Gson gson;
			private Class<T> type;

			public GsonConverter(Gson gson, Class<T> type) {
				this.gson = gson;
				this.type = type;
			}

			@Override
			public T from(byte[] bytes) {
				Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes));
				
				return gson.fromJson(reader, type);
			}

			@Override
			public void toStream(T object, OutputStream bytes) throws IOException {
				Writer writer = new OutputStreamWriter(bytes);
				gson.toJson(object, writer);
				writer.close();
			}
		}
	}
}
