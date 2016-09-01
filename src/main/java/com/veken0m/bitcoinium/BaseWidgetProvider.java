package com.veken0m.bitcoinium;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.format.Time;
import android.util.Log;

import com.veken0m.bitcoinium.preferences.PreferencesActivity;
import com.veken0m.bitcoinium.preferences.PriceAlertPreferencesActivity;
import com.veken0m.utils.Utils;
import org.knowm.xchange.currency.CurrencyPair;

import java.util.HashMap;
import java.util.Map;

public class BaseWidgetProvider extends AppWidgetProvider
{
    static boolean pref_widgetBidAsk = false;
    static boolean pref_wifiOnly = false;
    static boolean pref_alarmClock = false;
    static boolean pref_tapToUpdate = false;
    static boolean pref_enableWidgetCustomization = false;
    static boolean pref_pricesInMilliBtc = false;

    static int pref_mainWidgetTextColor = R.color.widgetMainTextColor;
    static int pref_secondaryWidgetTextColor = R.color.widgetSecondaryTextColor;
    static int pref_backgroundWidgetColor = R.color.widgetBackgroundColor;
    static int pref_widgetRefreshSuccessColor = R.color.widgetRefreshSuccessColor;
    static int pref_widgetRefreshFailedColor = R.color.widgetRefreshFailedColor;
    static int pref_widgetPayoutUnits = 0;

    static SharedPreferences prefs = null;

    static Map<Integer, Float> prevPrice = new HashMap<>(); //
    /**
     * List of preference variables
     */
    private static boolean pref_alarmSound = false;
    private static boolean pref_alarmVibrate = false;
    private static String pref_notificationSound = null;

    static void readGeneralPreferences(Context context)
    {
        if (prefs == null) prefs = PreferenceManager.getDefaultSharedPreferences(context);
        readAlarmPreferences(context);

        pref_tapToUpdate = prefs.getBoolean("widgetTapUpdatePref", false);
        pref_wifiOnly = prefs.getBoolean("wifiRefreshOnlyPref", false);
        pref_pricesInMilliBtc = prefs.getBoolean("displayPricesInMilliBtcPref", true);
        pref_widgetBidAsk = prefs.getBoolean("bidasktogglePref", false);
        pref_widgetPayoutUnits = Integer.parseInt(prefs.getString("widgetMiningPayoutUnitPref", "0"));

        // Theming
        pref_enableWidgetCustomization = prefs.getBoolean("enableWidgetCustomizationPref", false);
        if (pref_enableWidgetCustomization)
        {
            pref_mainWidgetTextColor = prefs.getInt("widgetMainTextColorPref", R.color.widgetMainTextColor);
            pref_secondaryWidgetTextColor = prefs.getInt("widgetSecondaryTextColorPref", R.color.widgetSecondaryTextColor);
            pref_backgroundWidgetColor = prefs.getInt("widgetBackgroundColorPref", R.color.widgetBackgroundColor);
            pref_widgetRefreshSuccessColor = prefs.getInt("widgetRefreshSuccessColorPref", R.color.widgetRefreshSuccessColor);
            pref_widgetRefreshFailedColor = prefs.getInt("widgetRefreshFailedColorPref", R.color.widgetRefreshFailedColor);
        }
    }

    private static void readAlarmPreferences(Context context)
    {
        if (prefs == null) prefs = PreferenceManager.getDefaultSharedPreferences(context);

        pref_alarmSound = prefs.getBoolean("alarmSoundPref", false);
        pref_alarmVibrate = prefs.getBoolean("alarmVibratePref", false);
        pref_notificationSound = prefs.getString("notificationSoundPref", "DEFAULT_RINGTONE_URI");
        pref_alarmClock = prefs.getBoolean("alarmClockPref", false);
    }

    // Sets a repeating alarm on a class that extends IntentService
    static void setRefreshServiceAlarm(Context context, Class<? extends IntentService> cls)
    {
        // Get refresh settings
        if (prefs == null) prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int alarmType = prefs.getBoolean("wakeupPref", true) ? AlarmManager.RTC : AlarmManager.RTC_WAKEUP;
        int refreshInterval = Integer.parseInt(prefs.getString("refreshPref", "1800")) * 1000; // milliseconds

        Intent intent = new Intent(context, cls);
        PendingIntent refreshIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(alarmType, System.currentTimeMillis(), refreshInterval, refreshIntent);
    }

