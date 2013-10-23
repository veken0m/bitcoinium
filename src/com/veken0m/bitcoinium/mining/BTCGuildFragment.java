
package com.veken0m.bitcoinium.mining;

import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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
import com.veken0m.bitcoinium.utils.Utils;
import com.veken0m.mining.btcguild.BTCGuild;
import com.veken0m.mining.btcguild.Worker;

public class BTCGuildFragment extends SherlockFragment {

    protected static String pref_btcguildKey = "";
    protected static BTCGuild data;
    protected Boolean connectionFail = false;
    private ProgressDialog minerProgressDialog;
    final Handler mMinerHandler = new Handler();

    public BTCGuildFragment() {
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
    
    public void onPause(){
        super.onPause();
        mMinerHandler.removeCallbacks(mGraphView);
        minerProgressDialog.dismiss();
    }

    public void getMinerStats(Context context) {

        try {
            HttpClient client = new DefaultHttpClient();

            HttpGet post = new HttpGet("https://www.btcguild.com/api.php?api_key=" + pref_btcguildKey);
            HttpResponse response = client.execute(post);

            ObjectMapper mapper = new ObjectMapper();
            data = mapper.readValue(new InputStreamReader(response.getEntity()
                    .getContent(), "UTF-8"), BTCGuild.class);

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
                    + "BTCGuild"
                    + "\n\nPlease make sure that your API Token is entered correctly and that 3G or Wifi is working properly. \n\n*NOTE* BTC Guild limits calls to once every 15 seconds");
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

        View view = getView();

        if (view != null) {
            try {
                TableLayout t1 = (TableLayout) view.findViewById(
                        R.id.minerStatlist);

                TableRow tr1 = new TableRow(getActivity());
                TableRow tr2 = new TableRow(getActivity());
                TableRow tr3 = new TableRow(getActivity());

                TextView tvBTCRewards = new TextView(getActivity());
                TextView tvNMCRewards = new TextView(getActivity());
                TextView tvTotalHashrate = new TextView(getActivity());

                tr1.setGravity(Gravity.CENTER_HORIZONTAL);
                tr2.setGravity(Gravity.CENTER_HORIZONTAL);
                tr3.setGravity(Gravity.CENTER_HORIZONTAL);

                String RewardsBTC = "BTC Reward: " + data.getUser().getUnpaid_rewards()
                        + " BTC";
                String RewardsNMC = "NMC Reward: " + data.getUser().getUnpaid_rewards_nmc()
                        + " NMC";

                tvBTCRewards.setText(RewardsBTC);
                tvNMCRewards.setText(RewardsNMC);

                tr1.addView(tvBTCRewards);
                tr2.addView(tvNMCRewards);
                tr3.addView(tvTotalHashrate);

                t1.addView(tr1);
                t1.addView(tr2);
                t1.addView(tr3);

                // End of Non-worker data
                List<Worker> workers = data.getWorkers().getWorkers();
                for (int i = 0; i < workers.size(); i++) {
                    TableRow tr8 = new TableRow(getActivity());
                    TableRow tr9 = new TableRow(getActivity());
                    TableRow tr11 = new TableRow(getActivity());
                    TableRow tr12 = new TableRow(getActivity());

                    TextView tvMinerName = new TextView(getActivity());
                    TextView tvHashrate = new TextView(getActivity());
                    TextView tvShares = new TextView(getActivity());
                    TextView tvStales = new TextView(getActivity());

                    tr8.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr9.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr11.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr12.setGravity(Gravity.CENTER_HORIZONTAL);

                    tvMinerName.setText("Miner: " + workers.get(i).getWorker_name());
                    tvHashrate.setText("Hashrate: "
                            + Utils.formatDecimal(workers.get(i).getHash_rate()
                                    .floatValue(), 2, false) + " MH/s");
                    tvShares.setText("Shares: "
                            + Utils.formatDecimal(workers.get(i).getValid_shares().floatValue(), 0,
                                    true));
                    tvStales.setText("Stales: "
                            + Utils.formatDecimal(workers.get(i).getStale_shares().floatValue(), 0,
                                    true) + "\n");

                    tr8.addView(tvMinerName);
                    tr9.addView(tvHashrate);
                    tr11.addView(tvShares);
                    tr12.addView(tvStales);

                    t1.addView(tr8);
                    t1.addView(tr9);
                    t1.addView(tr11);
                    t1.addView(tr12);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected static void readPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_btcguildKey = prefs.getString("btcguildKey", "");
    }

}
