package com.sqiwy.menu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.fosslabs.android.utils.JSImageWorker;
import com.sqiwy.dashboard.R;
import com.sqiwy.restaurant.api.*;
import com.sqiwy.restaurant.api.ClientConfig.SupportedLanguage;
import com.sqiwy.restaurant.api.data.*;
import com.sqiwy.dashboard.DBMediaActivity;
import com.sqiwy.dashboard.logger.LogMessage;
import com.sqiwy.dashboard.logger.LoggerService;
import com.sqiwy.menu.MenuApplication.OnChatEventListener.ChatEvent;
import com.sqiwy.menu.MenuApplication.OnMenuEventListener.MenuEvent;
import com.sqiwy.menu.MenuApplication.OnOrderEventListener.OrderEvent;
import com.sqiwy.menu.MenuApplication.OnStaffEventListener.StaffEvent;
import com.sqiwy.menu.MenuApplication.OnTableSessionEventListener.SessionEvent;
import com.sqiwy.menu.provider.OrderManager;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.resource.ResourcesManager.Category;
import com.sqiwy.menu.service.UpdateService;
import com.sqiwy.menu.util.SystemControllerHelper;
import com.sqiwy.menu.util.PreferencesUtils;
import com.sqiwy.menu.util.UIUtils;

/**
 * Created by abrysov
 */

public class MenuApplication extends Application {

	private static final String TAG = MenuApplication.class.getName();

	public interface OnOrderEventListener{
		/**
		 * Order events
		 */
		enum OrderEvent {
			ORDER_ADDED,
			ORDER_LIST_CHANGED;
		}
		void orderEvent(OrderEvent event);
	}
	
	

	public interface OnCompanyInfoListener{
		void onCompanyInfoChanged();
	}

	public interface OnMapChangedListener{
		void onMapChanged();
	}
	
    public static volatile boolean mIsAppStarted = false;
    public static volatile boolean mIsCheckResources = true;

	public int orientation;
	private static final List<String> mLaunchedApps = new ArrayList<String>();

	private static Context mContext;
	/**
	 * Internal flag. True if 'table session' already opened, false if not opened yet or closed.
	 */
	private static boolean mIsTableSessionOpen=false;
	private static boolean mShowToastAlongWithLogEvent=false;
	
	public static Context getContext() {
		return mContext;
	}

	
    public static void trackLaunchedApp(String packageName) {
        if (!mLaunchedApps.contains(packageName)) {
            mLaunchedApps.add(packageName);
        }
    }
    
    
	private static OperationService mOperationService;
	private static BackendClient mClient;
	private static String mClientId;
	
	public static String getClientId() {
		return mClientId;
	}
	
	private Bitmap mDashboardBackground;

	/** 
	 * Lock for managing access to service.
	 * Rule: don't allow getting service until it's initialization is in progress. 
	 */
	private static final ReentrantReadWriteLock SERVICE_LOCK = new ReentrantReadWriteLock();
	private static final Lock SERVICE_LOCK_GET = SERVICE_LOCK.readLock();
	private static final Lock SERVICE_LOCK_SET = SERVICE_LOCK.writeLock();
	
	
    @Override
	public void onCreate() {
		super.onCreate();

		mIsTableSessionOpen=false;
		mContext = getApplicationContext();
		
		// Make Chrome to be launched in desktop mode
		SystemControllerHelper.setChromeToDesktopMode();
		
		initOperationService();
		
        addOnTableSessionEventListener(new TableSessionEventListener());
        
        // send logs
        LoggerService.sendQueuedMessages(this);
        
        UIUtils.setLandscapeOrientation(mContext, false);

	}
    
    private static void initializeTablePassword(String password) {
    	if (! TextUtils.isEmpty(password)) { // Password from server is consistent. Store them in preferences
            PreferencesUtils.setTablePassword(mContext, password);
    	}
    }
    
    private static void initializeEnvironment(EnvironmentType envType) {
    	mShowToastAlongWithLogEvent = EnvironmentType.QA.equals(envType);
    }
    
