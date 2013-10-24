
package com.veken0m.cavirtex;

import com.veken0m.cavirtex.R;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class MinerWidgetConfigureActivity extends PreferenceActivity {

    private static final String PREFS_NAME = "com.veken0m.cavirtex.MinerWidgetProvider";
    private static final String PREF_MININGPOOL_KEY = "miningpool_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public MinerWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.minerwidget_preferences);

        // Set the result to CANCELED. This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        Preference OKpref = findPreference("OKpref");

        OKpref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                final Context context = MinerWidgetConfigureActivity.this;

                String pref_widgetMiningPool = prefs.getString(
                        "widgetMiningPoolPref", "BitMinter");

                // When the button is clicked, save the string in our prefs and
                // return that they clicked OK.
                saveMiningPoolPref(context, mAppWidgetId, pref_widgetMiningPool);

                // Set alarm to refresh widget at specified interval
                BaseWidgetProvider.setMinerWidgetAlarm(context);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                        mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
                return true;
            }

        });

    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveMiningPoolPref(Context context, int appWidgetId,
            String miningPool) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(
                PREFS_NAME, 0).edit();
        prefs.putString(PREF_MININGPOOL_KEY + appWidgetId, miningPool);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadMiningPoolPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String prefix = prefs
                .getString(PREF_MININGPOOL_KEY + appWidgetId, null);
        if (prefix != null) {
            return prefix;
        } else {
            return context.getString(R.string.default_miningpool);
        }

    }

}
