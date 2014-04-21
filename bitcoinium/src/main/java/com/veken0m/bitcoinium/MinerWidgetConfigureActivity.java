
package com.veken0m.bitcoinium;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.veken0m.utils.Constants;

public class MinerWidgetConfigureActivity extends SherlockPreferenceActivity {

    private static final String PREFS_NAME = "com.veken0m.bitcoinium.MinerWidgetProvider";
    private static final String PREF_MININGPOOL_KEY = "miningpool_";
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public MinerWidgetConfigureActivity() {
        super();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.widget_accept_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_widget_accept) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String pref_widgetMiningPool = prefs.getString("widgetMiningPoolPref", Constants.DEFAULT_MINING_POOL);

            saveMiningPoolPref(this, mAppWidgetId, pref_widgetMiningPool);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_miner_widget);
        addPreferencesFromResource(R.xml.pref_miner);
        addPreferencesFromResource(R.xml.pref_widgets);

        // Set the result to CANCELED. This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null)
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
            finish();
    }

    // Write the prefix to the SharedPreferences object for this widget
    private static void saveMiningPoolPref(Context context, int appWidgetId,String miningPool) {

        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_MININGPOOL_KEY + appWidgetId, miningPool);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadMiningPoolPref(Context context, int appWidgetId) {

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_MININGPOOL_KEY + appWidgetId, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("googleAnalyticsPref", false)) {
            EasyTracker.getInstance(this).activityStart(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Intent intent = new Intent(this, MinerWidgetProvider.class);
        intent.setAction(Constants.REFRESH);
        this.sendBroadcast(intent);

        EasyTracker.getInstance(this).activityStop(this);
    }

}
