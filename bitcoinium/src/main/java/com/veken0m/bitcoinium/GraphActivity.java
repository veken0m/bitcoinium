
package com.veken0m.bitcoinium;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.veken0m.bitcoinium.exchanges.Exchange;
import com.veken0m.bitcoinium.preferences.GraphPreferenceActivity;
import com.veken0m.utils.Constants;
import com.veken0m.utils.CurrencyUtils;
import com.veken0m.utils.Utils;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.dto.marketdata.Trades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// import com.veken0m.utils.KarmaAdsUtils;

public class GraphActivity extends BaseActivity implements OnItemSelectedListener {

    private static final Handler mOrderHandler = new Handler();
    private static Boolean connectionFail = true;
    private static Boolean noTradesFound = false;

    private static SharedPreferences prefs = null;

    private static CurrencyPair currencyPair = null;
    private static String exchangeName = "";
    private static Exchange exchange = null;
    private static Boolean exchangeChanged = false;

    /**
     * Variables required for LineGraphView
     */
    private LineGraphView graphView = null;
    private static Boolean pref_scaleMode = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.show();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            exchange = new Exchange(this, extras.getString("exchange"));
        else
            exchange = new Exchange(this, prefs.getString("defaultExchangePref", Constants.DEFAULT_EXCHANGE));

        if(!exchange.supportsTrades())
            exchange = new Exchange(this, Constants.DEFAULT_EXCHANGE);

        exchangeName = exchange.getExchangeName();

        readPreferences();
        setContentView(R.layout.graph);
        createExchangeDropdown();
        createCurrencyDropdown();
        viewGraph();

