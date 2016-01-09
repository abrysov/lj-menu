package com.sqiwy.menu.resource;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.fosslabs.android.utils.JSFileWorker;
import com.fosslabs.android.utils.JSImageWorker;
import com.fosslabs.android.utils.JSLog;
import com.sqiwy.restaurant.api.ClientConfig;
import com.sqiwy.dashboard.R;
import com.sqiwy.dashboard.logger.LogMessage;
import com.sqiwy.dashboard.logger.LogMessage.Stage;
import com.sqiwy.dashboard.logger.LogMessage.Status;
import com.sqiwy.dashboard.logger.LoggerService;
import com.sqiwy.medialoader.MediaItem;
import com.sqiwy.medialoader.util.FileUtil;
import com.sqiwy.medialoader.util.FileUtil.StorageException;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.resource.ResourceOperation.Operation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abrysov
 */

/**
 * Stub resources management.
 */
public class ResourcesManager {

	private static final String TAG = ResourcesManager.class.getName();
	
	private static final String PREF_RESOURCES = "RESOURCES";
	private static final String PREF_LOADED = "loaded0";// "loaded"
	
	/**
	 * Root directory of the app.
	 */
	private static final File ROOT_DIR;
	private static final File BACKUP_DIR;
	
	static {
		// Init dirs for the app.
		
		File root = null;
		File backup = null;
		// Get storage to load data to
		try {
			File storage = FileUtil.getStorage(false);
			
			// Create root dir of our app
			root = new File(storage, "SQIWY");
			if (!root.exists() && !root.mkdir()) {
				Log.e(TAG, "Cannot create: " + root.getAbsolutePath());
				root = null;
			}
			
			// Create dir for resources backup
			backup = new File(storage, "SQIWY_BACKUP");
			if (!backup.exists() && !backup.mkdir()) {
				Log.e(TAG, "Cannot create: " + backup.getAbsolutePath());
				backup = null;
			}
			
		} catch (StorageException e) {
			Log.e(TAG, "Stroage is not available", e);
		}

		ROOT_DIR = root;
		BACKUP_DIR = backup;
	}
	
	/**
	 * Available resource categories.
	 */
	public static enum Category {
		BASE("."),
        DASHBOARD("DASHBOARD"),
		MENU_SMALL("MENU/small"),
		MENU_BIG("MENU/big"),
		MENU_ORIGINAL("MENU/original"),
		MENU("MENU"), 
		VIDEO("VIDEO"),
		ADVERTISEMENT("ADVERTISEMENT"),
        MAP("MAP");
		
		private String mDirName;
		private File mDir;
		
		private Category(String dir) {
			mDirName = dir;
			mDir = new File(ROOT_DIR, dir);
		}
		
		public String getDirName() {
			return mDirName;
		}
		
		public String getDirPath() {
			return mDir.getAbsolutePath();
		}
		
		public File getResource(String resourceName) {
			return new File(mDir, resourceName);
		}
		
		public static Category getCategoryFromPath(String path) {
			Category ret=null;
			int p=0;
			String rootPath=ROOT_DIR.getAbsolutePath(), categoryPath;
			
			if (path.startsWith(rootPath)) {
				p+=rootPath.length();
				if (p<path.length() && path.charAt(p)=='/') {
					p++;
				}
				categoryPath=path.substring(p);
				if (categoryPath.startsWith("DASHBOARD")) {
						ret=DASHBOARD;
				} else if (categoryPath.startsWith("MENU/small")) {
					ret=MENU_SMALL;
				} else if (categoryPath.startsWith("MENU/big")) {
					ret=MENU_BIG;
				} else if (categoryPath.startsWith("MENU/original")) {
					ret=MENU_ORIGINAL;
				} else if (categoryPath.startsWith("MENU")) {
					ret=MENU;
				} else if (categoryPath.startsWith("VIDEO")) {
					ret=VIDEO;
				} else if (categoryPath.startsWith("ADVERTISEMENT")) {
					ret=ADVERTISEMENT;
				} else {
					ret=BASE;
				}
			}
			
			return ret;
		}
		
		
	}
	
	public static boolean areResourcesLoaded(Context context) {
		return getPrefs(context).getBoolean(PREF_LOADED, false);
	}
	
	public static void setResourcesLoaded(Context context, boolean loaded) {
		getPrefs(context).edit().putBoolean(PREF_LOADED, loaded).commit();
	}
	
