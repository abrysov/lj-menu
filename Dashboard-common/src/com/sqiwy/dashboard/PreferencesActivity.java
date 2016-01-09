package com.sqiwy.dashboard;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.sqiwy.dashboard.util.FlowUtils;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.service.UpdateService;
import com.sqiwy.menu.util.PreferencesUtils;
import com.sqiwy.menu.util.SystemControllerHelper;

/**
 * Created by abrysov
 */

public class PreferencesActivity extends PreferenceActivity {

	/**
	 * consts
	 */
	private static final String TAG_DIALOG_PROGRESS = "dialog_progress";
	private static final String TAG_DIALOG_MESSAGE = "dialog_message";
	
	/**
	 * 
	 */
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Add a button to the header list.
        if (hasHeaders()) {
        	LayoutInflater li=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	ViewGroup prefsActivityButtonLayout=(ViewGroup)li.inflate(R.layout.prefsactivitybuttons,null);
        	Button reloadButton=(Button)prefsActivityButtonLayout.findViewById(R.id.preferences_reload_button),
        			proceedButton=(Button)prefsActivityButtonLayout.findViewById(R.id.preferences_proceed_button);
        	
        	reloadButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					UpdateService.start();
				}
        	});
            proceedButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					onButtonNextClicked();
				}
			});
            setListFooter(prefsActivityButtonLayout);
        }
        
        // remove progress dialog
        removeDialog(this, TAG_DIALOG_PROGRESS);
    }

    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
    	
        loadHeadersFromResource(R.xml.preference_headres, target);
    }
    
    /**Щ
     * 
     */
    @Override
    public void onBackPressed() {
    	// Do nothing
    }
    
    private class OperationServicePingTask extends AsyncTask<Void, Void, Void> {
		private Throwable mError = null;
		
		@Override
		protected void onPreExecute() {
			// show progress dialog
			DialogFragment dialog = ProgressDialogFragment.newInstance(null);
			PreferencesActivity.showDialog(PreferencesActivity.this, dialog, TAG_DIALOG_PROGRESS);
		};
		
		@Override
		protected Void doInBackground(Void... params) {

			try {
				if (!PreferencesUtils.isApplicationInDemoMode(PreferencesActivity.this)) {
					MenuApplication.getOperationService().ping();
				}
			}
			catch(Throwable error) {
				
				mError = error;
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
		
	    	// remove progress dialog
			PreferencesActivity.removeDialog(PreferencesActivity.this, TAG_DIALOG_PROGRESS);
	    	
			if(null == mError) {
				
				goNext();
			}
			else {
				
				showError(mError);
			} 
		};
    }
    
    /**
     * 
     */
    private void onButtonNextClicked() {
    	
    	if(!isAnyDialogShown(this, new String[]{TAG_DIALOG_PROGRESS, TAG_DIALOG_PROGRESS})) {
    		
    		MenuApplication.initOperationServiceWithCallback(new MenuApplication.OperationServiceInitCallback() {
    			public void OnOperationServiceInit() {
    				new OperationServicePingTask().execute((Void)null);
    			}
    		});
    	}
    }
    
    /**
     * 
     */
    private void goNext() {
    	
		// enable install apps
		SystemControllerHelper.enableInstallApps(
                PreferenceManager.getDefaultSharedPreferences(PreferencesActivity.this).getBoolean("pref_enable_install_apps", true));
		
		// Re-configure backend and operation service.
		MenuApplication.initOperationService();
		
		// Remember that backend config has been completed
		PreferencesUtils.setBackendConfigCompleted(PreferencesActivity.this, true);

        MenuApplication.mIsCheckResources = true;
		FlowUtils.continueSetupFlow(PreferencesActivity.this);
		
		PreferencesActivity.this.finish();
    }
    
    /**
     * 
     * @param error
     */
    private void showError(Throwable error) {
    	
    	showDialog(this, 
    			SimpleMessageDialogFragment.newInstance(getString(R.string.settings_error_dialog_title), error.toString(), null), 
    			TAG_DIALOG_MESSAGE);
    }
    
    /**
     * 
     * @param activity
     * @param dialog
     * @param dialogTag
     */
    private static void showDialog(final Activity activity, final DialogFragment dialog, final String dialogTag) {
    	
		FragmentManager fm = activity.getFragmentManager();
		
		try {
			
			fm.executePendingTransactions();
		} 
		catch (Throwable e) {

		}

		Fragment prev = fm.findFragmentByTag(dialogTag);
		
		if( (null != prev) &&
			(prev instanceof DialogFragment) ) {
			
			((DialogFragment)prev).dismissAllowingStateLoss();
		}
		
		dialog.show(fm, dialogTag);
	}
    
    /**
     * 
     * @param activity
     * @param dialogTag
     */
    private static void removeDialog(final Activity activity, final String dialogTag) {
		
		FragmentManager fm = activity.getFragmentManager();
		
		try {
			
			fm.executePendingTransactions();
		} 
		catch (Throwable e) {

		}

		Fragment prev = fm.findFragmentByTag(dialogTag);
		
		if( (null != prev) &&
			(prev instanceof DialogFragment) ) {
			
			((DialogFragment)prev).dismissAllowingStateLoss();
		}
	}
	
	/**
	 * 
	 * @param activity
	 * @param dialogTags
	 * @return
	 */
	private static boolean isAnyDialogShown(final Activity activity, final String[] dialogTags) {
		
		FragmentManager fm = activity.getFragmentManager();
		
		try {
			
			fm.executePendingTransactions();
		} 
		catch (Throwable e) {

		}
		
		for(String tag : dialogTags) {
			
			Fragment prev = fm.findFragmentByTag(tag);
			
			if( (null != prev) &&
				(prev instanceof DialogFragment) ) {
				
				return true;
			}
		}
		
		
		return false;
	}
    
    /**
     * This fragment shows the preferences for the first header.
     */
    public static class ClientConfig extends PreferenceFragment implements OnSharedPreferenceChangeListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.frag_pref_client_config);
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                initSummary(getPreferenceScreen().getPreference(i));
            }
        }
        
        @Override
        public void onResume() {
        	super.onResume();
        	getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }
        
        
        @Override
        public void onPause() {
        	super.onPause();
        	getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
        
    	@Override
    	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    		updatePrefSummary(findPreference(key));
    	}
    	
        private void initSummary(Preference p) {
            if (p instanceof PreferenceCategory) {
                PreferenceCategory pCat = (PreferenceCategory) p;
                for (int i = 0; i < pCat.getPreferenceCount(); i++) {
                    initSummary(pCat.getPreference(i));
                }
            } else {
                updatePrefSummary(p);
            }
        }

        private void updatePrefSummary(Preference p) {
            if (p instanceof EditTextPreference) {
                EditTextPreference editTextPref = (EditTextPreference) p;
                String text = editTextPref.getText(); 
                text = (text != null && text.trim().length() > 0) ? text : "Empty"; 
                p.setSummary(text);
            }
        }
    }
    
    /**
     * This fragment shows the preferences for the first header.
     */
    public static class SettingsConfig extends PreferenceFragment implements OnPreferenceClickListener {
    	
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.frag_pref_settings);
            
            findPreference("pref_system_settings").setOnPreferenceClickListener(this);            
        }
        
		@Override
		public boolean onPreferenceClick(Preference preference) {

			boolean result = false;
			
			if ("pref_system_settings".equals(preference.getKey())) {
				
				startActivity(new Intent(Settings.ACTION_SETTINGS));
				SystemControllerHelper.setSystemUiMode(SystemControllerHelper.SYSTEM_UI_MODE_ENABLE_NAVIGATION);
				
				result = true;
			}
			
			return result;
		}
    }
    
    /**
     *
     */
    public static class SimpleMessageDialogFragment extends DialogFragment {

    	/**
    	 * consts
    	 */
    	private static final String ARG_TITLE = "title";
    	private static final String ARG_MESSAGE = "message";
    	private static final String ARG_BUTTON = "button";
    	
    	/**
    	 * 
    	 * @param title
    	 * @param message
    	 * @param button
    	 * @return
    	 */
    	public static SimpleMessageDialogFragment newInstance(String title, String message, String button) {
    		
    		SimpleMessageDialogFragment fragment = new SimpleMessageDialogFragment();
    		Bundle args = new Bundle();
    		
    		if(null != title) {
    			
    			args.putString(ARG_TITLE, title);
    		}
    		
    		if(null != message) {
    			
    			args.putString(ARG_MESSAGE, message);
    		}
    		
    		if(null != button) {
    			
    			args.putString(ARG_BUTTON, button);
    		}
    		
    		fragment.setArguments(args);
    		
    		return fragment;
    	}
    	
    	/**
    	 * 
    	 * @param savedInstanceState
    	 * @return
    	 */
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {

    		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    		Bundle args = getArguments();
    		String str;
    		
    		if(null != (str = args.getString(ARG_TITLE, null))) {
    			
    			builder.setTitle(str);
    		}
    		
    		if(null != (str = args.getString(ARG_MESSAGE, null))) {
    			
    			builder.setMessage(str);
    		}
    		
    		if(null != (str = args.getString(ARG_TITLE, getActivity().getString(android.R.string.ok)))) {
    			
    			builder.setTitle(str);
    		}
    		
    		builder.setPositiveButton(args.containsKey(ARG_BUTTON) ? args.getString(ARG_BUTTON) : getActivity().getString(android.R.string.ok),
    				
    			new DialogInterface.OnClickListener() {
						
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
			});
    		
    		return builder.create();
    	}
    }
}
