package com.veken0m.bitcoinium;

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
import com.xeiam.xchange.bitcoincharts.BitcoinChartsFactory;
import com.xeiam.xchange.bitcoincharts.dto.MarketData;

public class BitcoinChartsActivity extends SherlockActivity {
	MarketData[] marketData;

	protected static ProgressDialog bitcoinchartsProgressDialog;
	final static Handler mOrderHandler = new Handler();
	Boolean connectionFail = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bitcoincharts);

		ActionBar actionbar = getSupportActionBar();
		actionbar.show();

		viewBitcoinCharts();
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
		setContentView(R.layout.bitcoincharts);
		// readPreferences(getApplicationContext());
		drawBitcoinChartsUI();
	}

	protected static void readPreferences(Context context, String prefix,
			String defaultCurrency) {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	/**
	 * Fetch the BitcoinChartsActivity and split into Ask/Bids lists
	 */
	public void getBitcoinCharts() {
		try {

			marketData = BitcoinChartsFactory.createInstance().getMarketData();

		} catch (Exception e) {
			connectionFail = true;
			e.printStackTrace();
		}

	}

	/**
	 * Draw the Orders to the screen in a table
	 */
	public void drawBitcoinChartsUI() {

		final TableLayout t1 = (TableLayout) findViewById(R.id.bitcoincharts_list);

		for (MarketData data : marketData) {
			System.out.println(data.getSymbol() + ": " + data);

			final TableRow tr1 = new TableRow(this);
			final TextView tvBidAmount = new TextView(this);
			// String last = "Last: " + data.getLatestTrade();
			String high = ", High: "
					+ Utils.formatDecimal(data.getHigh(), 2, true);
			String low = ", Low: "
					+ Utils.formatDecimal(data.getLow(), 2, true);
			;
			String vol = ", Vol: "
					+ Utils.formatDecimal(data.getVolume(), 2, true);
			;

			tvBidAmount.setText(data.getSymbol() + ": " + high + low + vol);
			// tvBidAmount.setText(data.getSymbol() + ": " + data);

			// if (pref_enableHighlight) {
			// if ((int) bidAmount < pref_highlightLow) {
			// tvBidAmount.setTextColor(Color.RED);
			// }
			// if ((int) bidAmount >= pref_highlightLow) {
			// tvBidAmount.setTextColor(Color.YELLOW);
			// }
			// if ((int) bidAmount >= pref_highlightHigh) {
			// tvBidAmount.setTextColor(Color.GREEN);
			// }
			// }

			try {
				tr1.addView(tvBidAmount);
				t1.addView(tr1);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void viewBitcoinCharts() {
		if (bitcoinchartsProgressDialog != null
				&& bitcoinchartsProgressDialog.isShowing()) {
			return;
		}
		bitcoinchartsProgressDialog = ProgressDialog.show(this, "Working...",
				"Retrieving data", true, true);
		bitcoinchartsThread gt = new bitcoinchartsThread();
		gt.start();
	}

	public class bitcoinchartsThread extends Thread {

		@Override
		public void run() {
			getBitcoinCharts();
			mOrderHandler.post(mGraphView);
		}
	}

	final Runnable mGraphView = new Runnable() {
		@Override
		public void run() {
			safelyDismiss(bitcoinchartsProgressDialog);
			drawBitcoinChartsUI();
		}
	};

	private void safelyDismiss(ProgressDialog dialog) {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		if (connectionFail) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Could not retrieve data from "
					+ "Bitcoin Charts"
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
