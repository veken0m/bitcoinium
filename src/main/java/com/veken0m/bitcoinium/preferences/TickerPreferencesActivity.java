package com.veken0m.bitcoinium.preferences;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.WidgetConfigureActivity;
import com.veken0m.bitcoinium.WidgetProvider;
import com.veken0m.bitcoinium.exchanges.ExchangeProperties;
import com.veken0m.utils.Constants;

public class TickerPreferencesActivity extends AppCompatActivity
{
    public static class TickerPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_ticker_notification);

            populateNotificationSettingsScreen(getActivity().getApplicationContext());
        }

        public void populateNotificationSettingsScreen(Context context)
        {
            PreferenceCategory notificationSettingsPref = (PreferenceCategory) findPreference("notificationSettingsPref");

            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
            ComponentName widgetComponent = new ComponentName(context, WidgetProvider.class);

            if (widgetManager != null)
            {
                int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
                if (widgetIds.length == 0) {
                    Preference pref = new Preference(context);
                    pref.setLayoutResource(R.layout.custom_red_preference);
                    pref.setTitle(getString(R.string.noWidgetFound));
                    pref.setSummary(getString(R.string.pref_requires_widget));

                    notificationSettingsPref.addPreference(pref);
                }

                for (int appWidgetId : widgetIds)
                {
                    // Obtain Widget configuration
                    String widgetCurrency = WidgetConfigureActivity.loadCurrencyPref(context, appWidgetId);
                    String widgetExchange = WidgetConfigureActivity.loadExchangePref(context, appWidgetId);
                    if (widgetCurrency == null || widgetExchange == null)
                    {
                        // Bad widget, destroy it.
                        AppWidgetHost host = new AppWidgetHost(context, 0);
                        host.deleteAppWidgetId(appWidgetId);
                        continue;
                    }

                    ExchangeProperties exchange = new ExchangeProperties(context, widgetExchange);

                    // Create Checkbox
                    CheckBoxPreference tickerCheckBox = new CheckBoxPreference(context);
                    tickerCheckBox.setDefaultValue(false);
                    tickerCheckBox.setKey(widgetExchange + widgetCurrency.replace("/", "") + "TickerPref");
                    tickerCheckBox.setTitle(exchange.getExchangeName() + " - " + widgetCurrency);

                    // Add to screen
                    notificationSettingsPref.addPreference(tickerCheckBox);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new TickerPreferenceFragment())
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
    }
}
