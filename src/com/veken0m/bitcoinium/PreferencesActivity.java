package com.veken0m.bitcoinium;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import net.margaritov.preference.colorpicker.*;

public class PreferencesActivity extends PreferenceActivity {
	public static final String REFRESH = "com.veken0m.bitcoinium.REFRESH";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO: Change Preferences to use Fragments, current method is
		// deprecated.
		addPreferencesFromResource(R.xml.preferences);

		Preference devEmailPref = (Preference) findPreference("devEmailPref");
		devEmailPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					Resources res = getResources();

					public boolean onPreferenceClick(Preference preference) {
						Intent i = new Intent(Intent.ACTION_SEND);
						i.setType("message/rfc822");
						i.putExtra(android.content.Intent.EXTRA_EMAIL,
								new String[] { "veken0m.apps@gmail.com" });
						i.putExtra(android.content.Intent.EXTRA_SUBJECT,
								res.getString(R.string.app_name) + " Feedback");
						startActivity(Intent.createChooser(i, "Send email"));

						return true;
					}
				});

		Preference xchangeGithubPref = (Preference) findPreference("xchangeGithubPref");
		xchangeGithubPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse("https://github.com/timmolter/xchange")));
						return true;
					}
				});
		Preference bitcoiniumGithubPref = (Preference) findPreference("bitcoiniumGithubPref");
		bitcoiniumGithubPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse("https://github.com/veken0m/bitcoinium")));
						return true;
					}
				});

		// Widget Customization
		((ColorPickerPreference) findPreference("widgetBackgroundColorPref"))
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						preference.setSummary(ColorPickerPreference
								.convertToARGB(Integer.valueOf(String
										.valueOf(newValue))));
						return true;
					}

				});

		((ColorPickerPreference) findPreference("widgetMainTextColorPref"))
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						preference.setSummary(ColorPickerPreference
								.convertToARGB(Integer.valueOf(String
										.valueOf(newValue))));
						return true;
					}

				});

		((ColorPickerPreference) findPreference("widgetSecondaryTextColorPref"))
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						preference.setSummary(ColorPickerPreference
								.convertToARGB(Integer.valueOf(String
										.valueOf(newValue))));
						return true;
					}

				});
		
		Preference donationAddressPref = (Preference) findPreference("donationAddressPref");
		donationAddressPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {

						int sdk = android.os.Build.VERSION.SDK_INT;
						if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
							android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
							clipboard
									.setText("1yjDmiukhB2i1XyVw5t7hpAK4WXo32d54");
						} else {
							android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
							android.content.ClipData clip = android.content.ClipData
									.newPlainText("Donation Address",
											"1yjDmiukhB2i1XyVw5t7hpAK4WXo32d54");
							clipboard.setPrimaryClip(clip);
						}
						Toast.makeText(getApplicationContext(),
								"Address copied to clipboard",
								Toast.LENGTH_SHORT).show();
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
