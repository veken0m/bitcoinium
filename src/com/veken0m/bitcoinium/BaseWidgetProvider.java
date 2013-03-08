package com.veken0m.bitcoinium;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.text.format.Time;

import com.veken0m.bitcoinium.WidgetProvider.UpdateService;

public class BaseWidgetProvider extends AppWidgetProvider {

	public static final String REFRESH = "com.veken0m.bitcoinium.REFRESH";
	public static final String GRAPH = "com.veken0m.bitcoinium.GRAPH";

	/**
	 * List of preference variables
	 */
	static Boolean pref_DisplayUpdates;
	static int pref_widgetRefreshFreq;
	static String pref_widgetBehaviour;
	static Boolean pref_PriceAlarm;
	static String pref_notifLimitLower;
	static String pref_notifLimitUpper;
	static String pref_currency;
	static String pref_main_currency;
	static Boolean pref_wakeupRefresh;
	static Boolean pref_alarmSound;
	static Boolean pref_alarmVibrate;
	static Boolean pref_ticker;
	static Boolean pref_widgetbidask;
	static Boolean pref_wifionly;
	static Boolean pref_alarmClock;

	// Service used to refresh widget
	static PendingIntent widgetRefreshService = null;

	protected static void readPreferences(Context context, String prefix,
			String defaultCurrency) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		pref_DisplayUpdates = prefs.getBoolean("checkboxPref", false);
		pref_widgetRefreshFreq = Integer.parseInt(prefs.getString("listPref",
				"30"));
		pref_wakeupRefresh = prefs.getBoolean("wakeupPref", true);
		pref_PriceAlarm = prefs.getBoolean("alarmPref", false);
		pref_notifLimitUpper = prefs.getString(prefix + "Upper", "999");
		pref_notifLimitLower = prefs.getString(prefix + "Lower", "0");
		pref_alarmSound = prefs.getBoolean("alarmSoundPref", false);
		pref_alarmVibrate = prefs.getBoolean("alarmVibratePref", false);
		pref_ticker = prefs.getBoolean(prefix + "TickerPref", false);
		pref_main_currency = prefs.getString(prefix + "CurrencyPref",
				defaultCurrency);
	}

	protected static void readAlarmPreferences(Context context) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		pref_DisplayUpdates = prefs.getBoolean("checkboxPref", false);
		pref_widgetRefreshFreq = Integer.parseInt(prefs.getString("listPref",
				"30"));
		pref_wakeupRefresh = prefs.getBoolean("wakeupPref", true);
		pref_PriceAlarm = prefs.getBoolean("alarmPref", false);
		pref_alarmSound = prefs.getBoolean("alarmSoundPref", false);
		pref_alarmVibrate = prefs.getBoolean("alarmVibratePref", false);
		pref_widgetbidask = prefs.getBoolean("bidasktogglePref", false);
		pref_wifionly = prefs.getBoolean("wifiRefreshOnlyPref", false);
		pref_alarmClock = prefs.getBoolean("alarmClockPref", false);

	}

	public void onDestoy(Context context) {
		final AlarmManager m = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		m.cancel(widgetRefreshService);
	}

	static void setAlarm(Context context) {
		readAlarmPreferences(context);
		final AlarmManager m1 = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		final Intent intent = new Intent(context, UpdateService.class);
		final Calendar TIME = Calendar.getInstance();
		TIME.set(Calendar.MINUTE, 0);
		TIME.set(Calendar.SECOND, 0);
		TIME.set(Calendar.MILLISECOND, 0);

		if (widgetRefreshService == null) {
			widgetRefreshService = PendingIntent.getService(context, 0, intent,
					PendingIntent.FLAG_CANCEL_CURRENT);
		}

		if (pref_wakeupRefresh) {
			m1.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(),
					1000 * 60 * pref_widgetRefreshFreq, widgetRefreshService);
		} else {
			m1.setRepeating(AlarmManager.RTC_WAKEUP, TIME.getTime().getTime(),
					1000 * 60 * pref_widgetRefreshFreq, widgetRefreshService);
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
		i.putExtra(AlarmClock.EXTRA_MESSAGE, "Bitcoinium alarm (please delete)");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    i.putExtra(AlarmClock.EXTRA_HOUR, hours);
	    i.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
	    i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        context.startActivity(i);
	}
	
	

	static void createNotification(Context ctxt, String lastPrice,
			String exchange, int BITCOIN_NOTIFY_ID) {
		String ns = Context.NOTIFICATION_SERVICE;

		String tickerText = "Bitcoin alarm value has been reached! \n"
				+ "Bitcoin valued at " + lastPrice + " on " + exchange;
		String contentTitle = "BTC @ " + lastPrice;
		String contentText = "Bitcoin value: " + lastPrice + " on " + exchange;

		int icon = R.drawable.bitcoin;
		NotificationManager mNotificationManager = (NotificationManager) ctxt
				.getSystemService(ns);
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		Intent notificationIntent = new Intent(ctxt, BaseWidgetProvider.class);
		PendingIntent contentIntent = PendingIntent.getActivity(ctxt, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(ctxt, contentTitle, contentText,
				contentIntent);

		if (pref_alarmSound) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		if (pref_alarmVibrate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}

		mNotificationManager.notify(BITCOIN_NOTIFY_ID, notification);
	}

	static void createPermanentNotification(Context ctxt, int icon,
			CharSequence contentTitle, CharSequence contentText,
			int BITCOIN_NOTIFY_ID) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) ctxt
				.getSystemService(ns);
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, null, when);

		Intent notificationIntent = new Intent(ctxt, BaseWidgetProvider.class);
		PendingIntent contentIntent = PendingIntent.getActivity(ctxt, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(ctxt, contentTitle, contentText,
				contentIntent);

		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_NO_CLEAR;

		mNotificationManager.notify(BITCOIN_NOTIFY_ID, notification);
	}

	static void removePermanentNotification(Context ctxt, int BITCOIN_NOTIFY_ID) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) ctxt
				.getSystemService(ns);
		mNotificationManager.cancel(BITCOIN_NOTIFY_ID);
	}

	/**
	 * createTicker creates a notification which only briefly appears in the
	 * ticker bar
	 * 
	 * @param Context
	 *            ctxt
	 * @param icon
	 *            (such as R.drawable.bitcoin)
	 * @param tickerText
	 *            (notification ticker)
	 */
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