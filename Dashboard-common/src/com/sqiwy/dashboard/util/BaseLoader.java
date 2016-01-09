package com.sqiwy.dashboard.util;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;

/**
 * Created by abrysov
 */

/**
 * Base loader to load data asynchronously.
 *
 * @param <T> result type.
 */
public abstract class BaseLoader<T> extends AsyncTaskLoader<T> {

	private final Loader<T>.ForceLoadContentObserver mObserver;
	private T mResult;
	
	public BaseLoader(Context context) {
		super(context);
		mObserver = new ForceLoadContentObserver();
	}
	
	public Loader<T>.ForceLoadContentObserver getObserver() {
		return mObserver;
	}

	@Override
	public void deliverResult(T data) {
		if (isReset()) {
			mResult = null;
			return;
		}
		
		mResult = data;
		
		if (isStarted()) {
			super.deliverResult(data);
		}
	}
	
	@Override
	protected void onStartLoading() {
		if (null != mResult) {
			deliverResult(mResult);
		}
		
		if (takeContentChanged() || null == mResult) {
			forceLoad();
		}
	}
	
	@Override
	protected void onStopLoading() {
		super.onStopLoading();
		cancelLoad();
	}
	
	@Override
	protected void onReset() {
		super.onReset();
		cancelLoad();
		getContext().getContentResolver().unregisterContentObserver(getObserver());
		mResult = null;
	}
}

