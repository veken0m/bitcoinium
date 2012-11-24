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
import android.widget.RemoteViews;

import com.xeiam.xchange.Currencies;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.dto.marketdata.Ticker;

public class WidgetProvider extends BaseWidgetProvider {

	@Override
	public void onReceive(Context ctxt, Intent intent) {

		if (REFRESH.equals(intent.getAction())) {
			readPreferences(ctxt);
			ctxt.startService(new Intent(ctxt, UpdateService.class));
		} else if (PREFERENCES.equals(intent.getAction())) {
			readPreferences(ctxt);
		} else {
			super.onReceive(ctxt, intent);
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		setAlarm(context);
	}

	/**
	 * This class lets us refresh the widget whenever we want to
	 */

	public static class UpdateService extends IntentService {

		public UpdateService() {

			super("WidgetProvider$UpdateService");
		}

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
					WidgetProvider.class);
			int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);

			final int N = widgetIds.length;
			for (int i = 0; i < N; i++) {
				int appWidgetId = widgetIds[i];

				String pref_widgetExchange = WidgetConfigureActivity
						.loadExchangePref(context, appWidgetId);
				String pref_currency;
				String exchange;
				int NOTIFY_ID;

				if (pref_widgetExchange
						.equalsIgnoreCase("com.xeiam.xchange.virtex.VirtExExchange")) {
					pref_currency = "CAD";
					exchange = "VirtEx";
					pref_mtgoxLower = pref_virtexLower;
					pref_mtgoxUpper = pref_virtexUpper;
					NOTIFY_ID = NOTIFY_ID_VIRTEX;
				} else {
					pref_currency = WidgetConfigureActivity.loadCurrencyPref(
							context, appWidgetId);
					exchange = "MtGox";
					NOTIFY_ID = NOTIFY_ID_MTGOX;
				}

				if (pref_currency.length() == 3) {

					RemoteViews views = new RemoteViews(
							context.getPackageName(), R.layout.appwidget);

					Intent intent = new Intent(context, MainActivity.class);
					PendingIntent pendingIntent = PendingIntent.getActivity(
							context, 0, intent, 0);
					views.setOnClickPendingIntent(R.id.widgetButton,
							pendingIntent);

					try {

						// Get ticker using XChange
						Ticker ticker = ExchangeFactory.INSTANCE
								.createExchange(pref_widgetExchange)
								.getPollingMarketDataService()
								.getTicker(Currencies.BTC, pref_currency);

						float lastValue = ticker.getLast().getAmount()
								.floatValue();

						String lastPrice = Utils.formatMoney(
								Utils.formatTwoDecimals(lastValue),
								pref_currency);
						String highPrice = Utils.formatMoney2(
								Utils.formatTwoDecimals(ticker.getHigh()
										.getAmount().floatValue()),
								pref_currency);
						String lowPrice = Utils.formatMoney2(
								Utils.formatTwoDecimals(ticker.getLow()
										.getAmount().floatValue()),
								pref_currency);
						String volume = Utils.formatTwoDecimals(ticker
								.getVolume().floatValue());

						views.setTextViewText(R.id.widgetExchange, exchange);
						views.setTextViewText(R.id.widgetLowText, lowPrice);
						views.setTextViewText(R.id.widgetHighText, highPrice);
						views.setTextViewText(R.id.widgetLastText, lastPrice);
						views.setTextViewText(R.id.widgetVolText, "Volume: "
								+ volume);

						// Date for widget "Refreshed" label
						SimpleDateFormat sdf = new SimpleDateFormat("h:mm a",
								Locale.US);
						String currentTime = sdf.format(new Date());
						views.setTextViewText(R.id.label, "Refreshed @ "
								+ currentTime);
						views.setTextColor(R.id.label, Color.GREEN);

						if (pref_DisplayUpdates == true) {
							createTicker(context, R.drawable.bitcoin, ""
									+ exchange + " Updated!");
						}

						if (pref_mtgoxTicker) {
							createPermanentNotification(context,
									R.drawable.bitcoin, "Bitcoin at "
											+ lastPrice, "Bitcoin value: "
											+ lastPrice + " on " + exchange,
									NOTIFY_ID);
						}

						createAlarmNotification(context, lastValue, lastPrice,
								exchange, NOTIFY_ID);

					} catch (Exception e) {
						e.printStackTrace();
						if (pref_DisplayUpdates == true) {
							createTicker(context, R.drawable.bitcoin, exchange
									+ " Update failed!");
						}
						views.setTextColor(R.id.label, Color.RED);

					}
					widgetManager.updateAppWidget(appWidgetId, views);
				}
			}
		}

	}

}
