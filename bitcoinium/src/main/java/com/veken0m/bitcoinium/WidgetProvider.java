
package com.veken0m.bitcoinium;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.veken0m.bitcoinium.exchanges.Exchange;
import com.veken0m.utils.Constants;
import com.veken0m.utils.CurrencyUtils;
import com.veken0m.utils.Utils;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.utils.CertHelper;

public class WidgetProvider extends BaseWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Constants.REFRESH.equals(intent.getAction()))
            onUpdate(context, null, null);

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        setPriceWidgetAlarm(context);
    }

    /**
     * This class lets us refresh the widget whenever we want to
     */
    public static class UpdateService extends IntentService {

        public void buildUpdate() {

            AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
            ComponentName widgetComponent = new ComponentName(this, WidgetProvider.class);
            int[] widgetIds = new int[0];
            if (widgetManager != null) {
                widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
            }

            readGeneralPreferences(this);
            notifyUserOfMilli(this);

            if (widgetIds.length > 0 && (!pref_wifiOnly || checkWiFiConnected(this))) {

                for (int appWidgetId : widgetIds) {

                    // Load widget configuration
                    String exchangePref = WidgetConfigureActivity.loadExchangePref(this,
                            appWidgetId);

                    if (exchangePref == null) continue; // skip to next
                    Exchange exchange = getExchange(exchangePref);
                    String currencyPair = WidgetConfigureActivity.loadCurrencyPref(this, appWidgetId);
                    String exchangeName = exchange.getExchangeName();
                    String exchangeKey = exchange.getIdentifier();

                    if (exchange.getIdentifier().equals("bitfinex")) {
                        try {
                            CertHelper.trustAllCerts();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.appwidget);
                    setTapBehaviour(appWidgetId, exchangeKey, views);

                    readGeneralPreferences(this);

                    try {
                        // if altcoin append baseCurrency
                        CurrencyPair pair = CurrencyUtils.stringToCurrencyPair(currencyPair);
                        if (!pair.baseCurrency.equals(Currencies.BTC))
                            exchangeName += " (" + pair.baseCurrency + ")";

                        String pairId = exchange.getIdentifier() + pair.baseCurrency
                                + pair.counterCurrency;

                        // Get ticker using XChange
                        Ticker ticker = ExchangeFactory.INSTANCE
                                .createExchange(exchange.getClassName())
                                .getPollingMarketDataService()
                                .getTicker(pair.baseCurrency, pair.counterCurrency);

                        // Retrieve values from ticker
                        float lastFloat = ticker.getLast().getAmount().floatValue();
                        String lastString = Utils.formatWidgetMoney(lastFloat, pair, true,
                                pref_pricesInMilliBtc);

                        String volumeString = "N/A";

                        //TODO: Return null volume in XChange for Bitfinex
                        if (!(ticker.getVolume() == null) && !exchangeKey.equals("bitfinex"))
                            volumeString = Utils.formatDecimal(ticker.getVolume());

                        setBidAskHighLow(ticker, views, pair, exchange.supportsTickerBidAsk());

                        views.setTextViewText(R.id.widgetExchange, exchangeName);
                        views.setTextViewText(R.id.widgetLastText, lastString);
                        views.setTextViewText(R.id.widgetVolText, "Volume: " + volumeString);
                        views.setTextViewText(R.id.label, "Updated @ " + Utils.getCurrentTime(this));
                        updateWidgetTheme(views);

                        checkAlarm(pair, pairId, lastFloat, exchangeName, exchangeKey);
                        createTickerNotif(pair, pairId, lastString, exchangeName, exchangeKey);

                    } catch (Exception e) {

                        if (pref_enableWidgetCustomization)
                            views.setTextColor(R.id.label, pref_widgetRefreshFailedColor);
                        else
                            views.setTextColor(R.id.label, Color.RED);

                    } finally {
                        if (widgetManager != null) {
                            widgetManager.updateAppWidget(appWidgetId, views);
                        }
                    }
                }
            }
        }

        private void createTickerNotif(CurrencyPair pair, String pairId, String lastString,
                                       String exchangeName, String exchangeKey) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            if (pref_enableTicker && prefs.getBoolean(pairId + "TickerPref", false)) {

                Resources res = getResources();
                String msg = String.format(
                        res.getString(R.string.priceContentNotif),
                        pair.baseCurrency, lastString, exchangeName);
                String title = String.format(
                        res.getString(R.string.permPriceTitleNotif),
                        exchangeKey, pair.baseCurrency, lastString);

                createPermanentNotification(this, title, msg, pairId.hashCode());
            } else {
                removePermanentNotification(this, pairId.hashCode());
            }

        }

        private void setTapBehaviour(int appWidgetId,
                                     String exchangeKey, RemoteViews views) {

            PendingIntent pendingIntent;
            if (pref_tapToUpdate) {
                Intent intent = new Intent(this, WidgetProvider.class);
                intent.setAction(Constants.REFRESH);
                pendingIntent = PendingIntent.getBroadcast(this, appWidgetId, intent, 0);
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("exchangeKey", exchangeKey);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, appWidgetId, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }
            views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
        }

        public Exchange getExchange(String exchange) {
            try {
                return new Exchange(this, exchange);
            } catch (Exception e) {
                return new Exchange(this, "MtGoxExchange");
            }
        }

        public void setTextColors(RemoteViews views, int color) {
            views.setTextColor(R.id.widgetLowText, color);
            views.setTextColor(R.id.widgetHighText, color);
            views.setTextColor(R.id.widgetVolText, color);
        }

        private void setBidAskHighLow(Ticker ticker, RemoteViews views, CurrencyPair pair, boolean bidAskSupported) {
            if (((ticker.getHigh() == null) || pref_widgetBidAsk) && bidAskSupported) {
                setBidAsk(ticker, views, pair);
            } else {
                setHighLow(ticker, views, pair);
            }
        }

        public void setBidAsk(Ticker ticker, RemoteViews views, CurrencyPair pair) {

            String bidString = Utils.formatWidgetMoney(ticker.getBid().getAmount().floatValue(),
                    pair, false, pref_pricesInMilliBtc);
            String askString = Utils.formatWidgetMoney(ticker.getAsk().getAmount().floatValue(),
                    pair, false, pref_pricesInMilliBtc);

            if (pref_enableWidgetCustomization) {
                setTextColors(views, pref_secondaryWidgetTextColor);
            } else {
                setTextColors(views, Color.WHITE);
            }

            views.setTextViewText(R.id.widgetLowText, bidString);
            views.setTextViewText(R.id.widgetHighText, askString);
        }

        public void setHighLow(Ticker ticker, RemoteViews views, CurrencyPair pair) {

            String highString = Utils.formatWidgetMoney(ticker.getHigh().getAmount().floatValue(),
                    pair, false, pref_pricesInMilliBtc);
            String lowString = Utils.formatWidgetMoney(ticker.getLow().getAmount().floatValue(),
                    pair, false, pref_pricesInMilliBtc);

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
                views.setInt(R.id.widget_layout, "setBackgroundColor", getResources().getColor(R.color.widgetBackgroundColor));
                views.setTextColor(R.id.widgetLastText, getResources().getColor(R.color.widgetMainTextColor));
                views.setTextColor(R.id.widgetExchange, getResources().getColor(R.color.widgetMainTextColor));
                views.setTextColor(R.id.label, Color.GREEN);
            }
        }

        public void checkAlarm(CurrencyPair pair, String pairId, float lastFloat,
                               String exchangeName, String exchangeKey) {

            if (pref_priceAlarm) {

                removeOldAlarmKeys(this, exchangeKey);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                float notifLimitUpper = Float.valueOf(prefs.getString(pairId + "Upper", "999999"));
                float notifLimitLower = Float.valueOf(prefs.getString(pairId + "Lower", "0"));

                try {
                    if (!Utils.isBetween(lastFloat, notifLimitLower, notifLimitUpper)) {
                        createNotification(this, lastFloat, exchangeName, pairId.hashCode(), pair);
                        if (pref_alarmClock)
                            setAlarmClock(this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: Fix toast message for invalid thresholds
                    // String text = exchangeName +
                    // "notification alarm thresholds are invalid";
                    // Toast.makeText(this, text, Toast.LENGTH_LONG).show();
                }
            }
        }

        public void removeOldAlarmKeys(Context context, String exchangeKey) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            if (prefs.contains(exchangeKey + "Upper") || prefs.contains(exchangeKey + "Lower")) {

                prefs.edit().remove(exchangeKey + "Upper").commit();
                prefs.edit().remove(exchangeKey + "Lower").commit();
                prefs.edit().remove(exchangeKey + "TickerPref").commit();

                notifyUserOfAlarmUpgrade(context);
            }
        }

        public void notifyUserOfAlarmUpgrade(Context context) {
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            long when = System.currentTimeMillis();
            Resources res = getResources();
            String tickerText = res.getString(R.string.priceAlarmUpgrade);
            String notifText = res.getString(R.string.priceAlarmUpgrade2);
            Notification notif = new Notification(R.drawable.bitcoin, tickerText, when);

            Intent notifIntent = new Intent(context, PriceAlarmPreferencesActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notifIntent, 0);

            notif.setLatestEventInfo(context, "Price Alarm upgraded", notifText, contentIntent);
            notif.defaults |= Notification.DEFAULT_VIBRATE;

            mNotificationManager.notify(1337, notif);
        }

        public void notifyUserOfMilli(Context ctxt) {

            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(ctxt);

            boolean pref_warnUnitsChangeNotif = prefs.getBoolean("warnUnitChangePref", true);

            if (pref_warnUnitsChangeNotif) {
                NotificationManager mNotificationManager = (NotificationManager) ctxt
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                long when = System.currentTimeMillis();
                //Resources res = getResources();
                String tickerText = "*NOTICE* - Widgets now display mBTC by default!";
                String notifText = "Change back to BTC in the Preferences";
                Notification notif = new Notification(R.drawable.bitcoin, tickerText, when);

                Intent notifIntent = new Intent(ctxt, PreferencesActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(ctxt, 0, notifIntent, 0);

                notif.setLatestEventInfo(ctxt, "Widgets in mBTC by default!", notifText,
                        contentIntent);
                notif.defaults |= Notification.DEFAULT_VIBRATE;

                mNotificationManager.notify(817, notif);

                Editor editor = prefs.edit();
                editor.putBoolean("warnUnitChangePref", false);
                editor.commit();
            }

        }

        public UpdateService() {
            super("WidgetProvider$UpdateService");
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
}
