
package com.veken0m.bitcoinium;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.text.format.Time;

import com.veken0m.bitcoinium.MinerWidgetProvider.MinerUpdateService;
import com.veken0m.bitcoinium.WidgetProvider.UpdateService;
import com.veken0m.bitcoinium.preferences.PreferencesActivity;
import com.veken0m.bitcoinium.preferences.PriceAlarmPreferencesActivity;
import com.veken0m.utils.Utils;
import com.xeiam.xchange.currency.CurrencyPair;

public class BaseWidgetProvider extends AppWidgetProvider {

    /**
     * List of preference variables
     */
    private static int pref_widgetRefreshFreq = 0;
    private static boolean pref_batterySavingMode = false;
    private static boolean pref_alarmSound = false;
    private static boolean pref_alarmVibrate = false;
    private static String pref_notificationSound = null;

    static boolean pref_priceAlarm = false;
    static boolean pref_enableTicker = false;
    static boolean pref_widgetBidAsk = false;
    static boolean pref_wifiOnly = false;
    static boolean pref_alarmClock = false;
    static boolean pref_tapToUpdate = false;

    static int pref_mainWidgetTextColor = R.color.widgetMainTextColor;
    static int pref_secondaryWidgetTextColor = R.color.widgetSecondaryTextColor;
    static int pref_backgroundWidgetColor = R.color.widgetBackgroundColor;
    static int pref_widgetRefreshSuccessColor = R.color.widgetRefreshSuccessColor;
    static int pref_widgetRefreshFailedColor = R.color.widgetRefreshFailedColor;
    static boolean pref_enableWidgetCustomization = false;
    static boolean pref_pricesInMilliBtc = false;
    static int pref_widgetMiningPayoutUnit = 0;

    // Service used to refresh widget
    private static PendingIntent widgetPriceWidgetRefreshService = null;
    private static PendingIntent widgetMinerWidgetRefreshService = null;

    static void readGeneralPreferences(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        readAlarmPreferences(context);

        pref_tapToUpdate = prefs.getBoolean("widgetTapUpdatePref", false);
        pref_wifiOnly = prefs.getBoolean("wifiRefreshOnlyPref", false);
        pref_pricesInMilliBtc = prefs.getBoolean("displayPricesInMilliBtcPref", true);
        pref_widgetBidAsk = prefs.getBoolean("bidasktogglePref", false);
        pref_enableTicker = prefs.getBoolean("enableTickerPref", false);
        pref_widgetMiningPayoutUnit = Integer.parseInt(prefs.getString("widgetMiningPayoutUnitPref", "0"));

        // Theming preferences
        pref_enableWidgetCustomization = prefs.getBoolean("enableWidgetCustomizationPref", false);
        if(pref_enableWidgetCustomization){
            pref_mainWidgetTextColor = prefs.getInt("widgetMainTextColorPref", R.color.widgetMainTextColor);
            pref_secondaryWidgetTextColor = prefs.getInt( "widgetSecondaryTextColorPref", R.color.widgetSecondaryTextColor);
            pref_backgroundWidgetColor = prefs.getInt("widgetBackgroundColorPref", R.color.widgetBackgroundColor);
            pref_widgetRefreshSuccessColor = prefs.getInt("widgetRefreshSuccessColorPref", R.color.widgetRefreshSuccessColor);
            pref_widgetRefreshFailedColor = prefs.getInt("widgetRefreshFailedColorPref", R.color.widgetRefreshFailedColor);
        }
    }

    private static void readAlarmPreferences(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        pref_widgetRefreshFreq = Integer.parseInt(prefs.getString("refreshPref", "1800"))*1000; // milliseconds
        pref_batterySavingMode = prefs.getBoolean("wakeupPref", true);
        pref_priceAlarm = prefs.getBoolean("alarmPref", false);
        pref_alarmSound = prefs.getBoolean("alarmSoundPref", false);
        pref_alarmVibrate = prefs.getBoolean("alarmVibratePref", false);
        pref_notificationSound = prefs.getString("notificationSoundPref","DEFAULT_RINGTONE_URI");
        pref_alarmClock = prefs.getBoolean("alarmClockPref", false);
    }

