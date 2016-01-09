package com.sqiwy.dashboard.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.sqiwy.menu.MenuApplication;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.fosslabs.android.utils.JSLog;
import com.sqiwy.dashboard.model.ClockTileDataLab;
import com.sqiwy.dashboard.model.CommercialData;
import com.sqiwy.dashboard.model.CommercialTileDataLab;
import com.sqiwy.dashboard.model.TileData;
import com.sqiwy.dashboard.model.TileData.TileDataType;
import com.sqiwy.dashboard.model.action.Action;
import com.sqiwy.dashboard.view.TileViewMosaicAdapter;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.resource.ResourcesManager.Category;

/**
 * Created by abrysov
 */

public class StorageQuery {
	private static final String TAG = "StorageQuery";

	public static Drawable getLockWindowBG(Context context) {
		return ResourcesManager.getDrawableResource(context,
				"bg_window_lock.png", Category.DASHBOARD);
	}

	public static Drawable getTileDataRes(Context context, String path) {
		JSLog.d(TAG, "getTileDataRes path=" + path);
		/*
		 * if(path.contains(".9.png")){ JSLog.d(TAG, "NINE " + path); return
		 * JSFileWorker.loadNinePatchDrawableFromAsset(context, path); }
		 * JSLog.d(TAG, path);
		 */
		return ResourcesManager.getDrawableResource(context, path,
               Category.DASHBOARD);
	}

	public static TileViewMosaicAdapter getDBTileViewMosaicAdapter(
			Context context) {
		// String json = JSFileWorker.loadDataFromAsset(context,
		// "db_tile.json");
		try {
			JSONObject object = ResourcesManager.getJsonResource(context,
					"db_tile.json", Category.DASHBOARD);
			// JSONObject object = ResourcesManager.getJsonResource(context,
			// "db_tile_new.json", Category.DASHBOARD);
			JSLog.d(TAG, "json came = " + object.toString());
			JSONObject tileview = object.getJSONObject("tileview");
			int column = tileview.getInt("column");
			JSLog.d(TAG, "column " + column);
			TileViewMosaicAdapter ma = new TileViewMosaicAdapter(context,
					column);

			JSONArray array = object.getJSONArray("tiledata");
			for (int i = 0; i < array.length(); i++) {
				JSONObject tiledata = array.getJSONObject(i);
				switch (tiledata.getInt("type")) {
				case TileDataType.TYPE_GALLERY_CLOCK: {
					ClockTileDataLab td = new ClockTileDataLab(
							Action.Resolver.resolve(tiledata));
					// or crete new TileData (String json) ?
					td.setUUID(tiledata.getString("uuid"));
					td.setName(tiledata.getString("name"));
					td.setBackgroundPath(tiledata.optString("backgroundPath"));
					td.setTextSize(tiledata.getInt("textSize"));
					td.setType(tiledata.getInt("type"));
					// JSONArray townsIds = tiledata.getJSONArray("townIds");
					// String [] ids = new String [townsIds.length()];
					// for(int j = 0; j < townsIds.length(); j++)
					// ids[j] = townsIds.getString(j);

					// TODO: temporary override values from javascript
					String[] ids = new String[3];
					ids[0] = "Europe/Kiev";
					ids[1] = "Europe/Moscow";
					ids[2] = "Europe/Berlin";
					td.setTownIds(ids);

					int length = ids.length;

					ArrayList<String> townNamesList = new ArrayList<String>();
					ArrayList<Integer> townDifferencesList = new ArrayList<Integer>();

					// if(!getCityAndDifferences(townNames, townDifferences)){
					// townNames = ClockManager.getTownNames(context, ids);
					// townDifferences =
					// ClockManager.getTownDifferences(context, ids);
					// StringBuilder builder = new StringBuilder();
					// if(townNames.length != 0 && townDifferences.length != 0){
					// for(int j = 0; j < length; j++){
					// builder.append(townNames[j] + " " + townDifferences[j] +
					// "\n");
					// }
					// }
					// writeCityAndDifferences(builder.toString());
					// }
					getCityAndDifferences(townNamesList, townDifferencesList);

					String[] townNames = new String[townNamesList.size()];
					int[] townDifferences = new int[townDifferencesList.size()];
					for (int j = 0; j < townDifferencesList.size(); j++) {
						townNames[j] = townNamesList.get(j);
						townDifferences[j] = townDifferencesList.get(j)
								.intValue();
					}
					Log.d(TAG, townNames.toString());

					td.setTownNames(townNames);
					td.setTownDifferences(townDifferences);

					ma.addObject(td, tiledata.getInt("width"),
							tiledata.getInt("height"));
					break;
				}
				case TileDataType.TYPE_GALLERY_IMAGE: {
					CommercialTileDataLab td = new CommercialTileDataLab(
							Action.Resolver.resolve(tiledata));
					// or crete new TileData (String json) ?
					td.setUUID(tiledata.getString("uuid"));
					td.setName(tiledata.getString("name"));
					td.setBackgroundPath(tiledata.optString("backgroundPath"));
					td.setTextSize(tiledata.getInt("textSize"));
					td.setType(tiledata.getInt("type"));
					td.setIvMetrica(tiledata.getInt("ivMetrica"));
//					JSONArray commercial = tiledata.getJSONArray("commercial");
//					ArrayList<CommercialData> list = new ArrayList<CommercialData>();
//					for (int j = 0; j < commercial.length(); j++) {
//						JSONObject ob = commercial.getJSONObject(j);
//						CommercialData cd = new CommercialData(
//								Action.Resolver.resolve(ob));
//						cd.setUUID(ob.getString("uuid"));
//						cd.setName(ob.getString("name"));
//						cd.setBackgroundPath(ob.getString("path"));
//						cd.setTimeShow(ob.getLong("timeShow"));
//						list.add(cd);
//					}
//					td.setCommercialDataList(list);
					td.setCountVisible(tiledata.getInt("countVisible"));
					td.setProporcional(Float.valueOf(tiledata
							.getString("proporcion")));
					ma.addObject(td, tiledata.getInt("width"),
							tiledata.getInt("height"));
					break;
				}
				case TileDataType.TYPE_GALLERY_MAGASIN: {
					CommercialTileDataLab td = new CommercialTileDataLab(
							Action.Resolver.resolve(tiledata));
					// or crete new TileData (String json) ?
					td.setUUID(tiledata.getString("uuid"));
					td.setName(tiledata.getString("name"));
					td.setBackgroundPath(tiledata.optString("backgroundPath"));
					td.setTextSize(tiledata.getInt("textSize"));
					td.setType(tiledata.getInt("type"));
					td.setIvMetrica(tiledata.getInt("ivMetrica"));
					JSONArray commercial = tiledata.getJSONArray("commercial");
					ArrayList<CommercialData> list = new ArrayList<CommercialData>();
					for (int j = 0; j < commercial.length(); j++) {
						JSONObject ob = commercial.getJSONObject(j);
						CommercialData cd = new CommercialData(
								Action.Resolver.resolve(ob));
						cd.setUUID(ob.getString("uuid"));
						cd.setName(ob.getString("name"));
						cd.setBackgroundPath(ob.getString("path"));
						cd.setTimeShow(ob.getLong("timeShow"));
						list.add(cd);
					}
					td.setCommercialDataList(list);
					td.setCountVisible(tiledata.getInt("countVisible"));
					td.setProporcional(Float.valueOf(tiledata
							.getString("proporcion")));
					ma.addObject(td, tiledata.getInt("width"),
							tiledata.getInt("height"));
					break;
				}
				default: {
					TileData td = new TileData(
							Action.Resolver.resolve(tiledata));
					// or crete new TileData (String json) ?
					td.setUUID(tiledata.getString("uuid"));
					td.setName(tiledata.getString("name"));
					td.setColorLogoPath(tiledata.getString("colorLogoPath"));
					td.setMonoLogoPath(tiledata.getString("monoLogoPath"));
					td.setBackgroundPath(tiledata.optString("backgroundPath"));
					td.setTextSize(tiledata.getInt("textSize"));
					td.setLandLayoutOrientation(Boolean.valueOf(tiledata
							.getString("landLayoutOrientation")));
					td.setIvMetrica(tiledata.getInt("ivMetrica"));
					td.setType(tiledata.getInt("type"));
					ma.addObject(td, tiledata.getInt("width"),
							tiledata.getInt("height"));
				}
				}

			}
			return ma;
		} catch (JSONException e) {
			JSLog.e(TAG, e, "JSON error");
		} catch (Exception e) {
			JSLog.e(TAG, e, "Error while generating tile view mosaic.");
		}
		return null;
	}

