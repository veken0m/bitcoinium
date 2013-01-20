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
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.xeiam.xchange.bitcoincharts.BitcoinChartsFactory;
import com.xeiam.xchange.bitcoincharts.dto.marketdata.BitcoinChartsTicker;

public class BitcoinChartsActivity extends SherlockActivity {
	BitcoinChartsTicker[] marketData;

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
		drawBitcoinChartsUI();
	}

	protected static void readPreferences(Context context, String prefix,
			String defaultCurrency) {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	/**
	 * Fetch the Bitcoin Charts data
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
	 * Draw the Tickers to the screen in a table
	 */
	public void drawBitcoinChartsUI() {

		final TableLayout t1 = (TableLayout) findViewById(R.id.bitcoincharts_list);

		String previousCurrency = "";
		int backGroundColor = Color.rgb(31, 31, 31);

		for (BitcoinChartsTicker data : marketData) {

			final TableRow tr1 = new TableRow(this);

			final TextView tvSymbol = new TextView(this);
			final TextView tvLast = new TextView(this);
			// final TextView tvAvg = new TextView(this);
			final TextView tvVolume = new TextView(this);
			final TextView tvHigh = new TextView(this);
			final TextView tvLow = new TextView(this);
			// final TextView tvBid = new TextView(this);
			// final TextView tvAsk = new TextView(this);
			String last = Utils.formatDecimal(data.getClose(), 2, true);
			String high = Utils.formatDecimal(data.getHigh(), 2, true);
			String low = Utils.formatDecimal(data.getLow(), 2, true);
			String vol = Utils.formatDecimal(data.getVolume(), 2, true);
			// String avg = Utils.formatDecimal(data.getAvg(), 2, true);
			// String bid = Utils.formatDecimal(data.getBid(), 2, true);
			// String ask = Utils.formatDecimal(data.getAsk(), 2, true);

			LayoutParams params = new TableRow.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
			tvSymbol.setText(data.getSymbol());
			tvSymbol.setLayoutParams(params);
			tvLast.setText(last);
			tvLast.setLayoutParams(params);
			tvLast.setGravity(1);
			tvVolume.setText(vol);
			tvVolume.setGravity(1);
			tvVolume.setLayoutParams(params);
			// tvAvg.setText(avg);
			// tvAvg.setGravity(1);
			// tvAvg.setLayoutParams(params);
			tvLow.setText(low);
			tvLow.setGravity(1);
			tvLow.setLayoutParams(params);
			tvHigh.setText(high);
			tvHigh.setLayoutParams(params);
			tvHigh.setGravity(1);
			// tvBid.setText(bid);
			// tvBid.setGravity(1);
			// tvBid.setLayoutParams(params);
			// tvAsk.setText(ask);
			// tvAsk.setGravity(1);
			// tvAsk.setLayoutParams(params);

			// If currencies are different
			if (!previousCurrency.equalsIgnoreCase(data.getCurrency())) {
				// Change the background color
				if (backGroundColor == Color.BLACK) {
					backGroundColor = Color.rgb(31, 31, 31);
				} else {
					backGroundColor = Color.BLACK;
				}
			}

			tr1.setBackgroundColor(backGroundColor);

			tr1.addView(tvSymbol);
			tr1.addView(tvLast);
			// tr1.addView(tvAvg);
			tr1.addView(tvVolume);
			tr1.addView(tvLow);
			tr1.addView(tvHigh);
			// tr1.addView(tvBid);
			// tr1.addView(tvAsk);
			tr1.setPadding(0, 3, 0, 3);
			t1.addView(tr1);

			// Insert a divider between rows
			View divider = new View(this);
			divider.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT, 1));
			divider.setBackgroundColor(Color.rgb(51, 51, 51));
			t1.addView(divider);

			previousCurrency = data.getCurrency();
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
