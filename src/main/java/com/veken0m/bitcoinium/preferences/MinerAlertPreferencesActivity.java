package com.veken0m.bitcoinium.preferences;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.veken0m.bitcoinium.R;

public class MinerAlertPreferencesActivity extends AppCompatActivity
{
    public static class MinerAlertPreferencesFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_miner_alert);

            PreferenceCategory minerDownAlertPref = (PreferenceCategory) findPreference("minerDownAlertPref");

            String[] sMiningPool = getResources().getStringArray(getResources().getIdentifier("miningpools", "array", getActivity().getPackageName()));
            for (String sMining : sMiningPool)
            {
                CheckBoxPreference alertCheckbox = new CheckBoxPreference(getActivity());
                alertCheckbox.setKey(sMining.toLowerCase().replaceAll("[ .-]", "") + "AlertPref");
                alertCheckbox.setTitle(getString(R.string.msg_minerDownAlert, sMining));
                alertCheckbox.setDefaultValue(false);

                minerDownAlertPref.addPreference(alertCheckbox);
            }
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
                .replace(android.R.id.content, new MinerAlertPreferencesFragment())
                .commit();
    }
}
