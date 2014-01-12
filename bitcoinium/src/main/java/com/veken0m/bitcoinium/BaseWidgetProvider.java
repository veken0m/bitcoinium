
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.text.format.Time;

import com.veken0m.bitcoinium.MinerWidgetProvider.MinerUpdateService;
import com.veken0m.bitcoinium.WidgetProvider.UpdateService;
import com.veken0m.utils.Utils;
import com.xeiam.xchange.currency.CurrencyPair;

import java.util.Calendar;

class BaseWidgetProvider extends AppWidgetProvider {

    static final String REFRESH = "com.veken0m.bitcoinium.REFRESH";

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

    static int pref_mainWidgetTextColor = 0;
    static int pref_secondaryWidgetTextColor = 0;
    static int pref_backgroundWidgetColor = 0;
    static int pref_widgetRefreshSuccessColor = 0;
    static int pref_widgetRefreshFailedColor = 0;
    static boolean pref_enableWidgetCustomization = false;
    static boolean pref_pricesInMilliBtc = false;
    static int pref_widgetMiningPayoutUnit = 0;

    // Service used to refresh widget
    private static PendingIntent widgetPriceWidgetRefreshService = null;
    private static PendingIntent widgetMinerWidgetRefreshService = null;

    static void readAllWidgetPreferences(Context context) {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_enableTicker = prefs.getBoolean("enableTickerPref", false);

        readGeneralPreferences(context);
        readAlarmPreferences(context);
    }

