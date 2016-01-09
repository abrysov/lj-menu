package com.sqiwy.dashboard.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by abrysov
 */

public class LoggerReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if(ConnectivityManager.CONNECTIVITY_ACTION == intent.getAction()) {
		
			ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
	        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
	        
	        if( (netInfo != null) && 
	        	(netInfo.isConnected())) {
	        
	        	LoggerService.sendQueuedMessages(context);
	        }
		}
	}
}
