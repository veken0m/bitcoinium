package com.veken0m.bitcoinium;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.veken0m.bitcoinium.exchanges.ExchangeProperties;
import com.veken0m.bitcoinium.preferences.OrderbookPreferenceActivity;
import com.veken0m.utils.Constants;
import com.veken0m.utils.CurrencyUtils;
import com.veken0m.utils.ExchangeUtils;
import com.veken0m.utils.Utils;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.service.polling.marketdata.PollingMarketDataService;

import java.util.Arrays;
import java.util.List;

import static com.veken0m.utils.ExchangeUtils.getDropdownItems;

// import com.veken0m.utils.KarmaAdsUtils;

public class OrderbookActivity extends BaseActivity implements OnItemSelectedListener, SwipeRefreshLayout.OnRefreshListener
{
    private final static Handler mOrderHandler = new Handler();
    /**
     * List of preference variables
     */
    private static int pref_highlightHigh = 0;
    private static int pref_highlightLow = 0;
    private static int pref_orderbookLimiter = 0;
    private static Boolean pref_enableHighlight = true;
    private static Boolean pref_showCurrencySymbol = true;
    private static SharedPreferences prefs = null;
    private static CurrencyPair currencyPair = null;
    private static String exchangeName = "";
    private static ExchangeProperties exchange = null;
    private static Boolean exchangeChanged = false;
    private static Boolean threadRunning = false;
    private static Boolean noOrdersFound = false;

    private final Runnable mOrderView = new Runnable()
    {
        @Override
        public void run()
        {
            drawOrderbookUI();
        }
    };
    private final Runnable mError = new Runnable()
    {
        @Override
        public void run()
        {
            errorOccured();
        }
    };
    private Dialog dialog = null;
    private List<LimitOrder> listAsks = null;
    private List<LimitOrder> listBids = null;

    private static void readPreferences()
    {
        pref_enableHighlight = prefs.getBoolean("highlightPref", true);
        pref_highlightHigh = Integer.parseInt(prefs.getString("depthHighlightUpperPref", "10"));
        pref_highlightLow = Integer.parseInt(prefs.getString("depthHighlightLowerPref", "1"));
        currencyPair = CurrencyUtils.stringToCurrencyPair(prefs.getString(exchange.getIdentifier() + "CurrencyPref", exchange.getDefaultCurrency()));
        pref_showCurrencySymbol = prefs.getBoolean("showCurrencySymbolPref", true);
        try
        {
            pref_orderbookLimiter = Integer.parseInt(prefs.getString("orderbookLimiterPref", "100"));
        }
        catch (Exception e)
        {
            pref_orderbookLimiter = 100;
            // If preference is not set a valid integer set to "100"
            Editor editor = prefs.edit();
            editor.putString("orderbookLimiterPref", "100");
            editor.commit();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_orderbook);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.orderbook_swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.holo_blue_light);
        swipeLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.show();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            exchange = new ExchangeProperties(this, extras.getString("exchange"));
        else
            exchange = new ExchangeProperties(this, prefs.getString("defaultExchangePref", Constants.DEFAULT_EXCHANGE));

        if (!exchange.supportsOrderbook())
            exchange = new ExchangeProperties(this, Constants.DEFAULT_EXCHANGE);

