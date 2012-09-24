package com.veken0m.cavirtex;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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
import com.veken0m.cavirtex.R;

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
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.*;

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
	LineGraphView graphView = null;

	public String exchange = VIRTEX;
	static Boolean pref_graphMode;
	static Boolean pref_scaleMode;
	static int pref_mtgoxWindowSize;
	static int pref_virtexWindowSize;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph);

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
			if (graphView != null) {

				// setContentView(g_graphView);
				setContentView(graphView);

				// LinearLayout layout = (LinearLayout)
				// findViewById(R.id.graphlayout);
				// layout.addView(graphView);

			} else if (g_graphView != null) {
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
		graphView = null;
		HttpClient client = new DefaultHttpClient();

		HttpGet post = new HttpGet();

		if (exchange.equalsIgnoreCase(MTGOX)) {
			post = new HttpGet("https://mtgox.com/api/1/BTCUSD/trades");
		}

		if (exchange.equalsIgnoreCase(VIRTEX)) {
			post = new HttpGet("https://www.cavirtex.com/api/CAD/trades.json");

		}
		try {
			HttpResponse response = null;

			if (exchange.equalsIgnoreCase(MTGOX)) {
				try {
					response = client.execute(post);
				} catch (Exception e) {
					post = new HttpGet(
							"http://anyorigin.com/get/?url=https://mtgox.com/api/1/BTCUSD/trades");
					response = client.execute(post);
				}

			}

			if (exchange.equalsIgnoreCase(VIRTEX)) {
				response = client.execute(post);
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			String text = reader.readLine();
			JSONTokener tokener = new JSONTokener(text);
			JSONArray jArray = new JSONArray();

			if (exchange.equalsIgnoreCase(MTGOX)) {
				JSONObject jJSON = new JSONObject(tokener);
				try {
					jArray = jJSON.getJSONArray("return");
				} catch (Exception e) {
					JSONObject jcontents = jJSON.getJSONObject("contents");
					jArray = jcontents.getJSONArray("return");
				}

			}

			if (exchange.equalsIgnoreCase(VIRTEX)) {
				jArray = new JSONArray(tokener);
			}

			JSONObject jKey;

			if (jArray.length() <= 0)
				return;

			float[] values = new float[jArray.length()];
			float[] dates = new float[jArray.length()];

			NumberFormat numberFormat2 = DecimalFormat.getInstance();
			numberFormat2.setMaximumFractionDigits(2);
			numberFormat2.setMinimumFractionDigits(2);

			for (int i = 0; i < jArray.length(); i++) {
				jKey = jArray.getJSONObject(i);
				values[i] = Float.valueOf(jKey.getString("price"));
			}

			/**
			 * Lets count the min and max values so we can set our axis
			 */

			for (int i = 0; i < jArray.length(); i++) {
				jKey = jArray.getJSONObject(i);
				dates[i] = Float.valueOf(jKey.getString("date"));
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

			String sOldestDate = formatter.format(oldestDate * 1000);
			String sNewestDate = formatter.format(newestDate * 1000);
			String sMidDate = formatter.format(midDate * 1000);

			// String sOldestDate =
			// DateFormat.getDateInstance(DateFormat.MEDIUM,
			// Locale.US).format(oldestDate*1000);
			// String sNewestDate =
			// DateFormat.getDateInstance(DateFormat.MEDIUM,
			// Locale.US).format(newestDate*1000);
			// String sMidDate = DateFormat.getDateInstance(DateFormat.MEDIUM,
			// Locale.US).format(midDate*1000);

			for (int i = 0; i < values.length; i++)
				if (values[i] < smallest)
					smallest = values[i];

			GraphViewData[] data = new GraphViewData[values.length];
			for (int i = 0; i < values.length; i++) {
				data[i] = new GraphViewData(dates[i], values[i]);
			}

			// Add spacing between edge of screen and graph
			double graphPadding = 0.00;
			smallest -= graphPadding;
			largest += graphPadding;

			if (pref_graphMode) {
				String[] verlabels = GraphViewer.createLabels(smallest,
						largest, 10, "$", "", 4);

				String[] horlabels = new String[] { sOldestDate, "", "",
						sMidDate, "", "", sNewestDate };

				g_graphView = new GraphViewer(this, values, currency
						+ "/BTC since " + sOldestDate, // title
						horlabels, // horizontal labels
						verlabels, // vertical labels
						GraphViewer.LINE, // type of graph
						smallest, // min
						largest); // max
			} else {
				graphView = new LineGraphView(this, exchangeName + ": "
						+ currency + "/BTC") {
					@Override
					protected String formatLabel(double value, boolean isValueX) {
						if (isValueX) {
							Format formatter = new SimpleDateFormat(
									"MMM dd @ HH:mm");
							// convert unix time to human time
							return formatter.format(value * 1000);
						} else
							return super.formatLabel(value, isValueX); // let
																		// the
																		// y-value
																		// be
																		// normal-formatted
					}
				};

				int windowSize;
				if (exchangeName.equalsIgnoreCase("mtgox")) {
					windowSize = pref_mtgoxWindowSize * 3600;
				} else {
					windowSize = pref_virtexWindowSize * 3600;
				}
				int startValue = (int) (dates[dates.length - 1] - windowSize);

				graphView.addSeries(new GraphViewSeries(data));
				graphView.setViewPort(startValue, windowSize);
				if (!pref_scaleMode) {
					graphView.setManualYAxisBounds(largest, smallest);
				}
				graphView.setScrollable(true);
				graphView.setScalable(true);
				//graphView.setTextColor(Color.BLACK);
				graphView.setTextSize(17);
			}
			// }

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
		if (graphView != null) {
			setContentView(graphView);
		} else if (g_graphView != null) {
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

	protected static void readPreferences(Context context) {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences pPrefs,
					String key) {

				pref_graphMode = pPrefs.getBoolean("graphmodePref", false);
				pref_scaleMode = pPrefs.getBoolean("graphscalePref", false);
				pref_mtgoxWindowSize = Integer.parseInt(pPrefs.getString(
						"mtgoxWindowSize", "4"));
				pref_virtexWindowSize = Integer.parseInt(pPrefs.getString(
						"virtexWindowSize", "36"));
			}
		};

		prefs.registerOnSharedPreferenceChangeListener(prefListener);

		pref_graphMode = prefs.getBoolean("graphmodePref", false);
		pref_scaleMode = prefs.getBoolean("graphscalePref", false);
		pref_mtgoxWindowSize = Integer.parseInt(prefs.getString(
				"mtgoxWindowSize", "4"));
		pref_virtexWindowSize = Integer.parseInt(prefs.getString(
				"virtexWindowSize", "36"));
	}

}
