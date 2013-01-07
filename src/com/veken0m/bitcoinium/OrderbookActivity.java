package com.veken0m.bitcoinium;

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

import com.veken0m.bitcoinium.R;
import com.xeiam.xchange.Currencies;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.trade.LimitOrder;

public class OrderbookActivity extends SherlockActivity {

	protected static ProgressDialog orderbookProgressDialog;
	final static Handler mOrderHandler = new Handler();
	int lengthAskArray = 0;
	int lengthBidArray = 0;
	int length = 0;
	Boolean connectionFail = false;
	protected static String exchangeName = "";
	protected String xchangeExchange = null;
	protected List<LimitOrder> listAsks;
	protected List<LimitOrder> listBids;
	/**
	 * List of preference variables
	 */
	static int pref_highlightHigh;
	static int pref_highlightLow;
	static Boolean pref_enableHighlight;
	static String pref_currency;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderbook);

		ActionBar actionbar = getSupportActionBar();
		actionbar.show();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			exchangeName = extras.getString("exchange");
		}
		
		Exchange exchange = new Exchange(getResources().getStringArray(
				getResources().getIdentifier(exchangeName, "array",
						this.getPackageName())));

		exchangeName = exchange.getExchangeName();
		xchangeExchange = exchange.getClassName();
		String defaultCurrency = exchange.getMainCurrency();
		String prefix = exchange.getPrefix();
		
		readPreferences(getApplicationContext(), prefix, defaultCurrency);

		viewOrderbook();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.preferences) {
			startActivity(new Intent(this, PreferencesActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.orderbook);
		//readPreferences(getApplicationContext());
		drawOrderbookUI();
	}

	protected static void readPreferences(Context context, String prefix, String defaultCurrency) {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		pref_enableHighlight = prefs.getBoolean("highlightPref", true);
		pref_highlightHigh = Integer.parseInt(prefs.getString("highlightUpper",
				"50"));
		pref_highlightLow = Integer.parseInt(prefs.getString("highlightLower",
				"10"));
		pref_currency = prefs.getString(prefix + "CurrencyPref", defaultCurrency);
	}

	/**
	 * Fetch the OrderbookActivity and split into Ask/Bids lists
	 */
	public void getOrderBook() {
		try {
			final OrderBook orderbook = ExchangeFactory.INSTANCE
					.createExchange(xchangeExchange)
					.getPollingMarketDataService()
					.getFullOrderBook(Currencies.BTC, pref_currency);		

			listAsks = orderbook.getSortedAsks();
			listBids = orderbook.getSortedBids();
			lengthAskArray = listAsks.size();
			lengthBidArray = listBids.size();

			if (lengthAskArray < lengthBidArray) {
				length = lengthAskArray;
			} else {
				length = lengthBidArray;
			}

		} catch (Exception e) {
			connectionFail = true;
			e.printStackTrace();
		}

	}

	/**
	 * Draw the Orders to the screen in a table
	 */
	public void drawOrderbookUI() {

		// Limit OrderbookActivity orders drawn to speed up performance
		int limiter = 100;
		if (limiter != 0 && limiter < length) {
			length = limiter;
		}

		final TableLayout t1 = (TableLayout) findViewById(R.id.orderlist);

		for (int i = 0; i < length; i++) {
			
			final TableRow tr1 = new TableRow(this);
			final TextView tvAskAmount = new TextView(this);
			final TextView tvAskPrice = new TextView(this);
			final TextView tvBidPrice = new TextView(this);
			final TextView tvBidAmount = new TextView(this);
			tr1.setId(100 + i);

			final LimitOrder limitorderBid = listBids.get(i);
			final LimitOrder limitorderAsk = listAsks.get(i);

			float bidPrice = limitorderBid.getLimitPrice().getAmount()
					.floatValue();
			float bidAmount = limitorderBid.getTradableAmount().floatValue();
			float askPrice = limitorderAsk.getLimitPrice().getAmount()
					.floatValue();
			float askAmount = limitorderAsk.getTradableAmount().floatValue();

			final String sBidPrice = Utils.formatDecimal(bidPrice, 5, false);
			final String sBidAmount = Utils.formatDecimal(bidAmount, 2, false);
			final String sAskPrice = Utils.formatDecimal(askPrice, 5, false);
			final String sAskAmount = Utils.formatDecimal(askAmount, 2, false);

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