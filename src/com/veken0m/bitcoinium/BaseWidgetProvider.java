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
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.veken0m.bitcoinium.WidgetProvider.UpdateService;
import com.veken0m.bitcoinium.R;

public class BaseWidgetProvider extends AppWidgetProvider {

	/**
	 * This constant is what we send to ourself to force a refresh
	 */
	public static final String REFRESH = "com.veken0m.bitcoinium.REFRESH";
	public static final String OPENMENU = "com.veken0m.bitcoinium.OPENMENU";
	public static final String GRAPH = "com.veken0m.bitcoinium.GRAPH";

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

	// Service used to refresh widget
	static PendingIntent widgetRefreshService = null;

	/**
	 * When we receive an Intent, we will either force a refresh if it matches
	 * REFRESH, or pass it on to our superclass
	 */

	protected static void readPreferences(Context context) {

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
				pref_virtexUpper = pPrefs.getString("virtexUpper", "999");
				pref_virtexLower = pPrefs.getString("virtexLower", "0");
				pref_mtgoxUpper = pPrefs.getString("mtgoxUpper", "999");
				pref_mtgoxLower = pPrefs.getString("mtgoxLower", "0");
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
		pref_virtexUpper = prefs.getString("virtexUpper", "999");
		pref_virtexLower = prefs.getString("virtexLower", "0");
		pref_mtgoxUpper = prefs.getString("mtgoxUpper", "999");
		pref_mtgoxLower = prefs.getString("mtgoxLower", "0");
		pref_alarmSound = prefs.getBoolean("alarmSoundPref", false);
		pref_alarmVibrate = prefs.getBoolean("alarmVibratePref", false);
		pref_virtexTicker = prefs.getBoolean("virtexTickerPref", false);
		pref_mtgoxTicker = prefs.getBoolean("mtgoxTickerPref", false);
		pref_mtgoxCurrency = prefs.getString("mtgoxCurrencyPref", "USD");
	}

	public void onDestoy(Context context) {
		final AlarmManager m = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		m.cancel(widgetRefreshService);
	}

	static void setAlarm(Context context) {
		readPreferences(context);
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

	/**
	 * widgetButtonAction latches different actions to the widget button
	 * 
	 * @param Context
	 *            context
	 */

	public void widgetButtonAction(Context context) {

		final RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.appwidget);

		if (pref_widgetBehaviour.equalsIgnoreCase("mainMenu")) {
			final Intent intent = new Intent(context, MainActivity.class);
			final PendingIntent pendingIntent = PendingIntent.getActivity(
					context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);

		}

		else if (pref_widgetBehaviour.equalsIgnoreCase("refreshWidget")) {
			final Intent intent = new Intent(context, WidgetProvider.class);
			intent.setAction(REFRESH);
			final PendingIntent pendingIntent = PendingIntent.getBroadcast(
					context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
		}

		else if (pref_widgetBehaviour.equalsIgnoreCase("openGraph")) {

			final Intent intent = new Intent(context, MainActivity.class);
			intent.setAction(GRAPH);
			final PendingIntent pendingIntent = PendingIntent.getBroadcast(
					context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
		}

		else if (pref_widgetBehaviour.equalsIgnoreCase("pref")) {

			final Intent intent = new Intent(context, PreferencesActivity.class);
			final PendingIntent pendingIntent = PendingIntent.getActivity(
					context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
		}

		else if (pref_widgetBehaviour.equalsIgnoreCase("extOrder")) {
			final Intent intent = new Intent(context, WebViewerActivity.class);
			final PendingIntent pendingIntent = PendingIntent.getActivity(
					context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
		}
	}

}