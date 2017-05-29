package com.veken0m.bitcoinium.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import com.veken0m.bitcoinium.R;

public class OrderbookPreferenceActivity extends AppCompatActivity
{
    public static class OrderbookPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_orderbook);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new OrderbookPreferenceFragment())
                .commit();
    }
}
