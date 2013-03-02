package com.veken0m.bitcoinium;

import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.veken0m.miningpools.fiftybtc.FiftyBTC;

public class FiftyBTCFragment extends SherlockFragment {

	protected static String pref_50BTCKey = "";
	protected static FiftyBTC data;
	protected Boolean connectionFail = false;
	private ProgressDialog minerProgressDialog;
	final Handler mMinerHandler = new Handler();

	public FiftyBTCFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		readPreferences(getActivity());

		if (pref_50BTCKey.equalsIgnoreCase("")) {

			int duration = Toast.LENGTH_LONG;
			CharSequence text = "Please enter your 50BTC API Token to use MinerStatsActivity with 50BTC";

			Toast toast = Toast.makeText(getActivity(), text, duration);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();

			Intent settingsActivity = new Intent(
					getActivity().getBaseContext(), PreferencesActivity.class);
			startActivity(settingsActivity);
		}

		View view = inflater.inflate(R.layout.table_fragment, container, false);
		viewMinerStats(view);
		return view;
	}

	public void getMinerStats(Context context) {

		try {
			HttpClient client = new DefaultHttpClient();

			HttpGet post = new HttpGet("https://50btc.com/en/api/" + pref_50BTCKey + "?text=1");
			HttpResponse response = client.execute(post);
			ObjectMapper mapper = new ObjectMapper();
			data = mapper.readValue(new InputStreamReader(response
					.getEntity().getContent(), "UTF-8"), FiftyBTC.class);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			connectionFail = true;
		}
	}

	private void viewMinerStats(View view) {
		if (minerProgressDialog != null && minerProgressDialog.isShowing()) {
			return;
		}
		minerProgressDialog = ProgressDialog.show(view.getContext(),
				"Working...", "Retrieving Miner Stats", true, true);

		MinerStatsThread gt = new MinerStatsThread();
		gt.start();
	}

	public class MinerStatsThread extends Thread {

		@Override
		public void run() {
			getMinerStats(getActivity());
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
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Could not retrieve data from "
					+ "50BTC"
					+ "\n\nPlease make sure that your API Token is entered correctly and that 3G or Wifi is working properly.");
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

			TableLayout t1 = (TableLayout) getView().findViewById(
					R.id.minerStatlist);

			TableRow tr1 = new TableRow(getActivity());
			TableRow tr2 = new TableRow(getActivity());
			TableRow tr3 = new TableRow(getActivity());
			TableRow tr4 = new TableRow(getActivity());
			TableRow tr5 = new TableRow(getActivity());
			TableRow tr6 = new TableRow(getActivity());
			TableRow tr7 = new TableRow(getActivity());
			TableRow tr9 = new TableRow(getActivity());

			TextView tvExchangeName = new TextView(getActivity());
			TextView tvBTCRewards = new TextView(getActivity());
			TextView tvBTCPayout = new TextView(getActivity());
			TextView tvHashrate = new TextView(getActivity());

			tr1.setGravity(Gravity.CENTER_HORIZONTAL);
			tr2.setGravity(Gravity.CENTER_HORIZONTAL);
			tr4.setGravity(Gravity.CENTER_HORIZONTAL);
			tr5.setGravity(Gravity.CENTER_HORIZONTAL);
			tr6.setGravity(Gravity.CENTER_HORIZONTAL);
			tr7.setGravity(Gravity.CENTER_HORIZONTAL);
			tr9.setGravity(Gravity.CENTER_HORIZONTAL);
			
			String RewardsBTC = "" + data.getUser().getConfirmed_rewards();
			String Hashrate = "" + data.getUser().getHash_rate();
			String Payout = "" + data.getUser().getPayouts();

			tvBTCRewards.setText("Reward: " + RewardsBTC
					+ " BTC");
			tvBTCPayout.setText("Total Payout: " + Payout
					+ " BTC");
			tvHashrate.setText("Total Hashrate: " + Hashrate
					+ " MH/s");

			tr1.addView(tvExchangeName);
			tr2.addView(tvBTCRewards);
			tr4.addView(tvBTCPayout);
			tr9.addView(tvHashrate);

			t1.addView(tr2);
			t1.addView(tr3);
			t1.addView(tr4);
			t1.addView(tr9);
			t1.addView(tr1);
			
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	protected static void readPreferences(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		pref_50BTCKey = prefs.getString("50BTCKey", "");
	}

}