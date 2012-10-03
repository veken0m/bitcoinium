package com.veken0m.cavirtex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewer extends Activity {
	WebView mWebView;
	final CharSequence cTrades = "VirtEx Trades";
	final CharSequence cOrderBook = "VirtEx OrderBook";
	final CharSequence cmtGoxLive = "MtGoxLive";
	final CharSequence cBTCChartsVirtex = "BTCCharts - VirtEx";
	final CharSequence cBTCChartsMtGox = "BTCCharts - MtGox";
	final CharSequence cClose = "Close";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		// displayMenu();

		if (savedInstanceState != null) {
			((WebView) findViewById(R.id.webviewer))
					.restoreState(savedInstanceState);
		} else {
			mWebView = (WebView) findViewById(R.id.webviewer);
		}
		// mWebView.getSettings().setSupportZoom(true);
		// mWebView.getSettings().setBuiltInZoomControls(true);
		// mWebView.loadUrl("https://www.cavirtex.com/orderbook");
	}

	protected void onSaveInstanceState(Bundle outState) {
		mWebView.saveState(outState);
	}

	private void displayMenu() {
		final CharSequence[] items = { cBTCChartsMtGox, cmtGoxLive, cOrderBook, cBTCChartsVirtex, cClose };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select an option");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				mWebView.getSettings().setPluginState(
						WebSettings.PluginState.ON);
				mWebView.getSettings().setJavaScriptEnabled(true);
				mWebView.getSettings().setSupportZoom(true);
				mWebView.getSettings().setBuiltInZoomControls(true);
				// mWebView.loadUrl("https://www.cavirtex.com/orderbook");

				if (items[item] == cOrderBook) {
					dialog.cancel();
					mWebView.loadUrl("https://www.cavirtex.com/orderbook");
				} else if (items[item] == cBTCChartsVirtex) {
					dialog.cancel();
					mWebView.loadUrl("http://btccharts.com/#m=cavirtex-BTC-CAD");
				} else if (items[item] == cBTCChartsMtGox) {
					dialog.cancel();
					mWebView.loadUrl("http://btccharts.com/#m=mtgox-BTC-USD");
				} else if (items[item] == cmtGoxLive) {
					dialog.cancel();
					mWebView.loadUrl("http://mtgoxlive.com/orders");
				}

				else {
					dialog.cancel();

				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		displayMenu();

		return false;
	}

	/**
	 * in onCreateOptionsMenu, we display our own custom menu and return false
	 * as to void the default android menu
	 * 
	 * @param menu
	 *            we ignore this (for now)
	 * @return boolean false to ignore the defualt menu, true to display it
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		displayMenu();

		return false;
	}
}