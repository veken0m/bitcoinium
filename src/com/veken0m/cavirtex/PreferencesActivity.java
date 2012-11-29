package com.veken0m.cavirtex;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {
	public static final String REFRESH = "com.veken0m.cavirtex.REFRESH";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		Preference primepref = (Preference) findPreference("primepref");
		primepref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri
						.parse("market://details?id=com.veken0m.bitcoinium"));
				startActivity(intent);
				return true;
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Tell the widgets to update preferences
		Intent intent = new Intent(getApplicationContext(),
				WidgetProvider.class);
		intent.setAction(REFRESH);
		sendBroadcast(intent);
	}
}
