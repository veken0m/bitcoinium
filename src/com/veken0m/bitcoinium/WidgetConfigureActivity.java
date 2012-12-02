package com.veken0m.bitcoinium;

import com.veken0m.bitcoinium.R;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;

public class WidgetConfigureActivity extends PreferenceActivity {

	private static final String PREFS_NAME = "com.veken0m.bitcoinium.WidgetProvider";
	private static final String PREF_EXCHANGE_KEY = "exchange_";
	private static final String PREF_CURRENCY_KEY = "currency_";
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	public WidgetConfigureActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.widget_preferences);

		// Set the result to CANCELED. This will cause the widget host to cancel
		// out of the widget placement if they press the back button.
		setResult(RESULT_CANCELED);

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

		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		Preference OKpref = (Preference) findPreference("OKpref");

		OKpref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				final Context context = WidgetConfigureActivity.this;

				String pref_widgetCurrency = prefs.getString(
						"mtgoxWidgetCurrencyPref", "USD");

				String pref_widgetExchange = prefs.getString(
						"widgetExchangesPref", "mtgox");

				// When the button is clicked, save the string in our prefs and
				// return that they clicked OK.
				saveCurrencyPref(context, mAppWidgetId, pref_widgetCurrency);
				saveExchangePref(context, mAppWidgetId, pref_widgetExchange);

				// Set alarm to refresh widget at specified interval
				BaseWidgetProvider.setAlarm(context);

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
		prefs.putString(PREF_CURRENCY_KEY + appWidgetId, currency);
		prefs.commit();
	}

	// Read the prefix from the SharedPreferences object for this widget.
	// If there is no preference saved, get the default from a resource
	static String loadCurrencyPref(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String prefix = prefs.getString(PREF_CURRENCY_KEY + appWidgetId, null);
		if (prefix != null) {
			return prefix;
		} else {
			return context.getString(R.string.appwidget_prefix_default);
		}
	}

	// Write the prefix to the SharedPreferences object for this widget
	static void saveExchangePref(Context context, int appWidgetId,
			String currency) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putString(PREF_EXCHANGE_KEY + appWidgetId, currency);
		prefs.commit();
	}

	// Read the prefix from the SharedPreferences object for this widget.
	// If there is no preference saved, get the default from a resource
	static String loadExchangePref(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String prefix = prefs.getString(PREF_EXCHANGE_KEY + appWidgetId, null);
		if (prefix != null) {
			return prefix;
		} else {
			return context.getString(R.string.appwidget_prefix_default);
		}

	}

}
