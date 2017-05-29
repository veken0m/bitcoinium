package com.veken0m.bitcoinium.preferences;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.veken0m.bitcoinium.R;
import com.veken0m.utils.Constants;
import com.veken0m.utils.Utils;

public class AboutPreferencesActivity extends AppCompatActivity
{
    public static class AboutPreferencesFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener
    {
        Context context;

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            context = getActivity().getApplicationContext();

            addPreferencesFromResource(R.xml.pref_about);

            if (Constants.adSupported)
            {
                /*
                CheckBoxPreference tapToUpdatePref = (CheckBoxPreference) findPreference("widgetTapUpdatePref");
                tapToUpdatePref.setTitle(R.string.tapToUpdate_free);
                tapToUpdatePref.setDefaultValue(false);
                tapToUpdatePref.setEnabled(false);
                */

                Preference playstorePref = findPreference("playstorePref");
                Intent playstoreIntent = new Intent(Intent.ACTION_VIEW);
                playstoreIntent.setData(Uri.parse(getString(R.string.link_bitcoiniumPlayStore_free)));
                playstorePref.setIntent(playstoreIntent);

                Preference emailPref = findPreference("devEmailPref");
                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                emailIntent.setData(Uri.parse(getString(R.string.link_email_author_free)));
                emailPref.setIntent(emailIntent);
            }
            else
            {
                Preference prefBuyPrime = findPreference("prefBuyPrime");
                PreferenceGroup prefAboutCategory = (PreferenceGroup) findPreference("prefAboutCategory");
                if (prefBuyPrime != null && prefAboutCategory != null)
                    prefAboutCategory.removePreference(prefBuyPrime);
            }

            // Donation addresses
            findPreference("bitcoinDonationAddressPref").setOnPreferenceClickListener(this);
            findPreference("xchangeDonationAddressPref").setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference)
        {
            Utils.copyDonationAddressToClipboard(context, preference.getSummary().toString());
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new AboutPreferencesFragment())
                .commit();
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
}
