package com.veken0m.bitcoinium.preferences;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.veken0m.bitcoinium.R;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class WidgetConfigurationPreferencesActivity extends AppCompatActivity
{
    public static class WidgetConfigurationPreferencesFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_widgets);

            // Widget Customization
            findPreference("widgetBackgroundColorPref").setOnPreferenceChangeListener(this);
            findPreference("widgetMainTextColorPref").setOnPreferenceChangeListener(this);
            findPreference("widgetSecondaryTextColorPref").setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o)
        {
            preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(o))));
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed(); // Go back to calling activity
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new WidgetConfigurationPreferencesFragment())
                .commit();
    }
}
