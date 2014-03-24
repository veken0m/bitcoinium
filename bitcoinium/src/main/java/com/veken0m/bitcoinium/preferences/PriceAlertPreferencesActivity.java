
package com.veken0m.bitcoinium.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.google.analytics.tracking.android.EasyTracker;
import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.WidgetProvider;
import com.veken0m.utils.Constants;

public class PriceAlertPreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_price_alert);

        Preference notificationRequestPref = findPreference("alertRequestPref");
        if (notificationRequestPref != null) {
            notificationRequestPref
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.emailAddress)});
                            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " - Price Alert Request");
                            startActivity(Intent.createChooser(i, getString(R.string.sendEmail)));

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
                WidgetProvider.class).setAction(Constants.REFRESH));

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
