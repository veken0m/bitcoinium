package com.veken0m.bitcoinium;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.veken0m.bitcoinium.exchanges.ExchangeProperties;
import com.veken0m.bitcoinium.preferences.TickerPreferencesActivity;
import com.veken0m.utils.CompatUtil;
import com.veken0m.utils.Constants;
import com.veken0m.utils.CurrencyUtils;
import com.veken0m.utils.ExchangeUtils;
import com.veken0m.utils.Utils;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;

public class WidgetProvider extends BaseWidgetProvider
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        if (Constants.REFRESH.equals(intent.getAction()))
            onUpdate(context, null, null);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        // onUpdate called upon create or when forced refresh by user. Use this to create a set refresh service.
        setRefreshServiceAlarm(context, UpdateService.class);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        super.onDeleted(context, appWidgetIds);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        try
        {
            // Clean up
            for (int appWidgetId : appWidgetIds)
            {
                String currencyPair = WidgetConfigureActivity.loadCurrencyPref(context, appWidgetId);
                String exchangePref = WidgetConfigureActivity.loadExchangePref(context, appWidgetId);
                CurrencyPair pair = CurrencyUtils.stringToCurrencyPair(currencyPair);
                ExchangeProperties exchange = UpdateService.getExchange(context, exchangePref);

                String pairId = exchange.getIdentifier() + pair.base.getSymbol() + pair.counter.getSymbol();

                clearOngoingNotification(context, pairId.hashCode());
                // Reset the preference for this combo
                prefs.edit().putBoolean(pairId + "TickerPref", false).commit();
                prefs.edit().putBoolean(pairId + "AlarmPref", false).commit();
            }
        }
        catch (Exception e)
        {
            // if anything is invalid during clean-up, suppress it
        }
    }

    @Override
    public void onEnabled(Context context)
    {
        super.onEnabled(context);
        // This is called when the first widget is created
        // TODO enable update service here
    }

    @Override
    public void onDisabled(Context context)
    {
        super.onDisabled(context);
        // This is called when the last widget is deleted
        // TODO disable update service here
    }

    /**
     * This class lets us refresh the widget whenever we want to
     */
    public static class UpdateService extends IntentService
    {
        public UpdateService()
        {
            super("WidgetProvider$UpdateService");
        }

        public static ExchangeProperties getExchange(Context context, String exchange)
        {
            try
            {
                return new ExchangeProperties(context, exchange);
            }
            catch (Exception e)
            {
                return new ExchangeProperties(context, Constants.DEFAULT_EXCHANGE);
            }
        }

        public void buildUpdate()
        {
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);

            readGeneralPreferences(this);

            if ((widgetManager == null) || (pref_wifiOnly && !Utils.isWiFiAvailable(this)))
                return; // no widgets and no WiFi when required? bail.

            CompatUtil.convertAlarmPrefs(this);

            // Get all price widgets and loop over them to update
            ComponentName widgetComponent = new ComponentName(this, WidgetProvider.class);
            int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
            for (int appWidgetId : widgetIds)
            {
                RemoteViews view = updateWidgetView(appWidgetId);
                if (view != null) widgetManager.updateAppWidget(appWidgetId, view);
            }
        }

        public RemoteViews updateWidgetView(int appWidgetId)
        {
            // Load widget configuration
            String exchangePref = WidgetConfigureActivity.loadExchangePref(this, appWidgetId);
            if (exchangePref == null) return null; // skip to next widget

            ExchangeProperties exchange = getExchange(this, exchangePref);
            String currencyPair = WidgetConfigureActivity.loadCurrencyPref(this, appWidgetId);
            String shortName = exchange.getShortName();
            String exchangeKey = exchange.getIdentifier();

            RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.appwidget);
            setTapBehaviour(appWidgetId, exchangeKey, views);

            try
            {
                // if altcoin append baseCurrency
                CurrencyPair pair = CurrencyUtils.stringToCurrencyPair(currencyPair);
                if (!pair.base.getSymbol().equals(Currency.BTC))
                    shortName += " " + pair.base.getSymbol();

                // Get ticker using XChange
                Ticker ticker = ExchangeUtils.getMarketData(exchange, pair).getTicker(pair);

                // Retrieve values from ticker
                float lastFloat = ticker.getLast().floatValue();
                String sLast = Utils.formatWidgetMoney(lastFloat, pair, true, pref_pricesInMilliBtc);

                String sVolume;
                if (ticker.getVolume() != null)
                {
                    sVolume = Utils.formatDecimal(ticker.getVolume().floatValue(), 2, 0, true);
                    sVolume += " " + pair.base.getSymbol();
                }
                else
                {
                    sVolume = getString(R.string.notAvailable);
                }

                setBidAskHighLow(ticker, views, pair);

                views.setTextViewText(R.id.widgetExchange, shortName);
                views.setTextViewText(R.id.widgetLastText, sLast);
                views.setTextViewText(R.id.widgetVolText, getString(R.string.volume_short) + ": " + sVolume);
                views.setTextViewText(R.id.label, getString(R.string.updated) + " @ " + Utils.getCurrentTime(this));
                updateWidgetTheme(views);

                checkAlarm(pair, lastFloat, exchange);
                updateOngoingTickerNotification(pair, lastFloat, exchange);

                // Update last price map
                String pairId = exchange.getIdentifier() + pair.base.getSymbol() + pair.counter.getSymbol();
                prevPrice.put(pairId.hashCode(), lastFloat);
            }
            catch (Exception e)
            {
                views.setTextColor(R.id.label, pref_enableWidgetCustomization ? pref_widgetRefreshFailedColor : Color.RED);
            }

            return views;
        }

        private void updateOngoingTickerNotification(CurrencyPair pair, float lastFloat, ExchangeProperties exchange)
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String pairId = exchange.getIdentifier() + pair.base.getSymbol() + pair.counter.getSymbol();

            if (prefs.getBoolean(pairId + "TickerPref", false))
            {
                String lastString = Utils.formatWidgetMoney(lastFloat, pair, true, pref_pricesInMilliBtc);
                String msg = getString(R.string.msg_priceContentNotif, pair.base.getSymbol(), lastString, exchange.getExchangeName());
                String title = getString(R.string.msg_permPriceTitleNotif, exchange.getIdentifier(), pair.base.getSymbol(), lastString);

                Intent notificationIntent = new Intent(this, TickerPreferencesActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

                sendNotification(this, title, msg, contentIntent, null, pairId.hashCode(), true, lastFloat);
            }
            else
            {
                clearOngoingNotification(this, 100 + pairId.hashCode());
            }
        }

        private void setTapBehaviour(int appWidgetId, String exchangeKey, RemoteViews views)
        {
            PendingIntent pendingIntent;
            if (pref_tapToUpdate)
            {
                Intent intent = new Intent(this, WidgetProvider.class);
                intent.setAction(Constants.REFRESH);
                pendingIntent = PendingIntent.getBroadcast(this, appWidgetId, intent, 0);
            }
            else
            {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("exchangeKey", exchangeKey);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
        }

        public void setTextColors(RemoteViews views, int color)
        {
            views.setTextColor(R.id.widgetLowText, color);
            views.setTextColor(R.id.widgetHighText, color);
            views.setTextColor(R.id.widgetVolText, color);
        }

        private void setBidAskHighLow(Ticker ticker, RemoteViews views, CurrencyPair pair)
        {
            if (((pref_widgetBidAsk || ticker.getHigh() == null)) && ticker.getBid() != null)
                setBidAsk(ticker, views, pair);
            else
                setHighLow(ticker, views, pair);
        }

        public void setBidAsk(Ticker ticker, RemoteViews views, CurrencyPair pair)
        {
            String bidString = Utils.formatWidgetMoney(ticker.getBid().floatValue(), pair, false, pref_pricesInMilliBtc);
            String askString = Utils.formatWidgetMoney(ticker.getAsk().floatValue(), pair, false, pref_pricesInMilliBtc);

            setTextColors(views, pref_enableWidgetCustomization ? pref_secondaryWidgetTextColor : Color.WHITE);
            views.setTextViewText(R.id.widgetLowText, bidString);
            views.setTextViewText(R.id.widgetHighText, askString);
        }

        public void setHighLow(Ticker ticker, RemoteViews views, CurrencyPair pair)
        {
            String highString = Utils.formatWidgetMoney(ticker.getHigh().floatValue(), pair, false, pref_pricesInMilliBtc);
            String lowString = Utils.formatWidgetMoney(ticker.getLow().floatValue(), pair, false, pref_pricesInMilliBtc);

            setTextColors(views, pref_enableWidgetCustomization ? pref_secondaryWidgetTextColor : Color.LTGRAY);
            views.setTextViewText(R.id.widgetLowText, lowString);
            views.setTextViewText(R.id.widgetHighText, highString);
        }

        public void updateWidgetTheme(RemoteViews views)
        {
            // set the color
            if (pref_enableWidgetCustomization)
            {
                views.setInt(R.id.widget_layout, "setBackgroundColor", pref_backgroundWidgetColor);
                views.setTextColor(R.id.widgetLastText, pref_mainWidgetTextColor);
                views.setTextColor(R.id.widgetExchange, pref_mainWidgetTextColor);
                views.setTextColor(R.id.label, pref_widgetRefreshSuccessColor);
                views.setTextColor(R.id.widgetVolText, pref_secondaryWidgetTextColor);
            }
            else
            {
                views.setInt(R.id.widget_layout, "setBackgroundColor", getResources().getColor(R.color.widgetBackgroundColor));
                views.setTextColor(R.id.widgetLastText, getResources().getColor(R.color.widgetMainTextColor));
                views.setTextColor(R.id.widgetExchange, getResources().getColor(R.color.widgetMainTextColor));
                views.setTextColor(R.id.label, Color.GREEN);
            }
        }

        public void checkAlarm(CurrencyPair pair, float lastFloat, ExchangeProperties exchange)
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String pairId = exchange.getIdentifier() + pair.base.getSymbol() + pair.counter.getSymbol();
            if (!prefs.getBoolean(pairId + "AlarmPref", false))
                return; // Alarm not enabled

            try
            {
                float notifLimitUpper = Float.valueOf(prefs.getString(pairId + "Upper", "999999"));
                float notifLimitLower = Float.valueOf(prefs.getString(pairId + "Lower", "0"));
                if (lastFloat != 0 && !Utils.isBetween(lastFloat, notifLimitLower, notifLimitUpper))
                {
                    createNotification(this, lastFloat, exchange.getExchangeName(), pairId.hashCode(), pair);
                    if (pref_alarmClock)
                        setAlarmClock(this);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                // TODO: Fix toast message for invalid thresholds
                // String text = exchangeName +
                // "notification alarm thresholds are invalid";
                // Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId)
        {
            super.onStartCommand(intent, flags, startId);
            return START_STICKY;
        }

        @Override
        public void onHandleIntent(Intent intent)
        {
            buildUpdate();
        }
    }
}
