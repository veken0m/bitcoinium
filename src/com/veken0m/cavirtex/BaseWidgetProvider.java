package com.veken0m.cavirtex;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.veken0m.cavirtex.WidgetProvider.UpdateService;

public class BaseWidgetProvider extends AppWidgetProvider {

	/**
	 * This constant is what we send to ourself to force a refresh
	 */
	public static final String REFRESH = "com.veken0m.cavirtex.REFRESH";
	public static final String PREFERENCES = "com.veken0m.cavirtex.PREFERENCES";
	public static final String OPENMENU = "com.veken0m.cavirtex.OPENMENU";
	public static final String GRAPH = "com.veken0m.cavirtex.GRAPH";
	public static final String NOTHING = "com.veken0m.cavirtex.NOTHING";

	/**
	 * List of IDs for notifications
	 */
	public static final int BITCOIN_NOTIFY_ID = 0;
	public static final int NOTIFY_ID_VIRTEX = 1;
	public static final int NOTIFY_ID_MTGOX = 2;

	/**
	 * List of preference variables
	 */
	static Boolean pref_DisplayUpdates = false;
	static int pref_widgetRefreshFreq = 30;
	static String pref_widgetBehaviour;
	static Boolean pref_PriceAlarm = false;
	static String pref_virtexUpper;
	static String pref_virtexLower;
	static String pref_mtgoxUpper;
	static String pref_mtgoxLower;
	static String pref_mtgoxCurrency;
	static Boolean pref_wakeupRefresh = false;
	static Boolean pref_alarmSound;
	static Boolean pref_alarmVibrate;
	static Boolean pref_virtexTicker;
	static Boolean pref_mtgoxTicker;

	static PendingIntent service = null; // used for AlarmManager
	static PendingIntent service2 = null; // used for AlarmManager

	/**
	 * When we receive an Intent, we will either force a refresh if it matches
	 * REFRESH, or pass it on to our superclass
	 */

	/**
	 * createNotification creates a notification which stays in the notification
	 * bar till removed
	 * 
	 * @param Context
	 *            ctxt
	 * @param icon
	 *            (such as R.drawable.bitcoin)
	 * @param tickerText
	 *            (notification ticker)
	 * @param contentTitle
	 *            (title of notification)
	 * @param contentText
	 *            (details of notification)
	 */

	protected static void readPreferences(Context context) {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences pPrefs,
					String key) {
				pref_DisplayUpdates = pPrefs.getBoolean("checkboxPref", false);
				pref_widgetRefreshFreq = Integer.parseInt(pPrefs.getString(
						"listPref", "30"));
				pref_wakeupRefresh = pPrefs.getBoolean("wakeupPref", true);
				pref_PriceAlarm = pPrefs.getBoolean("alarmPref", false);
				pref_virtexUpper = pPrefs.getString("virtexUpper", "");
				pref_virtexLower = pPrefs.getString("virtexLower", "");
				pref_mtgoxUpper = pPrefs.getString("mtgoxUpper", "");
				pref_mtgoxLower = pPrefs.getString("mtgoxLower", "");
				pref_alarmSound = pPrefs.getBoolean("alarmSoundPref", false);
				pref_alarmVibrate = pPrefs
						.getBoolean("alarmVibratePref", false);
				pref_virtexTicker = pPrefs
						.getBoolean("virtexTickerPref", false);
				pref_mtgoxTicker = pPrefs.getBoolean("mtgoxTickerPref", false);
				pref_mtgoxCurrency = pPrefs.getString("mtgoxCurrencyPref",
						"USD");

			}
		};

		prefs.registerOnSharedPreferenceChangeListener(prefListener);

		pref_DisplayUpdates = prefs.getBoolean("checkboxPref", false);
		pref_widgetRefreshFreq = Integer.parseInt(prefs.getString("listPref",
				"30"));
		pref_wakeupRefresh = prefs.getBoolean("wakeupPref", true);
		pref_PriceAlarm = prefs.getBoolean("alarmPref", false);
		pref_virtexUpper = prefs.getString("virtexUpper", "");
		pref_virtexLower = prefs.getString("virtexLower", "");
		pref_mtgoxUpper = prefs.getString("mtgoxUpper", "");
		pref_mtgoxLower = prefs.getString("mtgoxLower", "");
		pref_alarmSound = prefs.getBoolean("alarmSoundPref", false);
		pref_alarmVibrate = prefs.getBoolean("alarmVibratePref", false);
		pref_virtexTicker = prefs.getBoolean("virtexTickerPref", false);
		pref_mtgoxTicker = prefs.getBoolean("mtgoxTickerPref", false);
		pref_mtgoxCurrency = prefs.getString("mtgoxCurrencyPref", "USD");
	}

	public void onDestoy(Context context) {
		final AlarmManager m = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		m.cancel(service);
		m.cancel(service2);
	}

	// Might be needed to stop the AlarmManager
	public void onDisabled(Context context) {
		// final AlarmManager m = (AlarmManager)
		// context.getSystemService(Context.ALARM_SERVICE);

		// m.cancel(service);
	}

	static void setAlarm(Context context) {
		readPreferences(context);
		final AlarmManager m1 = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		final Intent i = new Intent(context, UpdateService.class);
		final Calendar TIME = Calendar.getInstance();
		TIME.set(Calendar.MINUTE, 0);
		TIME.set(Calendar.SECOND, 0);
		TIME.set(Calendar.MILLISECOND, 0);

		if (service2 == null) {
			service2 = PendingIntent.getService(context, 0, i,
					PendingIntent.FLAG_CANCEL_CURRENT);
		}

		if (pref_wakeupRefresh) {
			m1.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(),
					1000 * 60 * pref_widgetRefreshFreq, service2);
		} else {
			m1.setRepeating(AlarmManager.RTC_WAKEUP, TIME.getTime().getTime(),
					1000 * 60 * pref_widgetRefreshFreq, service2);
		}
	}

	static void createNotification(Context ctxt, int icon,
			CharSequence tickerText, CharSequence contentTitle,
			CharSequence contentText, int BITCOIN_NOTIFY_ID) {
		String ns = Context.NOTIFICATION_SERVICE;
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
		mNotificationManager.notify(BITCOIN_NOTIFY_ID, notification);
		mNotificationManager.cancel(BITCOIN_NOTIFY_ID);
	}

}