package com.veken0m.cavirtex;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.EditText;

public class MtGoxWidgetConfigure extends PreferenceActivity {

	static final String TAG = "ExampleAppWidgetConfigure";

	private static final String PREFS_NAME = "com.veken0m.cavirtex.WAtcherWidgetProvider2";
	private static final String PREF_PREFIX_KEY = "prefix_";

	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	String mAppWidgetPrefix;

	static String pref_widgetCurrency = null;

	public MtGoxWidgetConfigure() {
		super();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set the result to CANCELED. This will cause the widget host to cancel
		// out of the widget placement if they press the back button.
		setResult(RESULT_CANCELED);

		// setContentView(R.layout.main);
		addPreferencesFromResource(R.xml.widget_preferences);

		// Find the widget id from the intent.
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// If they gave us an intent without the widget id, just bail.
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

		mAppWidgetPrefix = loadCurrencyPref(MtGoxWidgetConfigure.this,
				mAppWidgetId);

		Context context = getBaseContext();

		// Do configuration stuff HERE
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		Preference OKpref = (Preference) findPreference("OKpref");
		OKpref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				final Context context = MtGoxWidgetConfigure.this;

				pref_widgetCurrency = prefs.getString(
						"mtgoxWidgetCurrencyPref", "USD");
				// Get instance of AppWidgetManaget

				// When the button is clicked, save the string in our prefs and
				// return that they
				// clicked OK.
				saveCurrencyPref(context, mAppWidgetId, pref_widgetCurrency);

				BaseWidgetProvider.setAlarm2(context);
				
				// Make sure we pass back the original appWidgetId
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						mAppWidgetId);
				setResult(RESULT_OK, resultValue);
				finish();
				return true;
			}

		});

	}

	// Write the prefix to the SharedPreferences object for this widget
	static void saveCurrencyPref(Context context, int appWidgetId,
			String currency) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putString(PREF_PREFIX_KEY + appWidgetId, currency);
		prefs.commit();
	}

	// Read the prefix from the SharedPreferences object for this widget.
	// If there is no preference saved, get the default from a resource
	static String loadCurrencyPref(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String prefix = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
		if (prefix != null) {
			return prefix;
		} else {
			return context.getString(R.string.appwidget_prefix_default);
		}
	}

	// static void deleteTitlePref(Context context, int appWidgetId) {
	// }
	//
	// static void loadAllTitlePrefs(Context context, ArrayList<Integer>
	// appWidgetIds,
	// ArrayList<String> texts) {
	// }

}
