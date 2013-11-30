
package com.veken0m.bitcoinium;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.veken0m.bitcoinium.exchanges.Exchange;
import com.veken0m.bitcoinium.utils.CurrencyUtils;
import com.veken0m.bitcoinium.utils.Utils;

import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Ticker;

public class WidgetProvider extends BaseWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (REFRESH.equals(intent.getAction())) {
            setPriceWidgetAlarm(context);
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        setPriceWidgetAlarm(context);
    }

    /**
     * This class lets us refresh the widget whenever we want to
     */
    public static class UpdateService extends IntentService {

        public void buildUpdate() {

            AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
            ComponentName widgetComponent = new ComponentName(this, WidgetProvider.class);
            int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
            PendingIntent pendingIntent;

            readGeneralPreferences(this);

            if (!pref_wifionly || checkWiFiConnected(this)) {

                for (int appWidgetId : widgetIds) {

                    // Load widget configuration
                    Exchange exchange = getExchange(WidgetConfigureActivity.loadExchangePref(this,
                            appWidgetId));
                    pref_currency = WidgetConfigureActivity.loadCurrencyPref(this, appWidgetId);
                    String exchangeName = exchange.getExchangeName();
                    String exchangeKey = exchange.getIdentifier();

                    if (pref_tapToUpdate) {
                        Intent intent = new Intent(this, WidgetProvider.class);
                        intent.setAction(REFRESH);
                        pendingIntent = PendingIntent.getBroadcast(this, appWidgetId, intent, 0);
                    } else {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("exchangeKey", exchangeKey);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pendingIntent = PendingIntent.getActivity(this, appWidgetId, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.appwidget);
                    views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);

                    readAllWidgetPreferences(this, exchangeKey, exchange.getDefaultCurrency());

                    if (pref_currency.length() == 3 || pref_currency.length() == 7) {

                        try {
                            CurrencyPair pair = CurrencyUtils.stringToCurrencyPair(pref_currency);
                            if (!pair.baseCurrency.equals(Currencies.BTC)) {
                                exchangeName += " (" + pair.baseCurrency + ")";
                            }

                            // Get a unique id for the exchange/currency pair
                            // combo
                            int NOTIFY_ID = (exchangeName + pair.toString()).hashCode();
                            String pairId = exchange.getIdentifier() + pair.baseCurrency
                                    + pair.counterCurrency;

                            // Get ticker using XChange
                            final Ticker ticker = ExchangeFactory.INSTANCE
                                    .createExchange(exchange.getClassName())
                                    .getPollingMarketDataService()
                                    .getTicker(pair.baseCurrency, pair.counterCurrency);

                            // Retrieve values from ticker
                            final float lastFloat = ticker.getLast().getAmount().floatValue();
                            final String lastString = Utils.formatWidgetMoney(lastFloat,
                                    pair.counterCurrency, true);

                            String volumeString = "N/A";
                            if (!(ticker.getVolume() == null)) {
                                float volumeFloat = ticker.getVolume().floatValue();
                                volumeString = Utils.formatDecimal(volumeFloat, 2, false);
                            }

                            if (((ticker.getHigh() == null) || pref_widgetbidask)
                                    && exchange.supportsTickerBidAsk()) {
                                setBidAsk(ticker, views, pair.counterCurrency);
                            } else {
                                setHighLow(ticker, views, pair.counterCurrency);
                            }

                            views.setTextViewText(R.id.widgetExchange, exchangeName);
                            views.setTextViewText(R.id.widgetLastText, lastString);
                            views.setTextViewText(R.id.widgetVolText, "Volume: " + volumeString);

                            if (pref_displayUpdates) {
                                String text = exchangeName + " Updated!";
                                createTicker(this, R.drawable.bitcoin, text);
                            }

                            SharedPreferences prefs = PreferenceManager
                                    .getDefaultSharedPreferences(this);

                            if (pref_priceAlarm) {

                                removeOldAlarmKeys(this, prefs, exchangeKey);
                                checkAlarm(this, pair, pairId, lastFloat, exchange, NOTIFY_ID);
                            }

                            if (pref_enableTicker && prefs.getBoolean(pairId + "TickerPref",
                                    false)) {

                                String msg = pair.baseCurrency + " value: " + lastString
                                        + " on " + exchangeName;
                                String title = exchangeKey + pair.baseCurrency + " @ " + lastString;

                                createPermanentNotification(this, R.drawable.bitcoin, title, msg,
                                        NOTIFY_ID);
                            } else {
                                removePermanentNotification(this, NOTIFY_ID);
                            }

                            String refreshedTime = "Updated @ " + Utils.getCurrentTime(this);
                            views.setTextViewText(R.id.label, refreshedTime);

                            updateWidgetTheme(views);

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (pref_enableWidgetCustomization) {
                                views.setTextColor(R.id.label, pref_widgetRefreshFailedColor);
                            } else {
                                views.setTextColor(R.id.label, Color.RED);
                            }

                            if (pref_displayUpdates) {
                                String txt = exchangeName + " Update failed!";
                                createTicker(this, R.drawable.bitcoin, txt);
                            }
                        } finally {
                            widgetManager.updateAppWidget(appWidgetId, views);
                        }
                    }
                }
            }
        }

        public Exchange getExchange(String pref_widget) {
            try {
                return new Exchange(this, pref_widget);
            } catch (Exception e) {
                return new Exchange(this, "MtGoxExchange");
            }
        }

        public void setTextColors(RemoteViews views, int color) {
            views.setTextColor(R.id.widgetLowText, color);
            views.setTextColor(R.id.widgetHighText, color);
            views.setTextColor(R.id.widgetVolText, color);
        }

        public void setBidAsk(Ticker ticker, RemoteViews views, String pref_currency) {

            float bidFloat = ticker.getBid().getAmount().floatValue();
            float askFloat = ticker.getAsk().getAmount().floatValue();

            String bidString = Utils.formatWidgetMoney(bidFloat, pref_currency, false);
            String askString = Utils.formatWidgetMoney(askFloat, pref_currency, false);

            if (pref_enableWidgetCustomization) {
                setTextColors(views, pref_secondaryWidgetTextColor);
            } else {
                setTextColors(views, Color.WHITE);
            }

            views.setTextViewText(R.id.widgetLowText, bidString);
            views.setTextViewText(R.id.widgetHighText, askString);
        }

        public void setHighLow(Ticker ticker, RemoteViews views, String pref_currency) {

            float highFloat = ticker.getHigh().getAmount().floatValue();
            float lowFloat = ticker.getLow().getAmount().floatValue();

            String highString = Utils.formatWidgetMoney(highFloat, pref_currency, false);
            String lowString = Utils.formatWidgetMoney(lowFloat, pref_currency, false);
            if (pref_enableWidgetCustomization) {
                setTextColors(views, pref_secondaryWidgetTextColor);
            } else {
                setTextColors(views, Color.LTGRAY);
            }
            views.setTextViewText(R.id.widgetLowText, lowString);
            views.setTextViewText(R.id.widgetHighText, highString);
        }

        public void updateWidgetTheme(RemoteViews views) {
            // set the color
            if (pref_enableWidgetCustomization) {
                views.setInt(R.id.widget_layout, "setBackgroundColor", pref_backgroundWidgetColor);
                views.setTextColor(R.id.widgetLastText, pref_mainWidgetTextColor);
                views.setTextColor(R.id.widgetExchange, pref_mainWidgetTextColor);
                views.setTextColor(R.id.label, pref_widgetRefreshSuccessColor);
                views.setTextColor(R.id.widgetVolText, pref_secondaryWidgetTextColor);
            } else {
                views.setInt(R.id.widget_layout, "setBackgroundColor",
                        getResources().getColor(R.color.widgetBackgroundColor));
                views.setTextColor(R.id.widgetLastText, getResources().getColor(
                        R.color.widgetMainTextColor));
                views.setTextColor(R.id.widgetExchange, getResources().getColor(
                        R.color.widgetMainTextColor));
                views.setTextColor(R.id.label, Color.GREEN);
            }
        }

        public void checkAlarm(Context context, CurrencyPair pair, String pairId, float lastFloat,
                Exchange exchange, int NOTIFY_ID) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            String pref_notifLimitUpper = prefs.getString(pairId + "Upper", "999999");
            String pref_notifLimitLower = prefs.getString(pairId + "Lower", "0");

            Boolean triggered = false;
            try {
                triggered = !Utils.isBetween(lastFloat,
                        Float.valueOf(pref_notifLimitLower),
                        Float.valueOf(pref_notifLimitUpper));
            } catch (Exception e) {
                e.printStackTrace();
                triggered = false;
                // TODO: Fix toast message for invalid thresholds
                // String text = exchangeName +
                // "notification alarm thresholds are invalid";
                // Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }

            if (triggered) {
                String lastString = Utils.formatWidgetMoney(lastFloat, pair.counterCurrency, true);
                createNotification(context, lastString, exchange.getExchangeName(), NOTIFY_ID,
                        pref_currency);

                if (pref_alarmClock)
                    setAlarmClock(context);
            }
        }

        public void removeOldAlarmKeys(Context ctxt, SharedPreferences prefs, String exchangeKey) {
            if (prefs.contains(exchangeKey + "Upper")
                    || prefs.contains(exchangeKey + "Lower")) {

                prefs.edit().remove(exchangeKey + "Upper").commit();
                prefs.edit().remove(exchangeKey + "Lower").commit();
                prefs.edit().remove(exchangeKey + "TickerPref").commit();

                notifyUserOfAlarmUpgrade(ctxt);
            }
        }

        public void notifyUserOfAlarmUpgrade(Context ctxt) {
            int icon = R.drawable.bitcoin;
            NotificationManager mNotificationManager = (NotificationManager) ctxt
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            long when = System.currentTimeMillis();
            Resources res = getResources();
            String tickerText = res.getString(R.string.priceAlarmUpgrade);
            String notifText = res.getString(R.string.priceAlarmUpgrade2);
            Notification notif = new Notification(icon, tickerText, when);

            Intent notifIntent = new Intent(ctxt, PriceAlarmPreferencesActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(ctxt, 0, notifIntent, 0);

            notif.setLatestEventInfo(ctxt, "Price Alarm upgraded", notifText, contentIntent);
            notif.defaults |= Notification.DEFAULT_VIBRATE;

            mNotificationManager.notify(1337, notif);
        }

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
            buildUpdate();
        }
    }

    public void onDestoy(Context context) {
        final AlarmManager m = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        m.cancel(widgetPriceWidgetRefreshService);
    }

}
