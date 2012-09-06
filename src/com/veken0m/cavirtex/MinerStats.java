package com.veken0m.cavirtex;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MinerStats extends SherlockActivity {

	public String APIToken = "";
	private ProgressDialog minerProgressDialog;
	final Handler mMinerHandler = new Handler();
	public String currentDifficulty = "";
	public String nextDifficulty = "";
	public String jRewards = "";
	public String jHashrate = "";
	public String jIpa = "";
	public String jPayout = "";
	public String Alive = "";
	public String Shares = "";
	public String Stales = "";
	public String Worker1 = "";
	Boolean connectionFail = false;

	public void onCreate(Bundle savedInstanceState) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences pPrefs,
					String key) {
				String pref_deepbitKey = pPrefs.getString("deepbitKey", "null");

				APIToken = pref_deepbitKey;

			}
		};

		String pref_deepbitKey = prefs.getString("deepbitKey", "");

		APIToken = pref_deepbitKey;

		if (APIToken.equalsIgnoreCase("")) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.minerstats);
			
			
			int duration = Toast.LENGTH_LONG;
			CharSequence text = "Please enter your Deepbit API Token to use MinerStats";

			Toast toast = Toast.makeText(getApplicationContext(), text,
					duration);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();

			Intent settingsActivity = new Intent(getBaseContext(),
					Preferences.class);
			startActivity(settingsActivity);
		} else {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.minerstats);
			
			ActionBar actionbar = getSupportActionBar();
			actionbar.show();
			
			viewMinerStats();
		}

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
		setContentView(R.layout.minerstats);
		drawMinerUI();
	}

	public void getMinerStats(Context context) {

		// StrictMode.ThreadPolicy policy = new
		// StrictMode.ThreadPolicy.Builder().permitAll().build();

		// StrictMode.setThreadPolicy(policy);

		HttpClient client = new DefaultHttpClient();
		HttpGet post = new HttpGet("http://deepbit.net/api/" + APIToken);
		try {
			HttpResponse response = client.execute(post); // some response
															// object
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			String text = reader.readLine();
			JSONTokener tokener = new JSONTokener(text);
			JSONObject jMinerStats = new JSONObject(tokener);
			reader.close();

			jRewards = jMinerStats.getString("confirmed_reward");
			jHashrate = jMinerStats.getString("hashrate");
			jIpa = jMinerStats.getString("ipa");
			jPayout = jMinerStats.getString("payout_history");

			JSONObject jWorkers = jMinerStats.getJSONObject("workers");
			JSONArray jWorker1 = jWorkers.names();

			Worker1 = "" + jWorker1.get(0);
			Alive = jWorkers.getJSONObject(Worker1).getString("alive");
			Shares = jWorkers.getJSONObject(Worker1).getString("shares");
			Stales = jWorkers.getJSONObject(Worker1).getString("stales");
		} catch (Exception e) {
			Log.e("Orderbook error", "exception", e);
			connectionFail = true;
		}

		// HttpGet post2 = new
		// HttpGet("http://blockexplorer.com/q/getdifficulty");
		// HttpGet post3 = new HttpGet("http://blockexplorer.com/q/estimate");
		try {
			post = new HttpGet("http://blockexplorer.com/q/getdifficulty");
			HttpResponse response = client.execute(post);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			currentDifficulty = reader.readLine();
			reader.close();

			post = new HttpGet("http://blockexplorer.com/q/estimate");
			response = client.execute(post);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent(), "UTF-8"));
			nextDifficulty = reader.readLine();
			reader.close();

			// HttpResponse response2 = client.execute(post2); // some response
			// object
			// HttpResponse response3 = client.execute(post2); // some response
			// object
			// BufferedReader reader2 = new BufferedReader(new
			// InputStreamReader(response2.getEntity().getContent(), "UTF-8"));
			// BufferedReader reader3 = new BufferedReader(new
			// InputStreamReader(response3.getEntity().getContent(), "UTF-8"));
			// currentDifficulty = reader2.readLine();
			// nextDifficulty = reader3.readLine();

		} catch (Exception e) {
			Log.e("Orderbook error", "exception", e);
			// connectionFail = true;
		}

	}

	private void viewMinerStats() {
		if (minerProgressDialog != null && minerProgressDialog.isShowing()) {
			return;
		}
		minerProgressDialog = ProgressDialog.show(this, "Working...",
				"Retrieving Miner Stats", true, true);

		OrderbookThread gt = new OrderbookThread();
		gt.start();
	}

	public class OrderbookThread extends Thread {

		@Override
		public void run() {
			getMinerStats(getApplicationContext());
			mMinerHandler.post(mGraphView); // after retrieveOrders is done, do
											// this
		}

	}

	final Runnable mGraphView = new Runnable() {
		@Override
		public void run() {
			safelyDismiss(minerProgressDialog);
			// if(g_graphView != null) {

			// setMyView(g_graphView);
			// setContentView(R.layout.orderbook);
			// } else {
			drawMinerUI();
			// setContentView(R.layout.orderbook);
			// createPopup("Unable to retrieve transactions, check your 3G or WiFi connection");
			// }
		}
	};

	private void safelyDismiss(ProgressDialog dialog) {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		if (connectionFail) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Could not retrieve data from Deepbit.\n\nPlease make sure that your DeepBit API Token is entered correctly and that 3G or Wifi is working properly.");
			builder.setPositiveButton("Ok",
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

	public void drawMinerUI() {

		try {

			TableLayout t1 = (TableLayout) findViewById(R.id.minerStatlist);
			TableRow tr1 = new TableRow(this);
			TableRow tr2 = new TableRow(this);
			TableRow tr3 = new TableRow(this);
			TableRow tr4 = new TableRow(this);
			TableRow tr5 = new TableRow(this);
			TableRow tr6 = new TableRow(this);
			TableRow tr7 = new TableRow(this);
			TableRow tr8 = new TableRow(this);
			TableRow tr9 = new TableRow(this);
			TableRow tr10 = new TableRow(this);
			TextView tvRewards = new TextView(this);
			TextView tvHashrate = new TextView(this);
			TextView tvMinerName = new TextView(this);
			TextView tvAlive = new TextView(this);
			TextView tvPayout = new TextView(this);
			TextView tvShares = new TextView(this);
			TextView tvStales = new TextView(this);

			tr1.setGravity(Gravity.CENTER_HORIZONTAL);
			tr2.setGravity(Gravity.CENTER_HORIZONTAL);
			tr3.setGravity(Gravity.CENTER_HORIZONTAL);
			tr4.setGravity(Gravity.CENTER_HORIZONTAL);
			tr5.setGravity(Gravity.CENTER_HORIZONTAL);
			tr6.setGravity(Gravity.CENTER_HORIZONTAL);
			tr7.setGravity(Gravity.CENTER_HORIZONTAL);

			NumberFormat numberFormat = DecimalFormat.getInstance();
			numberFormat.setMaximumFractionDigits(5);
			numberFormat.setMinimumFractionDigits(5);
			NumberFormat numberFormat2 = DecimalFormat.getInstance();
			numberFormat2.setMaximumFractionDigits(2);
			numberFormat2.setMinimumFractionDigits(2);

			tvMinerName.setText("Miner: " + Worker1);
			tvHashrate.setText("Hashrate: " + jHashrate + " MH/s");
			tvRewards.setText("Reward: " + jRewards + " BTC");
			tvPayout.setText("Total Payout: " + jPayout + " BTC");
			tvAlive.setText("Alive: " + Alive);

			if (Alive.equalsIgnoreCase("true")) {
				tvMinerName.setTextColor(Color.GREEN);
			} else {
				tvMinerName.setTextColor(Color.RED);
			}

			tvShares.setText("Shares: " + Shares);
			tvStales.setText("Stales: " + Stales);

			tr7.addView(tvHashrate);
			tr2.addView(tvRewards);
			tr3.addView(tvPayout);
			tr1.addView(tvMinerName);
			tr4.addView(tvAlive);
			tr5.addView(tvShares);
			tr6.addView(tvStales);

			t1.addView(tr1);
			t1.addView(tr7);
			t1.addView(tr2);
			t1.addView(tr3);
			t1.addView(tr4);
			t1.addView(tr5);
			t1.addView(tr6);

			TextView tvCurrentDifficulty = new TextView(this);
			TextView tvNextDifficulty = new TextView(this);

			tr9.setGravity(Gravity.CENTER_HORIZONTAL);
			tr10.setGravity(Gravity.CENTER_HORIZONTAL);

			NumberFormat numberFormat0 = DecimalFormat.getInstance();
			numberFormat0.setMaximumFractionDigits(0);
			numberFormat0.setMinimumFractionDigits(0);

			tvCurrentDifficulty.setText("\nCurrent Difficulty: "
					+ numberFormat0.format(Float.valueOf(currentDifficulty)));
			tvNextDifficulty.setText("Estimated Next Difficulty: "
					+ numberFormat0.format(Float.valueOf(nextDifficulty)));

			if (Float.valueOf(currentDifficulty) > Float
					.valueOf(nextDifficulty)) {
				tvNextDifficulty.setTextColor(Color.GREEN);
			} else {
				tvNextDifficulty.setTextColor(Color.RED);
			}
			tr9.addView(tvCurrentDifficulty);
			tr10.addView(tvNextDifficulty);

			t1.addView(tr8);
			t1.addView(tr9);
			t1.addView(tr10);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}
