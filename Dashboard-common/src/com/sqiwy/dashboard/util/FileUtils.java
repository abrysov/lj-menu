package com.sqiwy.dashboard.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.net.Uri;

/**
 * Created by abrysov
 */

public class FileUtils {

	/**
	 * Close input stream silently
	 * 
	 * @param is
	 * 			input stream
	 */
	public final static void close(InputStream is) {
		
		if(null != is) {
			
			try {
				
				is.close();
			} 
			catch (IOException e) {

			}
		}
	}
	
	/**
	 * 
	 * @param context
	 * @param uri
	 * @return
	 * @throws FileNotFoundException
	 */
	public static InputStream openUri(final Context context, final Uri uri) throws FileNotFoundException, IOException {
		
		InputStream res = null;
		
		if( (null != context) &&
			(null != uri) &&
			(null != uri.getScheme()) ) {
			
			if (0 == uri.getScheme().compareTo("file")) {
				
				String path = uri.toString();
				
				if(path.startsWith("file://android_asset/")) {
					
					res = context.getAssets().open(path.substring("file://android_asset/".length()));
				}
				else {
				
					res = new FileInputStream(new File(uri.getPath()));
				}
			} 
		}
		
		return res;
	}
}
