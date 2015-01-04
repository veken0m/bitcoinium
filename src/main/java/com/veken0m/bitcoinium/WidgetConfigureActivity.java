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
import android.support.v4.util.Pair;

import com.veken0m.bitcoinium.exchanges.ExchangeProperties;
import com.veken0m.utils.Constants;

import java.util.List;

import static com.veken0m.utils.ExchangeUtils.getDropdownItems;

public class WidgetConfigureActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener
{
    private static final String PREF_EXCHANGE_KEY = "exchange_";
    private static final String PREF_CURRENCY_KEY = "currency_";
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public WidgetConfigureActivity()
    {
        super();
    }

    private static void setCurrencyItems(ListPreference pCurrency, int nCurrencyArrayId)
    {
        pCurrency.setEntries(nCurrencyArrayId);
        pCurrency.setEntryValues(nCurrencyArrayId);
        pCurrency.setValueIndex(0);
    }

    // Write the prefix to the SharedPreferences object for this widget
    private static void saveCurrencyPref(Context context, int appWidgetId, String currency)
    {
        SharedPreferences.Editor prefs = context.getSharedPreferences(Constants.PREFS_NAME_PRICE, 0).edit();
        prefs.putString(PREF_CURRENCY_KEY + appWidgetId, currency);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static String loadCurrencyPref(Context context, int appWidgetId)
    {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME_PRICE, 0);
        return prefs.getString(PREF_CURRENCY_KEY + appWidgetId, null);
    }

    // Write the prefix to the SharedPreferences object for this widget
    private static void saveExchangePref(Context context, int appWidgetId, String exchange)
    {
        SharedPreferences.Editor prefs = context.getSharedPreferences(Constants.PREFS_NAME_PRICE, 0).edit();
        prefs.putString(PREF_EXCHANGE_KEY + appWidgetId, exchange);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static String loadExchangePref(Context context, int appWidgetId)
    {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME_PRICE, 0);
        String exchangePref = prefs.getString(PREF_EXCHANGE_KEY + appWidgetId, null);

        // Replace MtGox to BitcoinAverage
        if (exchangePref != null && exchangePref.toLowerCase().contains("mtgox"))
            exchangePref = "bitcoinaverage";

        return exchangePref;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
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

        Pair<List<String>, List<String>> exchanges = getDropdownItems(getApplicationContext(), ExchangeProperties.ItemType.TICKER_ENABLED);
        widgetExchangePref.setEntries(exchanges.first.toArray(new CharSequence[exchanges.first.size()]));
        widgetExchangePref.setEntryValues(exchanges.second.toArray(new CharSequence[exchanges.second.size()]));

        // get the Resource ID for the currency array
        ExchangeProperties ex = new ExchangeProperties(this, widgetExchangePref.getValue());
        int nCurrencyArrayId = getResources().getIdentifier(ex.getIdentifier() + "currencies", "array", this.getPackageName());

        // populate the list with the Exchange's Currency Pairs
        setCurrencyItems(pCurrency, nCurrencyArrayId);

        widgetExchangePref.setOnPreferenceChangeListener(this);

        Preference OKpref = findPreference("OKpref");
        OKpref.setOnPreferenceClickListener(this);
    }

    @Override
    public void onStop()
    {
        super.onStop();

        sendBroadcast(new Intent(this, WidgetProvider.class).setAction(Constants.REFRESH));
    }

    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String pref_widgetExchange = prefs.getString("widgetExchangePref", Constants.DEFAULT_EXCHANGE);

        ExchangeProperties exchange;
        try
        {
            exchange = new ExchangeProperties(this, pref_widgetExchange);
        }
        catch (Exception e)
        {
            Editor editor = prefs.edit();
            editor.putString("widgetExchangePref", Constants.DEFAULT_EXCHANGE).commit();
            exchange = new ExchangeProperties(this, Constants.DEFAULT_EXCHANGE);
        }

        String sCurrency = prefs.getString("widgetCurrencyPref", exchange.getDefaultCurrency());

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

    @Override
    public boolean onPreferenceChange(Preference preference, Object o)
    {
        ListPreference pCurrency = (ListPreference) findPreference("widgetCurrencyPref");
        int nCurrencyArrayId = getResources().getIdentifier(o.toString() + "currencies", "array", getBaseContext().getPackageName());

        setCurrencyItems(pCurrency, nCurrencyArrayId);

        return true;
    }
}
