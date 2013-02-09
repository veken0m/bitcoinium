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
import com.veken0m.miningpools.emc.EMC;

public class EMCFragment extends SherlockFragment {

	protected static String pref_emcKey = "";
	protected static EMC data;
	protected Boolean connectionFail = false;
	private ProgressDialog minerProgressDialog;
	final Handler mMinerHandler = new Handler();

	public EMCFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		readPreferences(getActivity());

		if (pref_emcKey.equalsIgnoreCase("")) {

			int duration = Toast.LENGTH_LONG;
			CharSequence text = "Please enter your EMC API Token to use MinerStatsActivity with EMC";

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
			HttpGet post = new HttpGet("https://eclipsemc.com/api.php?key=" + pref_emcKey + "&action=userstats");
			HttpResponse response = client.execute(post);

			ObjectMapper mapper = new ObjectMapper();
			data = mapper.readValue(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"),
					EMC.class);

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
					+ "EMC"
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

			TextView tvConfirmedRewards = new TextView(getActivity());
			TextView tvUnconfirmedRewards = new TextView(getActivity());
			TextView tvEstimatedRewards = new TextView(getActivity());
			TextView tvTotalRewards = new TextView(getActivity());
			TextView tvBlocksFound = new TextView(getActivity());

			tr1.setGravity(Gravity.CENTER_HORIZONTAL);
			tr2.setGravity(Gravity.CENTER_HORIZONTAL);
			tr3.setGravity(Gravity.CENTER_HORIZONTAL);
			tr4.setGravity(Gravity.CENTER_HORIZONTAL);
			tr5.setGravity(Gravity.CENTER_HORIZONTAL);
			tr6.setGravity(Gravity.CENTER_HORIZONTAL);
			tr7.setGravity(Gravity.CENTER_HORIZONTAL);

			// User Data
			String ConfirmedRewardsBTC = "Confirmed Rewards: " + data.getData().getUser().getConfirmed_rewards()  + " BTC";
			String UnconfirmedRewardsBTC = "Unconfirmed Rewards: " + data.getData().getUser().getUnconfirmed_rewards()  + " BTC";
			String EstimatedRewardsBTC = "Estimated Rewards: " + data.getData().getUser().getEstimated_rewards()  + " BTC";
			String TotalRewardsBTC = "Total Rewards: " + data.getData().getUser().getTotal_payout()  + " BTC";
			String BlocksFound = "Blocks Found " + data.getData().getUser().getBlocks_found()  + " BTC";
			
			tvConfirmedRewards.setText(ConfirmedRewardsBTC);
			tvUnconfirmedRewards.setText(UnconfirmedRewardsBTC);
			tvEstimatedRewards.setText(EstimatedRewardsBTC);
			tvTotalRewards.setText(TotalRewardsBTC);
			tvBlocksFound.setText(BlocksFound);

			// TODO: Fix Miner data JSON mapping from EMC
//			// Miner Data
//			List<Workers> workers = data.getWorkers();
//			String WorkerName = workers.get(0).getWorker_name();
//			String HashRate = workers.get(0).getHash_rate();
//			String RoundShares = workers.get(0).getRound_shares();
//			String ResetShares = workers.get(0).getReset_shares();
//			String TotalShares = workers.get(0).getTotal_shares();
//			String LastActivity = workers.get(0).getLast_activity();

			tr1.addView(tvConfirmedRewards);
			tr2.addView(tvUnconfirmedRewards);
			tr3.addView(tvEstimatedRewards);
			tr4.addView(tvTotalRewards);

			t1.addView(tr2);
			t1.addView(tr3);
			t1.addView(tr4);
			t1.addView(tr1);

			// End of Non-worker data

			//for (int i = 0; i < workers.size(); i++) {
				TableRow tr8 = new TableRow(getActivity());
				TableRow tr9 = new TableRow(getActivity());
				TableRow tr10 = new TableRow(getActivity());
				TableRow tr11 = new TableRow(getActivity());
				TableRow tr12 = new TableRow(getActivity());
				TableRow tr13 = new TableRow(getActivity());

				TextView tvMinerName = new TextView(getActivity());
				TextView tvHashrate = new TextView(getActivity());
				TextView tvAlive = new TextView(getActivity());
				TextView tvShares = new TextView(getActivity());
				TextView tvStales = new TextView(getActivity());

				tr8.setGravity(Gravity.CENTER_HORIZONTAL);
				tr9.setGravity(Gravity.CENTER_HORIZONTAL);
				tr10.setGravity(Gravity.CENTER_HORIZONTAL);
				tr11.setGravity(Gravity.CENTER_HORIZONTAL);
				tr12.setGravity(Gravity.CENTER_HORIZONTAL);
				tr13.setGravity(Gravity.CENTER_HORIZONTAL);

				tr8.addView(tvMinerName);
				tr9.addView(tvHashrate);
				tr10.addView(tvAlive);
				tr11.addView(tvShares);
				tr12.addView(tvStales);
				tr13.addView(tvBlocksFound);

				t1.addView(tr8);
				t1.addView(tr9);
				t1.addView(tr10);
				t1.addView(tr11);
				t1.addView(tr12);
				t1.addView(tr13);
			//}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	protected static void readPreferences(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		pref_emcKey = prefs.getString("emcKey", "");
	}

}
