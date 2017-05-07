package com.veken0m.bitcoinium;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;

import com.veken0m.utils.Constants;

public class MinerWidgetConfigureActivity extends AppCompatActivity
{
    private static final String PREF_MININGPOOL_KEY = "miningpool_";

    public MinerWidgetConfigureActivity()
    {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    private static void saveMiningPoolPref(Context context, int appWidgetId, String miningPool)
    {
        SharedPreferences.Editor prefs = context.getSharedPreferences(Constants.PREFS_NAME_MINER, 0).edit();
        prefs.putString(PREF_MININGPOOL_KEY + appWidgetId, miningPool);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadMiningPoolPref(Context context, int appWidgetId)
    {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME_MINER, 0);
        return prefs.getString(PREF_MININGPOOL_KEY + appWidgetId, null);
    }

    public static class MinerWidgetConfigureFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener
    {
        private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Context context;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            context = getActivity().getApplicationContext();

            addPreferencesFromResource(R.xml.pref_miner_widget);
            addPreferencesFromResource(R.xml.pref_miner);

            // Set the result to CANCELED. This will cause the widget host to cancel
            // out of the widget placement if they press the back button.
            getActivity().setResult(RESULT_CANCELED);

            // Find the widget id from the intent.
            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null)
                mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            // If they gave us an intent without the widget id, just bail.
            if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
                getActivity().finish();

            Preference OKpref = findPreference("OKpref");
            OKpref.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference)
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String pref_widgetMiningPool = prefs.getString("widgetMiningPoolPref", Constants.DEFAULT_MINING_POOL);

            saveMiningPoolPref(context, mAppWidgetId, pref_widgetMiningPool);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            getActivity().setResult(RESULT_OK, resultValue);

            getActivity().finish();
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MinerWidgetConfigureFragment())
                .commit();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        sendBroadcast(new Intent(this, MinerWidgetProvider.class).setAction(Constants.REFRESH));
    }
}
