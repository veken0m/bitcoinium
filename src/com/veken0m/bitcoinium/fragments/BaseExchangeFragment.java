
package com.veken0m.bitcoinium;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class BaseExchangeFragment extends SherlockFragment {
    public static final String REFRESH = "com.veken0m.bitcoinium.REFRESH";
    public static final String VIRTEX = "VirtExExchange";
    public static final String MTGOX = "MtGoxExchange";
    public static final String BTCE = "BTCEExchange";
    public static final String BITSTAMP = "BitstampExchange";
    public static final String CAMPBX = "CampBXExchange";
    public static final String BITCOINCENTRAL = "BitcoinCentralExchange";
    public static final String BITFLOOR = "BitfloorExchange";
    public static final String BITCOIN24 = "Bitcoin24Exchange";
    public static final String BTCCHINA = "BTCChinaExchange";
    public static final String BITCUREX = "BitcurexExchange";
    public static final String KRAKEN = "KrakenExchange";

    // Attaches OnClickListeners to menu buttons
    public void buildMenu(View view, final String exchange, final Boolean graph) {
        
        final Button widgetRefreshButton = (Button) view
                .findViewById(R.id.widgetrefresh);
        widgetRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity()
                        .getApplicationContext(), WidgetProvider.class);
                intent.setAction(REFRESH);
                Intent intent2 = new Intent(getActivity()
                        .getApplicationContext(), MinerWidgetProvider.class);
                intent2.setAction(REFRESH);
                getActivity().sendBroadcast(intent);
                getActivity().sendBroadcast(intent2);
                getActivity().moveTaskToBack(true);
            }
        });

        final Button displayGraphButton = (Button) view
                .findViewById(R.id.displaygraph);
        displayGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!graph) {
                    Toast.makeText(
                            getActivity(),
                            "This exchange does not currently support Price Graph",
                            Toast.LENGTH_LONG).show();
                } else {
                    Intent graphActivity = new Intent(getActivity()
                            .getBaseContext(), GraphActivity.class);
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
                Intent orderbookActivity = new Intent(getActivity()
                        .getBaseContext(), OrderbookActivity.class);
                orderbookActivity.putExtra("exchange", exchange);
                startActivity(orderbookActivity);
            }
        });

        final Button bitcoinChartsButton = (Button) view
                .findViewById(R.id.bitcoincharts);
        bitcoinChartsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bitcoinChartsActivity = new Intent(getActivity()
                        .getBaseContext(), BitcoinChartsActivity.class);
                startActivity(bitcoinChartsActivity);
            }
        });
        
        final Button bitcoinAverageButton = (Button) view
                .findViewById(R.id.bitcoinaverage);
        bitcoinAverageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bitcoinAverageActivity = new Intent(getActivity()
                        .getBaseContext(), BitcoinAverageActivity.class);
                startActivity(bitcoinAverageActivity);
            }
        });

        final Button minerStatsButton = (Button) view
                .findViewById(R.id.minerstats);
        minerStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent minerstatsActivity = new Intent(getActivity()
                        .getBaseContext(), MinerStatsActivity.class);
                startActivity(minerstatsActivity);
            }
        });

        final Button marketDepth = (Button) view.findViewById(R.id.marketdepth);
        marketDepth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getBaseContext(),
                        WebViewerActivity.class);
                startActivity(intent);
            }
        });
    }

}
