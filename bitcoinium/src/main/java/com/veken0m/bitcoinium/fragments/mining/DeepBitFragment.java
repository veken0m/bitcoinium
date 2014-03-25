
package com.veken0m.bitcoinium.fragments.mining;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import com.veken0m.utils.CurrencyUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStreamReader;
import java.util.List;

public class DeepBitFragment extends SherlockFragment {

    private static String pref_deepbitKey = "";
    private static int pref_widgetMiningPayoutUnit = 0;
    private static DeepBitData data = null;
    private Boolean connectionFail = false;
    private ProgressDialog minerProgressDialog;
    private final Handler mMinerHandler = new Handler();

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

    public void onPause() {
        super.onPause();
        mMinerHandler.removeCallbacks(mGraphView);
        minerProgressDialog.dismiss();
    }

    void getMinerStats() {

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

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            Resources res = getResources();
            String text = String.format(res.getString(R.string.minerConnectionError), "DeepBit");
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

                TextView tvBTCRewards = new TextView(activity);
                TextView tvBTCPayout = new TextView(activity);
                TextView tvHashrate = new TextView(activity);
                tr1.setGravity(Gravity.CENTER_HORIZONTAL);
                tr2.setGravity(Gravity.CENTER_HORIZONTAL);
                tr3.setGravity(Gravity.CENTER_HORIZONTAL);

                String RewardsBTC = "Reward: "
                        + CurrencyUtils.formatPayout(data.getConfirmed_reward(), pref_widgetMiningPayoutUnit, "BTC");
                String TotalHashrate = "Total Hashrate: "
                        + data.getHashrate() + " MH/s";
                String TotalPayout = "Total Payout: "
                        + CurrencyUtils.formatPayout(data.getPayout_history(), pref_widgetMiningPayoutUnit, "BTC");

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
                    TableRow tr9 = new TableRow(activity);
                    TableRow tr10 = new TableRow(activity);
                    TableRow tr11 = new TableRow(activity);
                    TableRow tr12 = new TableRow(activity);

                    TextView tvMinerName = new TextView(activity);
                    TextView tvAlive = new TextView(activity);
                    TextView tvShares = new TextView(activity);
                    TextView tvStales = new TextView(activity);

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
    }

    private static void readPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_deepbitKey = prefs.getString("deepbitKey", "");
        pref_widgetMiningPayoutUnit = Integer.parseInt(prefs.getString("widgetMiningPayoutUnitPref", "0"));
    }

}
