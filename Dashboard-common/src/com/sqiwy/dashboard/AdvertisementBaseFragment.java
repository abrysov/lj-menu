package com.sqiwy.dashboard;

import com.sqiwy.menu.advertisement.Advertisement;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by abrysov
 */

public abstract class AdvertisementBaseFragment extends Fragment {
	
	public static final String EXTRA_AD = "extra-ad";
	
	protected static final int MSG_WHAT_COMPLETED = 0x01;
	protected static final int MSG_WHAT_SHOWN = 0x02;
	
	protected Handler mHandler;
	
	protected class AdHandler extends Handler {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_WHAT_COMPLETED:
				((DBGalleryCommercialFragment) getTargetFragment()).onAdFinished(getAd());
				break;
			case MSG_WHAT_SHOWN:
				((DBGalleryCommercialFragment) getTargetFragment()).onAdShown(getAd());
				break;
			}
		};
	};
	
	public AdvertisementBaseFragment() {
		setArguments(new Bundle());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new AdHandler();
	}
	
	public final void showAd(Advertisement ad) {
		setAd(ad);
		showAdImpl(ad);
	}
	
	protected abstract void showAdImpl(Advertisement ad);
	
	public Advertisement getAd() {
		return (Advertisement) getArguments().getSerializable(EXTRA_AD);
	}
	
	public void setAd(Advertisement ad) {
		getArguments().putSerializable(EXTRA_AD, ad);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		clearMessages();
	}
	
	public void clearMessages() {
		mHandler.removeCallbacksAndMessages(null);
	}
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	protected boolean isInternalResource(String resource) {
		
		if(resource.startsWith("content://")) {
			
			return true;
		}
		
		return false;
	}
}
