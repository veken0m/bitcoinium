
package com.veken0m.bitcoinium;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.google.analytics.tracking.android.EasyTracker;
import com.veken0m.bitcoinium.exchanges.Exchange;
import com.veken0m.utils.Constants;

public class WidgetConfigureActivity extends PreferenceActivity {

    private static final String PREF_EXCHANGE_KEY = "exchange_";
    private static final String PREF_CURRENCY_KEY = "currency_";
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public WidgetConfigureActivity() {
        super();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_price_widget);
        addPreferencesFromResource(R.xml.pref_widgets);
        addPreferencesFromResource(R.xml.pref_create_widget);

        // Set the result to CANCELED. This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Find the widget id from the intent.
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
            finish();

        ListPreference widgetExchangePref = (ListPreference) findPreference("widgetExchangePref");
        ListPreference pCurrency = (ListPreference) findPreference("widgetCurrencyPref");

        // get the Resource ID for the currency array
        String sExchange = (widgetExchangePref != null) ? widgetExchangePref.getValue() : Constants.DEFAULT_EXCHANGE;
        int nCurrencyArrayId = getResources().getIdentifier(sExchange + "currencies", "array", this.getPackageName());

        // populate the list with the Exchange's Currency Pairs
        setCurrencyItems(pCurrency, nCurrencyArrayId);

        if(widgetExchangePref != null) {
            widgetExchangePref.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {

                            ListPreference pCurrency = (ListPreference) findPreference("widgetCurrencyPref");
                            int nCurrencyArrayId = getResources().getIdentifier(newValue.toString() + "currencies", "array", getBaseContext().getPackageName());

                            setCurrencyItems(pCurrency, nCurrencyArrayId);

                            return true;
                        }
                    }
            );
        }

        Preference OKpref = findPreference("OKpref");
        if (OKpref != null) {
            OKpref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Context context = WidgetConfigureActivity.this;
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    String pref_widgetExchange = prefs.getString("widgetExchangePref", Constants.DEFAULT_EXCHANGE);

                    Exchange exchange;
                    try {
                        exchange = new Exchange(context, pref_widgetExchange);
                    } catch (Exception e) {
                        Editor editor = prefs.edit();
                        editor.putString("widgetExchangePref", Constants.DEFAULT_EXCHANGE).commit();
                        exchange = new Exchange(context, Constants.DEFAULT_EXCHANGE);
                    }

                    String sCurrency = prefs.getString("widgetCurrencyPref", exchange.getDefaultCurrency());

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
    }

    private static void setCurrencyItems(ListPreference pCurrency, int nCurrencyArrayId){

        pCurrency.setEntries(nCurrencyArrayId);
        pCurrency.setEntryValues(nCurrencyArrayId);
        pCurrency.setValueIndex(0);
    }

    // Write the prefix to the SharedPreferences object for this widget
    private static void saveCurrencyPref(Context context, int appWidgetId, String currency) {

        SharedPreferences.Editor prefs = context.getSharedPreferences(Constants.PREFS_NAME_PRICE, 0).edit();
        prefs.putString(PREF_CURRENCY_KEY + appWidgetId, currency);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadCurrencyPref(Context context, int appWidgetId) {

        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME_PRICE, 0);
        return prefs.getString(PREF_CURRENCY_KEY + appWidgetId, null);
    }

    // Write the prefix to the SharedPreferences object for this widget
    private static void saveExchangePref(Context context, int appWidgetId, String exchange) {

        SharedPreferences.Editor prefs = context.getSharedPreferences(Constants.PREFS_NAME_PRICE, 0).edit();
        prefs.putString(PREF_EXCHANGE_KEY + appWidgetId, exchange);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadExchangePref(Context context, int appWidgetId) {

        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME_PRICE, 0);
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

        sendBroadcast(new Intent(this, WidgetProvider.class).setAction(Constants.REFRESH));
        EasyTracker.getInstance(this).activityStop(this);
    }

}
