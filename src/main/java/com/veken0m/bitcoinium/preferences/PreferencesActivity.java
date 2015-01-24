package com.veken0m.bitcoinium.preferences;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.support.v4.util.Pair;
import android.view.MenuItem;

import com.veken0m.bitcoinium.BalanceWidgetProvider;
import com.veken0m.bitcoinium.MinerWidgetProvider;
import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.WidgetProvider;
import com.veken0m.bitcoinium.exchanges.ExchangeProperties;
import com.veken0m.utils.Constants;
import com.veken0m.utils.Utils;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.List;

import static com.veken0m.utils.ExchangeUtils.getAllDropdownItems;

public class PreferencesActivity extends BasePreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener
{
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
        addPreferencesFromResource(R.xml.pref_widgets);
        addPreferencesFromResource(R.xml.pref_price_widget_extensions);
        generateMinerDownAlertPreferences();
        addPreferencesFromResource(R.xml.pref_about);


        if (Constants.adSupported)
        {
            CheckBoxPreference tapToUpdatePref = (CheckBoxPreference) findPreference("widgetTapUpdatePref");
            if (tapToUpdatePref != null)
            {
                tapToUpdatePref.setTitle(R.string.tapToUpdate_free);
                tapToUpdatePref.setDefaultValue(false);
                tapToUpdatePref.setEnabled(false);
            }

            Preference playstorePref = findPreference("playstorePref");
            if (playstorePref != null)
            {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.link_bitcoiniumPlayStore_free)));
                playstorePref.setIntent(i);
            }

            Preference emailPref = findPreference("devEmailPref");
            if (emailPref != null)
            {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_VIEW);
                emailIntent.setData(Uri.parse(getString(R.string.link_email_author_free)));
                emailPref.setIntent(emailIntent);
            }
        }
        else
        {
            Preference prefBuyPrime = findPreference("prefBuyPrime");
            PreferenceGroup prefAboutCategory = (PreferenceGroup) findPreference("prefAboutCategory");
            if (prefBuyPrime != null && prefAboutCategory != null)
                prefAboutCategory.removePreference(prefBuyPrime);
        }

        ListPreference defaultPref = (ListPreference) findPreference("defaultExchangePref");
        Pair<List<String>, List<String>> exchanges = getAllDropdownItems(this);
        defaultPref.setEntries(exchanges.first.toArray(new CharSequence[exchanges.first.size()]));
        defaultPref.setEntryValues(exchanges.second.toArray(new CharSequence[exchanges.second.size()]));

        populateDefaultCurrenciesScreen(exchanges.second);

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
        Preference xchangeDonationAddressPref = findPreference("xchangeDonationAddressPref");
        xchangeDonationAddressPref.setOnPreferenceClickListener(this);
    }

    public void populateDefaultCurrenciesScreen(List<String> sExchanges)
    {
        PreferenceScreen defaultCurrencyPref = (PreferenceScreen) findPreference("defaultCurrencyPref");

        for (String sExchange : sExchanges)
        {
            ExchangeProperties exchange = new ExchangeProperties(this, sExchange);
            String[] sCurrencies = exchange.getCurrencies();

            if (sCurrencies != null)
            {
                // Default Currency List
                ListPreference currencies = new ListPreference(this);
                currencies.setKey(exchange.getIdentifier() + "CurrencyPref");
                currencies.setEntries(sCurrencies);
                currencies.setEntryValues(sCurrencies);
                currencies.setTitle(exchange.getExchangeName() + " " + getString(R.string.currency));
                currencies.setSummary(getString(R.string.pref_currency_displayed, sExchange));
                currencies.setDefaultValue(exchange.getDefaultCurrency());

                defaultCurrencyPref.addPreference(currencies);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop()
    {
        super.onStop();

        // Tell the widgets to update pref_xtrader
        sendBroadcast(new Intent(this, WidgetProvider.class).setAction(Constants.REFRESH));
        sendBroadcast(new Intent(this, MinerWidgetProvider.class).setAction(Constants.REFRESH));
        sendBroadcast(new Intent(this, BalanceWidgetProvider.class).setAction(Constants.REFRESH));
    }


    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        Utils.copyDonationAddressToClipboard(getApplication(), preference.getSummary().toString());
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o)
    {
        preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(o))));
        return true;
    }
}
