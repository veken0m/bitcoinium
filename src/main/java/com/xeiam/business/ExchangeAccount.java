package com.xeiam.business;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.xeiam.tasks.CancelOrderTask;
import com.xeiam.xbtctrader.XTraderActivity;
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
import com.xeiam.xchange.service.polling.PollingAccountService;
import com.xeiam.xchange.service.polling.PollingTradeService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExchangeAccount {
    private static final String TAG = "ExchangeAccount";
    boolean connectionGood = false;
    float lastAccountBalance = 0;
    boolean accountValueIncreasing = true;
    XTraderActivity mainActivity;
    private String timewindow;
    private String pricewindow;
    private ExchangeSpecification exchangeSpecification;
    private BitcoiniumMarketDataServiceRaw bitcoiniumMarketDataService;
    private Exchange exchange;
    //private List<LimitOrder> pendingOrders=new ArrayList<LimitOrder>();
    private PollingAccountService accountService;
    private PollingTradeService tradeService;
    private AccountInfo accountInfo;
    private List<LimitOrder> openOrders = new ArrayList<LimitOrder>();
    private BitcoiniumTicker referenceTicker;


//	private float askChange=0;
//	private float bidChange=0;
    private LinkedList<BitcoiniumTicker> trades;
    private BitcoiniumOrderbook orderBook;
    private BitcoiniumOrderbook lastOrderBook;
    private BitcoiniumTicker lastTicker;

    public ExchangeAccount(XTraderActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    public boolean dataIsSet() {
        if (orderBook == null) {
            return false;
        }
        if (lastOrderBook == null) {
            return false;
        }
        if (trades == null) {
            return false;
        }
        if (referenceTicker == null) {
            return false;
        }
        if (lastTicker == null) {
            return false;
        }

        return true;
    }


    public void cancelLimitOrder(String id) {
        CancelOrderTask cancelOrderTask = new CancelOrderTask(id, this);
        cancelOrderTask.go();
        generateToast("Cancelation order sent to exchange.", Toast.LENGTH_SHORT);
    }

    public void placeLimitOrder(float price, float amount, OrderType orderType) {

        BigDecimal tradeableAmount = new BigDecimal(amount).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal limitPrice = new BigDecimal(XTraderActivity.fiveDecimalFormatter.format(price));

        LimitOrder limitOrder = new LimitOrder(orderType, tradeableAmount, new CurrencyPair(XTraderActivity.tradableIdentifier, XTraderActivity.transactionCurrency), null, null, limitPrice);
        Log.v(TAG, "Placing Order: " + limitOrder);

        generateToast("Trading is currently disabled. \nPlease contact developer to become a tester", Toast.LENGTH_LONG);
        //SubmitOrderTask submitOrderTask=new SubmitOrderTask(limitOrder, this); // execute network task on another thread.
        //submitOrderTask.go();
    }

    public boolean init() {

        try {

            ExchangeSpecification bitcoiniumExchangeSpec = new ExchangeSpecification(BitcoiniumExchange.class.getName());

            // TODO: input BitcoiniumWS API key before release!
            bitcoiniumExchangeSpec.setApiKey("INSERT_KEY_HERE");
            Exchange bitcoiniumExchange = ExchangeFactory.INSTANCE.createExchange(bitcoiniumExchangeSpec);
            bitcoiniumMarketDataService = (BitcoiniumMarketDataServiceRaw) bitcoiniumExchange.getPollingMarketDataService();

            // Use the factory to get the version 2 MtGox exchange API using default settings
            exchangeSpecification = new ExchangeSpecification(XTraderActivity.exchangeInfo.getClassName());

            //String username = XTraderActivity.preferences.getString(XTraderActivity.exchangeInfo.getIdentifier() + "Username", "");
            //String password = XTraderActivity.preferences.getString(XTraderActivity.exchangeInfo.getIdentifier() + "Password", "");
            //String apiKey = XTraderActivity.preferences.getString(XTraderActivity.exchangeInfo.getIdentifier() + "ApiKey", "");
            //String secretKey = XTraderActivity.preferences.getString(XTraderActivity.exchangeInfo.getIdentifier() + "SecretKey", "");


            //exchangeSpecification.setUserName(username);
            //exchangeSpecification.setPassword(password);
            //exchangeSpecification.setSecretKey(secretKey);
            //exchangeSpecification.setApiKey(apiKey);

            exchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
            // Interested in the private account functionality (authentication)
            accountService = exchange.getPollingAccountService();
            tradeService = exchange.getPollingTradeService();

            connectionGood = true;
            //return true;
            return false;
        } catch (Exception e) {//check the keys if a problem. trigger a dialog
            return false;
        }
    }

    public void setReferenceTicker() {
        this.referenceTicker = lastTicker;
    }

    public BitcoiniumTicker getReferenceTicker() {
        return this.referenceTicker;
    }

    public BitcoiniumTicker getLastTicker() {
        return this.lastTicker;
    }

    public void queryLastTicker() {
        try {
            // Use the factory to get Bitcoinium exchange API using default settings
            BitcoiniumTicker bitcoiniumTicker = bitcoiniumMarketDataService.getBitcoiniumTicker(XTraderActivity.tradableIdentifier, XTraderActivity.exchangeInfo.getIdentifier().toUpperCase() + "_" + XTraderActivity.transactionCurrency);

            if (bitcoiniumTicker != null && bitcoiniumTicker.getLast().floatValue() != 0) {
                //create a new ticker with the current time.
                this.lastTicker = bitcoiniumTicker;

                if (trades != null && trades.size() > 0) {
                    long dtInSec = getTimeFromLastUpdate();
                    System.out.println("TIME FROM LAST TICKER IN ARRAY: " + dtInSec + ", targetInterval=" + getTargetTimeIntervalFromPrefsInSec());
                    if (dtInSec >= getTargetTimeIntervalFromPrefsInSec()) {//need to add this to the array and chop off the front.
                        trades.add(bitcoiniumTicker);
                        if (trades.size() > XTraderActivity.CHART_TARGET_RESOLUTION) {
                            trades.remove(0);
                        }
                        System.out.println("adding to trade array and removing the first.");
                    } else {//just replace the last ticker but keep the time the same.
                        trades.set(trades.size() - 1, bitcoiniumTicker);
                    }

                    mainActivity.getPainter().setTradeHistoryPath();
                    mainActivity.onAccountInfoUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public long getExchangeDelay() {
        return (System.currentTimeMillis() - lastTicker.getTimestamp()) / 1000;
    }

    public long getTimeFromLastUpdate() {

        return (System.currentTimeMillis() - trades.get(trades.size() - 1).getTimestamp()) / 1000;

    }

    public float getTargetTimeIntervalFromPrefsInSec() {
        String timewindow = XTraderActivity.preferences.getString("timewindow", "24h");
        if (timewindow.equalsIgnoreCase("10m")) {
            return (float) TimeUnit.MINUTES.toSeconds(10l) / XTraderActivity.CHART_TARGET_RESOLUTION;
        } else if (timewindow.equalsIgnoreCase("1h")) {
            return (float) TimeUnit.MINUTES.toSeconds(60l) / XTraderActivity.CHART_TARGET_RESOLUTION;
        } else if (timewindow.equalsIgnoreCase("3h")) {
            return (float) TimeUnit.HOURS.toSeconds(3l) / XTraderActivity.CHART_TARGET_RESOLUTION;
        } else if (timewindow.equalsIgnoreCase("12h")) {
            return (float) TimeUnit.HOURS.toSeconds(12l) / XTraderActivity.CHART_TARGET_RESOLUTION;
        } else if (timewindow.equalsIgnoreCase("24h")) {
            return (float) TimeUnit.HOURS.toSeconds(24l) / XTraderActivity.CHART_TARGET_RESOLUTION;
        } else if (timewindow.equalsIgnoreCase("3d")) {
            return (float) TimeUnit.DAYS.toSeconds(3l) / XTraderActivity.CHART_TARGET_RESOLUTION;
        } else if (timewindow.equalsIgnoreCase("7d")) {
            return (float) TimeUnit.DAYS.toSeconds(7l) / XTraderActivity.CHART_TARGET_RESOLUTION;
        } else if (timewindow.equalsIgnoreCase("30d")) {
            return (float) TimeUnit.DAYS.toSeconds(30l) / XTraderActivity.CHART_TARGET_RESOLUTION;
        } else if (timewindow.equalsIgnoreCase("6m")) {
            return (float) TimeUnit.DAYS.toSeconds(30l) * 6l / XTraderActivity.CHART_TARGET_RESOLUTION;
        }
        return 0;

    }

    public boolean queryOrderBook() {

        this.pricewindow = XTraderActivity.preferences.getString("pricewindow", "5p");
        try {
            // Use the factory to get Bitcoinium exchange API using default settings
            BitcoiniumOrderbook bitcoiniumOrderbook = bitcoiniumMarketDataService.getBitcoiniumOrderbook(XTraderActivity.tradableIdentifier, XTraderActivity.exchangeInfo.getIdentifier().toUpperCase() + "_" + XTraderActivity.transactionCurrency, this.pricewindow);

            if (bitcoiniumOrderbook != null) {
                this.lastOrderBook = orderBook;
                this.orderBook = bitcoiniumOrderbook;
                //this.lastTicker=newOrderBook.getTicker();
            }

            mainActivity.getPainter().setBidAskPaths();//triggers the painter
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            connectionGood = false;
            return false;
        }
    }


    public boolean queryTradeHistory() {
        System.out.println("queryTradeHistory");
        try {
            //get the data. It comes pre-sorted.
            this.trades = new LinkedList<BitcoiniumTicker>();
            this.timewindow = XTraderActivity.preferences.getString("timewindow", "24h");
            // Use the factory to get Bitcoinium exchange API using default settings
            BitcoiniumTickerHistory tickerHistory = bitcoiniumMarketDataService.getBitcoiniumTickerHistory(XTraderActivity.tradableIdentifier, XTraderActivity.exchangeInfo.getIdentifier().toUpperCase() + "_" + XTraderActivity.transactionCurrency, this.timewindow);

            long timeLast = tickerHistory.getBaseTimestamp() * 1000;
            System.out.println("TIME LAST=" + timeLast);
            for (int i = 0; i < tickerHistory.getPriceHistoryList().size(); i++) {
                BitcoiniumTicker ticker = new BitcoiniumTicker(tickerHistory.getPriceHistoryList().get(i), timeLast + (long) tickerHistory.getTimeStampOffsets().get(i) * 1000, new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), "N");
                trades.add(ticker);
                timeLast = ticker.getTimestamp();
            }

            this.lastTicker = trades.get(trades.size() - 1);
            this.lastOrderBook = null;
            setReferenceTicker();
            mainActivity.getPainter().setTradeHistoryPath();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            connectionGood = false;
            return false;
        }
    }

    public boolean queryRemoteAccountInfo() {
        try {
            AccountInfo info = accountService.getAccountInfo();
            if (info != null) {
                accountInfo = info;
            }

            System.out.println("AcountInfo Received: " + info);
            mainActivity.onAccountInfoUpdate();
            connectionGood = true;
            return true;
        } catch (Exception e) {
            connectionGood = false;
            Log.d("Account Info", e.toString());
            e.printStackTrace();
            return false;
        }
    }

    public boolean queryOpenOrders() {
        try {
            OpenOrders orders = tradeService.getOpenOrders();
            if (orders != null) {
                this.openOrders = orders.getOpenOrders();
            }
            return true;
        } catch (Exception e) {
            connectionGood = false;
            return false;
        }
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public float getTotalFiatBalance(String fiatSymbol) {

        if (accountInfo == null) {
            return 0.0f;
        }

        return accountInfo.getBalance(XTraderActivity.transactionCurrency).floatValue();
    }

    public float getTotalBTC() {
        float totalBTC = 0;

        if (accountInfo == null) {
            return totalBTC;
        }

        List<Wallet> wallets = accountInfo.getWallets();

        for (int i = 0; i < wallets.size(); i++) {
            if (wallets.get(i).getCurrency().equals("BTC")) {
                totalBTC += wallets.get(i).getBalance().floatValue();
            }
        }
        return totalBTC;
    }

    public float getAccountValue(String fiatSymbol) {

        float balance = getTotalBTC() * getLastTicker().getLast().floatValue() + getTotalFiatBalance(fiatSymbol);

        if (balance > lastAccountBalance) {
            accountValueIncreasing = true;
        } else {
            accountValueIncreasing = false;
        }

        lastAccountBalance = balance;
        return balance;
    }

    public boolean accountValueIncreasing() {
        return accountValueIncreasing;
    }

    public List<LimitOrder> getOpenOrders() {
        return openOrders;
    }

    public boolean isConnectionGood() {
        return connectionGood;
    }

    public String getTimewindow() {
        return timewindow;
    }

    public LinkedList<BitcoiniumTicker> getTrades() {
        return trades;
    }

    public BitcoiniumOrderbook getOrderBook() {
        return orderBook;
    }

//	public float getAskChange() {
//		return askChange;
//	}
//
//	public float getBidChange() {
//		return bidChange;
//	}

    public String getPricewindow() {
        return pricewindow;
    }

    public BitcoiniumOrderbook getLastOrderBook() {
        return lastOrderBook;
    }

    public PollingTradeService getTradeService() {
        return tradeService;
    }

    public void generateToast(String msg, int duration) {
        Context context = mainActivity.getApplicationContext();
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }


//	public List<LimitOrder> getPendingOrders() {
//		return pendingOrders;
//	}


}
