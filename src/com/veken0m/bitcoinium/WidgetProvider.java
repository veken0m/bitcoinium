
package com.veken0m.bitcoinium;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.veken0m.bitcoinium.exchanges.Exchange;
import com.veken0m.bitcoinium.utils.Utils;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;

public class WidgetProvider extends BaseWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (REFRESH.equals(intent.getAction())) {
            setAlarm(context);
        } else {
            super.onReceive(context, intent);
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

        public void buildUpdate(Context context) {
            AppWidgetManager widgetManager = AppWidgetManager
                    .getInstance(context);
            ComponentName widgetComponent = new ComponentName(context,
                    WidgetProvider.class);
            int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);

            final Intent intent = new Intent(context, MainActivity.class);
            final PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, intent, 0);

            readGeneralPreferences(context);

            if (!pref_wifionly || checkWiFiConnected(context)) {

                for (int appWidgetId : widgetIds) {

                    RemoteViews views = new RemoteViews(
                            context.getPackageName(), R.layout.appwidget);
                    views.setOnClickPendingIntent(R.id.widgetButton,
                            pendingIntent);

                    // Load Widget preferences
                    String pref_widget = WidgetConfigureActivity
                            .loadExchangePref(context, appWidgetId);
                    pref_currency = WidgetConfigureActivity
                            .loadCurrencyPref(context, appWidgetId);

                    Exchange exchange = getExchange(pref_widget);

                    int NOTIFY_ID = exchange.getNotificationID();
                    String exchangeName = exchange.getExchangeName();
                    String pref_widgetExchange = exchange.getClassName();
                    String defaultCurrency = exchange.getMainCurrency();
                    String prefix = exchange.getPrefix();
                    Boolean tickerBidAsk = exchange.supportsTickerBidAsk();

                    readAllWidgetPreferences(context, prefix, defaultCurrency);

                    if (pref_currency.length() == 3 || pref_currency.length() == 7) {

                        try {

                            String baseCurrency = Currencies.BTC;
                            String counterCurrency = pref_currency;

                            if (pref_currency.contains("/")) {
                                baseCurrency = pref_currency.substring(0, 3);
                                counterCurrency = pref_currency.substring(4, 7);
                                if (!baseCurrency.equals(Currencies.BTC)) {
                                    exchangeName = exchangeName + " (" + baseCurrency + ")";
                                }
                            }

                            // Get ticker using XChange
                            final Ticker ticker = ExchangeFactory.INSTANCE
                                    .createExchange(pref_widgetExchange)
                                    .getPollingMarketDataService()
                                    .getTicker(baseCurrency, counterCurrency);

                            // Retrieve values from ticker
                            final float lastFloat = ticker.getLast()
                                    .getAmount().floatValue();
                            final String lastString = Utils.formatWidgetMoney(
                                    lastFloat, counterCurrency, true);

                            String volumeString = "N/A";
                            if (!(ticker.getVolume() == null)) {
                                float volumeFloat = ticker.getVolume()
                                        .floatValue();

                                volumeString = Utils.formatDecimal(volumeFloat,
                                        2, false);
                            }

                            if (((ticker.getHigh() == null) || pref_widgetbidask)
                                    && tickerBidAsk) {
                                setBidAsk(ticker, views, counterCurrency);
                            } else {
                                setHighLow(ticker, views, counterCurrency);
                            }

                            views.setTextViewText(R.id.widgetExchange,
                                    exchangeName);
                            views.setTextViewText(R.id.widgetLastText,
                                    lastString);
                            views.setTextViewText(R.id.widgetVolText,
                                    "Volume: " + volumeString);


                            if (pref_displayUpdates) {
                                String text = exchangeName + " Updated!";
                                createTicker(context, R.drawable.bitcoin, text);
                            }

                            if (pref_priceAlarm) {
                                checkAlarm(context, counterCurrency, lastFloat,
                                        exchangeName, NOTIFY_ID);
                            }

                            if (pref_ticker
                                    && counterCurrency.equals(pref_main_currency)) {

                                String msg = baseCurrency + " value: " + lastString
                                        + " on " + exchangeName;
                                String title = baseCurrency + " @ " + lastString;

                                createPermanentNotification(context,
                                        R.drawable.bitcoin, title, msg,
                                        NOTIFY_ID);
                            } else {
                                removePermanentNotification(context, NOTIFY_ID);
                            }
                            
                            String refreshedTime = "Updated @ "
                                    + Utils.getCurrentTime(context);
                            views.setTextViewText(R.id.label, refreshedTime);
                            
                            updateWidgetTheme(views);

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (pref_enableWidgetCustomization) {
                                views.setTextColor(R.id.label,
                                        pref_widgetRefreshFailedColor);
                            } else {
                                views.setTextColor(R.id.label, Color.RED);
                            }

                            if (pref_displayUpdates) {
                                String txt = exchangeName + " Update failed!";
                                createTicker(context, R.drawable.bitcoin, txt);
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
                return new Exchange(getResources().getStringArray(
                        getResources().getIdentifier(pref_widget, "array",
                                getBaseContext().getPackageName())));
            } catch (Exception e) {
                return new Exchange(getResources().getStringArray(
                        getResources().getIdentifier("MtGoxExchange", "array",
                                getBaseContext().getPackageName())));
            }
        }

        public void setTextColors(RemoteViews views, int color) {
            views.setTextColor(R.id.widgetLowText, color);
            views.setTextColor(R.id.widgetHighText, color);
            views.setTextColor(R.id.widgetVolText, color);
        }

        public void setBidAsk(Ticker ticker, RemoteViews views,
                String pref_currency) {

            float bidFloat = ticker.getBid().getAmount().floatValue();
            float askFloat = ticker.getAsk().getAmount().floatValue();

            String bidString = Utils.formatWidgetMoney(bidFloat, pref_currency,
                    false);
            String askString = Utils.formatWidgetMoney(askFloat, pref_currency,
                    false);

            if (pref_enableWidgetCustomization) {
                setTextColors(views, pref_secondaryWidgetTextColor);
            } else {
                setTextColors(views, Color.WHITE);
            }

            views.setTextViewText(R.id.widgetLowText, bidString);
            views.setTextViewText(R.id.widgetHighText, askString);
        }

        public void setHighLow(Ticker ticker, RemoteViews views,
                String pref_currency) {

            float highFloat = ticker.getHigh().getAmount().floatValue();
            float lowFloat = ticker.getLow().getAmount().floatValue();

            String highString = Utils.formatWidgetMoney(highFloat,
                    pref_currency, false);
            String lowString = Utils.formatWidgetMoney(lowFloat, pref_currency,
                    false);
            if (pref_enableWidgetCustomization) {
                setTextColors(views, pref_secondaryWidgetTextColor);
            } else {
                setTextColors(views, Color.LTGRAY);
            }
            views.setTextViewText(R.id.widgetLowText, lowString);
            views.setTextViewText(R.id.widgetHighText, highString);
        }
        
        public void updateWidgetTheme(RemoteViews views){
            // set the color
            if (pref_enableWidgetCustomization) {
                views.setInt(R.id.widget_layout,
                        "setBackgroundColor",
                        pref_backgroundWidgetColor);
                views.setTextColor(R.id.widgetLastText,
                        pref_mainWidgetTextColor);
                views.setTextColor(R.id.widgetExchange,
                        pref_mainWidgetTextColor);
                views.setTextColor(R.id.label,
                        pref_widgetRefreshSuccessColor);
                views.setTextColor(R.id.widgetVolText, pref_secondaryWidgetTextColor);
                
            } else {
                views.setInt(
                        R.id.widget_layout,
                        "setBackgroundColor",
                        getResources().getColor(
                                R.color.widgetBackgroundColor));
                views.setTextColor(
                        R.id.widgetLastText,
                        getResources().getColor(
                                R.color.widgetMainTextColor));
                views.setTextColor(
                        R.id.widgetExchange,
                        getResources().getColor(
                                R.color.widgetMainTextColor));
                views.setTextColor(R.id.label, Color.GREEN);
            }
        }

        public void checkAlarm(Context context, String prefcurrency,
                float lastFloat, String exchangeName, int NOTIFY_ID) {

            Boolean triggered;
            try {
                triggered = (pref_currency.equals(pref_main_currency) || prefcurrency
                        .equals(pref_main_currency))
                        && !Utils.isBetween(lastFloat,
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
                String lastString = Utils.formatWidgetMoney(lastFloat,
                        prefcurrency, true);
                createNotification(context, lastString, exchangeName, NOTIFY_ID, pref_currency);
                if (pref_alarmClock) {
                    setAlarmClock(context);
                }
            }
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
            buildUpdate(this);
        }
    }

}