	private static boolean getCityAndDifferences(
			ArrayList<String> townNamesList,
			ArrayList<Integer> townDifferencesList) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return false;
		}
		File sdPath = Environment.getExternalStorageDirectory();
		sdPath = new File(sdPath.getAbsolutePath()
				+ com.sqiwy.dashboard.model.ProjectConstants.GALLERY_CLOCK_PATH);
		File sdFile = new File(sdPath, "/clock_gallery.txt");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(sdFile));
			String str = "";
			int i = 0;
			str = br.readLine();
			while (str != null) {
				int space = str.indexOf(" ");
				// townNames[i] = str.substring(0, space);
				// townDifferences[i] = Integer.valueOf(str.substring(++space,
				// str.length()));
				townNamesList.add(str.substring(0, space));
				townDifferencesList.add(Integer.valueOf(str.substring(++space,
						str.length())));
				str = br.readLine();
				i++;
			}

			return true;

		} catch (Exception e) {

			Log.e(TAG, "Error while getting city and differences.", e);

		} finally {

			IOUtils.closeQuietly(br);
		}

		return false;
	}

	private static void writeCityAndDifferences(String info) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return;
		}
		File sdPath = Environment.getExternalStorageDirectory();
		sdPath = new File(sdPath.getAbsolutePath() + "/" + "DASH_BOARD");
		sdPath.mkdirs();
		File sdFile = new File(sdPath, "/city_time.txt");
		try {
			FileWriter fileWriter = new FileWriter(sdFile, true);
			fileWriter.write(info);
			fileWriter.close();
			// JSLog.d(TAG, "���� ������� �� SD: " + sdFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
