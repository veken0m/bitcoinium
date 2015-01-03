package com.veken0m.bitcoinium.preferences;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;

import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.WidgetConfigureActivity;
import com.veken0m.bitcoinium.WidgetProvider;
import com.veken0m.bitcoinium.exchanges.ExchangeProperties;
import com.veken0m.utils.Constants;

public class TickerPreferencesActivity extends BasePreferenceActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_ticker_notification);
        populateNotificationSettingsScreen();
    }

    public void populateNotificationSettingsScreen()
    {
        PreferenceCategory notificationSettingsPref = (PreferenceCategory) findPreference("notificationSettingsPref");

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        ComponentName widgetComponent = new ComponentName(this, WidgetProvider.class);

        if (widgetManager != null)
        {
            int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
            for (int appWidgetId : widgetIds)
            {
                // Obtain Widget configuration
                String widgetCurrency = WidgetConfigureActivity.loadCurrencyPref(this, appWidgetId);
                String widgetExchange = WidgetConfigureActivity.loadExchangePref(this, appWidgetId);
                if (widgetCurrency == null || widgetExchange == null)
                    continue;

                ExchangeProperties exchange = new ExchangeProperties(this, widgetExchange);

                // Create Checkbox
                CheckBoxPreference tickerCheckBox = new CheckBoxPreference(this);
                tickerCheckBox.setDefaultValue(false);
                tickerCheckBox.setKey(widgetExchange + widgetCurrency.replace("/", "") + "TickerPref");
                tickerCheckBox.setTitle(exchange.getExchangeName() + " - " + widgetCurrency);

                // Add to screen
                notificationSettingsPref.addPreference(tickerCheckBox);
            }
        }
        else
        {
            notificationSettingsPref.addPreference(noWidgetFound());
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();

        // Tell the widgets to update
        sendBroadcast(new Intent(this, WidgetProvider.class).setAction(Constants.REFRESH));
    }
}
