package com.sqiwy.dashboard.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.fosslabs.android.utils.JSLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

/**
 * Created by abrysov
 */

public class RestartReceiver extends BroadcastReceiver{
	private static String TAG = "RestartReceiver";
	private static String FILE_DIR = "/city_time.txt";

	@Override
	public void onReceive(Context context, Intent intent) {
		String[] ids = new String[3];
		ids[0] = "Europe/Kiev";
		ids[1] = "Europe/Moscow";
		ids[2] = "Europe/Berlin";
		
		int length = ids.length;
		
		String[] townNames = ClockManager.getTownNames(context, ids);
		int[] townDifferences = ClockManager.getTownDifferences(context, ids);
		
		StringBuilder builder = new StringBuilder();
		if(townNames.length != 0 && townDifferences.length != 0){
			for(int i = 0; i < length; i++){
				builder.append(townNames[i] + " " + townDifferences[i] + "\n");
			}
		}
		
		JSLog.d(TAG, "I'm restarted!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//		if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
//		}
		writeFile(builder.toString());
	}
	
	private void writeFile(String info){
		if (!Environment.getExternalStorageState().equals(
		        Environment.MEDIA_MOUNTED)) {
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
