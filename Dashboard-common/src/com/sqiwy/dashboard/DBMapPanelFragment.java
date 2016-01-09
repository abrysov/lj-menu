package com.sqiwy.dashboard;

import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.sqiwy.dashboard.DBMapActivity.SharedData;
import com.sqiwy.dashboard.model.map.FloorEx;
import com.sqiwy.menu.MenuApplication;

/**
 * Created by abrysov
 */

public class DBMapPanelFragment extends Fragment 
	implements Observer {
	
	/**
	 * variables
	 */	
	private SharedData mSharedData = null;
	private View mButtonFloor = null;
	private View mButtonFloorNormalLayout = null;
	private View mButtonFloorOpenedLayout = null;
	private TextView mButtonFloorTitle = null;
	private TextView mButtonFloorExtra = null;
	private TextView mButtonFloorTitle2 = null;
	private View mButton1 = null;
	private TextView mButton1Title = null;
	private View mButton1CloseBtn = null;
	private View mButton2 = null;
	private TextView mButton2Title = null;
	private View mButton2CloseBtn = null;
	
	/**
	 * 
	 */
	public DBMapPanelFragment() {
				
	}
	
	/**
	 * 
	 */
	@SuppressLint("CutPasteId")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Preconditions.checkArgument(null == mSharedData, "SharedData is not set!");
		
		View root = inflater.inflate(R.layout.fragment_map_panel, null);
		
		mButtonFloor = root.findViewById(R.id.map_panel_floor_btn);
		mButtonFloorTitle = (TextView)root.findViewById(R.id.map_panel_floor_button_title);
		mButtonFloorExtra = (TextView)root.findViewById(R.id.map_panel_floor_button_extra);
		mButtonFloorTitle2 = (TextView)root.findViewById(R.id.map_panel_floor_button_title2);
		mButtonFloorNormalLayout = root.findViewById(R.id.map_panel_floor_button);
		mButtonFloorOpenedLayout = root.findViewById(R.id.map_panel_floor_button_title2);
		mButton1 = root.findViewById(R.id.map_panel_button1);
		mButton1Title = (TextView)root.findViewById(R.id.map_panel_button1_title);
		mButton1CloseBtn = root.findViewById(R.id.map_panel_button1_close_btn);
		mButton2 = root.findViewById(R.id.map_panel_button2);
		mButton2Title = (TextView)root.findViewById(R.id.map_panel_button2_title);
		mButton2CloseBtn = root.findViewById(R.id.map_panel_button2_close_btn);

		mButtonFloor.setOnClickListener(mClickListener);
		mButton1.setOnClickListener(mClickListener);
		mButton1CloseBtn.setOnClickListener(mClickListener);
		mButton2.setOnClickListener(mClickListener);
		mButton2CloseBtn.setOnClickListener(mClickListener);
		
		mButton1Title.setText(getActivity().getString(R.string.map_goods_categories));
		setFloorButtonInfo("", "", "");
		
		return root;
	}
	
	/**
	 * 
	 */
	@Override
	public void onDestroyView() {

		super.onDestroyView();
		
		mSharedData.deleteObserver(this);
	}
	
	/**
	 * 
	 */
	@Override
	public void onResume() {

		super.onResume();
		
		// start observe data changes
		mSharedData.deleteObserver(this);
		mSharedData.addObserver(this);
		update(mSharedData, null);
	}

	/**
	 * 
	 */
	@Override
	public void onPause() {
		
		super.onPause();
		
		// stop observe shared data
		mSharedData.deleteObserver(this);
	}
	
	/**************************************************************************************************************
	 * 
	 * 													API
	 * 
	 **************************************************************************************************************/
	/**
	 * 
	 * @param sd
	 */
	public void setSharedData(SharedData sd) {
		
		mSharedData = sd;
	}
	
	/**************************************************************************************************************
	 * 
	 * 													HELPERS
	 * 
	 **************************************************************************************************************/
	@Override
	public void update(Observable observable, Object data) {

		SharedData sd = (SharedData)observable;
		
		if( (null == data) ||
			(SharedData.PROP_FLOOR.equals(data)) ) {
			
			FloorEx floor = sd.getCurrentFloor();
			
			setFloorButtonInfo((null == floor) ? "" : String.valueOf(floor.getFloor()), 
					(null == floor) ? "" : MenuApplication.getContext().getString(R.string.map_floor), 
					MenuApplication.getContext().getString(R.string.map_floors));
		}
					
		updateUi();
	}

	/**
	 * 
	 * @param title
	 * @param extra
	 * @param titleOpened
	 */
	private void setFloorButtonInfo(String title, String extra, String titleOpened) {
		
		mButtonFloorTitle.setText(title);
		mButtonFloorExtra.setText(extra);
		mButtonFloorTitle2.setText(titleOpened);
	}
	
	/**
	 * 
	 */
	private void updateUi() {
		
		if( (null != mSharedData) &&
			(null != mSharedData.getCurrentFloor()) ) {
			
			boolean isFloorPickerVisible = mSharedData.getIsFloorPickerVisible();
			String category = mSharedData.getSelectedCategory();
			
			mButtonFloor.setVisibility(View.VISIBLE);
			
			if(isFloorPickerVisible) {
			
				mButtonFloorNormalLayout.setVisibility(View.INVISIBLE);
				mButtonFloorOpenedLayout.setVisibility(View.VISIBLE);
			}
			else {
				
				mButtonFloorNormalLayout.setVisibility(View.VISIBLE);
				mButtonFloorOpenedLayout.setVisibility(View.INVISIBLE);				
			}
						
			if(null == category) {
				
				mButton1.setBackgroundResource(R.drawable.map_panel_button_background_normal);
				mButton1.setVisibility(View.VISIBLE);
				mButton1CloseBtn.setVisibility(View.GONE);
				mButton2.setVisibility(View.INVISIBLE);
				mButton2CloseBtn.setVisibility(View.GONE);
			}
			else
			if(TextUtils.isEmpty(category)) {
				
				mButton1.setBackgroundResource(R.drawable.map_panel_button_background_selected);
				mButton1.setVisibility(View.VISIBLE);
				mButton1CloseBtn.setVisibility(View.VISIBLE);
				mButton2.setVisibility(View.INVISIBLE);
				mButton2CloseBtn.setVisibility(View.GONE);
			}
			else {
				
				mButton1.setBackgroundResource(R.drawable.map_panel_button_background_normal);
				mButton2.setBackgroundResource(R.drawable.map_panel_button_background_selected);
				mButton1.setVisibility(View.VISIBLE);
				mButton1CloseBtn.setVisibility(View.GONE);
				mButton2.setVisibility(View.VISIBLE);
				mButton2CloseBtn.setVisibility(View.VISIBLE);
				mButton2Title.setText(category);
			}
		}
		else {
			
			mButtonFloor.setVisibility(View.INVISIBLE);
			mButton1.setVisibility(View.INVISIBLE);
			mButton2.setVisibility(View.INVISIBLE);
		}		
	}
	
	/**
	 * 
	 */
	private View.OnClickListener mClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			switch(v.getId()) {
			
				case R.id.map_panel_floor_btn: {
					
					mSharedData.setIsFloorPickerVisible(!mSharedData.getIsFloorPickerVisible());
				}
				break;
				
				case R.id.map_panel_button1: {
					
					mSharedData.setSelectedCategory("");
				}
				break;
				
				case R.id.map_panel_button1_close_btn:
				case R.id.map_panel_button2_close_btn: {
					
					mSharedData.setSelectedCategory(null);
				}
				break;
			}
		}
	};
}
