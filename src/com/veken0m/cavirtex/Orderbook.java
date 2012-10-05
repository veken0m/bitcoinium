package com.veken0m.cavirtex;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.xeiam.xchange.Currencies;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.service.marketdata.polling.PollingMarketDataService;

public class Orderbook extends SherlockActivity {

	private ProgressDialog orderbookProgressDialog;
	final Handler mOrderHandler = new Handler();
	int lengthAskArray = 0;
	int lengthBidArray = 0;
	int length = 0;
	Boolean connectionFail = false;
	String exchange = VIRTEX;
	public static final String VIRTEX = "com.veken0m.cavirtex.VIRTEX";
	public static final String MTGOX = "com.veken0m.cavirtex.MTGOX";
	public static final String sVirtex = "VirtEx";
	public static final String sMtgox = "MtGox";
	public static String exchangeName = "";
	public static String currency = "";
	public String xchangeExchange = null;
	public List<LimitOrder> listAsks;
	public List<LimitOrder> listBids;
	/**
	 * List of preference variables
	 */
	static int pref_highlightHigh;
	static int pref_highlightLow;
	static Boolean pref_enableHighlight;

	private static PollingMarketDataService marketDataService;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderbook);

		ActionBar actionbar = getSupportActionBar();
		actionbar.show();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			exchange = extras.getString("exchange");
		}

		if (exchange.equalsIgnoreCase(MTGOX)) {
			exchangeName = sMtgox;
			xchangeExchange =  "com.xeiam.xchange.mtgox.v1.MtGoxExchange";
			currency = Currencies.USD;
		}
		if (exchange.equalsIgnoreCase(VIRTEX)) {
			exchangeName = sVirtex;
			xchangeExchange =  "com.xeiam.xchange.virtex.VirtExExchange";
			currency = Currencies.CAD;
		}

		readPreferences(getApplicationContext());
		viewOrderbook();
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.orderbook);
		readPreferences(getApplicationContext());
		drawOrderbookUI();
	}
	
	protected static void readPreferences(Context context) {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences pPrefs,
					String key) {

				pref_enableHighlight = pPrefs.getBoolean("highlightPref", true);
				pref_highlightHigh = Integer.parseInt(pPrefs.getString(
						"highlightUpper", "50"));
				pref_highlightLow = Integer.parseInt(pPrefs.getString(
						"highlightLower", "10"));
			}
		};

		prefs.registerOnSharedPreferenceChangeListener(prefListener);

		pref_enableHighlight = prefs.getBoolean("highlightPref", true);
		pref_highlightHigh = Integer.parseInt(prefs.getString("highlightUpper",
				"50"));
		pref_highlightLow = Integer.parseInt(prefs.getString("highlightLower",
				"10"));
	}
	
	/**
	 * Fetch the Orderbook and split into Ask/Bids lists
	 */
	public void getOrderBook() {
		try {
				Exchange exchange = ExchangeFactory.INSTANCE
						.createExchange(xchangeExchange);
				marketDataService = exchange.getPollingMarketDataService();
				OrderBook orderbook = marketDataService
						.getOrderBook(Currencies.BTC, currency);
				listAsks = orderbook.getAsks();
				listBids = orderbook.getBids();
				lengthAskArray = listAsks.size();
				lengthBidArray = listBids.size();
			

			if (lengthAskArray < lengthBidArray) {
				length = lengthAskArray;
			} else {
				length = lengthBidArray;
			}

		} catch (Exception e) {
			connectionFail = true;
		}
	

	}
	
	/**
	 * Draw the Orders to the screen in a table
	 */
	public void drawOrderbookUI() {

		//Limit Orderbook orders drawn to speed up performance
		int limiter = 100;
		 if(limiter != 0 && limiter < length){
		 length = limiter;
		 }

		TableLayout t1 = (TableLayout) findViewById(R.id.orderlist);

		for (int i = 0; i < length; i++) {

			int reverse = lengthBidArray - 1 - i; //To read Bid array backwards
			
			TableRow tr1 = new TableRow(this);
			TextView tvAskAmount = new TextView(this);
			TextView tvAskPrice = new TextView(this);
			TextView tvBidPrice = new TextView(this);
			TextView tvBidAmount = new TextView(this);
			tr1.setId(100 + i);

			LimitOrder limitorderBid = (LimitOrder) listBids.get(reverse);
			LimitOrder limitorderAsk = (LimitOrder) listAsks.get(i);
			
			float bidPrice = limitorderBid.getLimitPrice().getAmount().floatValue();
			float bidAmount = limitorderBid.getTradableAmount().floatValue();
			float askPrice = limitorderAsk.getLimitPrice().getAmount().floatValue();
			float askAmount = limitorderAsk.getTradableAmount().floatValue();

			String sBidPrice = Utils.formatFiveDecimals(bidPrice);
			String sBidAmount = Utils.formatTwoDecimals(bidAmount);
			String sAskPrice = Utils.formatFiveDecimals(askPrice);
			String sAskAmount = Utils.formatTwoDecimals(askAmount);

			tvBidAmount.setText("" + sBidPrice + "          " + sBidAmount);
			tvAskAmount.setText("" + sAskPrice + "          " + sAskAmount);

			if (pref_enableHighlight) {
				if ((int) bidAmount < pref_highlightLow) {
					tvBidAmount.setTextColor(Color.RED);
				}
				if ((int) bidAmount >= pref_highlightLow) {
					tvBidAmount.setTextColor(Color.YELLOW);
				}
				if ((int) bidAmount >= pref_highlightHigh) {
					tvBidAmount.setTextColor(Color.GREEN);
				}

				if ((int) askAmount < pref_highlightLow) {
					tvAskAmount.setTextColor(Color.RED);
				}
				if ((int) askAmount >= pref_highlightLow) {
					tvAskAmount.setTextColor(Color.YELLOW);
				}
				if ((int) askAmount >= pref_highlightHigh) {
					tvAskAmount.setTextColor(Color.GREEN);
				}
			}

			try {
				tr1.addView(tvBidAmount);
				tr1.addView(tvBidPrice);
				tr1.addView(tvAskAmount);
				tr1.addView(tvAskPrice);

				t1.addView(tr1);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void viewOrderbook() {
		if (orderbookProgressDialog != null
				&& orderbookProgressDialog.isShowing()) {
			return;
		}
		orderbookProgressDialog = ProgressDialog.show(this, "Working...",
				"Retrieving Orderbook", true, true);
		OrderbookThread gt = new OrderbookThread();
		gt.start();
	}

	public class OrderbookThread extends Thread {

		@Override
		public void run() {
			getOrderBook();
			mOrderHandler.post(mGraphView);
		}

	}

	final Runnable mGraphView = new Runnable() {
		@Override
		public void run() {
			safelyDismiss(orderbookProgressDialog);
			drawOrderbookUI();
		}

	};

	private void safelyDismiss(ProgressDialog dialog) {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		if (connectionFail) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Could not retrieve orderbook from "
					+ exchangeName
					+ ".\n\nCheck 3G or Wifi connection and try again.");
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});

			AlertDialog alert = builder.create();
			alert.show();
		}
	}
}