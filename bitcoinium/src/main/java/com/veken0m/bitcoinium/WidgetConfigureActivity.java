
package com.veken0m.bitcoinium;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.veken0m.bitcoinium.exchanges.Exchange;
import com.veken0m.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WidgetConfigureActivity extends SherlockPreferenceActivity {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.widget_accept_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_widget_accept) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            String pref_widgetExchange = prefs.getString("widgetExchangesPref", "bitstamp");

            Exchange exchange;
            try {
                exchange = new Exchange(this, pref_widgetExchange);
            } catch (Exception e) {
                Editor editor = prefs.edit();
                editor.putString("widgetExchangesPref", "bitstamp").commit();
                exchange = new Exchange(this, "bitstamp");
            }

            String sCurrency = prefs.getString(exchange.getIdentifier() + "WidgetCurrencyPref", exchange.getDefaultCurrency());

            // Save widget configuration
            saveCurrencyPref(this, mAppWidgetId, sCurrency);
            saveExchangePref(this, mAppWidgetId, pref_widgetExchange);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_price_widget);
        addPreferencesFromResource(R.xml.pref_widgets);

        // Set the result to CANCELED. This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        try {
            String[] exchangeID = getResources().getStringArray(R.array.exchangeID);
            prefCategory = (PreferenceCategory) findPreference("priceWidgetPreferences");

            // Disable all the currency pickers and enable the selected exchange
            for (int i = 0; i < exchangeID.length; i++) {
                currencyPref.put(exchangeID[i], findPreference(exchangeID[i] + "WidgetCurrencyPref"));
                prefCategory.removePreference(currencyPref.get(exchangeID[i]));
            }

            widgetExchangePref = (ListPreference) findPreference("widgetExchangesPref");
            String sExchange = (widgetExchangePref.getValue() != null) ? widgetExchangePref.getValue().toString() : "bitstamp";
            prefCategory.addPreference(currencyPref.get(sExchange));

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Find the widget id from the intent.
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

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
                            prefCategory.addPreference(currencyPref.get(newValue.toString()));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                });
    }

    // Write the prefix to the SharedPreferences object for this widget
    private static void saveCurrencyPref(Context context, int appWidgetId, String currency) {

        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_CURRENCY_KEY + appWidgetId, currency);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadCurrencyPref(Context context, int appWidgetId) {

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_CURRENCY_KEY + appWidgetId, null);
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
        return prefs.getString(PREF_EXCHANGE_KEY + appWidgetId, null);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("googleAnalyticsPref", false))
            EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        Intent intent = new Intent(this, WidgetProvider.class);
        intent.setAction(Constants.REFRESH);
        this.sendBroadcast(intent);

        EasyTracker.getInstance(this).activityStop(this);
    }
}
