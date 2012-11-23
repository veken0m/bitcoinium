package com.veken0m.cavirtex;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.widget.RemoteViews;

import com.veken0m.cavirtex.WatcherWidgetProvider.UpdateService;
import com.xeiam.xchange.Currencies;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.marketdata.polling.PollingMarketDataService;

public class WatcherWidgetProvider2 extends BaseWidgetProvider {

	private static PollingMarketDataService marketDataService;
	private static String currency;
	String widgetID;

	@Override
	public void onReceive(Context ctxt, Intent intent) {

		if (REFRESH.equals(intent.getAction())) {
			readPreferences(ctxt);
			ctxt.startService(new Intent(ctxt, UpdateService.class));

		} else if (PREFERENCES.equals(intent.getAction())) {

			readPreferences(ctxt);

		} else {
			readPreferences(ctxt);
			ctxt.startService(new Intent(ctxt, UpdateService.class));
			super.onReceive(ctxt, intent);
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		setAlarm2(context);
	}

	static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			String titlePrefix) {

		setAlarm2(context);
	}

	/**
	 * This class lets us refresh the widget whenever we want to
	 */

	public static class UpdateService2 extends IntentService {

		public UpdateService2() {

			super("WatcherWidgetProvider2$UpdateService2");
		}

		/*
		 * Depreciated in Android Developper website; Use onStartCommand.
		 * 
		 * @Override public void onStart(Intent intent, int i) {
		 * super.onStart(intent, i); }
		 */

		@Override
		public void onCreate() {

			readPreferences(this);
			super.onCreate();
		}

		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
			super.onStartCommand(intent, flags, startId);
			return START_STICKY;
		}

		/*
		 * Returns null by default, no need to declare it
		 * 
		 * @Override public IBinder onBind(Intent intent) { return null; }
		 */

		@Override
		public void onHandleIntent(Intent intent) {

			buildUpdate(this);
		}

		public void buildUpdate(Context context) {

			AppWidgetManager widgetManager = AppWidgetManager
					.getInstance(context);
			ComponentName widgetComponent = new ComponentName(context,
					WatcherWidgetProvider2.class);
			int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);

			final int N = widgetIds.length;
			for (int i = 0; i < N; i++) {
				int appWidgetId = widgetIds[i];
				
				pref_mtgoxCurrency = MtGoxWidgetConfigure.loadCurrencyPref(
						context, appWidgetId);
				
				if(pref_mtgoxCurrency.length() == 3){

				RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.watcher_appwidget2);

				Intent intent = new Intent(context, MainActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, 0, intent, 0);
				views.setOnClickPendingIntent(R.id.widgetButton2, pendingIntent);

				try {

					Exchange mtGox = ExchangeFactory.INSTANCE
							.createExchange("com.xeiam.xchange.mtgox.v1.MtGoxExchange");
					marketDataService = mtGox.getPollingMarketDataService();
					Ticker ticker = marketDataService.getTicker(Currencies.BTC,
							pref_mtgoxCurrency);

					float lastValue = ticker.getLast().getAmount().floatValue();

					String lastPrice = Utils.formatMoney(
							Utils.formatTwoDecimals(lastValue),
							pref_mtgoxCurrency);
					String highPrice = Utils.formatMoney2(
							Utils.formatTwoDecimals(ticker.getHigh()
									.getAmount().floatValue()),
							pref_mtgoxCurrency);
					String lowPrice = Utils.formatMoney2(
							Utils.formatTwoDecimals(ticker.getLow().getAmount()
									.floatValue()), pref_mtgoxCurrency);
					String volume = Utils.formatTwoDecimals(ticker.getVolume()
							.floatValue());

					views.setTextViewText(R.id.widgetLowText2, lowPrice);
					views.setTextViewText(R.id.widgetHighText2, highPrice);
					views.setTextViewText(R.id.widgetLastText2, lastPrice);
					views.setTextViewText(R.id.widgetVolText2, "Volume: "
							+ volume);

					// Date for widget "Refreshed" label
					SimpleDateFormat sdf = new SimpleDateFormat("h:mm a",
							Locale.US);
					String currentTime = sdf.format(new Date());
					views.setTextViewText(R.id.label2, "Refreshed @ "
							+ currentTime);
					views.setTextColor(R.id.label2, Color.GREEN);

					if (pref_DisplayUpdates == true) {
						createTicker(context, R.drawable.bitcoin,
								"MtGox Updated!");
					}

					if (pref_mtgoxTicker) {
						createPermanentNotification(context,
								R.drawable.bitcoin, "Bitcoin at " + lastPrice,
								"Bitcoin value: " + lastPrice + " on MtGox",
								NOTIFY_ID_MTGOX);
					}

					try {
						if (pref_PriceAlarm) {
							if (!pref_mtgoxLower.equalsIgnoreCase("")) {

								if (lastValue <= Float.valueOf(pref_mtgoxLower)) {
									createNotification(context,
											R.drawable.bitcoin,
											"Bitcoin alarm value has been reached! \n"
													+ "Bitcoin valued at "
													+ lastPrice + " on MtGox",
											"BTC @ " + lastPrice,
											"Bitcoin value: " + lastPrice
													+ " on MtGox",
											NOTIFY_ID_MTGOX);
								}
							}

							if (!pref_mtgoxUpper.equalsIgnoreCase("")) {
								if (lastValue >= Float.valueOf(pref_mtgoxUpper)) {
									createNotification(context,
											R.drawable.bitcoin,
											"Bitcoin alarm value has been reached! \n"
													+ "Bitcoin valued at "
													+ lastPrice + " on MtGox",
											"BTC @ " + lastPrice,
											"Bitcoin value: " + lastPrice
													+ " on MtGox",
											NOTIFY_ID_MTGOX);
								}

							}
						}

					} catch (Exception e) {
						e.printStackTrace();
						views.setTextColor(R.id.label2, Color.CYAN);
					}

				} catch (Exception e) {
					e.printStackTrace();
					if (pref_DisplayUpdates == true) {
						createTicker(context, R.drawable.bitcoin,
								"MtGox Update failed!");
					}
					views.setTextColor(R.id.label2, Color.RED);

				}
				// return views;
				widgetManager.updateAppWidget(appWidgetId, views);
				}
			}
		}

		public void widgetButtonAction(Context context) {

			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.watcher_appwidget);

			if (pref_widgetBehaviour.equalsIgnoreCase("mainMenu")) {
				Intent intent = new Intent(this, MainActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, 0, intent, 0);
				views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);

			}

			else if (pref_widgetBehaviour.equalsIgnoreCase("refreshWidget")) {
				Intent intent = new Intent(this, WatcherWidgetProvider.class);
				intent.setAction(REFRESH);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						context, 0, intent, 0);
				views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
			}

			else if (pref_widgetBehaviour.equalsIgnoreCase("openGraph")) {

				Intent intent = new Intent(this, MainActivity.class);
				intent.setAction(GRAPH);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						context, 0, intent, 0);
				views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
			}

			else if (pref_widgetBehaviour.equalsIgnoreCase("pref")) {

				Intent intent = new Intent(this, Preferences.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, 0, intent, 0);
				views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
			}

			else if (pref_widgetBehaviour.equalsIgnoreCase("extOrder")) {
				Intent intent = new Intent(getBaseContext(), WebViewer.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, 0, intent, 0);
				views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
			}
		}

	}

}
