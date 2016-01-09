package com.fosslabs.android.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.fosslabs.android.view.NinePatchBitmapFactory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Environment;

public class JSFileWorker {
	private static final String TAG = "JSFileWorker";

	public static boolean hasSDCard() { // SD????????
		String status = Environment.getExternalStorageState();
		return status.equals(Environment.MEDIA_MOUNTED);
	}

	public static String getSDCardPath() {
		File path = Environment.getExternalStorageDirectory();
		return path.getAbsolutePath();
	}

	public static String getBasePath2() {
		if (hasSDCard())
			return getSDCardPath();
		else
			return Environment.getDataDirectory().getAbsolutePath();
	}

	public static File loadFromFile(String path) {
		//String BasePath = getBasePath();
		String resPath = path ;//+ BasePath;
		JSLog.d(TAG, resPath);
		File f = new File(resPath);
		if (!f.exists())
			return null;
		return f;
	}
	
	public static String loadDataFromFile(Context context, String path) {
		try {
			File f = loadFromFile(path);
			if(f== null) return null;
			
			InputStream is = new FileInputStream(f.getPath());
			int size = is.available();

			byte[] buffer = new byte[size];

			is.read(buffer);

			is.close();

			return  new String(buffer, "UTF-8");

		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}

	}

	private static final String[] MEDIA_EXTENSION = { ".aac", ".mp3", ".wav",
			".ogg", ".midi", ".3gp", ".mp4", ".m4a", ".amr", ".flac", ".wmv" };

	private static final String[] GRAFIC_EXTENSION = { ".png", ".gif", ".bmp" };

	public static ArrayList<String> getMediaFiles(String dir) {
		return getFiles(dir, MEDIA_EXTENSION);
	}

	public static ArrayList<String> getGraficFiles(String dir) {
		return getFiles(dir, GRAFIC_EXTENSION);
	}

	public static boolean checkDirsOnStart(String[] array) {//String basePath, 
		for (String dir : array) {
			String resPath = dir;// basePath + 
			JSLog.d(TAG, resPath);
			File directory = new File(resPath);
			if (!directory.exists()) {
				directory.mkdirs();
			}
		}
		return true;
	}

	@SuppressLint("DefaultLocale")
	public static ArrayList<String> getFiles(String dir, String[] extension) {

		ArrayList<String> listPath = new ArrayList<String>();
		String SDCardPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String resPath = //SDCardPath +
				dir;
		JSLog.d(TAG, resPath);
		File directory = new File(resPath);

		if (!directory.exists()) {
			directory.mkdirs();
			return listPath;
		}
		File list[] = directory.listFiles();

		for (int i = 0; i < list.length; i++) {
			String name = list[i].getName().toLowerCase();
			if (extension != null && extension.length > 0) {
				for (String anExt : extension) {
					if (name.endsWith(anExt)) {
						listPath.add(resPath + list[i].getName());
						break;
					}
				}
			} else {
				listPath.add(list[i].getName());
			}
			JSLog.d(TAG, list[i].getName());
		}

		return listPath;

	}

	public static String loadDataFromAsset(Context context, String file_name) {
		try {

			InputStream is = context.getAssets().open(file_name);

			int size = is.available();

			byte[] buffer = new byte[size];

			is.read(buffer);

			is.close();

			return  new String(buffer, "UTF-8");

		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public static Drawable loadDrawableFromAsset(Context context,
			String file_name) {
		try {
			// get input stream
			InputStream ims = context.getAssets().open(file_name);
			// load image as Drawable
			Drawable d = Drawable.createFromStream(ims, null);
			return d;

		} catch (IOException ex) {
			JSLog.e(TAG, "loadDrawableFromAsset " + ex.getMessage());
			return null;
		}
		
	}
			
	
	public static Drawable loadNinePatchDrawableFromAsset(Context context,
			String file_name) {
		try {
			
			file_name = "ic_lang_lite.9.png";
			// get input stream
			InputStream ims = context.getAssets().open(file_name);
			// load image as Drawable
			Options options = new BitmapFactory.Options(); 
			options.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeStream( ims);//, null ,options);
			return NinePatchBitmapFactory.createNinePathWithCapInsets(context.getResources(), bitmap, 50, 50, 80, 80, "name");
			//return new BitmapDrawable(context.getResources(), bitmap);
			/*
			byte[] chunk = bitmap.getNinePatchChunk();
			boolean result = NinePatch.isNinePatchChunk(chunk);
			if(!result) return null;
			NinePatchDrawable patchy = new NinePatchDrawable(context.getResources(), bitmap, chunk, new Rect(), null);
			return patchy;*/

		} catch (IOException ex) {
			return null;
		}
		
	}
}
