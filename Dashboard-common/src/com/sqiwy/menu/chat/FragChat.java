package com.sqiwy.menu.chat;

import java.util.Timer;
import java.util.TimerTask;

import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.sqiwy.restaurant.api.data.ChatMessage;
import com.sqiwy.restaurant.api.data.ChatUser;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.dashboard.R;
import com.sqiwy.menu.chat.ChatManager.ChatHistory;
import com.sqiwy.menu.chat.ChatManager.OnChatManagerEventListener;
import com.sqiwy.menu.chat.FragMessageEditor.OnMessageEditorEventListener;
import com.sqiwy.menu.view.TypefaceTextView;

/**
 * Created by abrysov
 */

public class FragChat extends Fragment implements OnClickListener,
												  OnItemClickListener,
												  OnChatManagerEventListener,
												  OnMessageEditorEventListener{

	private TypefaceTextView mWriteMessageButton;
	private TypefaceTextView mBlockUserButton;
	private ListView mChatList;
	private TypefaceTextView mChatUserName;
	private TypefaceTextView mChatUserStatus;
	private TypefaceTextView mPublicTitle;
	private GridLayout mTitle;
	private ChatAdapter mChatAdapter;
	private String mBlockString;
	private String mUnblockString;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.frag_chat, null);
		
		mBlockUserButton = (TypefaceTextView) view.findViewById(R.id.btn_block_user);
		mBlockUserButton.setOnClickListener(this);
		mBlockString = getResources().getString(R.string.btn_ban_chat);
		mUnblockString = getResources().getString(R.string.btn_unban_chat);
		
		
		mWriteMessageButton = (TypefaceTextView) view.findViewById(R.id.btn_write_message);
		mWriteMessageButton.setOnClickListener(this);
		
		mTitle = (GridLayout) view.findViewById(R.id.title);
		mPublicTitle = (TypefaceTextView) view.findViewById(R.id.public_title);
		
		mChatList = (ListView) view.findViewById(R.id.chat_list);
		mChatList.setOnItemClickListener(this);
		mChatAdapter = new ChatAdapter();
		mChatList.setAdapter(mChatAdapter);
		
		// Timer to update chat message time labels
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				mChatList.post(new Runnable() {
					@Override
					public void run() {
						 mChatAdapter.notifyDataSetChanged();
					}
				});
			}
		}, 10000, 10000);
		
		mChatUserName = (TypefaceTextView) view.findViewById(R.id.chat_user_name);
		mChatUserStatus = (TypefaceTextView) view.findViewById(R.id.chat_user_status);
		
		return view;
	}

	private class ChatAdapter extends BaseAdapter {
		
		private ChatHistory mHistory;
		
		public void setChatHistory(ChatHistory history) {
			mHistory = history;
		}
		
		@Override
		public int getCount() {
			return mHistory == null ? 0 : mHistory.getMessages().size();
		}

		@Override
		public Object getItem(int position) {
			return mHistory.getMessages().get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			switch (getItemViewType(position)) {
			
			case 1:
				convertView = LayoutInflater.from(getActivity())
				.inflate(R.layout.item_message_left, null);
				break;
				
			case 0:
				convertView = LayoutInflater.from(getActivity())
				.inflate(R.layout.item_message_right, null);
				break;
			}
			
			TextView nickTextView = (TextView) convertView.findViewById(R.id.nick);
			TextView timeTextView = (TextView) convertView.findViewById(R.id.time);
			TextView messageTextView = (TextView) convertView.findViewById(R.id.message);
			
			switch (getItemViewType(position)) {
				case 0:
					int chatUserId = mHistory.getMessages().get(position).getChatUserId(); 
					ChatUser user = ChatManager.getInstance().getUser(chatUserId);
					if (user != null) {
						nickTextView.setText(user.getNickname());
					}
					break;
				case 1:
					nickTextView.setText(R.string.me);
					break;
			}
			
			timeTextView.setText(mHistory.getMessages().get(position).getTimeAgo());
			messageTextView.setText(mHistory.getMessages().get(position).getMessage());
			
			return convertView;
		}
		
		@Override
		public int getViewTypeCount() {
			return 2;
		}
		
		@Override
		public int getItemViewType(int position) {
			ChatUser me = ChatManager.getInstance().getMe();
			if (me == null || mHistory == null) {
				return 0;
			}
			if(mHistory.getMessages().get(position).isMe(me.getId())){
				return 1;
			} else {
				return 0;
			}
		}	
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_block_user:
				int visavisId = ChatManager.getInstance().getVisavisId();
				if (ChatManager.getInstance().isBlocked(visavisId)) {
					ChatManager.getInstance().unblockVisavis();
				} else {
					ChatManager.getInstance().blockVisavis();
				}
				break;
			case R.id.btn_write_message:
				DialogFragment frag1 = new FragMessageEditor(this);
                frag1.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Translucent);
				frag1.show(getFragmentManager(), "frag1");
				mWriteMessageButton.setVisibility(View.INVISIBLE);
				break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}
	
	@Override
	public void messageSendRequest(final String message) {
		
		class SendmessageTask extends AsyncTask<Void, Void, Boolean>{
			
			boolean isSuccess = true;
			
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					int chatUserId = ChatManager.getInstance().getVisavisId();
					int senderId = ChatManager.getInstance().getMe().getId();
                    ChatMessage cm = new ChatMessage();
                    cm.setSenderId(senderId);
                    cm.setTargetId(chatUserId);
                    cm.setText(message);
                    MenuApplication.getOperationService().sendChatMessage(cm);
					ChatManager.getInstance().addMessage(senderId, chatUserId, message);
				} catch (Exception e) {
					isSuccess = false;
					e.printStackTrace();
				}
				return isSuccess;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if (result.booleanValue() && FragChat.this.isVisible()) {
                    Toast.makeText(MenuApplication.getContext(), getResources().getString(R.string.message_sended), Toast.LENGTH_SHORT).show();
                    updateChat(ChatManager.getInstance().getVisavisId());
                    mWriteMessageButton.setVisibility(View.VISIBLE);
				}
			}
		}
		
		SendmessageTask task = new SendmessageTask();
		task.execute();
	}
	
	private void setTitle(ChatUser chatUser) {
		if (chatUser.getId() != 0) {
			mChatUserName.setText(chatUser.getNickname());
			mChatUserName.setCompoundDrawablePadding(10);
			mChatUserName.setCompoundDrawables(new UserColorDrawable(chatUser.getColor(), new Rect(0, 0, 18, 18), 9), null, null, null);
			mChatUserStatus.setText(chatUser.getStatus());
			mChatUserStatus.setCompoundDrawablePadding(10);
			mChatUserStatus.setCompoundDrawables(ChatManager.getInstance().getSmileDrawable(chatUser.getSmile(), 20), null, null, null);
			mPublicTitle.setVisibility(View.GONE);
			mTitle.setVisibility(View.VISIBLE);
			if (ChatManager.getInstance().isBlocked(chatUser.getId())) {
				mBlockUserButton.setText(mUnblockString);
			} else {
				mBlockUserButton.setText(mBlockString);
			}
		} else {
			mTitle.setVisibility(View.GONE);
			mPublicTitle.setVisibility(View.VISIBLE);
		}
	}
	
	private void updateChat(final int visavisId) {
		Log.d("updateChat", "id:"+ visavisId);
		mChatList.post(new Runnable() {
			@Override
			public void run() {
				mChatAdapter.setChatHistory(ChatManager.getInstance().getChatHistory(visavisId));
				mChatAdapter.notifyDataSetChanged();
				mChatList.setSelection(mChatAdapter.getCount() - 1);
			}
		});
	}

	@Override
	public void onChatManagerEvent(ChatManagerEvent event, final Object... args) {
		int visavisId = ChatManager.getInstance().getVisavisId();
		ChatUser visasis = ChatManager.getInstance().getUser(visavisId);
		switch (event) {
			case CHAT_STARTED:
				Log.d("FragChat", "CHAT_STARTED");
				setTitle(visasis);
				updateChat(visavisId);
				break;
			case MESSAGE_RECEIVED:
				Log.d("FragChat", "MESSAGE_RECEIVED");
				int chatId = (Integer) args[1];
				if (chatId == visavisId) {
					updateChat(visavisId);
				}
				break;
			case USER_LIST_CHANGED:
				setTitle(visasis);
				Log.d("FragChat", "USER_LIST_CHANGED");
				updateChat(visavisId);
				break;
			case VISAVIS_CHANGED:
				Log.d("FragChat", "VISAVIS_CHANGED");
				setTitle(visasis);
				updateChat(visavisId);
				break;
			default:
				break;
		}
	}
}
