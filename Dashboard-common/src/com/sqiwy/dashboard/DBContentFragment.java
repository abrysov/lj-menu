package com.sqiwy.dashboard;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.fosslabs.android.utils.AnimFragment;
import com.fosslabs.android.utils.JSLog;
import com.fosslabs.android.view.MosaicLayout;
import com.sqiwy.dashboard.model.ClockTileDataLab;
import com.sqiwy.dashboard.model.CommercialData;
import com.sqiwy.dashboard.model.CommercialTileDataLab;
import com.sqiwy.dashboard.server.StorageQuery;
import com.sqiwy.dashboard.util.StatsUtils.StatsContext;
import com.sqiwy.dashboard.view.TileViewMosaicAdapter;
import com.sqiwy.menu.advertisement.Advertisement;

/**
 * Created by abrysov
 */

public class DBContentFragment extends ActionFragment implements AnimFragment {
	
	private static final String TAG = "DBContentFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		View v = inflater.inflate(R.layout.fragment_content_db, container, false);
		
		Resources resources = getResources();
		
		int menuTopBarHeight = resources.getDimensionPixelSize(R.dimen.menu_top_bar_height);
		int mosaicTopMargin = resources.getDimensionPixelSize(R.dimen.mosaic_layout_top_margin);
		int mosaicBottomMargin = resources.getDimensionPixelSize(R.dimen.mosaic_layout_bottom_margin);
		
		LinearLayout parent = (LinearLayout) v.findViewById(R.id.ll_parent);
		
		parent.setPadding(menuTopBarHeight, menuTopBarHeight  + mosaicTopMargin,
				menuTopBarHeight, mosaicBottomMargin);
		
		MosaicLayout mosaicLayout = (MosaicLayout) v.findViewById(R.id.pl);

		// Value set here was taken from wire frames (we can make this value to be configurable)
		//mosaicLayout.setCellWidthToHeightRatio(199f / 210f);
		
		TileViewMosaicAdapter ma = StorageQuery.getDBTileViewMosaicAdapter(getActivity());
		
		if(ma == null) return v;
		
		ma.setAnimFragment(this);
		
		//int height = Funs.getDisplayMetrics(getActivity()).heightPixels;
		
		/*height -=( pl.getPaddingTop() + pl.getPaddingBottom() + 
				((ViewGroup)pl.getParent()).getPaddingTop() + ((ViewGroup)pl.getParent()).getPaddingBottom());*/
		
		/*JSLog.d(TAG, "ma.getRowsCount() " + ma.getRowsCount() + " " + height);*/
		
		if (ma.getRowsCount() == 0) return v;

		/*height -= pl.getSpacingVertical() * (ma.getRowsCount() - 1);*/
		
		//float rowHeight = height / ma.getRowsCount();
		///rowHeight = 100;
		//pl.setRowHeight(rowHeight);	
		mosaicLayout.setAdapter(ma);
		
	    /*pl.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				pl.post(new Runnable() {
					public void run() {
						startAnimChild();
					}
				});
				
				pl.getViewTreeObserver().removeOnPreDrawListener(this);
				return true;
			}
		});*/
			
		for(int i = 0; i < mosaicLayout.getChildCount(); i++){
			final View child = mosaicLayout.getChildAt(i);
			child.setOnClickListener(getDefaultActionViewClickListener());
		}

