
package com.veken0m.bitcoinium;

import java.io.IOException;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.veken0m.bitcoinium.exchanges.Exchange;
import com.veken0m.utils.CurrencyUtils;
//import com.veken0m.utils.KarmaAdsUtils;
import com.veken0m.utils.Utils;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

public class OrderbookActivity extends SherlockActivity implements OnItemSelectedListener {

    private final static Handler mOrderHandler = new Handler();
    private Dialog dialog = null;
    private List<LimitOrder> listAsks = null;
    private List<LimitOrder> listBids = null;
    //private List<BigDecimal> listAsksPrice = null;
    //private List<BigDecimal> listBidsPrice = null;
    //private List<BigDecimal> listAsksAmount = null;
    //private List<BigDecimal> listBidsAmount = null;

    private static Exchange exchange = null;

    /**
     * List of preference variables
     */
    private static int pref_highlightHigh = 0;
    private static int pref_highlightLow = 0;
    private static int pref_orderbookLimiter = 0;
    private static Boolean pref_enableHighlight = true;
    private static Boolean pref_showCurrencySymbol = true;

    private static CurrencyPair currencyPair = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.orderbook);
        getSupportActionBar().show();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            exchange = new Exchange(this, extras.getString("exchange"));
        } else {
            // TODO: generation error message
            exchange = new Exchange(this, "MtGoxExchange");
        }

        readPreferences(this);
        createCurrencyDropdown();
        viewOrderbook();

        //KarmaAdsUtils.initAd(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_preferences)
            startActivity(new Intent(this, PreferencesActivity.class));

        if (item.getItemId() == R.id.action_refresh)
            viewOrderbook();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.orderbook);

        if(listAsks != null && listBids != null){
            createCurrencyDropdown();
            drawOrderbookUI();
        } else {
            // Fetch data
            viewOrderbook();
        }
    }

    /**
     * Fetch the OrderbookActivity and split into Ask/Bids lists
     */
    boolean getOrderBook() {

        if(listAsks != null && listBids != null){
            listAsks.clear();
            listBids.clear();
        }

        final PollingMarketDataService marketData = ExchangeFactory.INSTANCE
                .createExchange(exchange.getClassName())
                .getPollingMarketDataService();

        OrderBook orderbook;
        try {
            orderbook = marketData.getOrderBook(currencyPair.baseCurrency, currencyPair.counterCurrency);
        } catch (IOException e) {
            errorOccured();
            return false;
        }

        if(orderbook != null){
            // Limit OrderbookActivity orders drawn to improve performance
            int length = (orderbook.getAsks().size() < orderbook.getBids().size()) ? orderbook.getAsks().size() : orderbook.getBids().size();
            if (pref_orderbookLimiter != 0 && pref_orderbookLimiter < length)
                length = pref_orderbookLimiter;

            listAsks = orderbook.getAsks().subList(0, length);
            listBids = orderbook.getBids().subList(0, length);
        } else {
                errorOccured();
                return false;
        }
        return true;
    }

    /**
     * Draw the Orders to the screen in a table
     */
    void drawOrderbookUI() {

        final TableLayout orderbookTable = (TableLayout) findViewById(R.id.orderlist);
        if(orderbookTable != null) {

            orderbookTable.removeAllViews();

            removeLoadingSpinner();
            setOrderBookHeader();

            boolean bBackGroundColor = true;

            String currencySymbolBTC, currencySymbol;
            currencySymbolBTC = currencySymbol = "";
            if (pref_showCurrencySymbol) {
                currencySymbolBTC = " " + currencyPair.baseCurrency;
                currencySymbol = Utils.getCurrencySymbol(currencyPair.counterCurrency);
            }

            for (int i = 0; i < listBids.size(); i++) {
                final TableRow tr1 = new TableRow(this);
                final TextView tvAskAmount = new TextView(this);
                final TextView tvAskPrice = new TextView(this);
                final TextView tvBidPrice = new TextView(this);
                final TextView tvBidAmount = new TextView(this);

                final LimitOrder limitorderBid = listBids.get(i);
                final LimitOrder limitorderAsk = listAsks.get(i);

                float bidPrice = limitorderBid.getLimitPrice().getAmount().floatValue();
                float bidAmount = limitorderBid.getTradableAmount().floatValue();
                float askPrice = limitorderAsk.getLimitPrice().getAmount().floatValue();
                float askAmount = limitorderAsk.getTradableAmount().floatValue();

                tvBidAmount.setText(Utils.formatDecimal(bidAmount, 4, false) + currencySymbolBTC);
                tvBidAmount.setLayoutParams(Utils.symbolParams);
                tvBidAmount.setGravity(Gravity.CENTER);
                tvAskAmount.setText(Utils.formatDecimal(askAmount, 4, false) + currencySymbolBTC);
                tvAskAmount.setLayoutParams(Utils.symbolParams);
                tvAskAmount.setGravity(Gravity.CENTER);

                tvBidPrice.setText(currencySymbol + Utils.formatDecimal(bidPrice, 3, false));
                tvBidPrice.setLayoutParams(Utils.symbolParams);
                tvBidPrice.setGravity(Gravity.CENTER);
                tvAskPrice.setText(currencySymbol + Utils.formatDecimal(askPrice, 3, false));
                tvAskPrice.setLayoutParams(Utils.symbolParams);
                tvAskPrice.setGravity(Gravity.CENTER);

                // Text coloring for depth highlighting
                if(pref_enableHighlight){
                    int bidTextColor = depthColor(bidAmount);
                    int askTextColor = depthColor(askAmount);
                    tvBidAmount.setTextColor(bidTextColor);
                    tvBidPrice.setTextColor(bidTextColor);
                    tvAskAmount.setTextColor(askTextColor);
                    tvAskPrice.setTextColor(askTextColor);
                }

                // Toggle background color
                bBackGroundColor = !bBackGroundColor;
                if(bBackGroundColor)
                    tr1.setBackgroundColor(Color.BLACK);
                else
                    tr1.setBackgroundColor(Color.rgb(31, 31, 31));

                tr1.addView(tvBidPrice);
                tr1.addView(tvBidAmount);
                tr1.addView(tvAskPrice);
                tr1.addView(tvAskAmount);

                orderbookTable.addView(tr1);
            }
        } else {
            failedToDrawUI();
        }

    }

    private void viewOrderbook() {
        if(Utils.isConnected(getApplicationContext())){
            (new OrderbookThread()).start();
        } else {
            notConnected();
        }
    }

    private class OrderbookThread extends Thread {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startLoading();
                }
            });
            // if(exchangeName.equalsIgnoreCase("mtgox") &&
            // pref_currency.contains("USD")){
            // getXHubOrderBook();
            // } else {
            if(getOrderBook())
                mOrderHandler.post(mOrderView);
            // }
        }
    }

    private final Runnable mOrderView = new Runnable() {
        @Override
        public void run() {
                // if(exchangeName.equalsIgnoreCase("mtgox") &&
                // pref_currency.contains("USD")){
                // drawXHubOrderbookUI();
                // } else {
                drawOrderbookUI();
                // }
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        CurrencyPair prevCurrencyPair = currencyPair;
        currencyPair = CurrencyUtils.stringToCurrencyPair((String) parent.getItemAtPosition(pos));
        if (prevCurrencyPair != null && currencyPair != null && !currencyPair.equals(prevCurrencyPair))
            viewOrderbook();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing
    }

    private void errorOccured() {

        removeLoadingSpinner();

        if(dialog == null || !dialog.isShowing()){
            // Display error Dialog
            Resources res = getResources();
            String text = String.format(res.getString(R.string.connectionError),
                    res.getString(R.string.orderbook), exchange.getExchangeName());
            dialog = Utils.errorDialog(this, text);
        }
    }

    private void notConnected() {

        removeLoadingSpinner();

        if(dialog == null || !dialog.isShowing()){
            // Display error Dialog
            dialog = Utils.errorDialog(this, "No internet connection available", "Internet Connection");
        }
    }

    private void failedToDrawUI() {

        removeLoadingSpinner();
        if(dialog == null || !dialog.isShowing()){
            // Display error Dialog
            dialog = Utils.errorDialog(this, "A problem occurred when generating BitcoinAverage table", "Error");
        }
    }

    void startLoading() {
        TableLayout t1 = (TableLayout) findViewById(R.id.orderlist);
        if(t1 != null) t1.removeAllViews();
        LinearLayout loadingSpinner = (LinearLayout) findViewById(R.id.loadingSpinner);
        loadingSpinner.setVisibility(View.VISIBLE);
    }

    void removeLoadingSpinner() {
        LinearLayout loadingSpinner = (LinearLayout) findViewById(R.id.loadingSpinner);
        loadingSpinner.setVisibility(View.GONE);
    }

    void setOrderBookHeader() {
        TextView orderBookHeader = (TextView) findViewById(R.id.orderbook_header);
        orderBookHeader.setText(exchange.getExchangeName() + " " + currencyPair.baseCurrency + "/"
                + currencyPair.counterCurrency);
    }

    void createCurrencyDropdown() {
        // Re-populate the dropdown menu
        final String[] dropdownValues = getResources().getStringArray(
                getResources().getIdentifier(exchange.getIdentifier() + "currencies", "array",
                        this.getPackageName()));
        Spinner spinner = (Spinner) findViewById(R.id.orderbook_currency_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dropdownValues);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    int depthColor(float amount) {
            if ((int) amount < pref_highlightLow)
                return Color.RED;
            if ((int) amount >= pref_highlightLow)
                return Color.YELLOW;
            if ((int) amount >= pref_highlightHigh)
                return Color.GREEN;
        return Color.GRAY;
    }

    private static void readPreferences(Context context) {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_enableHighlight = prefs.getBoolean("highlightPref", true);
        pref_highlightHigh = Integer.parseInt(prefs.getString("highlightUpper",
                "50"));
        pref_highlightLow = Integer.parseInt(prefs.getString("highlightLower",
                "10"));
        currencyPair = CurrencyUtils.stringToCurrencyPair(
                prefs.getString(exchange.getIdentifier() + "CurrencyPref", exchange.getDefaultCurrency()));
        pref_showCurrencySymbol = prefs.getBoolean("showCurrencySymbolPref",
                true);
        try {
            pref_orderbookLimiter = Integer.parseInt(prefs.getString(
                    "orderbookLimiterPref", "100"));
        } catch (Exception e) {
            pref_orderbookLimiter = 100;
            // If preference is not set a valid integer set to "100"
            Editor editor = prefs.edit();
            editor.putString("orderbookLimiterPref", "100");
            editor.commit();
        }
    }

    /**
     * Draw the Orders to the screen in a table
     public void drawXHubOrderbookUI() {

     final TableLayout t1 = (TableLayout) findViewById(R.id.orderlist);

     removeLoadingSpinner();
     setOrderBookHeader();

     LayoutParams params = new LayoutParams(
     LayoutParams.WRAP_CONTENT,
     LayoutParams.WRAP_CONTENT, 1f);

     String currencySymbolBTC = "";
     String currencySymbol = "";

     if (pref_showCurrencySymbol) {
     currencySymbolBTC = " " + currencyPair.baseCurrency;
     currencySymbol = Utils.getCurrencySymbol(currencyPair.counterCurrency);
     }

     float previousBidAmount = 0;
     float previousAskAmount = 0;

     for (int i = 0; i < listAsksPrice.size(); i++) {

     final TableRow tr1 = new TableRow(this);
     final TextView tvAskAmount = new TextView(this);
     final TextView tvAskPrice = new TextView(this);
     final TextView tvBidPrice = new TextView(this);
     final TextView tvBidAmount = new TextView(this);
     tr1.setId(100 + i);

     float bidPrice = listBidsPrice.get(i).floatValue();
     float bidAmount = listBidsAmount.get(i).floatValue() - previousBidAmount;
     float askPrice = listAsksPrice.get(i).floatValue();
     float askAmount = listAsksAmount.get(i).floatValue() - previousAskAmount;

     previousAskAmount = listAsksAmount.get(i).floatValue();
     previousBidAmount = listBidsAmount.get(i).floatValue();

     final String sBidPrice = Utils.formatDecimal(bidPrice, 5, false);
     final String sBidAmount = Utils.formatDecimal(bidAmount, 2, false);
     final String sAskPrice = Utils.formatDecimal(askPrice, 5, false);
     final String sAskAmount = Utils.formatDecimal(askAmount, 2, false);

     tvBidAmount.setText(sBidAmount + currencySymbolBTC);
     tvBidAmount.setLayoutParams(params);
     tvBidAmount.setGravity(Gravity.CENTER);
     tvAskAmount.setText(sAskAmount + currencySymbolBTC);
     tvAskAmount.setLayoutParams(params);
     tvAskAmount.setGravity(Gravity.CENTER);

     tvBidPrice.setText(currencySymbol + sBidPrice);
     tvBidPrice.setLayoutParams(params);
     tvBidPrice.setGravity(Gravity.CENTER);
     tvAskPrice.setText(currencySymbol + sAskPrice);
     tvAskPrice.setLayoutParams(params);
     tvAskPrice.setGravity(Gravity.CENTER);

     int bidTextColor = depthColor(bidAmount);
     int askTextColor = depthColor(askAmount);

     tvBidAmount.setTextColor(bidTextColor);
     tvBidPrice.setTextColor(bidTextColor);
     tvAskAmount.setTextColor(askTextColor);
     tvAskPrice.setTextColor(askTextColor);

     try {
     tr1.addView(tvBidPrice);
     tr1.addView(tvBidAmount);
     tr1.addView(tvAskPrice);
     tr1.addView(tvAskAmount);

     t1.addView(tr1);
     addDivider(t1);

     } catch (Exception e) {
     e.printStackTrace();
     }
     }

     }
     */


    /**
     * Fetch the OrderbookActivity and split into Ask/Bids lists
     public void getXHubOrderBook() {
     try {
     currencyPair = CurrencyUtils.stringToCurrencyPair(pref_currency);

     HttpClient client = new DefaultHttpClient();
     HttpGet post = new HttpGet(
     "http://bitcoinium.com:9090/service/orderbook?exchange=mtgox&pair=BTC_USD&pricewindow=1p");
     HttpResponse response = client.execute(post);
     ObjectMapper mapper = new ObjectMapper();

     com.veken0m.bitcoinium.webservice.dto.Orderbook orderbook = mapper.readValue(
     new InputStreamReader(response.getEntity()
     .getContent(), "UTF-8"),
     com.veken0m.bitcoinium.webservice.dto.Orderbook.class);

     // Limit OrderbookActivity orders drawn to speed up performance
     int size = orderbook.getBidPriceList().size();
     if (orderbook.getAskPriceList().size() < size) {
     size = orderbook.getAskPriceList().size();
     }

     if (pref_orderbookLimiter != 0 && pref_orderbookLimiter < size) {
     size = pref_orderbookLimiter;
     }

     listAsksPrice = orderbook.getAskPriceList().subList(0, size);
     listBidsPrice = orderbook.getBidPriceList().subList(0, size);
     listAsksAmount = orderbook.getAskVolumeList().subList(0, size);
     listBidsAmount = orderbook.getBidVolumeList().subList(0, size);

     } catch (Exception e) {
     runOnUiThread(new Runnable() {
    @Override
    public void run() {
    connectionFailed();
    }
    });
     e.printStackTrace();
     }
     }
     */
}
