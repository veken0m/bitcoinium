package com.veken0m.cavirtex;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
    public static final String PREFERENCES="com.veken0m.cavirtex.PREFERENCES";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            
            Preference primepref = (Preference) findPreference("primepref");
            primepref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                    public boolean onPreferenceClick(Preference preference) {
                    	Intent intent = new Intent(Intent.ACTION_VIEW);
                    	intent.setData(Uri.parse("market://details?id=com.veken0m.bitcoinium"));
                    	startActivity(intent);
                           return true;
                    }
            });
    }
    
    @Override
    protected void onStop() 
    {
        super.onStop();
        Intent intent =
                new Intent(
                    getApplicationContext(),
                    WatcherWidgetProvider.class);
            intent.setAction(PREFERENCES);
            sendBroadcast(intent);
            super.onStop();
            Intent intent2 =
                    new Intent(
                        getApplicationContext(),
                        WatcherWidgetProvider2.class);
                intent2.setAction(PREFERENCES);
                sendBroadcast(intent2);
    }
}
