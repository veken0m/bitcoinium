
package com.veken0m.bitcoinium;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veken0m.bitcoinium.exchanges.Exchange;
import com.veken0m.utils.CurrencyUtils;
import com.veken0m.utils.KarmaAdsUtils;
import com.veken0m.utils.Utils;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import com.xeiam.xchange.service.polling.PollingMarketDataService.OrderBookType;

public class OrderbookActivity extends SherlockActivity implements OnItemSelectedListener {

    final static Handler mOrderHandler = new Handler();
    protected static String exchangeName = "";
    protected String xchangeExchange = null;
    protected List<LimitOrder> listAsks;
    protected List<LimitOrder> listBids;
    
    protected List<BigDecimal> listAsksPrice;
    protected List<BigDecimal> listBidsPrice;
    protected List<BigDecimal> listAsksAmount;
    protected List<BigDecimal> listBidsAmount;

    /**
     * List of preference variables
     */
    static int pref_highlightHigh;
    static int pref_highlightLow;
    static int pref_orderbookLimiter;
    static Boolean pref_enableHighlight;
    static String pref_currency;
    static Boolean pref_showCurrencySymbol;

    private Spinner spinner;
    private ArrayAdapter<String> dataAdapter;
    String prefix = "mtgox";
    CurrencyPair currencyPair = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderbook);

        ActionBar actionbar = getSupportActionBar();
        actionbar.show();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            exchangeName = extras.getString("exchange");
        }

        Exchange exchange = new Exchange(this, exchangeName);

        exchangeName = exchange.getExchangeName();
        xchangeExchange = exchange.getClassName();
        prefix = exchange.getIdentifier();
        readPreferences(getApplicationContext(), prefix, exchange.getDefaultCurrency());
        
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
        if (item.getItemId() == R.id.action_preferences) {
            startActivity(new Intent(this, PreferencesActivity.class));
        }
        if (item.getItemId() == R.id.action_refresh) {
            viewOrderbook();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.orderbook);

        try {
            createCurrencyDropdown();
            drawOrderbookUI();
        } catch (Exception e) {
            viewOrderbook();
        }
    }

    /**
     * Fetch the OrderbookActivity and split into Ask/Bids lists
     */
    public void getOrderBook() {
        try {

            currencyPair = CurrencyUtils.stringToCurrencyPair(pref_currency);

            final PollingMarketDataService marketData = ExchangeFactory.INSTANCE
                    .createExchange(xchangeExchange)
                    .getPollingMarketDataService();
            
            // TODO: Set to partial orderbook when working for all exchanges
            OrderBook orderbook = marketData.getOrderBook(currencyPair.baseCurrency,
                    currencyPair.counterCurrency);//, OrderBookType.PARTIAL);

            // Limit OrderbookActivity orders drawn to speed up performance
            int length = 0;
            if (orderbook.getAsks().size() < orderbook.getBids().size()) {
                length = orderbook.getAsks().size();
            } else {
                length = orderbook.getBids().size();
            }

            if (pref_orderbookLimiter != 0 && pref_orderbookLimiter < length) {
                length = pref_orderbookLimiter;
            }

            listAsks = orderbook.getAsks().subList(0, length);
            listBids = orderbook.getBids().subList(0, length);

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

    /**
     * Fetch the OrderbookActivity and split into Ask/Bids lists
     */
    public void getXHubOrderBook() {
        try {
            currencyPair = CurrencyUtils.stringToCurrencyPair(pref_currency);

            HttpClient client = new DefaultHttpClient();
            HttpGet post = new HttpGet("http://bitcoinium.com:9090/service/orderbook?exchange=mtgox&pair=BTC_USD&pricewindow=1p");
            HttpResponse response = client.execute(post);
            ObjectMapper mapper = new ObjectMapper();

            com.veken0m.bitcoinium.webservice.dto.Orderbook orderbook = mapper.readValue(new InputStreamReader(response.getEntity()
                    .getContent(), "UTF-8"), com.veken0m.bitcoinium.webservice.dto.Orderbook.class);

            // Limit OrderbookActivity orders drawn to speed up performance
            int size = orderbook.getBidPriceList().size();
            if(orderbook.getAskPriceList().size() < size){
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

    /**
     * Draw the Orders to the screen in a table
     */
    public void drawOrderbookUI() {

        final TableLayout t1 = (TableLayout) findViewById(R.id.orderlist);
        t1.removeAllViews();
        
        stopLoading();
        setOrderBookHeader();

        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 1f);
        int backGroundColor = Color.rgb(31, 31, 31);
       
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
            tr1.setId(100 + i);

            final LimitOrder limitorderBid = listBids.get(i);
            final LimitOrder limitorderAsk = listAsks.get(i);

            float bidPrice = limitorderBid.getLimitPrice().getAmount()
                    .floatValue();
            float bidAmount = limitorderBid.getTradableAmount().floatValue();
            float askPrice = limitorderAsk.getLimitPrice().getAmount()
                    .floatValue();
            float askAmount = limitorderAsk.getTradableAmount().floatValue();

            final String sBidPrice = Utils.formatDecimal(bidPrice, 3, false);
            final String sBidAmount = Utils.formatDecimal(bidAmount, 4, false);
            final String sAskPrice = Utils.formatDecimal(askPrice, 3, false);
            final String sAskAmount = Utils.formatDecimal(askAmount, 4, false);

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
            
                // Toggle background color
                if (backGroundColor == Color.BLACK) {
                      backGroundColor = Color.rgb(31, 31, 31);
                } else {
                      backGroundColor = Color.BLACK;
                }
                
                tr1.setBackgroundColor(backGroundColor);
                tr1.addView(tvBidPrice);
                tr1.addView(tvBidAmount);
                tr1.addView(tvAskPrice);
                tr1.addView(tvAskAmount);

                t1.addView(tr1);
                addDivider(t1);
        }

    }
    
    /**
     * Draw the Orders to the screen in a table
     */
    public void drawXHubOrderbookUI() {

        final TableLayout t1 = (TableLayout) findViewById(R.id.orderlist);
        
        stopLoading();
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

        for (int i = 0; i <  listAsksPrice.size(); i++) {

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

    private void viewOrderbook() {
        OrderbookThread gt = new OrderbookThread();
        gt.start();
    }

    public class OrderbookThread extends Thread {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startLoading();
                }
            });
            //if(exchangeName.equalsIgnoreCase("mtgox") && pref_currency.contains("USD")){
            //    getXHubOrderBook();
            //} else {
                getOrderBook();
            //}
            mOrderHandler.post(mOrderView);
        }
    }

    final Runnable mOrderView = new Runnable() {
        @Override
        public void run() {
            try {
                //if(exchangeName.equalsIgnoreCase("mtgox") && pref_currency.contains("USD")){
                //    drawXHubOrderbookUI();
                //} else {
                    drawOrderbookUI();
                //}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        pref_currency = (String) parent.getItemAtPosition(pos);
        viewOrderbook();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing
    }
    
    private void connectionFailed() {
        stopLoading();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Resources res = getResources();
        String text = String.format(res.getString(R.string.connectionError), res.getString(R.string.orderbook), exchangeName);
        builder.setMessage(text);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
    
    public void startLoading(){
        TableLayout t1 = (TableLayout) findViewById(R.id.orderlist);
        t1.removeAllViews();
        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
    }
    
    public void stopLoading(){
        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.GONE);
    }
    
    public void setOrderBookHeader(){
        TextView orderBookHeader = (TextView) findViewById(R.id.orderbook_header);
        orderBookHeader.setText(exchangeName + " " + currencyPair.baseCurrency + "/" + currencyPair.counterCurrency);
    }
    
    public void addDivider(TableLayout table){
        // Insert a divider between rows
        View divider = new View(this);
        divider.setLayoutParams(new TableRow.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(Color.rgb(51, 51, 51));
        table.addView(divider);
    }
    
    public void createCurrencyDropdown(){
        // Re-populate the dropdown menu
        final String[] dropdownValues = getResources().getStringArray(
                getResources().getIdentifier(prefix + "currencies", "array",
                        this.getPackageName()));
        spinner = (Spinner) findViewById(R.id.orderbook_currency_spinner);
        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dropdownValues);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);
    }
    
    public int depthColor(float amount){
        int color = Color.GRAY;
        if (pref_enableHighlight) {
            if ((int) amount < pref_highlightLow) {
                color = Color.RED;
            }
            if ((int) amount >= pref_highlightLow) {
                color = Color.YELLOW;
            }
            if ((int) amount >= pref_highlightHigh) {
                color = Color.GREEN;
            }
        }
        return color;  
    }
    
    protected static void readPreferences(Context context, String prefix,
            String defaultCurrency) {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_enableHighlight = prefs.getBoolean("highlightPref", true);
        pref_highlightHigh = Integer.parseInt(prefs.getString("highlightUpper",
                "50"));
        pref_highlightLow = Integer.parseInt(prefs.getString("highlightLower",
                "10"));
        pref_currency = prefs.getString(prefix + "CurrencyPref",
                defaultCurrency);
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
}
