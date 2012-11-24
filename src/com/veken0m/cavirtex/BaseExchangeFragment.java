package com.veken0m.cavirtex;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;

public class BaseExchangeFragment extends SherlockFragment{
	public static final String REFRESH = "com.veken0m.cavirtex.REFRESH";
	public static final String VIRTEX = "com.veken0m.cavirtex.VIRTEX";
	public static final String MTGOX = "com.veken0m.cavirtex.MTGOX";
	
	
	// Attaches OnClickListeners to menu buttons
	public void buildMenu(View view, final String exchange) {
		final Button widgetRefreshButton = (Button) view
				.findViewById(R.id.widgetrefresh);
		widgetRefreshButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity()
						.getApplicationContext(), WidgetProvider.class);
				intent.setAction(REFRESH);
				getActivity().sendBroadcast(intent);
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
				graphActivity.putExtra("exchange", exchange);
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
				orderbookActivity.putExtra("exchange", exchange);
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
