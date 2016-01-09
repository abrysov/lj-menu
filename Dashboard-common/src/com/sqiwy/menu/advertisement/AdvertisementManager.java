package com.sqiwy.menu.advertisement;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sqiwy.dashboard.model.action.Action;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.advertisement.Advertisement.Places;
import com.sqiwy.menu.advertisement.Advertisement.Type;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.resource.ResourcesManager.Category;

/**
 * Created by abrysov
 */

public class AdvertisementManager {

	/**
	 * consts
	 */
	private static final String TAG = AdvertisementManager.class.getName();
	
	/**
	 * Return the list of ads (both video and image) 
	 * @return
	 */
	public static List<Advertisement> getAds(Places place) {
		return getAds(null,place);
	}
	
	/**
	 * Return the list of ads of required type. If there is no ads of required type exists on
	 * sd-card then default ads are added
	 * 
	 * @param type
	 * @return
	 */
	public static List<Advertisement> getAds(Advertisement.Type type, Places place) {
		
		List<Advertisement> ads = getAdsImpl(type, "ads", true, place);
		
		if( (null == ads) ||
			(0 == ads.size()) ) {
			
			ads = getAdsImpl(type, "default_ads", false, place);
		}
				
		return ads;
	}
	
	/**
	 * 
	 * @param type
	 * @param root
	 * @param checkFileExistence
	 * @return
	 */
	public static List<Advertisement> getAdsImpl(Advertisement.Type type, String root, boolean checkFileExistence, Places place) {
		
		List<Advertisement> result = new ArrayList<Advertisement>();
		
		try {
			
			JSONObject json = ResourcesManager.getJsonResource(MenuApplication.getContext(), "advertisement.json", Category.ADVERTISEMENT);

			if (null != json) {
			
				JSONArray ads = json.getJSONArray(root);
				int size = ads.length();
				
				for (int i = 0; i < size; i++) {
				
					JSONObject adJson = ads.getJSONObject(i);
					Advertisement a = new Advertisement();
					a.setId(adJson.getInt("id"));
					a.setName(adJson.getString("name"));
					a.setTimeShow(adJson.optInt("showDuration", 0));
					a.setType(Advertisement.Type.valueOf(adJson.getString("type")));
					a.setPlaces(adJson.getJSONArray("places"));
					a.setAction(Action.Resolver.resolve(adJson));
					
					if (null == type || type == a.getType()) {
								
						if( (!checkFileExistence) || (
							(ResourcesManager.getResourcePath(a.getName(), (Type.IMAGE == a.getType() ? Category.ADVERTISEMENT : Category.VIDEO)).exists()) && 
							(a.hasPlace(place)))) {
							result.add(a);
						}
					}
				}
			} 
			else {
				
				Log.e(TAG, "There's no JSON descriptor file for advertisement.");
			}
			
		} 
		catch (JSONException e) {
			
			Log.e(TAG, "Cannot read advertisements: " + root, e);
		}
				
		return result;
	}
}
