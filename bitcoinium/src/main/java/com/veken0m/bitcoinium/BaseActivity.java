package com.veken0m.bitcoinium;

import android.app.Dialog;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.google.analytics.tracking.android.EasyTracker;
import com.veken0m.utils.Utils;

/**
 * Created by Michael on 18/02/14.
*/
public class BaseActivity extends SherlockFragmentActivity {

    public Dialog dialog = null;

    public void removeLoadingSpinner(int spinnerId) {

        LinearLayout loadingSpinner = (LinearLayout) findViewById(spinnerId);
        if(loadingSpinner != null) loadingSpinner.setVisibility(View.GONE);
    }

    public void startLoading(int tableId, int spinnerId) {

        TableLayout t1 = (TableLayout) findViewById(tableId);
        if (t1 != null) t1.removeAllViews();

        LinearLayout loadingSpinner = (LinearLayout) findViewById(spinnerId);
        if(loadingSpinner != null) loadingSpinner.setVisibility(View.VISIBLE);
    }

    public void notConnected(int spinnerId) {

        removeLoadingSpinner(spinnerId);
        // Display error Dialog
        if (dialog == null || !dialog.isShowing())
            dialog = Utils.errorDialog(this, getString(R.string.noInternetConnection), getString(R.string.internetConnection));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
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
