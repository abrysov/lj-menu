package com.sqiwy.dashboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.fosslabs.android.utils.Funs;
import com.fosslabs.android.utils.JSFileWorker;
import com.fosslabs.android.utils.JSLog;
import com.fosslabs.android.view.JSLinearLayout;
import com.sqiwy.dashboard.model.ProjectConstants;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.advertisement.Advertisement;
import com.sqiwy.menu.advertisement.Advertisement.Places;
import com.sqiwy.menu.advertisement.Advertisement.Type;
import com.sqiwy.menu.advertisement.AdvertisementManager;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.resource.ResourcesManager.Category;
import com.sqiwy.menu.util.PreferencesUtils;
import com.sqiwy.restaurant.api.BackendException;

/**
 * Created by abrysov
 */

public class DBMediaFragment extends Fragment implements
		OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,
		OnVideoSizeChangedListener, SurfaceHolder.Callback {
	private static final String TAG = "MediaFragment";
	private static final int ANIMATION_DURATION_BASE = 300;

	private int mVideoWidth;
	private int mVideoHeight;
	private MediaPlayer mMediaPlayer;

	private SurfaceHolder holder;

	private boolean mIsVideoSizeKnown = false;
	private boolean mIsVideoReadyToBePlayed = false;

	
	boolean flag;
	
	private ArrayList<String> mVideoPath = null;
	private int id_video = -1;

	long starttime;
	long endtime;

	private class ViewHolder {
		SurfaceView sv;
		JSLinearLayout ll_0;
		View parent;
	}

	private ViewHolder vh = new ViewHolder();
	private Callbacks mCallbacks;

	public interface Callbacks {
		void enter();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// extras = getIntent().getExtras();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		JSLog.d(TAG, "onCreateView ");
		View v = inflater.inflate(R.layout.fragment_media, null);
		createView(v);
		return v;
	}

	@SuppressWarnings("deprecation")
	private void createView(View v) {
		vh.parent = v.findViewById(R.id.parent);

		int width = Funs.getDisplayMetrics(getActivity()).widthPixels;

		int height = getResources().getDimensionPixelSize(
				R.dimen.fragment_grand_height)
				+ getResources().getDimensionPixelSize(
						R.dimen.fragment_grand_padding);
		JSLog.d(TAG, "fragment_menu_grand_height " + height);

		vh.ll_0 = (JSLinearLayout) v.findViewById(R.id.ll_0);
		vh.ll_0.setJSWidth(height * 6);
		vh.ll_0.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// playParentAnim(vh.parent);
				mCallbacks.enter();
			}
		});

		vh.sv = (SurfaceView) v.findViewById(R.id.sv);
		holder = vh.sv.getHolder();
		holder.addCallback(this);
		// holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		createVideoPath();
	}

	private void createVideoPath() {
		if (mVideoPath == null) {
			mVideoPath = JSFileWorker.getMediaFiles(ProjectConstants
					.getPathToDBMovies());
			if (mVideoPath.size() == 0) {
				// mVideoPath .add("android.resource://" +
				// "com.fosslabs.android.sqiwystartlittlejapan/raw/apollo_17_stroll1"
				// );
				// mVideoPath.add(com.sqiwy.dashboard.model.ProjectConstants
				// + "/Samsung/Video/Moments_of_EveryDay_Life.mp4");
				/*mVideoPath.add(Environment.getExternalStorageDirectory()
						.getPath() + "/SQIWY/VIDEO/testVideo_11.mp4");
				mVideoPath.add(Environment.getExternalStorageDirectory()
						.getPath() + "/SQIWY/VIDEO/testVideo_12.mp4");
				mVideoPath.add(Environment.getExternalStorageDirectory()
						.getPath() + "/SQIWY/VIDEO/testVideo_13.mp4");*/
				
				List<Advertisement> ads = AdvertisementManager.getAds(Type.VIDEO,Places.DASHBOARD);
				
				for (Advertisement ad : ads) {
					mVideoPath.add(ResourcesManager.getResourcePath(ad.getName(), Category.VIDEO).getAbsolutePath());
				}
			}

		}
		JSLog.d(TAG, "createVideoPath " + mVideoPath.size());
	}

	private void playVideo() {
		doCleanUp();
		try {
			id_video = (id_video + 1) % mVideoPath.size();
			// Create a new media player and set the listeners
			mMediaPlayer = new MediaPlayer();
			File f = new File(mVideoPath.get(id_video));
			JSLog.d(TAG,
					"playVideo()" + f.isFile() + f.exists()
							+ f.getAbsolutePath());
			JSLog.d(TAG, "mVideoPath.get(id_video) " + mVideoPath.get(id_video));

			mMediaPlayer.setDataSource(getActivity(),
					Uri.parse(mVideoPath.get(id_video)));
			mMediaPlayer.setDisplay(holder);
			mMediaPlayer.prepare();
			mMediaPlayer.setOnBufferingUpdateListener(this);
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setOnVideoSizeChangedListener(this);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		} catch (Exception e) {
			JSLog.e(TAG, "error: " + e.getMessage(), e);
		}
	}

	public void onBufferingUpdate(MediaPlayer arg0, int percent) {
		JSLog.d(TAG, "onBufferingUpdate percent:" + percent);
		Log.d("myLogs", " onBufferingUpdate()");

	}

	public void onCompletion(MediaPlayer mp) {
		JSLog.d(TAG, "onCompletion called");

		Log.d("myLogs", " onCompletion()");
		endtime = System.currentTimeMillis();
		Log.d("myLogs", "time play " + mVideoPath.get(id_video) + ": "
				+ (endtime - starttime) + " ms");
//		sendReportTask report = new sendReportTask();
//		report.execute(mVideoPath.get(id_video).substring(mVideoPath.get(id_video).indexOf("testVideo")),
//				String.valueOf((endtime - starttime) / 1000), "0");
		mp.release();
		releaseMediaPlayer();
		
		if(flag) playVideo();
	}

	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		Log.d("myLogs", " onVideoSizeChanged()");
		if (width == 0 || height == 0) {
			JSLog.e(TAG, "invalid video width(" + width + ") or height("
					+ height + ")");
			return;
		}
		mIsVideoSizeKnown = true;
		mVideoWidth = width;
		mVideoHeight = height;
