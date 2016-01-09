package com.sqiwy.menu.chat;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.sqiwy.restaurant.api.data.ChatUser;
import com.sqiwy.restaurant.api.data.ChatUser.Smile;
import com.sqiwy.dashboard.R;
import com.sqiwy.menu.chat.ChatManager.OnChatManagerEventListener;
import com.sqiwy.menu.view.TypefaceTextView;

/**
 * Created by abrysov
 */

public class FragUserList extends Fragment implements OnItemClickListener,
													  OnChatManagerEventListener {
	private UserListAdapter mUserListAdapter;
	private TypefaceTextView mNickView;
	private TypefaceTextView mStatusView;
	private TypefaceTextView mEditButton;
	private ListView mUserList;
	private TypefaceTextView mEmptyTitle;
	private OnFragUserListListener mListener;
	private GridLayout mTitle;
	private boolean mUserListClick = false;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragUserListListener) activity;
		} catch (Exception e) {
			throw new ClassCastException("Activity must implement OnFragUserListListener");
		}
	}
	
	public interface OnFragUserListListener {
		void onEditChatUser();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.frag_user_list, null);
		
		mNickView = (TypefaceTextView) view.findViewById(R.id.nick);
		mStatusView = (TypefaceTextView) view.findViewById(R.id.status);
		
		mEditButton = (TypefaceTextView) view.findViewById(R.id.btn_edit);
		mEditButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onEditChatUser();
			}
		});
		
		mTitle = (GridLayout) view.findViewById(R.id.title);
		mEmptyTitle = (TypefaceTextView) view.findViewById(R.id.empty_title);
		
		mUserList = (ListView) view.findViewById(R.id.user_list);
		mUserList.setOnItemClickListener(this);
		mUserListAdapter = new UserListAdapter();
		mUserList.setAdapter(mUserListAdapter);
		
		return view;
	}
	
	@Override
	public void onResume() {
		ChatUser me = ChatManager.getInstance().getMe();
		if (me != null) {
			setTitle(me.getNickname(), me.getStatus(), me.getColor(), me.getSmile());
		}
		super.onResume();
	}
	
	private int getVisavisPositon(int visavisId) {
		if (mUserListAdapter != null) {
			for (int i = 0; i < mUserListAdapter.getCount(); i++) {
				if (((ChatUser)mUserListAdapter.getItem(i)).getId() == visavisId) {
					return i;
				}
			}
		}
		return -1;
	}

	private void setTitle(String nick, String status, String color, Smile smile) {
		mEmptyTitle.setVisibility(View.GONE);
		mTitle.setVisibility(View.VISIBLE);
		mNickView.setText(nick);
		mNickView.setCompoundDrawablePadding(20);
		mNickView.setCompoundDrawables(new UserColorDrawable(color, new Rect(0, 0, 18, 18), 9), null, null, null);
		mStatusView.setCompoundDrawablePadding(20);
		mStatusView.setCompoundDrawables(ChatManager.getInstance().getSmileDrawable(smile, 20), null, null, null);
		mStatusView.setText(status);
	}
	
	private class UserListAdapter extends BaseAdapter {

		private ArrayList<ChatUser> mList = new ArrayList<ChatUser>();
		
		public boolean setUserList(ArrayList<ChatUser> list) {
			boolean isUserCountChanged = mList.size() != list.size();
			mList = list;
			return isUserCountChanged;
		}
		
		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int userId) {
			return -1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View resultView;
			resultView = LayoutInflater.from(getActivity()).inflate(R.layout.item_chat_user, null);
			TypefaceTextView userName = (TypefaceTextView) resultView.findViewById(R.id.user_name);
			TypefaceTextView status = (TypefaceTextView) resultView.findViewById(R.id.status);
			ImageView color = (ImageView) resultView.findViewById(R.id.color);
			ImageView smile = (ImageView) resultView.findViewById(R.id.smile);
			ImageView isBlocked = (ImageView) resultView.findViewById(R.id.is_blocked);
			TypefaceTextView messagesIn = (TypefaceTextView) resultView.findViewById(R.id.messages_in);
			
			ChatUser user = mList.get(position);
			
			if (user.getId() != 0) {
				userName.setText(user.getNickname());
				status.setVisibility(View.VISIBLE);
				color.setVisibility(View.VISIBLE);
				smile.setVisibility(View.VISIBLE);
				if (user.getStatus().length() > 0) {
					status.setText(user.getStatus().toUpperCase(getActivity().getApplicationContext().getResources().getConfiguration().locale));
				} else {
					status.setVisibility(View.GONE);
				}
				color.setImageDrawable(new UserColorDrawable(user.getColor(), new Rect(0, 18, 18, 0), 9));
				smile.setImageDrawable(ChatManager.getInstance().getSmileDrawable(user.getSmile(), 22));
			} else {
				color.setVisibility(View.VISIBLE);
				color.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.chat_all));
				userName.setText(Html.fromHtml("<big>"+getResources().getString(R.string.public_chat_user)+"</big>"));
			}
			
			int unreadCount = ChatManager.getInstance().getUnreadMessageCount(user.getId());
			
			if (unreadCount > 0) {
				messagesIn.setVisibility(View.VISIBLE);
				messagesIn.setText(String.valueOf(unreadCount));
			} else {
				messagesIn.setVisibility(View.INVISIBLE);
			}
			
			if (ChatManager.getInstance().isBlocked(user.getId())) {
				isBlocked.setVisibility(View.VISIBLE);
			} else {
				isBlocked.setVisibility(View.GONE);
			}
			return resultView;
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> adaptder, View view, int position, long arg3) {
		mUserListClick = true;
		view.setSelected(true);
		ChatUser chatUser = (ChatUser) mUserListAdapter.getItem(position);
		ChatManager.getInstance().setVisavisId(chatUser.getId());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onChatManagerEvent(ChatManagerEvent event, Object... args) {
		switch (event) {
			case CHAT_STARTED:
				Log.d("FragUserList", "CHAT_STARTED");
				if (args != null) {
					setTitle((String) args[0], (String) args[1], (String) args[2], (Smile) args[3]);
				}
				break;
			case MESSAGE_RECEIVED:
				Log.d("FragUserList", "MESSAGE_RECEIVED");
				int chatId = (Integer) args[1];
				int visavisId = ChatManager.getInstance().getVisavisId();
				if (chatId != visavisId) {
					updateUserList();
				}
				break;
			case USER_LIST_CHANGED:
				ArrayList<ChatUser> chatUsers = null;
				if (args.length > 0) {
					chatUsers = (ArrayList<ChatUser>) args[0];
				}
				updateUserList(chatUsers);
				break;
			case MY_USER_DATA_CHANGED:
				Log.d("FragUserList", "NICKNAME_CHANGED");
				if (args != null) {
					setTitle((String) args[0], (String) args[1], (String) args[2], (Smile) args[3]);
				}
				break;
			case VISAVIS_CHANGED:
				Log.d("FragUserList", "VISAVIS_CHANGED");
				updateUserList();
				selectUser();
				break;
		}
	}
	
	private void selectUser() {
		if (!mUserListClick) {
			int visavisId = ChatManager.getInstance().getVisavisId();
			int visavisPosition = getVisavisPositon(visavisId);
			mUserList.setItemChecked(visavisPosition, true);
			mUserList.setSelection(visavisPosition);
		} else {
			mUserListClick = false;
		}
	}
	
	private void updateUserList(ArrayList<ChatUser> chatUsers) {
		if (chatUsers != null) {
			boolean isUserCountChanged = mUserListAdapter.setUserList(chatUsers);
			if (isUserCountChanged) {
				selectUser();
			}
		}
		updateUserList();
	}
	
	private void updateUserList() {
		mUserList.post(new Runnable() {
			@Override
			public void run() {
				mUserListAdapter.notifyDataSetChanged();
			}
		});
	}
}
