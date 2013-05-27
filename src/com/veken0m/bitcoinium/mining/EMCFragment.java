
package com.veken0m.bitcoinium.mining;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.actionbarsherlock.app.SherlockFragment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veken0m.bitcoinium.R;
import com.veken0m.mining.emc.EMC;
import com.veken0m.mining.emc.Workers;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStreamReader;
import java.util.List;

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

        View view = inflater.inflate(R.layout.table_fragment, container, false);
        viewMinerStats(view);
        return view;
    }

    public void getMinerStats(Context context) {

        try {

            HttpClient client = new DefaultHttpClient();
            HttpGet post = new HttpGet("https://eclipsemc.com/api.php?key="
                    + pref_emcKey + "&action=userstats");
            HttpResponse response = client.execute(post);

            ObjectMapper mapper = new ObjectMapper();
            data = mapper.readValue(new InputStreamReader(response.getEntity()
                    .getContent(), "UTF-8"), EMC.class);

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

            // User Data
            String ConfirmedRewardsBTC = "Confirmed Rewards: "
                    + data.getData().getUser().getConfirmed_rewards() + " BTC";
            String UnconfirmedRewardsBTC = "Unconfirmed Rewards: "
                    + data.getData().getUser().getUnconfirmed_rewards()
                    + " BTC";
            String EstimatedRewardsBTC = "Estimated Rewards: "
                    + data.getData().getUser().getEstimated_rewards() + " BTC";
            String TotalRewardsBTC = "Total Rewards: "
                    + data.getData().getUser().getTotal_payout() + " BTC";
            String BlocksFound = "Blocks Found: "
                    + data.getData().getUser().getBlocks_found();

            tvConfirmedRewards.setText(ConfirmedRewardsBTC);
            tvUnconfirmedRewards.setText(UnconfirmedRewardsBTC);
            tvEstimatedRewards.setText(EstimatedRewardsBTC);
            tvTotalRewards.setText(TotalRewardsBTC);
            tvBlocksFound.setText(BlocksFound);

            tr1.addView(tvEstimatedRewards);
            tr2.addView(tvConfirmedRewards);
            tr3.addView(tvUnconfirmedRewards);
            tr4.addView(tvTotalRewards);
            tr5.addView(tvBlocksFound);

            t1.addView(tr1);
            t1.addView(tr2);
            t1.addView(tr3);
            t1.addView(tr4);
            t1.addView(tr5);

            // TODO: Fix Miner data JSON mapping from EMC
            // Miner Data
            List<Workers> workers = data.getWorkers();

            for (int i = 0; i < workers.size(); i++) {
                String WorkerName = "\nWorker: "
                        + workers.get(i).getWorker_name();
                String HashRate = "Hashrate: " + workers.get(i).getHash_rate();
                String RoundShares = "Round Shares: "
                        + workers.get(i).getRound_shares();
                String ResetShares = "Reset Shares: "
                        + workers.get(i).getReset_shares();
                String TotalShares = "Total Shares: "
                        + workers.get(i).getTotal_shares();
                String LastActivity = "Latest Activity: "
                        + workers.get(i).getLast_activity();

                TableRow tr8 = new TableRow(getActivity());
                TableRow tr9 = new TableRow(getActivity());
                TableRow tr10 = new TableRow(getActivity());
                TableRow tr11 = new TableRow(getActivity());
                TableRow tr12 = new TableRow(getActivity());
                TableRow tr13 = new TableRow(getActivity());

                TextView tvWorkerName = new TextView(getActivity());
                TextView tvMinerHashrate = new TextView(getActivity());
                TextView tvRoundShares = new TextView(getActivity());
                TextView tvResetShares = new TextView(getActivity());
                TextView tvTotalShares = new TextView(getActivity());
                TextView tvLastActivity = new TextView(getActivity());

                tr8.setGravity(Gravity.CENTER_HORIZONTAL);
                tr9.setGravity(Gravity.CENTER_HORIZONTAL);
                tr10.setGravity(Gravity.CENTER_HORIZONTAL);
                tr11.setGravity(Gravity.CENTER_HORIZONTAL);
                tr12.setGravity(Gravity.CENTER_HORIZONTAL);
                tr13.setGravity(Gravity.CENTER_HORIZONTAL);

                tvWorkerName.setText(WorkerName);
                tvMinerHashrate.setText(HashRate);
                tvRoundShares.setText(RoundShares);
                tvResetShares.setText(ResetShares);
                tvTotalShares.setText(TotalShares);
                tvLastActivity.setText(LastActivity);

                tr8.addView(tvWorkerName);
                tr9.addView(tvMinerHashrate);
                tr10.addView(tvRoundShares);
                tr11.addView(tvResetShares);
                tr12.addView(tvTotalShares);
                tr13.addView(tvLastActivity);

                t1.addView(tr8);
                t1.addView(tr9);
                t1.addView(tr10);
                t1.addView(tr11);
                t1.addView(tr12);
                t1.addView(tr13);
            }

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
