package com.sqiwy.dashboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sqiwy.restaurant.api.BackendException;
import com.sqiwy.dashboard.CancelableToast.VerticalAlignment;
import com.sqiwy.dashboard.logger.LogMessage;
import com.sqiwy.dashboard.logger.LogMessage.Stage;
import com.sqiwy.dashboard.logger.LoggerService;
import com.sqiwy.dashboard.util.FlowUtils;
import com.sqiwy.medialoader.MediaItem;
import com.sqiwy.medialoader.MediaLoader;
import com.sqiwy.medialoader.Result;
import com.sqiwy.medialoader.Result.Status;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.provider.MenuSaver;
import com.sqiwy.menu.provider.MenuSaver.OnMenuSavedListener;
import com.sqiwy.menu.resource.ResourceOperation;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.resource.ResourcesManager.Category;
import com.sqiwy.menu.util.PreferencesUtils;
import com.sqiwy.menu.util.SystemControllerHelper;

/**
 * Created by abrysov
 * Loads resources when added.
 */
public class ResourcesFragment extends Fragment implements OnClickListener {

	public interface Listener {
		public void onSettings();
		public void onContinue(boolean errorState);
		public void onError();
	}
	
	/**
	 * consts
	 */
	private static final String TAG = ResourcesFragment.class.getName();
	public static final int REQUEST_CODE_LOAD = 1;
	private static final long SHOW_TIME_MINIMUM = 10 * 1000; // 10 seconds
	private static final long SHOW_TIME_AFTER_ERROR = 20 * 1000; // 20 seconds;
	private static final String EXTRA_FORCE_LOAD_ALL_ITEMS = "force_load_all_items";
	private static final int REQUEST_CODE_LOCK = 100;

	/**
	 * variables
	 */
	private MediaItemBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	private Map<String, Result> mMediaItemResultsMapping;
	private Map<String, MediaItem> mMediaItemsLeftToLoad;
	private volatile Map<String, String> mBackedUpFolders;
	private List<MediaItem> mMediaItemsFailed;
	private Listener mListener;
	private TextView mLoaderTitle;
	private TextView mLoaderStatusText;
	private View mLoaderSettingsButton;
	private View mLoaderReloadButton;
	private ProgressBar mLoaderProgress;
	private ProgressBar mLoaderProgressInfinite;
	private EditText mPassword;
	private View mLoginButton;
	private boolean mShowPassword;
	private boolean mIsUserLeavingActivity;
	private boolean mIsLoadingFailed;
	private View mControls;
	private View mLoginControls;
	private List<ResourceOperation> mResourceOperations;
	private long mStartTime;
	private Handler mHandler; 
	private boolean mIsForceLoadAllItems = false;
	
	/**
	 * 
	 * @param forceLoadAllItems
	 * @return
	 */
	public static ResourcesFragment newInstance(boolean forceLoadAllItems) {
		
		ResourcesFragment fragment = new ResourcesFragment();
		Bundle args = new Bundle();
		args.putBoolean(EXTRA_FORCE_LOAD_ALL_ITEMS, forceLoadAllItems);
		
		fragment.setArguments(args);
		
		return fragment;
	}
	
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		if (null == mIntentFilter) {
			mIntentFilter = new IntentFilter(MediaLoader.INTENT_ACTION_COMPLETION);
		}
		
		if (null == mReceiver) {
			mReceiver = new MediaItemBroadcastReceiver();
		}
		
		mHandler = new Handler();
		
		// Backup resources
		//ResourcesManager.moveResourcesFromRootToBackup();
		
		MenuApplication.getContext().registerReceiver(mReceiver, mIntentFilter);
		
		mStartTime = System.currentTimeMillis();
		
