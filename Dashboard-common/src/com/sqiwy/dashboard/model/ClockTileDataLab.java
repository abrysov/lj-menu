package com.sqiwy.dashboard.model;

import com.sqiwy.dashboard.model.action.Action;

/**
 * Created by abrysov
 */

@SuppressWarnings("serial")
public class ClockTileDataLab extends TileData {
	
	private String[] mTownIds;
	private String[] mTownNames;
	//Differences between current town and necessary in hours
	private int[] mTownDifferences;
	
	public String[] getTownIds() {
		return mTownIds;
	}

	public void setTownIds(String[] townIds) {
		mTownIds = townIds;
	}
	
	public String[] getTownNames() {
		return mTownNames;
	}

	public void setTownNames(String[] townNames) {
		mTownNames = townNames;
	}

	public ClockTileDataLab(Action action) {
		super(action);
	}

	public int[] getTownDifferences() {
		return mTownDifferences;
	}

	public void setTownDifferences(int[] townDifferences) {
		mTownDifferences = townDifferences;
	}
	
	
}
