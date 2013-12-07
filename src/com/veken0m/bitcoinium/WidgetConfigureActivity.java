
package com.veken0m.bitcoinium;

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
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.veken0m.bitcoinium.exchanges.Exchange;

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
        addPreferencesFromResource(R.xml.pref_price_widget);
        addPreferencesFromResource(R.xml.pref_widgets);
        addPreferencesFromResource(R.xml.ok_button);

        // Set the result to CANCELED. This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Disable all the currency pickers until exchange is chosen
        ListPreference widgetExchangePref = (ListPreference) findPreference("widgetExchangesPref");
        PreferenceGroup widgetPref = (PreferenceGroup) findPreference("widget_currency");
        ((ListPreference) findPreference("mtgoxWidgetCurrencyPref"))
                .setEnabled(false);
        ((ListPreference) findPreference("btceWidgetCurrencyPref"))
                .setEnabled(false);
        ((ListPreference) findPreference("virtexWidgetCurrencyPref"))
                .setEnabled(false);
        ((ListPreference) findPreference("bitstampWidgetCurrencyPref"))
                .setEnabled(false);
        ((ListPreference) findPreference("campbxWidgetCurrencyPref"))
                .setEnabled(false);
        ((ListPreference) findPreference("btcchinaWidgetCurrencyPref"))
                .setEnabled(false);
        ((ListPreference) findPreference("bitcurexWidgetCurrencyPref"))
                .setEnabled(false);
        ((ListPreference) findPreference("krakenWidgetCurrencyPref"))
                .setEnabled(false);
        ((ListPreference) findPreference("bitcoinaverageWidgetCurrencyPref"))
                .setEnabled(false);

        try {
            ((ListPreference) findPreference(widgetExchangePref.getEntry()
                    .toString().toLowerCase().replace("exchange", "").replace("-", "")
                    + "WidgetCurrencyPref")).setEnabled(true);
        } catch (Exception e) {
            Toast toaster = new Toast(this);
            toaster.setText("Could not enable currency pref for this exchange");
            toaster.show();
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

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Preference OKpref = findPreference("OKpref");

        widgetExchangePref
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference,
                            Object newValue) {
                        // Only enable the currency preference of the selected
                        // exchange
                        ((ListPreference) findPreference("mtgoxWidgetCurrencyPref"))
                                .setEnabled(false);
                        ((ListPreference) findPreference("btceWidgetCurrencyPref"))
                                .setEnabled(false);
                        ((ListPreference) findPreference("virtexWidgetCurrencyPref"))
                                .setEnabled(false);
                        ((ListPreference) findPreference("bitstampWidgetCurrencyPref"))
                                .setEnabled(false);
                        ((ListPreference) findPreference("campbxWidgetCurrencyPref"))
                                .setEnabled(false);
                        ((ListPreference) findPreference("btcchinaWidgetCurrencyPref"))
                                .setEnabled(false);
                        ((ListPreference) findPreference("bitcurexWidgetCurrencyPref"))
                                .setEnabled(false);
                        ((ListPreference) findPreference("krakenWidgetCurrencyPref"))
                                .setEnabled(false);
                        ((ListPreference) findPreference("bitcoinaverageWidgetCurrencyPref"))
                                .setEnabled(false);

                        ((ListPreference) findPreference(newValue.toString()
                                .toLowerCase().replace("exchange", "")
                                + "WidgetCurrencyPref")).setEnabled(true);

                        return true;
                    }
                });

        OKpref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Context context = WidgetConfigureActivity.this;
                String pref_widgetExchange = prefs.getString(
                        "widgetExchangesPref", "MtGoxExchange");

                Exchange exchange;
                try {
                    exchange = new Exchange(context, pref_widgetExchange);
                } catch (Exception e) {
                    // If preference is not set a valid integer set to
                    // "MtGoxExchange"
                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(getBaseContext());

                    Editor editor = prefs.edit();
                    editor.putString("widgetExchangesPref", "MtGoxExchange");
                    editor.commit();
                    exchange = new Exchange(context, pref_widgetExchange);
                }

                String defaultCurrency = exchange.getDefaultCurrency();
                String prefix = exchange.getIdentifier();

                String pref_widgetCurrency = prefs.getString(prefix
                        + "WidgetCurrencyPref", defaultCurrency);

                // Save widget configuration
                saveCurrencyPref(context, mAppWidgetId, pref_widgetCurrency);
                saveExchangePref(context, mAppWidgetId, pref_widgetExchange);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                        mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                // Set alarm to refresh widget at specified interval
                BaseWidgetProvider.setPriceWidgetAlarm(context);
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
        String currency = prefs.getString(PREF_CURRENCY_KEY + appWidgetId, null);
        if (currency != null) {
            return currency;
        } else {
            return context.getString(R.string.default_currency);
        }
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveExchangePref(Context context, int appWidgetId,
            String exchange) {
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
}
