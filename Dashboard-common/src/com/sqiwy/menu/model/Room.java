package com.sqiwy.menu.model;

import java.util.ArrayList;

/**
 * Created by abrysov
 */

public class Room {
	private static final String TAG = "Room";

	protected int mId;
	protected String mName;
	protected String mMapUrl;
	protected ArrayList<Table> mTableList = new ArrayList<Table>();

	public Room() {
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getMapUrl() {
		return mMapUrl;
	}

	public void setMapUrl(String mapUrl) {
		mMapUrl = mapUrl;
	}

	public ArrayList<Table> getTableList() {
		return mTableList;
	}

	public void setTableList(ArrayList<Table> tableList) {
		mTableList = tableList;
	}
	
	
}
