package com.veken0m.cavirtex.fragments.mining;

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
import com.veken0m.cavirtex.R;
import com.veken0m.mining.fiftybtc.FiftyBTC;
import com.veken0m.mining.fiftybtc.Worker;
import com.veken0m.utils.CurrencyUtils;
import com.veken0m.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStreamReader;
import java.util.List;

public class FiftyBTCFragment extends Fragment {

    private static String pref_50BTCKey = "";
    private static int pref_widgetMiningPayoutUnit = 0;
    private static FiftyBTC data = null;
    private final Handler mMinerHandler = new Handler();
    private Boolean connectionFail = false;
    private ProgressDialog minerProgressDialog;
    private final Runnable mGraphView = new Runnable() {
        @Override
        public void run() {
            try {
                safelyDismiss(minerProgressDialog);
            } catch (Exception e) {
                // This happens when we try to show a dialog when app is not in the foreground. Suppress it for now
            }
            drawMinerUI();
        }
    };

    public FiftyBTCFragment() {
    }

    private static void readPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_50BTCKey = prefs.getString("50BTCKey", "");
        pref_widgetMiningPayoutUnit = Integer.parseInt(prefs.getString("widgetMiningPayoutUnitPref", "0"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readPreferences(getActivity());

        View view = inflater.inflate(R.layout.fragment_table, container, false);
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

            HttpGet post = new HttpGet("https://50btc.com/api/" + pref_50BTCKey);
            HttpResponse response = client.execute(post);
            ObjectMapper mapper = new ObjectMapper();

            // Testing from raw resource
            //InputStream raw = getResources().openRawResource(R.raw.fiftybtc);
            //Reader is = new BufferedReader(new InputStreamReader(raw, "UTF8"));
            //data = mapper.readValue(is, FiftyBTC.class);
            data = mapper.readValue(new InputStreamReader(response.getEntity()
                    .getContent(), "UTF-8"), FiftyBTC.class);

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

    private void safelyDismiss(ProgressDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (connectionFail) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            Resources res = getResources();
            String text = String.format(res.getString(R.string.error_minerConnection), "50BTC");
            builder.setMessage(text);
            builder.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }
            );

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
                        + CurrencyUtils.formatPayout(data.getUser().getConfirmed_rewards(), pref_widgetMiningPayoutUnit, "BTC");
                String Hashrate = "Total Hashrate: "
                        + data.getUser().getHash_rate() + " MH/s";
                String Payout = "Total Payout: " + CurrencyUtils.formatPayout(data.getUser().getPayouts(), pref_widgetMiningPayoutUnit, "BTC");

                tvBTCRewards.setText(RewardsBTC);
                tvBTCPayout.setText(Payout);
                tvHashrate.setText(Hashrate);

                tr1.addView(tvBTCRewards);
                tr2.addView(tvBTCPayout);
                tr3.addView(tvHashrate);

                t1.addView(tr1);
                t1.addView(tr2);
                t1.addView(tr3);

                // WORKER INFO
                List<Worker> workers = data.getWorkers().getWorkers();
                for (Worker worker : workers) {

                    String name = "\nMiner: " + worker.getWorker_name();
                    String lastShare = "Last Share: " + Utils.dateFormat(activity, worker.getLast_share() * 1000);
                    String totalShares = "Total Shares: " + worker.getTotal_shares();

                    TableRow tr9 = new TableRow(activity);
                    TableRow tr12 = new TableRow(activity);
                    TableRow tr13 = new TableRow(activity);

                    TextView tvMinerName = new TextView(activity);
                    TextView tvLastShare = new TextView(activity);
                    TextView tvTotalShares = new TextView(activity);

                    tr9.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr12.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr13.setGravity(Gravity.CENTER_HORIZONTAL);

                    tvMinerName.setText(name);
                    tvLastShare.setText(lastShare);
                    tvTotalShares.setText(totalShares);

                    tr9.addView(tvMinerName);
                    tr12.addView(tvLastShare);
                    tr13.addView(tvTotalShares);

                    t1.addView(tr9);
                    t1.addView(tr12);
                    t1.addView(tr13);
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    private class MinerStatsThread extends Thread {

        @Override
        public void run() {
            getMinerStats();
            mMinerHandler.post(mGraphView);
        }
    }

}
