package com.sqiwy.dashboard.view;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.haarman.supertooltips.ToolTip;
import com.haarman.supertooltips.ToolTipRelativeLayout;
import com.haarman.supertooltips.ToolTipView;
import com.haarman.supertooltips.ToolTipView.Style;
import com.sqiwy.dashboard.R;
import com.sqiwy.dashboard.model.map.MapDescriptor;
import com.sqiwy.dashboard.model.map.Shop;
import com.sqiwy.dashboard.util.ExecutorUtils;
import com.sqiwy.dashboard.util.FileUtils;
import com.sqiwy.dashboard.util.ImageUtils;
import com.sqiwy.dashboard.util.RewindableInputStream;
import com.sqiwy.dashboard.view.ScalableImageView.OnContentClickListener;

/**
 * Created by abrysov
 */

public class MapView extends FrameLayout {

	/**
	 * consts
	 */
	private static float MAX_TOOLTIP_WIDTH_PERCENT = 0.5f;
	
	/**
	 * 
	 *
	 */
	public interface MapViewCallbacks {
	
		void onMapDescriptorLoaded(MapView mapView, MapDescriptor mapDescriptorm, Throwable error);
		void onFloorChanged(MapView mapView, int floot);
		void onFloorMapLoaded(MapView mapView, int floor, Throwable error);
	}
	
	/**
	 * variables
	 */
	private ScalableImageView mMapImage = null;
	private ToolTipRelativeLayout mTootipOverlay = null;
	private MapDescriptor mMapDescriptor = null;
	private ListeningExecutorService mExecutor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
	private MapViewCallbacks mCallbacks = null;
	private int mCurrentFloor = -1;
	private List<Shop> mCurrentFloorShops = null;
	private ToolTipView mTooltip = null;
	private Shop mTooltipShop = null;
	private Point mPoint = new Point(0, 0);
	
