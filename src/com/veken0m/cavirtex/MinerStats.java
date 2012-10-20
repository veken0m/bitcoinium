package com.veken0m.cavirtex;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MinerStats extends SherlockActivity {
	
	protected static String pref_deepbitKey = "";
	protected static String pref_bitminterKey = "";
	protected static String pref_bitminterUser = "";
	protected static String pref_miningpool =  "";
	protected static String currentDifficulty = "";
	protected static String nextDifficulty = "";
	protected static String jRewardsNMC = "";
	protected static String jRewardsBTC = "";
	protected static String jHashrate = "";
	protected static String jPayout = "";
	protected static String Alive = "";
	protected static String Shares = "";
	protected static String Stales = "";
	protected static String Worker1 = "";
	
	final protected static String notAvailable = "N/A";
	
	public static String APIToken = "";
	private ProgressDialog minerProgressDialog;
	final Handler mMinerHandler = new Handler();
	protected Boolean connectionFail = false;
	

	public void onCreate(Bundle savedInstanceState) {

		readPreferences(getApplicationContext());
		
		if (APIToken.equalsIgnoreCase("") && pref_miningpool.equalsIgnoreCase("")) {
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
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.minerstats);
		drawMinerUI();
	}

	public void getMinerStats(Context context) {

		try {
			
			String poolData[] = new String[8];
			
			if(pref_miningpool.equalsIgnoreCase("deepbit")){
			poolData = fetchDeepbitData(APIToken);
			} else {
			poolData = fetchBitMinterData(pref_bitminterKey);
			}
			
			jRewardsBTC = poolData[0];
			jHashrate = poolData[1];
			jRewardsNMC = poolData[2];
			jPayout = poolData[3];
			Alive = poolData[4];
			Shares = poolData[5];
			Stales = poolData[6];	
			Worker1 =  poolData[7];
			
		} catch (Exception e) {
			Log.e("Orderbook error", "exception", e);
			connectionFail = true;
		}

		try {
		
			fetchDifficulty();

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
			mMinerHandler.post(mGraphView); 
		}
	}

	final Runnable mGraphView = new Runnable() {
		@Override
		public void run() {
			safelyDismiss(minerProgressDialog);
			drawMinerUI();
		}
	};

	private void safelyDismiss(ProgressDialog dialog) {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		if (connectionFail) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Could not retrieve data from " + pref_miningpool + "\n\nPlease make sure that your API Token and/or Username is entered correctly and that 3G or Wifi is working properly.");
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

			// Initialization of rows for the table layout
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
			TableRow tr11 = new TableRow(this);
			TableRow tr12 = new TableRow(this);
			TextView tvExchangeName = new TextView(this);
			TextView tvBTCRewards = new TextView(this);
			TextView tvNMCRewards = new TextView(this);
			TextView tvHashrate = new TextView(this);
			TextView tvMinerName = new TextView(this);
			TextView tvAlive = new TextView(this);
			TextView tvBTCPayout = new TextView(this);
			TextView tvShares = new TextView(this);
			TextView tvStales = new TextView(this);

			tr1.setGravity(Gravity.CENTER_HORIZONTAL);
			tr2.setGravity(Gravity.CENTER_HORIZONTAL);
			tr3.setGravity(Gravity.CENTER_HORIZONTAL);
			tr4.setGravity(Gravity.CENTER_HORIZONTAL);
			tr5.setGravity(Gravity.CENTER_HORIZONTAL);
			tr6.setGravity(Gravity.CENTER_HORIZONTAL);
			tr7.setGravity(Gravity.CENTER_HORIZONTAL);
			tr8.setGravity(Gravity.CENTER_HORIZONTAL);
			tr9.setGravity(Gravity.CENTER_HORIZONTAL);
			tr10.setGravity(Gravity.CENTER_HORIZONTAL);
			tr11.setGravity(Gravity.CENTER_HORIZONTAL);
			tr12.setGravity(Gravity.CENTER_HORIZONTAL);
			
			tvExchangeName.setText("Exchange: " + pref_miningpool);
			tvMinerName.setText("Miner: " + Worker1);
			tvHashrate.setText("Hashrate: " + jHashrate + " MH/s");
			tvBTCRewards.setText("Reward: " + jRewardsBTC + " BTC");
			tvNMCRewards.setText("Reward: " + jRewardsNMC + " NMC");
			tvBTCPayout.setText("Total Payout: " + jPayout + " BTC");
			tvAlive.setText("Alive: " + Alive);

			if (Alive.equalsIgnoreCase("true")) {
				tvMinerName.setTextColor(Color.GREEN);
			} else {
				tvMinerName.setTextColor(Color.RED);
			}

			tvShares.setText("Shares: " + Utils.formatNoDecimals(Float.valueOf(Shares)));
			tvStales.setText("Stales: " + Utils.formatNoDecimals(Float.valueOf(Stales)));

			tr1.addView(tvExchangeName);
			tr2.addView(tvMinerName);
			tr3.addView(tvHashrate);
			tr4.addView(tvBTCRewards);
			tr5.addView(tvNMCRewards);
			tr6.addView(tvBTCPayout);
			tr7.addView(tvAlive);
			tr8.addView(tvShares);
			tr9.addView(tvStales);
			

			t1.addView(tr1);
			t1.addView(tr2);
			t1.addView(tr3);
			t1.addView(tr4);
			t1.addView(tr5);
			t1.addView(tr6);
			t1.addView(tr7);
			t1.addView(tr8);
			t1.addView(tr9);

			TextView tvCurrentDifficulty = new TextView(this);
			TextView tvNextDifficulty = new TextView(this);

			tvCurrentDifficulty.setText("\nCurrent Difficulty: "
					+ Utils.formatNoDecimals(Float.valueOf(currentDifficulty)));
			tvNextDifficulty.setText("Estimated Next Difficulty: "
					+ Utils.formatNoDecimals(Float.valueOf(nextDifficulty)));

			if (Float.valueOf(currentDifficulty) > Float
					.valueOf(nextDifficulty)) {
				tvNextDifficulty.setTextColor(Color.GREEN);
			} else {
				tvNextDifficulty.setTextColor(Color.RED);
			}
			tr11.addView(tvCurrentDifficulty);
			tr12.addView(tvNextDifficulty);

			t1.addView(tr10);
			t1.addView(tr11);
			t1.addView(tr12);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	
	protected static void readPreferences(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences pPrefs,
					String key) {
				pref_deepbitKey = pPrefs.getString("deepbitKey", "null");
				pref_bitminterKey = pPrefs.getString("bitminterKey", "null");
				pref_bitminterUser = pPrefs.getString("bitminterUser", "null");
				pref_miningpool =  pPrefs.getString("favpoolPref", "deepbit");

				APIToken = pref_deepbitKey;

			}
		};

		pref_deepbitKey = prefs.getString("deepbitKey", "");
		pref_bitminterKey = prefs.getString("bitminterKey", "null");
		pref_bitminterUser = prefs.getString("bitminterUser", "null");
		pref_miningpool =  prefs.getString("favpoolPref", "deepbit");

		APIToken = pref_deepbitKey;
	}
	
	public static void fetchDifficulty()
			throws ClientProtocolException, IOException, JSONException {

		//String[] difficultyData = new String[2];

		HttpClient client = new DefaultHttpClient();
		HttpGet post = new HttpGet("http://blockexplorer.com/q/getdifficulty");
		HttpResponse response = client.execute(post);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent(), "UTF-8"));
		//difficultyData[0] = reader.readLine();
		currentDifficulty = reader.readLine();
		
		reader.close();

		post = new HttpGet("http://blockexplorer.com/q/estimate");
		response = client.execute(post);
		reader = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent(), "UTF-8"));
		//difficultyData[1] = reader.readLine();
		nextDifficulty = reader.readLine();
		
		reader.close();
	}
	
	public static String[] fetchDeepbitData(String APIToken)
			throws ClientProtocolException, IOException, JSONException {

		String[] deepbitData = new String[8];

		HttpClient client = new DefaultHttpClient();
		HttpGet post;
		post = new HttpGet("http://deepbit.net/api/" + APIToken);

		HttpResponse response = client.execute(post);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent(), "UTF-8"));
		String text = reader.readLine();
		JSONTokener tokener = new JSONTokener(text);
		JSONObject jMinerStats = new JSONObject(tokener);
		reader.close();

		JSONObject jWorkers = jMinerStats.getJSONObject("workers");
		JSONArray jWorker1 = jWorkers.names();

		String Worker1 = "" + jWorker1.get(0);

		deepbitData[0] = jMinerStats.getString("confirmed_reward");
		deepbitData[1] = jMinerStats.getString("hashrate");
		deepbitData[2] = notAvailable;
		deepbitData[3] = jMinerStats.getString("payout_history");
		deepbitData[4] = jWorkers.getJSONObject(Worker1).getString("alive");
		deepbitData[5] = jWorkers.getJSONObject(Worker1).getString("shares");
		deepbitData[6] = jWorkers.getJSONObject(Worker1).getString("stales");
		deepbitData[7] = Worker1;

		return deepbitData;
	}

	public static String[] fetchBitMinterData(String APIToken)
			throws ClientProtocolException, IOException, JSONException {

		String[] bitminterData = new String[8];

		HttpClient client = new DefaultHttpClient();
		
		//pref_bitminterUser = "Test";
		//pref_bitminterKey = "M3IIJ5OCN2SQKRGRYVIXUFCJGG44DPNJ";
		
		HttpGet post;
		post = new HttpGet(
				"https://bitminter.com/api/users/" + pref_bitminterUser + "?key=" + pref_bitminterKey);


		HttpResponse response = client.execute(post);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent(), "UTF-8"));
		String text = reader.readLine();
		JSONTokener tokener = new JSONTokener(text);
		JSONObject jMinerStats = new JSONObject(tokener);
		reader.close();

		JSONObject jBalances = jMinerStats.getJSONObject("balances");
		JSONArray jWorkers = jMinerStats.getJSONArray("workers");
		JSONObject jWork = jWorkers.getJSONObject(0).getJSONObject("work").getJSONObject("BTC");


		bitminterData[0] = jBalances.getString("BTC");
		bitminterData[1] = jMinerStats.getString("hash_rate");
		bitminterData[2] = jBalances.getString("NMC");
		bitminterData[3] = notAvailable; // jMinerStats.getString("payout_history");
		bitminterData[4] = jWorkers.getJSONObject(0).getString("alive");
		bitminterData[5] = jWork.getString("total_accepted");
		bitminterData[6] = jWork.getString("total_rejected");
		bitminterData[7] = jMinerStats.getString("name");

		return bitminterData;
	}

}
