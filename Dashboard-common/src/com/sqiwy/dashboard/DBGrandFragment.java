package com.sqiwy.dashboard;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.fosslabs.android.utils.JSLog;
import com.sqiwy.dashboard.model.action.Action;
import com.sqiwy.dashboard.model.action.Action.OnActionDoneListener;
import com.sqiwy.dashboard.util.ExecutorUtils;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.chat.ChatManager;
import com.sqiwy.restaurant.api.ClientConfig.SupportedLanguage;

import java.util.Locale;

/**
 * Created by abrysov
 */

public class DBGrandFragment extends ActionFragment implements OnClickListener {
	
	private static final String TAG = "DBGrandFragment";
	private static final int ANIMATION_DURATION_BASE = 600;
	private static final String EXTRA_TYPE = "EXTRA_TYPE";
	private int mType;
	private volatile boolean mIsCallingWaiter = false;
    private ToggleButton mLangChange;

	public static class DBGrandFragmentType{
		public static final int EXTRA_TYPE_MAIN = 0;
		public static final int  EXTRA_TYPE_CAFE= 1;
	}
	
	private TopMenuCallbacks mCallbacks;

	public interface TopMenuCallbacks {
		void onScreenRotate();
		void onLock();
		void onBack();
	}
	
	@Override
	public void onResume() {

		super.onResume();
		
		mIsCallingWaiter = false;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (TopMenuCallbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	public static DBGrandFragment newInstance(int type) {
		Bundle args = new Bundle();
		args.putInt(EXTRA_TYPE, type);
		
		 DBGrandFragment fragment = new DBGrandFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mType = getArguments().getInt(EXTRA_TYPE, DBGrandFragmentType.EXTRA_TYPE_MAIN);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = null;
		switch (mType) {
		case DBGrandFragmentType.EXTRA_TYPE_MAIN:{
			v = inflater.inflate(R.layout.fragment_grand_db, null);
			createViewMain(v);
			break;
		}
		case DBGrandFragmentType.EXTRA_TYPE_CAFE:{
			v = inflater.inflate(R.layout.fragment_grand_cafe, null);
			createViewCafe(v);
			break;
		}
		default:
			break;
		}
		return v;
	}
	
	private void createViewMain(View v) {
		v.findViewById(R.id.btn_call_waiter).setOnClickListener(this);
		v.findViewById(R.id.btn_clear_history).setOnClickListener(this);
		v.findViewById(R.id.btn_rotate_screen).setOnClickListener(this);
		v.findViewById(R.id.btn_lock_screen).setOnClickListener(this);
        mLangChange = (ToggleButton)v.findViewById(R.id.tbutton_lang_change);
        mLangChange.setOnClickListener(this);


        SupportedLanguage currentLanguage = MenuApplication.getLanguage();

        if (currentLanguage.equals(SupportedLanguage.RU)) { //ZH
            setLanguage(SupportedLanguage.RU);
            mLangChange.setBackgroundResource(R.drawable.bg_lang_change_ru_en);
        } else {
            // :FIXME add support for any of RU,EN,ZH
            MenuApplication.setLanguage(SupportedLanguage.EN);
            setLanguage(SupportedLanguage.EN);
            mLangChange.setBackgroundResource(R.drawable.bg_lang_change_en_ru);
        }
	}
	
	@Override
	public void onClick(View v) {
		anim_item_menu_click(v);
		switch (v.getId()) {
			case R.id.btn_call_waiter:
				if (!mIsCallingWaiter) {
					mIsCallingWaiter = true;
					Action callWaiterAction = Action.Resolver.create(Action.TYPE_CALL_WAITER)
							.setOnActionDoneListener(new OnActionDoneListener() {
						@Override
						public void onActionDone(Action action) {
							ExecutorUtils.executeOnUiThreadDelayed(new Runnable() {
								@Override
								public void run() {
									mIsCallingWaiter = false;
								}	
							}, 1000);					
						}
					});
					getActionHelper().executeDelayed(callWaiterAction, getActivity());
				}
				break;
			case R.id.btn_clear_history:
				Action closeSessionAction = Action.Resolver.create(Action.TYPE_CLOSE_SESSION);
				getActionHelper().executeDelayed(closeSessionAction, getActivity());
				ChatManager.getInstance().clearOnChatManagerEventListeners();			
				break;
			case R.id.btn_rotate_screen:
				mCallbacks.onScreenRotate();
				break;
			case R.id.btn_lock_screen:
				mCallbacks.onLock();
				break;
	        case  R.id.tbutton_lang_change:

	            if (MenuApplication.getLanguage().equals(SupportedLanguage.ZH)) {

                    MenuApplication.setLanguage(SupportedLanguage.EN);
                    mLangChange.setBackgroundResource(R.drawable.bg_lang_change_zh_en);
                    setLanguage(SupportedLanguage.EN);

                } else if (MenuApplication.getLanguage().equals(SupportedLanguage.EN)){

                    MenuApplication.setLanguage(SupportedLanguage.RU);
                    mLangChange.setBackgroundResource(R.drawable.bg_lang_change_ru_en);
                    setLanguage(SupportedLanguage.RU);

                } else {

                    MenuApplication.setLanguage(SupportedLanguage.EN);
                    mLangChange.setBackgroundResource(R.drawable.bg_lang_change_en_ru);
                    setLanguage(SupportedLanguage.EN);

                }
	            Intent intent = getActivity().getIntent();
	            getActivity().overridePendingTransition(0, 0);
	            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            getActivity().finish();
	            getActivity().overridePendingTransition(0, 0);
	            getActivity().getApplicationContext().startActivity(intent);
	            break;
		}
	}

    public static void setLanguage(SupportedLanguage language) {
        Locale locale = new Locale(language.getCode());

        if (SupportedLanguage.ZH == language) {
            Log.e("CURR_LANG", SupportedLanguage.ZH.getCode());
            locale = Locale.SIMPLIFIED_CHINESE; // Chinese (Simplified) or Russian

        }

        if (SupportedLanguage.RU == language) {
            Log.e("CURR_LANG", SupportedLanguage.RU.getCode());
            locale = new Locale("ru");
        }

        Configuration config = new Configuration();
        config.locale = locale;
        MenuApplication.getContext().getResources().updateConfiguration(config,
                MenuApplication.getContext().getResources().getDisplayMetrics());
    }
	
	private void createViewCafe(View v) {
		int height = (int) getResources().getDimension(
				R.dimen.fragment_grand_height);
		JSLog.d(TAG, "fragment_menu_grand_height " + height);
		View ll_00 = v.findViewById(R.id.ll_00);
		ll_00.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				anim_item_menu_click(v);
				mCallbacks.onBack();				
			}
		});
		
		View ll_1 = (LinearLayout) v.findViewById(R.id.ll_1);
		ll_1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				anim_item_menu_click(v);
				mCallbacks.onScreenRotate();				
			}
		});
		View ll_2 = (LinearLayout) v.findViewById(R.id.ll_2);
		ll_2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				anim_item_menu_click(v);
				mCallbacks.onLock();	
				
			}
		});
	}
	
	private void anim_item_menu_click(View v) {
		JSLog.d(TAG, v.getMeasuredWidth() + " " + v.getWidth());
		ObjectAnimator animator = ObjectAnimator
				.ofFloat(v, "scale_x", 1, 3);
		animator.setDuration(ANIMATION_DURATION_BASE/4);
		
		ObjectAnimator animAlpha = ObjectAnimator.ofFloat(v, "alpha", 0, 1);
		animAlpha.setDuration(ANIMATION_DURATION_BASE/4);
		
		AnimatorSet set = new AnimatorSet();
		set.setInterpolator(new LinearInterpolator());
		set.playTogether(animator, animAlpha);
		set.start();
	}
}
