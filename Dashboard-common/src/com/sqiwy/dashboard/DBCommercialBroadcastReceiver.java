package com.sqiwy.dashboard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.fosslabs.android.utils.JSLog;
import com.sqiwy.dashboard.model.ProjectConstants;

/**
 * Created by abrysov
 */

public class DBCommercialBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "DBBroadcastReceiver";
	private static final String FILE_DIR = "commertial_statistic.txt";
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		StringBuilder builder = new StringBuilder();
		builder.append(intent.getStringExtra(ProjectConstants.EXTRA_COMMERCIAL_NAME) + " ");
		builder.append(intent.getBooleanExtra(ProjectConstants.EXTRA_COMMECIAL_IS_CLICKED, false) + " ");
		builder.append(intent.getStringExtra(ProjectConstants.EXTRA_COMMERCIAL_TIME_SHOW) + "\n");
		
		Log.d(TAG, builder.toString());
		writeFile(builder.toString());				
	}

	private void writeFile(String info){
		if (!Environment.getExternalStorageState().equals(
		        Environment.MEDIA_MOUNTED)) {
		      JSLog.d(TAG, "SD-����� �� ��������: " + Environment.getExternalStorageState());
		      return;
		    }
		    File sdPath = Environment.getExternalStorageDirectory();
		    sdPath = new File(sdPath.getAbsolutePath() + "/" + "DASH_BOARD");
		    sdPath.mkdirs();
		    File sdFile = new File(sdPath, FILE_DIR);
		    try {
		      FileWriter fileWriter = new FileWriter(sdFile, true);
		      fileWriter.write(info);
		      fileWriter.close();
		      //JSLog.d(TAG, "���� ������� �� SD: " + sdFile.getAbsolutePath());
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
	}
	
}
