package com.fosslabs.android.context;

import java.util.TimeZone;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import com.fosslabs.android.jsbase.R;
import com.fosslabs.android.utils.Funs;
import com.fosslabs.android.utils.JSLog;
import com.fosslabs.android.utils.JSTimeWorker;

public class TestClockPreferenceFragment extends PreferenceFragment {
	private static final String TAG = "TestClockPreferenceFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
		// fetch the item where you wish to insert the CheckBoxPreference, in
		// this case a PreferenceCategory with key "targetCategory"
		PreferenceCategory targetCategory = (PreferenceCategory) findPreference("timezone");

		String[] ids = TimeZone.getAvailableIDs();
		for (int i = 0; i < ids.length; i++) {
			// create one check box for each setting you need
			CheckBoxPreference checkBoxPreference = new CheckBoxPreference(
					getActivity());
			// make sure each key is unique
			checkBoxPreference.setKey(ids[i]);
			checkBoxPreference.setChecked(false);
			checkBoxPreference.setTitle(JSTimeWorker.getTimeZonePretty(ids[i]));
			targetCategory.addPreference(checkBoxPreference);
		}

	}
}
