package com.veken0m.cavirtex.preferences;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.InputType;

import com.veken0m.cavirtex.R;
import com.veken0m.cavirtex.WidgetConfigureActivity;
import com.veken0m.cavirtex.WidgetProvider;
import com.veken0m.cavirtex.exchanges.Exchange;
import com.veken0m.utils.Constants;

public class PriceAlertPreferencesActivity extends BasePreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_price_alert);

        // Generate the alarm preferences
        PreferenceCategory alertSettingsPref = (PreferenceCategory) findPreference("alertSettingsPref");
        if (alertSettingsPref != null) {

            int numberWithDecimal = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
            String sAlertOverLimit = getString(R.string.pref_alert_over_summary);
            String sAlertUnderLimit = getString(R.string.pref_alert_under_summary);

            AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
            ComponentName widgetComponent = new ComponentName(this, WidgetProvider.class);
            int[] widgetIds = (widgetManager != null) ? widgetManager.getAppWidgetIds(widgetComponent) : new int[0];

            if (widgetIds.length > 0) {

                for (int appWidgetId : widgetIds) {

                    // Obtain Widget configuration
                    String widgetCurrency = WidgetConfigureActivity.loadCurrencyPref(this, appWidgetId);
                    String widgetExchange = WidgetConfigureActivity.loadExchangePref(this, appWidgetId);
                    if(widgetCurrency == null || widgetExchange == null)
                        continue;

                    Exchange exchange = new Exchange(this, widgetExchange);

                    PreferenceScreen alertLimits = getPreferenceManager().createPreferenceScreen(this);
                    alertLimits.setTitle(exchange.getExchangeName() + " - " + widgetCurrency);
                    String prefix = exchange.getIdentifier() + widgetCurrency.replace("/", "");

                    // Upper limit
                    EditTextPreference sHighInput = new EditTextPreference(this);
                    sHighInput.setDefaultValue("999999");
                    sHighInput.getEditText().setInputType(numberWithDecimal);
                    sHighInput.setKey(prefix + "Upper");
                    sHighInput.setTitle(getString(R.string.pref_alert_upper_limit, exchange.getExchangeName(), widgetCurrency));
                    sHighInput.setSummary(sAlertOverLimit);
                    alertLimits.addPreference(sHighInput);

                    // Lower limit
                    EditTextPreference sLowInput = new EditTextPreference(this);
                    sLowInput.setDefaultValue("0");
                    sLowInput.getEditText().setInputType(numberWithDecimal);
                    sLowInput.setKey(prefix + "Lower");
                    sLowInput.setTitle(getString(R.string.pref_alert_lower_threshold, exchange.getExchangeName(), widgetCurrency));
                    sLowInput.setSummary(sAlertUnderLimit);
                    alertLimits.addPreference(sLowInput);

                    alertSettingsPref.addPreference(alertLimits);
                }
            } else {
                Preference pref = new Preference(this);
                pref.setLayoutResource(R.layout.red_preference);
                pref.setTitle(getString(R.string.noWidgetFound));
                pref.setSummary(getString(R.string.pref_requires_widget));

                alertSettingsPref.addPreference(pref);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Tell the widgets to update preferences
        sendBroadcast(new Intent(this, WidgetProvider.class).setAction(Constants.REFRESH));
    }
}
