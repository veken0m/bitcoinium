package com.xeiam.xbtctrader;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.veken0m.bitcoinium.R;

public class SettingsFragment extends PreferenceFragment
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_trader);
    }
}
