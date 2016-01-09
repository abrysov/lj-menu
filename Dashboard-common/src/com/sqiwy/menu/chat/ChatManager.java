package com.sqiwy.menu.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseIntArray;

import com.sqiwy.restaurant.api.data.ChatMessage;
import com.sqiwy.restaurant.api.data.ChatUser;
import com.sqiwy.restaurant.api.data.ChatUser.Smile;
import com.sqiwy.restaurant.api.data.ChatUserChangeType;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.MenuApplication.OnChatEventListener;
import com.sqiwy.dashboard.R;
import com.sqiwy.menu.chat.ChatManager.OnChatManagerEventListener.ChatManagerEvent;

/**
 * Created by abrysov
 */

public class ChatManager implements OnChatEventListener {
	
	private ChatManager() {
		this.mClientId = MenuApplication.getClientId();
		MenuApplication.addOnChatEventListener(this);
	}
	
	private static class Keeper {
		private static final ChatManager INSTANCE = new ChatManager(); 
	}
	
	
	public static ChatManager getInstance(){
		return Keeper.INSTANCE;
	}
	
	private String mClientId;
	private ChatUser mMe;
	private ArrayList<ChatUser> mChatUsers;
	private int mVisavisId = 0;
	private ArrayList<Integer> mBlockedUsers = new ArrayList<Integer>();

	/**
	 * Returns ChatUser which is served by current instance of 
	 * ChatManager
	 * @return
	 */
	public ChatUser getMe() {
		return this.mMe;
	}

	/**
	 * Sets current ChatUser. This method should be called when
	 * chat focus should be changed to particular user.
	 * @param chatUser
	 */
	public void setVisavisId(int visavisId) {
		this.mVisavisId = visavisId;
		notifyListeners(ChatManagerEvent.VISAVIS_CHANGED, visavisId);
		resetUnreadMessageCount(visavisId);
	}
	
	/**
	 * Returns current ChatUser that is in the chat 'focus'
	 * @return
	 */
	public int getVisavisId() {
		return this.mVisavisId;
	}
	
	/**
	 * Checks if the supplied ChatUser id is in the chat 'focus'
	 * @param chatUser
	 * @return
	 */
	public boolean isCurrentChatUser(int chatUserId) {
		return this.mVisavisId == chatUserId ? true : false;
	}	
	
	/**
	 * Returns the ChatUser nickname which is served by 
	 * current instance of ChatManager.
	 * @return
	 */
	public String getNickname(){
		if(this.mMe == null) return null;
		return this.mMe.getNickname();
	}
	
	/**
	 * Returns the ChatUser status which is served by 
	 * current instance of ChatManager.
	 * @return
	 */
	public String getStatus() {
		if(this.mMe == null) return null;
		return this.mMe.getStatus();
	}
	
	/**
	 * Returns ChatUser SparseArray, which is available for chat. <br/>
	 * WARNING: When USER_LIST_CHANGED event occurs
	 * ChatManager will supply new instance of SparseArray, 
	 * where all ChatUsers also newly instantiated.
	 * Please do not relay on ChatUser reference value and 
	 * do not keep references to ChatUser from old generation.
	 * @return SparseArray of ChatUsers
	 */
	public ArrayList<ChatUser> getChatUserList(){
		return this.mChatUsers;
	}
	
	public ChatUser getUser(int userId) {
		for (ChatUser user : mChatUsers) {
			if (user.getId() == userId) {
				return user;
			}
		}
		return null;
	}
	
	/**
	 * Starts chat in current opened session. 
	 * This action will cause ChatUser creation with the supplied 
	 * nickname and status. When chat started successfully 
	 * the isStarted flag will be set to true. 
	 * Until chat started the call of other methods is invalid.
	 * @param nick 
	 * @param status
	 */
	public void startChat(final String nick, final String status, final String color, final Smile smile) {
		class StartChatTask extends AsyncTask<Void, Void, ChatUser> {
			@Override
			protected ChatUser doInBackground(Void... params) {
				try {
					mMe = new ChatUser();
					mMe.setNickname(nick);
					mMe.setStatus(status);
					mMe.setColor(color);
					mMe.setSmile(smile);
					MenuApplication.getOperationService().createChatUser(mMe);					
				} catch (Exception e) {
					mMe = null;
					e.printStackTrace();
				}
				return mMe;
			}
			
			@Override
			protected void onPostExecute(ChatUser result) {
				if (result != null && result.getId() > 0) {
					notifyListeners(ChatManagerEvent.CHAT_STARTED, nick, status, color, smile);
				}
				
			}
		}
		new StartChatTask().execute();
	}