	/**
	 * 
	 * @param context
	 */
	public MapView(Context context) {
		
		this(context, null);
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public MapView(Context context, AttributeSet attrs) {
		
		super(context, attrs);		
		
		initialize(context);
	}
	
	/**************************************************************************************************************
	 * 
	 * 													API
	 * 
	 **************************************************************************************************************/
	/**
	 * 
	 * @param md
	 */
	public void setMapDescriptor(MapDescriptor md) {
		
		resetCurrentFloor(true);
		onMapDescriptorLoaded(md, (null != md) ? null : new IllegalArgumentException("setMapDescriptor(null)"));
	}
	
	/**
	 * 
	 * @param uri
	 */
	public void loadMapDescriptor(final Uri uri) {
		
		resetCurrentFloor(true);
		
		Futures.addCallback(mExecutor.submit(new MapDescriptorLoader(getContext(), uri)), 
				new MapDescriptorLoaderCallback(this), ExecutorUtils.getUiThreadExecutor());
	}
	
	/**
	 * 
	 * @return
	 */
	public MapDescriptor getMapDescriptor() {
		
		synchronized (this) {
			
			return mMapDescriptor;
		}
	}
	
	/**
	 * 
	 * @param callback
	 */
	public void setCallback(MapViewCallbacks callback) {
		
		synchronized (this) {
			
			mCallbacks = callback;
		}
	}
	
	/**
	 * 
	 * @param
	 */
	public synchronized boolean setCurrentFloor(int floor) {
		
		String mapImageFile = null;		
		
		if(null != (mapImageFile = mMapDescriptor.getFloorMap(floor))) {
			
			if(mCurrentFloor != floor) {
				
				resetCurrentFloor(false);
				
				mCurrentFloor = floor;
				mMapImage.setImageBitmap(null);
				mCurrentFloorShops = mMapDescriptor.getShopsByFloor(floor);
				notifyFloorChanged();
				
				loadMapImage(Uri.parse(mapImageFile), mCurrentFloor);
			}
			else {
				
				notifyFloorChanged();
			}
		
			return true;
		}
		
		return false;		
	}
	
	/**
	 * 
	 * @return
	 */
	public synchronized int getCurrentFloor() {

		return mCurrentFloor;
	}
	
	/**
	 * 
	 * @param shop
	 */
	public void showTooltip(Shop shop) {
				
		if(mCurrentFloorShops.contains(shop)) {
		
			if( (null != mTooltipShop) &&
				(mTooltipShop.getId() == shop.getId()) ) {
			
				return;
			}
			
			hideTooltip();
	
			Point pt;
			
			if( (null != shop) &&
				(null != (pt = mMapImage.contentCoordinatesToViewCoordinates(mPoint, shop.getX(), shop.getY()))) ){
				
				mTooltipShop = shop;

				
		        mTooltip = mTootipOverlay.showToolTipForPosition(
		                new ToolTip()
		                	.withContentView(createTooltipContent(getContext(), shop))
		                    .withColorTopPointer(R.color.tips_black)
                            .withColorBottomPointer(R.color.tips_red)
                            .withColorBackground(R.color.tips_black)
		                    .withStyle(Style.RECTANGULAR)
		                    .withMargins(0, 0, 0, 0)
		                    .removeByClick(false),
		                    pt.x, pt.y);

		        
		        mTooltip.findViewById(R.id.map_tooltip_btn_close).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        hideTooltip();
                    }
                });
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Shop getTooltipShop() {
		
		return mTooltipShop;
	}
	
	/**
	 * 
	 */
	public boolean isMapImageLoaded() {
		
		return mMapImage.isImageLoaded();
	}
	
	/**************************************************************************************************************
	 * 
	 * 													HELPERS
	 * 
	 **************************************************************************************************************/
	
	/**
	 * 
	 */
	private void initialize(Context context) {
		
		removeAllViews();
	
		mMapImage = new ScalableImageView(context);
		mMapImage.setIsMultitouchScaleEnabled(false);
		mMapImage.setContentClickListener(mMapContentClickListener);
		mTootipOverlay = new ToolTipRelativeLayout(context);
		
		addView(mMapImage, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(mTootipOverlay, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		resetCurrentFloor(true);
	}
		
	/**
	 * 
	 * @param md
	 */
	private void onMapDescriptorLoaded(final MapDescriptor md, Throwable error) {
		
		synchronized(this) {
			
			mMapDescriptor = md;
			resetCurrentFloor(true);
			
			if(null != mCallbacks) {
				
				mCallbacks.onMapDescriptorLoaded(MapView.this, mMapDescriptor, error);
			}
		}
	}
	
	/**
	 * 
	 * @param uri
	 */
	private void loadMapImage(final Uri uri, int floor) {
				
		Futures.addCallback(mExecutor.submit(new MapImageLoader(getContext(), uri)), 
				new MapImageLoaderCallback(this, floor), ExecutorUtils.getUiThreadExecutor());
	}
	
	/**
	 * 
	 * @param bmp
	 * @param error
	 */
	private synchronized void onMapImageLoaded(final Bitmap bmp, int floor, Throwable error) {
		
		if(mCurrentFloor == floor) {

			mMapImage.setImageBitmap(bmp);
			
			if(null != bmp) {
			
				mMapImage.setMaxZoom(mMapImage.getMinZoom());						
			}
			
			if(null != mCallbacks) {
				
				mCallbacks.onFloorMapLoaded(this, floor, error);
			}
		}
	}
	
	/**
	 * 
	 */
	private void notifyFloorChanged() {
		
		synchronized(this) {
			
			if(null != mCallbacks) {
				
				mCallbacks.onFloorChanged(this, mCurrentFloor);
			}
		}
	}
	
	/**
	 * 
	 */
	private void resetCurrentFloor(boolean notifyChanged) {
		
		mCurrentFloor = -1;
		mMapImage.setImageBitmap(null);
		mCurrentFloorShops = Lists.newArrayList();
		hideTooltip();
		
		if(notifyChanged) {
		
			notifyFloorChanged();
		}
	}
	
	/**
	 * 
	 */
	private void hideTooltip() {
		
		if(null != mTooltip) {
			
			mTooltip.remove();
			mTooltip = null;
			mTooltipShop = null;
		}
	}
	
	/**
	 * 
	 * @param context
	 * @param shop
	 * @return
	 */
	private View createTooltipContent(Context context, Shop shop) {
		
		View tooltip = LayoutInflater.from(context).inflate(R.layout.item_map_shop_tooltip, null);
		
		((MaxWidthLinearLayout)tooltip).setMaxWidth((int)(getWidth() * MAX_TOOLTIP_WIDTH_PERCENT));
		
		TextView shopTitle = (TextView)tooltip.findViewById(R.id.map_tooltip_shop_title);
		TextView shopDescr = (TextView)tooltip.findViewById(R.id.map_tooltip_shop_descr);
		TextView shopFloor = (TextView)tooltip.findViewById(R.id.map_tooltip_shop_floor);
		
		shopTitle.setText(shop.getName());
		shopDescr.setText(shop.getDescription());
		shopFloor.setText(String.format("%d " + getContext().getString(R.string.map_floor), shop.getFloor()));
		
		return tooltip;
	}
	
	/**
	 * 
	 */
	private OnContentClickListener mMapContentClickListener = new OnContentClickListener() {
		
		@Override
		public void onContentClicked(ScalableImageView iv, int contentX, int contentY) {

			for(Shop shop : mCurrentFloorShops) {
				
				if(distance(contentX, contentY, shop.getX(), shop.getY()) < shop.getSnapDistance()) {
					
					showTooltip(shop);
					break;
				}
			}
		}
	}; 
	
	/**
	 * 
	 * @return
	 */
	private static int distance(int x1, int y1, int x2, int y2) {
		
		int dx = x2 - x1;
		int dy = y2 - y1;
		
		return (int)Math.sqrt(dx * dx + dy * dy);
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
			}
			finally {
				
				if(null != is) {
					
					FileUtils.close(is);
				}
			}
			
			return md;
		}		
	}
	
	/**
	 * 
	 *
	 */
	private final static class MapDescriptorLoaderCallback extends SafeFutureCallback<MapDescriptor, MapView> {

		public MapDescriptorLoaderCallback(MapView ref) {
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
			
			MapView mv = getReference();
			
			if(null != mv) {
				
				mv.onMapDescriptorLoaded(md, error);
			}
		}		
	}
	
	/**
	 *
	 */
	private static class MapImageLoader implements Callable<Bitmap>{

		private final Context mContext;
		private final Uri mUri;
		
		public MapImageLoader(Context context, Uri uri) {
		
			mContext = context.getApplicationContext();
			mUri = uri;
		}
		
		@Override
		public Bitmap call() throws Exception {

			Bitmap bmp = null;
			RewindableInputStream is = null;
			
			try {
				
				is = new RewindableInputStream(FileUtils.openUri(mContext, mUri));
				bmp = ImageUtils.loadImageFromStream(is, (2048 * 2048));
			}
			finally {
				
				if(null != is) {
					
					FileUtils.close(is);
				}
			}
			
			return bmp;
		}		
	}
	
	/**
	 *
	 */
	private final static class MapImageLoaderCallback extends SafeFutureCallback<Bitmap, MapView> {

		private final int mFloor;
		
		public MapImageLoaderCallback(MapView ref, int floor) {
			
			super(ref);
			mFloor = floor;
		}

		@Override
		public void onFailure(Throwable error) {

			notifyLoaded(null, error);
		}

		@Override
		public void onSuccess(Bitmap bmp) {

			notifyLoaded(bmp, null);
		}
		
		private void notifyLoaded(Bitmap bmp, Throwable error) {
			
			MapView mv = getReference();
			
			if(null != mv) {
				
				mv.onMapImageLoaded(bmp, mFloor, error);
			}
		}		
	}
}
