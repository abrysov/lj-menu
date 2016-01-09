package com.sqiwy.medialoader.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

import android.util.Log;

import com.sqiwy.medialoader.MediaItem;

/**
 * Created by abrysov
 */

public class ZippedMediaItemLoaderHelper extends MediaLoaderHelper {

	private static final String TAG = ZippedMediaItemLoaderHelper.class.getName();
	
	private static final int BUFFER_SIZE = 1024;

	/**
	 * <ul>
	 * <li>Downloads ZIP file from the network</li>
	 * <li>Unpacks downloaded file</li>
	 * </ul>
	 */
	@Override
	protected void performLoad(MediaItem mediaItem) throws Exception {
		
		File temp = null;
		
		try {
			
			File rootForMediaItem = new File(mediaItem.getStorageUri());
			
			temp = new File(rootForMediaItem, "temp" + System.nanoTime());
			
			Config config = DEFAULT_CONFIG;
			
			// Download zip file updating progress
			FileUtil.copyURLToFile(new URL(mediaItem.getServerUri()), temp,
					config.getConnectionTimeout(), config.getHttpReadTimeout(),
					getCancellationPolicy());
			
			// Unpack zip file
			unpack(temp, mediaItem);
		
		} catch (Exception e) {
			Log.e(TAG, "Error while downloading media item: " + mediaItem, e);
			throw e;
		} finally {
			
			if (null != temp) {
				FileUtils.deleteQuietly(temp);
			}
		}
	}

	protected void unpack(File file, MediaItem mediaItem) throws IOException {
		
		File path = null;
		ZipInputStream zis = null;
		
		try {
			
			// Unpack ZIP archive
			zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
			
			path = new File(mediaItem.getStorageUri());
		
			FileUtil.checkDir(path);
			
			ZipEntry ze;
			while ((ze = zis.getNextEntry()) != null && !getCancellationPolicy().isCancelled()) {

				File zeFile = new File(path, ze.getName());
				
				if (ze.isDirectory()) {
					FileUtil.checkDir(zeFile);
				} else {
					readFile(zeFile, zis);
				}
			}
		
		} catch (IOException e) {
			
			Log.e(TAG, "Error while downloading media item: " + mediaItem, e);
			
			// Something wrong happened during downloading of media item flow:
			// delete all downloaded files (cleanup) for the particular media item
			FileUtils.deleteDirectory(path);
			
			throw e;
			
		} finally {
			try {
				zis.close();
			} catch (IOException e) {
				Log.e(TAG, "Cannot close zip input stream.", e);
			}
			
			if (getCancellationPolicy().isCancelled()) {
				FileUtils.deleteDirectory(path);
			}
		}
		
	}
	
	protected void readFile(File file, InputStream is) throws IOException {
		FileOutputStream os = null;

		try {

			os = new FileOutputStream(file);

			byte[] buffer = new byte[BUFFER_SIZE];

			int bytesRead = -1;
			while (-1 != (bytesRead = is.read(buffer))) {
				os.write(buffer, 0, bytesRead);
			}
			
			os.flush();

		} catch (IOException e) {
			Log.e(TAG, "Exception during reading and writing data (file).", e);
			throw e;
		} finally {
			
			if (null != os) {
				try {
					os.close();
				} catch (IOException ex) {
					Log.e(TAG, "Cannot close output stream.", ex);
				}
			}
		}
	}

}
