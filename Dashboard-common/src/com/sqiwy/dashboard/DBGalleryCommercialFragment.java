package com.sqiwy.dashboard;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.sqiwy.dashboard.model.action.Action;
import com.sqiwy.dashboard.util.AnimUtils;
import com.sqiwy.dashboard.util.StatsUtils;
import com.sqiwy.dashboard.util.StatsUtils.StatsContext;
import com.sqiwy.menu.advertisement.Advertisement;
import com.sqiwy.menu.advertisement.AdvertisementManager;
import com.sqiwy.menu.view.TransitionAnimationFrameLayout;

import java.util.List;

/**
 * Created by abrysov
 */

public class DBGalleryCommercialFragment extends ActionFragment implements OnClickListener {

	private static final String ARG_CLICK_ANIMATION = "arg-click-animation";
	private static final String ARG_STATS_CONTEXT = "arg-stats-context", ARG_PLACE="arg-place";
	private static final String ARG_TYPE = "arg-type";
	private static final boolean DEBUG = BuildConfig.DEBUG;
	private static final String TAG = DBGalleryCommercialFragment.class.getName();
	
	private List<Advertisement> mAds;
	private int mCurrentAdPosition = -1;
	private long mCurrentAdStartTime;
	private Advertisement mCurrentAd;
	private boolean mIsCurrentAdPressed;
	private boolean mIsCurrentAdReported;
	
	private AdvertisementBaseFragment mVideoAdFragment;
	private AdvertisementBaseFragment mImageAdFragment;
	private TransitionAnimationFrameLayout mContainer;

	private Advertisement.Type mType;
	private Advertisement.Places mPlace;
	private StatsContext mStatsContext;
	private boolean mEnableClickAnimation;
	
	/**
	 * Instantiates fragment.
	 * @param type pass null to get all ads.
	 * @param statsContext context to use to report stats.
	 * @param enableClickAnimation enable click ad animation or not.
	 * @return ads fragment.
	 */
	public static DBGalleryCommercialFragment newInstacne(
			Advertisement.Type type, StatsContext statsContext, boolean enableClickAnimation, Advertisement.Places place) {
		
		DBGalleryCommercialFragment f = new DBGalleryCommercialFragment();
		
		Bundle args = new Bundle();
		if (null != type) {
			args.putString(ARG_TYPE, type.name());
		}
		args.putString(ARG_STATS_CONTEXT, statsContext.name());
		args.putBoolean(ARG_CLICK_ANIMATION, enableClickAnimation);
		args.putString(ARG_PLACE, place.toString());
		
		f.setArguments(args);
		
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mStatsContext = StatsContext.valueOf(args.getString(ARG_STATS_CONTEXT));
		
		if (args.containsKey(ARG_TYPE)) {
			mType = Advertisement.Type.valueOf(args.getString(ARG_TYPE));
		}
		mPlace=Advertisement.Places.fromString(args.getString(ARG_PLACE));
		mEnableClickAnimation = args.getBoolean(ARG_CLICK_ANIMATION);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContainer = (TransitionAnimationFrameLayout) inflater.inflate(R.layout.fragment1, null);
		return mContainer;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		FragmentManager fm = getFragmentManager();
		
		mImageAdFragment = (AdvertisementBaseFragment) fm.findFragmentById(R.id.ad_image_fragment);
		mImageAdFragment.setTargetFragment(this, 0);
		mImageAdFragment.getView().setOnClickListener(this);
		
		mVideoAdFragment = (AdvertisementBaseFragment) fm.findFragmentById(R.id.ad_video_fragment);
		mVideoAdFragment.setTargetFragment(this, 0);
		mVideoAdFragment.getView().setOnClickListener(this);
		
		mContainer.setMaskVisibility(true);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		mAds = AdvertisementManager.getAds(mType,mPlace);
		
		showNextAd();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		mContainer.setMaskVisibility(true);
		//mContainer.startMaskTransition(true);
		
		reportAd(mCurrentAd);
	}
	
	public void showNextAd() {
		// There can be cases when ads are not available.
		if (null != mAds && !mAds.isEmpty()) {
			int positionOfAdToDisplay = ++mCurrentAdPosition % mAds.size();
			Advertisement adToDisplay = mAds.get(positionOfAdToDisplay);
			showAd(adToDisplay);
		}
	}
	
	public void showAd(Advertisement ad) {

		if (isAdded() && null != getFragmentManager()) {
		
			mCurrentAd = ad;
			mIsCurrentAdPressed = false;
			mIsCurrentAdReported = false;
			
			if (DEBUG) {
				Log.d(TAG, "showAd " + ad.getName() + " " + ad.getType());
			}
			
			AdvertisementBaseFragment fragment = null;
	
			final FragmentTransaction transaction = getFragmentManager().beginTransaction();
			if (ad.getType() == Advertisement.Type.IMAGE) {
				
				hide(mVideoAdFragment, transaction);
				show(mImageAdFragment, transaction);
				
				fragment = mImageAdFragment;
			} else if (ad.getType() == Advertisement.Type.VIDEO){
				
				hide(mImageAdFragment, transaction);
				show(mVideoAdFragment, transaction);
				
				fragment = mVideoAdFragment;
			}
			transaction.commitAllowingStateLoss();
	
			// Set approximate start time. More precise time will be set in onAdShown.
			mCurrentAdStartTime = System.currentTimeMillis();
			fragment.showAd(ad);
		}
	}
	
	protected void hide(Fragment fragment, FragmentTransaction transaction) {
		if (null != fragment) {
			transaction.hide(fragment);
		}
	}
	
	protected void show(Fragment fragment, FragmentTransaction transaction) {
		if (null != fragment) {
			transaction.show(fragment);
		}
	}
	
	public void onAdShown(Advertisement ad) {
		
		if (DEBUG) {
			Log.d(TAG, "onAdShown " + ad.getName());
		}
		
		if (mCurrentAd.equals(ad)) {
			// Disable mask transition if ad shown is what we scheduled previously. 
			mContainer.startMaskTransition(false);
			
			// Set precise start time.
			mCurrentAdStartTime = System.currentTimeMillis();
			
		} else {
			Log.w(TAG, "Wrong ad shown reported");
		}
	}
	
	public void onAdFinished(Advertisement ad) {
		
		if (DEBUG) {
			Log.d(TAG, "onAdFinished " + ad.getName() + " " + isResumed());
		}
		
		if (mCurrentAd.equals(ad)) {
			
			reportAd(ad);
			
			mContainer.setMaskVisibility(true);
			showNextAd();
			
		} else {
			Log.w(TAG, "Wrong ad finished reported");
		}
	}

	public void reportAd(Advertisement ad) {
		if (!mIsCurrentAdReported && null != ad) {
			StatsUtils.reportAdvertisementStats(
					ad.getId(), System.currentTimeMillis() - mCurrentAdStartTime, 
					mIsCurrentAdPressed ? 1 : 0, mStatsContext);
			
			mIsCurrentAdReported = true;
		}
	}
	
	@Override
	public void onClick(View v) {
		if (null != mCurrentAd) {
			if (mEnableClickAnimation) {
				AnimUtils.animateViewInset((View) v.getParent());
			}
			
			Action action = mCurrentAd.getAction();
			if (null != action) {
				getActionHelper().executeDelayed(action, getActivity());
			}
			mIsCurrentAdPressed = true;
			reportAd(mCurrentAd);
		}
	}

}
