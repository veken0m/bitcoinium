package com.veken0m.bitcoinium.preferences;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.veken0m.bitcoinium.R;

public class OrderbookPreferenceActivity extends SherlockPreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.show();

        addPreferencesFromResource(R.xml.pref_orderbook);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("googleAnalyticsPref", false))
            EasyTracker.getInstance(this).activityStart(this);
    }
}
