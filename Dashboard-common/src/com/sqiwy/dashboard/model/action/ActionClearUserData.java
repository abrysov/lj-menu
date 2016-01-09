package com.sqiwy.dashboard.model.action;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.sqiwy.dashboard.CancelableToast;
import com.sqiwy.dashboard.CancelableToast.VerticalAlignment;
import com.sqiwy.dashboard.R;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.MenuApplication.OnTableSessionEventListener.SessionEvent;
import com.sqiwy.menu.MenuApplication.TableSessionEventListener;
import com.sqiwy.menu.chat.ChatManager;
import com.sqiwy.restaurant.api.OperationService;

/**
 * Created by abrysov
 */

@SuppressWarnings("serial")
class ActionClearUserData extends ActionBase {

	private static final String TAG = ActionClearUserData.class.getName();
	
	@Override
	public void execute(ActionContext actionContext) {
		super.execute(actionContext);
		
		final Context context = actionContext.getContext();
		
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				OperationService operationService = MenuApplication.getOperationService();
				boolean result = true;
				try {
					ChatManager.getInstance().exitChat();
					MenuApplication.getOperationService().exitChat();
					operationService.closeTableSession();
				} catch (Exception e) {
					e.printStackTrace();
					result = false;
					Log.e(TAG, "Error while closing session", e);
				}
				Log.d("ActionClearUser", "done");
				MenuApplication.notifyTableSessionListeners(SessionEvent.SESSION_CLOSED);
				MenuApplication.TableSessionEventListener tsl = new TableSessionEventListener();
				tsl.onTableSessionEvent(SessionEvent.SESSION_CLOSED);
				return result;
			}
			
			protected void onPostExecute(Boolean result) {
				Resources rc = context.getResources();
				Activity ac = (Activity)context;
				FragmentManager fm = ac.getFragmentManager();
				new CancelableToast.Config()
					.setText(result ? R.string.closing_session_success : R.string.closing_session_failure)
					.setVerticalAlignment(result ? VerticalAlignment.CENTER : VerticalAlignment.BOTTOM)
					.show(fm);
		        onActionDone();
			};
		}.execute();
			
	}

	@Override
	public String getType() {
		return Action.TYPE_CLOSE_SESSION;
	}

}
