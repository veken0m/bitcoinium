
package com.veken0m.bitcoinium;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
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
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BitcoinAverageActivity extends SherlockActivity {

    private final static Handler mOrderHandler = new Handler();
    final private ArrayList<Ticker> tickers;
    private final String[] curr;
    private Dialog dialog = null;

    public BitcoinAverageActivity() {
        tickers = new ArrayList<Ticker>();
        curr = new String[]{
                "AUD", "BRL", "CAD", "CNY", "CZK", "EUR", "GBP", "ILS", "JPY", "NOK", "NZD",
                "PLN", "RUB", "SEK", "USD", "ZAR"
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bitcoinaverage);

        ActionBar actionbar = getSupportActionBar();
        actionbar.show();

        // KarmaAdsUtils.initAd(this);
        viewBitcoinAverage();
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
            viewBitcoinAverage();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.bitcoinaverage);
        if (tickers.size() > 0) drawBitcoinAverageUI();
    }

    /**
     * Fetch the Bitcoin Average data
     */
    boolean getBitcoinAverage() {

        tickers.clear();
        PollingMarketDataService pollingService = ExchangeFactory.INSTANCE
                .createExchange("com.xeiam.xchange.bitcoinaverage.BitcoinAverageExchange")
                .getPollingMarketDataService();

        if (pollingService != null) {
            for (String currency : curr) {

                try {
                    tickers.add(pollingService.getTicker("BTC", currency));
                } catch (IOException e) {
                    // Skip ticker and keep looping
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Draw the Tickers to the screen in a table
     */
    void drawBitcoinAverageUI() {

        TableLayout bitcoinAverageTable = (TableLayout) findViewById(R.id.bitcoinaverage_list);
        removeLoadingSpinner();

        boolean bBackGroundColor = true;

        if (tickers.size() > 0 && bitcoinAverageTable != null) {

            // Clear table
            bitcoinAverageTable.removeAllViews();

            // Sort Tickers by volume
            Collections.sort(tickers, new
                    Comparator<Ticker>() {
                        @Override
                        public int compare(Ticker entry1, Ticker entry2) {
                            return entry2.getVolume().compareTo(entry1.getVolume());
                        }
                    });


            for (Ticker ticker : tickers) {

                final TextView tvSymbol = new TextView(this);
                final TextView tvLast = new TextView(this);
                final TextView tvVolume = new TextView(this);
                final TextView tvBid = new TextView(this);
                final TextView tvAsk = new TextView(this);
                // final TextView tvAvg = new TextView(this);

                tvSymbol.setText(ticker.getLast().getCurrencyUnit().getCurrencyCode());
                Utils.setTextViewParams(tvLast, ticker.getLast());
                Utils.setTextViewParams(tvVolume, ticker.getVolume());
                Utils.setTextViewParams(tvBid, ticker.getBid());
                Utils.setTextViewParams(tvAsk, ticker.getAsk());
                // Utils.setTextViewParams(tvAvg, avg);

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
                newRow.addView(tvBid);
                newRow.addView(tvAsk);
                // newRow.addView(tvAvg);
                newRow.setPadding(0, 3, 0, 3);
                bitcoinAverageTable.addView(newRow);
            }
        } else {
            failedToDrawUI();
        }
    }

    private void viewBitcoinAverage() {

        if (Utils.isConnected(getApplicationContext())) {
            (new bitcoinAverageThread()).start();
        } else {
            notConnected();
        }
    }

    private class bitcoinAverageThread extends Thread {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout loadSpinner = (LinearLayout) findViewById(R.id.loadSpinner);
                    if (loadSpinner != null) loadSpinner.setVisibility(View.VISIBLE);
                }
            });
            if (getBitcoinAverage())
                mOrderHandler.post(mGraphView);
            else
                mOrderHandler.post(mError);
        }
    }

    // Remove loading spinner
    void removeLoadingSpinner() {
        LinearLayout bitcoinChartsLoadSpinner = (LinearLayout) findViewById(R.id.loadSpinner);
        if (bitcoinChartsLoadSpinner != null) bitcoinChartsLoadSpinner.setVisibility(View.GONE);
    }

    private final Runnable mGraphView = new Runnable() {
        @Override
        public void run() {
            drawBitcoinAverageUI();
        }
    };

    private final Runnable mError = new Runnable() {
        @Override
        public void run() {
            errorOccured();
        }
    };

    private void errorOccured() {

        removeLoadingSpinner();

        if (dialog == null || !dialog.isShowing()) {
            // Display error Dialog
            Resources res = getResources();
            dialog = Utils.errorDialog(this, String.format(res.getString(R.string.connectionError), "data", "BitcoinAverage.com"));
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
            dialog = Utils.errorDialog(this, "A problem occurred when generating BitcoinAverage table", "Error");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("googleAnalyticsPref", false)) {
            EasyTracker.getInstance(this).activityStart(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

}
