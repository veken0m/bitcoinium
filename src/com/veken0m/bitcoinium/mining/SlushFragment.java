
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
import android.text.format.DateFormat;
import android.util.Log;
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
import com.veken0m.mining.slush.Slush;
import com.veken0m.mining.slush.Worker;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.List;

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

        View view = inflater.inflate(R.layout.table_fragment, container, false);
        viewMinerStats(view);
        return view;
    }

    public void getMinerStats(Context context) {

        try {
            HttpClient client = new DefaultHttpClient();

            HttpGet post = new HttpGet(
                    "https://mining.bitcoin.cz/accounts/profile/json/"
                            + pref_slushKey);
            HttpResponse response = client.execute(post);
            ObjectMapper mapper = new ObjectMapper();
            
            // Testing from raw resource
            //InputStream raw = getResources().openRawResource(R.raw.slush);
            //Reader is = new BufferedReader(new InputStreamReader(raw, "UTF8"));
            //data = mapper.readValue(is, Slush.class);
            
            data = mapper.readValue(new InputStreamReader(response.getEntity()
                    .getContent(), "UTF-8"), Slush.class);

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

            TextView tvConfirmed_nmc_reward = new TextView(getActivity());
            TextView tvConfirmed_reward = new TextView(getActivity());
            TextView tvEstimated_reward = new TextView(getActivity());
            TextView tvHashrate = new TextView(getActivity());
            // TextView tvRating = new TextView(getActivity());
            TextView tvUnconfirmed_nmc_reward = new TextView(getActivity());
            TextView tvUnconfirmed_reward = new TextView(getActivity());
            TextView tvUsername = new TextView(getActivity());
            // TextView tvSend_threshold = new TextView(getActivity());
            // TextView tvWallet = new TextView(getActivity());
            // TextView tvNmc_send_threshold = new TextView(getActivity());
            // TextView tvWorkers = new TextView(getActivity());

            tr1.setGravity(Gravity.CENTER_HORIZONTAL);
            tr2.setGravity(Gravity.CENTER_HORIZONTAL);
            tr3.setGravity(Gravity.CENTER_HORIZONTAL);
            tr4.setGravity(Gravity.CENTER_HORIZONTAL);
            tr5.setGravity(Gravity.CENTER_HORIZONTAL);
            tr6.setGravity(Gravity.CENTER_HORIZONTAL);
            tr7.setGravity(Gravity.CENTER_HORIZONTAL);

            // USER INFO
            String hashrate = "Total Hashrate: " + data.getHashrate() + " MH/s";
            String confirmed_reward = "Confirmed: "
                    + data.getConfirmed_reward() + " BTC";
            String estimated_reward = "Estimated: "
                    + data.getEstimated_reward() + " BTC";
            String confirmed_nmc_reward = "Confirmed: "
                    + data.getConfirmed_nmc_reward() + " NMC";
            String unconfirmed_reward = "Unconfirmed: "
                    + data.getUnconfirmed_reward() + " BTC";
            String unconfirmed_nmc_reward = "Unconfirmed: "
                    + data.getUnconfirmed_nmc_reward() + " NMC";
            String username = "Username: " + data.getUsername();
            // String rating = "Rating: " + data.getRating();
            // String nmc_send_threshold = "Send Threshold: " +
            // data.getNmc_send_threshold() + " NMC";
            // String send_threshold = "Send Threshold: " +
            // data.getSend_threshold() + " BTC";
            // String wallet = "Wallet: " + data.getWallet();

            tvHashrate.setText(hashrate);
            tvConfirmed_nmc_reward.setText(confirmed_nmc_reward);
            tvConfirmed_reward.setText(confirmed_reward);
            tvEstimated_reward.setText(estimated_reward);
            tvUnconfirmed_nmc_reward.setText(unconfirmed_nmc_reward);
            tvUnconfirmed_reward.setText(unconfirmed_reward);
            tvUsername.setText(username);
            // tvWallet.setText(wallet);
            // tvNmc_send_threshold.setText(nmc_send_threshold);
            // tvRating.setText(rating);
            // tvSend_threshold.setText(send_threshold);

            tr1.addView(tvUsername);
            tr2.addView(tvEstimated_reward);
            tr3.addView(tvConfirmed_reward);
            tr4.addView(tvConfirmed_nmc_reward);
            tr5.addView(tvUnconfirmed_reward);
            tr6.addView(tvUnconfirmed_nmc_reward);
            tr7.addView(tvHashrate);
            // tr10.addView(tvSend_threshold);
            // tr5.addView(tvWallet);

            t1.addView(tr1);
            t1.addView(tr2);
            t1.addView(tr3);
            t1.addView(tr4);
            t1.addView(tr5);
            t1.addView(tr7);

            // WORKER INFO
            List<Worker> workers = data.getWorkers().getWorkers();
            List<String> names = data.getWorkers().getNames();

            for (int i = 0; i < workers.size(); i++) {
                Worker worker = workers.get(i);

                String name = "\nMiner: " + names.get(i);
                String alive = "Alive: " + worker.getAlive();
                String minerHashrate = "Hashrate: " + worker.getHashrate()
                        + " MH/s";
                String shares = "Shares: " + worker.getShares().floatValue();
                String lastShare = "Last Share: "
                        + Utils.dateFormat(getActivity(), worker.getLast_share()*1000);
                String score = "Score: " + worker.getScore();

                TableRow tr9 = new TableRow(getActivity());
                TableRow tr10 = new TableRow(getActivity());
                TableRow tr11 = new TableRow(getActivity());
                TableRow tr12 = new TableRow(getActivity());
                TableRow tr13 = new TableRow(getActivity());
                TableRow tr14 = new TableRow(getActivity());

                tr9.setGravity(Gravity.CENTER_HORIZONTAL);
                tr10.setGravity(Gravity.CENTER_HORIZONTAL);
                tr11.setGravity(Gravity.CENTER_HORIZONTAL);
                tr12.setGravity(Gravity.CENTER_HORIZONTAL);
                tr13.setGravity(Gravity.CENTER_HORIZONTAL);
                tr14.setGravity(Gravity.CENTER_HORIZONTAL);

                TextView tvMinerName = new TextView(getActivity());
                TextView tvAlive = new TextView(getActivity());
                TextView tvMinerHashrate = new TextView(getActivity());
                TextView tvShares = new TextView(getActivity());
                TextView tvLastShare = new TextView(getActivity());
                TextView tvScore = new TextView(getActivity());

                tvMinerName.setText(name);
                tvAlive.setText(alive);
                tvMinerHashrate.setText(minerHashrate);
                tvShares.setText(shares);
                tvLastShare.setText(lastShare);
                tvScore.setText(score);

                if (worker.getAlive()) {
                    tvMinerName.setTextColor(Color.GREEN);
                } else {
                    tvMinerName.setTextColor(Color.RED);
                }

                tr9.addView(tvMinerName);
                tr14.addView(tvAlive);
                tr10.addView(tvMinerHashrate);
                tr11.addView(tvShares);
                tr12.addView(tvLastShare);
                tr13.addView(tvScore);

 
                t1.addView(tr9);
                t1.addView(tr14);
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

        pref_slushKey = prefs.getString("slushKey", "");
    }

}
