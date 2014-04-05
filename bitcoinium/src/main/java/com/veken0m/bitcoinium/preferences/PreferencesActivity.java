
package com.veken0m.bitcoinium.preferences;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.veken0m.bitcoinium.MinerWidgetProvider;
import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.WidgetProvider;
import com.veken0m.utils.Constants;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class PreferencesActivity extends SherlockPreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
        addPreferencesFromResource(R.xml.pref_widgets);
        addPreferencesFromResource(R.xml.pref_price_alert_category);
        addPreferencesFromResource(R.xml.pref_notfication_tickers);
        addPreferencesFromResource(R.xml.pref_miner);
        addPreferencesFromResource(R.xml.pref_about);

        Preference devEmailPref = findPreference("devEmailPref");
        Preference devTwitterPref = findPreference("devTwitterPref");
        Preference xchangeGithubPref = findPreference("xchangeGithubPref");
        Preference widgetBackgroundColorPref = findPreference("widgetBackgroundColorPref");
        Preference widgetMainTextColorPref = findPreference("widgetMainTextColorPref");
        Preference widgetSecondaryTextColorPref = findPreference("widgetSecondaryTextColorPref");
        Preference alarmSettings = findPreference("alarmSettingsPref");
        Preference bitcoinDonationAddressPref = findPreference("bitcoinDonationAddressPref");
        Preference litecoinDonationAddressPref = findPreference("litecoinDonationAddressPref");
        Preference dogecoinDonationAddressPref = findPreference("dogecoinDonationAddressPref");
        Preference bitcoiniumGithubPref = findPreference("bitcoiniumGithubPref");
        Preference playstoreGithubPref = findPreference("playstoreGithubPref");

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.show();

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

        Preference notificationRequestPref = findPreference("notificationRequestPref");
        if (notificationRequestPref != null) {
            notificationRequestPref
                    .setOnPreferenceClickListener(new OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.emailAddress)});
                            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " - Notification Ticker Request");
                            startActivity(Intent.createChooser(i, getString(R.string.sendEmail)));

                            return true;
                        }
                    });
        }

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

        if (playstoreGithubPref != null) {
            playstoreGithubPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    return true;
                }
            });
        }

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

        if (alarmSettings != null) {
            alarmSettings.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    startActivity(new Intent(getApplicationContext(), PriceAlertPreferencesActivity.class));
                    return true;
                }
            });
        }

        if (bitcoinDonationAddressPref != null) {
            bitcoinDonationAddressPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    String donationAddress = getResources().getString(R.string.bitcoinDonationAddress);
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(donationAddress);
                    } else {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(android.content.ClipData.newPlainText(getString(R.string.donationAddressText), donationAddress));
                    }
                    Context context = getApplicationContext();
                    if (context != null)
                        Toast.makeText(context, getString(R.string.addressCopiedToClipboard), Toast.LENGTH_SHORT).show();

                    return true;
                }
            });
        }

        if (litecoinDonationAddressPref != null) {
            litecoinDonationAddressPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    String donationAddress = getResources().getString(R.string.litecoinDonationAddress);
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(donationAddress);
                    } else {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(android.content.ClipData.newPlainText(getString(R.string.donationAddressText), donationAddress));
                    }
                    Context context = getApplicationContext();
                    if (context != null)
                        Toast.makeText(context, getString(R.string.addressCopiedToClipboard), Toast.LENGTH_SHORT).show();

                    return true;
                }
            });
        }

        if (dogecoinDonationAddressPref != null) {
            dogecoinDonationAddressPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    String donationAddress = getResources().getString(R.string.dogecoinDonationAddress);
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(donationAddress);
                    } else {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(android.content.ClipData.newPlainText(getString(R.string.donationAddressText), donationAddress));
                    }
                    Context context = getApplicationContext();
                    if (context != null)
                        Toast.makeText(context, getString(R.string.addressCopiedToClipboard), Toast.LENGTH_SHORT).show();

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
    protected void onStop() {
        super.onStop();

        // Tell the widgets to update preferences
        sendBroadcast(new Intent(this,
                WidgetProvider.class).setAction(Constants.REFRESH));

        sendBroadcast(new Intent(this,
                MinerWidgetProvider.class).setAction(Constants.REFRESH));

        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("googleAnalyticsPref", false)) {
            EasyTracker.getInstance(this).activityStart(this);
        }
    }

}