	private List<ChatHistory> mChats = new ArrayList<ChatHistory>();
		
	/**
	 * Returns ChatHistory for particular ChatUser if any, 
	 * or creates new one.
	 * @param chatUserId
	 * @return ChatHistory
	 */
	public ChatHistory getChatHistory(int chatUserId) {
		if (mChatUsers == null) {
			return null;
		}
		for (ChatHistory chatHistory : mChats) {
			if (chatHistory.getChatUserId() == chatUserId) {
				return chatHistory;
			}
		}
		ChatHistory chatHistory = new ChatHistory(chatUserId);
		mChats.add(chatHistory);
		return chatHistory;
	}
	
	
	
	/**
	 * Adds message to ChatHistory.
	 * @param sender - the id of sender
	 * @param chatId - the id of receiver
	 * @param message - actual message
	 */
	public void addMessage(int sender, int chatId, String message) {
		if (mChatUsers == null) {
			return;
		}
		/*Get chat history by id*/
		ChatHistory chatHistory = getChatHistory(chatId);
		chatHistory.addMessage(message, sender);
		if (chatId != this.mVisavisId) {
			incrementUnreadMessageCounter(chatId);
		}
	}
	
	/*
	 * Unread messages counter
	 */
	
	/**
	 * Sparse array to of unread message counting
	 */
	private SparseIntArray unreadMessageCounter = new SparseIntArray();
	
	
	
	/**
	 * Increments unread message counter for particular ChatUser
	 * @param chatUserId - ChatUser id
	 */
	private void incrementUnreadMessageCounter(int chatUserId){
		int count = unreadMessageCounter.get(chatUserId);
		unreadMessageCounter.put(chatUserId, ++count);
	}
	
	
	
	/**
	 * Returns unread messages count for particular ChatUser
	 * @param chatUserId - ChatUser id
	 * @return
	 */
	public int getUnreadMessageCount(int chatUserId){
		return unreadMessageCounter.get(chatUserId);
	}
	
	
	
	/**
	 * Resets unread messages counter for particular ChatUser
	 * @param chatUserId - ChatUser id
	 */
	private void resetUnreadMessageCount(int chatUserId) {
		int index = unreadMessageCounter.indexOfKey(chatUserId);
		if (index >= 0) {
			unreadMessageCounter.removeAt(index);
		}
	}
	
	/**
	 * This class represents chat history for 
	 * particular chat user.
	 * Chat messages can be added to history by calling addMessage method.
	 * To retrieve messages call getMessages method.
	 */
	public static class ChatHistory {
		
		private int chatUserId;
		
		private List<FrontEndChatMessage> messages = new ArrayList<FrontEndChatMessage>();
		
		public ChatHistory(int chatUserId){
			this.chatUserId = chatUserId;
		}
		
		public int getChatUserId(){
			return this.chatUserId;
		}
		
		public void addMessage(String message, int senderId){
			FrontEndChatMessage chatMessage = new FrontEndChatMessage(message, senderId);
			this.messages.add(chatMessage);
		}
		
		public List<FrontEndChatMessage> getMessages(){
			return this.messages;
		}
		 
	}
	
	
	
	/**
	 * This class represents the chat message,
	 * and actually map message to the author nickname.
	 * Time-stamp for this message captured locally 
	 * at the time of creation. 
	 */
	public static class FrontEndChatMessage {
		
		private String mMessage;
		private long mTime;
		private int mChatUserId;
		
		public FrontEndChatMessage(String message, int chatUserId){
			this.mMessage = message;
			this.mChatUserId = chatUserId;
			this.mTime = new Date().getTime();
		}
		
		public String getMessage(){
			return this.mMessage;
		}
		
		public int getChatUserId(){
			return mChatUserId;
		}
		
		public boolean isMe(long chatUserId){
			return chatUserId == this.mChatUserId;
		}
		
		private String getDecline(int hours, String str0, String str1, String str2) {
			hours = hours % 100;
			if (hours >= 10 && hours <= 20) {
				return str0;
			}
			hours = hours % 10;
			if (hours == 1) {
				return str1;
			}
			if (hours >= 2 && hours <= 4) {
				return str2;
			}
			if (hours == 0 || hours >= 5) {
				return str0;
			}
			return null;
		}
		
