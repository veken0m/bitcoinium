package com.veken0m.bitcoinium.preferences;

import android.os.Build;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import com.veken0m.bitcoinium.R;

public class BasePreferenceActivity extends PreferenceActivity
{
    // Generate Mining Pool miner alerts pref_xtrader screen
    void generateMinerDownAlertPreferences()
    {
        addPreferencesFromResource(R.xml.pref_miner);

        String[] sPiscines = getResources().getStringArray(getResources().getIdentifier("miningpools", "array", getPackageName()));
        PreferenceCategory minerDownAlertPref = (PreferenceCategory) findPreference("minerDownAlertPref");

        if (minerDownAlertPref != null)
        {
            for (String sPiscine : sPiscines)
            {
                CheckBoxPreference alertCheckbox = new CheckBoxPreference(this);
                alertCheckbox.setKey(sPiscine.toLowerCase().replaceAll("[ .-]", "") + "AlertPref");
                alertCheckbox.setTitle(getString(R.string.msg_minerDownAlert, sPiscine));
                alertCheckbox.setDefaultValue(false);

                minerDownAlertPref.addPreference(alertCheckbox);
            }
        }
    }

    public Preference noWidgetFound()
    {
        Preference pref = new Preference(this);
        pref.setLayoutResource(R.layout.custom_red_preference);
        pref.setTitle(getString(R.string.noWidgetFound));
        pref.setSummary(getString(R.string.pref_requires_widget));

        return pref;
    }

    /* A nasty hack to fix a bug with PreferenceScreen background color on pre-Honeycomb devices with light themes */
    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
    {
        super.onPreferenceTreeClick(preferenceScreen, preference);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {
            if (preference != null)
                if (preference instanceof PreferenceScreen)
                    if (((PreferenceScreen) preference).getDialog() != null)
                        ((PreferenceScreen) preference).getDialog().getWindow().getDecorView().setBackgroundDrawable(this.getWindow().getDecorView().getBackground().getConstantState().newDrawable());
        }
        return false;
    }
}