	private static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences(PREF_RESOURCES, Context.MODE_PRIVATE);
	}
	
	public static File getRootDir() {
		return ROOT_DIR;
	}
	
	public static File getResourcePath(String resourceName, Category category) {
		return category.getResource(resourceName);
	}
	
	public static Drawable getDrawableResource(Context context, String resourceName, Category category) {
        File resourcePath;

        if (category == Category.DASHBOARD) {
            resourcePath = new File (ROOT_DIR + "/" + MenuApplication.getContext().getString(R.string.dashboard_resources), resourceName );
        }else{
            resourcePath = category.getResource(resourceName);
        }

		BitmapDrawable bd = new BitmapDrawable(context.getResources(),
				JSImageWorker.loadFromFile(resourcePath.getAbsolutePath()));
		
		return bd;
	}
	
	public static JSONObject getJsonResource(Context context, String resourceName, Category category) throws JSONException {
		String json = JSFileWorker.loadDataFromFile(context, category.getResource(resourceName).getAbsolutePath());
		
		if (TextUtils.isEmpty(json)) {
			return null;
		} else {
			return new JSONObject(json);
		}
	}
	
	public synchronized static void moveResourcesFromRootToBackup() {
		move(ROOT_DIR, BACKUP_DIR);
	}
	
	public synchronized static void moveResroucesFromBackupToRoot() {
		move(BACKUP_DIR, ROOT_DIR);
	}
	
	public static void move(File fromDir, File toDir) {
		try {
			if (fromDir.exists() && 0 < fromDir.list().length) {
				FileUtils.deleteDirectory(toDir);
				FileUtils.moveDirectory(fromDir, toDir);
				
				Log.i(TAG, fromDir.getAbsolutePath() + " has been moved to " + toDir.getAbsolutePath());
			} else {
				Log.w(TAG, "Cannot move " + fromDir.getAbsolutePath() + " because it doesn't exist or empty");
			}
		} catch (IOException e) {
			Log.e(TAG, "Cannot move " + fromDir.getAbsolutePath() + " to " + toDir.getAbsolutePath(), e);
		}
	}

    public static String getBaseResourcesUrl() throws Throwable {
        ClientConfig config = MenuApplication.getBackendClientConfig();
        String rootUrl = config.getWebUrl() + "/resources/downloads/";
        return rootUrl;
    }
	
	/**
	 * We have JSON file which describes what and where to load and save.
	 * 
	 * Should be invoked on non-UI thread (use AsyncTask for instance).
	 * 
	 * <strong>
	 * Don't forget to register completed operations with {@link com.sqiwy.menu.resource.ResourcesManager#setResourceOperationsCompleted(java.util.List)}
	 * method</strong>
	 *
	 * @return list of resources ({@link com.sqiwy.medialoader.MediaItem}s) to load (<code>null</code> if error occurred).
	 */
	public static List<ResourceOperation> getResourceOperations() {

		List<ResourceOperation> result = null;
		String newResourcesJSON, resourcesToDownload = null;
        Context context = MenuApplication.getContext();

		try {
            String rootUrl = getBaseResourcesUrl();


            //String resourcesToDownload = "https://drive.google.com/uc?export=download&id=0B0lKX51T_sQFaEtibzFTUkUyeGc";
            resourcesToDownload = rootUrl + "resources.json";
            JSLog.d(TAG, "resources to download = " + resourcesToDownload);

			newResourcesJSON = IOUtils.toString(new URL(resourcesToDownload));
			
			List<MediaItem> newItems = parseMediaItems(newResourcesJSON, rootUrl);
			List<MediaItem> oldItems = getOldMediaItems(context);
			
			result = buildOperations(newItems, oldItems);
			LoggerService.sendMessage(context,
					new LogMessage(System.currentTimeMillis(), Stage.LOAD_RESOURCES, Status.SUCCESS, resourcesToDownload));
		} catch (IOException e) {
			LoggerService.sendMessage(context,
				new LogMessage(System.currentTimeMillis(), Stage.LOAD_RESOURCES, Status.FAILED, "Error while downloading '"+resourcesToDownload+"'"));
		} catch (JSONException e) {
			LoggerService.sendMessage(context,
					new LogMessage(System.currentTimeMillis(), Stage.LOAD_RESOURCES, Status.FAILED, "Error while parsing '"+resourcesToDownload+"'"));
		} catch (Exception e) {
			LoggerService.sendMessage(context,
					new LogMessage(System.currentTimeMillis(), Stage.LOAD_RESOURCES, Status.FAILED, "Other error while processing '"+resourcesToDownload+"'"));
		} catch (Throwable t) {
            JSLog.e(TAG, "failed to get resourcces from server: " + resourcesToDownload);
        }
		
		JSLog.d(TAG, "result = " + result);
		
//		TODO: can be used for testing, to load only one item.
//		if (null != result && 1 < result.size()) {
//			ResourceOperation resourceOperation = result.get(0);
//			result = new ArrayList<ResourceOperation>();
//			MediaItem mediaItem = resourceOperation.getMediaItems().get(0);
//			resourceOperation.getMediaItems().clear();
//			resourceOperation.getMediaItems().add(mediaItem);
//			result.add(resourceOperation);
//		}
		
		return result;
	}
	
	/**
	 * All completed resource operations should be registered with this method.
	 * @param operations completed resource operations.
	 */
	public static synchronized void setResourceOperationsCompleted(List<ResourceOperation> operations) {

		if (null == operations) {
			return;
		}
		
		List<MediaItem> oldMediaItems = getOldMediaItems(MenuApplication.getContext());
		
		Map<String, MediaItem> mapping = new LinkedHashMap<String, MediaItem>();
		for (MediaItem item : oldMediaItems) {
			mapping.put(item.getStorageUri(), item);
		}
		
		for (ResourceOperation operation : operations) {
			
			if (null != operation.getMediaItems()) {
				
				for (MediaItem item : operation.getMediaItems()) {
				
					switch (operation.getOperation()) {
					case LOAD:
						
						mapping.put(item.getStorageUri(), item);
						
						break;
						
					case REMOVE:
						
						mapping.remove(item.getStorageUri());
						
						break;
					}
				}
					
			}
		}
		
		setOldMediaItems(MenuApplication.getContext(), mapping.values());
	}

	private static List<ResourceOperation> buildOperations(List<MediaItem> newItems, List<MediaItem> oldItems) {
		List<ResourceOperation> result = new ArrayList<ResourceOperation>();
		
		ResourceOperation load = new ResourceOperation(Operation.LOAD);
		load.mediaItems = new ArrayList<MediaItem>();
		
		Map<String, MediaItem> elements = new LinkedHashMap<String, MediaItem>();
		if (null != oldItems) {
			for (MediaItem item : oldItems) {
				elements.put(item.getStorageUri(), item);
			}
		}
		
		for (MediaItem item : newItems) {
			
			MediaItem oldItem = elements.remove(item.getStorageUri());
			
			if (null != oldItem && item.getVersion() <= oldItem.getVersion()) {
				// No need for update
				continue;
			}
			
			load.mediaItems.add(item);
		}
		
		if (0 < load.mediaItems.size()) {
			result.add(load);
		}
		

		if (0 < elements.size()) {
			ResourceOperation operation = new ResourceOperation(Operation.REMOVE);
			operation.mediaItems = new ArrayList<MediaItem>(elements.values());
			result.add(operation);
		}
		
		return result;
	}
	
	private synchronized static List<MediaItem> getOldMediaItems(Context context) {
		List<MediaItem> result = new ArrayList<MediaItem>();
		InputStream is = null;
		try {
			is = context.openFileInput("mediaItems.json");
			
			JSONArray jsonArray  = new JSONArray(IOUtils.toString(is));
			
			int length = jsonArray.length();
			for (int i = 0; i < length; i++) {
				result.add(MediaItem.fromJSON(jsonArray.getJSONObject(i)));
			}
			
		} catch (FileNotFoundException e) {
			Log.w(TAG, "There's no old mediaItems.json (It is correct behavior for the first launch)");
		} catch (Exception e) {
			Log.e(TAG, "Cannot read old mediaItems.json", e);
		} finally {
			IOUtils.closeQuietly(is);
		}
		
		return result;
	}
	
	private synchronized static void setOldMediaItems(Context context, Collection<MediaItem> mediaItems) {
		
		OutputStream os = null;
		try {
			os = context.openFileOutput("mediaItems.json", Context.MODE_PRIVATE);
		
			JSONArray jsonArray = new JSONArray();
			
			for (MediaItem mediaItem : mediaItems) {
				jsonArray.put(mediaItem.toJSON());
			}
			
			IOUtils.write(jsonArray.toString(), os);
			
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Couldn't open/create resources.json", e);
		} catch (Exception e) {
			Log.e(TAG, "Couldn't save json.", e);
		} finally {
			IOUtils.closeQuietly(os);
		}
	}
	
	private static List<MediaItem> parseMediaItems(String json, String rootUrl) throws JSONException {
		List<MediaItem> result = new ArrayList<MediaItem>();
		
		if (null != json) {
			JSONArray resources;
			
			resources = new JSONObject(json).getJSONArray("resources");
		
			int length = resources.length();
			
			for (int i = 0; i < length; i++) {
				
				JSONObject resourcesJSON = resources.getJSONObject(i);
				
				MediaItem item = new MediaItem();
				
				item.setArchive(resourcesJSON.optBoolean("archive", false));
				
				String uri = resourcesJSON.getString("uri");
				
				boolean isAbsolute = resourcesJSON.optBoolean("absolute", false);
				if (!isAbsolute) {
					uri = rootUrl + uri;
				}
				
				
				Category category = Category.valueOf(resourcesJSON.getString("category"));
				
				item.setServerUri(uri);
				
				File storage = new File(category.getDirPath());
				
				String name = resourcesJSON.optString("name", null);
				if (!TextUtils.isEmpty(name)) {
					storage = new File(storage, name);
				}
				item.setStorageUri(storage.getAbsolutePath());
				
				item.setVersion(resourcesJSON.optInt("version", 0));
				
				result.add(item);
			}
		}
		
		return result;
	}
	
}