    public static boolean isQAEnvironment() {
    	return mShowToastAlongWithLogEvent;
    }
    
    public interface OperationServiceInitCallback {
    	public void OnOperationServiceInit();
    }

    public static void setLanguage(SupportedLanguage language) {
        SERVICE_LOCK_SET.lock();
        try {
            if (mClient != null) {
                mClient.setLanguage(language);
            }
        } finally {
            SERVICE_LOCK_SET.unlock();
        }
    }


    private static class InitializeOperationalServiceTask extends AsyncTask<OperationServiceInitCallback, Void, Void> {
    	private OperationServiceInitCallback mCbk;
    	
		protected Void doInBackground(OperationServiceInitCallback... params) {
			SystemParameters sysParms=null;
			
			mCbk=params[0];
			SERVICE_LOCK_SET.lock();
			try {
				ClientConfig config = getBackendClientConfig();
		 		if (null != config) {
		 			mClient = getBackendClient(config);
		 			mOperationService = mClient == null ? null : mClient.getOperationService();
		 			if (mOperationService!=null) {
		 				sysParms=mOperationService.getSystemParameters();
		 			}
		 		}
			} catch (Throwable e) {
				e.printStackTrace();
                Log.e(TAG, "Cannot load System Parameters from Backend Server");
            }
			finally {
				SERVICE_LOCK_SET.unlock();
                if (sysParms != null) {
                    initializeTablePassword(sysParms.getTablePassword());
                    initializeEnvironment(sysParms.getEnvironment());
                } else {
                    Log.e(TAG, "System Parameters were not defined on the Backend Server");
                }

			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mCbk!=null) {
				mCbk.OnOperationServiceInit();
			}
		}
    }
    
    public static void initOperationServiceWithCallback(OperationServiceInitCallback cbk) {
    	new InitializeOperationalServiceTask().execute(cbk);
    }
	
    public static void initOperationService() {
    	// Some of operations access network and therefore should be performed on a non-UI thread
    	new InitializeOperationalServiceTask().execute((OperationServiceInitCallback)null);
	}
	
	public static OperationService getOperationService() {
		
		SERVICE_LOCK_GET.lock();
		try {
			
			return mOperationService;
		
		} finally {
			SERVICE_LOCK_GET.unlock();
		}
 	}
	
	 /**
     * Have to store lock background here and make it load
	 * only once in order to make lock screen work smooth
	 * (except the first time it's opened)
	 */
	private Bitmap mLockBackground;
	
	public Bitmap getLockBackground() {
		if (null == mLockBackground) {
			
			File back = ResourcesManager.getResourcePath("bg_window_lock.png", Category.DASHBOARD);
			mLockBackground = JSImageWorker.loadFromFile(back.getAbsolutePath());
		}
		
		return mLockBackground;
	}
	
	public Bitmap getDashboardBackground(){
		if(null == mDashboardBackground){
			
			File back = ResourcesManager.getResourcePath("bg_window_dashboard.png", Category.DASHBOARD);
			mDashboardBackground = JSImageWorker.loadFromFile(back.getAbsolutePath());
		}
		
		return mDashboardBackground;
	}
	
