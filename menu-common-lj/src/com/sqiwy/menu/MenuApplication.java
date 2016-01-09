package com.sqiwy.menu;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.fosslabs.android.utils.JSImageWorker;
import com.sqiwy.backend.api.BackendClient;
import com.sqiwy.backend.api.ClientConfig;
import com.sqiwy.backend.api.constants.RestartMode;
import com.sqiwy.backend.api.table.OperationService;
import com.sqiwy.backend.api.table.TableEventService;
import com.sqiwy.menu.MenuApplication.OnChatEventListener.ChatEvent;
import com.sqiwy.menu.MenuApplication.OnMenuEventListener.MenuEvent;
import com.sqiwy.menu.MenuApplication.OnOrderEventListener.OrderEvent;
import com.sqiwy.menu.MenuApplication.OnStaffEventListener.StaffEvent;
import com.sqiwy.menu.MenuApplication.OnTableSessionEventListener.SessionEvent;
import com.sqiwy.menu.provider.OrderManager;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.resource.ResourcesManager.Category;
import com.sqiwy.menu.service.UpdateService;
import com.sqiwy.menu.util.MenuControllerHelper;
import com.sqiwy.menu.util.PreferencesUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abrysov
 */

public class MenuApplication extends Application {
	
	public int orientation;
	
	
	private static Context sContext;
	public static Context getContext() {
		return sContext;
	}

	
	private static final List<String> sLaunchedApps = new ArrayList<String>();
    public static void trackLaunchedApp(String packageName) {
        if (!sLaunchedApps.contains(packageName)) {
            sLaunchedApps.add(packageName);
        }
    }
    
    
	private static OperationService sService;
	private static BackendClient sClient;
	
	/**
	 * Have to store lock background here and make it load
	 * only once in order to make lock screen work smooth
	 * (except the first time it's opened)
	 */
	private Bitmap mLockBackground;

	
    @Override
	public void onCreate() {
		super.onCreate();
		
		sContext = getApplicationContext();
		
		// Make Chrome to be launched in desktop mode
		MenuControllerHelper.setChromeToDesktopMode();
		
		initOperationService();
		
		sService = getOperationService();

        addOnTableSessionEventListener(new TableSessionEventListener());
        addOnRebootRequestListener(new RebootRequestedListener());
	}
	
	
    public static void initOperationService() {
 		ClientConfig config = getBackendClientConfig();
		
 		if (null != config) {

 			sClient = getBackendClient(config);
 			sService = sClient == null ? null : sClient.getOperationService();
 		}
	}
	
	public static OperationService getOperationService() {
 		return sService;
 	}
	
	/**
	 * Have to store lock background here and make it load
	 * only once in order to make lock screen work smooth
	 * (except the first time it's opened)
	 */
	public Bitmap getLockBackground() {
		if (null == mLockBackground) {
			
			File back = ResourcesManager.getResourcePath("bg_window_lock.jpg", Category.DASHBOARD);
			mLockBackground = JSImageWorker.loadFromFile(back.getAbsolutePath());
		}
		
		return mLockBackground;
	}
	
