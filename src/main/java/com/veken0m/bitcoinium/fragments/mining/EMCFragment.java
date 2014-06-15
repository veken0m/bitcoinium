
package com.veken0m.bitcoinium.fragments.mining;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veken0m.bitcoinium.R;
import com.veken0m.mining.emc.EMC;
import com.veken0m.mining.emc.User;
import com.veken0m.mining.emc.Workers;
import com.veken0m.utils.CurrencyUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStreamReader;
import java.util.List;

public class EMCFragment extends Fragment {

    private static String pref_emcKey = "";
    private static int pref_widgetMiningPayoutUnit = 0;
    private static EMC data = null;
    private Boolean connectionFail = false;
    private ProgressDialog minerProgressDialog;
    private final Handler mMinerHandler = new Handler();

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

    public void onPause() {
        super.onPause();
        mMinerHandler.removeCallbacks(mGraphView);
        minerProgressDialog.dismiss();
    }

    void getMinerStats() {

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
        if (minerProgressDialog != null && minerProgressDialog.isShowing())
            return;

        Context context = view.getContext();
        if (context != null)
            minerProgressDialog = ProgressDialog.show(context, getString(R.string.working), getString(R.string.retreivingMinerStats), true, false);


        MinerStatsThread gt = new MinerStatsThread();
        gt.start();
    }

    private class MinerStatsThread extends Thread {

        @Override
        public void run() {
            getMinerStats();
            mMinerHandler.post(mGraphView);
        }
    }

    private final Runnable mGraphView = new Runnable() {
        @Override
        public void run() {
            try {
                safelyDismiss(minerProgressDialog);
            } catch(Exception e){
                // This happens when we try to show a dialog when app is not in the foreground. Suppress it for now
            }
            drawMinerUI();
        }
    };

    private void safelyDismiss(ProgressDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (connectionFail) {

            final Context context = getActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            Resources res = getResources();
            String text = String.format(res.getString(R.string.minerConnectionError), "EclipseMC");
            builder.setMessage(text);
            builder.setPositiveButton(R.string.OK,
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

    void drawMinerUI() {

        View view = getView();

        if (view != null) {
            try {
                TableLayout t1 = (TableLayout) view.findViewById(
                        R.id.minerStatlist);

                Activity activity = getActivity();

                TableRow tr1 = new TableRow(activity);
                TableRow tr2 = new TableRow(activity);
                TableRow tr3 = new TableRow(activity);
                TableRow tr4 = new TableRow(activity);
                TableRow tr5 = new TableRow(activity);

                TextView tvConfirmedRewards = new TextView(activity);
                TextView tvUnconfirmedRewards = new TextView(activity);
                TextView tvEstimatedRewards = new TextView(activity);
                TextView tvTotalRewards = new TextView(activity);
                TextView tvBlocksFound = new TextView(activity);

                tr1.setGravity(Gravity.CENTER_HORIZONTAL);
                tr2.setGravity(Gravity.CENTER_HORIZONTAL);
                tr3.setGravity(Gravity.CENTER_HORIZONTAL);
                tr4.setGravity(Gravity.CENTER_HORIZONTAL);
                tr5.setGravity(Gravity.CENTER_HORIZONTAL);

                // User Data
                User userData = data.getData().getUser();
                String ConfirmedRewardsBTC = "Confirmed Rewards: "
                        + CurrencyUtils.formatPayout(userData.getConfirmed_rewards(), pref_widgetMiningPayoutUnit, "BTC");
                String UnconfirmedRewardsBTC = "Unconfirmed Rewards: "
                        + CurrencyUtils.formatPayout(userData.getUnconfirmed_rewards(), pref_widgetMiningPayoutUnit, "BTC");
                String EstimatedRewardsBTC = "Estimated Rewards: "
                        + CurrencyUtils.formatPayout(userData.getEstimated_rewards(), pref_widgetMiningPayoutUnit, "BTC");
                String TotalRewardsBTC = "Total Rewards: "
                        + CurrencyUtils.formatPayout(userData.getTotal_payout(), pref_widgetMiningPayoutUnit, "BTC");
                String BlocksFound = "Blocks Found: "
                        + userData.getBlocks_found();

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

                for (Workers worker : workers) {
                    String WorkerName = "\nWorker: "
                            + worker.getWorker_name();
                    String HashRate = "Hashrate: " + worker.getHash_rate();
                    String RoundShares = "Round Shares: "
                            + worker.getRound_shares();
                    String ResetShares = "Reset Shares: "
                            + worker.getReset_shares();
                    String TotalShares = "Total Shares: "
                            + worker.getTotal_shares();
                    String LastActivity = "Latest Activity: "
                            + worker.getLast_activity();

                    TableRow tr8 = new TableRow(activity);
                    TableRow tr9 = new TableRow(activity);
                    TableRow tr10 = new TableRow(activity);
                    TableRow tr11 = new TableRow(activity);
                    TableRow tr12 = new TableRow(activity);
                    TableRow tr13 = new TableRow(activity);

                    TextView tvWorkerName = new TextView(activity);
                    TextView tvMinerHashrate = new TextView(activity);
                    TextView tvRoundShares = new TextView(activity);
                    TextView tvResetShares = new TextView(activity);
                    TextView tvTotalShares = new TextView(activity);
                    TextView tvLastActivity = new TextView(activity);

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
    }

    private static void readPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_emcKey = prefs.getString("emcKey", "");
        pref_widgetMiningPayoutUnit = Integer.parseInt(prefs.getString("widgetMiningPayoutUnitPref", "0"));
    }

}
