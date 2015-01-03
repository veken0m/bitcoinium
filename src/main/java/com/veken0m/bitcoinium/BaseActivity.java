package com.veken0m.bitcoinium;

import android.app.Dialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.veken0m.utils.Utils;

public class BaseActivity extends ActionBarActivity
{
    public Dialog dialog = null;
    public SwipeRefreshLayout swipeLayout;

    void notConnected()
    {
        // Display error Dialog
        if (dialog == null || !dialog.isShowing())
            dialog = Utils.errorDialog(this, getString(R.string.error_noInternetConnection), getString(R.string.internetConnection));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action, menu);
        return true;
    }
}
