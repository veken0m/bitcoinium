package com.xeiam.xbtctrader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.preferences.BasePreferenceActivity;

public class PreferenceActivity extends BasePreferenceActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.

        /*getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();*/

        addPreferencesFromResource(R.xml.pref_xtrader);

        Preference prefLicense = findPreference("pref_license");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("pref_licenseViewed", false))
            showLicenseDialog(this);

        prefLicense.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {

                showLicenseDialog(preference.getContext());

                return false;
            }
        });
    }

    public void showLicenseDialog(final Context context)
    {
        AlertDialog.Builder ab = new AlertDialog.Builder(context);
        ab.setTitle("License");
        ab.setMessage(R.string.license);

        ab.setPositiveButton("Accept", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
                prefs.putBoolean("pref_licenseViewed", true);
                prefs.commit();
            }
        });

        ab.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                if (!prefs.getBoolean("pref_licenseViewed", false))
                {
                    Toast.makeText(context, "Please accept the License to proceed", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        ab.show();
    }
}