    static void setPriceWidgetAlarm(Context context) {
        readAlarmPreferences(context);

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, UpdateService.class);

        if (widgetPriceWidgetRefreshService == null)
            widgetPriceWidgetRefreshService = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        int alarmType = (pref_batterySavingMode) ? AlarmManager.RTC : AlarmManager.RTC_WAKEUP;
        alarmManager.setRepeating(alarmType, Utils.getCurrentTime(), pref_widgetRefreshFreq, widgetPriceWidgetRefreshService);
    }

    static void setMinerWidgetAlarm(Context context) {
        readAlarmPreferences(context);

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intentMiner = new Intent(context, MinerUpdateService.class);

        if (widgetMinerWidgetRefreshService == null)
            widgetMinerWidgetRefreshService = PendingIntent.getService(context, 0,intentMiner, PendingIntent.FLAG_CANCEL_CURRENT);

        int alarmType = (pref_batterySavingMode) ? AlarmManager.RTC : AlarmManager.RTC_WAKEUP;
        alarmManager.setRepeating(alarmType, Utils.getCurrentTime(), pref_widgetRefreshFreq, widgetMinerWidgetRefreshService);
    }

    static void setAlarmClock(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Editor editor = prefs.edit();
        editor.putBoolean("alarmClockPref", false);
        editor.commit();
        Time dtNow = (new Time());
        dtNow.setToNow();
        int hours = dtNow.hour;
        int minutes = dtNow.minute + 1;

        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(AlarmClock.EXTRA_MESSAGE, "Bitcoinium alarm (delete)");
        i.putExtra(AlarmClock.EXTRA_HOUR, hours);
        i.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        context.startActivity(i);
    }

    static void createNotification(Context context, float last, String exchange, int NOTIFY_ID, CurrencyPair pair) {

        String baseCurrency = pair.baseSymbol;
        String lastPrice = Utils.formatWidgetMoney(last, pair, true, pref_pricesInMilliBtc);

        Resources res = context.getResources();
        String tickerText = String.format(res.getString(R.string.priceTickerNotif), baseCurrency, lastPrice, exchange);
        String contentTitle = String.format(res.getString(R.string.priceTitleNotif), baseCurrency, lastPrice);
        String contentText = String.format(res.getString(R.string.priceContentNotif), baseCurrency, lastPrice, exchange);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.bitcoin, tickerText, System.currentTimeMillis());

        Intent notificationIntent = new Intent(context, PriceAlarmPreferencesActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        if (pref_alarmSound) notification.sound = Uri.parse(pref_notificationSound);

        if (pref_alarmVibrate) notification.defaults |= Notification.DEFAULT_VIBRATE;

        mNotificationManager.notify(NOTIFY_ID, notification);
    }

    static void createMinerDownNotification(Context context, String sMiningPool) {

        Resources res = context.getResources();
        String tickerText = res.getString(R.string.minerDownTickerNotif);
        String contentTitle = res.getString(R.string.minerDownTitleNotif);
        String contentText = String.format(res.getString(R.string.minerDownContentNotif), sMiningPool);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.bitcoin, tickerText, System.currentTimeMillis());

        Intent notificationIntent = new Intent(context, PreferencesActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        if (pref_alarmSound) notification.sound = Uri.parse(pref_notificationSound);

        if (pref_alarmVibrate) notification.defaults |= Notification.DEFAULT_VIBRATE;

        mNotificationManager.notify(sMiningPool.hashCode(), notification);
    }

    static void createPermanentNotification(Context context,
                                            CharSequence contentTitle, CharSequence contentText,
                                            int NOTIFY_ID) {

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.bitcoin, null, System.currentTimeMillis());

        Intent notificationIntent = new Intent(context, PreferencesActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        mNotificationManager.notify(100 + NOTIFY_ID, notification);
    }

    static void removePermanentNotification(Context context, int NOTIFY_ID) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
        mNotificationManager.cancel(100 + NOTIFY_ID);
    }

}
