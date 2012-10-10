package com.veken0m.cavirtex;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;

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
	
	static String pref_deepbitKey = "";
	static String pref_bitminterKey = "";
	static String pref_miningpool =  "";

	public static String APIToken = "";
	public static String miningPool = "";
	private ProgressDialog minerProgressDialog;
	final Handler mMinerHandler = new Handler();
	public static String currentDifficulty = "";
	public static String nextDifficulty = "";
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

		readPreferences(getApplicationContext());
		
		if (APIToken.equalsIgnoreCase("") && miningPool.equalsIgnoreCase("")) {
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

		//HttpClient client = new DefaultHttpClient();
		//HttpGet post;
		//	post = new HttpGet("http://deepbit.net/api/" + APIToken);
		try {
			
			String poolData[] = new String[8];
			
			if(miningPool.equalsIgnoreCase("deepbit")){
			poolData = fetchDeepbitData(APIToken);
			} else {
			poolData = fetchBitMinterData(pref_bitminterKey);
			}
			/* Old Code, Moved to fetchDeepBitData method
			HttpResponse response = client.execute(post);
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
			*/
			
			jRewards = poolData[0];
			jHashrate = poolData[1];
			jIpa = poolData[2];
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
			mMinerHandler.post(mGraphView); // after retrieveOrders is done, do
											// this
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
			tr9.addView(tvCurrentDifficulty);
			tr10.addView(tvNextDifficulty);

			t1.addView(tr8);
			t1.addView(tr9);
			t1.addView(tr10);
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
				pref_miningpool =  pPrefs.getString("favpoolPref", "deepbit");

				APIToken = pref_deepbitKey;

			}
		};

		pref_deepbitKey = prefs.getString("deepbitKey", "");
		pref_bitminterKey = prefs.getString("bitminterKey", "null");
		pref_miningpool =  prefs.getString("favpoolPref", "deepbit");

		APIToken = pref_deepbitKey;
		//APIToken = "4de7e447816197d782000000_5BA98E3B73"; //Deepbit Test
	}
	
	public static String[] fetchDifficulty()
			throws ClientProtocolException, IOException, JSONException {

		String[] difficultyData = new String[2];

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

		return difficultyData;
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
		deepbitData[2] = jMinerStats.getString("ipa");
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
		HttpGet post;
		post = new HttpGet(
				"https://bitminter.com/api/users/Test?key=M3IIJ5OCN2SQKRGRYVIXUFCJGG44DPNJ");

		HttpResponse response = client.execute(post);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent(), "UTF-8"));
		String text = reader.readLine();
		JSONTokener tokener = new JSONTokener(text);
		JSONObject jMinerStats = new JSONObject(tokener);
		reader.close();

		// JSONObject jWorkers = jMinerStats.getJSONObject("workers");
		// JSONArray jWorker1 = jWorkers.names();
		JSONObject jBalances = jMinerStats.getJSONObject("balances");
		JSONArray jWorkers = jMinerStats.getJSONArray("workers");
		// JSONObject jWork = jWorkers.get(0).getObject("work");

		// String Worker1 = "" + jWorker1.get(0);

		bitminterData[0] = jBalances.getString("BTC");
		// bitminterData[0] = jBalances.getString("NMC");
		bitminterData[1] = jMinerStats.getString("hash_rate");
		bitminterData[2] = ""; // jMinerStats.getString("ipa");
		bitminterData[3] = ""; // jMinerStats.getString("payout_history");
		bitminterData[4] = ""; // jWorkers.getString("alive");
								// //jWorkers.getJSONObject(Worker1).getString("alive");
		bitminterData[5] = ""; // jShift.getString("BTC");
		bitminterData[6] = ""; // jWorkers.getJSONObject(Worker1).getString("stales");
		bitminterData[7] = jMinerStats.getString("name");

		return bitminterData;
	}

}
