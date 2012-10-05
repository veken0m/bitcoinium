package com.veken0m.cavirtex;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.xeiam.xchange.Currencies;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.service.marketdata.polling.PollingMarketDataService;

public class Graph extends SherlockActivity {

	private GraphViewer g_graphView = null;
	private ProgressDialog graphProgressDialog;
	final Handler mOrderHandler = new Handler();
	public static final String VIRTEX = "com.veken0m.cavirtex.VIRTEX";
	public static final String MTGOX = "com.veken0m.cavirtex.MTGOX";

	public static final String sVirtex = "VirtEx";
	public static final String sMtgox = "MtGox";
	public static String exchangeName = "";
	public static String currency = "";
	private static PollingMarketDataService marketDataService;
	public List tradesList;

	public String exchange = VIRTEX;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.minerstats);

		ActionBar actionbar = getSupportActionBar();
		actionbar.show();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			exchange = extras.getString("exchange");
		}

		if (exchange.equalsIgnoreCase(MTGOX)) {
			exchangeName = sMtgox;
			currency = Currencies.USD;
		}
		if (exchange.equalsIgnoreCase(VIRTEX)) {
			exchangeName = sVirtex;
			currency = Currencies.CAD;
		}

		viewGraph();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		// preparation code here
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.preferences) {
			startActivity(new Intent(this, Preferences.class));
		}
		return super.onOptionsItemSelected(item);
	}

	public class GraphThread extends Thread {

		@Override
		public void run() {
			generatePreviousPriceGraph();
			mOrderHandler.post(mGraphView);
		}

	}

	/**
	 * mGraphView run() is called when our GraphThread is finished
	 */
	final Runnable mGraphView = new Runnable() {
		@Override
		public void run() {
			safelyDismiss(graphProgressDialog);
			if (g_graphView != null) {

				setContentView(g_graphView);
			} else {
				createPopup("Unable to retrieve transactions from "
						+ exchangeName + ", check your 3G or WiFi connection");
			}
		}
	};

	/**
	 * generatePreviousPriceGraph prepares price graph of the last 48 hrs -It
	 * connects to exchange, reads the JSON, and plots a GraphView of it
	 */
	private void generatePreviousPriceGraph() {

		g_graphView = null;

		try {
			if (exchange.equalsIgnoreCase(MTGOX)) {
				Exchange mtGox = ExchangeFactory.INSTANCE
						.createExchange("com.xeiam.xchange.mtgox.v1.MtGoxExchange");
				marketDataService = mtGox.getPollingMarketDataService();
				Trades trades = marketDataService.getTrades(Currencies.BTC,
						Currencies.USD);

				tradesList = trades.getTrades();

			}

			if (exchange.equalsIgnoreCase(VIRTEX)) {
				Exchange virtex = ExchangeFactory.INSTANCE
						.createExchange("com.xeiam.xchange.virtex.VirtExExchange");
				marketDataService = virtex.getPollingMarketDataService();
				Trades trades = marketDataService.getTrades(Currencies.BTC,
						Currencies.CAD);

				tradesList = trades.getTrades();
			}

			List<Float> priceList = new ArrayList<Float>();
			List<Float> dateList = new ArrayList<Float>();
			
			String sOldestDate = "";
			String sNewestDate = "";
			String sMidDate = "";
			
			Format formatter = new SimpleDateFormat("MMM dd @ HH:mm");
			
				for (int i = 0; i < tradesList.size(); i++) {
					Trade trade = (Trade) tradesList.get(i);
					priceList.add(i, trade.getPrice().getAmount().floatValue());
					dateList.add(i, Float.valueOf(trade.getTimestamp().getMillis()));
					
					if(i == tradesList.size() - 1) 
						sNewestDate = formatter.format(trade.getTimestamp().getMillis());
					if(i == 0) 
						sOldestDate = formatter.format(trade.getTimestamp().getMillis());
					if(i == tradesList.size()/2 - 1) 
						sMidDate = formatter.format(trade.getTimestamp().getMillis());
				}

			float[] values = new float[priceList.size()];
			float[] dates = new float[dateList.size()];

			for (int i = 0; i < priceList.size(); i++) {
				values[i] = priceList.get(i);
				dates[i] = dateList.get(i);

			}

			float largest = Integer.MIN_VALUE;
			float smallest = Integer.MAX_VALUE;

			for (int i = 0; i < values.length; i++)
				if (values[i] > largest)
					largest = values[i];


			for (int i = 0; i < values.length; i++)
				if (values[i] < smallest)
					smallest = values[i];

			// min, max, steps, pre string, post string, number of decimal
			// places
			String[] verlabels = GraphViewer.createLabels(smallest, largest,
					10, "$", "", 4);

			String[] horlabels = new String[] { sOldestDate, "", "", sMidDate,
					"", "", sNewestDate };

			g_graphView = new GraphViewer(this, values, currency
					+ "/BTC since " + sOldestDate, // title
					horlabels, // horizontal labels
					verlabels, // vertical labels
					GraphViewer.LINE, // type of graph
					smallest, // min
					largest); // max

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void createPopup(String pMessage) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(pMessage);
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
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.minerstats);
		if (g_graphView != null) {
			setContentView(g_graphView);
		} else {
			viewGraph();
		}
	}

	private void safelyDismiss(ProgressDialog dialog) {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	private void viewGraph() {
		if (graphProgressDialog != null && graphProgressDialog.isShowing()) {
			return;
		}
		graphProgressDialog = ProgressDialog.show(this, "Working...",
				"Retrieving trades", true, true);
		GraphThread gt = new GraphThread();
		gt.start();
	}

}
