package com.sqiwy.dashboard;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fosslabs.android.utils.JSLog;
import com.sqiwy.menu.MenuApplication;

/**
 * Created by abrysov
 */

/**
 * Use CancelableToast to create notification
 * that is similar to system toast notification, 
 * but with ability to receive user input.
 * 
 * To create and show CancelableToast You
 * should instantiate {@link Config} class,
 * and call show() method.   
 * 
 * @see Config
 * @see VerticalAlignment
 */
public class CancelableToast extends DialogFragment {
	
	public static final String TAG = "notificationFragment"; 
	
	private static final int DEFAULT_BG_COLOR = R.color.cancelable_toast_default_color;
	
	public interface IOnCloseListener {
		public void onClose();
	};
	
	private String mText;
	private boolean isDimed; 
	private boolean enableClose;
	private VerticalAlignment vAlignment;
	private int bgColor = 0;
	private long delay;
	private IOnCloseListener mOnCloseToastListener;
	
	private ViewGroup mInflatedLayout; 
	private ViewGroup root; 
	private ViewGroup toastRoot;
	private ImageView mCloseView;
	private TextView mNotificationTextview;
	private int mBottomMargin;
	
	
	/**
	 * Vertical alignment for {@link CancelableToast} 
	 */
	public enum VerticalAlignment{
		TOP,
		CENTER,
		BOTTOM;
	}
	
	
	/*
	 * 
	 */
	private Runnable mHideRunnable = new Runnable() {
		public void run() {
			try {
				CancelableToast.this.dismiss();
			} catch (Throwable thr) {} // to avoid NPE's by unknown reason, as in ticket 83
		}
	};
	
	
	
	/*
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();
		
		isDimed = args.getBoolean(ARG_DIM, true);
		
		if(isDimed){

			String color = args.getString(ARG_BG);
			
			if(color != null){
				try {
					this.bgColor = Color.parseColor(color);
				} catch (IllegalArgumentException e) {
					this.bgColor = DEFAULT_BG_COLOR;
				}
			} else {
				this.bgColor = DEFAULT_BG_COLOR;
			}
			
		} 
		
		enableClose = args.getBoolean(ARG_CLOSE, true);
		
		vAlignment = VerticalAlignment.valueOf(args.getString(ARG_ALIGN));
		
		int resId = args.getInt(ARG_RES);

		mText = resId != 0 ? getResources().getString(resId) : args.getString(ARG_TEXT); 
		
		this.delay = args.getLong(ARG_DELAY);
		
		mBottomMargin = getResources().getDimensionPixelOffset(R.dimen.notification_bottom_margin);
	}
	
	
	
	/*
	 * 
	 */
	@Override
	public void onDestroyView() {
		mInflatedLayout.removeCallbacks(mHideRunnable);
		if (mOnCloseToastListener!=null) {
			mOnCloseToastListener.onClose();
		}
		super.onDestroyView();
	}

	
	
	/*
	 * 
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mInflatedLayout = (ViewGroup) inflater.inflate(R.layout.notification, container, false);
		
		/*  SET Backgroung here */
		root = (ViewGroup) mInflatedLayout.findViewById(R.id.root);
		
		root.setBackgroundColor(bgColor);
		
		toastRoot = (ViewGroup) mInflatedLayout.findViewById(R.id.toast_root);
		mCloseView = (ImageView) mInflatedLayout.findViewById(R.id.btn_close);
		mNotificationTextview = (TextView) mInflatedLayout.findViewById(R.id.notification_text);
		mNotificationTextview.setText(mText);
		
		if (!enableClose) {
			mCloseView.setVisibility(View.INVISIBLE);
		} else {
			mCloseView.setVisibility(View.VISIBLE);
		}
		
