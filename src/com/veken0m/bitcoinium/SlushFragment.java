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
import com.veken0m.miningpools.slush.Slush;

public class SlushFragment extends SherlockFragment {

	protected static String pref_slushKey = "";
	protected static Slush data;
	protected Boolean connectionFail = false;
	private ProgressDialog minerProgressDialog;
	final Handler mMinerHandler = new Handler();

	public SlushFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		readPreferences(getActivity());

		if (pref_slushKey.equalsIgnoreCase("")) {

			int duration = Toast.LENGTH_LONG;
			CharSequence text = "Please enter your Slush API Token to use MinerStatsActivity with Slush";

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

			HttpGet post = new HttpGet("https://mining.bitcoin.cz/accounts/profile/json/" + pref_slushKey);
			HttpResponse response = client.execute(post);
			ObjectMapper mapper = new ObjectMapper();
			data = mapper.readValue(new InputStreamReader(response
					.getEntity().getContent(), "UTF-8"), Slush.class);

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

		OrderbookThread gt = new OrderbookThread();
		gt.start();
	}

	public class OrderbookThread extends Thread {

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
					+ "Slush"
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
			TableRow tr8 = new TableRow(getActivity());
			TableRow tr9 = new TableRow(getActivity());
			TableRow tr10 = new TableRow(getActivity());
			TableRow tr11 = new TableRow(getActivity());
			TableRow tr12 = new TableRow(getActivity());
			
			TextView tvConfirmed_nmc_reward = new TextView(getActivity());
			TextView tvConfirmed_reward = new TextView(getActivity());
			TextView tvEstimated_reward = new TextView(getActivity());
			TextView tvHashrate = new TextView(getActivity());
			TextView tvNmc_send_threshold = new TextView(getActivity());
			TextView tvRating = new TextView(getActivity());
			TextView tvSend_threshold = new TextView(getActivity());
			TextView tvUnconfirmed_nmc_reward = new TextView(getActivity());
			TextView tvUnconfirmed_reward = new TextView(getActivity());
			TextView tvUsername = new TextView(getActivity());
			TextView tvWallet = new TextView(getActivity());
			//TextView tvWorkers = new TextView(getActivity());

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
			
			String hashrate = "Hashrate: " + data.getHashrate() + " MH/s";
			String confirmed_reward = "Confirmed: " + data.getConfirmed_reward() + " BTC";
			String estimated_reward = "Estimated: " + data.getEstimated_reward() + " BTC";
			String confirmed_nmc_reward = "Confirmed: " + data.getConfirmed_nmc_reward() + " NMC";
			String rating = "Rating: " + data.getRating();
			String unconfirmed_reward = "Unconfirmed: " + data.getUnconfirmed_reward() + " BTC";
			//String send_threshold = "Send Threshold: " + data.getSend_threshold() + " BTC";
			String unconfirmed_nmc_reward = "Unconfirmed: " + data.getUnconfirmed_nmc_reward() + " NMC";
			//String nmc_send_threshold = "Send Threshold: " + data.getNmc_send_threshold() + " NMC";
			String username = "Username: " + data.getUsername();
			String wallet = "Wallet: " + data.getWallet();
			//String workers = data.getWorkers();
			
			tvHashrate.setText(hashrate);
			tvConfirmed_nmc_reward.setText(confirmed_nmc_reward);
			tvConfirmed_reward.setText(confirmed_reward);
			tvEstimated_reward.setText(estimated_reward);
			//tvNmc_send_threshold.setText(nmc_send_threshold);
			tvRating.setText(rating);
			//tvSend_threshold.setText(send_threshold);
			tvUnconfirmed_nmc_reward.setText(unconfirmed_nmc_reward);
			tvUnconfirmed_reward.setText(unconfirmed_reward);
			tvUsername.setText(username);
			tvWallet.setText(wallet);

			tr8.addView(tvConfirmed_nmc_reward);
			tr4.addView(tvConfirmed_reward);
			tr3.addView(tvEstimated_reward);
			tr2.addView(tvHashrate);
			tr5.addView(tvWallet);
			tr1.addView(tvUsername);
			tr9.addView(tvRating);
			//tr10.addView(tvSend_threshold);
			tr11.addView(tvUnconfirmed_nmc_reward);
			tr12.addView(tvUnconfirmed_reward);

			t1.addView(tr1);
			t1.addView(tr2);
			t1.addView(tr3);
			t1.addView(tr4);
			t1.addView(tr5);
			t1.addView(tr8);
			t1.addView(tr9);
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

		pref_slushKey = prefs.getString("slushKey", "");
	}

}
