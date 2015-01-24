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
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.veken0m.bitcoinium.preferences.PreferencesActivity;
import com.veken0m.utils.Utils;
import com.xeiam.xchange.bitcoincharts.BitcoinChartsFactory;
import com.xeiam.xchange.bitcoincharts.dto.marketdata.BitcoinChartsTicker;

import java.util.Arrays;
import java.util.Comparator;

// import com.veken0m.utils.KarmaAdsUtils;

public class BitcoinChartsActivity extends BaseActivity implements OnItemSelectedListener, SwipeRefreshLayout.OnRefreshListener
{
    private final static Handler mOrderHandler = new Handler();
    private final Runnable mBitcoinChartsView;
    private final Runnable mError;
    private BitcoinChartsTicker[] marketData = null;
    private String currencyFilter;

    public BitcoinChartsActivity()
    {
        currencyFilter = "SHOW ALL";
        mBitcoinChartsView = new Runnable()
        {
            @Override
            public void run()
            {
                if (marketData != null) drawBitcoinChartsUI();
            }
        };

        mError = new Runnable()
        {
            @Override
            public void run()
            {
                errorOccured();
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitcoincharts);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.bitcoincharts_swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.holo_blue_light);

        // Temp fix to show refresh indicator. This is a bug in android.support.v4 v21.0.1
        // https://code.google.com/p/android/issues/detail?id=77712
        swipeLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        populateCurrencyDropdown();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.show();

        onRefresh();
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
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            case R.id.action_refresh:
                viewBitcoinCharts();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_bitcoincharts);
        populateCurrencyDropdown();

        if (marketData != null) drawBitcoinChartsUI();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        String prevCurrencyfilter = currencyFilter;
        currencyFilter = (String) parent.getItemAtPosition(pos);
        if (prevCurrencyfilter != null && currencyFilter != null && !currencyFilter.equals(prevCurrencyfilter))
            drawBitcoinChartsUI();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
        // Do nothing
    }

    void populateCurrencyDropdown()
    {
        // Re-populate the dropdown menu
        final String[] dropdownValues = getResources().getStringArray(R.array.bitcoinChartsDropdown);

        Spinner spinner = (Spinner) findViewById(R.id.bitcoincharts_currency_spinner);
        if (spinner != null)
        {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, dropdownValues);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
            spinner.setOnItemSelectedListener(this);
        }
    }

    /**
     * Fetch the Bitcoin Charts data
     */
    boolean getBitcoinCharts()
    {
        try
        {
            marketData = BitcoinChartsFactory.createInstance().getMarketData();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Draw the Tickers to the screen in a table
     */
    void drawBitcoinChartsUI()
    {
        if (swipeLayout != null)
            swipeLayout.setRefreshing(true);
        TableLayout bitcoinChartsTable = (TableLayout) findViewById(R.id.bitcoincharts_list);

        if (marketData != null && marketData.length > 0 && bitcoinChartsTable != null)
        {
            // Clear table
            bitcoinChartsTable.removeAllViews();

            // Sort Tickers by volume
            Arrays.sort(marketData, new Comparator<BitcoinChartsTicker>()
            {
                @Override
                public int compare(BitcoinChartsTicker entry1,
                                   BitcoinChartsTicker entry2)
                {
                    return entry2.getVolume().compareTo(entry1.getVolume());
                }
            });

            boolean bBackGroundColor = false;
            for (BitcoinChartsTicker data : marketData)
            {
                // Only print active exchanges... vol != 0 or contains selected currency
                if (data.getVolume().floatValue() != 0.0
                        && (currencyFilter.equals("SHOW ALL") || data.getCurrency().contains(currencyFilter)))
                {
                    final TextView tvSymbol = new TextView(this);
                    final TextView tvLast = new TextView(this);
                    final TextView tvVolume = new TextView(this);
                    final TextView tvHigh = new TextView(this);
                    final TextView tvLow = new TextView(this);
                    // final TextView tvAvg = new TextView(this);
                    // final TextView tvBid = new TextView(this);
                    // final TextView tvAsk = new TextView(this);

                    tvSymbol.setText(data.getSymbol());
                    tvSymbol.setTextColor(Color.WHITE);
                    Utils.setTextViewParams(tvLast, data.getClose());
                    Utils.setTextViewParams(tvVolume, data.getVolume());
                    Utils.setTextViewParams(tvLow, data.getLow());
                    Utils.setTextViewParams(tvHigh, data.getHigh());
                    // Utils.setTextViewParams(tvAvg, data.getAvg());
                    // Utils.setTextViewParams(tvBid, data.getBid());
                    // Utils.setTextViewParams(tvAsk, data.getAsk());

                    final TableRow newRow = new TableRow(this);

                    // Toggle background color
                    if (bBackGroundColor = !bBackGroundColor)
                        newRow.setBackgroundColor(getResources().getColor(R.color.light_tableRow));

                    newRow.addView(tvSymbol, Utils.adjustParams);
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
        }
        else
        {
            failedToDrawUI();
        }
        if (swipeLayout != null)
            swipeLayout.setRefreshing(false);
    }

    private void viewBitcoinCharts()
    {
        if (swipeLayout != null)
            swipeLayout.setRefreshing(true);
        if (Utils.isConnected(this))
            (new bitcoinChartsThread()).start();
        else
            notConnected();
    }

    private void errorOccured()
    {
        if (swipeLayout != null)
            swipeLayout.setRefreshing(false);
        try
        {
            if (dialog == null || !dialog.isShowing())
            {
                // Display error Dialog
                Resources res = getResources();
                dialog = Utils.errorDialog(this, String.format(res.getString(R.string.error_exchangeConnection), "tickers", "Bitcoin Charts"));
            }
        }
        catch (WindowManager.BadTokenException e)
        {
            // This happens when we try to show a dialog when app is not in the foreground. Suppress it for now
        }
    }

    private void failedToDrawUI()
    {
        if (swipeLayout != null)
            swipeLayout.setRefreshing(false);

        if (dialog == null || !dialog.isShowing())
        {
            // Display error Dialog
            dialog = Utils.errorDialog(this, "A problem occurred when generating Bitcoin Charts table", "Error");
        }
    }

    @Override
    public void onRefresh()
    {
        viewBitcoinCharts();
    }

    private class bitcoinChartsThread extends Thread
    {
        @Override
        public void run()
        {
            if (getBitcoinCharts())
                mOrderHandler.post(mBitcoinChartsView);
            else
                mOrderHandler.post(mError);
        }
    }
}

