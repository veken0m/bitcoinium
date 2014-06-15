package com.veken0m.bitcoinium.preferences;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class MinerPreferenceActivity extends BasePreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        generateMinerDownAlertPreferences();
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
}
