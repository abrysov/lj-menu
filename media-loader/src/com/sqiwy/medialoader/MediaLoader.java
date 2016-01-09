package com.sqiwy.medialoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import com.sqiwy.medialoader.Result.Status;
import com.sqiwy.medialoader.util.MediaLoaderHelper;
import com.sqiwy.medialoader.util.FileMediaItemLoaderHelper;
import com.sqiwy.medialoader.util.ZippedMediaItemLoaderHelper;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by abrysov
 */

/**
 * Allows to load {@link MediaItem}s via
 * {@link MediaLoader#load(Context, int, MediaItem...)} method.
 */
public class MediaLoader extends IntentService {

	private static final String TAG = MediaLoader.class.getName();

	private static final String ARG_ACTION = "action";
	private static final String INTENT_ACTION_CANCEL = "cancel";
	
	public static final String ARG_MEDIA_ITEMS = "arg-media-items";
	public static final String ARG_REQUEST_CODE = "arg-request-code";
	public static final String ARG_RESULTS = "arg-completion-results";

	
	public static final String INTENT_ACTION_COMPLETION = "ACTION_"
			+ MediaLoader.class.getSimpleName().toUpperCase(Locale.US)
			+ "_LOAD_COMPLETED";

	private boolean mIsCancelled;
	private MediaLoaderHelper mRunningLoaderHelper;
	
	public MediaLoader() {
		super(MediaLoader.class.getSimpleName());
	}

	/**
	 * Initiates loading of passed media items from and to locations specified
	 * by them.
	 * <p>
	 * Returns {@link IntentFilter} which you can use to subscribe (via
	 * {@link BroadcastReceiver}) to {@link Intent} which will contain passed
	 * here request code ({@link MediaLoader#ARG_REQUEST_CODE} extra key) and
	 * results of loading (list of {@link Result}s stored as parcelable array
	 * list extra under {@link MediaLoader#ARG_RESULTS} key)
	 * 
	 * @param context
	 *            <code>Context</code> instance to start service.
	 * @param requestCode
	 *            <code>int</code> value that will be returned in the
	 *            broadcasted intent on load completion.
	 * @param mediaItems
	 *            <code>MediaItem</code>s to load.
	 * 
	 * @return <code>IntentFilter</code> which can be used to register
	 *         <code>BroadcastReceiver</code> to receive load completion intent
	 *         containing the passed <code>requestCode</code>. Returns
	 *         <code>null</code> if load initiating failed.
	 */
	public static IntentFilter load(Context context, int requestCode,
			MediaItem... mediaItems) {

		if (null == mediaItems || 0 == mediaItems.length) {
			Log.w(TAG, "Nothing to load.");
			return null;
		}

		Intent intent = new Intent(context, MediaLoader.class);
		intent.putExtra(ARG_REQUEST_CODE, requestCode);
		intent.putParcelableArrayListExtra(ARG_MEDIA_ITEMS,
				new ArrayList<MediaItem>(Arrays.asList(mediaItems)));

		context.startService(intent);

		// Currently we return the same intent filter all the time
		return new IntentFilter(INTENT_ACTION_COMPLETION);
	}
	
	/**
	 * Cancel current loading and all scheduled loadings.
	 * @param context <code>Context</code> to cancel loading.
	 */
	public static void cancel(Context context) {
		Intent intent = new Intent(context, MediaLoader.class);
		intent.putExtra(ARG_ACTION, INTENT_ACTION_CANCEL);
		context.startService(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		String action = intent.getStringExtra(ARG_ACTION);
		if (INTENT_ACTION_CANCEL.equals(action)) {
			// Stop downloading
			mIsCancelled = true;
			if (null != mRunningLoaderHelper) {
				mRunningLoaderHelper.getCancellationPolicy().setCancelled(true);
			}
			// Stop service
			stopSelf();
		}

		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {

		String action = intent.getStringExtra(ARG_ACTION);
		if (mIsCancelled || INTENT_ACTION_CANCEL.equals(action)) {
			Log.i(TAG, "Cancelled - action: " + action + ", canelled flag: " + mIsCancelled);
			// We already triggered stopSelf in onStartCommand
			return;
		}
		
		final int requestCode = intent.getIntExtra(ARG_REQUEST_CODE, 0);
		final ArrayList<MediaItem> mediaItems = intent
				.getParcelableArrayListExtra(ARG_MEDIA_ITEMS);
		final ArrayList<Result> completionResults = new ArrayList<Result>(
				mediaItems.size());

		final int count = mediaItems.size();
		MediaItem mediaItem;
		MediaLoaderHelper loaderHelper;
		Status result;

		// Load media items
		for (int i = 0; i < count; i++) {
			if (mIsCancelled) {
				break;
			}
			
			mediaItem = mediaItems.get(i);

			loaderHelper = getLoaderHelper(mediaItem);
			mRunningLoaderHelper = loaderHelper;
			
			try {
				loaderHelper.load(mediaItem);
				if (!mIsCancelled) {
					result = Status.SUCCESS;
				} else {
					result = Status.FAILURE;
				}
				
			} catch (Exception e) {
				Log.e(TAG,
						"Failed to load media item: " + mediaItem.toString(), e);
				result = Status.FAILURE;
			}

			completionResults.add(new Result(mediaItem, result));
		}

		// Notify load completion subscribers
		Intent resultIntent = new Intent(INTENT_ACTION_COMPLETION);
		resultIntent.putExtra(ARG_REQUEST_CODE, requestCode);
		resultIntent.putParcelableArrayListExtra(ARG_RESULTS, completionResults);
		sendBroadcast(resultIntent);
	}

	/**
	 * Gets loader helper to load certain media item. TODO: cache loader
	 * helpers.
	 * 
	 * @param mediaItem
	 * @return
	 */
	protected MediaLoaderHelper getLoaderHelper(MediaItem mediaItem) {
		MediaLoaderHelper result;

		if (mediaItem.isArchive()) {
			result = new ZippedMediaItemLoaderHelper();
		} else {
			result = new FileMediaItemLoaderHelper();
		}

		return result;
	}

}
