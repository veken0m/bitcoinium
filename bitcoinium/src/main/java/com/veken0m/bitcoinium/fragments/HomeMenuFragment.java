
package com.veken0m.bitcoinium.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.veken0m.bitcoinium.exchanges.Exchange;
import com.veken0m.utils.Constants;

public class HomeMenuFragment extends SherlockFragment {

    private Activity activity = null;
    private Context context = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = getActivity();
        context = activity.getApplicationContext();

        String exchangeName = getArguments().getString("exchange");

        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        buildMenu(view, exchangeName);
        return view;
    }

    // Attaches OnClickListeners to menu buttons
    void buildMenu(View view, final String exchangeName) {

        final Button widgetRefreshButton = (Button) view.findViewById(R.id.widgetrefresh);
        widgetRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, WidgetProvider.class);
                intent.setAction(Constants.REFRESH);
                Intent intent2 = new Intent(context, MinerWidgetProvider.class);
                intent2.setAction(Constants.REFRESH);
                activity.sendBroadcast(intent);
                activity.sendBroadcast(intent2);
                activity.moveTaskToBack(true);
            }
        });

        final Button displayGraphButton = (Button) view.findViewById(R.id.displaygraph);
        displayGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent graphActivity = new Intent(context, GraphActivity.class);

                Exchange exchange = null;
                try {
                    exchange = new Exchange(context, exchangeName);
                } catch (Exception e) {
                    // Do nothing
                }

                if(exchange != null && exchange.supportsOrderbook())
                    graphActivity.putExtra("exchange", exchange.getIdentifier());
                else
                    graphActivity.removeExtra("exchange");

                startActivity(graphActivity);
            }
        });

        final Button orderbookButton = (Button) view.findViewById(R.id.orderbook);
        orderbookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent orderbookActivity = new Intent(context, OrderbookActivity.class);

                Exchange exchange = null;
                try {
                    exchange = new Exchange(context, exchangeName);
                } catch (Exception e) {
                    // Do nothing
                }

                if(exchange != null && exchange.supportsOrderbook())
                    orderbookActivity.putExtra("exchange", exchange.getIdentifier());
                else
                    orderbookActivity.removeExtra("exchange");

                startActivity(orderbookActivity);
            }
        });

        final Button bitcoinChartsButton = (Button) view.findViewById(R.id.bitcoincharts);
        bitcoinChartsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bitcoinChartsActivity = new Intent(context, BitcoinChartsActivity.class);
                startActivity(bitcoinChartsActivity);
            }
        });

        final Button bitcoinAverageButton = (Button) view.findViewById(R.id.bitcoinaverage);
        bitcoinAverageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bitcoinAverageActivity = new Intent(context, BitcoinAverageActivity.class);
                startActivity(bitcoinAverageActivity);
            }
        });

        final Button minerStatsButton = (Button) view.findViewById(R.id.minerstats);
        minerStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent minerstatsActivity = new Intent(context, MinerStatsActivity.class);
                startActivity(minerstatsActivity);
            }
        });

        final Button marketDepth = (Button) view.findViewById(R.id.marketdepth);
        marketDepth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webViewerActivity = new Intent(context, WebViewerActivity.class);
                startActivity(webViewerActivity);
            }
        });
    }

}
