package com.sqiwy.dashboard.logger;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sqiwy.dashboard.logger.LogMessage.Stage;
import com.sqiwy.dashboard.logger.LogMessage.Status;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.util.PreferencesUtils;
import com.squareup.tape.ObjectQueue;
import com.squareup.tape.ObjectQueue.Listener;

/**
 * Created by abrysov
 */

public class LoggerService extends Service {

	/**
	 * 
	 */
	private static final String TAG = "LoggerService";
	private static final String ACTION_SEND_MESSAGE = "com.sqiwy.dashboard.logger.ACTION_SEND_MESSAGE";
	private static final String ACTION_SEND_QUEUED_MESSAGES = "com.sqiwy.dashboard.logger.ACTION_SEND_QUEUED_MESSAGES";
	private static final String ARG_LOG_MESSAGE = "log_message";
	
	/**
	 * 
	 */
	private volatile Object SYNC_LOGGER_QUEUE = new Object();
	private volatile LoggerQueue mLoggerQueue = null;
	private volatile Set<Integer> mStartIds = new HashSet<Integer>(); 
	private HandlerThread mSendQueuedMessagesThread;
	private HandlerThread mSendMessageThread;
	private SendQueuedMessagesHandler mSendQueuedMessagesHandler;
	private SendMessageHandler mSendMessageHandler;
	private static Handler mToastHandler;
	
	/**
	 * 
	 */
	public LoggerService() {
		super();
	}

	/**
	 * 
	 */
	@Override
	public void onCreate() {

		super.onCreate();
		
		mSendQueuedMessagesThread = new HandlerThread(TAG + "_SendQueuedMessagesThread");
		mSendQueuedMessagesThread.start();
		
		mSendMessageThread = new HandlerThread(TAG + "_SendMessageThread");
		mSendMessageThread.start();
		
		mSendQueuedMessagesHandler = new SendQueuedMessagesHandler(mSendQueuedMessagesThread.getLooper());
		mSendMessageHandler = new SendMessageHandler(mSendMessageThread.getLooper());
		
//		Log.i(TAG, "Service started.");
	}
	
	/**
	 * 
	 */
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		
		String action = intent.getAction();
		Message msg;
		
		requestStartService(startId);
		
//		Log.i(TAG, String.format("Handling action '%s'", action));
		
		if(ACTION_SEND_QUEUED_MESSAGES.equals(action)) {
					
			msg = mSendQueuedMessagesHandler.obtainMessage();
			msg.arg1 = startId;
			msg.obj = intent;
			msg.sendToTarget();					
		}
		else 
		if(ACTION_SEND_MESSAGE.equals(action)) {

			msg = mSendMessageHandler.obtainMessage();
			msg.arg1 = startId;
			msg.obj = intent;
			msg.sendToTarget();
		}
		else {
			
			throw new IllegalArgumentException("Invalid action: " + ((null != action) ? action : "<null>"));
		}

