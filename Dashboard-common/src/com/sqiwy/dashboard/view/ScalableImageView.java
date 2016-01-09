package com.sqiwy.dashboard.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;

/**
 * Created by abrysov
 */

public class ScalableImageView extends ImageView {

	// We can be in one of these 4 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	static final int TOUCH = 3;	
	private final static int MOTION_EVENT_ACTION_POINTER_UP = 6;
	
	/**
	 *
	 */
	public interface OnContentClickListener {
	
		void onContentClicked(ScalableImageView iv, int contentX, int contentY);
	}
	
	/**
	 * 
	 */
	Matrix mMatrix = new Matrix();
	int mTouchMode = NONE;
	PointF mPtLastTouch = new PointF();
	PointF mPtStartTouch = new PointF();
	float mZoomMin = 1.0f;
	float mZoomMax = 5.0f;
	float mZoomCurrent = 1.0f;
	float[] m;
	float mRedundantXSpace, mRedundantYSpace;
	int mViewWidth, mViewHeight;
	static final int CLICK = 3;
	float mSavedScale = -1f;
	float mRight, mBottom, mOrigWidth, mOrigHeight;
	int mImageWidth, mImageHeight;
	ScaleGestureDetector mScaleDetector;
	private OnContentClickListener mContentClickListener = null;
	private boolean mIsMultitouchScaleEnabled = true;
	private int mTouchSlope = 1;
	private Point mContentClickPoint = new Point();

