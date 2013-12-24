
package com.veken0m.bitcoinium;

import java.io.InputStreamReader;
import java.util.Arrays;
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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.analytics.tracking.android.EasyTracker;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.veken0m.bitcoinium.exchanges.Exchange;
import com.veken0m.bitcoinium.webservice.dto.TickerHistory;
import com.veken0m.utils.CurrencyUtils;
import com.veken0m.utils.KarmaAdsUtils;
import com.veken0m.utils.Utils;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.dto.marketdata.Trades;

public class  GraphActivity extends SherlockActivity implements OnItemSelectedListener {

    private static final Handler mOrderHandler = new Handler();
    private static String exchangeName = null;
    private static Boolean connectionFail = true;
    private static Boolean noTradesFound = false;
    private String xchangeExchange = null;
    private static String pref_currency = null;
    private String prefix = "mtgox";

    /**
     * Variables required for LineGraphView
     */
    private LineGraphView graphView = null;
    private static Boolean pref_scaleMode = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionbar = getSupportActionBar();
        actionbar.show();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            exchangeName = extras.getString("exchange");
        }

        Exchange exchange = new Exchange(this, exchangeName);

        exchangeName = exchange.getExchangeName();
        xchangeExchange = exchange.getClassName();
        String defaultCurrency = exchange.getDefaultCurrency();
        prefix = exchange.getIdentifier();

        readPreferences(getApplicationContext(), prefix, defaultCurrency);

        if (exchange.supportsPriceGraph()) {
            setContentView(R.layout.graph);
            createCurrencyDropdown();
            viewGraph();
        } else {
            Toast.makeText(this,
                    exchangeName + " does not currently support Price Graph",
                    Toast.LENGTH_LONG).show();
        }
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
            viewGraph();
            LinearLayout graphLinearLayout = (LinearLayout) findViewById(R.id.graphView);
            graphLinearLayout.removeAllViews();
        }
        return super.onOptionsItemSelected(item);
    }

    private class GraphThread extends Thread {

        @Override
        public void run() {
            //if (exchangeName.equalsIgnoreCase("mtgox") && pref_currency.contains("USD")) {
            //    generateXHubPriceGraph();
            //} else {
                generatePriceGraph();
            //}
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
    private final Runnable mGraphView = new Runnable() {
        @Override
        public void run() {
            if (graphView != null && !connectionFail && !noTradesFound) {
                LinearLayout graphLinearLayout = (LinearLayout) findViewById(R.id.graphView);
                graphLinearLayout.removeAllViews(); // make sure layout has no child
                graphLinearLayout.addView(graphView);

            } else if (noTradesFound) {
                createPopup("No recent trades found for this currency. Please try again later.");
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

        String graphExchange = xchangeExchange;
        Trades trades = null;

        CurrencyPair currencyPair = CurrencyUtils.stringToCurrencyPair(pref_currency);

        try {
            trades = ExchangeFactory.INSTANCE.createExchange(graphExchange)
                    .getPollingMarketDataService()
                    .getTrades(currencyPair.baseCurrency, currencyPair.counterCurrency);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            List<Trade> tradesList = null;
            if (trades != null) {
                tradesList = trades.getTrades();
            }

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
                data[i] = new GraphViewData(dates[i], values[i]);
            }

            graphView = new LineGraphView(this, exchangeName + ": "
                    + currencyPair.baseCurrency + "/" + currencyPair.counterCurrency) {
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

    private void generateXHubPriceGraph() {

        TickerHistory trades = null;

        CurrencyPair currencyPair = CurrencyUtils.stringToCurrencyPair(pref_currency);

        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet post = new HttpGet(
                    "http://bitcoinium.com:9090/service/tickerhistory?exchange=mtgox&pair=BTC_USD&timewindow=7d");
            HttpResponse response = client.execute(post);
            ObjectMapper mapper = new ObjectMapper();

            trades = mapper.readValue(new InputStreamReader(response.getEntity()
                    .getContent(), "UTF-8"), TickerHistory.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            int tradeListSize = 0;
            if (trades != null) {
                tradeListSize = trades.getPriceHistoryList().size();
            }
            float[] values = new float[tradeListSize];
            long[] dates = new long[tradeListSize];
            final GraphViewData[] data = new GraphViewData[tradeListSize];

            float largest = Integer.MIN_VALUE;
            float smallest = Integer.MAX_VALUE;

            long baseTime = trades.getBaseTimestamp() * 1000;

            for (int i = 0; i < tradeListSize; i++) {
                values[i] = trades.getPriceHistoryList().get(i).floatValue();
                long delta = trades.getTimeStampOffsets().get(i).longValue() * 1000;
                baseTime += delta;
                dates[i] = baseTime;

                if (values[i] > largest) {
                    largest = values[i];
                }
                if (values[i] < smallest) {
                    smallest = values[i];
                }
                data[i] = new GraphViewData(dates[i], values[i]);
            }

            graphView = new LineGraphView(this, exchangeName + ": "
                    + currencyPair.baseCurrency + "/" + currencyPair.counterCurrency) {
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
                LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress2);
                linlaHeaderProgress.setVisibility(View.VISIBLE);
            }
        });

        GraphThread gt = new GraphThread();
        gt.start();
    }

    private static void readPreferences(Context context, String prefix,
                                        String defaultCurrency) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_scaleMode = prefs.getBoolean("graphscalePref", false);
        pref_currency = prefs.getString(prefix + "CurrencyPref",
                defaultCurrency);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        pref_currency = (String) parent.getItemAtPosition(pos);
        viewGraph();
        LinearLayout graphLinearLayout = (LinearLayout) findViewById(R.id.graphView);
        graphLinearLayout.removeAllViews();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing
    }

    void createCurrencyDropdown() {
        final String[] dropdownValues = getResources().getStringArray(
                getResources().getIdentifier(prefix + "currencies", "array",
                        this.getPackageName()));

        Spinner spinner = (Spinner) findViewById(R.id.graph_currency_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dropdownValues);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(Arrays.asList(dropdownValues).indexOf(pref_currency));
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

}