		mIsForceLoadAllItems = getArguments().getBoolean(EXTRA_FORCE_LOAD_ALL_ITEMS, true);
	}
	
	/**
	 * 
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = (Listener) activity;
	}
	
	/**
	 * 
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		return inflater.inflate(R.layout.media_item_loader, container, false);
	}
	
	/**
	 * 
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mLoaderTitle = (TextView) view.findViewById(R.id.loader_title);
		
		mLoaderStatusText = (TextView) view.findViewById(R.id.loader_status_text);
		
		mLoaderSettingsButton = view.findViewById(R.id.loader_settings_button);
		mLoaderSettingsButton.setOnClickListener(this);
		
		mLoaderReloadButton = view.findViewById(R.id.loader_reload_button);
		mLoaderReloadButton.setOnClickListener(this);
		
		mLoaderProgressInfinite = (ProgressBar) view.findViewById(R.id.loader_progress_ininite);
		mLoaderProgress = (ProgressBar) view.findViewById(R.id.loader_progress);
		
		mPassword = (EditText) view.findViewById(R.id.loader_password);
		
		mLoginButton = view.findViewById(R.id.loader_login);
		mLoginButton.setOnClickListener(this);
		
		mControls = view.findViewById(R.id.loader_controls);
		mLoginControls = view.findViewById(R.id.loader_login_controls);
		
		loadMediaItems();
		
		updateUI();
	}
	
	/**
	 * 
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (null != mReceiver) {
			MenuApplication.getContext().unregisterReceiver(mReceiver);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (REQUEST_CODE_LOCK == requestCode) {
			
			if(Activity.RESULT_OK == resultCode) {
				
				loadMediaItems();
				updateUI();
			}
			else {
				
				cancelMediaItemsLoading();
				updateUI();
			}
		}
		
		//super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 
	 */
	protected void loadMediaItems() {
		
		mIsLoadingFailed = false;
		mIsUserLeavingActivity = false;
		mMediaItemsFailed = new ArrayList<MediaItem>();
		mMediaItemResultsMapping = new LinkedHashMap<String, Result>();
		mMediaItemsLeftToLoad = new HashMap<String, MediaItem>();
		mBackedUpFolders = new ConcurrentHashMap<String, String>();
		
		new AsyncTask<Void, Void, List<ResourceOperation>>() {

			@Override
			protected List<ResourceOperation> doInBackground(Void... params) {
				
				List<ResourceOperation> operations = null;
				Map<String, String> backedUpFolders = new ConcurrentHashMap<String, String>();
				
				if (isServerAccessible()) {
					if (null != (operations = ResourcesManager.getResourceOperations())) {
						
						for(ResourceOperation op : operations) {
							
							getFoldersToBackup(op, backedUpFolders);
						}
						
						// backup folders
						backupFolders(backedUpFolders);
					}
				}

				mBackedUpFolders = backedUpFolders;
				return operations;
			}
			
			@Override
			protected void onPostExecute(List<ResourceOperation> result) {
				publishOperations(result);
			}
			
		}.execute();
	}
	
	protected void publishOperations(List<ResourceOperation> operations) {
		
		mResourceOperations = operations;
		
		if (null == operations) {
			if (!PreferencesUtils.isApplicationInDemoMode(getActivity())) {
				LoggerService.sendMessage(MenuApplication.getContext(), 
						new LogMessage(System.currentTimeMillis(), (mIsForceLoadAllItems ? Stage.DOWNLOAD : Stage.UPDATE),
								LogMessage.Status.FAILED, "Failed to connect to server."));
				if (mIsForceLoadAllItems) {
					onFirstLoadFailed();
				} else {
					startActivityForResult(ResourcesLockActivity.getLaunchIntent(MenuApplication.getContext()), REQUEST_CODE_LOCK);
				}
			} else {
				onLoadMediaItemsSkipped();
			}
		} else {
			
			int loadOperations = 0;
			for (ResourceOperation operation : operations) {
			
				if (null != operation.mediaItems) {
					for (MediaItem mediaItem : operation.getMediaItems()) {
						
						switch (operation.getOperation()) {
						case LOAD:
						
							mMediaItemResultsMapping.put(mediaItem.getServerUri(), new Result().setMediaItem(mediaItem));
							mMediaItemsLeftToLoad.put(mediaItem.getServerUri(), mediaItem);
							MediaLoader.load(getActivity(), REQUEST_CODE_LOAD, mediaItem);
							
							++loadOperations;
							
							break;
						
						case REMOVE:
	
							File file = new File(mediaItem.getStorageUri());
							if (file.exists()) {
								if (file.isDirectory()) {
									try {
										FileUtils.deleteDirectory(file);
									} catch (IOException e) {
										Log.e("TAGTAG", "Couldn't delete: " + file, e);
									}
								} else {
									FileUtils.deleteQuietly(file);
								}
							}
							
							break;
						}
					}
				}
			}
			
			if (0 == loadOperations) {
				// Nothing to load.
				onLoadMediaItemsSkipped();
			}
		}
		
		updateUI();
	}
	
	@Override
	public void onClick(final View v) {
		
		if (mIsUserLeavingActivity) {
			return;
		}
		
		if (v == mLoaderReloadButton) {
			
			// Don't proceed as user wants to redownload resources
			mHandler.removeCallbacksAndMessages(null);
			
			cancelMediaItemsLoading();
			loadMediaItems();
			
		} else if (v == mLoaderSettingsButton) {
			
			mShowPassword = true;
			
		} else if (v == mLoginButton) {
			
			if (PreferencesUtils.getTablePassword(MenuApplication.getContext()).equals(String.valueOf(mPassword.getText()))) {
				
				// Don't proceed as user wants to change settings
				mHandler.removeCallbacksAndMessages(null);
				// Move back resources
				restoreFolders(mBackedUpFolders);
				//ResourcesManager.moveResroucesFromBackupToRoot();
				
				mIsUserLeavingActivity = true;
				mListener.onSettings();
				cancelMediaItemsLoading();
				
			} else {
//				CancelableToast.showNotificationAtBottom(getFragmentManager(), R.string.wrong_password);
				// Funs.getToast(getActivity(), getResources().getString(R.string.wrong_password));
				new CancelableToast.Config()
					.setText(R.string.wrong_password)
					.setVerticalAlignment(VerticalAlignment.BOTTOM)
					.show(getFragmentManager());
			}
			
		}
		
		updateUI();
	}
	
	private void cancelMediaItemsLoading() {
		mIsLoadingFailed = true;
		MediaLoader.cancel(getActivity());
		for (Result result : mMediaItemResultsMapping.values()) {
			if (null == result.getStatus()) {
				result.setStatus(Status.FAILURE);
				mMediaItemsFailed.add(result.getMediaItem());
			}
		}
		mMediaItemsLeftToLoad.clear();
		restoreFolders(mBackedUpFolders);
	}
	
	protected void updateUI() {
		
		if (!isAdded() || isDetached()) {
			return;
		}
		
		if (mIsUserLeavingActivity) {
			return;
		}
		
		int totalCount = mMediaItemResultsMapping.size();
		int leftToLoadCount = mMediaItemsLeftToLoad.size();
		
		if (mIsLoadingFailed || 0 < mMediaItemsFailed.size()) {
			
			mLoaderTitle.setText(R.string.data_loading_error);
			mLoaderReloadButton.setVisibility(View.VISIBLE);
			
			mLoaderStatusText.setVisibility(View.INVISIBLE);
			mLoaderProgress.setVisibility(View.INVISIBLE);
			
			mLoaderProgressInfinite.setVisibility(View.INVISIBLE);
			
		} else {
			mLoaderTitle.setText(R.string.data_loading);
			mLoaderReloadButton.setVisibility(View.GONE);
		
			mLoaderStatusText.setVisibility(View.VISIBLE);
			mLoaderStatusText.setText(
					getString(R.string.data_loading_status, totalCount - leftToLoadCount, totalCount));
			
			mLoaderProgress.setVisibility(View.VISIBLE);
			mLoaderProgress.setMax(totalCount);
			mLoaderProgress.setProgress(totalCount - leftToLoadCount);
			
			mLoaderProgressInfinite.setVisibility(View.VISIBLE);
		}
		
		if (mShowPassword) {
			mControls.setVisibility(View.GONE);
			mLoginControls.setVisibility(View.VISIBLE);
		}
		
	}

	/**
	 * 
	 */
	private void onLoadMediaItemsSkipped() {
    	
    	//ResourcesManager.moveResroucesFromBackupToRoot();
		restoreFolders(mBackedUpFolders);
    	
    	onLoadMediaItemsCompleted();
    }
	
    private void onLoadMediaItemsCompleted() {
    	
    	ResourcesManager.setResourceOperationsCompleted(mResourceOperations);
    	
        // Request the menu data from the server and save them to the database
        // after all media items were successfully loaded to have possibility
        // to check whether images are loaded for the corresponding products.
    	
        new MenuSaver((MenuApplication) getActivity().getApplication()).saveMenu(
        		new OnMenuSavedListener() {
					@Override
					public void onMenuSaved(boolean isSuccess) {
						
						PreferencesUtils.setMenuLoaded(MenuApplication.getContext(), isSuccess);
						
						LoggerService.sendMessage(MenuApplication.getContext(),
							new LogMessage(
									System.currentTimeMillis(), Stage.GET_MENU,
									isSuccess ? LogMessage.Status.SUCCESS : LogMessage.Status.FAILED,
									"get menu and save it operations"));
						
						if (!isSuccess && mIsForceLoadAllItems) {
							onFirstLoadFailed();
						} else {
							proceed(false);
							updateUI();
						}
					}
				});
        
        updateUI();
    }
    
    protected void proceed(final boolean errorState) {
    	
    	long now = System.currentTimeMillis();
    	long delay = SHOW_TIME_MINIMUM - (now - mStartTime);
    	
    	if (0 < delay) {
    		
    		mHandler.postDelayed(new Runnable() {
    			
    			@Override
    			public void run() {
    				
    				mIsUserLeavingActivity = true;
    				mListener.onContinue(errorState);
    			}
    		}, SHOW_TIME_MINIMUM);
    	} 
    	else {
    		mIsUserLeavingActivity = true;
    		mListener.onContinue(errorState);
    	}
    }
    
	public class MediaItemBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			int requestCode = intent.getIntExtra(MediaLoader.ARG_REQUEST_CODE, -1);
			ArrayList<Result> results = intent.getParcelableArrayListExtra(MediaLoader.ARG_RESULTS);

			switch (requestCode) {
			
			case REQUEST_CODE_LOAD:

				if (BuildConfig.DEBUG) {
					
					Log.d(TAG, "Request code: " + requestCode + ", results: " + results);
				}
				
				if (null != results && !mIsLoadingFailed) {
					
					int size = results.size();
					Result result;
					
					for (int i = 0; i < size; i++) {
						
						result = results.get(i);
						
						MediaItem mediaItem = result.getMediaItem();
						Category category=Category.getCategoryFromPath(mediaItem.getStorageUri());
						LogMessage.Stage itemStage=null;
						
						if (category!=null) {
							switch (category) {
								case DASHBOARD: itemStage=LogMessage.Stage.LOAD_DASHBOARD; break;
								case MENU_SMALL: itemStage=LogMessage.Stage.LOAD_MENU_PICTURES; break;
								case MENU_BIG: itemStage=LogMessage.Stage.LOAD_MENU_PICTURES; break;
								case MENU_ORIGINAL: itemStage=LogMessage.Stage.LOAD_MENU_PICTURES; break;
								case MENU: itemStage=LogMessage.Stage.LOAD_MENU_PICTURES; break;
								case VIDEO: itemStage=LogMessage.Stage.LOAD_VIDEO; break;
								case ADVERTISEMENT: itemStage=LogMessage.Stage.LOAD_ADVERTISEMENT; break;
								default: itemStage=mIsForceLoadAllItems ? Stage.DOWNLOAD : Stage.UPDATE; break;
							}
						}
						
						mMediaItemResultsMapping.put(mediaItem.getServerUri(), result);
						mMediaItemsLeftToLoad.remove(mediaItem.getServerUri());
						
						if (Status.FAILURE == result.getStatus()) {
							
							mMediaItemsFailed.add(mediaItem);
							
							
							LoggerService.sendMessage(MenuApplication.getContext(), 
									new LogMessage(System.currentTimeMillis(), itemStage, LogMessage.Status.FAILED, mediaItem.getServerUri()));
						}
						else {
							
							LoggerService.sendMessage(MenuApplication.getContext(), 
									new LogMessage(System.currentTimeMillis(), itemStage, LogMessage.Status.SUCCESS, mediaItem.getServerUri()));
							
							restoreNotNeeded(mBackedUpFolders, mediaItem);
						}						
					}

					// Cancel loading right away if some item failed to load.
					if (0 != mMediaItemsFailed.size()) {
						
						// Cancel media items loading
						cancelMediaItemsLoading();
						
						if (mIsForceLoadAllItems) {
							onFirstLoadFailed();
						} else {
							onLoadMediaItemsSkipped();
						}
						
					} else if (0 == mMediaItemsLeftToLoad.size()) {
						
                        // All items has been loaded.
						onLoadMediaItemsCompleted();
                    }
					
					updateUI();
				}
				
				break;

			default:

				Log.w(TAG, "Unknown request code: " + requestCode + ", results: " + results);
				
			}
		}
	}

	/**
	 * Process the operation and fill the map with the folders that requires backup. Map key is
	 * original dest folder, map value is backup folder
	 * @param op
	 * @param foldersToBackUp
	 */
	private static void getFoldersToBackup(ResourceOperation op, Map<String, String> foldersToBackUp) {
		
		if(ResourceOperation.Operation.LOAD == op.getOperation()) {
			
			List<MediaItem> items = op.getMediaItems();
			
			for(MediaItem item : items) {
				
				String folderToBackup = getDestinationFolder(item);
				String backupFolder = getBackupFolder(item);
				
				if( (null != folderToBackup) &&
					(null != backupFolder) &&
					(false == foldersToBackUp.containsKey(folderToBackup)) ) {
					
					foldersToBackUp.put(folderToBackup, backupFolder);					
				}
			}
		}
	}
	
	/**
	 * 
	 * @param foldersToBackup
	 */
	private static void backupFolders(Map<String, String> foldersToBackup) {
		
		for(Iterator<Entry<String, String>> it = foldersToBackup.entrySet().iterator(); it.hasNext();) {
			
			Entry<String, String> entry = it.next();
			File dst = new File(entry.getValue());
			File src = new File(entry.getKey());
			
			ResourcesManager.move(src, dst);
		}
	}
	
	/**
	 * 
	 * @param backedUpFolders
	 */
	private static void restoreFolders(Map<String, String> backedUpFolders) {
		
		for(Iterator<Entry<String, String>> it = backedUpFolders.entrySet().iterator(); it.hasNext();) {
			
			Entry<String, String> entry = it.next();
			File dst = new File(entry.getValue());
			File src = new File(entry.getKey());
			
			ResourcesManager.move(dst, src);			
		}
		
		backedUpFolders.clear();
	}
	
	/**
	 * 
	 * @param foldersToBackUp
	 * @param item
	 */
	private static void restoreNotNeeded(Map<String, String> foldersToBackUp, MediaItem item) {
		
		foldersToBackUp.remove(getDestinationFolder(item));
	}
	
	/**
	 * 
	 * @param item
	 * @return
	 */
	private static String getDestinationFolder(MediaItem item) {
		
		return getFolderFromFilePath(item.getStorageUri());
	}
	
	/**
	 * 
	 * @param item
	 * @return
	 */
	private static String getBackupFolder(MediaItem item) {
	
		return getBackupFolderForPath(getDestinationFolder(item));
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	private static String getFolderFromFilePath(String path) {
		
		int index = path.length();
		
		if(-1 != path.lastIndexOf('.')) {
			
			if(-1 == (index = path.lastIndexOf(File.separator))) {
				
				return null;
			}
		}
		
		return path.substring(0, index).trim();
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	private static String getBackupFolderForPath(String path) {
		
		if(path.contains(File.separator + "SQIWY" + File.separator)) {
			
			return path.replace(File.separator + "SQIWY" + File.separator, File.separator + "SQIWY_BACKUP" + File.separator);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	private static boolean isServerAccessible() {
	
		MenuApplication.initOperationService();
		
		try {
			if (!PreferencesUtils.isApplicationInDemoMode(MenuApplication.getContext())) {
				MenuApplication.getOperationService().ping();
			}
			return true;
		}
		catch (Exception e) {
			
		} 
		
		return false;		
	}
	
	private void onFirstLoadFailed() {
		
		PreferencesUtils.setBackendConfigCompleted(MenuApplication.getContext(), false);
		
		// First load of resources failed.
		// Show settings screen so someone can tune something and restart resource loading.
		Activity activity = getActivity();
		activity.finish();
        MenuApplication.mIsCheckResources = false;
        FlowUtils.continueSetupFlow(activity);
	}
}