    static void readGeneralPreferences(Context context) {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_widgetRefreshFreq = Integer.parseInt(prefs.getString(
                "refreshPref", "1800"));
        pref_batterySavingMode = prefs.getBoolean("wakeupPref", true);
        pref_tapToUpdate = prefs.getBoolean("widgetTapUpdatePref", false);
        pref_priceAlarm = prefs.getBoolean("alarmPref", false);
        pref_alarmSound = prefs.getBoolean("alarmSoundPref", false);
        pref_alarmVibrate = prefs.getBoolean("alarmVibratePref", false);
        pref_wifiOnly = prefs.getBoolean("wifiRefreshOnlyPref", false);
        pref_pricesInMilliBtc = prefs.getBoolean("displayPricesInMilliBtcPref", true);
        pref_notificationSound = prefs.getString("notificationSoundPref",
                "DEFAULT_RINGTONE_URI");
        pref_widgetBidAsk = prefs.getBoolean("bidasktogglePref", false);
        pref_alarmClock = prefs.getBoolean("alarmClockPref", false);

        pref_widgetMiningPayoutUnit = Integer.parseInt(prefs.getString("widgetMiningPayoutUnitPref", "0"));

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

    private static void readAlarmPreferences(Context context) {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_widgetRefreshFreq = Integer.parseInt(prefs.getString(
                "refreshPref", "1800"));
        pref_batterySavingMode = prefs.getBoolean("wakeupPref", true);
        pref_priceAlarm = prefs.getBoolean("alarmPref", false);
        pref_alarmSound = prefs.getBoolean("alarmSoundPref", false);
        pref_alarmVibrate = prefs.getBoolean("alarmVibratePref", false);
        pref_notificationSound = prefs.getString("notificationSoundPref",
                "DEFAULT_RINGTONE_URI");
        pref_alarmClock = prefs.getBoolean("alarmClockPref", false);

    }

    static void setPriceWidgetAlarm(Context context) {
        readAlarmPreferences(context);

        final AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, UpdateService.class);

        if (widgetPriceWidgetRefreshService == null) {
            widgetPriceWidgetRefreshService = PendingIntent.getService(context, 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        int alarmType = AlarmManager.RTC_WAKEUP;
        if (pref_batterySavingMode) {
            alarmType = AlarmManager.RTC;
        }

        final Calendar TIME = Calendar.getInstance();
        TIME.set(Calendar.MINUTE, 0);
        TIME.set(Calendar.SECOND, 0);
        TIME.set(Calendar.MILLISECOND, 0);

        alarmManager.setRepeating(alarmType, TIME.getTimeInMillis(),
                1000 * pref_widgetRefreshFreq, widgetPriceWidgetRefreshService);
    }

    static void setMinerWidgetAlarm(Context context) {
        readAlarmPreferences(context);

        final AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        final Intent intentMiner = new Intent(context, MinerUpdateService.class);

        if (widgetMinerWidgetRefreshService == null) {
            widgetMinerWidgetRefreshService = PendingIntent.getService(context, 0,
                    intentMiner, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        final Calendar TIME = Calendar.getInstance();
        TIME.set(Calendar.MINUTE, 0);
        TIME.set(Calendar.SECOND, 0);
        TIME.set(Calendar.MILLISECOND, 0);

        int alarmType = AlarmManager.RTC_WAKEUP;
        if (pref_batterySavingMode) {
            alarmType = AlarmManager.RTC;
        }

        alarmManager.setRepeating(alarmType, TIME.getTimeInMillis(),
                1000 * pref_widgetRefreshFreq, widgetMinerWidgetRefreshService);
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

    static Boolean checkWiFiConnected(Context ctxt) {

        ConnectivityManager connMgr = (ConnectivityManager) ctxt
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return (wifi != null && ((wifi.isAvailable()) && wifi.getDetailedState() == DetailedState.CONNECTED));
    }

    static void createNotification(Context ctxt, float last, String exchange, int NOTIFY_ID,
                                   CurrencyPair pair) {

        String baseCurrency = pair.baseCurrency;
        String lastPrice = Utils.formatWidgetMoney(last, pair, true, pref_pricesInMilliBtc);

        Resources res = ctxt.getResources();
        String tickerText = String.format(res.getString(R.string.priceTickerNotif), baseCurrency,
                lastPrice, exchange);
        String contentTitle = String.format(res.getString(R.string.priceTitleNotif), baseCurrency,
                lastPrice);
        String contentText = String.format(res.getString(R.string.priceContentNotif), baseCurrency,
                lastPrice, exchange);

        int icon = R.drawable.bitcoin;
        NotificationManager mNotificationManager = (NotificationManager) ctxt
                .getSystemService(Context.NOTIFICATION_SERVICE);
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);

        Intent notificationIntent = new Intent(ctxt,
                PriceAlarmPreferencesActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctxt, 0,
                notificationIntent, 0);

        notification.setLatestEventInfo(ctxt, contentTitle, contentText,
                contentIntent);

        if (pref_alarmSound)
            notification.sound = Uri.parse(pref_notificationSound);

        if (pref_alarmVibrate)
            notification.defaults |= Notification.DEFAULT_VIBRATE;

        mNotificationManager.notify(NOTIFY_ID, notification);
    }

    static void createMinerDownNotification(Context ctxt, String miningpool) {

        Resources res = ctxt.getResources();
        String tickerText = res.getString(R.string.minerDownTickerNotif);
        String contentTitle = res.getString(R.string.minerDownTitleNotif);
        String contentText = String.format(res.getString(R.string.minerDownContentNotif),
                miningpool);

        NotificationManager mNotifManager = (NotificationManager) ctxt
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.bitcoin, tickerText,
                System.currentTimeMillis());

        Intent notifIntent = new Intent(ctxt, PreferencesActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctxt, 0, notifIntent, 0);
        notification.setLatestEventInfo(ctxt, contentTitle, contentText, contentIntent);

        if (pref_alarmSound)
            notification.sound = Uri.parse(pref_notificationSound);

        if (pref_alarmVibrate)
            notification.defaults |= Notification.DEFAULT_VIBRATE;

        mNotifManager.notify(miningpool.hashCode(), notification);
    }

    static void createPermanentNotification(Context context,
                                            CharSequence contentTitle, CharSequence contentText,
                                            int NOTIFY_ID) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(ns);
        long when = System.currentTimeMillis();
        Notification notification = new Notification(R.drawable.bitcoin, null, when);

        Intent notificationIntent = new Intent(context, PreferencesActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText,
                contentIntent);

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        mNotificationManager.notify(100 + NOTIFY_ID, notification);
    }

    static void removePermanentNotification(Context ctxt, int NOTIFY_ID) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) ctxt
                .getSystemService(ns);
        mNotificationManager.cancel(100 + NOTIFY_ID);
    }
}
