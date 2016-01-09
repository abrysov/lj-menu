package com.fosslabs.android.utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.graphics.PorterDuff.Mode;

public class JSImageWorker {
	private static final String TAG = "JSImageWorker";
	
	public static View setColorFilterBackGround(View v, int color, Mode mode){
		Drawable d = v.getBackground();
		if(color < 0 || mode == null) d.setColorFilter(Color.argb(150, 155, 155, 155), Mode.MULTIPLY);
		else d.setColorFilter(color, mode);
		return setBackground(v, d);
	}
	
	public static View setNullColorFilterBackGround(View v){
		Drawable d = v.getBackground();
		d.setColorFilter(null);
		return setBackground(v, d);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static View setBackground(View v, Drawable drawable){
		if(android.os.Build.VERSION.SDK_INT >= 16){//android.os.Build.VERSION_CODES.JELLY_BEAN){
			v.setBackground(drawable);//new BitmapDrawable(getResources(), bm));//mChildBackground));
		}
		else{
			v.setBackgroundDrawable(drawable);//new BitmapDrawable(bm));//mChildBackground));
		}
		return v;
	}
	
	// view.setBackgroundColor(getRandomColor(256));
	public static int getRandomColor(int alpha) {
		Random color = new Random();
		return Color.rgb(color.nextInt(256), color.nextInt(256),
				color.nextInt(256));
	}

	public static Bitmap loadFromFile(String path) {
		RandomAccessFile raf=null;
		FileDescriptor fd;
		BitmapFactory.Options bOpts=new BitmapFactory.Options();
		
		try {
			File f = JSFileWorker.loadFromFile(path);
			if (f == null) {
				return null;
			}
			
			raf=new RandomAccessFile(f,"r"); fd=raf.getFD();
			bOpts.inPurgeable=bOpts.inInputShareable=true; bOpts.inMutable=false;
			
			Bitmap tmp = BitmapFactory.decodeFileDescriptor(fd,null,bOpts);
			return tmp;
		} catch (Exception e) {
			return null;
		} catch (OutOfMemoryError e) {
			JSLog.e(TAG, "loadFromFile() " + path + e.getMessage());
			return null;
		} finally {
			if (raf!=null) try { raf.close(); } catch (Exception e) {}
		}
	}

	public static Bitmap cropFromBottomAndSave(Bitmap bm, int reqWidth,
			int reqHeight, String name) {
		int w = (bm.getWidth() < reqWidth) ? bm.getWidth() : reqWidth;
		int h, top;
		if (bm.getHeight() < reqHeight) {
			h = bm.getHeight();
			top = 0;
		} else {
			h = reqHeight;
			top = bm.getHeight() - reqHeight;
		}
		try {
			Bitmap newBm = Bitmap.createBitmap(bm, 0, top, w, h);
			//newBm.recycle();
			saveToFile(newBm, name);
			return newBm;

		} catch (Exception e) {
			return null;
		} catch (OutOfMemoryError e) {
			JSLog.e(TAG, "cropFromBottomAndSave " + e.getMessage());
			return null;
		}

	}

	public static void saveToFile(Bitmap bmp, String filename) {
		String path = filename; //JSFileWorker.getBasePath() + 
		try {
			FileOutputStream out = new FileOutputStream(path);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.close();
			out = null;
		} catch (Exception e) {
			e.printStackTrace();
			JSLog.d(TAG, "ERROR! CAN NOT SAVE " + path);
		}
	}

	public static Bitmap loadFromSD(String path, int reqWidth, int reqHeight) {
		String SDCardPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String resPath = SDCardPath + path;
		JSLog.e(TAG, resPath);
		File file = new File(resPath);
		RandomAccessFile raf=null;
		FileDescriptor fd;

		if (!file.exists()) {
			return null;
		}

		final BitmapFactory.Options options = new BitmapFactory.Options();
		
		try {
			// First decode with inJustDecodeBounds=true to check dimensions
			options.inJustDecodeBounds = true;
			raf=new RandomAccessFile(file,"r"); fd=raf.getFD();
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);
			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds=options.inMutable=false; options.inPurgeable=options.inInputShareable=true;
			return BitmapFactory.decodeFileDescriptor(fd, null, options);
		} catch (IOException e) {
			return null;
		} finally {
			if (raf!=null) try { raf.close(); } catch (Exception ex) {}
		}
		
	}
	
