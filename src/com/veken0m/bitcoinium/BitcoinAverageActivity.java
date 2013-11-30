
package com.veken0m.bitcoinium;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.veken0m.bitcoinium.utils.Utils;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.dto.marketdata.Ticker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class BitcoinAverageActivity extends SherlockActivity {

    final static Handler mOrderHandler = new Handler();
    ArrayList<Ticker> tickers = new ArrayList<Ticker>();
    String[] curr = new String[] {
            "AUD", "BRL", "CAD", "CNY", "CZK", "EUR", "GBP", "ILS", "JPY", "NOK", "NZD",
            "PLN", "RUB", "SEK", "USD", "ZAR"
    };

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
        if (item.getItemId() == R.id.action_preferences) {
            startActivity(new Intent(this, PreferencesActivity.class));
        }
        if (item.getItemId() == R.id.action_refresh) {
            viewBitcoinAverage();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.bitcoinaverage);
        drawBitcoinAverageUI();
    }

    /**
     * Fetch the Bitcoin Average data
     */
    public void getBitcoinAverage() {
        try {
            tickers.clear();

            for (String currency : curr) {
                tickers.add(ExchangeFactory.INSTANCE
                        .createExchange("com.xeiam.xchange.bitcoinaverage.BitcoinAverageExchange")
                        .getPollingMarketDataService()
                        .getTicker("BTC",currency));
            }

        } catch (Exception e) {
            tickers = null;
            e.printStackTrace();
        }
    }

    /**
     * Draw the Tickers to the screen in a table
     */
    public void drawBitcoinAverageUI() {

        final TableLayout t1 = (TableLayout) findViewById(R.id.bitcoinaverage_list);
        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress3);
        linlaHeaderProgress.setVisibility(View.GONE);

        int backGroundColor = Color.rgb(51, 51, 51);
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 1f);

        try {
            
            // Sort Tickers by volume
            Collections.sort(tickers, new
                    Comparator<Ticker>() {
                        @Override
                        public int compare(Ticker entry1, Ticker entry2) {
                            return entry2.getVolume().compareTo(entry1.getVolume());
                        }
                    });
             

            for (Ticker ticker : tickers) {

                // Only print active exchanges... vol > 0
                if (ticker.getVolume().intValue() != 0) {

                    final TableRow tr1 = new TableRow(this);

                    final TextView tvSymbol = new TextView(this);
                    final TextView tvLast = new TextView(this);
                    // final TextView tvAvg = new TextView(this);
                    final TextView tvVolume = new TextView(this);
                    final TextView tvBid = new TextView(this);
                    final TextView tvAsk = new TextView(this);
                    String last = Utils.formatDecimal(ticker.getLast().getAmount(), 2, true);
                    String volume = Utils.formatDecimal(ticker.getVolume(), 2, true);
                    String bid = Utils.formatDecimal(ticker.getBid().getAmount(), 2, true);
                    String ask = Utils.formatDecimal(ticker.getAsk().getAmount(), 2, true);

                    tvSymbol.setText(ticker.getLast().getCurrencyUnit().getCurrencyCode());
                    tvSymbol.setLayoutParams(params);
                    Utils.setTextViewParams(tvLast, last);
                    Utils.setTextViewParams(tvVolume, volume);
                    Utils.setTextViewParams(tvBid, bid);
                    Utils.setTextViewParams(tvAsk, ask);
                    // Utils.setTextViewParams(tvAvg, avg);
                    // Utils.setTextViewParams(tvBid, bid);
                    // Utils.setTextViewParams(tvAsk, ask);

                    // Toggle background color
                    if (backGroundColor == Color.BLACK) {
                        backGroundColor = Color.rgb(31, 31, 31);
                    } else {
                        backGroundColor = Color.BLACK;
                    }

                    tr1.setBackgroundColor(backGroundColor);

                    tr1.addView(tvSymbol);
                    tr1.addView(tvLast);
                    // tr1.addView(tvAvg);
                    tr1.addView(tvVolume);
                    tr1.addView(tvBid);
                    tr1.addView(tvAsk);
                    tr1.setPadding(0, 3, 0, 3);
                    t1.addView(tr1);

                    // Insert a divider between rows
                    View divider = new View(this);
                    divider.setLayoutParams(new LayoutParams(
                            LayoutParams.MATCH_PARENT, 1));
                    divider.setBackgroundColor(Color.rgb(51, 51, 51));
                    t1.addView(divider);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            connectionFailed();
        }
    }

    private void viewBitcoinAverage() {
        bitcoinaverageThread gt = new bitcoinaverageThread();
        gt.start();
    }

    public class bitcoinaverageThread extends Thread {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TableLayout t1 = (TableLayout) findViewById(R.id.bitcoinaverage_list);
                    t1.removeAllViews();
                    LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress3);
                    linlaHeaderProgress.setVisibility(View.VISIBLE);
                }
            });
            getBitcoinAverage();
            mOrderHandler.post(mGraphView);
        }
    }

    final Runnable mGraphView = new Runnable() {
        @Override
        public void run() {
            drawBitcoinAverageUI();
        }
    };

    private void connectionFailed() {
        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress3);
        linlaHeaderProgress.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Resources res = getResources();
        String text = String.format(res.getString(R.string.connectionError), "data", "BitcoinAverage.com");
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

}
