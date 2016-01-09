package com.sqiwy.dashboard.model;

import java.util.ArrayList;

import com.sqiwy.dashboard.model.action.Action;

/**
 * Created by abrysov
 */

@SuppressWarnings("serial")
public class CommercialTileDataLab extends TileData {
	
	private static final String TAG = "CommercialTileDataLab";
	
	private ArrayList<CommercialData> mCommercialDataList = new ArrayList<CommercialData>();
	private int mCountVisible = 1;
	private float mProporcional = 1;
	
	public CommercialTileDataLab(Action action) {
		super(action);
	}

	public ArrayList<CommercialData> getCommercialDataList() {
		return mCommercialDataList;
	}

	public void setCommercialDataList(ArrayList<CommercialData> commercialDataList) {
		mCommercialDataList = commercialDataList;
	}

	public int getCountVisible() {
		return mCountVisible;
	}

	public void setCountVisible(int countVisible) {
		mCountVisible = countVisible;
	}
	
	public float getProporcional() {
		return mProporcional;
	}

	public void setProporcional(float proporcional) {
		mProporcional = proporcional;
	}

	public ArrayList<String> getCommercialDataStringList(){
		ArrayList<String> list = new ArrayList<String>();
		for(CommercialData cd : mCommercialDataList){
			list.add(cd.toString());
		}
		return list;
	}
}
