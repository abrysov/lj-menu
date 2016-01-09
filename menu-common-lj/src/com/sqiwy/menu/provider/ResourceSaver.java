package com.sqiwy.menu.provider;

import java.io.IOException;
import java.util.List;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.sqiwy.backend.api.BackendException;
import com.sqiwy.backend.data.Resource;
import com.sqiwy.menu.MenuApplication;

/**
 * Created by abrysov
 */

public class ResourceSaver {

	
	private MenuApplication app;
	
	public ResourceSaver(MenuApplication app){
		this.app = app;
	}
	
	public interface OnResourceSaveListener{
		
		void onResourceSaved(boolean isSuccess);
		
	}
	
	
	private OnResourceSaveListener listener;
	
	public void saveResources(OnResourceSaveListener listener){
		this.listener = listener;
		SaveTask task = new SaveTask();
		task.execute();
	}
	
	
	private class SaveTask extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			
			List<Resource> res = null;
			try {
				res = app.getOperationService().getResources();
			} catch (BackendException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return res == null ? false : saveResources(res);
			
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			ResourceSaver.this.listener.onResourceSaved(result);
		}
		
	}
	
	private boolean saveResources(final List<Resource> res){
		
		boolean result = true;
		
		app.getContentResolver().delete(MenuProvider.URI_RESOURCE, null, null);
		
		ContentValues[] cvs = new ContentValues[res.size()];
		
		for(int i=0; i<cvs.length; i++){
			ContentValues cv = new ContentValues();
			cv.put(DBHelper.F_PACKAGE_URL, res.get(i).getPackageUrl());
			cv.put(DBHelper.F_CODE, res.get(i).getCode());
			cvs[i] = cv;
		}
		
		int insertedRows = app.getContentResolver().bulkInsert(MenuProvider.URI_RESOURCE, cvs);
		
		Log.e("Resources", String.valueOf(insertedRows));
		
		if(cvs.length > insertedRows) {
			Log.e("Resources", "some data was not saved");
			result = false;
		}
		
		return result;
		
	}
	
	
}