		loadClockGallery(v);
		loadCommercialImageGallery(v, R.id.fragment_commercial_gallery, false, true);
		loadCommercialImageGallery(v, R.id.fragment_magasin_gallery, true, false);
		//loadMagasinGallery();
		return v;
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		startAnimChild();
	}
	
	private void loadClockGallery(View parent){
		
		try {	
			final View child = parent.findViewById(R.id.fragment_gallery_clock);
			final ClockTileDataLab td = (ClockTileDataLab) child.getTag();

			child.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				
				@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {
					Log.d("TAGTAG", "onGlobalLayout loadClockGallery");
					
					//Funs.getToast(getActivity(), "onGlobalLayout() " + child.getHeight());
					FragmentManager fm = getFragmentManager();
					Fragment fragment = fm.findFragmentById(R.id.fragment_gallery_clock);
					if (fragment == null) {
						//fragment = DBGalleryClockFragment.newInstance(child.getWidth(), child.getHeight(), td.getTownIds(), td.getTownNames(), td.getTownDifferences());
						fragment = DBClockFragment.newInstance(child.getWidth(), child.getHeight(), td.getTownIds(), td.getTownNames(), td.getTownDifferences());
						fm.beginTransaction().add(R.id.fragment_gallery_clock, fragment)
								.commit();
					}					
					if(android.os.Build.VERSION.SDK_INT >= 16){//android.os.Build.VERSION_CODES.JELLY_BEAN){
						child.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					}
					else
						child.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			});
			
		} catch (Exception e) {
			JSLog.e(TAG, ""+e.getMessage());
		}
	}
	
	private void loadCommercialImageGallery(final View parent, final int id, final boolean gallery_touch, final boolean gallery_auto_update){
		
		try {	
			final View child = parent.findViewById(id);
			final CommercialTileDataLab td = (CommercialTileDataLab) child.getTag();
			final ArrayList<CommercialData> commercialDataList = td.getCommercialDataList();
			
			child.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				
				@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {

					//Funs.getToast(getActivity(), "onGlobalLayout() " + child.getHeight());
					FragmentManager fm = getFragmentManager();
					Fragment fragment = fm.findFragmentById(id);
					if (fragment == null) {
						
						// TODO: temporary solution to fix magazines
						if (1 < td.getCountVisible()) {
							fragment = DBGalleryCommercialTempFragment.newInstance
									(child.getWidth(), child.getHeight(), commercialDataList, td.getCountVisible(), td.getProporcional(),
											gallery_touch, gallery_auto_update);
							fm.beginTransaction().add(id, fragment)
									.commit();
						} else {
							fragment = DBGalleryCommercialFragment.newInstacne(null, StatsContext.DASHBOARD, true, Advertisement.Places.DASHBOARD);
							fm.beginTransaction().add(id, fragment).commit();
						}
						
					}					
					if(android.os.Build.VERSION.SDK_INT >= 16){//android.os.Build.VERSION_CODES.JELLY_BEAN){
						child.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					}
					else
						child.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			});
			
		} catch (Exception e) {
			JSLog.e(TAG, ""+e.getMessage());
		}
	}
	/*
	private void loadMagasinGallery(){
		try {	
			final View child = mParent.findViewById(R.id.fragment_magasin_gallery);
			child.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				
				@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {
					Funs.getToast(getActivity(), "onGlobalLayout() " + child.getHeight());
					FragmentManager fm = getFragmentManager();
					Fragment fragment = fm.findFragmentById(R.id.fragment_magasin_gallery);
					if (fragment == null) {
						fragment = MGMagasinGalleryFragment.newInstance(child.getWidth(), child.getHeight());
						fm.beginTransaction().add(R.id.fragment_magasin_gallery, fragment)
								.commit();
					}					
					if(android.os.Build.VERSION.SDK_INT >= 16){//android.os.Build.VERSION_CODES.JELLY_BEAN){
						child.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					}
					else
						child.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			});
			
		} catch (Exception e) {
			JSLog.e(TAG, e.getMessage());
		}
	}
	
*/
	
	@Override
	public void startAnimChild() {
//		if (!mSkipNextAnimation) {
//
//			final View view = ll_parent;
//			
//			ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view,
//					PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, view.getHeight(), 0));
//			
//			animator.setDuration(1000).setInterpolator(new OvershootInterpolator(2.0f));
//			animator.start();
//			
//		} else {
//			mSkipNextAnimation = false;
//		}
	}
	
}
