package com.sqiwy.menu.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.fosslabs.android.utils.JSFileWorker;
import com.fosslabs.android.utils.JSImageWorker;
import com.sqiwy.backend.api.ClientConfig;
import com.sqiwy.medialoader.MediaItem;
import com.sqiwy.medialoader.util.FileUtil;
import com.sqiwy.medialoader.util.FileUtil.StorageException;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.resource.ResourceOperation.Operation;

/**
 * Created by abrysov
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
	
	static {
		// Init root dir for the app.
		
		File root = null;
		// Get storage to load data to
		try {
			File storage = FileUtil.getStorage(false);
			
			// Create root dir of our app
			root = new File(storage, "SQIWY");
			if (!root.exists() && !root.mkdir()) {
				Log.e(TAG, "Cannot create: " + root.getAbsolutePath());
				root = null;
			}
			
		} catch (StorageException e) {
			Log.e(TAG, "Stroage is not available", e);
		}

		ROOT_DIR = root;
	}
	
	/**
	 * Available resource categories.
	 */
	public static enum Category {
		DASHBOARD("DASHBOARD"),
		MENU_SMALL("MENU/small"),
		MENU_BIG("MENU/big"),
		MENU_ORIGINAL("MENU/original"),
		MENU("MENU"), 
		VIDEO("VIDEO"),
		ADVERTISEMENT("ADVERTISEMENT");
		
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
	
	public static File getResourcePath(String resourceName, Category category) {
		return category.getResource(resourceName);
	}
	
	public static Drawable getDrawableResource(Context context, String resourceName, Category category) {
		File resourcePath = category.getResource(resourceName);
		
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
	
	/**
	 * We have JSON file which describes what and where to load and save.
	 * 
	 * Should be invoked on non-UI thread (use AsyncTask for instance).
	 * 
	 * <strong>
	 * Don't forget to register completed operations with {@link ResourcesManager#setResourceOperationsCompleted(List)}
	 * method</strong>
	 * 
	 * @return list of resources ({@link MediaItem}s) to load.
	 */
	public static List<ResourceOperation> getResourceOperations() {

		// Prepare root url
		ClientConfig config = MenuApplication.getBackendClientConfig();
		Context context = MenuApplication.getContext();
		
		String rootUrl = "http://" + config.getHostName() + "/resources/downloads/";

		//String resourcesToDownload = "https://drive.google.com/uc?export=download&id=0B0lKX51T_sQFaEtibzFTUkUyeGc";
		String resourcesToDownload = "http://" + config.getHostName() + "/resources/downloads/resources.json";
		
		List<ResourceOperation> result = null;
		
		String newResourcesJSON = null;
		try {
			newResourcesJSON = IOUtils.toString(new URL(resourcesToDownload));
			
			List<MediaItem> newItems = parseMediaItems(newResourcesJSON, rootUrl);
			
			List<MediaItem> oldItems = getOldMediaItems(context);
			
			result = buildOperations(newItems, oldItems);
			
		} catch (Exception e) {
			Log.e(TAG, "Error while downloading/reading media item list.", e);
		}
		
		return result;
	}
	
	/**
	 * All completed resource operations should be registered with this method.
	 * @param operations completed resource operations.
	 */
	public static synchronized void setResourceOperationsCompleted(List<ResourceOperation> operations) {

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
		for (MediaItem item : oldItems) {
			elements.put(item.getStorageUri(), item);
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
	
	private static List<MediaItem> parseMediaItems(String json, String rootUrl) {
		List<MediaItem> result = new ArrayList<MediaItem>();
		
		if (null != json) {
			JSONArray resources;
			
			try {
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
					item.setServerUri(uri);
					
					Category category = Category.valueOf(resourcesJSON.getString("category"));
					
					File storage = new File(category.getDirPath());
					
					String name = resourcesJSON.optString("name", null);
					if (!TextUtils.isEmpty(name)) {
						storage = new File(storage, name);
					}
					item.setStorageUri(storage.getAbsolutePath());
					
					item.setVersion(resourcesJSON.optInt("version", 0));
					
					result.add(item);
				}
			} catch (JSONException e) {
				Log.e(TAG, "Error while parsing resources.json", e);
			}
		}
		
		return result;
	}
	
	public static File getRootDir() {
		return ROOT_DIR;
	}
	
	
}