        readPreferences();
        populateExchangeDropdown();
        populateCurrencyDropdown();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        viewOrderbook();
    }

    @Override
    public void onRefresh()
    {
        viewOrderbook();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_preferences:
                startActivity(new Intent(this, OrderbookPreferenceActivity.class));
                return true;
            case R.id.action_refresh:
                viewOrderbook();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_orderbook);

        if (listAsks != null && listBids != null)
        {
            populateExchangeDropdown();
            populateCurrencyDropdown();
            drawOrderbookUI();
        }
        else
        {
            // Fetch data
            viewOrderbook();
        }
    }

    /**
     * Fetch the OrderbookActivity and split into Ask/Bids lists
     */
    boolean getOrderBook()
    {
        if (listAsks != null && listBids != null)
        {
            listAsks.clear();
            listBids.clear();
        }
        noOrdersFound = false;

        PollingMarketDataService marketData = ExchangeUtils.getMarketData(exchange, currencyPair);
        OrderBook orderbook;

        try
        {
            orderbook = marketData.getOrderBook(currencyPair);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        if (orderbook != null)
        {
            listAsks = orderbook.getAsks();
            listBids = orderbook.getBids();

            if (listAsks.isEmpty() && listBids.isEmpty())
                noOrdersFound = true;

            // Limit OrderbookActivity orders drawn to improve performance
            if (pref_orderbookLimiter != 0)
            {
                if (listAsks.size() > pref_orderbookLimiter)
                    listAsks = listAsks.subList(0, pref_orderbookLimiter);

                if (listBids.size() > pref_orderbookLimiter)
                    listBids = listBids.subList(0, pref_orderbookLimiter);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Draw the Orders to the screen in a table
     */
    void drawOrderbookUI()
    {
        if (swipeLayout != null)
            swipeLayout.setRefreshing(true);
        final TableLayout orderbookTable = (TableLayout) findViewById(R.id.orderlist);
        if (orderbookTable != null)
        {
            orderbookTable.removeAllViews();

            boolean bBackGroundColor = true;

            String baseCurrencySymbol = "";
            String counterCurrencySymbol = "";
            if (pref_showCurrencySymbol)
            {
                counterCurrencySymbol = CurrencyUtils.getSymbol(currencyPair.counter.getSymbol());
                baseCurrencySymbol = CurrencyUtils.getSymbol(currencyPair.base.getSymbol());
            }

            // if numbers are too small adjust the units. Use first bid to determine the units
            int priceUnitIndex = 0;
            if (!listBids.isEmpty() || !listAsks.isEmpty())
            {
                LimitOrder tempOrder = listBids.isEmpty() ? listAsks.get(0) : listBids.get(0);
                priceUnitIndex = Utils.getUnitIndex(tempOrder.getLimitPrice().floatValue());
            }

            String sCounterCurrency = currencyPair.counter.getSymbol();
            if (priceUnitIndex >= 0)
                sCounterCurrency = Constants.METRIC_UNITS[priceUnitIndex] + sCounterCurrency;
            priceUnitIndex++; // increment to use a scale factor

            TextView tvAskAmountHeader = (TextView) findViewById(R.id.askAmountHeader);
            TextView tvAskPriceHeader = (TextView) findViewById(R.id.askPriceHeader);
            TextView tvBidPriceHeader = (TextView) findViewById(R.id.bidPriceHeader);
            TextView tvBidAmountHeader = (TextView) findViewById(R.id.bidAmountHeader);

            tvAskAmountHeader.setText("(" + currencyPair.base.getSymbol() + ")");
            tvAskPriceHeader.setText("(" + sCounterCurrency + ")");
            tvBidPriceHeader.setText("(" + sCounterCurrency + ")");
            tvBidAmountHeader.setText("(" + currencyPair.base.getSymbol() + ")");

            LayoutInflater mInflater = LayoutInflater.from(this);

            int askSize = listAsks.size();
            int bidSize = listBids.size();

            int length = Math.max(askSize, bidSize);
            for (int i = 0; i < length; i++)
            {
                TextView tvAskAmount = (TextView) mInflater.inflate(R.layout.table_textview, null);
                TextView tvAskPrice = (TextView) mInflater.inflate(R.layout.table_textview, null);
                TextView tvBidPrice = (TextView) mInflater.inflate(R.layout.table_textview, null);
                TextView tvBidAmount = (TextView) mInflater.inflate(R.layout.table_textview, null);

                if (bidSize > i)
                {
                    LimitOrder limitorderBid = listBids.get(i);
                    float bidPrice = limitorderBid.getLimitPrice().floatValue();
                    float bidAmount = limitorderBid.getTradableAmount().floatValue();
                    tvBidAmount.setText(baseCurrencySymbol + Utils.formatDecimal(bidAmount, 4, 0, true));
                    tvBidPrice.setText(counterCurrencySymbol + Utils.formatDecimal(bidPrice, 3, priceUnitIndex, true));

                    if (pref_enableHighlight)
                    {
                        int bidTextColor = depthColor(bidAmount);
                        tvBidAmount.setTextColor(bidTextColor);
                        tvBidPrice.setTextColor(bidTextColor);
                    }
                }

                if (askSize > i)
                {
                    LimitOrder limitorderAsk = listAsks.get(i);
                    float askPrice = limitorderAsk.getLimitPrice().floatValue();
                    float askAmount = limitorderAsk.getTradableAmount().floatValue();
                    tvAskAmount.setText(baseCurrencySymbol + Utils.formatDecimal(askAmount, 4, 0, true));
                    tvAskPrice.setText(counterCurrencySymbol + Utils.formatDecimal(askPrice, 3, priceUnitIndex, true));

                    if (pref_enableHighlight)
                    {
                        int askTextColor = depthColor(askAmount);
                        tvAskAmount.setTextColor(askTextColor);
                        tvAskPrice.setTextColor(askTextColor);
                    }
                }

                final TableRow newRow = new TableRow(this);

                // Toggle background color
                if (bBackGroundColor = !bBackGroundColor)
                    newRow.setBackgroundColor(getResources().getColor(R.color.light_tableRow));

                newRow.addView(tvBidPrice);
                newRow.addView(tvBidAmount);
                newRow.addView(tvAskPrice);
                newRow.addView(tvAskAmount);

                orderbookTable.addView(newRow);
            }
        }
        else
        {
            failedToDrawUI();
        }
        if (swipeLayout != null)
            swipeLayout.setRefreshing(false);
    }

    private void viewOrderbook()
    {
        swipeLayout.setRefreshing(true);
        if (Utils.isConnected(getApplicationContext()))
        {
            if (!threadRunning) // if thread running don't start a another one
                (new OrderbookThread()).start();
        }
        else
        {
            notConnected();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        CurrencyPair prevCurrencyPair = currencyPair;
        String prevExchangeName = exchangeName;

        switch (parent.getId())
        {
            case R.id.orderbook_exchange_spinner:
                exchangeName = (String) parent.getItemAtPosition(pos);
                exchangeChanged = prevExchangeName != null && exchangeName != null && !exchangeName.equals(prevExchangeName);
                if (exchangeChanged)
                {
                    exchange = new ExchangeProperties(this, exchangeName);
                    currencyPair = CurrencyUtils.stringToCurrencyPair(prefs.getString(exchange.getIdentifier() + "CurrencyPref", exchange.getDefaultCurrency()));
                    populateCurrencyDropdown();
                }
                break;
            case R.id.orderbook_currency_spinner:
                currencyPair = CurrencyUtils.stringToCurrencyPair((String) parent.getItemAtPosition(pos));
                break;
        }

        if (prevCurrencyPair != null && currencyPair != null && !currencyPair.equals(prevCurrencyPair) || exchangeChanged)
            viewOrderbook();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        // Do nothing...
    }

    private void errorOccured()
    {
        swipeLayout.setRefreshing(false);

        try
        {
            if (dialog == null || !dialog.isShowing())
            {
                // Display error Dialog
                Resources res = getResources();
                String text = String.format(res.getString(R.string.error_exchangeConnection),
                        res.getString(R.string.orderbook), exchange.getExchangeName());
                dialog = Utils.errorDialog(this, text);
            }
        }
        catch (WindowManager.BadTokenException e)
        {
            // This happens when we try to show a dialog when app is not in the foreground. Suppress it for now
        }
    }

    private void failedToDrawUI()
    {
        swipeLayout.setRefreshing(false);
        // Display error Dialog
        if (dialog == null || !dialog.isShowing())
            dialog = Utils.errorDialog(this, getString(R.string.error_BitcoinAverageTable), getString(R.string.error));
    }

    void populateExchangeDropdown()
    {
        // Re-populate the dropdown menu
        List<String> exchanges = getDropdownItems(getApplicationContext(), ExchangeProperties.ItemType.ORDERBOOK_ENABLED).first;
        Spinner spinner = (Spinner) findViewById(R.id.orderbook_exchange_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, exchanges);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);

        int index = exchanges.indexOf(exchange.getExchangeName());
        spinner.setSelection(index);
    }

    void populateCurrencyDropdown()
    {
        // Re-populate the dropdown menu
        String[] currencies = exchange.getCurrencies();
        Spinner spinner = (Spinner) findViewById(R.id.orderbook_currency_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);

        if (exchangeChanged)
        {
            int index = Arrays.asList(currencies).indexOf(currencyPair.toString());
            spinner.setSelection(index);
        }
    }

    int depthColor(float amount)
    {
        if ((int) amount >= pref_highlightHigh)
            return Color.GREEN;
        else if ((int) amount < pref_highlightLow)
            return Color.RED;
        else
            return Color.YELLOW;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        readPreferences();
        populateExchangeDropdown();

        if (listAsks != null && listBids != null)
        {
            populateExchangeDropdown();
            populateCurrencyDropdown();
            drawOrderbookUI();
        }
    }

    private class OrderbookThread extends Thread
    {
        @Override
        public void run()
        {
            threadRunning = true;
            if (getOrderBook())
                mOrderHandler.post(mOrderView);
            else
                mOrderHandler.post(mError);
            threadRunning = false;
        }
    }
}
