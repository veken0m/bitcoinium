package com.veken0m.cavirtex;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;

public class MtGoxFragment extends SherlockFragment {

	public static final String REFRESH = "com.veken0m.cavirtex.REFRESH";
	public static final String PREFERENCES = "com.veken0m.cavirtex.PREFERENCES";
	public static final String GRAPH = "com.veken0m.cavirtex.GRAPH";
	public static final String VIRTEX = "com.veken0m.cavirtex.VIRTEX";
	public static final String MTGOX = "com.veken0m.cavirtex.MTGOX";

	/**
	 * Menu options we display when launching
	 */
	final CharSequence cTrades = "VirtEx Trades";
	final CharSequence cOrderBook = "VirtEx OrderBook";
	final CharSequence cmtGoxLive = "mtGoxLive";
	final CharSequence cBTCCharts = "BTCCharts";
	final CharSequence cClose = "Close";

	/**
	 * Other constants
	 */
	final int SCREEN_ORIENTATION_LANDSCAPE = 0;
	final Handler mAccountHandler = new Handler();
	final Handler mOrderHandler = new Handler();
	final Handler mPlaceOrderHandler = new Handler();
	final Handler mGraphHandler = new Handler();
	View view = null;

	public MtGoxFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.menu_fragment, container, false);
		buttonMaker(view);
		return view;
	}

	public void buttonMaker(View view) {
		final Button widgetRefreshButton = (Button) view
				.findViewById(R.id.widgetrefresh);
		widgetRefreshButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity()
						.getApplicationContext(), WatcherWidgetProvider.class);
				intent.setAction(REFRESH);
				getActivity().sendBroadcast(intent);
				Intent intent2 = new Intent(getActivity()
						.getApplicationContext(), WatcherWidgetProvider2.class);
				intent2.setAction(REFRESH);
				getActivity().sendBroadcast(intent2);
				getActivity().moveTaskToBack(true);
			}
		});

		final Button displayGraphButton = (Button) view
				.findViewById(R.id.displaygraph);
		displayGraphButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent graphActivity = new Intent(getActivity()
						.getBaseContext(), Graph.class);
				graphActivity.putExtra("exchange", MTGOX);
				startActivity(graphActivity);
			}
		});

		final Button orderbookButton = (Button) view
				.findViewById(R.id.orderbook);
		orderbookButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent orderbookActivity = new Intent(getActivity()
						.getBaseContext(), Orderbook.class);
				orderbookActivity.putExtra("exchange", MTGOX);
				startActivity(orderbookActivity);
			}
		});

		final Button minerStatsButton = (Button) view
				.findViewById(R.id.minerstats);
		minerStatsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent minerstatsActivity = new Intent(getActivity()
						.getBaseContext(), MinerStats.class);
				startActivity(minerstatsActivity);
			}
		});

		final Button marketDepth = (Button) view.findViewById(R.id.marketdepth);
		marketDepth.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity().getBaseContext(),
						WebViewer.class);
				startActivity(intent);
			}
		});
	}

}
