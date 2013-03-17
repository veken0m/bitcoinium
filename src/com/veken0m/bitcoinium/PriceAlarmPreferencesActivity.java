package com.veken0m.bitcoinium;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class PriceAlarmPreferencesActivity extends PreferenceActivity {
	public static final String REFRESH = "com.veken0m.bitcoinium.REFRESH";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.price_alarm_preferences);

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
