package com.veken0m.cavirtex;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.xeiam.xchange.Currencies;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.service.marketdata.polling.PollingMarketDataService;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class Graph extends SherlockActivity {

	private GraphViewer g_graphView = null;
	private ProgressDialog graphProgressDialog;
	final Handler mOrderHandler = new Handler();
	public static final String VIRTEX = "com.veken0m.cavirtex.VIRTEX";
	public static final String MTGOX = "com.veken0m.cavirtex.MTGOX";

	public static final String sVirtex = "VirtEx";
	public static final String sMtgox = "MtGox";
	public static final String sCAD = "CAD";
	public static final String sUSD = "USD";
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
			currency = sUSD;
		}
		if (exchange.equalsIgnoreCase(VIRTEX)) {
			exchangeName = sVirtex;
			currency = sCAD;
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
		// if (item.getItemId() == R.id.scores) {
		// startActivity(new Intent(this, ScoresActivity.class));
		// }
		// if (item.getItemId() == R.id.handicap) {
		// startActivity(new Intent(this, HandicapActivity.class));
		// }
		return super.onOptionsItemSelected(item);
	}

	public class GraphThread extends Thread {

		@Override
		public void run() {
			generatePreviousPriceGraph();
			mOrderHandler.post(mGraphView); // after retrieveOrders is done, do
											// this
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
	 * connects to Virtex, reads the JSON, and plots a GraphView of it
	 */
	private void generatePreviousPriceGraph() {

		g_graphView = null;

		if (exchange.equalsIgnoreCase(MTGOX)) {
			Exchange mtGox = ExchangeFactory.INSTANCE
					.createExchange("com.xeiam.xchange.mtgox.v1.MtGoxExchange");
			marketDataService = mtGox.getPollingMarketDataService();
			Trades trades = marketDataService.getTrades(Currencies.BTC,
					Currencies.USD);

			tradesList = trades.getTrades();

		}

		HttpClient client = new DefaultHttpClient();

		HttpGet post = new HttpGet();

		if (exchange.equalsIgnoreCase(VIRTEX)) {
			post = new HttpGet("https://www.cavirtex.com/api/CAD/trades.json");

		}
		try {

			List<Float> priceList = new ArrayList();
			List<Float> dateList = new ArrayList();

			if (exchange.equalsIgnoreCase(VIRTEX)) {
				HttpResponse response = null;
				response = client.execute(post);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "UTF-8"));
				String text = reader.readLine();
				JSONTokener tokener = new JSONTokener(text);
				JSONArray jArray = new JSONArray();
				jArray = new JSONArray(tokener);

				JSONObject jKey;

				for (int i = 0; i < jArray.length(); i++) {
					jKey = jArray.getJSONObject(i);
					priceList.add(i, Float.valueOf(jKey.getString("price")));
				}

				/**
				 * Lets count the min and max values so we can set our axis
				 */

				for (int i = 0; i < jArray.length(); i++) {
					jKey = jArray.getJSONObject(i);
					dateList.add(i, Float.valueOf(jKey.getString("date")));
				}
			}
			
			String sOldestDate = "";
			String sNewestDate = "";
			String sMidDate = "";

			if (exchange.equalsIgnoreCase(MTGOX)) {

				for (int i = 0; i < tradesList.size(); i++) {
					Trade trade = (Trade) tradesList.get(i);
					priceList.add(i, trade.getPrice().getAmount().floatValue());
					dateList.add(i, Float.valueOf(trade.getTimestamp().getMillis()));// fix it
					
					if(i == tradesList.size() - 1) 
						sNewestDate = trade.getTimestamp().toString("MMM dd @ HH:mm");
					if(i == 0) 
						sOldestDate = trade.getTimestamp().toString("MMM dd @ HH:mm");
					if(i == tradesList.size()/2 - 1) 
						sMidDate = trade.getTimestamp().toString("MMM dd @ HH:mm");
				}
			}

			float[] values = new float[priceList.size()];
			float[] dates = new float[dateList.size()];

			for (int i = 0; i < priceList.size(); i++) {
				values[i] = priceList.get(i);
				dates[i] = dateList.get(i);

			}

			float largest = Integer.MIN_VALUE;
			float smallest = Integer.MAX_VALUE;
			float oldestDate = dates[0];
			float newestDate = dates[dates.length - 1];
			float midDate = dates[dates.length / 2 - 1];

			for (int i = 0; i < values.length; i++)
				if (values[i] > largest)
					largest = values[i];

			Format formatter = new SimpleDateFormat("MMM dd @ HH:mm");

			if (exchange.equalsIgnoreCase(VIRTEX)) {
				sOldestDate = formatter.format(oldestDate * 1000);
				sNewestDate = formatter.format(newestDate * 1000);
				sMidDate = formatter.format(midDate * 1000);
				
			}

			for (int i = 0; i < values.length; i++)
				if (values[i] < smallest)
					smallest = values[i];

			// Add spacing between edge of screen and graph
			double graphPadding = 0.01;
			smallest -= graphPadding;
			largest += graphPadding;

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
			return;
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
