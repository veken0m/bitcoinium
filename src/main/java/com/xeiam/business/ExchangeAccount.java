package com.xeiam.business;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.xeiam.tasks.CancelOrderTask;
import com.xeiam.tasks.SubmitOrderTask;
import com.xeiam.xbtctrader.TraderActivity;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.bitcoinium.BitcoiniumExchange;
import com.xeiam.xchange.bitcoinium.dto.marketdata.BitcoiniumOrderbook;
import com.xeiam.xchange.bitcoinium.dto.marketdata.BitcoiniumTicker;
import com.xeiam.xchange.bitcoinium.dto.marketdata.BitcoiniumTickerHistory;
import com.xeiam.xchange.bitcoinium.service.polling.BitcoiniumMarketDataServiceRaw;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.account.AccountInfo;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.dto.trade.OpenOrders;
import com.xeiam.xchange.dto.trade.Wallet;
import com.xeiam.xchange.service.polling.account.PollingAccountService;
import com.xeiam.xchange.service.polling.trade.PollingTradeService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExchangeAccount
{
    private static final String TAG = "ExchangeAccount";
    //private final Object orderbookLock = new Object();
    private final Object tradesLock = new Object();
    boolean connectionGood = false;
    float lastAccountBalance = 0;
    boolean accountValueIncreasing = true;
    TraderActivity mainActivity;
    private String timewindow;
    private String pricewindow;
    private ExchangeSpecification exchangeSpecification;
    private BitcoiniumMarketDataServiceRaw bitcoiniumMarketDataService;
    private Exchange exchange;
    //private List<LimitOrder> pendingOrders=new ArrayList<LimitOrder>();
    private PollingAccountService accountService;
    private PollingTradeService tradeService;
    private AccountInfo accountInfo;
    private List<LimitOrder> openOrders = new ArrayList<>();
    private BitcoiniumTicker referenceTicker;
    //	private float askChange=0;
//	private float bidChange=0;
    private LinkedList<BitcoiniumTicker> trades;
    private BitcoiniumOrderbook orderBook;
    private BitcoiniumOrderbook lastOrderBook;
    private BitcoiniumTicker lastTicker;

    public ExchangeAccount(TraderActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }


    public boolean dataIsSet()
    {
        if (orderBook == null)
        {
            return false;
        }
        if (lastOrderBook == null)
        {
            return false;
        }
        if (trades == null)
        {
            return false;
        }
        if (referenceTicker == null)
        {
            return false;
        }
        if (lastTicker == null)
        {
            return false;
        }

        return true;
    }


    public void cancelLimitOrder(String id)
    {
        CancelOrderTask cancelOrderTask = new CancelOrderTask(id, this);
        cancelOrderTask.go();
        generateToast("Cancellation order sent to exchange.", Toast.LENGTH_SHORT);
    }

    public void placeLimitOrder(float price, float amount, OrderType orderType, Activity activity)
    {

        BigDecimal tradeableAmount = new BigDecimal(amount).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal limitPrice = new BigDecimal(TraderActivity.fiveDecimalFormatter.format(price));

        LimitOrder limitOrder = new LimitOrder(orderType, tradeableAmount, new CurrencyPair(TraderActivity.tradableIdentifier, TraderActivity.transactionCurrency), null, null, limitPrice);
        Log.v(TAG, "Placing Order: " + limitOrder);

        //generateToast("Trading is currently disabled. \nPlease contact developer to become a tester", Toast.LENGTH_LONG);
        SubmitOrderTask submitOrderTask = new SubmitOrderTask(limitOrder, this, activity); // execute network task on another thread.
        submitOrderTask.go();
    }

    public boolean init()
    {
        try
        {
            ExchangeSpecification bitcoiniumExchangeSpec = new ExchangeSpecification(BitcoiniumExchange.class.getName());

            // TODO: input BitcoiniumWS API key before release!
            bitcoiniumExchangeSpec.setApiKey("INSERT_KEY_HERE");
            // SSL issues on Android 2.2, works on Android 2.3.3
            //bitcoiniumExchangeSpec.setPlainTextUri("http://173.10.241.154:9090");

            Exchange bitcoiniumExchange = ExchangeFactory.INSTANCE.createExchange(bitcoiniumExchangeSpec);
            bitcoiniumMarketDataService = (BitcoiniumMarketDataServiceRaw) bitcoiniumExchange.getPollingMarketDataService();

            // Use the factory to get the version 2 MtGox exchange API using default settings
            exchangeSpecification = new ExchangeSpecification(TraderActivity.exchangeInfo.getClassName());

            String exchangeId = TraderActivity.exchangeInfo.getIdentifier();
            String username = TraderActivity.preferences.getString(exchangeId + "Username", "");
            String password = TraderActivity.preferences.getString(exchangeId + "Password", "");
            String apiKey = TraderActivity.preferences.getString(exchangeId + "ApiKey", "");
            String secretKey = TraderActivity.preferences.getString(exchangeId + "SecretKey", "");

            // If use settings are not null, set them in exchange specifications
            if (!(username).equals(""))
                exchangeSpecification.setUserName(username);

            if (!(password).equals(""))
                exchangeSpecification.setPassword(password);

            if (!(apiKey).equals(""))
                exchangeSpecification.setApiKey(apiKey);

            if (!(secretKey).equals(""))
                exchangeSpecification.setSecretKey(secretKey);

            exchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
            // Interested in the private account functionality (authentication)
            accountService = exchange.getPollingAccountService();
            tradeService = exchange.getPollingTradeService();

            connectionGood = true;
            return true;
            //return false;
        }
        catch (Exception e)
        {//check the keys if a problem. trigger a dialog
            e.printStackTrace();
            return false;
        }
    }

    public void setReferenceTicker()
    {
        this.referenceTicker = lastTicker;
    }

    public BitcoiniumTicker getReferenceTicker()
    {
        return this.referenceTicker;
    }

    public BitcoiniumTicker getLastTicker()
    {
        return this.lastTicker;
    }

    public void queryLastTicker()
    {
        try
        {
            // Use the factory to get Bitcoinium exchange API using default settings
            BitcoiniumTicker bitcoiniumTicker = bitcoiniumMarketDataService.getBitcoiniumTicker(TraderActivity.tradableIdentifier, TraderActivity.exchangeInfo.getShortName().toUpperCase() + "_" + TraderActivity.transactionCurrency);

            if (bitcoiniumTicker != null && bitcoiniumTicker.getLast().floatValue() != 0)
            {
                //create a new ticker with the current time.
                this.lastTicker = bitcoiniumTicker;

                synchronized (tradesLock)
                {
                    if (trades != null && trades.size() > 0)
                    {

                        long dtInSec = getTimeFromLastUpdate();
                        System.out.println("TIME FROM LAST TICKER IN ARRAY: " + dtInSec + ", targetInterval=" + getTargetTimeIntervalFromPrefsInSec());
                        if (dtInSec >= getTargetTimeIntervalFromPrefsInSec())
                        {//need to add this to the array and chop off the front.
                            trades.add(bitcoiniumTicker);
                            if (trades.size() > TraderActivity.CHART_TARGET_RESOLUTION)
                            {
                                trades.removeFirst();
                            }
                            System.out.println("adding to trade array and removing the first.");
                        }
                        else
                        {//just replace the last ticker but keep the time the same.
                            trades.set(trades.size() - 1, bitcoiniumTicker);
                        }
                    }

                    mainActivity.getPainter().setTradeHistoryPath();
                    mainActivity.onAccountInfoUpdate();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public long getExchangeDelay()
    {
        if (orderBook == null) return 0;

        return (System.currentTimeMillis() - orderBook.getBitcoiniumTicker().getTimestamp()) / 1000;
    }

    public long getTimeFromLastUpdate()
    {

        return (System.currentTimeMillis() - trades.getLast().getTimestamp()) / 1000;
    }

    public float getTargetTimeIntervalFromPrefsInSec()
    {
        String timewindow = TraderActivity.preferences.getString("time_window", "TWENTY_FOUR_HOURS");
        if (timewindow.equalsIgnoreCase("ONE_HOUR"))
        {
            return (float) TimeUnit.MINUTES.toSeconds(60l) / TraderActivity.CHART_TARGET_RESOLUTION;
        }
        else if (timewindow.equalsIgnoreCase("THREE_HOURS"))
        {
            return (float) TimeUnit.HOURS.toSeconds(3l) / TraderActivity.CHART_TARGET_RESOLUTION;
        }
        else if (timewindow.equalsIgnoreCase("TWELVE_HOURS"))
        {
            return (float) TimeUnit.HOURS.toSeconds(12l) / TraderActivity.CHART_TARGET_RESOLUTION;
        }
        else if (timewindow.equalsIgnoreCase("TWENTY_FOUR_HOURS"))
        {
            return (float) TimeUnit.HOURS.toSeconds(24l) / TraderActivity.CHART_TARGET_RESOLUTION;
        }
        else if (timewindow.equalsIgnoreCase("THREE_DAYS"))
        {
            return (float) TimeUnit.DAYS.toSeconds(3l) / TraderActivity.CHART_TARGET_RESOLUTION;
        }
        else if (timewindow.equalsIgnoreCase("SEVEN_DAYS"))
        {
            return (float) TimeUnit.DAYS.toSeconds(7l) / TraderActivity.CHART_TARGET_RESOLUTION;
        }
        else if (timewindow.equalsIgnoreCase("THIRTY_DAYS"))
        {
            return (float) TimeUnit.DAYS.toSeconds(30l) / TraderActivity.CHART_TARGET_RESOLUTION;
        }
        else if (timewindow.equalsIgnoreCase("TWO_MONTHS"))
        {
            return (float) TimeUnit.DAYS.toSeconds(30l) * 2l / TraderActivity.CHART_TARGET_RESOLUTION;
        }
        return 0;
    }

    public boolean queryOrderBook()
    {

        this.pricewindow = TraderActivity.preferences.getString("price_window", "FIVE_PERCENT");
        try
        {
            // Use the factory to get Bitcoinium exchange API using default settings
            BitcoiniumOrderbook bitcoiniumOrderbook = bitcoiniumMarketDataService.getBitcoiniumOrderbook(TraderActivity.tradableIdentifier, TraderActivity.exchangeInfo.getShortName().toUpperCase() + "_" + TraderActivity.transactionCurrency, this.pricewindow);

            if (bitcoiniumOrderbook != null)
            {
                this.lastOrderBook = orderBook;
                this.orderBook = bitcoiniumOrderbook;
            }

            mainActivity.getPainter().setBidAskPaths();//triggers the painter
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            connectionGood = false;
            return false;
        }
    }


    public boolean queryTradeHistory()
    {
        System.out.println("queryTradeHistory");
        try
        {
            synchronized (tradesLock)
            {
                //get the data. It comes pre-sorted.
                this.trades = new LinkedList<BitcoiniumTicker>();
                this.timewindow = TraderActivity.preferences.getString("time_window", "TWENTY_FOUR_HOURS");
                // Use the factory to get Bitcoinium exchange API using default settings
                BitcoiniumTickerHistory tickerHistory = bitcoiniumMarketDataService.getBitcoiniumTickerHistory(TraderActivity.tradableIdentifier, TraderActivity.exchangeInfo.getShortName().toUpperCase() + "_" + TraderActivity.transactionCurrency, this.timewindow);

                System.out.println("TIME LAST=" + tickerHistory.getBitcoiniumTicker().getTimestamp());
                trades = new LinkedList<BitcoiniumTicker>(Arrays.asList(tickerHistory.getCondensedTickers()));

                this.lastTicker = trades.getLast();
                this.lastOrderBook = null;
                setReferenceTicker();
                mainActivity.getPainter().setTradeHistoryPath();
            }

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            connectionGood = false;
            return false;
        }
    }

    public boolean queryRemoteAccountInfo()
    {
        try
        {
            if(exchangeSpecification.getApiKey() == null)
                return false;

            AccountInfo info = accountService.getAccountInfo();
            if (info != null)
            {
                accountInfo = info;
            }

            System.out.println("AccountInfo Received: " + info);
            mainActivity.onAccountInfoUpdate();
            connectionGood = true;
            return true;
        }
        catch (Exception e)
        {
            connectionGood = false;
            Log.d("Account Info", e.toString());
            e.printStackTrace();
            return false;
        }
    }

    public boolean queryOpenOrders()
    {
        try
        {
            if(exchangeSpecification.getApiKey() == null)
                return false;

            OpenOrders orders = tradeService.getOpenOrders();
            if (orders != null)
            {
                this.openOrders = orders.getOpenOrders();
            }
            return true;
        }
        catch (Exception e)
        {
            connectionGood = false;
            return false;
        }
    }

    public AccountInfo getAccountInfo()
    {
        return accountInfo;
    }

    public float getTotalFiatBalance(String fiatSymbol)
    {

        if (accountInfo == null)
        {
            return 0.0f;
        }

        return accountInfo.getBalance(TraderActivity.transactionCurrency).floatValue();
    }

    public float getTotalBTC()
    {
        float totalBTC = 0;

        if (accountInfo == null)
        {
            return totalBTC;
        }

        List<Wallet> wallets = accountInfo.getWallets();

        for (Wallet wallet : wallets)
        {
            if (wallet.getCurrency().equals("BTC"))
            {
                totalBTC += wallet.getBalance().floatValue();
            }
        }
        return totalBTC;
    }

    public float getAccountValue(String fiatSymbol)
    {

        float balance = getTotalBTC() * getLastTicker().getLast().floatValue() + getTotalFiatBalance(fiatSymbol);

        if (balance > lastAccountBalance)
        {
            accountValueIncreasing = true;
        }
        else
        {
            accountValueIncreasing = false;
        }

        lastAccountBalance = balance;
        return balance;
    }

    public boolean accountValueIncreasing()
    {
        return accountValueIncreasing;
    }

    public List<LimitOrder> getOpenOrders()
    {
        return openOrders;
    }

    public boolean isConnectionGood()
    {
        return connectionGood;
    }

    public String getTimewindow()
    {
        return timewindow;
    }

    public LinkedList<BitcoiniumTicker> getTrades()
    {
        return trades;
    }

    public BitcoiniumOrderbook getOrderBook()
    {
        return orderBook;
    }

//	public float getAskChange() {
//		return askChange;
//	}
//
//	public float getBidChange() {
//		return bidChange;
//	}

    public String getPricewindow()
    {
        return pricewindow;
    }

    public BitcoiniumOrderbook getLastOrderBook()
    {
        return lastOrderBook;
    }

    public PollingTradeService getTradeService()
    {
        return tradeService;
    }

    public void generateToast(String msg, int duration)
    {
        Context context = mainActivity.getApplicationContext();
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }


//	public List<LimitOrder> getPendingOrders() {
//		return pendingOrders;
//	}
}
