
package com.veken0m.bitcoinium;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class PreferencesActivity extends PreferenceActivity {
    private static final String REFRESH = "com.veken0m.bitcoinium.REFRESH";

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
        addPreferencesFromResource(R.xml.pref_widgets);
        addPreferencesFromResource(R.xml.pref_price_alarm_category);
        addPreferencesFromResource(R.xml.pref_notfication_tickers);
        addPreferencesFromResource(R.xml.pref_miner);
        addPreferencesFromResource(R.xml.pref_orderbook);
        addPreferencesFromResource(R.xml.pref_graph);
        addPreferencesFromResource(R.xml.pref_about);

        Preference devEmailPref = findPreference("devEmailPref");
        Preference devTwitterPref = findPreference("devTwitterPref");
        Preference xchangeGithubPref = findPreference("xchangeGithubPref");
        Preference widgetBackgroundColorPref = findPreference("widgetBackgroundColorPref");
        Preference widgetMainTextColorPref = findPreference("widgetMainTextColorPref");
        Preference widgetSecondaryTextColorPref = findPreference("widgetSecondaryTextColorPref");
        Preference alarmSettings = findPreference("alarmSettingsPref");
        Preference donationAddressPref = findPreference("donationAddressPref");
        Preference bitcoiniumGithubPref = findPreference("bitcoiniumGithubPref");

        final Resources res = getResources();

        if (devEmailPref != null) {
            devEmailPref
                    .setOnPreferenceClickListener(new OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL,
                                    new String[]{
                                            res.getString(R.string.emailAddress)
                                    });
                            i.putExtra(Intent.EXTRA_SUBJECT,
                                    res.getString(R.string.app_name) + " Feedback");
                            startActivity(Intent.createChooser(i, "Send email"));

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
                                    .parse(res.getString(R.string.twitterAddress))));
                            return true;
                        }
                    });
        }


        if (xchangeGithubPref != null) {
            xchangeGithubPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse(res.getString(R.string.xchangeGithub))));
                    return true;
                }
            });
        }

        if (bitcoiniumGithubPref != null) {
            bitcoiniumGithubPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse(res.getString(R.string.bitcoiniumGithub))));
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

                    startActivity(new Intent(getApplicationContext(),
                            PriceAlarmPreferencesActivity.class));
                    return true;
                }
            });
        }

        if (donationAddressPref != null) {
            donationAddressPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    String donationAddress = getResources().getString(R.string.donationAddress);
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(donationAddress);
                    } else {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        android.content.ClipData clip = android.content.ClipData
                                .newPlainText("Donation Address", donationAddress);
                        clipboard.setPrimaryClip(clip);
                    }
                    Toast.makeText(getApplicationContext(), "Address copied to clipboard", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        // Tell the widgets to update preferences
        sendBroadcast(new Intent(this,
                WidgetProvider.class).setAction(REFRESH));

        sendBroadcast(new Intent(this,
                MinerWidgetProvider.class).setAction(REFRESH));

        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

}
