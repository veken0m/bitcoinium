package com.veken0m.bitcoinium.preferences;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.WidgetConfigureActivity;
import com.veken0m.bitcoinium.WidgetProvider;
import com.veken0m.bitcoinium.exchanges.ExchangeProperties;
import com.veken0m.utils.Constants;

public class PriceAlertPreferencesActivity extends BasePreferenceActivity
{
    final int NUMBER_WITH_DECIMAL = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        generatePriceAlarmPreferenceScreen();
    }

    public void generatePriceAlarmPreferenceScreen()
    {
        addPreferencesFromResource(R.xml.pref_price_alert);
        // Generate the alarm
        final PreferenceCategory alertSettingsPref = (PreferenceCategory) findPreference("alertSettingsPref");
        if (alertSettingsPref != null)
        {
            final AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
            ComponentName widgetComponent = new ComponentName(this, WidgetProvider.class);

            if (widgetManager != null)
            {
                int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
                if (widgetIds.length == 0)
                    alertSettingsPref.addPreference(noWidgetFound());

                for (final int appWidgetId : widgetIds)
                {
                    // Obtain Widget configuration
                    String widgetCurrency = WidgetConfigureActivity.loadCurrencyPref(this, appWidgetId);
                    String widgetExchange = WidgetConfigureActivity.loadExchangePref(this, appWidgetId);
                    if (widgetCurrency == null || widgetExchange == null)
                    {
                        // Bad widget, destroy it.
                        AppWidgetHost host = new AppWidgetHost(getApplicationContext(), 0);
                        host.deleteAppWidgetId(appWidgetId);
                        continue;
                    }

                    ExchangeProperties exchange = new ExchangeProperties(this, widgetExchange);
                    String exchangeName = exchange.getExchangeName();

                    final PreferenceScreen alertLimits = getPreferenceManager().createPreferenceScreen(this);
                    alertLimits.setTitle(exchange.getExchangeName() + " - " + widgetCurrency);
                    String prefix = exchange.getIdentifier() + widgetCurrency.replace("/", "");

                    // Enable
                    alertLimits.addPreference(createEnablePref(prefix, exchangeName, widgetCurrency));
                    // Upper limit
                    alertLimits.addPreference(createAlarmLimitPref(prefix, exchangeName, widgetCurrency, true));
                    // Lower limit
                    alertLimits.addPreference(createAlarmLimitPref(prefix, exchangeName, widgetCurrency, false));

                    // Add category
                    PreferenceCategory prefCat = new PreferenceCategory(this);
                    prefCat.setTitle(getString(R.string.phantom_widget_removal));
                    alertLimits.addPreference(prefCat);

                    // Preference that allows user to deactivate a widget
                    Preference deactivateWidget = createDeactivateWidgetPref();
                    deactivateWidget.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
                    {
                        @Override
                        public boolean onPreferenceClick(Preference preference)
                        {

                            // Update the widget so the user doesn't think it's still active
                            RemoteViews views = populateWidgetView();
                            if (widgetManager != null)
                                widgetManager.updateAppWidget(appWidgetId, views);

                            // Delete the widget Id
                            AppWidgetHost host = new AppWidgetHost(getApplicationContext(), 0);
                            host.deleteAppWidgetId(appWidgetId);

                            // Remove preference, back out and notify user
                            alertSettingsPref.removePreference(alertLimits);
                            Toast.makeText(getBaseContext(), getString(R.string.toast_widget_deactivated), Toast.LENGTH_LONG).show();
                            alertLimits.getDialog().dismiss();

                            return false;
                        }
                    });
                    alertLimits.addPreference(deactivateWidget);

                    alertSettingsPref.addPreference(alertLimits);
                }
            }
        }
    }

    public Preference createDeactivateWidgetPref()
    {
        Preference deactivateWidget = new Preference(this);
        deactivateWidget.setTitle(getString(R.string.pref_widget_deactivate));
        deactivateWidget.setLayoutResource(R.layout.custom_red_preference);
        deactivateWidget.setSummary(getString(R.string.pref_widget_deactivate_summary));

        return deactivateWidget;
    }

    public RemoteViews populateWidgetView()
    {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.appwidget);
        views.setTextViewText(R.id.widgetHighText, "");
        views.setTextViewText(R.id.widgetLowText, "");
        views.setTextViewText(R.id.widgetLastText, getString(R.string.deactivated));
        views.setTextViewText(R.id.widgetVolText, getString(R.string.please_delete));

        return views;
    }

    /**
     * @param prefPrefix     the prefix prepended to the key
     * @param exchangeName   exchange name
     * @param widgetCurrency the currency pair
     * @return checkbox preference used to enabled the notifications
     */
    public CheckBoxPreference createEnablePref(String prefPrefix, String exchangeName, String widgetCurrency)
    {
        CheckBoxPreference enableCheckbox = new CheckBoxPreference(this);
        enableCheckbox.setDefaultValue(false);
        enableCheckbox.setKey(prefPrefix + "AlarmPref");
        enableCheckbox.setTitle(getString(R.string.pref_enable_alert_title, exchangeName, widgetCurrency));
        enableCheckbox.setSummary(getString(R.string.pref_enable_alert_summary, exchangeName, widgetCurrency));

        return enableCheckbox;
    }

    /**
     * @param prefPrefix     the prefix prepended to the key
     * @param exchangeName   exchange name
     * @param widgetCurrency the currency pair
     * @param upper          flag that determine if this is upper or lower alert limit
     * @return edittext preference used to input alert limits
     */
    public EditTextPreference createAlarmLimitPref(String prefPrefix, String exchangeName, String widgetCurrency, Boolean upper)
    {
        EditTextPreference editText = new EditTextPreference(this);
        editText.setDefaultValue((upper) ? Constants.ALERT_UPPER_DEFAULT : Constants.ALERT_LOWER_DEFAULT);
        editText.getEditText().setInputType(NUMBER_WITH_DECIMAL);
        editText.setKey(prefPrefix + ((upper) ? "Upper" : "Lower"));
        editText.setTitle(getString((upper) ? R.string.pref_alert_upper_limit : R.string.pref_alert_lower_limit, exchangeName, widgetCurrency));
        editText.setSummary(getString((upper) ? R.string.pref_alert_over_summary : R.string.pref_alert_under_summary));

        return editText;
    }

    @Override
    public void onStop()
    {
        super.onStop();

        // Tell the widgets to update
        sendBroadcast(new Intent(this, WidgetProvider.class).setAction(Constants.REFRESH));
    }
}
