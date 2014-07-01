package com.xeiam.xbtctrader;

import android.os.Bundle;

import com.veken0m.bitcoinium.preferences.BasePreferenceActivity;

public class PreferenceActivity extends BasePreferenceActivity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
	
}
