package com.veken0m.bitcoinium;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;

public class WidgetProvider extends BaseWidgetProvider {

	@Override
	public void onReceive(Context ctxt, Intent intent) {

		if (REFRESH.equals(intent.getAction())) {
			setAlarm(ctxt);
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
			super.onCreate();
		}

		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
			super.onStartCommand(intent, flags, startId);
			return START_STICKY;
		}

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

			final Intent intent = new Intent(context, MainActivity.class);
			final PendingIntent pendingIntent = PendingIntent.getActivity(
					context, 0, intent, 0);

			for (int appWidgetId : widgetIds) {
				RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.appwidget);
				//views.setInt(R.id.widget_layout, "setBackgroundColor", android.R.color.white); //sets the widget background color

				String pref_widget = WidgetConfigureActivity.loadExchangePref(
						context, appWidgetId);
				String pref_currency = WidgetConfigureActivity
						.loadCurrencyPref(context, appWidgetId);
  
				Exchange exchange;
				try{
				exchange = new Exchange(getResources().getStringArray(
						getResources().getIdentifier(pref_widget, "array",
								getBaseContext().getPackageName())));
				} catch (Exception e){
					exchange = new Exchange(getResources().getStringArray(
							getResources().getIdentifier("MtGoxExchange", "array",
									getBaseContext().getPackageName())));
				}

				int NOTIFY_ID = exchange.getNotificationID();
				String exchangeName = exchange.getExchangeName();
				String pref_widgetExchange = exchange.getClassName();
				String defaultCurrency = exchange.getMainCurrency();
				String prefix = exchange.getPrefix();
				Boolean tickerBidAsk = exchange.supportsTickerBidAsk();

				// BitcoinCentral is too long for widget, change to B.Central
				if (exchangeName.equalsIgnoreCase("BitcoinCentral")) {
					exchangeName = "B.Central";
				}

				readPreferences(context, prefix, defaultCurrency);

				if ((pref_currency.length() == 3)
						&& !(exchangeName.equals("NA"))) {

					views.setOnClickPendingIntent(R.id.widgetButton,
							pendingIntent);

					try {

						// Get ticker using XChange
						final Ticker ticker = ExchangeFactory.INSTANCE
								.createExchange(pref_widgetExchange)
								.getPollingMarketDataService()
								.getTicker(Currencies.BTC, pref_currency);

						// Retrieve values from ticker
						float lastValue = ticker.getLast().getAmount()
								.floatValue();

						final String lastPrice = Utils.formatWidgetMoney(
								lastValue, pref_currency, true);
						String volume = "N/A";

						if (!(ticker.getVolume() == null)) {
							volume = Utils.formatDecimal(ticker.getVolume()
									.floatValue(), 2, false);
						}

						final String highPrice;
						final String lowPrice;

						if (((ticker.getHigh() == null) || pref_widgetbidask) && tickerBidAsk) {
							highPrice = Utils.formatWidgetMoney(ticker.getAsk()
									.getAmount().floatValue(), pref_currency,
									false);
							lowPrice = Utils.formatWidgetMoney(ticker.getBid()
									.getAmount().floatValue(), pref_currency,
									false);
							// Color.rgb(150,220,220) => very light blue
							views.setTextColor(R.id.widgetLowText,  Color.WHITE);
							views.setTextColor(R.id.widgetHighText, Color.WHITE);
							views.setTextColor(R.id.widgetVolText, Color.WHITE);
						} else {
							highPrice = Utils.formatWidgetMoney(ticker
									.getHigh().getAmount().floatValue(),
									pref_currency, false);
							lowPrice = Utils.formatWidgetMoney(ticker.getLow()
									.getAmount().floatValue(), pref_currency,
									false);
						}
						
						views.setTextViewText(R.id.widgetExchange, exchangeName);
						views.setTextViewText(R.id.widgetLowText, lowPrice);
						views.setTextViewText(R.id.widgetHighText, highPrice);
						views.setTextViewText(R.id.widgetLastText, lastPrice);
						views.setTextViewText(R.id.widgetVolText, "Volume: "
								+ volume);

						views.setTextViewText(R.id.label, "Refreshed @ "
								+ Utils.getCurrentTime());
						views.setTextColor(R.id.label, Color.GREEN);

						if (pref_DisplayUpdates == true) {
							createTicker(context, R.drawable.bitcoin, ""
									+ exchangeName + " Updated!");
						}

						if (pref_ticker
								|| pref_currency.equals(pref_main_currency)) {
							createPermanentNotification(context,
									R.drawable.bitcoin,
									"Bitcoin  " + lastPrice,
									"Bitcoin value: " + lastPrice + " on "
											+ exchangeName, NOTIFY_ID + 100);
						}

						if (!pref_ticker) {
							removePermanentNotification(context,
									NOTIFY_ID + 100);
						}

						if (pref_PriceAlarm) {
							try {
								if (pref_currency.equals(pref_main_currency)
										&& !Utils
												.isBetween(
														lastValue,
														Float.valueOf(pref_notifLimitLower),
														Float.valueOf(pref_notifLimitUpper))) {
									createNotification(context, lastPrice,
											exchangeName, NOTIFY_ID);

								}
							} catch (Exception e) {
								Toast.makeText(
										getApplicationContext(),
										exchangeName
												+ "notification alarm thresholds are invalid.",
										Toast.LENGTH_LONG).show();
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
						if (pref_DisplayUpdates == true) {
							createTicker(context, R.drawable.bitcoin,
									exchangeName + " Update failed!");
						}
						views.setTextColor(R.id.label, Color.RED);
					}
					widgetManager.updateAppWidget(appWidgetId, views);
				}
			}
		}
	}

}
