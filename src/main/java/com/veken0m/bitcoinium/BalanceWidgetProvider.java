package com.veken0m.bitcoinium;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.veken0m.utils.Constants;
import com.veken0m.utils.CurrencyUtils;
import com.veken0m.utils.Utils;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.bitcoinaverage.BitcoinAverageExchange;
import com.xeiam.xchange.blockchain.Blockchain;
import com.xeiam.xchange.blockchain.BlockchainExchange;
import com.xeiam.xchange.blockchain.dto.BitcoinAddress;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

import si.mazi.rescu.RestProxyFactory;

public class BalanceWidgetProvider extends BaseWidgetProvider
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
        setRefreshServiceAlarm(context, BalanceUpdateService.class);
    }

    /**
     * This class lets us refresh the widget whenever we want to
     */
    public static class BalanceUpdateService extends IntentService
    {
        public BalanceUpdateService()
        {
            super("BalanceWidgetProvider$BalanceUpdateService");
        }

        public void buildUpdate()
        {
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
            ComponentName widgetComponent = new ComponentName(this, BalanceWidgetProvider.class);

            readGeneralPreferences(this);

            if (widgetManager != null && (!pref_wifiOnly || Utils.isWiFiAvailable(this)))
            {
                int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
                for (int appWidgetId : widgetIds)
                {
                    // Load widget configuration
                    String walletAddress = BalanceWidgetConfigureActivity.loadAddressPref(this, appWidgetId);
                    String walletNickname = BalanceWidgetConfigureActivity.loadNicknamePref(this, appWidgetId);
                    String walletCurrency = BalanceWidgetConfigureActivity.loadCurrencyPref(this, appWidgetId);
                    if (walletAddress == null) continue; // skip to next widget

                    CurrencyPair currencyPair = CurrencyUtils.stringToCurrencyPair(walletCurrency);

                    Exchange blockchainExchangexchange = ExchangeFactory.INSTANCE.createExchange(BlockchainExchange.class.getName());
                    Blockchain blockchain = RestProxyFactory.createProxy(Blockchain.class, blockchainExchangexchange.getExchangeSpecification().getPlainTextUri());

                    RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.appwidget_balance);
                    try
                    {
                        setTapBehaviour(appWidgetId, views);

                        BitcoinAddress bitcoinAddress = blockchain.getBitcoinAddress(walletAddress);

                        // If no nickname chosen, use 20 first characters of address
                        if (walletNickname.length() == 0)
                            walletNickname = bitcoinAddress.getAddress().substring(0, 20);

                        float balance = bitcoinAddress.getFinalBalanceDecimal().floatValue();

                        views.setTextViewText(R.id.widgetAddress, walletNickname);
                        views.setTextViewText(R.id.widgetBalance, CurrencyUtils.formatPayout(balance, pref_widgetPayoutUnits, "BTC"));
                        views.setTextViewText(R.id.label, getString(R.string.updated) + " @ " + Utils.getCurrentTime(this));

                        try
                        {
                            // Interested in the public polling market data feed (no authentication)
                            Exchange bitcoinAverageExchange = ExchangeFactory.INSTANCE.createExchange(BitcoinAverageExchange.class.getName());
                            PollingMarketDataService marketDataService = bitcoinAverageExchange.getPollingMarketDataService();
                            Ticker ticker = marketDataService.getTicker(currencyPair);
                            float value = ticker.getLast().floatValue();

                            views.setTextViewText(R.id.widgetLastTransaction, Utils.formatWidgetMoney(bitcoinAddress.getFinalBalanceDecimal().floatValue() * value, currencyPair, true, false));
                        }
                        catch (Exception e)
                        {
                            views.setTextViewText(R.id.widgetLastTransaction, getString(R.string.notAvailable));
                        }

                        updateWidgetTheme(views);

                        // if (pref_priceAlarm) checkAlarm(pair, lastFloat, exchange);
                        //createTickerNotif(pair, lastString, exchange);

                    }
                    catch (Exception e)
                    {

                        e.printStackTrace();
                        views.setTextColor(R.id.label, pref_enableWidgetCustomization ? pref_widgetRefreshFailedColor : Color.RED);
                    }
                    finally
                    {
                        if (widgetManager != null)
                            widgetManager.updateAppWidget(appWidgetId, views);
                    }
                }
            }
        }

        private void setTapBehaviour(int appWidgetId, RemoteViews views)
        {
            PendingIntent pendingIntent;
            if (pref_tapToUpdate)
            {
                Intent intent = new Intent(this, BalanceWidgetProvider.class);
                intent.setAction(Constants.REFRESH);
                pendingIntent = PendingIntent.getBroadcast(this, appWidgetId, intent, 0);
            }
            else
            {
                Intent intent = new Intent(this, MainActivity.class);
                //intent.putExtra("exchangeKey", exchangeKey);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
        }

        public void updateWidgetTheme(RemoteViews views)
        {
            // set the color
            if (pref_enableWidgetCustomization)
            {
                views.setInt(R.id.widget_layout, "setBackgroundColor", pref_backgroundWidgetColor);
                views.setTextColor(R.id.widgetAddress, pref_mainWidgetTextColor);
                views.setTextColor(R.id.widgetBalance, pref_mainWidgetTextColor);
                views.setTextColor(R.id.label, pref_widgetRefreshSuccessColor);
                views.setTextColor(R.id.widgetLastTransaction, pref_secondaryWidgetTextColor);
            }
            else
            {
                views.setInt(R.id.widget_layout, "setBackgroundColor", getResources().getColor(R.color.widgetBackgroundColor));
                views.setTextColor(R.id.widgetAddress, getResources().getColor(R.color.widgetMainTextColor));
                views.setTextColor(R.id.widgetBalance, getResources().getColor(R.color.widgetMainTextColor));
                views.setTextColor(R.id.label, Color.GREEN);
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