	private static BackendClient getBackendClient(ClientConfig config){
		BackendClient client = null;
		if(config != null){
			client = new BackendClient();
			client.setConfig(config);
			client.setEventService(new ServiceEventListener());
			try {
				client.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return client;
	}
	


    
	public static ClientConfig getBackendClientConfig() {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
		
		String clientId = sp.getString(PreferencesUtils.CLIENT_ID, "");
		String pass = sp.getString(PreferencesUtils.PASSWORD, "");
		String hostName = sp.getString(PreferencesUtils.HOST_NAME, "");
		int hostPort = Integer.valueOf(sp.getString(PreferencesUtils.HOST_PORT, "0"));
		int maxPacket = Integer.valueOf(sp.getString(PreferencesUtils.MAX_PACKET_SIZE, "0"));
		int pollDelay = Integer.valueOf(sp.getString(PreferencesUtils.POLLING_DELAY, "0"));
		int timeout = Integer.valueOf(sp.getString(PreferencesUtils.SOCKET_TIMEOUT, "0"));
		
		// Check if all data provided in preferences
		if(TextUtils.isEmpty(clientId) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(hostName)
				|| hostPort == 0 || maxPacket == 0 || pollDelay == 0 || timeout == 0) {
			
			return null;
			
		}
		
		ClientConfig config = new ClientConfig();
		
		config.setClientId(clientId);
		config.setPassword(pass);
		config.setHostName(hostName);
		config.setHostPort(hostPort);
		config.setMaxPacketSize(maxPacket);
		config.setPollingDelay(pollDelay);
		config.setSocketTimeout(timeout);
		
//		config.setClientId("1");
//		config.setPassword("1234");
//		config.setHostName("185.12.117.33");
//		config.setHostPort(8080);
//		config.setMaxPacketSize(1048576);
//		config.setPollingDelay(5000);
//		config.setSocketTimeout(15000);
		
		return config;
	}
	
	
	
	
	
	
	
	
	
	
	/*
	 * Back-end events
	 */
	
	public static class ServiceEventListener implements TableEventService {

		/* CHAT EVENTS */
		
		@Override
		public void chatBanned() {
			notifyChatListeners(ChatEvent.CHAT_BANNED);
		}

		@Override
		public void chatClosed() {
			notifyChatListeners(ChatEvent.CHAT_CLOSED);
		}

		@Override
		public void chatMessageReceived(int arg0, String arg1, boolean arg2) {
			notifyChatListeners(ChatEvent.CHAT_MESSAGE_RECEIVED, arg0, arg1, arg2);
		}

		@Override
		public void chatUserListChanged() {
			notifyChatListeners(ChatEvent.CHAT_USER_LIST_CHANGED);
		}
		


		/* ORDER EVENTS */
		
		@Override
		public void orderAdded() {
			notifyOrderListeners(OrderEvent.ORDER_ADDED);
		}

		@Override
		public void orderListChanged() {
			notifyOrderListeners(OrderEvent.ORDER_LIST_CHANGED);
		}

		/* STAFF CALL EVENT */
		
		@Override
		public void staffCallConfirmed() {
			notifyOnStaffEventListeners(StaffEvent.STAFF_CALL_CONFIRM);
		}
		
		@Override
		public void waiterLinkedToTable(int arg0, String arg1) {
			notifyOnStaffEventListeners(StaffEvent.WAITER_LINKED_TO_TABLE, arg0, arg1);
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

		/* COMPANY INFO EVENT */
		@Override
		public void companyInfoChanged() {
			notifyOnCompanyInfoListener();
		}

		/* MAP EVENT */
		@Override
		public void mapChanged() {
			notifyMapListeners();
		}

		/* MENU EVENT */
		@Override
		public void menuChanged() {
			notifyMenuListeners(MenuEvent.MENU_CHANGED);
		}
		
		/* ADS EVENT */
		@Override
		public void advertisementsChanged() {
			notifyAdsListeners();
		}

		/* RESOURCES CHANGED */
		@Override
		public void resourcesChanged() {
			notifyOnResourceChangeListeners();
		}

        @Override
        public void restartRequested(RestartMode restartMode) {
            // :TODO
        }
    }
	
	
	
	/*
	  Interfaces that splits the back-end events by logical groups  
	 */
	
	
	/* CHAT EVENT LISTENERS */
	
	private static List<OnChatEventListener> chatListeners;
	
	/**
	 * Chat events 
	 */
	public interface OnChatEventListener{
		/**
		 * Chat events.
		 */
		enum ChatEvent{
			/**
			 * Chat is banned.
			 * No additional arguments.
			 */
			CHAT_BANNED,
			/**
			 * Chat is closed.
			 * No additional arguments.
			 */
			CHAT_CLOSED,
			/**
			 * Chat message is received.
			 * Additional arguments is (int, String, boolean).
			 */
			CHAT_MESSAGE_RECEIVED,
			/**
			 * User list is changed.
			 * No additional arguments.
			 */
			CHAT_USER_LIST_CHANGED
		}
		/**
		 * This method will be called if chat event occurs.  
		 * @param event
		 * @param args
		 * @see ChatEvent
		 */
		void chatEvent(ChatEvent event, Object...args);
	}
	
	/**
	 * Adds OnChatEvent listener.
	 * If used in activity it is recommended to add listener in onResume() callback.
	 * Do not forget to call removeOnChatEvent() in onPause().
	 * @param listener
	 */
	public static void addOnChatEventListener(OnChatEventListener listener){
		if(chatListeners == null)
			chatListeners = new ArrayList<OnChatEventListener>();
		chatListeners.add(listener);
	}
	
	/**
	 * Removes OnChatEvent listener
	 * @param listener
	 */
	public static void removeOnChatEventListener(OnChatEventListener listener){
		if(chatListeners == null) return;
		chatListeners.remove(listener);
		if(chatListeners.isEmpty()) chatListeners = null;
	}
	
	private static void notifyChatListeners(ChatEvent event, Object...args){
		if(chatListeners == null) return;
		for(OnChatEventListener listener :chatListeners){
			listener.chatEvent(event, args);
		}
	}
	
	
	/* ORDER EVENT LISTENERS */
	
	private static List<OnOrderEventListener> orderListeners; 

	
	public interface OnOrderEventListener{
		/**
		 * Order events
		 */
		enum OrderEvent{
			ORDER_ADDED,
			ORDER_LIST_CHANGED;
		}
		void orderEvent(OrderEvent event);
	}
	
	public static void addOnOrderEventListener(OnOrderEventListener listener){
		if(orderListeners == null) orderListeners = new ArrayList<OnOrderEventListener>();
		orderListeners.add(listener);
	}
	
	public static void removeOnOrderEventListener(OnOrderEventListener listener){
		if(orderListeners == null) return;
		orderListeners.remove(listener);
		if(orderListeners.isEmpty()) orderListeners = null;
	}
	
	private static void notifyOrderListeners(OrderEvent event){
		if(orderListeners == null) return;
		for(OnOrderEventListener listener : orderListeners){
			listener.orderEvent(event);
		}
	}
	
	
	
	/* MENU EVENT LISTENERS */
	
	private static List<OnMenuEventListener> menuListeners;

	
	public interface OnMenuEventListener{
		enum MenuEvent{
			MENU_CHANGED
		}
		void onMenuEvent(MenuEvent event);
	}
	
	public static void addOnMenuEventListener(OnMenuEventListener listener){
		if(menuListeners == null) menuListeners = new ArrayList<OnMenuEventListener>();
		menuListeners.add(listener);
	}
	
	public static void removeOnMenuEventListener(OnMenuEventListener listener){
		if(menuListeners == null) return;
		menuListeners.remove(listener);
		if(menuListeners.isEmpty()) menuListeners = null;
	}
	
	private static void notifyMenuListeners(MenuEvent event){
		if(menuListeners == null) return;
		for(OnMenuEventListener listener : menuListeners){
			listener.onMenuEvent(event);
		}
	}


	
	/* TABLE SESSION EVENTS */
	
	private static List<OnTableSessionEventListener> sessionListeners;
	
	public interface OnTableSessionEventListener{
		enum SessionEvent{
			SESSION_OPENED,
			SESSION_CLOSED
		}
		void onTableSessionEvent(SessionEvent event);
	}
	
	public static void addOnTableSessionEventListener(OnTableSessionEventListener listener){
		if(sessionListeners == null){
			sessionListeners = 
					new ArrayList<OnTableSessionEventListener>();
		}
		sessionListeners.add(listener);
	}
	
	public static void removeOnTableSessionEventListener(OnTableSessionEventListener listener){
		if(sessionListeners == null) return;
		sessionListeners.remove(listener);
		if(sessionListeners.isEmpty()) sessionListeners = null;
	}
	
	private static void notifyTableSessionListeners(SessionEvent event){
		if(sessionListeners == null) return;
		for(OnTableSessionEventListener listener : sessionListeners){
			listener.onTableSessionEvent(event);
		}
	}
	
	
	
	/* STAFF EVENTS */
	public interface OnStaffEventListener{
		enum StaffEvent{
			STAFF_CALL_CONFIRM,
			WAITER_LINKED_TO_TABLE
		}
		void onStaffEvent(StaffEvent event, Object...args);
	}
	
	private static List<OnStaffEventListener> staffListeners;
			
	public static void addOnStaffEventListener(OnStaffEventListener lisener){
		if(staffListeners == null) 
			staffListeners = 
				new ArrayList<MenuApplication.OnStaffEventListener>();
		staffListeners.add(lisener);
	}
	
	public static void removeOnStaffEventListener(OnStaffEventListener listener){
		if(staffListeners == null) return;
		staffListeners.remove(listener);
		if(staffListeners.isEmpty()) staffListeners = null;
	}
	
	private static void notifyOnStaffEventListeners(StaffEvent event, Object...args){
		if(staffListeners == null) return;
		for(OnStaffEventListener listener : staffListeners){
			listener.onStaffEvent(event, args);
		}
	}
	
	
	
	
	/* Company info changed */
	
	public interface OnCompanyInfoListener{
		void onCompanyInfoChanged();
	}
	private static List<OnCompanyInfoListener> companyInfoListeners;
	public static void addOnCompanyInfoListener(OnCompanyInfoListener listener){
		if(companyInfoListeners == null)
			companyInfoListeners = new ArrayList<MenuApplication.OnCompanyInfoListener>();
		companyInfoListeners.add(listener);
	}
	public static void removeOnCompanyInfoListener(OnCompanyInfoListener listener){
		if(companyInfoListeners == null) return;
		companyInfoListeners.remove(listener);
		if(companyInfoListeners.isEmpty()) companyInfoListeners = null;
	}
	private static void notifyOnCompanyInfoListener(){
		if(companyInfoListeners == null) return;
		for(OnCompanyInfoListener listener : companyInfoListeners){
			listener.onCompanyInfoChanged();
		}
	}
	
	
	
	/* MAP CHANGED EVENT */
	public interface OnMapChangedListener{
		void onMapChanged();
	}
	private static List<OnMapChangedListener> mapListeners;
	public static void addOnMapChangedListener(OnMapChangedListener listener){
		if(mapListeners == null) mapListeners = 
				new ArrayList<MenuApplication.OnMapChangedListener>();
		mapListeners.add(listener);
	}
	public static void removeOnMapListener(OnMapChangedListener listener){
		if(mapListeners == null) return;
		mapListeners.remove(listener);
		if(mapListeners.isEmpty()) mapListeners = null;
	}
	private static void notifyMapListeners(){
		if(mapListeners == null) return;
		for(OnMapChangedListener listener : mapListeners){
			listener.onMapChanged();
		}
	}
	
	
	
	/* ADS CHANGED */
	public interface OnAdsChangedListener{
		void onAdsChanged();
	}
	private static List<OnAdsChangedListener> adsListener;
	public static void addAdsListener(OnAdsChangedListener listener){
		if(adsListener == null) adsListener = new ArrayList<MenuApplication.OnAdsChangedListener>();
		adsListener.add(listener);
	}
	public static void removeAdsListener(OnAdsChangedListener listener){
		if(adsListener == null) return;
		adsListener.remove(listener);
		if(adsListener.isEmpty()) adsListener = null;
	}
	private static void notifyAdsListeners(){
		if(adsListener == null) return;
		for(OnAdsChangedListener listener : adsListener){
			listener.onAdsChanged();
		}
	}
	
	
	
	/* REBOOT REQUEST */
	public interface OnRebootRequestedListener{
		void onRebootRequested();
	}
	private static List<OnRebootRequestedListener> rebootListeners;
	public static void addOnRebootRequestListener(OnRebootRequestedListener listener){
		if(rebootListeners == null) 
			rebootListeners = new ArrayList<MenuApplication.OnRebootRequestedListener>();
		rebootListeners.add(listener);
	}
	public static void remooveOnRebootRequestListeners(OnRebootRequestedListener listener){
		if(rebootListeners == null) return;
		rebootListeners.remove(listener);
		if(rebootListeners.isEmpty()) rebootListeners = null;
	}
	private static void notifyRebootListeners(){
		if(rebootListeners == null) return;
		for(OnRebootRequestedListener listener : rebootListeners){
			listener.onRebootRequested();
		}
	}
	public static void fakeRequestReboot(){
		notifyRebootListeners();
	}

	
	
	/* RESOURCES CHANGED */
	public interface OnResourcesChangedListener{
		void onResourcesChanged();
	}
	private static List<OnResourcesChangedListener> resourceChangeListeners;
	public static void addOnResourceChangeListener(OnResourcesChangedListener listener){
		if(resourceChangeListeners == null) 
			resourceChangeListeners = new ArrayList<MenuApplication.OnResourcesChangedListener>();
		resourceChangeListeners.add(listener);
	}
	public static void removeOnResourceChangeListener(OnResourcesChangedListener listener){
		if(resourceChangeListeners == null) return;
		resourceChangeListeners.remove(listener);
		if(resourceChangeListeners.isEmpty()) resourceChangeListeners = null;
	}
	private static void notifyOnResourceChangeListeners(){
		if(resourceChangeListeners == null) return;
		for(OnResourcesChangedListener listener : resourceChangeListeners){
			listener.onResourcesChanged();
		}
	}

    private static class TableSessionEventListener implements OnTableSessionEventListener {
        @Override
        public void onTableSessionEvent(SessionEvent event) {
            if (event == SessionEvent.SESSION_CLOSED) {
                OrderManager.deleteSessionOrders();
                if (!sLaunchedApps.isEmpty()) {
                    MenuControllerHelper.clearAppData(sLaunchedApps);
                    sLaunchedApps.clear();
                }
            }
        }
    }

    private static class RebootRequestedListener implements OnRebootRequestedListener {
        @Override
        public void onRebootRequested() {
            // It's required that we reload all resources after reboot
            // (clearing flag that resources were loaded).
            ResourcesManager.setResourcesLoaded(sContext, false);

            UpdateService.start();
        }
    }

}
