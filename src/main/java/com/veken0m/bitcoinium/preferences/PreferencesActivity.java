package com.veken0m.bitcoinium.preferences;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.veken0m.bitcoinium.BalanceWidgetProvider;
import com.veken0m.bitcoinium.MinerWidgetProvider;
import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.WidgetProvider;
import com.veken0m.utils.Constants;

import java.util.List;

import static com.veken0m.utils.ExchangeUtils.getAllDropdownItems;

public class PreferencesActivity extends AppCompatActivity
{
    public static class PreferencesFragment extends PreferenceFragment
    {
        Context context;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            context = getActivity().getApplicationContext();

            addPreferencesFromResource(R.xml.pref_general);

            ListPreference defaultPref = (ListPreference) findPreference("defaultExchangePref");
            Pair<List<String>, List<String>> exchanges = getAllDropdownItems(context);
            defaultPref.setEntries(exchanges.first.toArray(new CharSequence[exchanges.first.size()]));
            defaultPref.setEntryValues(exchanges.second.toArray(new CharSequence[exchanges.second.size()]));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed(); // Go back to calling activity
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop()
    {
        super.onStop();

        // Tell the widgets to update
        sendBroadcast(new Intent(this, WidgetProvider.class).setAction(Constants.REFRESH));
        sendBroadcast(new Intent(this, MinerWidgetProvider.class).setAction(Constants.REFRESH));
        sendBroadcast(new Intent(this, BalanceWidgetProvider.class).setAction(Constants.REFRESH));
    }
}
