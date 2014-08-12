package com.veken0m.bitcoinium.preferences;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.veken0m.bitcoinium.BalanceWidgetProvider;
import com.veken0m.bitcoinium.MinerWidgetProvider;
import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.WidgetConfigureActivity;
import com.veken0m.bitcoinium.WidgetProvider;
import com.veken0m.bitcoinium.exchanges.ExchangeProperties;
import com.veken0m.utils.Constants;
import com.veken0m.utils.Utils;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class PreferencesActivity extends BasePreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
        addPreferencesFromResource(R.xml.pref_widgets);
        addPreferencesFromResource(R.xml.pref_price_widget_extensions);
        generateMinerDownAlertPreferences();
        addPreferencesFromResource(R.xml.pref_about);

        String[] sExchanges = getResources().getStringArray(getResources().getIdentifier("exchanges", "array", getPackageName()));
        PreferenceScreen defaultCurrencyPref = (PreferenceScreen) findPreference("defaultCurrencyPref");
        PreferenceCategory notificationSettingsPref = (PreferenceCategory) findPreference("notificationSettingsPref");

        for (String sExchange : sExchanges) {
            ExchangeProperties exchange = new ExchangeProperties(this, sExchange);
            String[] sCurrencies = exchange.getCurrencies();

            // Default Currency List
            ListPreference currencies = new ListPreference(this);
            currencies.setKey(exchange.getIdentifier() + "CurrencyPref");
            currencies.setEntries(sCurrencies);
            currencies.setEntryValues(sCurrencies);
            currencies.setTitle(sExchange + " " + getString(R.string.currency));
            currencies.setSummary(getString(R.string.pref_currency_displayed, sExchange));
            currencies.setDefaultValue(exchange.getDefaultCurrency());

            defaultCurrencyPref.addPreference(currencies);
        }

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        ComponentName widgetComponent = new ComponentName(this, WidgetProvider.class);
        int[] widgetIds = (widgetManager != null) ? widgetManager.getAppWidgetIds(widgetComponent) : new int[0];

        if (widgetIds.length > 0) {

            for (int appWidgetId : widgetIds) {

                // Obtain Widget configuration
                String widgetCurrency = WidgetConfigureActivity.loadCurrencyPref(this, appWidgetId);
                String widgetExchange = WidgetConfigureActivity.loadExchangePref(this, appWidgetId);
                if(widgetCurrency == null || widgetExchange == null)
                    continue;

                ExchangeProperties exchange = new ExchangeProperties(this, widgetExchange);

                // Create Checkbox
                CheckBoxPreference tickerCheckBox = new CheckBoxPreference(this);
                tickerCheckBox.setDefaultValue(false);
                tickerCheckBox.setKey(widgetExchange + widgetCurrency.replace("/", "") + "TickerPref");
                tickerCheckBox.setTitle(exchange.getExchangeName() + " - " + widgetCurrency);

                // Add to screen
                notificationSettingsPref.addPreference(tickerCheckBox);
            }

        } else {
            Preference pref = new Preference(this);
            pref.setLayoutResource(R.layout.custom_red_preference);
            pref.setTitle(getString(R.string.noWidgetFound));
            pref.setSummary(getString(R.string.pref_requires_widget));

            notificationSettingsPref.addPreference(pref);
        }

        // Widget Customization
        Preference widgetBackgroundColorPref = findPreference("widgetBackgroundColorPref");
        widgetBackgroundColorPref.setOnPreferenceChangeListener(this);
        Preference widgetMainTextColorPref = findPreference("widgetMainTextColorPref");
        widgetMainTextColorPref.setOnPreferenceChangeListener(this);
        Preference widgetSecondaryTextColorPref = findPreference("widgetSecondaryTextColorPref");
        widgetSecondaryTextColorPref.setOnPreferenceChangeListener(this);

        // Donation addresses
        Preference bitcoinDonationAddressPref = findPreference("bitcoinDonationAddressPref");
        bitcoinDonationAddressPref.setOnPreferenceClickListener(this);
        Preference litecoinDonationAddressPref = findPreference("litecoinDonationAddressPref");
        litecoinDonationAddressPref.setOnPreferenceClickListener(this);
        Preference dogecoinDonationAddressPref = findPreference("dogecoinDonationAddressPref");
        dogecoinDonationAddressPref.setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Tell the widgets to update pref_xtrader
        sendBroadcast(new Intent(this, WidgetProvider.class).setAction(Constants.REFRESH));
        sendBroadcast(new Intent(this, MinerWidgetProvider.class).setAction(Constants.REFRESH));
        sendBroadcast(new Intent(this, BalanceWidgetProvider.class).setAction(Constants.REFRESH));
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {

        Utils.copyDonationAddressToClipboard(getApplication(), preference.getSummary().toString());
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {

        preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(o))));
        return true;
    }
}
