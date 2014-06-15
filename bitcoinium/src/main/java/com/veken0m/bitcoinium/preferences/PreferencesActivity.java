
package com.veken0m.bitcoinium.preferences;

import android.content.Intent;
import android.net.Uri;
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

import com.veken0m.bitcoinium.MinerWidgetProvider;
import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.WidgetProvider;
import com.veken0m.bitcoinium.exchanges.Exchange;
import com.veken0m.utils.Constants;
import com.veken0m.utils.Utils;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class PreferencesActivity extends BasePreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
        addPreferencesFromResource(R.xml.pref_widgets);
        addPreferencesFromResource(R.xml.pref_price_alert_category);
        addPreferencesFromResource(R.xml.pref_notfication_tickers);
        generateMinerDownAlertPreferences();
        addPreferencesFromResource(R.xml.pref_about);

        String[] sExchanges = getResources().getStringArray(getResources().getIdentifier("exchanges", "array", getPackageName()));
        PreferenceScreen defaultCurrencyPref = (PreferenceScreen) findPreference("defaultCurrencyPref");
        PreferenceCategory notificationSettingsPref = (PreferenceCategory) findPreference("notificationSettingsPref");

        for (String sExchange : sExchanges) {
            Exchange exchange = new Exchange(this, sExchange);
            String[] sCurrencies = exchange.getCurrencies();

            PreferenceScreen prefScreen = getPreferenceManager().createPreferenceScreen(this);
            prefScreen.setTitle(getString(R.string.pref_notif_tickers, sExchange));

            // Default Currency List
            ListPreference currencies = new ListPreference(this);
            currencies.setKey(exchange.getIdentifier() + "CurrencyPref");
            currencies.setEntries(sCurrencies);
            currencies.setEntryValues(sCurrencies);
            currencies.setTitle(sExchange + " " + getString(R.string.currency));
            currencies.setSummary(getString(R.string.pref_currency_displayed, sExchange));
            currencies.setDefaultValue(exchange.getDefaultCurrency());

            defaultCurrencyPref.addPreference(currencies);

            // Notification Drawer Ticker
            for (String sCurrency : sCurrencies) {

                CheckBoxPreference tickerCheckBox = new CheckBoxPreference(this);
                tickerCheckBox.setDefaultValue(false);
                tickerCheckBox.setKey(exchange.getIdentifier() + sCurrency.replace("/", "") + "TickerPref");
                tickerCheckBox.setTitle(sCurrency);
                prefScreen.addPreference(tickerCheckBox);
            }
            notificationSettingsPref.addPreference(prefScreen);
        }

        Preference devEmailPref = findPreference("devEmailPref");
        if (devEmailPref != null) {
            devEmailPref
                    .setOnPreferenceClickListener(new OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.emailAddress)});
                            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " - " + getString(R.string.feedback));
                            startActivity(Intent.createChooser(i, getString(R.string.sendEmail)));

                            return true;
                        }
                    });
        }

        Preference devTwitterPref = findPreference("devTwitterPref");
        if (devTwitterPref != null) {
            devTwitterPref
                    .setOnPreferenceClickListener(new OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                    .parse(getString(R.string.twitterAddress))));
                            return true;
                        }
                    });
        }

        Preference xchangeGithubPref = findPreference("xchangeGithubPref");
        if (xchangeGithubPref != null) {
            xchangeGithubPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse(getString(R.string.xchangeGithub))));
                    return true;
                }
            });
        }

        Preference playstoreGithubPref = findPreference("playstoreGithubPref");
        if (playstoreGithubPref != null) {
            playstoreGithubPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    return true;
                }
            });
        }

        Preference bitcoiniumGithubPref = findPreference("bitcoiniumGithubPref");
        if (bitcoiniumGithubPref != null) {
            bitcoiniumGithubPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse(getString(R.string.bitcoiniumGithub))));
                    return true;
                }
            });
        }

        // Widget Customization
        Preference widgetBackgroundColorPref = findPreference("widgetBackgroundColorPref");
        if (widgetBackgroundColorPref != null) {
            widgetBackgroundColorPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    preference.setSummary(ColorPickerPreference
                            .convertToARGB(Integer.valueOf(String
                                    .valueOf(newValue))));
                    return true;
                }
            });
        }

        Preference widgetMainTextColorPref = findPreference("widgetMainTextColorPref");
        if (widgetMainTextColorPref != null) {
            widgetMainTextColorPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    preference.setSummary(ColorPickerPreference
                            .convertToARGB(Integer.valueOf(String
                                    .valueOf(newValue))));
                    return true;
                }

            });
        }

        Preference widgetSecondaryTextColorPref = findPreference("widgetSecondaryTextColorPref");
        if (widgetSecondaryTextColorPref != null) {
            widgetSecondaryTextColorPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    preference.setSummary(ColorPickerPreference
                            .convertToARGB(Integer.valueOf(String
                                    .valueOf(newValue))));
                    return true;
                }

            });
        }

        Preference alarmSettings = findPreference("alarmSettingsPref");
        if (alarmSettings != null) {
            alarmSettings.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    startActivity(new Intent(getApplicationContext(), PriceAlertPreferencesActivity.class));
                    return true;
                }
            });
        }

        Preference bitcoinDonationAddressPref = findPreference("bitcoinDonationAddressPref");
        if (bitcoinDonationAddressPref != null) {
            bitcoinDonationAddressPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Utils.copyDonationAddressToClipboard(getApplication(), R.string.bitcoinDonationAddress);
                    return true;
                }
            });
        }

        Preference litecoinDonationAddressPref = findPreference("litecoinDonationAddressPref");
        if (litecoinDonationAddressPref != null) {
            litecoinDonationAddressPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Utils.copyDonationAddressToClipboard(getApplication(), R.string.litecoinDonationAddress);
                    return true;
                }
            });
        }

        Preference dogecoinDonationAddressPref = findPreference("dogecoinDonationAddressPref");
        if (dogecoinDonationAddressPref != null) {
            dogecoinDonationAddressPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Utils.copyDonationAddressToClipboard(getApplication(), R.string.dogecoinDonationAddress);
                    return true;
                }
            });
        }

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

        // Tell the widgets to update preferences
        sendBroadcast(new Intent(this,WidgetProvider.class).setAction(Constants.REFRESH));
        sendBroadcast(new Intent(this, MinerWidgetProvider.class).setAction(Constants.REFRESH));
    }
}