	/**
	 * @param context
	 */
	public ScalableImageView(Context context) {
		
		super(context);
		
		initialize(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ScalableImageView(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		
		initialize(context);
	}

	/**
     * 
     */
	@Override
	public void setImageBitmap(Bitmap bm) {

		super.setImageBitmap(bm);

		for (int i = 0; i != m.length; i++) {

			m[i] = 0.0f;
		}

		if (bm != null) {

			mImageWidth = bm.getWidth();
			mImageHeight = bm.getHeight();
		} 
		else {

			mImageWidth = 0;
			mImageHeight = 0;
		}

		setZoom((mZoomMin = recalculateMinZoom(mImageWidth, mImageHeight, mViewWidth, mViewHeight)));
	}
	
	/**
     * 
     */
	@Override
	public void setImageDrawable(Drawable drawable) {
	
		super.setImageDrawable(drawable);
		
		for (int i = 0; i != m.length; i++) {

			m[i] = 0.0f;
		}

		if (drawable != null) {

			mImageWidth = drawable.getIntrinsicWidth();
			mImageHeight = drawable.getIntrinsicHeight();
		} 
		else {

			mImageWidth = 0;
			mImageHeight = 0;
		}

		setZoom((mZoomMin = recalculateMinZoom(mImageHeight, mImageHeight, mViewWidth, mViewHeight)));
	}

	/**
	 * 
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		super.onSizeChanged(w, h, oldw, oldh);

		mViewWidth = w;
		mViewHeight = h;

		setZoom((mZoomMin = recalculateMinZoom(mImageWidth, mImageHeight, mViewWidth, mViewHeight)));
	}
	
	/**************************************************************************************************************
	 * 
	 * 													API
	 * 
	 **************************************************************************************************************/

	/**
	 * 
	 * @return
	 */
	public boolean isImageLoaded() {
	
		return ((mImageWidth > 0) && (mImageHeight > 0));
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void setContentClickListener(OnContentClickListener listener) {
		
		mContentClickListener = listener;
	}
	
	/**
	 * 
	 * @param enable
	 */
	public void setIsMultitouchScaleEnabled(boolean enable) {
		
		if(mIsMultitouchScaleEnabled != enable) {
			
			mTouchMode = NONE;
			mIsMultitouchScaleEnabled = enable;
		}
	}
	
	/**
	 * @return
	 */
	public Rect getVisibleArea() {

		float[] m = new float[9];

		mMatrix.getValues(m);

		return getVisibleArea(new Rect(0, 0, (int) mViewWidth,
				(int) mViewHeight), new Rect(0, 0, (int) mImageWidth,
				(int) mImageHeight), m);
	}
	
	/**
	 * @param x
	 */
	public void setMaxZoom(float zoomMax) {
		
		mZoomMax = zoomMax;
		setZoom(mZoomCurrent);
	}

	/**
     * 
     */
	public float getZoom() {

		return mZoomCurrent;
	}

	/**
	 * 
	 * @return
	 */
	public float getMinZoom() {

		return mZoomMin;
	}

	/**
	 * @param zoom
	 * @return
	 */
	public void setZoom(float zoom) {

		if (mZoomMin > mZoomMax) {

			mZoomMax = mZoomMin;
		}

		if (zoom < mZoomMin) {

			zoom = mZoomMin;
		} 
		else 
		if (zoom > mZoomMax) {

			zoom = mZoomMax;
		}

		if ((0.0f != zoom) && (0 != mImageWidth) && (0 != mImageHeight)) {

			mMatrix.getValues(m);

			float matrixTranslateX, matrixTranslateY;
			float imageActialWidth = mImageWidth * zoom;
			float imageActialHeight = mImageHeight * zoom;
			float left = m[Matrix.MTRANS_X];
			float top = m[Matrix.MTRANS_Y];
			float zoomCurr = m[Matrix.MSCALE_X];
			float oldCenterX = (-left + (mViewWidth / 2)) / zoomCurr;
			float oldCenterY = (-top + (mViewHeight / 2)) / zoomCurr;

			if (imageActialWidth < mViewWidth) {

				// Center the image horizontally
				matrixTranslateX = (imageActialWidth - mViewWidth) / 2.0f;
			} 
			else {

				matrixTranslateX = (oldCenterX * zoom) - (mViewWidth / 2);

				if ((matrixTranslateX + mViewWidth) > imageActialWidth) {

					matrixTranslateX = imageActialWidth - mViewWidth;
				}

				if (matrixTranslateX < 0) {

					matrixTranslateX = 0;
				}
			}

			if (imageActialHeight < mViewHeight) {

				// center the image vertically
				matrixTranslateY = (imageActialHeight - mViewHeight) / 2.0f;
			} 
			else {

				matrixTranslateY = (oldCenterY * zoom) - (mViewHeight / 2);

				if ((matrixTranslateY + mViewHeight) > imageActialHeight) {

					matrixTranslateY = imageActialHeight - mViewHeight;
				}

				if (matrixTranslateY < 0) {

					matrixTranslateY = 0;
				}
			}

			// set-up the matrix
			mZoomCurrent = zoom;
			applyImageMatrix(mZoomCurrent, -matrixTranslateX, -matrixTranslateY);
		}
	}
	
	/**
	 * 
	 * @param reusablePt
	 * @return
	 */
	public Point contentCoordinatesToViewCoordinates(Point reusablePt, int x, int y) {
		
		if( ((mImageWidth > 0) && (x >= 0) && (x < mImageWidth)) &&
			((mImageHeight > 0) && (y >= 0) && (y < mImageHeight))) {
			
			float[] m = new float[9];
			
			mMatrix.getValues(m);
			
			if(null == reusablePt) {
				
				reusablePt = new Point(0, 0);
			}
			
			float zoom = m[Matrix.MSCALE_X];
			float left = m[Matrix.MTRANS_X];
			float top = m[Matrix.MTRANS_Y];
			
			reusablePt.x = (int)(((float)x * zoom) + left);
			reusablePt.y = (int)(((float)y * zoom) + top);
			
			return reusablePt;
		}
		
		return null;
	}
	
	/**************************************************************************************************************
	 * 
	 * 													HELPERS
	 * 
	 **************************************************************************************************************/
	/**
	 * @param context
	 */
	private void initialize(Context context) {

		super.setClickable(true);

		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		mTouchSlope = ViewConfiguration.get(context).getScaledTouchSlop();

		m = new float[9];
		applyImageMatrix(1.0f, 0.0f, 0.0f);

		setScaleType(ScaleType.MATRIX);

		setOnTouchListener(mTouchListener);
	}
	
	/**
	 * 
	 * @param zoom
	 * @param translateX
	 * @param translateY
	 */
	private void applyImageMatrix(float zoom, float translateX, float translateY) {

		mMatrix.reset();
		mMatrix.setScale(zoom, zoom);
		mMatrix.postTranslate(translateX, translateY);
		setImageMatrix(mMatrix);
		invalidate();
	}
	
	/**
     * 
     */
	private float recalculateMinZoom(int imgW, int imgH, int viewW, int viewH) {

		if ((0 != imgW) && (0 != imgH)) {

			//
			float scaleX = (float) viewW / (float) imgW;
			float scaleY = (float) viewH / (float) imgH;

			return Math.min(scaleX, scaleY);
		} 

		return 0.0f;
	}
	
	/**
	 * 
	 * @param dest
	 * @param viewSize
	 * @param imageSize
	 * @param matrix
	 */
	private Point calculateContentClickPoint(Point reusablePt, int viewWidth, int viewHeight, int imgWidth, int imgHeight, float[] matrix, 
			float clickX, float clickY) {
		
		if ((0 != imgWidth) && (0 != imgHeight)) {

			float zoom = matrix[Matrix.MSCALE_X];
			float left = matrix[Matrix.MTRANS_X];
			float top = matrix[Matrix.MTRANS_Y];
			float right = left + (imgWidth * zoom); 
			float bottom = top + (imgHeight * zoom);

			if( ((clickX >= left) && (clickX < right)) &&
				((clickY >= top) && (clickY < bottom)) ) {
				
				if(null == reusablePt) {
					
					reusablePt = new Point();
				}
				
				reusablePt.x = (int)((clickX - left) / zoom);
				reusablePt.y = (int)((clickY - top) / zoom);
				
				return reusablePt;
			}
		}
		
		return null;
	}
	
	/**
	 * @param viewSize
	 * @param imageSize
	 * @param matrix
	 * @return
	 */
	private Rect getVisibleArea(Rect viewSize, Rect imageSize, float[] matrix) {

		float viewWidth = viewSize.width();
		float viewHeight = viewSize.height();
		float imageWidth = imageSize.width();
		float imageHeight = imageSize.height();
		Rect res = null;

		if ((0 != imageWidth) && (0 != imageHeight)) {

			int left, top, right, bottom, width, height;

			float x = matrix[Matrix.MTRANS_X];
			float y = matrix[Matrix.MTRANS_Y];
			float zoomCurr = matrix[Matrix.MSCALE_X];
			if (x > 0) {
				x = 0;
			}
			if (y > 0) {
				y = 0;
			}
			width = (int) Math.abs(viewWidth / zoomCurr);
			height = (int) Math.abs(viewHeight / zoomCurr);
			left = (int) Math.abs(-x / zoomCurr);
			top = (int) Math.abs(-y / zoomCurr);
			right = left + width;
			bottom = top + height;

			if (right > imageWidth) {

				right = (int) imageWidth;
			}

			if (bottom > imageHeight) {

				bottom = (int) imageHeight;
			}

			if (left < 0) {

				left = 0;
			}

			if (top < 0) {

				top = 0;
			}

			res = new Rect(left, top, right, bottom);
		}

		return res;
	}
	
	/**
	 * 
	 * @param ptClick
	 */
	private void notifyContentClicked(PointF ptClick) {
		
		if(null != mContentClickListener) {
			
			float[] m = new float[9];
			mMatrix.getValues(m);
			
			Point contentClickPoint = calculateContentClickPoint(mContentClickPoint, mViewWidth, mViewHeight,
					mImageWidth, mImageHeight, m, ptClick.x, ptClick.y);
			
			if(null != contentClickPoint) {
				
				mContentClickListener.onContentClicked(this, contentClickPoint.x, contentClickPoint.y);
			}
		}
	}

	/**
	 * 
	 */
	private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			float[] m = new float[9];
			
			mScaleDetector.onTouchEvent(event);
						
			mMatrix.getValues(m);

			PointF ptCurrTouch = new PointF(event.getX(), event.getY());

			switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN: {
					
					mPtLastTouch.set(ptCurrTouch);
					mPtStartTouch.set(ptCurrTouch);
					mTouchMode = TOUCH;
				}
				break;
	
				case MotionEvent.ACTION_MOVE: {
	
					if(TOUCH == mTouchMode) {
					
						if( (Math.abs(ptCurrTouch.x - mPtStartTouch.x) > mTouchSlope) ||
							(Math.abs(ptCurrTouch.y - mPtStartTouch.y) > mTouchSlope) ) {
							
							mTouchMode = DRAG;
						}
					}
					
					if (mTouchMode == DRAG) {
	
						float zoom = m[Matrix.MSCALE_X];
						float matrixTranslateX = -m[Matrix.MTRANS_X];
						float matrixTranslateY = -m[Matrix.MTRANS_Y];
						float imageActialWidth = mImageWidth * zoom;
						float imageActialHeight = mImageHeight * zoom;
						float deltaX = (mPtLastTouch.x - ptCurrTouch.x);
						float deltaY = (mPtLastTouch.y - ptCurrTouch.y);
	
						matrixTranslateX += deltaX;
						matrixTranslateY += deltaY;
	
						if (imageActialWidth < mViewWidth) {
	
							matrixTranslateX = (imageActialWidth - mViewWidth) / 2.0f;
						} 
						else 
						if ((matrixTranslateX + mViewWidth) > imageActialWidth) {
	
							matrixTranslateX = imageActialWidth - mViewWidth;
						} 
						else 
						if (matrixTranslateX < 0.0f) {
	
							matrixTranslateX = 0.0f;
						}
	
						if (imageActialHeight < mViewHeight) {
	
							matrixTranslateY = (imageActialHeight - mViewHeight) / 2.0f;
						} 
						else 
						if ((matrixTranslateY + mViewHeight) > imageActialHeight) {
	
							matrixTranslateY = imageActialHeight - mViewHeight;
						} 
						else 
						if (matrixTranslateY < 0.0f) {
	
							matrixTranslateY = 0.0f;
						}
	
						applyImageMatrix(mZoomCurrent, -matrixTranslateX, -matrixTranslateY);
						mPtLastTouch.set(ptCurrTouch);
					}
				}
				break;
	
				case MotionEvent.ACTION_UP: {
					
					if(TOUCH == mTouchMode) {
					
						performClick();						
						notifyContentClicked(mPtStartTouch);
					}
					
					mTouchMode = NONE;	
					mSavedScale = -1.0f;
				}
				break;
	
				case MOTION_EVENT_ACTION_POINTER_UP: {
					
					mTouchMode = NONE;
					mSavedScale = -1.0f;
				}
				break;
				
				case MotionEvent.ACTION_CANCEL: {
					
					mTouchMode = NONE;
					mSavedScale = -1.0f;
				}
				break;
			}
			
			return true; // indicate event was handled
		}
	};
	
	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {

		/**
    	 * 
    	 */
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			
			if(mIsMultitouchScaleEnabled) {
			
				mTouchMode = ZOOM;
			}
			
			return true;
		}

		/**
    	 * 
    	 */
		@Override
		public boolean onScale(ScaleGestureDetector detector) {

			if(ZOOM == mTouchMode) {

				if (mSavedScale < 0.0) {

					mMatrix.getValues(m);
					mSavedScale = m[Matrix.MSCALE_X];
				}

				float mScaleFactor = (float) Math.min(Math.max(.95f, detector.getScaleFactor()), 1.05);
				float origScale = mSavedScale;

				mSavedScale *= mScaleFactor;

				if (mSavedScale > mZoomMax) {

					mSavedScale = mZoomMax;
					mScaleFactor = mZoomMax / origScale;
				} 
				else 
				if (mSavedScale < mZoomMin) {

					mSavedScale = mZoomMin;
					mScaleFactor = mZoomMin / origScale;
				}

				setZoom(mSavedScale);
			}

			return true;
		}
	}
}
