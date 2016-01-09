package com.sqiwy.dashboard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.sqiwy.menu.MenuApplication;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fosslabs.android.utils.JSLog;
import com.fosslabs.android.utils.ProjectConstants;
import com.sqiwy.dashboard.model.TileData.TileDataType;
import com.sqiwy.dashboard.server.ClockManager;
import com.sqiwy.dashboard.util.FlowUtils;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.resource.ResourcesManager.Category;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.ProgressBar;

/**
 * Created by abrysov
 */

public class DBInitializeGalleryClock extends Activity {

	private String[] ids;
	private String[] townNames;
	private static final String TAG = "DBInitializeGalleryClock";
	private Context mContext;
	
	private class ViewHolder{
		ProgressBar pb;
	}
	
	ViewHolder vh = new ViewHolder();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initialize_gallery_clock);
		
		mContext = DBInitializeGalleryClock.this;
		vh.pb = (ProgressBar) findViewById(R.id.pb);
		
		Initialization init = new Initialization();
		init.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dbinitialize_gallery_clock, menu);
		return true;
	}
	
	private static void writeCityAndDifferences(String info){
		if (!Environment.getExternalStorageState().equals(
		        Environment.MEDIA_MOUNTED)) {
		      return;
		    }
		    File sdPath = Environment.getExternalStorageDirectory();
		    sdPath = new File(sdPath.getAbsolutePath() + com.sqiwy.dashboard.model.ProjectConstants.GALLERY_CLOCK_PATH);
		    if(!sdPath.exists())
		    	sdPath.mkdirs();
		    File sdFile = new File(sdPath, "/clock_gallery.txt");
		    try {
		      FileWriter fileWriter = new FileWriter(sdFile, true);
		      fileWriter.write(info);
		      fileWriter.close();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
	}
	
	private class Initialization extends AsyncTask<Void, Integer, String>{
		
		private StringBuilder builder = new StringBuilder();
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
//			Uncomment to take towns from json
//			try {
//				JSONObject object = ResourcesManager.getJsonResource(mContext, "db_tile.json", Category.DASHBOARD);
//				
//				JSONArray array = object.getJSONArray("tiledata");
//				for(int i = 0; i < array.length(); i++){
//					JSONObject tiledata = array.getJSONObject(i);
//					if(tiledata.getInt("type") == TileDataType.TYPE_GALLERY_CLOCK){
//						JSONArray townsIds = tiledata.getJSONArray("townIds");
//						ids = new String [townsIds.length()];
//						for(int j = 0; j < townsIds.length(); j++)
//							ids[j] = townsIds.getString(j);
//						break;
//					}
//				}
			
			JSONObject object;
			try {
				object = ResourcesManager.getJsonResource(mContext, "db_tile.json", Category.DASHBOARD);
			JSONArray array = object.getJSONArray("tiledata");
			for(int i = 0; i < array.length(); i++){
				JSONObject tiledata = array.getJSONObject(i);
				if(tiledata.getInt("type") == TileDataType.TYPE_GALLERY_CLOCK){
					JSONArray towns = tiledata.getJSONArray("towns");
					ids = new String [towns.length()];
					townNames = new String[towns.length()];
					for(int j = 0; j < towns.length(); j++){
						ids[j] = towns.getJSONObject(j).getString("id");
						townNames[j] = towns.getJSONObject(j).getString("name");
					}
					break;
				}
			}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//				ids = new String[3];
//				ids[0] = "Europe/Kiev";
//				ids[1] = "Europe/Moscow";
//				ids[2] = "Europe/Berlin";
				
				int[] townDifferences = new int[ids.length];
				
				//townNames = ClockManager.getTownNames(mContext, ids);
				townDifferences = ClockManager.getTownDifferences(DBInitializeGalleryClock.this, ids);
				if(townNames.length != 0 && townDifferences.length != 0){
					for(int j = 0; j < ids.length; j++){
						builder.append(townNames[j] + " " + townDifferences[j] + "\n");
					}
				}
//				
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
			return builder.toString();
		}
		
		@Override
		protected void onPostExecute(String result) {
			writeCityAndDifferences(result);

            MenuApplication.mIsCheckResources = false;
            FlowUtils.continueSetupFlow(DBInitializeGalleryClock.this);
		    
		    finish();
		}
		
	}

}