		public String getTimeAgo() {
			long now = (new Date()).getTime();
			long timePassed = now - mTime;
			int minutesPassed = (int)Math.floor(timePassed / (1000 * 60));
			int hoursPassed = (int)Math.floor(minutesPassed / 60);
			minutesPassed -= hoursPassed * 60;
			Context context = MenuApplication.getContext();
			if (hoursPassed == 0 && minutesPassed == 0) {
				return context.getResources().getString(R.string.just_now);
			}
			String result = "";
			if (hoursPassed > 0) {
				result += String.valueOf(hoursPassed) + " " 
						+ getDecline(hoursPassed, context.getResources().getString(R.string.hour0), 
								context.getResources().getString(R.string.hour1), 
								context.getResources().getString(R.string.hour2)) + " ";
			}
			if (minutesPassed > 0) {
				result += String.valueOf(minutesPassed) + " " 
						+ getDecline(minutesPassed, context.getResources().getString(R.string.minute0), 
								context.getResources().getString(R.string.minute1), 
								context.getResources().getString(R.string.minute2)) + " ";
			}
			return result + context.getResources().getString(R.string.ago);
		}
		
	}


	
	/**
	 * Performs ChatUser nickname and status updating.
	 * This operation is asynchronous and do not provides any callback. 
	 * However the success of this operation on the server will launch
	 * {@link OnChatManagerEventListener#onChatManagerEvent(ChatManagerEvent, Object...)} 
	 * callback with USER_LIST_CHAGED event.
	 * @param nickname
	 * @param status
	 */
	
