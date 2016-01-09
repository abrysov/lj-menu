package com.sqiwy.dashboard.model.map;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * Created by abrysov
 */

public class MapDescriptor {

	/**
	 * variables
	 */
	
	@SerializedName("floors")
	Map<Integer, Floor> mFloors = null;
	
	@SerializedName("cat")
	Map<String, Shop[]>  mCategories = null;
	
	/**
	 * 
	 * @param stream
	 * @return
	 */
	public final static MapDescriptor load(InputStream stream) {
		
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(new InputStreamReader(stream), MapDescriptor.class);
	}
	
	/**
	 * 
	 */
	public MapDescriptor() {
		
		mFloors = null;
		mCategories = null;
	}
	
	/**
	 * 
	 * @param floorMaps
	 * @param categories
	 */
	public MapDescriptor(Map<Integer, Floor> floors, Map<String, Shop[]> categories) {
		
		mFloors = floors;
		mCategories = categories;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<String> getCategories() {

		List<String> res = new ArrayList<String>();

		if (null != mCategories) {

			for(Iterator<Entry<String, Shop[]>> itCategories = mCategories.entrySet().iterator(); itCategories.hasNext(); ) {
				
				res.add(itCategories.next().getKey());
			}
		}

		return res;
	}
	
	/**
	 * 
	 * @param categoryName
	 * @return
	 */
	public Shop[] getShopsByCategory(String categoryName) {

		if( (null != mCategories) &&
			(mCategories.containsKey(categoryName)) ){

			return mCategories.get(categoryName);
		}

		return null;
	}
	
	/**
	 * 
	 * @param shopId
	 * @return
	 */
	public Shop findShopById(int shopId) {
		
		if(null != mCategories) {

			for(Iterator<Entry<String, Shop[]>> itCategories = mCategories.entrySet().iterator(); itCategories.hasNext(); ) {
				
				for(Shop shop : itCategories.next().getValue()) {
					
					if(shop.getId() == shopId) {
						
						return shop;
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Shop> getAllShops() {
		
		List<Shop> shops = new ArrayList<Shop>();
		
		if(null != mCategories) {
		
			for(Iterator<Entry<String, Shop[]>> itCategories = mCategories.entrySet().iterator(); itCategories.hasNext(); ) {
				
				shops.addAll(Arrays.asList(itCategories.next().getValue()));
			}
		}
				
		return shops;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Shop> getShopsByFloor(int floor) {
		
		List<Shop> shops = new ArrayList<Shop>();
		
		if(null != mCategories) {
		
			for(Iterator<Entry<String, Shop[]>> itCategories = mCategories.entrySet().iterator(); itCategories.hasNext(); ) {
				
				for(Shop shop : itCategories.next().getValue()) {
					
					if(shop.getFloor() == floor) {
						
						shops.add(shop);
					}
				}
			}
		}
				
		return shops;
	}
	
	/**
	 * 
	 * @param floor
	 * @return
	 */
	public Shop getShopByFloorAndId(int floor, int id) {
		
		if(null != mCategories) {
		
			for(Iterator<Entry<String, Shop[]>> itCategories = mCategories.entrySet().iterator(); itCategories.hasNext(); ) {
				
				for(Shop shop : itCategories.next().getValue()) {
					
					if( (shop.getFloor() == floor) &&
						(shop.getId() == id) ) {
						
						return shop;
					}
				}
			}
		}
				
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getFloorsCount() {
		
		return ((null != mFloors) ? mFloors.entrySet().size() : 0);
	}
	
	/**
	 * 
	 * @param floor
	 * @return
	 */
	public String getFloorMap(int _floor) {
		
		Integer floor = Integer.valueOf(_floor);
		
		if( (null != mFloors) &&
			(mFloors.containsKey(floor)) ) {
			
			return mFloors.get(floor).getMapImage();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<FloorEx> getFloors() {
	
		List<FloorEx> res = Lists.newArrayList();
		
		if(null != mFloors) {
			
			for(Iterator<Entry<Integer, Floor>> itFloors = mFloors.entrySet().iterator(); itFloors.hasNext(); ) {
				
				Entry<Integer, Floor> entry = itFloors.next();
				res.add(FloorEx.create(entry.getValue(), entry.getKey()));
			}
		}
		
		return res;
	}
	
	/**
	 * 
	 * @param floor
	 * @return
	 */
	public Floor getFloorInfo(int floor) {
	
		if(null != mFloors) {
			
			return mFloors.get(floor);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param floor
	 * @return
	 */
	public FloorEx getFloorInfoEx(int floor) {
		
		if( (null != mFloors) &&
			(null != mFloors.get(floor)) ) {
			
			return FloorEx.create(mFloors.get(floor), floor);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public FloorEx getMinFloor() {
		
		if(mFloors.keySet().size() > 0) {
			
			Integer min = Collections.min(mFloors.keySet());
			return FloorEx.create(mFloors.get(min), min);
		}
		
		return null;
	}
}
