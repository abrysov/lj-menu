package com.fosslabs.android.context;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.fosslabs.android.model.JSMapImageViewPoint;
import com.fosslabs.android.jsbase.R;
import com.fosslabs.android.utils.Funs;
import com.fosslabs.android.utils.JSImageWorker;
import com.fosslabs.android.utils.JSLog;
import com.fosslabs.android.view.JSMapImageView;
import com.fosslabs.android.view.JSMapImageViewTouch;

public class TestMapImageViewActivity extends Activity implements
		JSMapImageViewTouch.OnDoubleClickListener,
		JSMapImageView.OnDrawFirstTime {
	private static final String TAG = "TestMapImageViewActivity";

	private int mPinWidth = 24;
	private int mCurPos = -1;
	private ArrayList<ContentValues> list = new ArrayList<ContentValues>();
	private ArrayList<BitmapDrawable> list_back = new ArrayList<BitmapDrawable>();

	private class ViewHolder {
		private JSMapImageView miv;
		private Drawable miv_bg;
		private LinearLayout ll_parent;
	}

	private ViewHolder vh = new ViewHolder();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Hide the status bar and other OS-level chrome
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_test_map_image_view);
		vh.ll_parent = (LinearLayout) findViewById(R.id.ll_parent);
		vh.miv = (JSMapImageView) findViewById(R.id.miv);
		vh.miv_bg = getResources().getDrawable(R.drawable.jsmapiv);
		vh.miv.setImageDrawable(vh.miv_bg);// Drawable(circle_pic.getDrawable(this));
		vh.miv.setOnMeasure(this);
		vh.miv.setOnTouchListener(new JSMapImageViewTouch(this));
		vh.miv.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@SuppressLint("NewApi")
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						JSLog.d(TAG, "onGlobalLayout() " + vh.miv.getWidth());
						// anim_item_menu(vr);
						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
							vh.miv.getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);
						} else
							vh.miv.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
					}
				});

	}

	private ContentValues createPoint(float x, float y, float x2, float y2,
			String desc) {
		ContentValues cv = new ContentValues();
		cv.put(JSMapImageViewPoint.Columns.ID, -1);
		// cv.put(SuperObject.Columns.ID_CIRCLE_PIC,
		// circle_pic.getAsInteger(SuperObject.Columns.ID_CIRCLE_PIC));
		cv.put(JSMapImageViewPoint.Columns.X_COOR, x);
		cv.put(JSMapImageViewPoint.Columns.Y_COOR, y);
		cv.put(JSMapImageViewPoint.Columns.X_COOR2, x2);
		cv.put(JSMapImageViewPoint.Columns.Y_COOR2, y2);
		cv.put(JSMapImageViewPoint.Columns.DESC, desc);
		return cv;
	}

	private void recreatePoints() {
		vh.miv.setImageDrawable(vh.miv_bg);// setImageDrawable(circle_pic.getDrawable(this));
		mCurPos = -1;
		for (ContentValues cv : list) {
			mCurPos++;
			doubleClick(true,
					cv.getAsFloat(JSMapImageViewPoint.Columns.X_COOR),
					cv.getAsFloat(JSMapImageViewPoint.Columns.Y_COOR), vh.miv,
					cv.getAsFloat(JSMapImageViewPoint.Columns.X_COOR2),
					cv.getAsFloat(JSMapImageViewPoint.Columns.Y_COOR2));

		}

	}

	@Override
	public void draw() {
		recreatePoints();
	}

	@Override
	public void doubleClick(boolean start, float x, float y, JSMapImageView iv,
			float x2, float y2) {
		Log.i(TAG, " doubleClick");
		if (!start) {
			if (!addPoint(x, y, iv, x2, y2))
				return;
		}

		iv.buildDrawingCache();
		BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
		Bitmap bmap = iv.getDrawingCache();
		Bitmap bmap2 = drawable.getBitmap();
		Bitmap bm = getPin();
		Canvas tempCanvas = new Canvas(bmap);
		tempCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

		// Draw the image bitmap into the canvas
		tempCanvas.drawBitmap(bm, x, y, null);

		iv.setImageDrawable(new BitmapDrawable(getResources(), combineImages(
				bmap2, bmap, x2 - mPinWidth / 2, y2 - mPinWidth / 2)));
		list_back.add(mCurPos, new BitmapDrawable(getResources(), bm));
		showPin(false, x, y, iv, x2, y2);
	}

	private void showPin(boolean old, float x, float y, JSMapImageView iv,
			float x2, float y2) {
		Funs.getToast(this, old + " ShowPin " + x + " " + y + " " + x2 + " "
				+ y2);
	}

	private boolean addPoint(float x, float y, JSMapImageView iv, float x2,
			float y2) {

		if (list.size() == 0) {
			list.add(createPoint(x, y, x2, y2, ""));
			mCurPos = 0;
			return true;
		}
		for (int i = 0; i < list.size(); i++) {
			if (isEquals(list.get(i), x, y, x2, y2)) {
				mCurPos = i;
				showPin(true, x, y, iv, x2, y2);
				Log.i(TAG, "isequals");
				return false;
			}
		}
		mCurPos = list.size();

		list.add(createPoint(x, y, x2, y2, ""));

		return true;
	}

	private boolean isEquals(ContentValues cv, float x, float y, float x2,
			float y2) {

		float px = cv.getAsFloat(JSMapImageViewPoint.Columns.X_COOR);
		float py = cv.getAsFloat(JSMapImageViewPoint.Columns.Y_COOR);
		float px2 = cv.getAsFloat(JSMapImageViewPoint.Columns.X_COOR2);
		float py2 = cv.getAsFloat(JSMapImageViewPoint.Columns.Y_COOR2);

		if (((x + x2) - (px + px2)) * ((x + x2) - (px + px2))
				+ ((y + y2) - (py + py2)) * ((y + y2) - (py + py2)) < mPinWidth
				* mPinWidth)
			return true;
		return false;
	}

	public Bitmap combineImages(Bitmap c, Bitmap s, float x2, float y2) {
		// can add a 3rd parameter 'String loc' if you want to save the new
		// image - left some code to do that at the bottom
		Bitmap cs = null;

		int width, height = 0;

		width = c.getWidth();
		height = c.getHeight();

		cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		Canvas comboImage = new Canvas(cs);

		comboImage.drawBitmap(c, 0f, 0f, null);
		comboImage.drawBitmap(s, x2, y2, null);

		// this is an extra bit I added, just incase you want to save the new
		// image somewhere and then return the location
		/*
		 * String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";
		 * 
		 * OutputStream os = null; try { os = new FileOutputStream(loc +
		 * tmpImg); cs.compress(CompressFormat.PNG, 100, os); }
		 * catch(IOException e) { Log.e("combineImages",
		 * "problem combining images", e); }
		 */

		return cs;
	}

	public Bitmap getPin() {
		int w = mPinWidth;
		Bitmap bmp = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(getResources().getColor(R.color.shade));
		paint.setAlpha(85);
		int s = w / 8;
		canvas.drawCircle((w + s) / 2, (w + s) / 2, (w - s) / 2, paint);
		paint.setARGB(255, ((int) (Math.random() * 100000)) % 256,
				((int) (Math.random() * 100000)) % 256,
				((int) (Math.random() * 100000)) % 256);
		canvas.drawCircle((w - s) / 2, (w - s) / 2, (w - s) / 2, paint);

		return bmp;
	}

}
