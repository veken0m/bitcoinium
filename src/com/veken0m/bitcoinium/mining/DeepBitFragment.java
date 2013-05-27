
package com.veken0m.bitcoinium.mining;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.actionbarsherlock.app.SherlockFragment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veken0m.bitcoinium.R;
import com.veken0m.mining.deepbit.DeepBitData;
import com.veken0m.mining.deepbit.Worker;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStreamReader;
import java.util.List;

public class DeepBitFragment extends SherlockFragment {

    protected static String pref_deepbitKey = "";
    protected static DeepBitData data;
    protected Boolean connectionFail = false;
    private ProgressDialog minerProgressDialog;
    final Handler mMinerHandler = new Handler();

    public DeepBitFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readPreferences(getActivity());

        View view = inflater.inflate(R.layout.table_fragment, container, false);
        viewMinerStats(view);
        return view;
    }

    public void getMinerStats(Context context) {

        try {
            HttpClient client = new DefaultHttpClient();

            HttpGet post = new HttpGet("http://deepbit.net/api/"
                    + pref_deepbitKey);
            HttpResponse response = client.execute(post);
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.readValue(new InputStreamReader(response.getEntity()
                    .getContent(), "UTF-8"), DeepBitData.class);

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
                "Working...", "Retrieving Miner Stats", true, false);

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
                    + "DeepBit"
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
        }
    }

    public void drawMinerUI() {

        try {

            TableLayout t1 = (TableLayout) getView().findViewById(
                    R.id.minerStatlist);

            TableRow tr1 = new TableRow(getActivity());
            TableRow tr2 = new TableRow(getActivity());
            TableRow tr3 = new TableRow(getActivity());

            TextView tvBTCRewards = new TextView(getActivity());
            TextView tvBTCPayout = new TextView(getActivity());
            TextView tvHashrate = new TextView(getActivity());
            tr1.setGravity(Gravity.CENTER_HORIZONTAL);
            tr2.setGravity(Gravity.CENTER_HORIZONTAL);
            tr3.setGravity(Gravity.CENTER_HORIZONTAL);

            String RewardsBTC = "Reward: "
                    + data.getConfirmed_reward().floatValue() + "BTC";
            String TotalHashrate = "Total Hashrate: "
                    + data.getHashrate().floatValue() + " MH/s";
            String TotalPayout = "Total Payout: "
                    + data.getPayout_history().floatValue() + "BTC";

            tvBTCRewards.setText(RewardsBTC);
            tvBTCPayout.setText(TotalPayout);
            tvHashrate.setText(TotalHashrate);

            tr1.addView(tvBTCRewards);
            tr2.addView(tvBTCPayout);
            tr3.addView(tvHashrate);

            t1.addView(tr1);
            t1.addView(tr2);
            t1.addView(tr3);

            // End of Non-worker data
            List<Worker> Workers = data.getWorkers().getWorkers();
            List<String> WorkerNames = data.getWorkers().getNames();
            for (int i = 0; i < Workers.size(); i++) {
                TableRow tr9 = new TableRow(getActivity());
                TableRow tr10 = new TableRow(getActivity());
                TableRow tr11 = new TableRow(getActivity());
                TableRow tr12 = new TableRow(getActivity());

                TextView tvMinerName = new TextView(getActivity());
                TextView tvAlive = new TextView(getActivity());
                TextView tvShares = new TextView(getActivity());
                TextView tvStales = new TextView(getActivity());

                tr9.setGravity(Gravity.CENTER_HORIZONTAL);
                tr10.setGravity(Gravity.CENTER_HORIZONTAL);
                tr11.setGravity(Gravity.CENTER_HORIZONTAL);
                tr12.setGravity(Gravity.CENTER_HORIZONTAL);

                tvMinerName.setText("\nMiner: " + WorkerNames.get(i));
                tvAlive.setText("Alive: " + Workers.get(i).getAlive());
                tvShares.setText("Shares: " + Workers.get(i).getShares());
                tvStales.setText("Stales: " + Workers.get(i).getStales());

                if (Workers.get(i).getAlive()) {
                    tvMinerName.setTextColor(Color.GREEN);
                } else {
                    tvMinerName.setTextColor(Color.RED);
                }

                tr9.addView(tvMinerName);
                tr10.addView(tvAlive);
                tr11.addView(tvShares);
                tr12.addView(tvStales);

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

        pref_deepbitKey = prefs.getString("deepbitKey", "");
    }

}
