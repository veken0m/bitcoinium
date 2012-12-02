package com.veken0m.bitcoinium;

import com.veken0m.bitcoinium.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {
	public static final String REFRESH = "com.veken0m.bitcoinium.REFRESH";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		Preference devEmailPref = (Preference) findPreference("devEmailPref");
		devEmailPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "veken0m.apps@gmail.com" });
				i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Bitcoinium Prime Feedback");
				//i.putExtra(android.content.Intent.EXTRA_TEXT, "");
				startActivity(Intent.createChooser(i, "Send email"));
				
				return true;
			}
		});
		Preference donationAddressPref = (Preference) findPreference("donationAddressPref");
		donationAddressPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				
				
				int sdk = android.os.Build.VERSION.SDK_INT;
				if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
				    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				    clipboard.setText("1yjDmiukhB2i1XyVw5t7hpAK4WXo32d54");
				} else {
				    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE); 
				    android.content.ClipData clip = android.content.ClipData.newPlainText("Donation Address","1yjDmiukhB2i1XyVw5t7hpAK4WXo32d54");
				    clipboard.setPrimaryClip(clip);
				}
				Toast.makeText(getApplicationContext(), "Address copied to clipboard", Toast.LENGTH_SHORT).show();
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
