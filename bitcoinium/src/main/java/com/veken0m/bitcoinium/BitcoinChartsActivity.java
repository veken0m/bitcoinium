
package com.veken0m.bitcoinium;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.veken0m.utils.Utils;
import com.xeiam.xchange.bitcoincharts.BitcoinChartsFactory;
import com.xeiam.xchange.bitcoincharts.dto.marketdata.BitcoinChartsTicker;

import java.util.Arrays;
import java.util.Comparator;

public class BitcoinChartsActivity extends SherlockActivity implements OnItemSelectedListener {

    private final static Handler mOrderHandler = new Handler();
    private BitcoinChartsTicker[] marketData = null;
    private final Runnable mBitcoinChartsView;
    private final Runnable mError;
    private String currencyFilter;
    private Dialog dialog = null;

    public BitcoinChartsActivity() {
        currencyFilter = "SHOW ALL";
        mBitcoinChartsView = new Runnable() {
            @Override
            public void run() {
                if (marketData != null) drawBitcoinChartsUI();
            }
        };

        mError = new Runnable() {
            @Override
            public void run() {
                errorOccured();
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bitcoincharts);

        createCurrencyDropdown();
        ActionBar actionbar = getSupportActionBar();
        actionbar.show();

        //KarmaAdsUtils.initAd(this);
        viewBitcoinCharts();
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
            viewBitcoinCharts();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.bitcoincharts);
        if (marketData != null) drawBitcoinChartsUI();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        String prevCurrencyfilter = currencyFilter;
        currencyFilter = (String) parent.getItemAtPosition(pos);
        if (prevCurrencyfilter != null && currencyFilter != null && !currencyFilter.equals(prevCurrencyfilter))
            drawBitcoinChartsUI();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing
    }

    void createCurrencyDropdown() {
        // Re-populate the dropdown menu
        final String[] dropdownValues = getResources().getStringArray(R.array.bitcoinChartsDropdown);

        Spinner spinner = (Spinner) findViewById(R.id.bitcoincharts_currency_spinner);
        if (spinner != null) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, dropdownValues);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
            spinner.setOnItemSelectedListener(this);
        }
    }

    /**
     * Fetch the Bitcoin Charts data
     */
    boolean getBitcoinCharts() {
        try {
            marketData = BitcoinChartsFactory.createInstance().getMarketData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Draw the Tickers to the screen in a table
     */
    void drawBitcoinChartsUI() {

        TableLayout bitcoinChartsTable = (TableLayout) findViewById(R.id.bitcoincharts_list);
        removeLoadingSpinner();

        if (marketData != null && marketData.length > 0 && bitcoinChartsTable != null) {

            // Clear table
            bitcoinChartsTable.removeAllViews();

            // Sort Tickers by volume
            Arrays.sort(marketData, new Comparator<BitcoinChartsTicker>() {
                @Override
                public int compare(BitcoinChartsTicker entry1,
                                   BitcoinChartsTicker entry2) {
                    return entry2.getVolume().compareTo(entry1.getVolume());
                }
            });

            boolean bBackGroundColor = true;
            for (BitcoinChartsTicker data : marketData) {

                // Only print active exchanges... vol > 0 or contains selected currency
                if (data.getVolume().floatValue() != 0.0
                        && (currencyFilter.equals("SHOW ALL")
                        || data.getCurrency().contains(currencyFilter))) {

                    final TextView tvSymbol = new TextView(this);
                    final TextView tvLast = new TextView(this);
                    final TextView tvVolume = new TextView(this);
                    final TextView tvHigh = new TextView(this);
                    final TextView tvLow = new TextView(this);
                    // final TextView tvAvg = new TextView(this);
                    // final TextView tvBid = new TextView(this);
                    // final TextView tvAsk = new TextView(this);

                    tvSymbol.setText(data.getSymbol());
                    Utils.setTextViewParams(tvLast, data.getClose());
                    Utils.setTextViewParams(tvVolume, data.getVolume());
                    Utils.setTextViewParams(tvLow, data.getLow());
                    Utils.setTextViewParams(tvHigh, data.getHigh());
                    // Utils.setTextViewParams(tvAvg, data.getAvg());
                    // Utils.setTextViewParams(tvBid, data.getBid());
                    // Utils.setTextViewParams(tvAsk, data.getAsk());

                    final TableRow newRow = new TableRow(this);

                    // Toggle background color
                    bBackGroundColor = !bBackGroundColor;
                    if (bBackGroundColor)
                        newRow.setBackgroundColor(Color.BLACK);
                    else
                        newRow.setBackgroundColor(Color.rgb(31, 31, 31));

                    newRow.addView(tvSymbol, Utils.symbolParams);
                    newRow.addView(tvLast);
                    newRow.addView(tvVolume);
                    newRow.addView(tvLow);
                    newRow.addView(tvHigh);
                    // newRow.addView(tvBid);
                    // newRow.addView(tvAsk);
                    // newRow.addView(tvAvg);
                    newRow.setPadding(0, 3, 0, 3);
                    bitcoinChartsTable.addView(newRow);
                }
            }
        } else {
            failedToDrawUI();
        }
    }

    private void viewBitcoinCharts() {
        if (Utils.isConnected(getApplicationContext())) {
            (new bitcoinChartsThread()).start();
        } else {
            notConnected();
        }
    }

    private class bitcoinChartsThread extends Thread {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout bitcoinChartsLoadSpinner = (LinearLayout) findViewById(R.id.loadSpinner);
                    if (bitcoinChartsLoadSpinner != null) bitcoinChartsLoadSpinner.setVisibility(View.VISIBLE);
                }
            });

            if (getBitcoinCharts())
                mOrderHandler.post(mBitcoinChartsView);
            else
                mOrderHandler.post(mError);
        }
    }



    private void errorOccured() {

        removeLoadingSpinner();

        if (dialog == null || !dialog.isShowing()) {
            // Display error Dialog
            Resources res = getResources();
            dialog = Utils.errorDialog(this, String.format(res.getString(R.string.connectionError), "tickers", "Bitcoin Charts"));
        }
    }

    private void notConnected() {

        removeLoadingSpinner();

        if (dialog == null || !dialog.isShowing()) {
            // Display error Dialog
            dialog = Utils.errorDialog(this, "No internet connection available", "Internet Connection");
        }
    }

    private void failedToDrawUI() {

        removeLoadingSpinner();
        if (dialog == null || !dialog.isShowing()) {
            // Display error Dialog
            dialog = Utils.errorDialog(this, "A problem occurred when generating Bitcoin Charts table", "Error");
        }
    }

    // Remove loading spinner
    void removeLoadingSpinner() {
        LinearLayout bitcoinChartsLoadSpinner = (LinearLayout) findViewById(R.id.loadSpinner);
        if (bitcoinChartsLoadSpinner != null) bitcoinChartsLoadSpinner.setVisibility(View.GONE);
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