	private static BackendClient getBackendClient(ClientConfig config){
		if(config != null){
			try {
                final BackendClient client = new BackendClient();
                client.setConfig(config);
                client.setEventService(new ServiceEventListener());
				client.start();
                return client;
			} catch (IOException e) {
				Log.e(TAG, "Cannot start backend client.", e);
                return null;

			}
		} else {
			return null;
		}
	}
	


    
	public static ClientConfig getBackendClientConfig() throws Throwable {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
		
		mClientId = sp.getString(PreferencesUtils.CLIENT_ID, "");
		//TODO wanna hardcoded id? 
		//mClientId = "202";
		String pass = sp.getString(PreferencesUtils.PASSWORD, "");
		String apiUrl = sp.getString(PreferencesUtils.API_URL, "");
        String webUrl = sp.getString(PreferencesUtils.WEB_URL, "");
		int maxPacket = Integer.valueOf(sp.getString(PreferencesUtils.MAX_PACKET_SIZE, "0"));

		// Check if all data provided in preferences
		if (TextUtils.isEmpty(mClientId) || TextUtils.isEmpty(pass) ||
                TextUtils.isEmpty(apiUrl) || TextUtils.isEmpty(webUrl) || maxPacket == 0) {
			return null;
		}
		
		ClientConfig config = new ClientConfig();
		
		config.setClientId(mClientId);
		config.setPassword(pass);
		config.setApiUrl(apiUrl);
		config.setWebUrl(webUrl);
		config.setMaxPacketSize(maxPacket);

        // TODO add new language for interface in menu you need add new string file with correct "lang_tag" value

        if (mContext.getString(R.string.lang_tag).equals(SupportedLanguage.EN.getCode())) {
            config.setDefaultLanguage(SupportedLanguage.EN);
        }else if (mContext.getString(R.string.lang_tag).equals(SupportedLanguage.RU.getCode())) {
            config.setDefaultLanguage(SupportedLanguage.RU);
        }else if (mContext.getString(R.string.lang_tag).equals(SupportedLanguage.ZH.getCode())) {
            config.setDefaultLanguage(SupportedLanguage.ZH);
        }

		return config;
	}

	/*
	 * Back-end events
	 */
	
	public static class ServiceEventListener implements EventService {

		/* CHAT EVENTS */
		
		@Override
		public void chatMessageReceived(ChatMessage arg0) {
			notifyChatListeners(ChatEvent.CHAT_MESSAGE_RECEIVED, arg0);
		}

		@Override
		public void chatUserChanged(ChatUserChangeType arg0, ChatUser arg1) {
			Log.d("chatUserChanged","changeType="+arg0.name());
			notifyChatListeners(ChatEvent.CHAT_USER_CHANGED, arg0, arg1);
		}

