package com.veken0m.utils;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.veken0m.cavirtex.WidgetConfigureActivity;
import com.veken0m.cavirtex.WidgetProvider;
import com.veken0m.cavirtex.exchanges.ExchangeProperties;
import com.xeiam.xchange.currency.CurrencyPair;

// This class will contain all conversions required to maintain backwards compatibility with settings from previous version of the app
public class CompatUtil {

    static public void convertAlarmPrefs(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(!prefs.getBoolean("alarmPref", false))
            return;

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(context, WidgetProvider.class);

        int widgetIds[] = widgetManager.getAppWidgetIds(widgetComponent);
        // This preference was removed, if was previously enabled update it
        for (int appWidgetId : widgetIds) {
            String exchangePref = WidgetConfigureActivity.loadExchangePref(context, appWidgetId);
            if (exchangePref == null) continue; // skip to next widget

            ExchangeProperties exchange = new ExchangeProperties(context, exchangePref);
            String currencyPair = WidgetConfigureActivity.loadCurrencyPref(context, appWidgetId);
            CurrencyPair pair = CurrencyUtils.stringToCurrencyPair(currencyPair);

            prefs.edit().putBoolean(exchange.getIdentifier() + pair.baseSymbol + pair.counterSymbol + "AlarmPref", true).commit();
        }
        prefs.edit().remove("alarmPref").commit();
    }
}
