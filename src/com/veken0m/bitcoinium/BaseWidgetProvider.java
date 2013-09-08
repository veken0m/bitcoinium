
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.text.format.Time;

import com.veken0m.bitcoinium.MinerWidgetProvider.MinerUpdateService;
import com.veken0m.bitcoinium.WidgetProvider.UpdateService;
import com.xeiam.xchange.currency.Currencies;

import java.util.Calendar;

public class BaseWidgetProvider extends AppWidgetProvider {

    public static final String REFRESH = "com.veken0m.bitcoinium.REFRESH";

    /**
     * List of preference variables
     */
    static int pref_widgetRefreshFreq;
    static Boolean pref_priceAlarm;
    static Boolean pref_displayUpdates;
    static Boolean pref_wakeupRefresh;
    static Boolean pref_alarmSound;
    static Boolean pref_alarmVibrate;
    static Boolean pref_enableTicker;
    static Boolean pref_widgetbidask;
    static Boolean pref_wifionly;
    static Boolean pref_alarmClock;
    static String pref_main_currency;
    static String pref_currency;
    static String pref_notificationSound;
    static Boolean pref_extremePowerSaver;
    static Boolean pref_tapToUpdate;

    static int pref_mainWidgetTextColor;
    static int pref_secondaryWidgetTextColor;
    static int pref_backgroundWidgetColor;
    static int pref_widgetRefreshSuccessColor;
    static int pref_widgetRefreshFailedColor;
    static Boolean pref_enableWidgetCustomization;

    // Service used to refresh widget
    static PendingIntent widgetRefreshService = null;
    static PendingIntent widgetMinerRefreshService = null;

    protected static void readAllWidgetPreferences(Context context, String prefix,
            String defaultCurrency) {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        
        pref_enableTicker = prefs.getBoolean("enableTickerPref", false);
        pref_main_currency = prefs.getString(prefix + "CurrencyPref",
                defaultCurrency);

        readGeneralPreferences(context);
        readAlarmPreferences(context);
    }

    protected static void readGeneralPreferences(Context context) {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_displayUpdates = prefs.getBoolean("checkboxPref", false);
        pref_widgetRefreshFreq = Integer.parseInt(prefs.getString(
                "refreshPref", "1800"));
        pref_wakeupRefresh = prefs.getBoolean("wakeupPref", true);
        //pref_extremePowerSaver = prefs.getBoolean("extremeSaverModePref", false);
        pref_tapToUpdate = prefs.getBoolean("widgetTapUpdatePref", false);
        pref_priceAlarm = prefs.getBoolean("alarmPref", false);
        pref_alarmSound = prefs.getBoolean("alarmSoundPref", false);
        pref_alarmVibrate = prefs.getBoolean("alarmVibratePref", false);
        pref_wifionly = prefs.getBoolean("wifiRefreshOnlyPref", false);
        pref_notificationSound = prefs.getString("notificationSoundPref",
                "DEFAULT_RINGTONE_URI");
        pref_widgetbidask = prefs.getBoolean("bidasktogglePref", false);
        pref_alarmClock = prefs.getBoolean("alarmClockPref", false);
        
        // Theming preferences
        pref_mainWidgetTextColor = prefs.getInt("widgetMainTextColorPref",
                R.color.widgetMainTextColor);
        pref_secondaryWidgetTextColor = prefs.getInt(
                "widgetSecondaryTextColorPref",
                R.color.widgetSecondaryTextColor);
        pref_backgroundWidgetColor = prefs.getInt("widgetBackgroundColorPref",
                R.color.widgetBackgroundColor);
        pref_widgetRefreshSuccessColor = prefs.getInt(
                "widgetRefreshSuccessColorPref",
                R.color.widgetRefreshSuccessColor);
        pref_widgetRefreshFailedColor = prefs.getInt(
                "widgetRefreshFailedColorPref",
                R.color.widgetRefreshFailedColor);
        pref_enableWidgetCustomization = prefs.getBoolean(
                "enableWidgetCustomizationPref", false);
    }

    protected static void readAlarmPreferences(Context context) {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_displayUpdates = prefs.getBoolean("checkboxPref", false);
        pref_widgetRefreshFreq = Integer.parseInt(prefs.getString(
                "refreshPref", "1800"));
        pref_wakeupRefresh = prefs.getBoolean("wakeupPref", true);
        pref_priceAlarm = prefs.getBoolean("alarmPref", false);
        pref_alarmSound = prefs.getBoolean("alarmSoundPref", false);
        pref_alarmVibrate = prefs.getBoolean("alarmVibratePref", false);
        pref_notificationSound = prefs.getString("notificationSoundPref",
                "DEFAULT_RINGTONE_URI");
        pref_alarmClock = prefs.getBoolean("alarmClockPref", false);

    }

    public void onDestoy(Context context) {
        final AlarmManager m = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        
        m.cancel(widgetMinerRefreshService);
        m.cancel(widgetRefreshService);
    }

