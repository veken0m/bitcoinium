package com.veken0m.bitcoinium;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;

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
		
		// Disable all the currency pickers until exchange is chosen
		ListPreference widgetExchangePref = (ListPreference) findPreference("widgetExchangesPref");
		PreferenceGroup widgetPref = (PreferenceGroup) findPreference("widget_currency");
		((ListPreference) findPreference("mtgoxWidgetCurrencyPref")).setEnabled(false);
		((ListPreference) findPreference("btceWidgetCurrencyPref")).setEnabled(false);
		((ListPreference) findPreference("virtexWidgetCurrencyPref")).setEnabled(false);
		((ListPreference) findPreference("bitstampWidgetCurrencyPref")).setEnabled(false);
		((ListPreference) findPreference("campbxWidgetCurrencyPref")).setEnabled(false);
		//((ListPreference) findPreference("bitcoincentralWidgetCurrencyPref")).setEnabled(false);
		try{
		((ListPreference) findPreference(widgetExchangePref.getEntry().toString().toLowerCase().replace("exchange", "") + "WidgetCurrencyPref")).setEnabled(true);
		} catch (Exception e){
		}

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
	    
	    widgetExchangePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
            	// Only enable the currency preference of the selected exchange
        		((ListPreference) findPreference("mtgoxWidgetCurrencyPref")).setEnabled(false);
        		((ListPreference) findPreference("btceWidgetCurrencyPref")).setEnabled(false);
        		((ListPreference) findPreference("virtexWidgetCurrencyPref")).setEnabled(false);
        		((ListPreference) findPreference("bitstampWidgetCurrencyPref")).setEnabled(false);
        		((ListPreference) findPreference("campbxWidgetCurrencyPref")).setEnabled(false);
        		//((ListPreference) findPreference("bitcoincentralWidgetCurrencyPref")).setEnabled(false);
        		((ListPreference) findPreference(newValue.toString().toLowerCase().replace("exchange", "") + "WidgetCurrencyPref")).setEnabled(true);		
            	
                return true;
            }
	    }
	    		);

		OKpref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				final Context context = WidgetConfigureActivity.this;
				
				String pref_widgetExchange = prefs.getString(
						"widgetExchangesPref", "MtGoxExchange");
				
				Exchange exchange = new Exchange(getResources().getStringArray(
						getResources().getIdentifier(pref_widgetExchange, "array",
								getBaseContext().getPackageName())));

				String defaultCurrency = exchange.getMainCurrency();
				String prefix = exchange.getPrefix();

				String pref_widgetCurrency = prefs.getString(
						prefix + "WidgetCurrencyPref", defaultCurrency);

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
			return context.getString(R.string.default_exchange);
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
			return context.getString(R.string.default_exchange);
		}

	}

}
