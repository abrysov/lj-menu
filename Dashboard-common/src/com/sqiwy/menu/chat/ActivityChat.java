package com.sqiwy.menu.chat;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sqiwy.restaurant.api.data.ChatUser;
import com.sqiwy.dashboard.R;
import com.sqiwy.dashboard.model.action.Action;
import com.sqiwy.dashboard.model.action.ActionHelper;
import com.sqiwy.dashboard.model.action.Action.OnActionDoneListener;
import com.sqiwy.dashboard.util.ExecutorUtils;
import com.sqiwy.menu.chat.FragMap.OnMapEventListener;
import com.sqiwy.menu.chat.FragUserData.OnUserDataChangeListener;
import com.sqiwy.menu.chat.FragUserList.OnFragUserListListener;
import com.sqiwy.menu.model.ProjectConstants;
import com.sqiwy.menu.util.UIUtils;

/**
 * Created by abrysov
 */

public class ActivityChat extends Activity implements OnMapEventListener, 
													  OnUserDataChangeListener,
													  OnFragUserListListener {
	private FragChat mFragChat;
	private FragUserList mFragUserList;
	private FragMap mFragTableMap;
	private ActionHelper mActionHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		mFragChat = (FragChat) getFragmentManager().findFragmentById(R.id.frag_chat);
		mFragUserList = (FragUserList) getFragmentManager().findFragmentById(R.id.frag_user_list);
		mFragTableMap = (FragMap) getFragmentManager().findFragmentById(R.id.frag_map);
		ChatManager.getInstance().addOnChatManagerEventListener(mFragChat);
		ChatManager.getInstance().addOnChatManagerEventListener(mFragUserList);
		ChatManager.getInstance().addOnChatManagerEventListener(mFragTableMap);
        mActionHelper = new ActionHelper();
        mActionHelper.register(this);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		ChatManager.getInstance().updateChatUserList(null, null);
		if (ChatManager.getInstance().getMe() == null) {
			showUserDataFragment();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        mActionHelper.unregister(this);
        ChatManager.getInstance().clearOnChatManagerEventListeners();
	}
	
	private FragUserData mFragUserData = null;
	
	private void showUserDataFragment() {
		if (mFragUserData == null) {
			mFragUserData = new FragUserData();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.add(R.id.fl_chat, mFragUserData);
			transaction.commit();
		}
	}
	
	public void setColor(View v) {
		mFragUserData.setColor();
	}
	
	private boolean mIsCallingWaiter = false;
	
	private void anim_item_menu_click(View v) {	
		ObjectAnimator animAlpha = ObjectAnimator.ofFloat(v, "alpha", 0, 1);
		animAlpha.setDuration(ProjectConstants.getAnimDuration(this));
		animAlpha.start();
	}
	
	public void callWaiter(View v) {
		anim_item_menu_click(v);
		if(!mIsCallingWaiter) {
			mIsCallingWaiter = true;
			Action callWaiterAction = Action.Resolver.create(Action.TYPE_CALL_WAITER).setOnActionDoneListener(new OnActionDoneListener() {
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
			mActionHelper.executeDelayed(callWaiterAction, this);
		}
	}
	
	public void setSmile(View v) {
		mFragUserData.setSmile();
	}
	
	@Override
	public void onMapObjectClick(String tableId) {
		//TODO not working yet
		Toast.makeText(getApplication(), String.valueOf(tableId), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onUserDataReady(String name, String status, String color, ChatUser.Smile smile) {
		mFragUserData = null;
		if (ChatManager.getInstance().getMe() == null) {
			ChatManager.getInstance().startChat(name, status, color, smile);
		} else {
			ChatManager.getInstance().editChatUser(name, status, color, smile);
		}
	}
	
	@Override
	public void onEditChatUser() {
		showUserDataFragment();
	}
	
	public void goHome(View v) {
		this.finish();
	}
	
	public void rotateScreen(View v) {
		UIUtils.toggleOrientation(this);
	}

}
