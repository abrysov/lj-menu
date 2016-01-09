package com.sqiwy.dashboard.model.action;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.util.Log;
import com.sqiwy.restaurant.api.data.NotificationType;
import com.sqiwy.restaurant.api.OperationService;
import com.sqiwy.dashboard.CancelableToast;
import com.sqiwy.dashboard.CancelableToast.VerticalAlignment;
import com.sqiwy.dashboard.R;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.util.PreferencesUtils;

/**
 * Created by abrysov
 */

@SuppressWarnings("serial")
class ActionCallWaiter extends ActionBase {

	private static final String TAG = ActionCallWaiter.class.getName();
	
	private class CallWaiterTask extends AsyncTask<Activity, Void, Boolean> {
		private Activity mActivity;
		
		@SuppressWarnings("deprecation")
		protected Boolean doInBackground(Activity... params) {
			OperationService operationService = MenuApplication.getOperationService();
			
			Boolean result = Boolean.FALSE;
			
			mActivity=params[0];
			if (null != operationService) {
				try {
					if (!PreferencesUtils.isApplicationInDemoMode(MenuApplication.getContext())) {
						operationService.callStaff(NotificationType.CALL_WAITER);
					}
					result = Boolean.TRUE;
				} catch (Exception e) {
					Log.e(TAG, "Error while calling waiter.", e);
				}
			}
			
			return result;
		}
		
		protected void onPostExecute(Boolean result) {
			FragmentManager fm= ((Activity)mActivity).getFragmentManager();
			new CancelableToast.Config()
				.setText(result ? R.string.waiter_is_coming : R.string.error_on_call_waiter)
				.setVerticalAlignment(result ? VerticalAlignment.BOTTOM : VerticalAlignment.CENTER)
				.show(fm);
			
			onActionDone();
		};
	}
	
	@Override
	public void execute(ActionContext actionContext) {
		super.execute(actionContext);
		
		Activity context = (Activity)actionContext.getContext();
		
		new CallWaiterTask().execute(context);
	}

	@Override
	public String getType() {
		return Action.TYPE_CALL_WAITER;
	}

}
