
package com.veken0m.cavirtex;

import com.veken0m.cavirtex.R;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PriceAlarmPreferencesActivity extends PreferenceActivity {
    public static final String REFRESH = "com.veken0m.cavirtex.REFRESH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.price_alarm_preferences);

    }

    @Override
    protected void onStop() {
        super.onStop();

        // Tell the widgets to update preferences
        Intent intent = new Intent(getApplicationContext(),
                WidgetProvider.class);
        intent.setAction(REFRESH);
        sendBroadcast(intent);
    }
}
