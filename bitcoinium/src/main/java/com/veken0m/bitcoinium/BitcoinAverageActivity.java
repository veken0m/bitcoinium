
package com.veken0m.bitcoinium;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.veken0m.bitcoinium.preferences.PreferencesActivity;
import com.veken0m.utils.Constants;
import com.veken0m.utils.Utils;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

// import com.veken0m.utils.KarmaAdsUtils;

public class BitcoinAverageActivity extends BaseActivity {

    private final static Handler mOrderHandler = new Handler();
    final private ArrayList<Ticker> tickers;

    public BitcoinAverageActivity() {
        tickers = new ArrayList<Ticker>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bitcoinaverage);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.show();

        // KarmaAdsUtils.initAd(this);
        viewBitcoinAverage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_preferences:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            case R.id.action_refresh:
                viewBitcoinAverage();
                return true;
        }

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
            for (String currency : Constants.BITCOINAVERAGE_CURRENCIES) {

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
        removeLoadingSpinner(R.id.bitcoinaverage_loadSpinner);

        boolean bBackGroundColor = false;

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
                tvSymbol.setTextColor(Color.WHITE);
                Utils.setTextViewParams(tvLast, ticker.getLast());
                Utils.setTextViewParams(tvVolume, ticker.getVolume());
                Utils.setTextViewParams(tvBid, ticker.getBid());
                Utils.setTextViewParams(tvAsk, ticker.getAsk());
                // Utils.setTextViewParams(tvAvg, avg);

                final TableRow newRow = new TableRow(this);

                // Toggle background color
                if (bBackGroundColor = !bBackGroundColor)
                    newRow.setBackgroundColor(getResources().getColor(R.color.light_tableRow));

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
            notConnected(R.id.bitcoinaverage_loadSpinner);
        }
    }

    private class bitcoinAverageThread extends Thread {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startLoading(R.id.bitcoinaverage_list, R.id.bitcoinaverage_loadSpinner);
                }
            });
            if (getBitcoinAverage())
                mOrderHandler.post(mGraphView);
            else
                mOrderHandler.post(mError);
        }
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

        removeLoadingSpinner(R.id.bitcoinaverage_loadSpinner);

        if (dialog == null || !dialog.isShowing()) {
            // Display error Dialog
            Resources res = getResources();
            dialog = Utils.errorDialog(this, String.format(res.getString(R.string.connectionError), "data", "BitcoinAverage.com"));
        }
    }

    private void failedToDrawUI() {

        removeLoadingSpinner(R.id.bitcoinaverage_loadSpinner);
        if (dialog == null || !dialog.isShowing()) {
            dialog = Utils.errorDialog(this, "A problem occurred when generating BitcoinAverage table", "Error");
        }
    }
}
