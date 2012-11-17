package com.veken0m.cavirtex;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.veken0m.miningpools.bitminter.Workers;
import com.veken0m.cavirtex.MinerStats.MinerData;

public class BitMinterFragment extends SherlockFragment {

	protected static String pref_bitminterKey = "";
	static MinerData minerdata = new MinerData();;
	final protected static String notAvailable = "N/A";

	private ProgressDialog minerProgressDialog;
	final Handler mMinerHandler = new Handler();
	protected Boolean connectionFail = false;

	View view = null;

	public BitMinterFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		readPreferences(getActivity());

		if (pref_bitminterKey.equalsIgnoreCase("")) {
			// super.onCreate(savedInstanceState);

			int duration = Toast.LENGTH_LONG;
			CharSequence text = "Please enter your BitMinter API Token to use MinerStats with BitMinter";

			Toast toast = Toast.makeText(getActivity(), text, duration);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();

			Intent settingsActivity = new Intent(
					getActivity().getBaseContext(), Preferences.class);
			startActivity(settingsActivity);
		}

		view = inflater.inflate(R.layout.table_fragment, container, false);
		viewMinerStats(view);
		return view;
	}

	public void getMinerStats(Context context) {

		try {
			minerdata.setBitMinterData(pref_bitminterKey);

		} catch (Exception e) {
			Log.e("Orderbook error", "exception", e);
			connectionFail = true;
		}

		try {

			minerdata.setDifficulty();

		} catch (Exception e) {
			Log.e("Orderbook error", "exception", e);
			// connectionFail = true;
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
					+ "BitMinter"
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

			TextView tvExchangeName = new TextView(getActivity());
			TextView tvBTCRewards = new TextView(getActivity());
			TextView tvNMCRewards = new TextView(getActivity());
			TextView tvCurrentDifficulty = new TextView(getActivity());
			TextView tvNextDifficulty = new TextView(getActivity());
			TextView tvTotalHashrate = new TextView(getActivity());

			tr1.setGravity(Gravity.CENTER_HORIZONTAL);
			tr2.setGravity(Gravity.CENTER_HORIZONTAL);
			tr3.setGravity(Gravity.CENTER_HORIZONTAL);
			tr4.setGravity(Gravity.CENTER_HORIZONTAL);
			tr5.setGravity(Gravity.CENTER_HORIZONTAL);
			tr6.setGravity(Gravity.CENTER_HORIZONTAL);
			tr7.setGravity(Gravity.CENTER_HORIZONTAL);
			tvBTCRewards.setText("BTC Reward: " + minerdata.getRewardsBTC()
					+ " BTC");
			tvNMCRewards.setText("NMC Reward: " + minerdata.getRewardsNMC()
					+ " NMC");
			tvTotalHashrate.setText("Total Hashrate: " + minerdata.getHashrate()
					+ " MH/s");

			tr1.addView(tvExchangeName);
			tr2.addView(tvBTCRewards);
			tr3.addView(tvNMCRewards);
			tr4.addView(tvTotalHashrate);

			t1.addView(tr2);
			t1.addView(tr3);
			t1.addView(tr4);
			t1.addView(tr1);

			tvCurrentDifficulty.setText("Current Difficulty: "
					+ Utils.formatNoDecimals(Float.valueOf(minerdata
							.getCurrentDifficulty())));
			tvNextDifficulty.setText("Estimated Next Difficulty: "
					+ Utils.formatNoDecimals(Float.valueOf(minerdata
							.getNextDifficulty())) + "\n");

			if (Float.valueOf(minerdata.getNextDifficulty()) < Float
					.valueOf(minerdata.getCurrentDifficulty())) {
				tvNextDifficulty.setTextColor(Color.GREEN);
			} else {
				tvNextDifficulty.setTextColor(Color.RED);
			}

			tr5.addView(tvCurrentDifficulty);
			tr6.addView(tvNextDifficulty);

			t1.addView(tr5);
			t1.addView(tr6);
			// t1.addView(tr7);

			// End of Non-worker data
			List<Workers> worker = minerdata.getWorkers();

			for (int i = 0; i < worker.size(); i++) {
				TableRow tr8 = new TableRow(getActivity());
				TableRow tr9 = new TableRow(getActivity());
				TableRow tr10 = new TableRow(getActivity());
				TableRow tr11 = new TableRow(getActivity());
				TableRow tr12 = new TableRow(getActivity());

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

				tvMinerName.setText("Miner: " + worker.get(i).getName());
				tvHashrate.setText("Hashrate: "
						+ Utils.formatTwoDecimals(worker.get(i).getHash_rate()
								.floatValue()) + " MH/s");
				tvAlive.setText("Alive: " + worker.get(i).getAlive());
				tvShares.setText("Shares: "
						+ Utils.formatNoDecimals(worker.get(i).getWork()
								.getBTC().getTotal_accepted().floatValue()));
				tvStales.setText("Stales: "
						+ Utils.formatNoDecimals(worker.get(i).getWork()
								.getBTC().getTotal_rejected().floatValue())
						+ "\n");

				if (worker.get(i).getAlive()) {
					tvMinerName.setTextColor(Color.GREEN);
				} else {
					tvMinerName.setTextColor(Color.RED);
				}

				tr8.addView(tvMinerName);
				tr9.addView(tvHashrate);
				tr10.addView(tvAlive);
				tr11.addView(tvShares);
				tr12.addView(tvStales);

				t1.addView(tr8);
				t1.addView(tr9);
				t1.addView(tr10);
				t1.addView(tr11);
				t1.addView(tr12);
			}

			// sv.addView(t1);

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
				pref_bitminterKey = pPrefs.getString("bitminterKey", "");
			}
		};
		pref_bitminterKey = prefs.getString("bitminterKey", "");
	}

}
