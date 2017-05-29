package com.xeiam.xbtctrader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.veken0m.bitcoinium.R;

public class PreferenceActivity extends AppCompatActivity
{
    public static class PrefFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_trader);

            Preference prefLicense = findPreference("pref_license");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (!prefs.getBoolean("pref_licenseViewed", false))
                showLicenseDialog(getActivity());

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
                    prefs.apply();
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
                        getActivity().finish();
                    }
                }
            });

            ab.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragment())
                .commit();
    }
}
