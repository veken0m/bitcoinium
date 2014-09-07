package com.veken0m.bitcoinium;

import android.app.Dialog;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.google.analytics.tracking.android.EasyTracker;
import com.veken0m.utils.Utils;

public class BaseActivity extends ActionBarActivity {

    public Dialog dialog = null;
    public SwipeRefreshLayout swipeLayout;

    void removeLoadingSpinner(int spinnerId) {

        LinearLayout loadingSpinner = (LinearLayout) findViewById(spinnerId);
        if (loadingSpinner != null) loadingSpinner.setVisibility(View.GONE);
    }

    void startLoading(int tableId, int spinnerId) {

        TableLayout t1 = (TableLayout) findViewById(tableId);
        if (t1 != null) t1.removeAllViews();

        LinearLayout loadingSpinner = (LinearLayout) findViewById(spinnerId);
        if (loadingSpinner != null) loadingSpinner.setVisibility(View.VISIBLE);
    }

    void notConnected(int spinnerId) {

        removeLoadingSpinner(spinnerId);
        // Display error Dialog
        if (dialog == null || !dialog.isShowing())
            dialog = Utils.errorDialog(this, getString(R.string.error_noInternetConnection), getString(R.string.internetConnection));
    }

    void notConnected() {
        // Display error Dialog
        if (dialog == null || !dialog.isShowing())
            dialog = Utils.errorDialog(this, getString(R.string.error_noInternetConnection), getString(R.string.internetConnection));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action, menu);
        return true;
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
        EasyTracker.getInstance(this).activityStop(this);
    }

}