    static void setAlarm(Context context) {
        readAlarmPreferences(context);
        final AlarmManager m1 = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        final AlarmManager m2 = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, UpdateService.class);
        final Intent intentMiner = new Intent(context, MinerUpdateService.class);
        final Calendar TIME = Calendar.getInstance();
        TIME.set(Calendar.MINUTE, 0);
        TIME.set(Calendar.SECOND, 0);
        TIME.set(Calendar.MILLISECOND, 0);

        if (widgetRefreshService == null) {
            widgetRefreshService = PendingIntent.getService(context, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
        }
        if (widgetMinerRefreshService == null) {
            widgetMinerRefreshService = PendingIntent.getService(context, 0,
                    intentMiner, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        if (pref_wakeupRefresh) {
            m1.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(),
                    1000 * pref_widgetRefreshFreq, widgetRefreshService);
            m2.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(),
                    1000 * pref_widgetRefreshFreq, widgetMinerRefreshService);
        } else {
            m1.setRepeating(AlarmManager.RTC_WAKEUP, TIME.getTime().getTime(),
                    1000 * pref_widgetRefreshFreq, widgetRefreshService);
            m2.setRepeating(AlarmManager.RTC_WAKEUP, TIME.getTime().getTime(),
                    1000 * pref_widgetRefreshFreq, widgetMinerRefreshService);
        }
    }

    static void setAlarmClock(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        Editor editor = prefs.edit();
        editor.putBoolean("alarmClockPref", false);
        editor.commit();
        Time dtNow = new Time();
        dtNow.setToNow();
        int hours = dtNow.hour;
        int minutes = dtNow.minute + 1;
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_MESSAGE, "Bitcoinium alarm (delete)");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(AlarmClock.EXTRA_HOUR, hours);
        i.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        context.startActivity(i);
    }

    public static Boolean checkWiFiConnected(Context ctxt) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) ctxt
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            return (wifi.isAvailable() && wifi.getDetailedState() == DetailedState.CONNECTED);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static void createNotification(Context ctxt, String lastPrice,
            String exchange, int BITCOIN_NOTIFY_ID, String currencyPair) {
        String ns = Context.NOTIFICATION_SERVICE;

        String baseCurrency = Currencies.BTC;

        if (currencyPair.contains("/")) {
            baseCurrency = currencyPair.substring(0, 3);
        }

        String tickerText = baseCurrency + " alarm value has been reached! \n"
                + baseCurrency + " valued at " + lastPrice + " on " + exchange;
        String contentTitle = baseCurrency + " @ " + lastPrice;
        String contentText = baseCurrency + " value: " + lastPrice + " on " + exchange;

        int icon = R.drawable.bitcoin;
        NotificationManager mNotificationManager = (NotificationManager) ctxt
                .getSystemService(ns);
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);

        Intent notificationIntent = new Intent(ctxt,
                PriceAlarmPreferencesActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctxt, 0,
                notificationIntent, 0);

        notification.setLatestEventInfo(ctxt, contentTitle, contentText,
                contentIntent);

        if (pref_alarmSound) {
            notification.sound = Uri.parse(pref_notificationSound);
        }

        if (pref_alarmVibrate) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        mNotificationManager.notify(BITCOIN_NOTIFY_ID, notification);
    }

    static void createMinerDownNotification(Context ctxt, String miningpool,
            int BITCOIN_NOTIFY_ID) {
        String ns = Context.NOTIFICATION_SERVICE;

        String tickerText = "Bitcoin Miner down!";
        String contentTitle = "Bitcoin miner down";
        String contentText = "Miner on " + miningpool + " is down";

        int icon = R.drawable.bitcoin;
        NotificationManager mNotificationManager = (NotificationManager) ctxt
                .getSystemService(ns);
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);

        Intent notificationIntent = new Intent(ctxt, PreferencesActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(ctxt, 0,
                notificationIntent, 0);

        notification.setLatestEventInfo(ctxt, contentTitle, contentText,
                contentIntent);

        if (pref_alarmSound) {
            notification.sound = Uri.parse(pref_notificationSound);
        }

        if (pref_alarmVibrate) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        mNotificationManager.notify(BITCOIN_NOTIFY_ID * 100, notification);
    }

    static void createPermanentNotification(Context ctxt, int icon,
            CharSequence contentTitle, CharSequence contentText,
            int BITCOIN_NOTIFY_ID) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) ctxt
                .getSystemService(ns);
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, null, when);

        Intent notificationIntent = new Intent(ctxt, PreferencesActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctxt, 0,
                notificationIntent, 0);

        notification.setLatestEventInfo(ctxt, contentTitle, contentText,
                contentIntent);

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        mNotificationManager.notify(100 + BITCOIN_NOTIFY_ID, notification);
    }

    static void removePermanentNotification(Context ctxt, int BITCOIN_NOTIFY_ID) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) ctxt
                .getSystemService(ns);
        mNotificationManager.cancel(100 + BITCOIN_NOTIFY_ID);
    }

    static void createTicker(Context ctxt, int icon, CharSequence tickerText) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) ctxt
                .getSystemService(ns);
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);

        Intent notificationIntent = new Intent(ctxt, BaseWidgetProvider.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctxt, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(ctxt, null, null, contentIntent);
        mNotificationManager.notify(0, notification);
        mNotificationManager.cancel(0);
    }

}
