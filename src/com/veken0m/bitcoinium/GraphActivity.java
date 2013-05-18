
package com.veken0m.bitcoinium;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.veken0m.bitcoinium.exchanges.Exchange;
import com.veken0m.bitcoinium.utils.Utils;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.dto.marketdata.Trades;

import java.util.List;

public class GraphActivity extends SherlockActivity {

    private static final Handler mOrderHandler = new Handler();
    public static String exchangeName;
    public static Boolean connectionFail;
    public String xchangeExchange;
    static String pref_currency;

    /**
     * Variables required for LineGraphView
     */
    LineGraphView graphView;
    static Boolean pref_graphMode;
    static Boolean pref_scaleMode;
    static Boolean pref_APIv1Mode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionbar = getSupportActionBar();
        actionbar.show();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            exchangeName = extras.getString("exchange");
        }

        Exchange exchange = new Exchange(getResources().getStringArray(
                getResources().getIdentifier(exchangeName, "array",
                        this.getPackageName())));

        exchangeName = exchange.getExchangeName();
        xchangeExchange = exchange.getClassName();
        String defaultCurrency = exchange.getMainCurrency();
        String prefix = exchange.getPrefix();

        readPreferences(getApplicationContext(), prefix, defaultCurrency);

        if (exchange.supportsPriceGraph()) {
            setContentView(R.layout.graph);
            viewGraph();
        } else {
            Toast.makeText(getApplicationContext(),
                    exchangeName + " does not currently support Price Graph",
                    Toast.LENGTH_LONG).show();
        }
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
            viewGraph();
        }
        return super.onOptionsItemSelected(item);
    }

    public class GraphThread extends Thread {

        @Override
        public void run() {
            generatePriceGraph();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.graph);
                    LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress2);
                    linlaHeaderProgress.setVisibility(View.INVISIBLE);
                }
            });
            mOrderHandler.post(mGraphView);
        }
    }

    /**
     * mGraphView run() is called when our GraphThread is finished
     */
    final Runnable mGraphView = new Runnable() {
        @Override
        public void run() {
            if (graphView != null && !connectionFail) {
                setContentView(graphView);
            } else {
                createPopup("Unable to retrieve transactions from "
                        + exchangeName + ", check your 3G or WiFi connection");
            }
        }
    };

    /**
     * generatePriceGraph prepares price graph of all the values available from
     * the API It connects to exchange, reads the JSON, and plots a GraphView of
     * it
     */
    private void generatePriceGraph() {

        String graphExchange = xchangeExchange;
        Trades trades = null;

        if (pref_APIv1Mode == true) {
            // Use API V1 instead of V0 for MtGox Trades
            graphExchange = xchangeExchange.replace("0", "1");
        }

        String baseCurrency = Currencies.BTC;
        String counterCurrency = pref_currency;

        if (pref_currency.contains("/")) {
            baseCurrency = pref_currency.substring(0, 3);
            counterCurrency = pref_currency.substring(4, 7);
        }

        try {

            trades = ExchangeFactory.INSTANCE.createExchange(graphExchange)
                    .getPollingMarketDataService()
                    .getTrades(baseCurrency, counterCurrency);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            List<Trade> tradesList = trades.getTrades();

            float[] values = new float[tradesList.size()];
            long[] dates = new long[tradesList.size()];
            final GraphViewData[] data = new GraphViewData[values.length];

            float largest = Integer.MIN_VALUE;
            float smallest = Integer.MAX_VALUE;

            final int tradesListSize = tradesList.size();
            for (int i = 0; i < tradesListSize; i++) {
                final Trade trade = tradesList.get(i);
                values[i] = trade.getPrice().getAmount().floatValue();
                dates[i] = trade.getTimestamp().getTime();
                if (values[i] > largest) {
                    largest = values[i];
                }
                if (values[i] < smallest) {
                    smallest = values[i];
                }
            }

            for (int i = 0; i < tradesListSize; i++) {
                data[i] = new GraphViewData(dates[i], values[i]);
            }

            graphView = new LineGraphView(this, exchangeName + ": "
                    + baseCurrency + "/" + counterCurrency) {
                @Override
                protected String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        return Utils.dateFormat(getBaseContext(), (long) value);
                    } else
                        return super.formatLabel(value, isValueX);
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

        } catch (Exception e) {
            connectionFail = true;
            e.printStackTrace();
        }
    }

    private void createPopup(String pMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(pMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.graph);
        if (graphView != null) {
            setContentView(graphView);
        } else {
            viewGraph();
        }
    }

    private void viewGraph() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.graph);
                LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress2);
                linlaHeaderProgress.setVisibility(View.VISIBLE);
            }
        });

        GraphThread gt = new GraphThread();
        gt.start();
    }

    protected static void readPreferences(Context context, String prefix,
            String defaultCurrency) {
        // Get the xml/preferences.xml preferences
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_graphMode = prefs.getBoolean("graphmodePref", false);
        pref_scaleMode = prefs.getBoolean("graphscalePref", false);
        pref_currency = prefs.getString(prefix + "CurrencyPref",
                defaultCurrency);
        pref_APIv1Mode = prefs.getBoolean("mtgoxapiv1Pref", false);
    }

}
