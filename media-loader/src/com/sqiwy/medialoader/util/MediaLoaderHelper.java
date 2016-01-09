package com.sqiwy.medialoader.util;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sqiwy.medialoader.MediaItem;

/**
 * Created by abrysov
 */

public abstract class MediaLoaderHelper {

	/**
	 * Config for loader. 
	 */
	public static class Config {
		/** Connection timeout in millis. */
		private int connectionTimeout;
		/** Http read timeout in millis. */
		private int httpReadTimeout;
		
		public int getConnectionTimeout() {
			return connectionTimeout;
		}
		public Config setConnectionTimeout(int connectionTimeout) {
			this.connectionTimeout = connectionTimeout;
			return this;
		}
		public int getHttpReadTimeout() {
			return httpReadTimeout;
		}
		public Config setHttpReadTimeout(int httpReadTimeout) {
			this.httpReadTimeout = httpReadTimeout;
			return this;
		}
	}
	
	public static final Config DEFAULT_CONFIG =
			new Config().setConnectionTimeout(10000).setHttpReadTimeout(30000);
	
	protected CancellationPolicy mCancellationPolicy = new CancellationPolicy();
	
	public void load(MediaItem mediaItem) throws Exception {
		
		// Delete existing content stored under specified path
		File file = new File(mediaItem.getStorageUri());
		
		if (file.exists()) {
			if (file.isDirectory()) {
				FileUtils.deleteDirectory(file);
			} else {
				FileUtils.deleteQuietly(file);
			}
		}
		
		// Load new content
		performLoad(mediaItem);
	}

	public CancellationPolicy getCancellationPolicy() {
		return mCancellationPolicy;
	}

	protected abstract void performLoad(MediaItem mediaItem) throws Exception;
	
}

