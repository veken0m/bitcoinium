
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
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.veken0m.bitcoinium.exchanges.Exchange;
import com.veken0m.bitcoinium.preferences.PreferencesActivity;
import com.veken0m.bitcoinium.preferences.PriceAlertPreferencesActivity;
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
            int[] widgetIds = (widgetManager != null) ? widgetManager.getAppWidgetIds(widgetComponent) : new int[0];

            readGeneralPreferences(this);
            //notifyUserOfMilli(this); //don't think this is needed anymore

            if (widgetIds.length > 0 && (!pref_wifiOnly || Utils.checkWiFiConnected(this))) {

                for (int appWidgetId : widgetIds) {

                    // Load widget configuration
                    String exchangePref = WidgetConfigureActivity.loadExchangePref(this, appWidgetId);
                    if (exchangePref == null) continue; // skip to next widget

                    if(exchangePref.toLowerCase().contains("mtgox")) exchangePref = "bitcoinaverage";

                    Exchange exchange = getExchange(exchangePref);
                    String currencyPair = WidgetConfigureActivity.loadCurrencyPref(this, appWidgetId);
                    String shortName = exchange.getShortName();
                    String exchangeKey = exchange.getIdentifier();

                    // TODO: find way to import required certificates
                    if (exchange.getIdentifier().equals("bitfinex") || exchange.getIdentifier().equals("cryptotrade")) {
                        try {
                            CertHelper.trustAllCerts();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.appwidget);
                    setTapBehaviour(appWidgetId, exchangeKey, views);

                    try {
                        // if altcoin append baseCurrency
                        CurrencyPair pair = CurrencyUtils.stringToCurrencyPair(currencyPair);
                        if (!pair.baseSymbol.equals(Currencies.BTC))
                            shortName += " " + pair.baseSymbol;

                        // Get ticker using XChange
                        Ticker ticker = ExchangeFactory.INSTANCE
                                .createExchange(exchange.getClassName())
                                .getPollingMarketDataService()
                                .getTicker(pair);

                        // Retrieve values from ticker
                        float lastFloat = ticker.getLast().floatValue();
                        String lastString = Utils.formatWidgetMoney(lastFloat, pair, true, pref_pricesInMilliBtc);

                        String volumeString = (ticker.getVolume() != null) ?
                                Utils.formatDecimal(ticker.getVolume().floatValue(), 2, 0, true) : getString(R.string.notAvailable);

                        setBidAskHighLow(ticker, views, pair, exchange.supportsTickerBidAsk());

                        views.setTextViewText(R.id.widgetExchange, shortName);
                        views.setTextViewText(R.id.widgetLastText, lastString);
                        views.setTextViewText(R.id.widgetVolText, getString(R.string.volume) + volumeString);
                        views.setTextViewText(R.id.label, getString(R.string.updatedAt) + Utils.getCurrentTime(this));
                        updateWidgetTheme(views);

                        if(pref_priceAlarm) checkAlarm(pair, lastFloat, exchange);
                        createTickerNotif(pair, lastString, exchange);

                    } catch (Exception e) {

                        e.printStackTrace();
                        views.setTextColor(R.id.label, pref_enableWidgetCustomization ? pref_widgetRefreshFailedColor : Color.RED);

                    } finally {
                        if (widgetManager != null)
                            widgetManager.updateAppWidget(appWidgetId, views);
                    }
                }
            }
        }

        private void createTickerNotif(CurrencyPair pair, String lastString, Exchange exchange) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String pairId = exchange.getIdentifier() + pair.baseSymbol + pair.counterSymbol;

            if (pref_enableTicker && prefs.getBoolean(pairId + "TickerPref", false)) {

                String msg = String.format(getString(R.string.priceContentNotif), pair.baseSymbol, lastString, exchange.getExchangeName());
                String title = String.format(getString(R.string.permPriceTitleNotif), exchange.getIdentifier(), pair.baseSymbol, lastString);

                createPermanentNotification(this, title, msg, pairId.hashCode());
            } else {
                removePermanentNotification(this, pairId.hashCode());
            }

        }

        private void setTapBehaviour(int appWidgetId, String exchangeKey, RemoteViews views) {

            PendingIntent pendingIntent;
            if (pref_tapToUpdate) {
                Intent intent = new Intent(this, WidgetProvider.class);
                intent.setAction(Constants.REFRESH);
                pendingIntent = PendingIntent.getBroadcast(this, appWidgetId, intent, 0);
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("exchangeKey", exchangeKey);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
        }

        public Exchange getExchange(String exchange) {
            try {
                return new Exchange(this, exchange);
            } catch (Exception e) {
                return new Exchange(this, Constants.DEFAULT_EXCHANGE);
            }
        }

        public void setTextColors(RemoteViews views, int color) {
            views.setTextColor(R.id.widgetLowText, color);
            views.setTextColor(R.id.widgetHighText, color);
            views.setTextColor(R.id.widgetVolText, color);
        }

        private void setBidAskHighLow(Ticker ticker, RemoteViews views, CurrencyPair pair, boolean bidAskSupported) {

            if (((ticker.getHigh() == null) || pref_widgetBidAsk) && bidAskSupported)
                setBidAsk(ticker, views, pair);
            else
                setHighLow(ticker, views, pair);
        }

        public void setBidAsk(Ticker ticker, RemoteViews views, CurrencyPair pair) {

            String bidString = Utils.formatWidgetMoney(ticker.getBid().floatValue(), pair, false, pref_pricesInMilliBtc);
            String askString = Utils.formatWidgetMoney(ticker.getAsk().floatValue(), pair, false, pref_pricesInMilliBtc);

            setTextColors(views, pref_enableWidgetCustomization ? pref_secondaryWidgetTextColor : Color.WHITE);
            views.setTextViewText(R.id.widgetLowText, bidString);
            views.setTextViewText(R.id.widgetHighText, askString);
        }

        public void setHighLow(Ticker ticker, RemoteViews views, CurrencyPair pair) {

            String highString = Utils.formatWidgetMoney(ticker.getHigh().floatValue(), pair, false, pref_pricesInMilliBtc);
            String lowString = Utils.formatWidgetMoney(ticker.getLow().floatValue(), pair, false, pref_pricesInMilliBtc);

            setTextColors(views, pref_enableWidgetCustomization ? pref_secondaryWidgetTextColor : Color.LTGRAY);
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

        public void checkAlarm(CurrencyPair pair, float lastFloat, Exchange exchange) {

            removeOldAlarmKeys(this, exchange.getIdentifier());

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String pairId = exchange.getIdentifier() + pair.baseSymbol + pair.counterSymbol;

            float notifLimitUpper = Float.valueOf(prefs.getString(pairId + "Upper", "999999"));
            float notifLimitLower = Float.valueOf(prefs.getString(pairId + "Lower", "0"));

            try {
                if (!Utils.isBetween(lastFloat, notifLimitLower, notifLimitUpper)) {
                    createNotification(this, lastFloat, exchange.getExchangeName(), pairId.hashCode(), pair);
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

        public void removeOldAlarmKeys(Context context, String exchangeKey) {

            if(prefs == null) prefs = PreferenceManager.getDefaultSharedPreferences(context);

            if (prefs.contains(exchangeKey + "Upper") || prefs.contains(exchangeKey + "Lower")) {

                prefs.edit().remove(exchangeKey + "Upper").commit();
                prefs.edit().remove(exchangeKey + "Lower").commit();
                prefs.edit().remove(exchangeKey + "TickerPref").commit();

                notifyUserOfAlarmUpgrade(context);
            }
        }

        public void notifyUserOfAlarmUpgrade(Context context) {

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            String tickerText = getString(R.string.priceAlarmUpgrade);
            String notifText = getString(R.string.priceAlarmUpgrade2);
            Notification notif = new Notification(R.drawable.bitcoin, tickerText, System.currentTimeMillis());

            Intent notifIntent = new Intent(context, PriceAlertPreferencesActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notifIntent, 0);

            notif.setLatestEventInfo(context, "Price Alarm upgraded", notifText, contentIntent);
            notif.defaults |= Notification.DEFAULT_VIBRATE;

            mNotificationManager.notify(1337, notif);
        }

        public void notifyUserOfMilli(Context context) {

            if(prefs == null) prefs = PreferenceManager.getDefaultSharedPreferences(context);

            if (prefs.getBoolean("warnUnitChangePref", true)) {

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                String tickerText = getString(R.string.milliBtcNotice);
                String notifText = getString(R.string.millioBtcNoticeInfo);
                Notification notif = new Notification(R.drawable.bitcoin, tickerText, System.currentTimeMillis());

                Intent notifIntent = new Intent(context, PreferencesActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notifIntent, 0);

                notif.setLatestEventInfo(context, getString(R.string.mtcByDefault), notifText, contentIntent);
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