		// close notification after click
		mInflatedLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mInflatedLayout.removeCallbacks(mHideRunnable);
				mHideRunnable.run();
			}
		});
		
		getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		
		return mInflatedLayout;
	}
	
	
	
	/*
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();
		
		toastRoot.post(new Runnable() {
			
			public void run() {
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) toastRoot.getLayoutParams();
				
				switch (vAlignment) {
				case TOP:
					lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;					
					break;
				case BOTTOM:
					lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
					break;
				case CENTER:
					lp.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
					break;
					
				}
				
				lp.topMargin = mBottomMargin;
				lp.bottomMargin = mBottomMargin;
				toastRoot.setLayoutParams(lp);
				
			}
		});
		
		toastRoot.postDelayed(mHideRunnable, this.delay);
	}

	public void setOnCloseListener(IOnCloseListener listener) {
		mOnCloseToastListener=listener;
	}
	
	private static final String ARG_DIM = "dim";
	private static final String ARG_CLOSE = "close";
	private static final String ARG_RES = "res";
	private static final String ARG_TEXT = "text";
	private static final String ARG_ALIGN = "align";
	private static final String ARG_BG = "bg";
	private static final String ARG_DELAY = "delay";

	
	
	/**
	 * In order to instantiate and show CancelableToast 
	 * use this class as the helper.
	 * <br/>
	 * Set parameters for CancelableToast by 
	 * <br/><br/>
	 * {@link Config#setVerticalAlignment(VerticalAlignment)} - sets
	 * the {@link VerticalAlignment} for the toast. Default value {@link VerticalAlignment#BOTTOM}
	 * <br/><br/>
	 * {@link Config#setDim(boolean)} - enables or disables screen dim when toast gets displayed. 
	 * By default screen dim is enabled and default background color is #77000000.
	 * <br/><br/>
	 * {@link Config#setDim(String)} - enables screen dim with the custom color. 
	 * Color can be passed in HTML like style color. If passed value is invalid - then default 
	 * background color will be used.
	 * <br/>
	 * If {@link Config#setDim(boolean)} disables dim then transparent background going to be used
	 * despite the {@link Config#setDim(String)} call with valid value.
	 * 
	 * <br/><br/>
	 * {@link Config#setText(int)} or {@link Config#setText(String)} - sets the toast text message.
	 * setText(int) will always override text setted by setText(String) despite the call order.
	 * <br/><br/>
	 * {@link Config#setClosable(boolean)} - enables close button in the top right corner. However
	 * ClosableToast can be closed without enabled close button. Use this feature to explicitly
	 * show to user that {@link CancelableToast} can be closed manually.
	 * <br/><br/>
	 * Finally call {@link Config#show(FragmentManager)} to show the {@link CancelableToast}.
	 */
	public static class Config{
		
		private VerticalAlignment alignment = VerticalAlignment.BOTTOM;
		private boolean isDimed = true;
		private boolean isClosable = true;
		private String text;
		private int resId;
		private IOnCloseListener mOnCloseListener=null;
		private String backgroundColor = "#77000000";
		private long delay = 7000;
		
		
		
		/**
		 * Sets {@link CancelableToast} {@link VerticalAlignment}.
		 * Default value {@link VerticalAlignment#BOTTOM}.
		 * @param alignment
		 * @return
		 */
		public Config setVerticalAlignment(VerticalAlignment alignment){
			this.alignment = alignment;
			return this;
		}
		
		
		
		/**
		 * Enables or disables screen dim.
		 * By default screen dim is enabled and default background color is #77000000.
		 * If pass false value then screen dim will be disabled
		 * despite the {@link Config#setDim(String)} call.
		 * @param isDimed - true enables, fals edisables
		 * @return
		 */
		public Config setDim(boolean isDimed){
			this.isDimed = isDimed;
			return this;
		}
		
		
		
		/**
		 * Enables screen dim with the custom color. 
		 * Color can be passed in HTML like style color. 
		 * If passed value is invalid - then default 
		 * background color will be used {@link CancelableToast#DEFAULT_BG_COLOR}.
		 * This method is useless if call {@link Config#setDim(boolean)} wit false argument.
		 * @param backgroundColor
		 * @return
		 */
		public Config setDim(String backgroundColor){
			this.backgroundColor = backgroundColor;
			return this;
		}
		
		
		/**
		 * Enables or disables close button in the top right corner. However
		 * ClosableToast can be closed without enabled close button. Use this feature to explicitly
		 * show to user that {@link CancelableToast} can be closed manually.
		 * @param isClosable
		 * @return
		 */
		public Config setClosable(boolean isClosable){
			this.isClosable = isClosable;
			return this;
		}
		
		
		
		/**
		 * Sets the toast text message.
		 * This method will always override text setted 
		 * by {@link Config#setText(String)} despite the call order.
		 * @param resId
		 * @return
		 */
		public Config setText(int resId){
			this.resId = resId;
			return this;
		}
		
		
		/**
		 * Sets the toast text message.
		 * @param text
		 * @return
		 */
		public Config setText(String text){
			this.text = text;
			return this;
		}
		
		
		
		/**
		 * Sets timeout in milliseconds. After passed time elapse 
		 * notification will be dismissed.
		 * Default value is 7000.
		 * @param delay
		 * @return
		 */
		public Config setTimeOut(long delay){
			this.delay = delay;
			return this;
		}
		
		/**
		 * Set callback to be called at toast close.
		 * @param listener
		 */
		public Config setOnCloseListener(IOnCloseListener listener) {
			this.mOnCloseListener=listener;
			return this;
		}
		
		/**
		 * Creates {@link CancelableToast} with specified parameters
		 * and displays it. FragmentManager must be passed as 
		 * {@link CancelableToast} is {@link DialogFragment}.
		 * If invalid object or null passed, then standard system toast
		 * notification will be shown.
		 * @param FragmentManager
		 */
		public void show(FragmentManager fm){
			
			if(fm != null && fm instanceof FragmentManager){
				CancelableToast toast = new CancelableToast();
				
				Bundle args = new Bundle();
				
				args.putInt(ARG_RES, resId);
				args.putLong(ARG_DELAY, delay);
				args.putBoolean(ARG_CLOSE, isClosable);
				args.putBoolean(ARG_DIM, isDimed);
				args.putString(ARG_TEXT, text);
				args.putString(ARG_ALIGN, alignment.name());
				args.putString(ARG_BG, backgroundColor);
				
				toast.setArguments(args);
				
				toast.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme);
				toast.setOnCloseListener(mOnCloseListener);
				
				try {
					toast.show(fm, TAG);
				} catch (Throwable error) {
					JSLog.d(TAG, "showNotificationInCenter() failed", error);
				}
				
			} else {
				
				String message = this.resId != 0 ? MenuApplication.getContext().getResources().getString(resId) : this.text; 
				Toast.makeText(MenuApplication.getContext(), message, Toast.LENGTH_SHORT).show();
				
			}
		}
		
	}
	
	
}