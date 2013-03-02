package com.veken0m.bitcoinium;

import java.io.InputStreamReader;
import java.util.List;

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
import android.graphics.Color;
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
import com.veken0m.miningpools.bitminter.BitMinterData;
import com.veken0m.miningpools.bitminter.Workers;

public class BitMinterFragment extends SherlockFragment {

	protected static String pref_bitminterKey = "";
	protected static BitMinterData data;
	protected Boolean connectionFail = false;
	private ProgressDialog minerProgressDialog;
	final Handler mMinerHandler = new Handler();

	public BitMinterFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		readPreferences(getActivity());

		if (pref_bitminterKey.equalsIgnoreCase("")) {

			int duration = Toast.LENGTH_LONG;
			CharSequence text = "Please enter your BitMinter API Token to use MinerStatsActivity with BitMinter";

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

			// pref_bitminterKey = "M3IIJ5OCN2SQKRGRYVIXUFCJGG44DPNJ"; //Test
			// Key

			HttpGet post = new HttpGet("https://bitminter.com/api/users"
					+ "?key=" + pref_bitminterKey);
			HttpResponse response = client.execute(post);

			ObjectMapper mapper = new ObjectMapper();
			data = mapper.readValue(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"),
					BitMinterData.class);

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

			TextView tvBTCRewards = new TextView(getActivity());
			TextView tvNMCRewards = new TextView(getActivity());
			TextView tvTotalHashrate = new TextView(getActivity());

			tr1.setGravity(Gravity.CENTER_HORIZONTAL);
			tr2.setGravity(Gravity.CENTER_HORIZONTAL);
			tr3.setGravity(Gravity.CENTER_HORIZONTAL);
			tr4.setGravity(Gravity.CENTER_HORIZONTAL);
			tr5.setGravity(Gravity.CENTER_HORIZONTAL);
			tr6.setGravity(Gravity.CENTER_HORIZONTAL);
			tr7.setGravity(Gravity.CENTER_HORIZONTAL);
			
			List<Workers> workers = data.getWorkers();

			String RewardsBTC = "" + data.getBalances().getBTC();
			String Hashrate = "" + data.getHash_rate().toString();
			String RewardsNMC = "" + data.getBalances().getNMC();
			
			tvBTCRewards.setText("BTC Reward: " + RewardsBTC
					+ " BTC");
			tvNMCRewards.setText("NMC Reward: " + RewardsNMC
					+ " NMC");
			tvTotalHashrate.setText("Total Hashrate: "
					+ Hashrate + " MH/s\n");

			tr2.addView(tvBTCRewards);
			tr3.addView(tvNMCRewards);
			tr4.addView(tvTotalHashrate);

			t1.addView(tr2);
			t1.addView(tr3);
			t1.addView(tr4);
			t1.addView(tr1);

			// End of Non-worker data

			for (int i = 0; i < workers.size(); i++) {
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

				tvMinerName.setText("Miner: " + workers.get(i).getName());
				tvHashrate.setText("Hashrate: "
						+ Utils.formatDecimal(workers.get(i).getHash_rate()
								.floatValue(), 2, false) + " MH/s");
				tvAlive.setText("Alive: " + workers.get(i).getAlive());
				tvShares.setText("Shares: "
						+ Utils.formatDecimal(workers.get(i).getWork()
								.getBTC().getTotal_accepted().floatValue(), 0, true));
				tvStales.setText("Stales: "
						+ Utils.formatDecimal(workers.get(i).getWork()
								.getBTC().getTotal_rejected().floatValue(), 0 , true)
						+ "\n");

				if (workers.get(i).getAlive()) {
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
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	protected static void readPreferences(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		pref_bitminterKey = prefs.getString("bitminterKey", "");
	}

}