    static void setAlarmClock(Context context)
    {
        if (prefs == null) prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Editor editor = prefs.edit();
        editor.putBoolean("alarmClockPref", false);
        editor.commit();

        Time dtNow = new Time();
        dtNow.setToNow();

        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(AlarmClock.EXTRA_MESSAGE, "Bitcoinium alarm (delete)");
        i.putExtra(AlarmClock.EXTRA_HOUR, dtNow.hour);
        i.putExtra(AlarmClock.EXTRA_MINUTES, (dtNow.minute + 1));
        i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        context.startActivity(i);
    }

    static void createNotification(Context context, float last, String exchange, int notifyId, CurrencyPair pair)
    {
        String baseCurrency = pair.base.getCurrencyCode();
        String lastPrice = Utils.formatWidgetMoney(last, pair, true, pref_pricesInMilliBtc);

        Resources res = context.getResources();
        String tickerText = res.getString(R.string.msg_priceTickerNotif, baseCurrency, lastPrice, exchange);
        String contentTitle = res.getString(R.string.msg_priceTitleNotif, baseCurrency, lastPrice);
        String contentText = res.getString(R.string.msg_priceContentNotif, baseCurrency, lastPrice, exchange);

        Intent notificationIntent = new Intent(context, PriceAlertPreferencesActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        sendNotification(context, contentTitle, contentText, contentIntent, tickerText, notifyId, false, last);
    }

    static void createMinerDownNotification(Context context, String sMiningPool)
    {
        Resources res = context.getResources();
        String tickerText = res.getString(R.string.msg_minerDownTicker);
        String contentTitle = res.getString(R.string.msg_minerDownTitle);
        String contentText = res.getString(R.string.msg_minerDownContent, sMiningPool);

        Intent notificationIntent = new Intent(context, PreferencesActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        sendNotification(context, contentTitle, contentText, contentIntent, tickerText, sMiningPool.hashCode(), false, 0);
    }

    /**
     * Generic method to send notification using the NotificationCompat API.
     */
    public static void sendNotification(Context context,
                                        String contentTitle,
                                        String contentText,
                                        PendingIntent contentIntent,
                                        String contentTicker,
                                        int notifyId,
                                        boolean isOngoing,
                                        float lastPrice)
    {
        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentIntent(contentIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);

        if (contentTicker != null) builder.setTicker(contentTicker);

        Log.d("Previous price", prevPrice.toString());

        if (lastPrice != 0.0 && prevPrice.containsKey(notifyId))
        {
            int nCompare = Float.compare(lastPrice, prevPrice.get(notifyId));

            if (nCompare == 0)
            {
                builder.setSmallIcon(R.drawable.ic_stat);
                builder.setColor(Color.YELLOW);
            }
            else if (nCompare < 0)
            {
                builder.setSmallIcon(R.drawable.ic_stat_down);
                builder.setColor(Color.RED);
            }
            else if (nCompare > 0)
            {
                builder.setSmallIcon(R.drawable.ic_stat_up);
                builder.setColor(Color.GREEN);
            }
        }
        else
        {
            builder.setSmallIcon(R.drawable.ic_stat);
        }

        // This will show-up in the devices with Android 4.2 and above only
        //builder.setSubText("");

        if (isOngoing)
        {
            builder.setOngoing(true);
            notifyId += 100; // Ongoing ID is offset by 100;
        }
        else
        {
            if (pref_alarmVibrate) builder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
            if (pref_alarmSound) builder.setSound(Uri.parse(pref_notificationSound));
        }

        // Will display the notification in the notification bar
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);
        mNotificationManager.notify(notifyId, builder.build());
    }

    static void clearOngoingNotification(Context context, int notifyId)
    {
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);
        mNotificationManager.cancel(notifyId);
    }
}
