package com.sqiwy.dashboard;

import java.util.Observable;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.fosslabs.android.utils.Funs;
import com.google.common.base.Objects;
import com.sqiwy.dashboard.model.map.FloorEx;
import com.sqiwy.dashboard.model.map.Shop;
import com.sqiwy.dashboard.util.StatsUtils.StatsContext;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.advertisement.Advertisement;
import com.sqiwy.menu.cm.CMMenuGrandFragment;
import com.sqiwy.menu.util.UIUtils;

/**
 * Created by abrysov
 */

public final class DBMapActivity extends Activity implements CMMenuGrandFragment.Callbacks,
		DBMapFragment.OnMapFragmentCallbacks {

	/**
	 * variables
	 */
	private DBMapPanelFragment mPanel = null;
	private DBMapFragment mMap = null;
	private SharedData mSharedData;

	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_map);
		
		// set the background
		findViewById(R.id.map_root).setBackgroundDrawable(new BitmapDrawable(((MenuApplication) getApplication()).getDashboardBackground()));
		
		mMap = (DBMapFragment)getFragmentManager().findFragmentById(R.id.fragment_map);
		mPanel = (DBMapPanelFragment)getFragmentManager().findFragmentById(R.id.fragment_map_panel);
		
		// initialize shared data
		mSharedData = new SharedData(null, null, false);
		mPanel.setSharedData(mSharedData);
		mMap.setSharedData(mSharedData);
		
		// set the title
		View titleContainer = findViewById(R.id.ll_menu);
		LayoutParams lp = titleContainer.getLayoutParams();
		lp.width = LayoutParams.WRAP_CONTENT;
		titleContainer.setLayoutParams(lp);
		((TextView)findViewById(R.id.tv_menu)).setText(getResources().getString(R.string.map_title));
		
		// instantiate ads
		FragmentManager fm = getFragmentManager();
		
		if(null == fm.findFragmentByTag("ads")) {
			
			Fragment f = DBGalleryCommercialFragment.newInstacne(null, StatsContext.MAP, true, Advertisement.Places.VENUE_MAP);
			fm.beginTransaction().add(R.id.map_ads_container, f, "ads").commit();	
		}				
	}
	
	/**
	 * 
	 */
	@Override
	public void onConfigurationChanged() {
		UIUtils.toggleOrientation(this);
	}

	/**************************************************************************************************************
	 * 
	 * 										DBMapPanelFragment.OnMapPanelEvents
	 * 
	 **************************************************************************************************************/

	/**
	 * 
	 */
	@Override
	public void onShopSelected(DBMapFragment fragment, Shop shop) {

		fragment.showTooltipForShop(shop);
	}
	
	/**
	 *
	 */
	public static class SharedData extends Observable {

		/**
		 * consts
		 */
		public static String PROP_FLOOR_PICKER = "floor_picker";
		public static String PROP_SELECTED_CATEGORY = "selected_category";
		public static String PROP_FLOOR = "floor";
		
		/**
		 * variables
		 */
		private boolean mIsFloorPickerVisible = false;
		private String mSelectedCategory = null;
		private FloorEx mCurrentFloor = null;
		
		/**
		 * 
		 * @param currentFloor
		 * @param selectedCategory
		 * @param isFloorPickerVisible
		 */
		public SharedData(FloorEx floor, String selectedCategory, boolean isFloorPickerVisible) {
			
			setIsFloorPickerVisible(isFloorPickerVisible);
			setSelectedCategory(selectedCategory);
			setCurrentFloor(floor);
		}
		
		/**
		 * 
		 * @param floor
		 * @return
		 */
		public SharedData setCurrentFloor(FloorEx floor) {
			
			if(mCurrentFloor != floor) {
				
				mCurrentFloor = floor;
				setChanged();
				notifyObservers(PROP_FLOOR);
			}
			
			return this;
		}
		
		/**
		 * 
		 * @param selectedCategory
		 * @return
		 */
		public SharedData setSelectedCategory(String selectedCategory) {
			
			if(false == Objects.equal(mSelectedCategory, selectedCategory)) {

				mSelectedCategory = selectedCategory;
				setChanged();
				notifyObservers(PROP_SELECTED_CATEGORY);
			}
			
			return this;
		}
		
		/**
		 * 
		 * @param isFloorPickerVisible
		 * @return
		 */
		public SharedData setIsFloorPickerVisible(boolean isFloorPickerVisible) {
			
			if(mIsFloorPickerVisible != isFloorPickerVisible) {
				
				mIsFloorPickerVisible = isFloorPickerVisible;
				setChanged();
				notifyObservers(PROP_FLOOR_PICKER);
			}
			
			return this;
		}
		
		/**
		 * 
		 * @return
		 */
		public String getSelectedCategory() {
			
			return mSelectedCategory;
		}
		
		/**
		 * 
		 * @return
		 */
		public boolean getIsFloorPickerVisible() {
			
			return mIsFloorPickerVisible;
		}
		
		/**
		 * 
		 * @return
		 */
		public FloorEx getCurrentFloor() {
			
			return mCurrentFloor;
		}
	}
}
