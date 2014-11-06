package com.veken0m.bitcoinium;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.veken0m.bitcoinium.preferences.PreferencesActivity;
import com.veken0m.utils.Utils;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.bitcoinaverage.BitcoinAverageExchange;
import com.xeiam.xchange.bitcoinaverage.dto.marketdata.BitcoinAverageTicker;
import com.xeiam.xchange.bitcoinaverage.service.polling.BitcoinAverageMarketDataServiceRaw;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// import com.veken0m.utils.KarmaAdsUtils;

public class BitcoinAverageActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final static Handler mOrderHandler = new Handler();
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
    private Map<String, BitcoinAverageTicker> tickers = new HashMap<String, BitcoinAverageTicker>();

    public BitcoinAverageActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitcoinaverage);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.bitcoinaverage_swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(R.color.holo_blue_light);

        // Temp fix to show refresh indicator. This is a bug in android.support.v4 v21.0.1
        // https://code.google.com/p/android/issues/detail?id=77712
        swipeLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

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
        setContentView(R.layout.activity_bitcoinaverage);
        if (tickers.size() > 0) drawBitcoinAverageUI();
    }

    /**
     * Fetch the Bitcoin Average data
     */
    boolean getBitcoinAverage() {

        tickers.clear();
        Exchange bitcoinAverageExchange = ExchangeFactory.INSTANCE.createExchange(BitcoinAverageExchange.class.getName());
        BitcoinAverageMarketDataServiceRaw pollingService = (BitcoinAverageMarketDataServiceRaw) bitcoinAverageExchange.getPollingMarketDataService();

        if (pollingService != null) {
            try {
                tickers = pollingService.getBitcoinAverageAllTickers().getTickers();
            } catch (IOException e) {
                // Skip ticker and keep looping
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Draw the Tickers to the screen in a table
     */
    void drawBitcoinAverageUI() {

        TableLayout bitcoinAverageTable = (TableLayout) findViewById(R.id.bitcoinaverage_list);

        boolean bBackGroundColor = false;

        if (tickers.size() > 0 && bitcoinAverageTable != null) {

            // Clear table
            bitcoinAverageTable.removeAllViews();

            List<Map.Entry<String, BitcoinAverageTicker>> entries = new LinkedList<Map.Entry<String, BitcoinAverageTicker>>(tickers.entrySet());

            // Sort Tickers by volume
            Collections.sort(entries, new Comparator<Map.Entry<String, BitcoinAverageTicker>>() {

                @Override
                public int compare(Map.Entry<String, BitcoinAverageTicker> o1, Map.Entry<String, BitcoinAverageTicker> o2) {
                    return o2.getValue().getVolume().compareTo(o1.getValue().getVolume());
                }
            });

            for (Map.Entry<String, BitcoinAverageTicker> tickerEntry : entries) {

                BitcoinAverageTicker ticker = tickerEntry.getValue();
                if (ticker.getVolume().floatValue() > 0.0) {

                    final TextView tvSymbol = new TextView(this);
                    final TextView tvLast = new TextView(this);
                    final TextView tvVolume = new TextView(this);
                    final TextView tvBid = new TextView(this);
                    final TextView tvAsk = new TextView(this);
                    // final TextView tvAvg = new TextView(this);

                    tvSymbol.setText(tickerEntry.getKey());
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

                    newRow.addView(tvSymbol, Utils.adjustParams);
                    newRow.addView(tvLast);
                    newRow.addView(tvVolume);
                    newRow.addView(tvBid);
                    newRow.addView(tvAsk);
                    // newRow.addView(tvAvg);
                    newRow.setPadding(0, 3, 0, 3);
                    bitcoinAverageTable.addView(newRow);
                }
            }
        } else {
            failedToDrawUI();
        }
        if(swipeLayout != null)
            swipeLayout.setRefreshing(false);
    }

    private void viewBitcoinAverage() {
        swipeLayout.setRefreshing(true);
        if (Utils.isConnected(this))
            (new bitcoinAverageThread()).start();
        else
            notConnected();
    }

    private void errorOccured() {
        if(swipeLayout != null)
            swipeLayout.setRefreshing(false);

        try {
            if (dialog == null || !dialog.isShowing()) {
                // Display error Dialog
                Resources res = getResources();
                dialog = Utils.errorDialog(this, String.format(res.getString(R.string.error_exchangeConnection), "data", "BitcoinAverage.com"));
            }
        } catch (WindowManager.BadTokenException e) {
            // This happens when we try to show a dialog when app is not in the foreground. Suppress it for now
        }
    }

    private void failedToDrawUI() {
        if(swipeLayout != null)
            swipeLayout.setRefreshing(false);

        if (dialog == null || !dialog.isShowing())
            dialog = Utils.errorDialog(this, "A problem occurred when generating BitcoinAverage table", "Error");
    }

    @Override
    public void onRefresh() {
        viewBitcoinAverage();
    }

    private class bitcoinAverageThread extends Thread {

        @Override
        public void run() {
            if (getBitcoinAverage())
                mOrderHandler.post(mGraphView);
            else
                mOrderHandler.post(mError);
        }
    }
}
