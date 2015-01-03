package com.veken0m.bitcoinium.fragments.mining;

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
import com.veken0m.mining.slush.Slush;
import com.veken0m.mining.slush.Worker;
import com.veken0m.utils.CurrencyUtils;
import com.veken0m.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStreamReader;
import java.util.List;

public class SlushFragment extends Fragment
{
    private static String pref_slushKey = "";
    private static int pref_widgetMiningPayoutUnit = 0;
    private static Slush data = null;
    private final Handler mMinerHandler = new Handler();
    private Boolean connectionFail = false;
    private ProgressDialog minerProgressDialog;
    private final Runnable mGraphView = new Runnable()
    {
        @Override
        public void run()
        {
            try
            {
                safelyDismiss(minerProgressDialog);
            } catch (Exception e)
            {
                // This happens when we try to show a dialog when app is not in the foreground. Suppress it for now
            }
            drawMinerUI();
        }
    };

    public SlushFragment()
    {
    }

    private static void readPreferences(Context context)
    {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_slushKey = prefs.getString("slushKey", "");
        pref_widgetMiningPayoutUnit = Integer.parseInt(prefs.getString("widgetMiningPayoutUnitPref", "0"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        readPreferences(getActivity());

        View view = inflater.inflate(R.layout.fragment_table, container, false);
        viewMinerStats(view);
        return view;
    }

    public void onPause()
    {
        super.onPause();
        mMinerHandler.removeCallbacks(mGraphView);
        minerProgressDialog.dismiss();
    }

    void getMinerStats()
    {
        try
        {
            HttpClient client = new DefaultHttpClient();

            HttpGet post = new HttpGet("https://mining.bitcoin.cz/accounts/profile/json/" + pref_slushKey);
            HttpResponse response = client.execute(post);
            ObjectMapper mapper = new ObjectMapper();

            // Testing from raw resource
            //InputStream raw = getResources().openRawResource(R.raw.slush);
            //Reader is = new BufferedReader(new InputStreamReader(raw, "UTF8"));
            //data = mapper.readValue(is, Slush.class);

            data = mapper.readValue(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), Slush.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            connectionFail = true;
        }
    }

    private void viewMinerStats(View view)
    {
        if (minerProgressDialog != null && minerProgressDialog.isShowing())
            return;

        Context context = view.getContext();
        if (context != null)
            minerProgressDialog = ProgressDialog.show(context, getString(R.string.working), getString(R.string.retreivingMinerStats), true, false);

        MinerStatsThread gt = new MinerStatsThread();
        gt.start();
    }

    private void safelyDismiss(ProgressDialog dialog)
    {
        if (dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }
        if (connectionFail)
        {
            final Context context = getActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            Resources res = getResources();
            String text = String.format(res.getString(R.string.error_minerConnection), "Slush");
            builder.setMessage(text);
            builder.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dialog.cancel();
                        }
                    }
            );

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    void drawMinerUI()
    {
        View view = getView();

        if (view != null)
        {
            try
            {
                TableLayout t1 = (TableLayout) view.findViewById(R.id.minerStatlist);

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
                        + CurrencyUtils.formatPayout(data.getConfirmed_reward(), pref_widgetMiningPayoutUnit, "BTC");
                String estimated_reward = "Estimated: "
                        + CurrencyUtils.formatPayout(data.getEstimated_reward(), pref_widgetMiningPayoutUnit, "BTC");
                String confirmed_nmc_reward = "Confirmed: "
                        + CurrencyUtils.formatPayout(data.getConfirmed_nmc_reward(), pref_widgetMiningPayoutUnit, "NMC");
                String unconfirmed_reward = "Unconfirmed: "
                        + CurrencyUtils.formatPayout(data.getUnconfirmed_reward(), pref_widgetMiningPayoutUnit, "BTC");
                String unconfirmed_nmc_reward = "Unconfirmed: "
                        + CurrencyUtils.formatPayout(data.getUnconfirmed_nmc_reward(), pref_widgetMiningPayoutUnit, "NMC");
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

                for (int i = 0; i < workers.size(); i++)
                {
                    Worker worker = workers.get(i);

                    String name = "\nMiner: " + names.get(i);
                    String alive = "Alive: " + worker.getAlive();
                    String minerHashrate = "Hashrate: " + worker.getHashrate()
                            + " MH/s";
                    String shares = "Shares: " + worker.getShares();
                    String lastShare = "Last Share: "
                            + Utils.dateFormat(getActivity(), worker.getLast_share() * 1000);
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

                    if (worker.getAlive())
                    {
                        tvMinerName.setTextColor(Color.GREEN);
                    }
                    else
                    {
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

            }
            catch (Exception e)
            {
                //e.printStackTrace();
            }
        }
    }

    private class MinerStatsThread extends Thread
    {
        @Override
        public void run()
        {
            getMinerStats();
            mMinerHandler.post(mGraphView);
        }
    }
}
