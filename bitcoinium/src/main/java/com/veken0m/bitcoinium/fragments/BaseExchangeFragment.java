
package com.veken0m.bitcoinium.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.veken0m.bitcoinium.BitcoinAverageActivity;
import com.veken0m.bitcoinium.BitcoinChartsActivity;
import com.veken0m.bitcoinium.GraphActivity;
import com.veken0m.bitcoinium.MinerStatsActivity;
import com.veken0m.bitcoinium.MinerWidgetProvider;
import com.veken0m.bitcoinium.OrderbookActivity;
import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.WebViewerActivity;
import com.veken0m.bitcoinium.WidgetProvider;
import com.veken0m.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseExchangeFragment extends SherlockFragment {

    Activity activity = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        buildMenu(view, prefs.getString("favExchangePref", "bitstamp"), true);
        return view;
    }

    // Attaches OnClickListeners to menu buttons
    protected void buildMenu(View view, final String exchange, final Boolean graph) {

        activity = getActivity();

        final Button widgetRefreshButton = (Button) view.findViewById(R.id.widgetrefresh);
        widgetRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity.getApplicationContext(), WidgetProvider.class);
                intent.setAction(Constants.REFRESH);
                Intent intent2 = new Intent(activity.getApplicationContext(), MinerWidgetProvider.class);
                intent2.setAction(Constants.REFRESH);
                activity.sendBroadcast(intent);
                activity.sendBroadcast(intent2);
                activity.moveTaskToBack(true);
            }
        });

        final Button displayGraphButton = (Button) view
                .findViewById(R.id.displaygraph);
        displayGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!graph) {
                    Toast.makeText(
                            activity,
                            getString(R.string.priceGraphNotSupported),
                            Toast.LENGTH_LONG).show();
                } else {
                    Intent graphActivity = new Intent(activity.getBaseContext(), GraphActivity.class);
                    graphActivity.putExtra("exchange", exchange);
                    startActivity(graphActivity);
                }
            }
        });

        final Button orderbookButton = (Button) view
                .findViewById(R.id.orderbook);
        orderbookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderbookActivity = new Intent(activity.getBaseContext(), OrderbookActivity.class);
                orderbookActivity.putExtra("exchange", exchange);
                startActivity(orderbookActivity);
            }
        });

        final Button bitcoinChartsButton = (Button) view
                .findViewById(R.id.bitcoincharts);
        bitcoinChartsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bitcoinChartsActivity = new Intent(activity.getBaseContext(), BitcoinChartsActivity.class);
                startActivity(bitcoinChartsActivity);
            }
        });

        final Button bitcoinAverageButton = (Button) view
                .findViewById(R.id.bitcoinaverage);
        bitcoinAverageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bitcoinAverageActivity = new Intent(activity.getBaseContext(), BitcoinAverageActivity.class);
                startActivity(bitcoinAverageActivity);
            }
        });

        final Button minerStatsButton = (Button) view
                .findViewById(R.id.minerstats);
        minerStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent minerstatsActivity = new Intent(activity.getBaseContext(), MinerStatsActivity.class);
                startActivity(minerstatsActivity);
            }
        });

        final Button marketDepth = (Button) view.findViewById(R.id.marketdepth);
        marketDepth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getBaseContext(), WebViewerActivity.class);
                startActivity(intent);
            }
        });
    }

}