	public static Bitmap loadFromResource(Context context, int id) {
		JSLog.d(TAG, "loadFromResource");
		// ImageView image = (ImageView) findViewById(R.id.test_image);
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), id);// R.drawable.icon);
		// image.setImageBitmap(bMap);
		return bm;
	}

	public static Bitmap loadFromResource(Context context, int resId,
			int reqWidth, int reqHeight) {
		JSLog.e(TAG, "loadFromResource " + reqWidth + " " + reqHeight);
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(context.getResources(), resId,
				options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap getBitmapFromView1(View view) {
		// Define a bitmap with the same size as the view
		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
				view.getHeight(), Bitmap.Config.ARGB_8888);
		// Bind a canvas to it
		Canvas canvas = new Canvas(returnedBitmap);
		// Get the view's background

		Drawable bgDrawable = view.getBackground();
		if (bgDrawable != null)
			// has background drawable, then draw it on the canvas
			bgDrawable.draw(canvas);
		else
			// does not have background drawable, then draw white background on
			// the canvas
			canvas.drawColor(Color.TRANSPARENT);
		// draw the view on the canvas
		view.draw(canvas);
		// return the bitmap
		return returnedBitmap;
	}

	public static Bitmap getBitmapFromView2(View view) {
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		return view.getDrawingCache();
	}

	// addShadowLine(getActivity(), R.drawable.menu_category_bg, 4, Color.BLACK)
	/*
	 * public static Bitmap addShadowLine(Context context, int idFonRes, int
	 * reqWidth, int reqHeight, int heightDp, int color){ Bitmap fon =
	 * JSImageWorker.loadFromResource(context, idFonRes);
	 * 
	 * Bitmap shadow = Bitmap.createBitmap(fon.getWidth(), fon.getHeight(),
	 * Config.ARGB_8888); Canvas c_shadow = new Canvas(shadow); Paint paint =
	 * new Paint(); int shadow_height = Funs.dpToPx(context, heightDp);
	 * paint.setStrokeWidth(0); paint.setColor(color); Rect r = new Rect(0,
	 * fon.getHeight()- shadow_height, fon.getWidth(), fon.getHeight());
	 * c_shadow.drawRect(r, paint );
	 * 
	 * Bitmap bm = Bitmap.createBitmap(fon.getWidth(), fon.getHeight(),
	 * Config.ARGB_8888); Canvas c = new Canvas(bm); c.drawBitmap(fon, 0f, 0f,
	 * null); c.drawBitmap(shadow, 0f, 0f, null);
	 * 
	 * return bm; }
	 */
	public static Bitmap addShadowLine(Context context, int idFonRes,
			int heightDp, int color) {
		JSLog.d(TAG, "addShadowLine ");
		Bitmap fon = JSImageWorker.loadFromResource(context, idFonRes);

		Bitmap shadow = Bitmap.createBitmap(fon.getWidth(), fon.getHeight(),
				Config.ARGB_8888);
		Canvas c_shadow = new Canvas(shadow);
		Paint paint = new Paint();
		int shadow_height = Funs.dpToPx(context, heightDp);
		paint.setStrokeWidth(0);
		paint.setColor(color);
		Rect r = new Rect(0, fon.getHeight() - shadow_height, fon.getWidth(),
				fon.getHeight());
		c_shadow.drawRect(r, paint);

		Bitmap bm = Bitmap.createBitmap(fon.getWidth(), fon.getHeight(),
				Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		c.drawBitmap(fon, 0f, 0f, null);
		c.drawBitmap(shadow, 0f, 0f, null);

		return bm;/**/
	}

	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);

		// "RECREATE" THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;
	}

	private static final int BRIGHTNESS_KEY = android.R.drawable.ic_delete;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void changeBrightness(Context context, View v) {
		String s = null;
		if (v instanceof TextView) {
			s = ((TextView) v).getText().toString();
			((TextView) v).setText(null);
		}
		Bitmap bm_old = getBitmapFromView1(v);
		Bitmap bm_new;
		if (v.getTag(BRIGHTNESS_KEY) == null) {

			bm_new = getBitmapFromView1(v);
			bm_new = SetBrightness(bm_new, 50);

		} else {

			bm_new = (Bitmap) v.getTag(BRIGHTNESS_KEY);

		}
		v.setTag(BRIGHTNESS_KEY, bm_old);
		if (android.os.Build.VERSION.SDK_INT >= 16) {// android.os.Build.VERSION_CODES.JELLY_BEAN){
			v.setBackground(new BitmapDrawable(context.getResources(), bm_new));// mChildBackground));
		} else {
			v.setBackgroundDrawable(new BitmapDrawable(bm_new));// mChildBackground));
		}

		if (v instanceof TextView) {

			((TextView) v).setText(s);
		}
	}

	// ������ bitmap - � ������ rect � �������� color
	public static Bitmap drawRect(Bitmap bm, int color) {
		if (bm == null)
			bm = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);

		if (color < 0)
			color = Color.argb(70, 128, 128, 128);
		Canvas canvas = new Canvas(bm);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		canvas.drawRect(0, 0, bm.getWidth(), bm.getHeight(), paint);
		return bm;
	}

	// value -80 dark; 90 light
	public static Bitmap SetBrightness(Bitmap src, int value) {

		// original image size
		int width = src.getWidth();
		int height = src.getHeight();
		int A, R, G, B;
		int pixel;

		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information

		boolean isTransp = true;
		// �������� �� ������ ������������, ��������� ���-�� ����� ��������
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				if (A > 0) {
					isTransp = false;
					break;
				}
			}
		}
		if (isTransp) {
			return drawRect(bmOut, -1);

		}

		// scan through all pixels
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);

				// increase/decrease each channel
				R += value;
				if (R > 255) {
					R = 255;
				} else if (R < 0) {
					R = 0;
				}

				G += value;
				if (G > 255) {
					G = 255;
				} else if (G < 0) {
					G = 0;
				}

				B += value;
				if (B > 255) {
					B = 255;
				} else if (B < 0) {
					B = 0;
				}

				// apply new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}

	public static Bitmap applyReflection(Bitmap originalImage) {
		// gap space between original and reflected
		final int reflectionGap = 4;
		// get image size
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		// this will not scale but will flip on the Y axis
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		// create a Bitmap with the flip matrix applied to it.
		// we only want the bottom half of the image
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				height / 2, width, height / 2, matrix, false);

		// create a new bitmap with same width but taller to fit reflection
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		// create a new Canvas with the bitmap that's big enough for
		// the image plus gap plus reflection
		Canvas canvas = new Canvas(bitmapWithReflection);
		// draw in the original image
		canvas.drawBitmap(originalImage, 0, 0, null);
		// draw in the gap
		Paint defaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
		// draw in the reflection
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		// create a shader that is a linear gradient that covers the reflection
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0,
				originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
						+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		// set the paint to use this shader (linear gradient)
		paint.setShader(shader);
		// set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}
}
