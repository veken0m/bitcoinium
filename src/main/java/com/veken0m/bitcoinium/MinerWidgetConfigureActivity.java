
package com.veken0m.bitcoinium;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.google.analytics.tracking.android.EasyTracker;
import com.veken0m.utils.Constants;

public class MinerWidgetConfigureActivity extends PreferenceActivity {

    private static final String PREF_MININGPOOL_KEY = "miningpool_";
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public MinerWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_miner_widget);
        addPreferencesFromResource(R.xml.pref_miner);
        addPreferencesFromResource(R.xml.pref_widgets);
        addPreferencesFromResource(R.xml.pref_create_widget);

        // Set the result to CANCELED. This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Find the widget id from the intent.
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
            finish();

        Preference OKpref = findPreference("OKpref");
        if (OKpref != null) {
            OKpref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Context context = MinerWidgetConfigureActivity.this;
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    String pref_widgetMiningPool = prefs.getString("widgetMiningPoolPref", Constants.DEFAULT_MINING_POOL);

                    saveMiningPoolPref(context, mAppWidgetId, pref_widgetMiningPool);

                    // Make sure we pass back the original appWidgetId
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);

                    finish();
                    return true;
                }
            });
        }
    }

    // Write the prefix to the SharedPreferences object for this widget
    private static void saveMiningPoolPref(Context context, int appWidgetId,String miningPool) {

        SharedPreferences.Editor prefs = context.getSharedPreferences(Constants.PREFS_NAME_MINER, 0).edit();
        prefs.putString(PREF_MININGPOOL_KEY + appWidgetId, miningPool);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadMiningPoolPref(Context context, int appWidgetId) {

        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME_MINER, 0);
        return prefs.getString(PREF_MININGPOOL_KEY + appWidgetId, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("googleAnalyticsPref", false))
            EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        sendBroadcast(new Intent(this, MinerWidgetProvider.class).setAction(Constants.REFRESH));
        EasyTracker.getInstance(this).activityStop(this);
    }

}
