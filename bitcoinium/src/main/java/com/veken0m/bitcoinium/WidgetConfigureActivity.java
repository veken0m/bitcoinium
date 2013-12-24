
package com.veken0m.bitcoinium;

import java.util.HashMap;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;

import com.google.analytics.tracking.android.EasyTracker;
import com.veken0m.bitcoinium.exchanges.Exchange;

public class WidgetConfigureActivity extends PreferenceActivity {

    private static final String PREFS_NAME = "com.veken0m.bitcoinium.WidgetProvider";
    private static final String PREF_EXCHANGE_KEY = "exchange_";
    private static final String PREF_CURRENCY_KEY = "currency_";
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private ListPreference widgetExchangePref = null;
    private PreferenceCategory prefCategory = null;

    private final HashMap<String, Preference> currencyPref = new HashMap<String, Preference>();

    public WidgetConfigureActivity() {
        super();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_price_widget);
        addPreferencesFromResource(R.xml.pref_widgets);
        addPreferencesFromResource(R.xml.ok_button);

        // Set the result to CANCELED. This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        try {

            widgetExchangePref = (ListPreference) findPreference("widgetExchangesPref");
            prefCategory = (PreferenceCategory) findPreference("priceWidgetPreferences");

            currencyPref.put("MtGox", findPreference("mtgoxWidgetCurrencyPref"));
            currencyPref.put("VirtEx", findPreference("virtexWidgetCurrencyPref"));
            currencyPref.put("BTCE", findPreference("btceWidgetCurrencyPref"));
            currencyPref.put("Bitstamp", findPreference("bitstampWidgetCurrencyPref"));
            currencyPref.put("CampBX", findPreference("campbxWidgetCurrencyPref"));
            currencyPref.put("BTCChina", findPreference("btcchinaWidgetCurrencyPref"));
            currencyPref.put("Bitcurex", findPreference("bitcurexWidgetCurrencyPref"));
            currencyPref.put("Kraken", findPreference("krakenWidgetCurrencyPref"));
            currencyPref.put("BitcoinAverage", findPreference("bitcoinaverageWidgetCurrencyPref"));

            // Disable all the currency pickers and enable the selected exchange
            for (Preference value : currencyPref.values()){
                prefCategory.removePreference(value);
            }

            prefCategory.addPreference(currencyPref.get(widgetExchangePref
                    .getEntry().toString().replace("Exchange", "").replace("-", "")));

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Find the widget id from the intent.
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
            finish();

        widgetExchangePref.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {

                        try {
                            prefCategory = (PreferenceCategory) findPreference("priceWidgetPreferences");

                            // Disable all the currency pickers
                            for (Preference value : currencyPref.values())
                                prefCategory.removePreference(value);

                            // Enable the selected exchange
                            prefCategory.addPreference(currencyPref.get(newValue
                                    .toString().replace("Exchange", "").replace("-", "")));
                            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                });
        
        Preference OKpref = findPreference("OKpref");
        OKpref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Context context = WidgetConfigureActivity.this;
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(context);

                String pref_widgetExchange = prefs.getString(
                        "widgetExchangesPref", "MtGoxExchange");

                Exchange exchange;
                try {
                    exchange = new Exchange(context, pref_widgetExchange);
                } catch (Exception e) {
                    Editor editor = prefs.edit();
                    editor.putString("widgetExchangesPref", "MtGoxExchange").commit();
                    exchange = new Exchange(context, "MtGoxExchange");
                }

                String sCurrency = prefs.getString(exchange.getIdentifier() + "WidgetCurrencyPref",
                        exchange.getDefaultCurrency());

                // Save widget configuration
                saveCurrencyPref(context, mAppWidgetId, sCurrency);
                saveExchangePref(context, mAppWidgetId, pref_widgetExchange);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);

                finish();
                return true;
            }
        });
    }

    // Write the prefix to the SharedPreferences object for this widget
    private static void saveCurrencyPref(Context context, int appWidgetId,
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
        String currency = prefs.getString(PREF_CURRENCY_KEY + appWidgetId, null);
        if (currency != null) {
            return currency;
        } else {
            return context.getString(R.string.default_currency);
        }
    }

    // Write the prefix to the SharedPreferences object for this widget
    private static void saveExchangePref(Context context, int appWidgetId, String exchange) {

        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_EXCHANGE_KEY + appWidgetId, exchange);
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

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }
}
