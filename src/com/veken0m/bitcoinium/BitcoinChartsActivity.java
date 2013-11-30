
package com.veken0m.bitcoinium;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.veken0m.bitcoinium.utils.KarmaAdsUtils;
import com.veken0m.bitcoinium.utils.Utils;
import com.xeiam.xchange.bitcoincharts.BitcoinChartsFactory;
import com.xeiam.xchange.bitcoincharts.dto.marketdata.BitcoinChartsTicker;

import java.util.Arrays;
import java.util.Comparator;

public class BitcoinChartsActivity extends SherlockActivity implements OnItemSelectedListener {

    final static Handler mOrderHandler = new Handler();
    BitcoinChartsTicker[] marketData;
    private Spinner spinner;
    private ArrayAdapter<String> dataAdapter;
    String currencyFilter = "SHOW ALL";

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
    
    public void createCurrencyDropdown(){
        // Re-populate the dropdown menu
        final String[] dropdownValues = {"SHOW ALL",
                "USD", "CAD", "GBP", "EUR", "CNY", "RUR", "PLN", "JPY", "XRP", "SLL", "AUD", "BRL",
                "HKD", "SEK", "NOK", "LTC", "SGD", "NZD", "XRP", "ZAR", "CHF", "DKK", "ARS", "MXN",
                "INR", "THB", "RUB"
        };

        spinner = (Spinner) findViewById(R.id.bitcoincharts_currency_spinner);
        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dropdownValues);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);
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
            viewBitcoinCharts();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.bitcoincharts);
        drawBitcoinChartsUI();
    }
    

    /**
     * Fetch the Bitcoin Charts data
     */
    public void getBitcoinCharts() {
        try {
            marketData = BitcoinChartsFactory.createInstance().getMarketData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Draw the Tickers to the screen in a table
     */
    public void drawBitcoinChartsUI() {

        final TableLayout t1 = (TableLayout) findViewById(R.id.bitcoincharts_list);
        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress3);
        linlaHeaderProgress.setVisibility(View.GONE);

        int backGroundColor = Color.rgb(51, 51, 51);
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 1f);

        try {
            // Sort Tickers by volume
            Arrays.sort(marketData, new Comparator<BitcoinChartsTicker>() {
                @Override
                public int compare(BitcoinChartsTicker entry1,
                        BitcoinChartsTicker entry2) {
                    return entry2.getVolume().compareTo(entry1.getVolume());
                }
            });

            for (BitcoinChartsTicker data : marketData) {

                // Only print active exchanges... vol > 0
                if (data.getVolume().intValue() != 0
                        && (currencyFilter.equalsIgnoreCase("SHOW ALL") || data.getCurrency()
                                .contains(currencyFilter))) {

                    final TableRow tr1 = new TableRow(this);

                    final TextView tvSymbol = new TextView(this);
                    final TextView tvLast = new TextView(this);
                    // final TextView tvAvg = new TextView(this);
                    final TextView tvVolume = new TextView(this);
                    final TextView tvHigh = new TextView(this);
                    final TextView tvLow = new TextView(this);
                    // final TextView tvBid = new TextView(this);
                    // final TextView tvAsk = new TextView(this);
                    String last = Utils.formatDecimal(data.getClose(), 2, true);
                    String high = Utils.formatDecimal(data.getHigh(), 2, true);
                    String low = Utils.formatDecimal(data.getLow(), 2, true);
                    String vol = Utils.formatDecimal(data.getVolume(), 2, true);
                    // String avg = Utils.formatDecimal(data.getAvg(), 2, true);
                    // String bid = Utils.formatDecimal(data.getBid(), 2, true);
                    // String ask = Utils.formatDecimal(data.getAsk(), 2, true);

                    tvSymbol.setText(data.getSymbol());
                    tvSymbol.setLayoutParams(params);
                    Utils.setTextViewParams(tvLast, last);
                    Utils.setTextViewParams(tvVolume, vol);
                    Utils.setTextViewParams(tvLow, low);
                    Utils.setTextViewParams(tvHigh, high);
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
                    tr1.addView(tvLow);
                    tr1.addView(tvHigh);
                    // tr1.addView(tvBid);
                    // tr1.addView(tvAsk);
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
            connectionFailed();
        }
    }

    private void viewBitcoinCharts() {
        bitcoinchartsThread gt = new bitcoinchartsThread();
        gt.start();
    }

    public class bitcoinchartsThread extends Thread {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TableLayout t1 = (TableLayout) findViewById(R.id.bitcoincharts_list);
                    t1.removeAllViews();
                    LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress3);
                    linlaHeaderProgress.setVisibility(View.VISIBLE);
                }
            });
            getBitcoinCharts();
            mOrderHandler.post(mGraphView);
        }
    }

    final Runnable mGraphView = new Runnable() {
        @Override
        public void run() {
            drawBitcoinChartsUI();
        }
    };

    private void connectionFailed() {
        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress3);
        linlaHeaderProgress.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Could not retrieve data from " + "Bitcoin Charts"
                + ".\n\nCheck 3G or Wifi connection and try again.");
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
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        currencyFilter = (String) parent.getItemAtPosition(pos);
        viewBitcoinCharts();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing
    }
    

}
