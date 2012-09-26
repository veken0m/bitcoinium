package com.veken0m.cavirtex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
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
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.xeiam.xchange.Currencies;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.service.marketdata.polling.PollingMarketDataService;

public class Orderbook extends SherlockActivity {

	private ProgressDialog orderbookProgressDialog;
	final Handler mOrderHandler = new Handler();
	String[][] bidData = null;
	String[][] askData = null;
	int lengthAskArray = 0;
	int lengthBidArray = 0;
	int length = 0;
	Boolean connectionFail = false;
	String exchange = VIRTEX;
	public static final String VIRTEX = "com.veken0m.cavirtex.VIRTEX";
	public static final String MTGOX = "com.veken0m.cavirtex.MTGOX";
	public static final String sVirtex = "VirtEx";
	public static final String sMtgox = "MtGox";
	public static final String sCAD = "CAD";
	public static final String sUSD = "USD";
	public static String exchangeName = "";
	public static String currency = "";
	public List listAsks;
	public List listBids;
	/**
	 * List of preference variables
	 */
	static int pref_highlightHigh;
	static int pref_highlightLow;
	static Boolean pref_enableHighlight;

	public static String mtGoxOrderbook = "https://mtgox.com/api/0/data/getDepth.php?Currency=USD";
	public static String mtGoxOrderbookAlternative = "http://anyorigin.com/get/?url=https://mtgox.com/api/0/data/getDepth.php?Currency=USD";
	public static String virtExOrderbook = "https://www.cavirtex.com/api/CAD/orderbook.json";
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
			currency = sUSD;
		}
		if (exchange.equalsIgnoreCase(VIRTEX)) {
			exchangeName = sVirtex;
			currency = sCAD;
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
		// if (item.getItemId() == R.id.scores) {
		// startActivity(new Intent(this, ScoresActivity.class));
		// }
		// if (item.getItemId() == R.id.handicap) {
		// startActivity(new Intent(this, HandicapActivity.class));
		// }
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

	public void drawOrderbookUI() {

		// int limiter = 25;
		// if(limiter != 0){
		// length = limiter;
		// }

		TableLayout t1 = (TableLayout) findViewById(R.id.orderlist);
		LimitOrder limitorderBid;
		LimitOrder limitorderAsk;
		String bidPrice = "";
		String bidAmount = "";
		String askPrice = "";
		String askAmount = "";

		for (int i = 0; i < length; i++) {

			int reverse = lengthBidArray - 1 - i; // use this to read array from last
											// value
											// to first value
			TableRow tr1 = new TableRow(this);
			TextView tvAskAmount = new TextView(this);
			TextView tvAskPrice = new TextView(this);
			TextView tvBidPrice = new TextView(this);
			TextView tvBidAmount = new TextView(this);

			tr1.setId(100 + i);
			
			

			NumberFormat numberFormat = DecimalFormat.getInstance();
			numberFormat.setMaximumFractionDigits(5);
			numberFormat.setMinimumFractionDigits(5);
			NumberFormat numberFormat2 = DecimalFormat.getInstance();
			numberFormat2.setMaximumFractionDigits(2);
			numberFormat2.setMinimumFractionDigits(2);
			numberFormat.setGroupingUsed(false);
			numberFormat2.setGroupingUsed(false);

			if (exchange.equalsIgnoreCase(MTGOX)) {
			limitorderBid = (LimitOrder) listBids.get(reverse);
			limitorderAsk = (LimitOrder) listAsks.get(i);
			
			limitorderBid.getLimitPrice().getAmount().floatValue();
			limitorderAsk.getLimitPrice().getAmount().floatValue();

			bidPrice = numberFormat.format(limitorderBid.getLimitPrice()
					.getAmount().floatValue());
			bidAmount = numberFormat2.format(limitorderBid
					.getTradableAmount().floatValue());
			askPrice = numberFormat.format(limitorderAsk.getLimitPrice()
					.getAmount().floatValue());
			askAmount = numberFormat2.format(limitorderAsk
					.getTradableAmount().floatValue());
			} 
			
			if (exchange.equalsIgnoreCase(VIRTEX)) {
				bidPrice = bidData[reverse][0];
				bidAmount = bidData[reverse][1];
				askPrice = askData[i][0];
				askAmount = askData[i][1];
			}

			tvBidAmount.setText("" + bidPrice + "          " + bidAmount);

			tvAskAmount.setText("" + askPrice + "          " + askAmount);

			if (pref_enableHighlight) {
				if ((int) Double.parseDouble(bidAmount) < pref_highlightLow) {
					tvBidAmount.setTextColor(Color.RED);
				}
				if ((int) Double.parseDouble(bidAmount) >= pref_highlightLow) {
					tvBidAmount.setTextColor(Color.YELLOW);
				}
				if ((int) Double.parseDouble(bidAmount) >= pref_highlightHigh) {
					tvBidAmount.setTextColor(Color.GREEN);
				}

				if ((int) Double.parseDouble(askAmount) < pref_highlightLow) {
					tvAskAmount.setTextColor(Color.RED);
				}
				if ((int) Double.parseDouble(askAmount) >= pref_highlightLow) {
					tvAskAmount.setTextColor(Color.YELLOW);
				}
				if ((int) Double.parseDouble(askAmount) >= pref_highlightHigh) {
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
			mOrderHandler.post(mGraphView); // after retrieveOrders is done, do
											// this
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
		} else {
		}
	}

	public void getOrderBook() {
		try {

			if (exchange.equalsIgnoreCase(VIRTEX)) {
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = null;
				response = client.execute(new HttpGet(virtExOrderbook));

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "UTF-8"));
				String text = reader.readLine();
				JSONTokener tokener = new JSONTokener(text);
				JSONObject jOrderbook = new JSONObject(tokener);
				JSONArray jAskArray = new JSONArray();
				JSONArray jBidArray = new JSONArray();
				jAskArray = jOrderbook.getJSONArray("asks");
				jBidArray = jOrderbook.getJSONArray("bids");
				lengthAskArray = jAskArray.length();
				lengthBidArray = jBidArray.length();
				askData = sortJSON(jAskArray, true);
				bidData = sortJSON(jBidArray, true);
			}
			if (exchange.equalsIgnoreCase(MTGOX)) {
				Exchange mtGox = ExchangeFactory.INSTANCE
						.createExchange("com.xeiam.xchange.mtgox.v1.MtGoxExchange");
				marketDataService = mtGox.getPollingMarketDataService();
				OrderBook orderbook = marketDataService
						.getOrderBook(Currencies.BTC, Currencies.USD);
				listAsks = orderbook.getAsks();
				listBids = orderbook.getBids();
				lengthAskArray = listAsks.size();
				lengthBidArray = listBids.size();
			}

			if (lengthAskArray < lengthBidArray) {
				length = lengthAskArray;
			} else {
				length = lengthBidArray;
			}

		} catch (JSONException e) {
			e.printStackTrace();
			connectionFail = true;
		} catch (ClientProtocolException e) {
			connectionFail = true;
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			connectionFail = true;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String[][] sortJSON(JSONArray jsonArray, Boolean sort)
			throws JSONException {

		String[][] data = new String[jsonArray.length()][2];

		NumberFormat numberFormat = DecimalFormat.getInstance();
		numberFormat.setMaximumFractionDigits(5);
		numberFormat.setMinimumFractionDigits(5);
		NumberFormat numberFormat2 = DecimalFormat.getInstance();
		numberFormat2.setMaximumFractionDigits(2);
		numberFormat2.setMinimumFractionDigits(2);
		numberFormat.setGroupingUsed(false);
		numberFormat2.setGroupingUsed(false);

		if (jsonArray != null) {

			for (int i = 0; i < jsonArray.length(); i++) {
				data[i][0] = numberFormat.format(Float.valueOf(jsonArray
						.getJSONArray(i).getString(0)));
				data[i][1] = numberFormat2.format(Float.valueOf(jsonArray
						.getJSONArray(i).getString(1)));

			}
		}

		if (sort) {
			Arrays.sort(data, new Comparator<String[]>() {

				public int compare(String[] entry1, String[] entry2) {
					if ((Double.parseDouble(entry1[0]) > Double
							.parseDouble(entry2[0]))) {
						return 1;
					} else if (Double.parseDouble(entry1[0]) < Double
							.parseDouble(entry2[0])) {
						return -1;
					} else
						return 0;

				}

			});
		}
		return data;

	}

}