//		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
//			startVideoPlayback();
//		}
	}

	public void onPrepared(MediaPlayer mediaplayer) {
		Log.d("myLogs", " onPrepared()");
		JSLog.d(TAG, "onPrepared called");
		mIsVideoReadyToBePlayed = true;
		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
			startVideoPlayback();
		}
	}

	public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
		JSLog.d(TAG, "surfaceChanged called");
		Log.d("myLogs", " surfaceChanged()");

	}

	public void surfaceDestroyed(SurfaceHolder surfaceholder) {
		JSLog.d(TAG, "surfaceDestroyed called");
		Log.d("myLogs", " surfaceDestroyed()");
	}

	public void surfaceCreated(SurfaceHolder holder) {
		JSLog.d(TAG, "surfaceCreated called");
		Log.d("myLogs", " surfaceCreated()");
		playVideo();
	}

	@Override
	public void onPause() {
		super.onPause();
		endtime = System.currentTimeMillis();
		Log.d("myLogs", " onPause()");
		if (id_video>-1) {
			Log.d("myLogs", "time play " + mVideoPath.get(id_video) + ": "
				+ (endtime - starttime) + " ms");
//			sendReportTask report = new sendReportTask();
//			report.execute(mVideoPath.get(id_video).substring(mVideoPath.get(id_video).indexOf("testVideo")),
//					String.valueOf((endtime - starttime) / 1000), "0");
		}
		releaseMediaPlayer();
		doCleanUp();
		flag = false;
	}

	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		playVideo();
		flag = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("myLogs", " onDestroy()");
		releaseMediaPlayer();
		doCleanUp();
	}

	private void releaseMediaPlayer() {
		Log.d("myLogs", " releaseMediaPlayer()");
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	private void doCleanUp() {
		Log.d("myLogs", " doCleanUp()");
		mVideoWidth = 0;
		mVideoHeight = 0;
		mIsVideoReadyToBePlayed = false;
		mIsVideoSizeKnown = false;
	}

	private void startVideoPlayback() {
		Log.d("myLogs", " startVideoPlayback()");
		JSLog.d(TAG, "startVideoPlayback");
		holder.setFixedSize(mVideoWidth, mVideoHeight);
		starttime = System.currentTimeMillis();
		mMediaPlayer.start();
	}

//	private void playParentAnim(View v) {
//		releaseMediaPlayer();
//		doCleanUp();
//		vh.sv.setVisibility(View.GONE);
//
//		ObjectAnimator animY = ObjectAnimator.ofFloat(v, "y", v.getTop(),
//				-v.getMeasuredHeight() / 2);
//		animY.setDuration(ANIMATION_DURATION_BASE);
//
//		ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotationX", 0, 90);
//		animator.setDuration(ANIMATION_DURATION_BASE);
//
//		AnimatorSet set = new AnimatorSet();
//		set.playTogether(animator, animY);
//		set.addListener(new Animator.AnimatorListener() {
//
//			@Override
//			public void onAnimationStart(Animator animation) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onAnimationRepeat(Animator animation) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onAnimationEnd(Animator animation) {
//				mCallbacks.enter();
//
//			}
//
//			@Override
//			public void onAnimationCancel(Animator animation) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//		set.start();
//	}

	class sendReportTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.d("myLogs", "DBMEDIAFRAGMENT sendReportTask()");
			int ad_id;
			int play_time;
			int click;

//			Log.d("myLogs", "ad_id: " + params[0]);
//			Log.d("myLogs", "play_time: " + params[1]);
//			Log.d("myLogs", "click: " + params[2]);
			
			String str = params[0].substring(params[0].lastIndexOf("_") + 1,
					params[0].length()-4);
			Log.d("myLogs", "str: " + str);
			ad_id = Integer.valueOf(str);
			play_time = Integer.valueOf(params[1]);
			click = Integer.valueOf(params[2]);

//			Log.d(TAG, "ad_id: " + ad_id + "; play_time: " + play_time
//					+ "; click: " + click);

			Map<String, Integer> statData = new HashMap();
			statData.put("ad_id", ad_id);
			statData.put("play_time", play_time);
			statData.put("click", click);
			
			MenuApplication app = (MenuApplication) MenuApplication
					.getContext();
			try {
				if (!PreferencesUtils.isApplicationInDemoMode(getActivity())) {
					MenuApplication.getOperationService().statData(statData);
				}
			} catch (BackendException e) {
				// TODO Auto-generated catch block
				Log.d("myLogs", "BackendException: " + e.toString());
				e.printStackTrace();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d("myLogs", "IOException: " + e.toString());
				e.printStackTrace();
				
			} catch(Exception e){
				Log.d("myLogs", "IOException: " + e.toString());
			}

			return null;
		}

	}
}