	/**
	 * 
	 * @param nickname
	 * @param status
	 */
	public void editChatUser(final String nickname, final String status, final String color, final Smile smile) {
		class EditChatUserTask extends AsyncTask<Void, Void, Boolean> {
			
			private String oldNick;
			String oldStatus;
			String oldColor;
			Smile oldSmile;
			
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					oldNick = mMe.getNickname();
					oldStatus = mMe.getStatus();
					oldColor = mMe.getColor();
					oldSmile = mMe.getSmile();
					mMe.setNickname(nickname);
					mMe.setStatus(status);
					mMe.setColor(color);
					mMe.setSmile(smile);
					MenuApplication.getOperationService().updateChatUser(mMe);
				} catch (Exception e) {
					mMe.setNickname(oldNick);
					mMe.setStatus(oldStatus);
					mMe.setColor(oldColor);
					mMe.setSmile(oldSmile);
					e.printStackTrace();
					return false;
				} 
				return true;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					ChatManager.this.notifyListeners(ChatManagerEvent.MY_USER_DATA_CHANGED, nickname, status, color, smile);
				}
			}
		}
		new EditChatUserTask().execute();
	}
	
	public void exitChat() {
		mChats.clear();
		mChatUsers = null;
		mMe = null;
		mBlockedUsers.clear();
		mVisavisId = 0;
	}
	
	public boolean isBlocked(int userId) {
		return mBlockedUsers.contains(userId);
	}
	
	public void unblockVisavis() {
		mBlockedUsers.remove((Object)mVisavisId);
		ChatManager.this.notifyListeners(ChatManagerEvent.USER_LIST_CHANGED);
	}
	
	public void blockVisavis() {
		mBlockedUsers.add(mVisavisId);
		ChatManager.this.notifyListeners(ChatManagerEvent.USER_LIST_CHANGED);
	}
	
	/**
	 * Updates ChatUser list.
	 * This operation is asynchronous, 
	 * after this operation {@link OnChatManagerEventListener} with 
	 * event USER_LIST_CHANGED will be called.
	 */
	// TODO: The ChatHistory should be modified according to 
	// presented users
	protected void updateChatUserList(final ChatUserChangeType changeType, final ChatUser chatUser) {
		if (changeType != null) {
			Log.d("UpdateUserListTask", "changeType="+changeType.name());
		} else {
			Log.d("UpdateUserListTask", "changeType=null");
		}
		
		class UpdateUserListTask extends AsyncTask<Void, Void, ArrayList<ChatUser>> {
			@Override
			protected ArrayList<ChatUser> doInBackground(Void... params) {
				try {
					if (mChatUsers == null || (changeType == null && chatUser == null)) {
						mChatUsers = new ArrayList<ChatUser>();
						List<ChatUser> rawList = MenuApplication.getOperationService().getChatUsers();
						
						addPublicChatUser(rawList);
						for (int i = 0; i < rawList.size(); i++) {
							ChatUser chatUser = rawList.get(i);
							Log.d("rawList", chatUser.getNickname() + ":" + chatUser.getId());
							if (!chatUser.getClientId().equals(mClientId)) {
								mChatUsers.add(chatUser);
							}
						}
					} else {
						if (chatUser.getId() != mMe.getId()) {
							switch (changeType) {
								case CREATED:
									Log.d("updateChatUserList", "CREATED");
									mChatUsers.add(chatUser);
									break;
								case UPDATED:
									Log.d("updateChatUserList", "UPDATED");
									for (ChatUser user : mChatUsers) {
										if (user.getId() == chatUser.getId()) {
											mChatUsers.remove(user);
											break;
										}
									}
									mChatUsers.add(chatUser);
									break;
								case DELETED:
									Log.d("updateChatUserList", "DELETED");
									for (ChatUser user : mChatUsers) {
										if (user.getId() == chatUser.getId()) {
											mChatUsers.remove(user);
											break;
										}
									}
									break;
							}
						}
					}
					if (mVisavisId != 0 && getUser(mVisavisId) == null) {
						mVisavisId = 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				mChatUsers.remove(mMe);
				return mChatUsers;
			}
			
			@Override
			protected void onPostExecute(ArrayList<ChatUser> result) {
				Log.d("UpdateUserListTask", "onPostExecute result.size="+result.size());
				notifyListeners(ChatManagerEvent.USER_LIST_CHANGED, result);
			}
		}
		new UpdateUserListTask().execute();
	}
	
	
	
	/**
	 * Adds synthetic ChatUser with the id = 0,
	 * that represents public chat.
	 * @param chatUsers - ChatUser list to which 'public chat user'
	 * should be assigned
	 */
	private void addPublicChatUser(List<ChatUser> chatUsers) {
		ChatUser chatUser = new ChatUser();
		chatUser.setId(0);
		String nick = MenuApplication.getContext()
				.getResources().getString(R.string.public_chat_user);
		chatUser.setNickname(nick);
		chatUser.setClientId("");
		chatUsers.add(0, chatUser);
	}
	
	
	
	/*
	 * Back-end library callback
	 */
	@Override
	public void chatEvent(ChatEvent event, Object... args) {
		Log.d("chatEvent", "occures");
		switch (event) {
			case CHAT_USER_CHANGED:
				Log.d("chatEvent", "CHAT_USER_CHANGED");
				ChatUserChangeType changeType = (ChatUserChangeType) args[0];
				ChatUser chatUser = (ChatUser) args[1];
				updateChatUserList(changeType, chatUser);
				break;
			case CHAT_MESSAGE_RECEIVED:
				Log.d("chatEvent", "CHAT_MESSAGE_RECEIVED");
				ChatMessage msg = (ChatMessage) args[0];				
				int sender = msg.getSenderId();
				if (sender != mMe.getId()) {
					for (int userId : mBlockedUsers) {
						if (userId == sender) {
							return;
						}
					}
					int receiver = msg.getTargetId();
					int chatId = receiver == 0 ? 0 : sender == mMe.getId() ? receiver : sender; 
					String message = msg.getText();				
					addMessage(sender, chatId, message);
					notifyListeners(ChatManagerEvent.MESSAGE_RECEIVED, sender, chatId);
				}
				break;
		}
	}
	
	
	
	/*
	 * ChatManager listeners handling
	 */
	
	/**
	 * ChatManager accepts listeners for a local and external
	 * events related to the chat through this interface. 
	 * There is only one method {@link OnChatManagerEventListener#onChatManagerEvent(ChatManagerEvent, Object...)},
	 * which is supplied with the {@link ChatManagerEvent} that describes event, 
	 * and Object varargs.  
	 * 
	 * There is the {@link ChatManagerEvent#MESSAGE_RECEIVED}, 
	 * {@link ChatManagerEvent#USER_LIST_CHANGED} and {@link ChatManagerEvent#VISAVIS_CHANGED}
	 * events that is supplied with additional parameters.  
	 * 
	 * The {@link ChatManagerEvent#MESSAGE_RECEIVED} event will always called from Daemon thread, 
	 * please do appropriate action to interact with UI components.
	 * 
	 */
	public interface OnChatManagerEventListener{
		enum ChatManagerEvent {
			/**
			 * 
			 */
			CHAT_STARTED,
			/**
			 * Supplied with additional argument -
			 * List of ChatUser 
			 */
			USER_LIST_CHANGED,
			/**
			 * Supplied with additional argument -
			 * chatUserId, the message author id.
			 * WARNING: this event will always called from Daemon thread, 
			 * please do appropriate action to interact with UI components.
			 */
			MESSAGE_RECEIVED, 
			MY_USER_DATA_CHANGED,
			/**
			 * Supplied with additional argument -
			 * ChatUser, the user which is in chat 'focus'
			 */
			VISAVIS_CHANGED;
		}
		void onChatManagerEvent(ChatManagerEvent event, Object...args);
	}
	
	
	
	/**
	 * List of ChatManager event listeners
	 */
	private List<OnChatManagerEventListener> mChatEventListeners;
	

	
	/**
	 * Adds {@link OnChatManagerEventListener} to listeners.
	 * WARNING: ChatManager it self does not 
	 * @param listener
	 */
	public void addOnChatManagerEventListener(OnChatManagerEventListener listener){
		if(this.mChatEventListeners == null) this.mChatEventListeners = 
				new ArrayList<ChatManager.OnChatManagerEventListener>();
		this.mChatEventListeners.add(listener);
	}	
	
	/**
	 * Notify ChatMessage listeners about event.
	 * @param event
	 * @param args
	 */
	private void notifyListeners(ChatManagerEvent event, Object...args) {
		if (this.mChatEventListeners == null) {
			return;
		}
		for (OnChatManagerEventListener listener : mChatEventListeners) {
			listener.onChatManagerEvent(event, args);
		}
	}

	public Drawable getSmileDrawable(Smile smile, int size) {
		Drawable result;
		Rect rect = new Rect(0, 0, size, size);
		Resources resources = MenuApplication.getContext().getResources();
		if (smile.equals(Smile.ANGRY)) {
			result = resources.getDrawable(R.drawable.g_angry);
			result.setBounds(rect);
			return result;
		}
		if (smile.equals(Smile.CONFUSE)) {
			result = resources.getDrawable(R.drawable.g_confuse);
			result.setBounds(rect);
			return result;
		}
		if (smile.equals(Smile.COOL)) {
			result = resources.getDrawable(R.drawable.g_cool);
			result.setBounds(rect);
			return result;
		}
		if (smile.equals(Smile.EVIL)) {
			result = resources.getDrawable(R.drawable.g_evil);
			result.setBounds(rect);
			return result;
		}
		if (smile.equals(Smile.HAPPY)) {
			result = resources.getDrawable(R.drawable.g_happy);
			result.setBounds(rect);
			return result;
		}
		if (smile.equals(Smile.HOLLYWOOD)) {
			result = resources.getDrawable(R.drawable.g_hollywood);
			result.setBounds(rect);
			return result;
		}
		if (smile.equals(Smile.NETUTRAL)) {
			result = resources.getDrawable(R.drawable.g_netutral);
			result.setBounds(rect);
			return result;
		}
		if (smile.equals(Smile.SAD)) {
			result = resources.getDrawable(R.drawable.g_sad);
			result.setBounds(rect);
			return result;
		}
		if (smile.equals(Smile.SURPRISE)) {
			result = resources.getDrawable(R.drawable.g_surprise);
			result.setBounds(rect);
			return result;
		}
		if (smile.equals(Smile.TOUNGUE)) {
			result = resources.getDrawable(R.drawable.g_toungue);
			result.setBounds(rect);
			return result;
		}
		if (smile.equals(Smile.WONDER)) {
			result = resources.getDrawable(R.drawable.g_wonder);
			result.setBounds(rect);
			return result;
		}
		if (smile.equals(Smile.WRINKLE)) {
			result = resources.getDrawable(R.drawable.g_wrinkle);
			result.setBounds(rect);
			return result;
		}
		result = resources.getDrawable(R.drawable.g_smile);
		result.setBounds(rect);
		return result;
	}

	public Smile getSmileFromDrawableId(int resourceId) {
		if (resourceId == R.drawable.g_angry) {
			return Smile.ANGRY;
		}
		if (resourceId == R.drawable.g_confuse) {
			return Smile.CONFUSE;
		}
		if (resourceId == R.drawable.g_cool) {
			return Smile.COOL;
		}
		if (resourceId == R.drawable.g_evil) {
			return Smile.EVIL;
		}
		if (resourceId == R.drawable.g_happy) {
			return Smile.HAPPY;
		}
		if (resourceId == R.drawable.g_hollywood) {
			return Smile.HOLLYWOOD;
		}
		if (resourceId == R.drawable.g_netutral) {
			return Smile.NETUTRAL;
		}
		if (resourceId == R.drawable.g_sad) {
			return Smile.SAD;
		}
		if (resourceId == R.drawable.g_surprise) {
			return Smile.SURPRISE;
		}
		if (resourceId == R.drawable.g_toungue) {
			return Smile.TOUNGUE;
		}
		if (resourceId == R.drawable.g_wonder) {
			return Smile.WONDER;
		}
		if (resourceId == R.drawable.g_wrinkle) {
			return Smile.WRINKLE;
		}
		return Smile.SMILE;
	}



	public void clearOnChatManagerEventListeners() {
		if (mChatEventListeners == null) {
			return;
		}
		mChatEventListeners.clear();
		mChatEventListeners = null;
	}
	
	
}
