package com.sqiwy.dashboard;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.sqiwy.dashboard.DBMapActivity.SharedData;
import com.sqiwy.dashboard.logger.LogMessage;
import com.sqiwy.dashboard.logger.LoggerService;
import com.sqiwy.dashboard.model.map.FloorEx;
import com.sqiwy.dashboard.model.map.MapDescriptor;
import com.sqiwy.dashboard.model.map.Shop;
import com.sqiwy.dashboard.util.ExecutorUtils;
import com.sqiwy.dashboard.util.FileUtils;
import com.sqiwy.dashboard.view.MapView;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.dashboard.model.map.FloorEx.FloorDescendingComparator;

/**
 * Created by abrysov
 */

public class DBMapFragment extends Fragment 
	implements MapView.MapViewCallbacks, Observer {

	/**
	 *
	 */
	public static interface OnMapFragmentCallbacks {

		void onShopSelected(DBMapFragment fragment, Shop shop);
	}
	
	/**
	 * variables
	 */
	private View mMapPickerOverlay = null;	
	private ListView mFloorsList = null;
	private FloorsAdapter mFloorsAdapter = null;
	private MapView mMap = null;
	private MapDescriptor mMapDescriptor = null;
	private ListeningExecutorService mExecutor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
	private ShopsAdapter mShopsAdapter = null;
	private CategoriesAdapter mCategoriesAdapter = null;
	private GridView mPopupItems = null;
	private Integer mRequestedShopTooltip = null; 
	private Integer mSavedFloor = null;	
	private Integer mSavedTooltipShopId = null;
	private String mSavedCategory = null;
	private Boolean mSavedFloorsListVisibility = null;
	private SharedData mSharedData = null;
	private Point mSize = new Point(-1, -1);

	/**
	 * 
	 */
	public DBMapFragment() {
		
		setRetainInstance(false);
	}

	/**
	 * 
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Preconditions.checkArgument(null == mSharedData, "SharedData is not set!");
		
		View root = inflater.inflate(R.layout.fragment_map, null);
		
		//
		mMapPickerOverlay = root.findViewById(R.id.map_picker_overlay);
		mMapPickerOverlay.setVisibility(View.GONE);
		mMapPickerOverlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				mSharedData.setIsFloorPickerVisible(false);
				mSharedData.setSelectedCategory(null);
			}
		});
		
		// setup floors list
		mFloorsAdapter = new FloorsAdapter(getActivity(), null);
		mFloorsList = (ListView)root.findViewById(R.id.map_floors_list);
		mFloorsList.setVisibility(View.GONE);
		mFloorsList.setAdapter(mFloorsAdapter);		
		mFloorsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				
				mSharedData.setIsFloorPickerVisible(false);
				mSharedData.setSelectedCategory(null);
				setCurrentFloor(((FloorEx)mFloorsAdapter.getItem(position)).getFloor());
			}
		});
		
		// setup items
		mShopsAdapter = new ShopsAdapter(getActivity(), null);
		mCategoriesAdapter = new CategoriesAdapter(getActivity(), null);
		mPopupItems = (GridView)root.findViewById(R.id.map_popup_items);
		mPopupItems.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				
				if("".equals(mSharedData.getSelectedCategory())) {
					
					mSharedData.setSelectedCategory((String)mCategoriesAdapter.getItem(position));
				}
				else
				if(!TextUtils.isEmpty(mSharedData.getSelectedCategory())) {
					
					mSharedData.setSelectedCategory(null);
					
					OnMapFragmentCallbacks callback = getTarget(OnMapFragmentCallbacks.class);
					
					if(null != callback) {
						
						callback.onShopSelected(DBMapFragment.this, ((Shop)mShopsAdapter.getItem(position)));
					}
				}
			}
		});
		
		//
		mMap = (MapView)root.findViewById(R.id.map_view);
		mMap.setCallback(this);
		
		// restore instance state
		mSavedFloor = null;
		mSavedTooltipShopId = null;
		mSavedCategory = null;
		mSavedFloorsListVisibility = null;			
		
		if(null != savedInstanceState) {
			
			if(savedInstanceState.containsKey("mSavedFloor")) {
				
				mSavedFloor = savedInstanceState.getInt("mSavedFloor");
			}
			else {
				
				mSavedFloor = null;
			}
			
			if(savedInstanceState.containsKey("mSavedTooltipShopId")) {
				
				mSavedTooltipShopId = savedInstanceState.getInt("mSavedTooltipShopId");
			}
			else {
				
				mSavedTooltipShopId = null;
			}

			mSavedCategory = savedInstanceState.getString("mSavedCategory", null);
			mSavedFloorsListVisibility = savedInstanceState.getBoolean("mSavedFloorsListVisibility", false);
		}
		
		//
		if(null != mMapDescriptor) {

			mapDescriptorLoaded(mMapDescriptor, null);
		}
		else {
			
			Futures.addCallback(mExecutor.submit(new MapDescriptorLoader(MenuApplication.getContext(), Uri.parse("file://android_asset/map/map.json"))), 
					new MapDescriptorLoaderCallback(this), ExecutorUtils.getUiThreadExecutor());
		}

		root.addOnLayoutChangeListener(new OnLayoutChangeListener() {
			
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, 
					int oldLeft, int oldTop, int oldRight, int oldBottom) {

				int newWidth = getView().getWidth();
				int newHeight = getView().getHeight();
				
				if( (newWidth != mSize.x) ||
					(newHeight != mSize.y) ) {
					
					mSize.set(newWidth, newHeight);
					updatePopupPanelHeight();
				}
			}
		});
		
		return root;
	}
	
	/**
	 * 
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		
		Shop shop;
		
		if(mMap.getCurrentFloor() >= 0) {
			
			outState.putInt("mSavedFloor", mMap.getCurrentFloor());
		}
		
		if(null != (shop = mMap.getTooltipShop())) {
			
			outState.putInt("mSavedTooltipShopId", shop.getId());
		}
		
		outState.putString("mSavedCategory", mSharedData.getSelectedCategory());		
		outState.putBoolean("mSavedFloorsListVisibility", mSharedData.getIsFloorPickerVisible());
	}

	/**
	 * 
	 */
	@Override
	public void onResume() {

		super.onResume();
		
		if(null != mSharedData) {
			
			mSharedData.deleteObserver(this);
			mSharedData.addObserver(this);
			update(mSharedData, null);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void onPause() {

		super.onPause();
		
		if(null != mSharedData) {
			
			mSharedData.deleteObserver(this);
		}
	}
	
	/**************************************************************************************************************
	 * 
	 * 													API
	 * 
	 **************************************************************************************************************/
	public void setSharedData(SharedData sd) {
	
		if(null != mSharedData) {
			
			mSharedData.deleteObserver(this);
		}
		
		mSharedData = sd;
	}	
	
	/**
	 * 
	 * @return
	 */
	public FloorEx getCurrentFloorInfo() {
		
		int currentFloor;
		
		if( (null != mMapDescriptor) &&
			(null != mMap) &&
			(-1 != (currentFloor = mMap.getCurrentFloor())) ) {
			
			return FloorEx.create(mMapDescriptor.getFloorInfo(currentFloor), currentFloor);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param shop
	 */
	public void showTooltipForShop(Shop shop) {

		if(null != mMapDescriptor) {
			
			mRequestedShopTooltip = shop.getId();
			setCurrentFloor(shop.getFloor());
		}
	}
	
	/**************************************************************************************************************
	 * 
	 * 										MapView.MapViewCallbacks
	 * 
	 **************************************************************************************************************/

	@Override
	public void onMapDescriptorLoaded(MapView mapView, MapDescriptor md, Throwable error) {
		
		// set the height of popup panel
		updatePopupPanelHeight();
				
		// load floor
		mMap.setCurrentFloor((null != mSavedFloor) ? mSavedFloor : md.getMinFloor().getFloor());
				
		// restore selected category
		mSharedData.setSelectedCategory(mSavedCategory);		
		
		// restore floors list state
		mSharedData.setIsFloorPickerVisible((null != mSavedFloorsListVisibility) ? mSavedFloorsListVisibility : false);
		
		// restore saved tooltip
		if(null != mSavedTooltipShopId) {
			
			showTooltipForShop(md.findShopById(mSavedTooltipShopId));
		}
		
		// reset saved state
		mSavedFloor = null;
		mSavedTooltipShopId = null;
		mSavedCategory = null;
		mSavedFloorsListVisibility = null;
	}

	/**
	 * 
	 */
	@Override
	public void onFloorChanged(MapView mapView, int floor) {
		
		if(null != mSharedData) {
		
			mSharedData.setCurrentFloor(mMapDescriptor.getFloorInfoEx(floor));
		}
		
		if(mapView.isMapImageLoaded()) {
			
			onFloorMapLoaded(mapView, floor, null);			
		}
	}

	/**
	 * 
	 */
	@Override
	public void onFloorMapLoaded(MapView mapView, int floor, Throwable error) {
		
		if(null != mRequestedShopTooltip) {
			
			if(null == error) {
			
				Shop shop = mMapDescriptor.getShopByFloorAndId(floor, mRequestedShopTooltip);
				
				if(null != shop) {
					
					mapView.showTooltip(shop);
				}
			}
			
			mRequestedShopTooltip = null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
//	public Shop getTooltipShop() {
//		
//		if(null != mMapDescriptor) {
//			
//			return mMap.getTooltipShop();
//		}
//		
//		return null;
//	}
	
	/**************************************************************************************************************
	 * 
	 * 													Helpers
	 * 
	 **************************************************************************************************************/
	/**
	 * 
	 */
	private void hideFloorsPicker() {
		
		if(View.VISIBLE == mFloorsList.getVisibility()) {
		
			mFloorsList.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 
	 */
	private void showFloorsPicker() {
		
		if(View.VISIBLE != mFloorsList.getVisibility()) {
			
			mFloorsList.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 
	 */
	private void showCategoriesPopup() {

		if(null != mMapDescriptor)  {
			
			mPopupItems.setAdapter(mCategoriesAdapter);
			mMapPickerOverlay.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 
	 * @param category
	 */
	private void showShopsPopup(String category) {

		if( (null != mMapDescriptor) &&
			(!TextUtils.isEmpty(category)) )  {
			
			List<Shop> shops = Lists.newArrayList(mMapDescriptor.getShopsByCategory(category));
			Collections.sort(shops, new Shop.ShopsAscendingNameComparator());
			mShopsAdapter.setData(shops);
			mPopupItems.setAdapter(mShopsAdapter);
			mMapPickerOverlay.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 
	 */
	private void closePopup() {

		if(null != mMapDescriptor)  {
		
			if(View.VISIBLE == mMapPickerOverlay.getVisibility()) {
				
				mMapPickerOverlay.setVisibility(View.GONE);
			}
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void update(Observable observable, Object data) {

		SharedData sd = (SharedData)observable;
		
		if ((null == data) || (SharedData.PROP_FLOOR.equals(data))) {

		}

		if ((null == data) || (SharedData.PROP_FLOOR_PICKER.equals(data))) {

			if(true == sd.getIsFloorPickerVisible()) {
				
				showFloorsPicker();
			}
			else {
				
				hideFloorsPicker();
			}
		}

		if ((null == data) || (SharedData.PROP_SELECTED_CATEGORY.equals(data))) {

			if(null == sd.getSelectedCategory()) {
				
				closePopup();
			}
			else
			if(TextUtils.isEmpty(sd.getSelectedCategory())) {
				
				showCategoriesPopup();
			}
			else {
				
				showShopsPopup(sd.getSelectedCategory());
			}
		}
	}
	
	/**
	 * Return the "target" for this fragment of specified type. By default target is activity that owns
	 * current fragment but also could be any fragment. Please see {@link #Fragment.setTargetFragment(Fragment, int)}
	 * for more info.
	 * @param clazz requested callback interface
	 * @return requested callback or null if no callback of requested type is found
	 */
	public <T> T getTarget(Class<T> clazz) {
		
		Object[] targets = new Object[]{getTargetFragment(), getActivity()};
		
		for(Object target : targets) {
			
			if( (null != target) &&
				(clazz.isInstance(target)) ) {
				
				return clazz.cast(target);
			}
		}

		return null;
	}
	
	/**
	 * 
	 * @param md
	 * @param error
	 */
	private void mapDescriptorLoaded(MapDescriptor md, Throwable error) {
	
		mMapDescriptor = md;		
		
		List<FloorEx> floors = md.getFloors();
		Collections.sort(floors, new FloorDescendingComparator());
		mFloorsAdapter.setData(floors);
		
		List<String> categories = md.getCategories();
		Collections.sort(categories);
		mCategoriesAdapter.setData(categories);
		
		mMap.setMapDescriptor(md);
	}
	
	/**
	 * 
	 */
	private void setCurrentFloor(int floor) {

		mMap.setCurrentFloor(floor);
	}
	
	/**
	 * Update the height of popup panel
	 */
	private void updatePopupPanelHeight() {
		
		if( (null != mMapDescriptor) &&
			(mSize.x > 0) &&
			(mSize.y > 0)) {

			Context context = getActivity();
			int maxHeight = mSize.y;
			int desiredHeight = (mMapDescriptor.getFloorsCount() > 0) ? 
					(context.getResources().getDimensionPixelSize(R.dimen.cm_fragment_menu_grand_height) * mMapDescriptor.getFloorsCount() + (mMapDescriptor.getFloorsCount() + 1) * dipToPixels(context, 1))
					: 0;

			LayoutParams lp = mPopupItems.getLayoutParams();
			lp.height = Math.min(maxHeight, desiredHeight);
			mPopupItems.setLayoutParams(lp);
		}
	}
	
	/**
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dipToPixels(Context context, float dipValue) {
		
	    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
	    return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
	}
	
	/**
	 *
	 */
	private static class FloorsAdapter extends BaseAdapter {

		/**
		 * variables
		 */
		private LayoutInflater mInflater;
		private List<FloorEx> mData;
		
		/**
		 * 
		 * @param context
		 * @param data
		 */
		public FloorsAdapter(Context context, List<FloorEx> data) {
			
			mInflater = LayoutInflater.from(context);
			setData(data);
		}
		
		/**
		 * 
		 * @param data
		 */
		public void setData(List<FloorEx> data) {
			
			if(null != data) {
				
				mData = new ArrayList<FloorEx>(data);
			}
			else {
				
				mData = new ArrayList<FloorEx>();
			}
			
			notifyDataSetChanged();
		}
		
		/**
		 * 
		 */
		@Override
		public int getCount() {

			return mData.size();
		}

		/**
		 * 
		 */
		@Override
		public Object getItem(int location) {

			return mData.get(location);
		}

		/**
		 * 
		 */
		@Override
		public long getItemId(int location) {

			return location;
		}

		@Override
		public View getView(int location, View recycleView, ViewGroup parent) {
			
			if(null == recycleView) {
				
				recycleView = mInflater.inflate(R.layout.item_map_floor, null);
				recycleView.setTag(FloorViewHolder.create(recycleView));
			}

			FloorEx floor = (FloorEx)getItem(location);
			FloorViewHolder vh = (FloorViewHolder)recycleView.getTag();
			vh.floorTitle.setText(String.valueOf(floor.getFloor()));
			vh.floorExtra.setText((!TextUtils.isEmpty(floor.getDescription())) ? floor.getDescription() : "");
			
			return recycleView;
		}		
	}
	
	/**
	 *
	 */
	private static class FloorViewHolder {
		
		public TextView floorTitle = null;
		public TextView floorExtra = null;
		
		public static FloorViewHolder create(View v) {
			
			FloorViewHolder res = new FloorViewHolder();
			res.floorTitle = (TextView)v.findViewById(R.id.map_panel_floor_button_title);
			res.floorExtra = (TextView)v.findViewById(R.id.map_panel_floor_button_extra);
			return res;
		}
		
		private FloorViewHolder() {
			
		}
	}
	
	/**
	 *
	 */
	public class CategoriesAdapter extends BaseAdapter {

		/**
		 * variables
		 */
		private LayoutInflater mInflater;
		private List<String> mData;
		
		/**
		 * 
		 * @param context
		 * @param data
		 */
		public CategoriesAdapter(Context context, List<String> data) {
			
			mInflater = LayoutInflater.from(context);
			setData(data);
		}
		
		/**
		 * 
		 * @param data
		 */
		public void setData(List<String> data) {
			
			if(null != data) {
				
				mData = new ArrayList<String>(data);
			}
			else {
				
				mData = new ArrayList<String>();
			}
			
			notifyDataSetChanged();
		}

		/**
		 * 
		 */
		@Override
		public int getCount() {

			return mData.size();
		}

		/**
		 * 
		 */
		@Override
		public Object getItem(int position) {

			return mData.get(position);
		}

		/**
		 * 
		 */
		@Override
		public long getItemId(int position) {

			return position;
		}

		/**
		 * 
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(null == convertView) {
				
				convertView = mInflater.inflate(R.layout.item_map_picker_category, null);
			}

			((TextView)convertView.findViewById(android.R.id.text1)).setText((String)getItem(position));
			return convertView;
		}		
	}

	/**
	 *
	 */
	public class ShopsAdapter extends BaseAdapter {

		/**
		 * variables
		 */
		private LayoutInflater mInflater;
		private List<Shop> mData;
		
		/**
		 * 
		 * @param context
		 * @param data
		 */
		public ShopsAdapter(Context context, List<Shop> data) {
			
			mInflater = LayoutInflater.from(context);
			setData(data);
		}
		
		/**
		 * 
		 * @param data
		 */
		public void setData(List<Shop> data) {
			
			if(null != data) {
				
				mData = new ArrayList<Shop>(data);
			}
			else {
				
				mData = new ArrayList<Shop>();
			}
			
			notifyDataSetChanged();
		}

		/**
		 * 
		 */
		@Override
		public int getCount() {

			return mData.size();
		}

		/**
		 * 
		 */
		@Override
		public Object getItem(int position) {

			return mData.get(position);
		}

		/**
		 * 
		 */
		@Override
		public long getItemId(int position) {

			return position;
		}

		/**
		 * 
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(null == convertView) {
				
				convertView = mInflater.inflate(R.layout.item_map_picker_category, null);
			}

			((TextView)convertView.findViewById(android.R.id.text1)).setText(((Shop)getItem(position)).getName());
			return convertView;
		}		
	}
	
	/**
	 * 
	 * @param <T>
	 */
	private static abstract class SafeFutureCallback<T, E> implements FutureCallback<T> {

		private WeakReference<E> mRef = null;
		
		/**
		 * 
		 * @param ref
		 */
		public SafeFutureCallback(E ref) {
			
			mRef = new WeakReference<E>(ref);
		}
		
		/**
		 * 
		 * @return
		 */
		protected E getReference() {
			
			return (null != mRef) ? mRef.get() : null;
		}		
	}
	
	/**
	 *
	 */
	private static class MapDescriptorLoader implements Callable<MapDescriptor>{

		private final Context mContext;
		private final Uri mUri;
		
		public MapDescriptorLoader(Context context, Uri uri) {
		
			mContext = context.getApplicationContext();
			mUri = uri;
		}
		
		@Override
		public MapDescriptor call() throws Exception {

			InputStream is = null;
			MapDescriptor md = null;
			
			try {
				is = FileUtils.openUri(mContext, mUri);
				md = MapDescriptor.load(is);
			} catch (Exception ex) {
				LoggerService.sendMessage(mContext,
					new LogMessage(System.currentTimeMillis(),LogMessage.Stage.LOAD_MAP,LogMessage.Status.FAILED,"Can't load map from assets"));
			} finally {
				
				if(null != is) {
					
					FileUtils.close(is);
				}
				if (md!=null) {
					LoggerService.sendMessage(mContext,
						new LogMessage(System.currentTimeMillis(),LogMessage.Stage.LOAD_MAP,LogMessage.Status.SUCCESS,"Map load from assets"));
				}
			}
			
			return md;
		}		
	}
	
	/**
	 *
	 */
	private final static class MapDescriptorLoaderCallback extends SafeFutureCallback<MapDescriptor, DBMapFragment> {

		public MapDescriptorLoaderCallback(DBMapFragment ref) {
			super(ref);
		}

		@Override
		public void onFailure(Throwable error) {

			notifyLoaded(null, error);
		}

		@Override
		public void onSuccess(MapDescriptor md) {

			notifyLoaded(md, null);
		}
		
		private void notifyLoaded(MapDescriptor md, Throwable error) {
			
			DBMapFragment fragment = getReference();
			
			if(null != fragment) {
				
				fragment.mapDescriptorLoaded(md, error);
			}
		}		
	}
}
