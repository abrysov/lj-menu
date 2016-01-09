package com.sqiwy.dashboard.util;

import java.io.File;
import java.io.FileInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Created by abrysov
 * Image helper
 */
public final class ImageUtils {
	
	/**
	 * Load image from file
	 * 
	 * @param file
	 * 			image file
	 * @param maxPixels
	 * 			max image pixels allowed, if actual image size bigger than
	 * 			allowed then loaded image automatically downscaled 
	 * @return loaded image
	 */
	public static Bitmap loadImageFromFile(final File file, final int maxPixels) throws Exception {
		
		RewindableInputStream is = null;
		
		try {
			
			is = new RewindableInputStream(new FileInputStream(file));
			
			return loadImageFromStream(is, maxPixels);
		}
		finally {
		
			FileUtils.close(is);
		}
	}

	/**
	 * Load image from input stream
	 * @param stream
	 * 			image rewindable input stream
	 * @param maxPixels
	 * 			max image pixels allowed, if actual image size bigger than
	 * 			allowed then loaded image automatically downscaled 
	 * @return loaded image
	 */
	public static Bitmap loadImageFromStream(final RewindableInputStream stream, final int maxPixels) throws Exception {

		Bitmap res = null;

		int inWidth = 0;
		int inHeight = 0;

		// decode image size (decode metadata only, not the whole image)
		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(stream, null, options);
		stream.rewind();

		// save width and height
		inWidth = options.outWidth;
		inHeight = options.outHeight;

		if ((inWidth * inHeight) > maxPixels) {

			int desiredHeight = (int) Math.sqrt(((float) inHeight * (float) maxPixels)/ (float) inWidth);
			int desiredWidth = maxPixels / desiredHeight;

			// decode full image pre-resized
			options = new BitmapFactory.Options();
			options.inSampleSize = Math.max(inWidth / desiredWidth,	inHeight / desiredHeight);

			// decode full image
			Bitmap roughBitmap = BitmapFactory.decodeStream(stream, null, options);

			// calc exact destination size
			Matrix m = new Matrix();
			RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
			RectF outRect = new RectF(0, 0, desiredWidth, desiredHeight);
			m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
			float[] values = new float[9];
			m.getValues(values);

			// resize bitmap
			res = Bitmap.createScaledBitmap(roughBitmap,
					(int) (roughBitmap.getWidth() * values[0]),
					(int) (roughBitmap.getHeight() * values[4]), true);

		} 
		else {

			res = BitmapFactory.decodeStream(stream);
		}
		
		if(null == res) {
			
			throw new Exception("Fail to decode input stream");
		}

		return res;
	}

	/**
	 * Resize bitmap if its size in pixels exceed limit
	 * 
	 * @param src
	 *            source bitmap
	 * @param maxPixels
	 *            max image pixels allowed, if actual image size bigger than
	 *            allowed then loaded image automatically downscaled
	 * @return original or resized bitmap, anyway returned bitmap will not
	 *         exceed pixels limit
	 */
	public static Bitmap resizeBitmapIfNecessary(Bitmap src, int maxPixels) {

		Bitmap dst = src;

		final int imgWidth = src.getWidth();
		final int imgHeight = src.getHeight();

		if ((src.getWidth() * src.getHeight()) > maxPixels) {

			// calc exact destination size
			int desiredHeight = (int) Math.sqrt(((float) imgHeight * (float) maxPixels)	/ (float) imgWidth);
			int desiredWidth = maxPixels / desiredHeight;
			Matrix m = new Matrix();
			RectF inRect = new RectF(0, 0, imgWidth, imgHeight);
			RectF outRect = new RectF(0, 0, desiredWidth, desiredHeight);
			m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
			float[] values = new float[9];
			m.getValues(values);

			// resize bitmap
			dst = Bitmap.createScaledBitmap(src, (int) (imgWidth * values[0]), (int) (imgHeight * values[4]), true);
		}

		return dst;
	}
}