        return START_REDELIVER_INTENT;
    }

	/**
	 * 
	 */
    @Override
    public void onDestroy() {
    	
    	mSendQueuedMessagesThread.getLooper().quit();
    	mSendMessageThread.getLooper().quit();
    	
//		Log.i(TAG, "Service stopped.");
    }
	 
    /**
     * 
     */
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	/*******************************************************************************
	 * 
	 * 										API
	 * 
	 *******************************************************************************/
	/**
	 * 
	 * @param context
	 */
	public static void sendQueuedMessages(Context context) {
		
		Intent service = new Intent(context, LoggerService.class);
		service.setAction(ACTION_SEND_QUEUED_MESSAGES);
		context.startService(service);
	}
	
	/**
	 * 
	 * @param context
	 * @param message
	 */
	public static void sendMessage(Context context, LogMessage message) {
		
		Intent service = new Intent(context, LoggerService.class);
		service.setAction(ACTION_SEND_MESSAGE);
		service.putExtra(ARG_LOG_MESSAGE, message);
		context.startService(service);
		if (message!=null && MenuApplication.isQAEnvironment()) {
			displayToast(context,message);
		}
	}
	
	private static class QAToastRunnable implements Runnable {
		private Context mContext;
		private LogMessage mMessage;
		
		public QAToastRunnable(Context context, LogMessage message) {
			mContext=context; mMessage=message;
		}
		
		public void run() {
			Toast t=new Toast(mContext);
			LayoutInflater li=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View toastView=li.inflate(com.sqiwy.dashboard.R.layout.loggertoast, null);
			TextView stageView=(TextView)toastView.findViewById(com.sqiwy.dashboard.R.id.logMessageStage),
					messageView=(TextView)toastView.findViewById(com.sqiwy.dashboard.R.id.logMessageText);
			Stage messageStage=mMessage.getStage();
			Status messageStatus=mMessage.getStatus();
			String messageDetails=mMessage.getMessage();
			
			t.setDuration(messageStatus!=null && messageStatus.equals(Status.FAILED)?Toast.LENGTH_LONG:Toast.LENGTH_SHORT);
			t.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			stageView.setText(messageStage==null?"Stage not set":messageStage.toString());
			messageView.setVisibility(View.VISIBLE);
			if (messageStatus!=null) {
				messageView.setText(messageDetails);
			}
			switch (messageStatus) {
				case SUCCESS: toastView.setBackgroundColor(0xFFFFFFFF); stageView.setTextColor(0xFF000000); messageView.setTextColor(0xFF000000); break;
				case FAILED: toastView.setBackgroundColor(0xFFFF0000); stageView.setTextColor(0xFFFFFFFF); messageView.setTextColor(0xFFFFFFFF); break;
			}
			t.setView(toastView);
			t.show();
		}
	}
	
	private static void displayToast(Context context, LogMessage message) {
		if (mToastHandler==null) {
			mToastHandler=new Handler(Looper.getMainLooper());
		}
		mToastHandler.post(new QAToastRunnable(context,message));
	}
	
	/*******************************************************************************
	 * 
	 * 								HELPERS
	 * 
	 *******************************************************************************/
	
	/**
	 * 
	 * @param startId
	 */
	private synchronized void finishService(int startId) {

		if(true == requestStopService(startId)) {
			
			stopSelf();
		}
	}
	
	/**
	 * 
	 * @param startId
	 */
	private synchronized void requestStartService(int startId) {
		
		mStartIds.add(startId);	
	}
	
	/**
	 * 
	 * @param startId
	 * @return
	 */
	private synchronized boolean requestStopService(int startId) {
		
		mStartIds.remove(startId);
		
		if(0 == mStartIds.size()) {
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 */
	private LoggerQueue getLoggerQueue() {
		
		synchronized (SYNC_LOGGER_QUEUE) {
			
			if(null == mLoggerQueue) {
				
				try {
					
					mLoggerQueue = new LoggerQueue(new File(ResourcesManager.getRootDir(), "logger.msgs"), new LoggerQueue.GsonConverter<LogMessage>(new Gson(), LogMessage.class));

                    mLoggerQueue.setListener(new Listener<LogMessage>() {

                        @Override
                        public void onAdd(ObjectQueue<LogMessage> queue, LogMessage message) {

                            // send queued messages
                            sendQueuedMessages(LoggerService.this);
                        }

                        @Override
                        public void onRemove(ObjectQueue<LogMessage> arg0) {

                        }
                    });

				}
				catch (IOException e) {
					
					mLoggerQueue = null;
				}
				
			}
			
			return mLoggerQueue;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private static boolean isConnectedToNetwork(Context context) {
		
		ConnectivityManager connMan = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = connMan.getActiveNetworkInfo();

		if(null != ni) {

			return ni.isConnected();
		}
		
		return false;
	}
	
	/**
	 *
	 */
	private final class SendQueuedMessagesHandler extends Handler {

		/**
		 * 
		 * @param looper
		 */
		public SendQueuedMessagesHandler(Looper looper) {

			super(looper);
		}

		/**
	     * 
	     */
		@Override
		public void handleMessage(Message msg) {

			int serviceId = msg.arg1;
			LoggerQueue queue = getLoggerQueue();
			LogMessage message;
			
			while(null != (message = queue.peek())) {				
	
				if (!PreferencesUtils.isApplicationInDemoMode(MenuApplication.getContext())) {
					// TODO: send message
					// if (false == MenuApplication.getOperationService().sendLogMessage(message)) {
					//     
					// break;
					// }
				}

				if(!isConnectedToNetwork(LoggerService.this)) {
				
					Log.i(TAG, "Cant send queued message: no internet connection");
					break;
				}
				
//				Log.i(TAG, "Message sent: " + message.toString());
				
				queue.remove();
			}

			//
			finishService(serviceId);
		}
	}
	
	/**
	 *
	 */
	private final class SendMessageHandler extends Handler {

		/**
		 * 
		 * @param looper
		 */
		public SendMessageHandler(Looper looper) {

			super(looper);
		}

		/**
	     * 
	     */
		@Override
		public void handleMessage(Message msg) {

			int serviceId = msg.arg1;
			Intent intent = (Intent) msg.obj;
			LoggerQueue queue = getLoggerQueue();
			
			// put message to the queue
			LogMessage message = (LogMessage)intent.getSerializableExtra(ARG_LOG_MESSAGE); 
			queue.add(message);
			
//			Log.i(TAG, "Message queued: " + message.toString());
			
			//
			finishService(serviceId);
		}
	}
}
