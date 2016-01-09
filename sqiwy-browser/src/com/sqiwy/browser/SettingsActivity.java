package com.sqiwy.browser;

import com.sqiwi.browser.R;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Created by abrysov
 */

public class SettingsActivity extends PreferenceActivity {
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        Preference sysSettingsButton = (Preference)getPreferenceManager().findPreference("systemSettingsLink");      
        if (sysSettingsButton != null) {
        	sysSettingsButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        	    @Override
                public boolean onPreferenceClick(Preference arg0) {
        	    	startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS)); 
                    return true;
        	    }
        	});
        }
    }
    
    @Override
    public void onResume() {
    	SystemControllerHelper.setSystemUiMode(getApplicationContext(),
        		SystemControllerHelper.SYSTEM_UI_MODE_ENABLE_NAVIGATION);
        super.onResume();
    }
    
}