        @Override
        public SystemParameters getInfo() {
            // :FIXME SystemParameters should be loaded only from server-side, plz check this code
            try {
                return MenuApplication.getOperationService().getSystemParameters();
            } catch (BackendException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /* ORDER EVENTS */
        @Override
        public void orderChanged(Order order) {
            // :FIXME ???
        }

		/* STAFF CALL EVENT */
		
		@Override
		public void staffCallConfirmed() {
			notifyOnStaffEventListeners(StaffEvent.STAFF_CALL_CONFIRM);
		}
		
		/* TABLE SESSION EVENTS */

		@Override
		public void tableSessionClosed() {
			notifyTableSessionListeners(SessionEvent.SESSION_CLOSED);
		}

		@Override
		public void tableSessionOpened() {
			notifyTableSessionListeners(SessionEvent.SESSION_OPENED);
		}




		/* MENU EVENT */
		@Override
		public void menuChanged() {
			notifyMenuListeners(MenuEvent.MENU_CHANGED);
		}
		

        @Override
        public void restartRequested(RestartMode restartMode) {
            
        	Log.i(TAG, String.format("Restart requested [%s]", restartMode.name()));
        	LoggerService.sendMessage(getContext(),
        		new LogMessage(System.currentTimeMillis(),LogMessage.Stage.RESTART_SESSION,LogMessage.Status.SUCCESS,
        			"Restart session request obtained ("+restartMode.toString()+')'));

            MenuApplication.mIsAppStarted = false;

            switch (restartMode) {
            case HARD:
            	
            	SystemControllerHelper.reboot();
            	
            	break;
            case SOFT:
            	// Re-start app (reinstall if needed)
                UpdateService.start();
            	break;
            }
        }
    }
	
	/*
	  Interfaces that splits the back-end events by logical groups  
	 */
	/* CHAT EVENT LISTENERS */
	
	/**
	 * Adds OnChatEvent listener.
	 * If used in activity it is recommended to add listener in onResume() callback.
	 * Do not forget to call removeOnChatEvent() in onPause().
	 * @param listener
	 */
	public static void addOnChatEventListener(OnChatEventListener listener){
		if(mChatListeners == null)
			mChatListeners = new ArrayList<OnChatEventListener>();
		mChatListeners.add(listener);
	}

	/**
	 * Removes OnChatEvent listener
	 * @param listener
	 */
	public static void removeOnChatEventListener(OnChatEventListener listener){
		if(mChatListeners == null) return;
		mChatListeners.remove(listener);
		if(mChatListeners.isEmpty()) mChatListeners = null;
	}

	public static void addOnTableSessionEventListener(OnTableSessionEventListener listener){
		if(mSessionListeners == null){
			mSessionListeners = new ArrayList<OnTableSessionEventListener>();
		}
		mSessionListeners.add(listener);
	}
	
	
	public interface OnTableSessionEventListener{
		enum SessionEvent{
			SESSION_OPENED,
			SESSION_CLOSED
		}
		void onTableSessionEvent(SessionEvent event);
	}

	/**
	 * TODO: has to expose it so it can be called manually, as backend doesn't work properly now.
	 * Make private and remove manual usage after backend is completed.
	 * @param event
	 */
	public static void notifyTableSessionListeners(SessionEvent event){
		if(mSessionListeners == null) return;
		for(OnTableSessionEventListener listener : mSessionListeners){
			listener.onTableSessionEvent(event);
		}
		if (SessionEvent.SESSION_CLOSED.equals(event)) {
			LoggerService.sendMessage(getContext(),
				new LogMessage(System.currentTimeMillis(),LogMessage.Stage.CLOSE_SESSION,LogMessage.Status.SUCCESS,"Table session closed"));
		} else if (SessionEvent.SESSION_OPENED.equals(event)) {
			LoggerService.sendMessage(getContext(),
				new LogMessage(System.currentTimeMillis(),LogMessage.Stage.OPEN_SESSION,LogMessage.Status.SUCCESS,"Table session opened"));
		}
	}

	private static void notifyOnResourceChangeListeners(){
		if(mResourceChangeListeners == null) return;
		for(OnResourcesChangedListener listener : mResourceChangeListeners){
			listener.onResourcesChanged();
		}
	}

    public static class TableSessionEventListener implements OnTableSessionEventListener {
        @Override
        public void onTableSessionEvent(SessionEvent event) {
            Log.v(TAG, "Session status changed to " + event.name());

            if (event == SessionEvent.SESSION_CLOSED) {
            	mIsTableSessionOpen=false;
                OrderManager.deleteSessionOrders();
                if (!mLaunchedApps.isEmpty()) {
                    SystemControllerHelper.clearAppData(mLaunchedApps);
                    mLaunchedApps.clear();
                }

                Intent videoScreen = new Intent(getContext(), DBMediaActivity.class);
                videoScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(videoScreen);
            } else if (event == SessionEvent.SESSION_OPENED) {
            	mIsTableSessionOpen=true;
            }
        }
    }
    
    /**
     * @return true if 'table session' already open; false otherwise
     */
    public static boolean isTableSessionAlreadyOpen() {
    	return mIsTableSessionOpen;
    }
    
    public static void resetTableSessionOpenFlag() {
    	mIsTableSessionOpen=false;
    }
    
    public int mOrientation;
	
	private boolean mIsConfigPresented = false;
	
	public boolean isConfigPresented(){
		return mIsConfigPresented;
	}
	
	
	
	/*
	 * Back-end events
	 */
	
	
	private static List<OnChatEventListener> mChatListeners;
	
	/**
	 * Chat events 
	 *
	 */
	public interface OnChatEventListener{
		/**
		 * Chat events.
		 */
		enum ChatEvent{
			CHAT_MESSAGE_RECEIVED,
			/**
			 * User list is changed.
			 * No additional arguments.
			 */
			CHAT_USER_CHANGED
		}
		/**
		 * This method will be called if chat event occurs.  
		 * @param event
		 * @param args
		 * @see ChatEvent
		 */
		void chatEvent(ChatEvent event, Object...args);
	}
	
	
	private static void notifyChatListeners(ChatEvent event, Object...args) {
		if (mChatListeners == null) {
			return;
		}
		for (OnChatEventListener listener : mChatListeners) {
			listener.chatEvent(event, args);
		}
	}
	
	
	/* ORDER EVENT LISTENERS */
	
	private static List<OnOrderEventListener> mOrderListeners; 

	
	public static void addOnOrderEventListener(OnOrderEventListener listener){
		if(mOrderListeners == null) mOrderListeners = new ArrayList<OnOrderEventListener>();
		mOrderListeners.add(listener);
	}
	
	public static void removeOnOrderEventListener(OnOrderEventListener listener){
		if(mOrderListeners == null) return;
		mOrderListeners.remove(listener);
		if(mOrderListeners.isEmpty()) mOrderListeners = null;
	}
	
	private static void notifyOrderListeners(OrderEvent event){
		if(mOrderListeners == null) return;
		for(OnOrderEventListener listener : mOrderListeners){
			listener.orderEvent(event);
		}
	}
	
	
	
	/* MENU EVENT LISTENERS */
	
	private static List<OnMenuEventListener> mMenuListeners;

	
	public interface OnMenuEventListener{
		enum MenuEvent{
			MENU_CHANGED
		}
		void onMenuEvent(MenuEvent event);
	}
	
	public void addOnMenuEventListener(OnMenuEventListener listener){
		if(mMenuListeners == null) mMenuListeners = new ArrayList<OnMenuEventListener>();
		mMenuListeners.add(listener);
	}
	
	public void removeOnMenuEventListener(OnMenuEventListener listener){
		if(mMenuListeners == null) return;
		mMenuListeners.remove(listener);
		if(mMenuListeners.isEmpty()) mMenuListeners = null;
	}
	
	private static void notifyMenuListeners(MenuEvent event){
		if(mMenuListeners == null) return;
		for(OnMenuEventListener listener : mMenuListeners){
			listener.onMenuEvent(event);
		}
	}

	/* TABLE SESSION EVENTS */
	
	private static List<OnTableSessionEventListener> mSessionListeners;
	
	public void removeOnTableSessionEventListener(OnTableSessionEventListener listener){
		if(mSessionListeners == null) return;
		mSessionListeners.remove(listener);
		if(mSessionListeners.isEmpty()) mSessionListeners = null;
	}	
	
	
	/* STAFF EVENTS */
	public interface OnStaffEventListener{
		enum StaffEvent{
			STAFF_CALL_CONFIRM,
			WAITER_LINKED_TO_TABLE
		}
		void onStaffEvent(StaffEvent event, Object...args);
	}
	
	private static List<OnStaffEventListener> mStaffListeners;
			
	public void addOnStaffEventListener(OnStaffEventListener lisener){
		if(mStaffListeners == null) 
			mStaffListeners = 
				new ArrayList<MenuApplication.OnStaffEventListener>();
		mStaffListeners.add(lisener);
	}
	
	public void removeOnStaffEventListener(OnStaffEventListener listener){
		if(mStaffListeners == null) return;
		mStaffListeners.remove(listener);
		if(mStaffListeners.isEmpty()) mStaffListeners = null;
	}
	
	private static void notifyOnStaffEventListeners(StaffEvent event, Object...args){
		if(mStaffListeners == null) return;
		for(OnStaffEventListener listener : mStaffListeners){
			listener.onStaffEvent(event, args);
		}
	}
	
	
	private List<OnCompanyInfoListener> mCompanyInfoListeners;
	
	public void addOnCompanyInfoListener(OnCompanyInfoListener listener){
		if(mCompanyInfoListeners == null)
			mCompanyInfoListeners = new ArrayList<MenuApplication.OnCompanyInfoListener>();
		mCompanyInfoListeners.add(listener);
	}
	
	public void removeOnCompanyInfoListener(OnCompanyInfoListener listener){
		if(mCompanyInfoListeners == null) return;
		mCompanyInfoListeners.remove(listener);
		if(mCompanyInfoListeners.isEmpty()) mCompanyInfoListeners = null;
	}
	
	private void notifyOnCompanyInfoListener(){
		if(mCompanyInfoListeners == null) return;
		for(OnCompanyInfoListener listener : mCompanyInfoListeners){
			listener.onCompanyInfoChanged();
		}
	}
	
	
	private List<OnMapChangedListener> mMapListeners;
	
	public void addOnMapChangedListener(OnMapChangedListener listener){
		if (mMapListeners == null) {
			mMapListeners = new ArrayList<MenuApplication.OnMapChangedListener>();
		}	
		mMapListeners.add(listener);
	}
	
	public void removeOnMapListener(OnMapChangedListener listener){
		if (mMapListeners == null) {
			return;
		}
		mMapListeners.remove(listener);
		if (mMapListeners.isEmpty()) {
			mMapListeners = null;
		}
	}
	
	private void notifyMapListeners(){
		if (mMapListeners == null) {
			return;
		}
		for(OnMapChangedListener listener : mMapListeners){
			listener.onMapChanged();
		}
	}
	
	/* ADS CHANGED */
	public interface OnAdsChangedListener{
		void onAdsChanged();
	}
	
	private List<OnAdsChangedListener> mAdsListener;
	
	public void addAdsListener(OnAdsChangedListener listener){
		if (mAdsListener == null) {
			mAdsListener = new ArrayList<MenuApplication.OnAdsChangedListener>();
		}
		mAdsListener.add(listener);
	}
	public void removeAdsListener(OnAdsChangedListener listener){
		if (mAdsListener == null) {
			return;
		}
		mAdsListener.remove(listener);
		if (mAdsListener.isEmpty()) {
			mAdsListener = null;
		}
	}
	private void notifyAdsListeners() {
		if (mAdsListener == null) {
			return;
		}
		for (OnAdsChangedListener listener : mAdsListener) {
			listener.onAdsChanged();
		}
	}
	
	/* REBOOT REQUEST */
	public interface OnRebootRequestedListener {
		void onRebootRequested();
	}
	
	private List<OnRebootRequestedListener> mRebootListeners;
	
	public void addOnRebootRequestListener(OnRebootRequestedListener listener){
		if(mRebootListeners == null) mRebootListeners = new ArrayList<MenuApplication.OnRebootRequestedListener>();
		mRebootListeners.add(listener);
	}
	
	public void remooveOnRebootRequestListeners(OnRebootRequestedListener listener){
		if(mRebootListeners == null) return;
		mRebootListeners.remove(listener);
		if(mRebootListeners.isEmpty()) mRebootListeners = null;
	}
	
	private void notifyRebootListeners(){
		if(mRebootListeners == null) return;
		for(OnRebootRequestedListener listener : mRebootListeners){
			listener.onRebootRequested();
		}
	}
	
	public void fakeRequestReboot(){
		notifyRebootListeners();
	}

	/* RESOURCES CHANGED */
	public interface OnResourcesChangedListener{
		void onResourcesChanged();
	}
	
	public static SupportedLanguage getLanguage() {
		return mClient.getLanguage();
	}
	
	private static List<OnResourcesChangedListener> mResourceChangeListeners;
	
	public void addOnResourceChangeListener(OnResourcesChangedListener listener){
		if(mResourceChangeListeners == null) mResourceChangeListeners = new ArrayList<MenuApplication.OnResourcesChangedListener>();
		mResourceChangeListeners.add(listener);
	}
	
	public void removeOnResourceChangeListener(OnResourcesChangedListener listener){
		if(mResourceChangeListeners == null) return;
		mResourceChangeListeners.remove(listener);
		if(mResourceChangeListeners.isEmpty()) mResourceChangeListeners = null;
	}
}