        // KarmaAdsUtils.initAd(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_preferences:
                startActivity(new Intent(this, GraphPreferenceActivity.class));
                return true;
            case R.id.action_refresh:
                viewGraph();
                LinearLayout graphLinearLayout = (LinearLayout) findViewById(R.id.graphView);
                graphLinearLayout.removeAllViews();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GraphThread extends Thread {

        @Override
        public void run() {

            generatePriceGraph();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.graph_loadSpinner);
                    linlaHeaderProgress.setVisibility(View.INVISIBLE);
                }
            });
            mOrderHandler.post(mGraphView);
        }
    }

    /**
     * mGraphView run() is called when our GraphThread is finished
     */
    private final Runnable mGraphView = new Runnable() {
        @Override
        public void run() {
            if (graphView != null && !connectionFail && !noTradesFound) {
                LinearLayout graphLinearLayout = (LinearLayout) findViewById(R.id.graphView);
                graphLinearLayout.removeAllViews(); // make sure layout has no child
                graphLinearLayout.addView(graphView);

            } else if (noTradesFound) {
                createPopup(getString(R.string.noTradesFound));
            } else {
                Resources res = getResources();
                String text = String.format(res.getString(R.string.connectionError), res.getString(R.string.trades), exchangeName);
                createPopup(text);
            }
        }
    };

    /**
     * generatePriceGraph prepares price graph of all the values available from
     * the API It connects to exchange, reads the JSON, and plots a GraphView of
     * it
     */
    private void generatePriceGraph() {

        Trades trades = null;

        try {
            trades = ExchangeFactory.INSTANCE.createExchange(exchange.getClassName())
                    .getPollingMarketDataService()
                    .getTrades(currencyPair);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            List<Trade> tradesList = new ArrayList<Trade>();
            if (trades != null) tradesList = trades.getTrades();

            float[] values = new float[tradesList.size()];
            long[] dates = new long[tradesList.size()];
            final GraphViewData[] data = new GraphViewData[values.length];

            float largest = Integer.MIN_VALUE;
            float smallest = Integer.MAX_VALUE;

            final int tradesListSize = tradesList.size();
            for (int i = 0; i < tradesListSize; i++) {
                final Trade trade = tradesList.get(i);
                values[i] = trade.getPrice().floatValue();
                dates[i] = trade.getTimestamp().getTime();
                if (values[i] > largest) {
                    largest = values[i];
                }
                if (values[i] < smallest) {
                    smallest = values[i];
                }
                data[i] = new GraphViewData(dates[i], values[i]);
            }

            graphView = new LineGraphView(this, exchangeName + ": " + currencyPair.toString()) {
                @Override
                protected String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        return Utils.dateFormat(getBaseContext(), (long) value);
                    } else
                        return super.formatLabel(value, false);
                }
            };

            double windowSize = (dates[dates.length - 1] - dates[0]) / 2;
            // startValue enables graph window to be aligned with latest
            // trades
            final double startValue = dates[dates.length - 1] - windowSize;
            graphView.addSeries(new GraphViewSeries(data));
            graphView.setViewPort(startValue, windowSize);
            graphView.setScrollable(true);
            graphView.setScalable(true);

            if (!pref_scaleMode) {
                graphView.setManualYAxisBounds(largest, smallest);
            }
            connectionFail = false;
            noTradesFound = false;

        } catch (ArrayIndexOutOfBoundsException e) {
            noTradesFound = true;

        } catch (Exception e) {
            connectionFail = true;
            e.printStackTrace();
        }
    }

    private void createPopup(String pMessage) {
        try {
            if (dialog == null || !dialog.isShowing()) {
                // Display error Dialog
                Resources res = getResources();
                dialog = Utils.errorDialog(this, pMessage);
            }
        } catch (WindowManager.BadTokenException e){
            // This happens when we try to show a dialog when app is not in the foreground. Suppress it for now
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (graphView != null) {
            LinearLayout graphLinearLayout = (LinearLayout) findViewById(R.id.graphView);
            graphLinearLayout.removeAllViews();
            graphLinearLayout.addView(graphView);
        } else {
            viewGraph();
        }
    }

    private void viewGraph() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.graph_loadSpinner);
                linlaHeaderProgress.setVisibility(View.VISIBLE);
            }
        });

        GraphThread gt = new GraphThread();
        gt.start();
    }

    private static void readPreferences() {

        pref_scaleMode = prefs.getBoolean("graphscalePref", false);
        currencyPair = CurrencyUtils.stringToCurrencyPair(prefs.getString(exchange.getIdentifier() + "CurrencyPref", exchange.getDefaultCurrency()));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        CurrencyPair prevCurrencyPair = currencyPair;
        String prevExchangeName = exchangeName;

        switch (parent.getId()){
            case R.id.graph_exchange_spinner:
                exchangeName = (String) parent.getItemAtPosition(pos);
                exchangeChanged = prevExchangeName != null && exchangeName != null && !exchangeName.equals(prevExchangeName);
                if (exchangeChanged){
                    exchange = new Exchange(this, exchangeName);
                    currencyPair = CurrencyUtils.stringToCurrencyPair(prefs.getString(exchange.getIdentifier() + "CurrencyPref", exchange.getDefaultCurrency()));
                    createCurrencyDropdown();
                }
                break;
            case R.id.graph_currency_spinner:
                currencyPair = CurrencyUtils.stringToCurrencyPair((String) parent.getItemAtPosition(pos));
                break;
        }

        if (prevCurrencyPair != null && currencyPair != null && !currencyPair.equals(prevCurrencyPair) || exchangeChanged)
            viewGraph();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing
    }

    void createExchangeDropdown() {

        // Re-populate the dropdown menu
        String[] exchanges = getResources().getStringArray(R.array.exchangesTrades);
        Spinner spinner = (Spinner) findViewById(R.id.graph_exchange_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, exchanges);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);

        int index = Arrays.asList(exchanges).indexOf(exchange.getExchangeName());
        spinner.setSelection(index);
    }

    void createCurrencyDropdown() {
        // Re-populate the dropdown menu
        int arrayId = getResources().getIdentifier(exchange.getIdentifier() + "currencies", "array", this.getPackageName());
        String[] currencies = getResources().getStringArray(arrayId);

        Spinner spinner = (Spinner) findViewById(R.id.graph_currency_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencies);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);

        if(exchangeChanged){
            int index = Arrays.asList(currencies).indexOf(currencyPair.toString());
            spinner.setSelection(index);
        }
    }
}
