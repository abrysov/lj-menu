package com.sqiwy.dashboard.util;

import java.util.concurrent.Executor;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by abrysov
 * Helper class to work with executors
 */
public class ExecutorUtils {
	
	/**
	 * variables
	 */
	private static Handler mUiHandler = null;
	private static Executor mUiExecutor = null;
	
	/**
	 * Check if called from UI thread
	 * @return true if called from UI thread, false otherwise
	 */
	public final static boolean isUiThread() {
		
		return (Looper.getMainLooper() == Looper.myLooper());
	}
	
	/**
	 * Return executor which invoke calls in ui thread.  
	 * @return executor which invoke calls in ui thread
	 */
	public static synchronized Executor getUiThreadExecutor() {
		
		if(null == mUiExecutor) {
		
			validateHandlerExists();
			
			mUiExecutor = new Executor() {
				
				@Override
				public void execute(Runnable command) {

					mUiHandler.post(command);
				}
			};
		}
		
		return mUiExecutor;
	}
	
	/**
	 * 
	 * @param runnable
	 * @param delay
	 */
	public static synchronized void executeOnUiThread(Runnable runnable) {
		
		validateHandlerExists();		
		mUiHandler.post(runnable);
	}
	
	/**
	 * 
	 * @param runnable
	 * @param delay
	 */
	public static synchronized void executeOnUiThreadDelayed(Runnable runnable, long delay) {
		
		validateHandlerExists();		
		mUiHandler.postDelayed(runnable, delay);
	}
	
	/**
	 * 
	 */
	private static void validateHandlerExists() {
		
		if(null == mUiHandler) {
			
			mUiHandler = new Handler(Looper.getMainLooper());
		}
	}
}
