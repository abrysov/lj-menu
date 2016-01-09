package com.sqiwy.medialoader.util;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import android.util.Log;

import com.sqiwy.medialoader.MediaItem;

/**
 * Created by abrysov
 */

public class FileMediaItemLoaderHelper extends MediaLoaderHelper {

	private static final String TAG = FileMediaItemLoaderHelper.class.getName();

	/**
	 * <ul>
	 * <li>Downloads file from the network</li>
	 * </ul>
	 */
	@Override
	public void performLoad(MediaItem mediaItem) throws Exception {
		
		File rootForMediaItem = null;
		try {
			
			rootForMediaItem = new File(mediaItem.getStorageUri());
			
			Config config = DEFAULT_CONFIG;
			
			// Download zip file updating progress
			FileUtil.copyURLToFile(new URL(mediaItem.getServerUri()), rootForMediaItem,
					config.getConnectionTimeout(), config.getHttpReadTimeout(),
					getCancellationPolicy());
			
		} catch (Exception e) {
			Log.e(TAG, "Error while downloading media item: " + mediaItem, e);
			throw e;
		} finally {
			if (getCancellationPolicy().isCancelled()) {
				FileUtils.deleteQuietly(rootForMediaItem);
			}
		}
	}

}

