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
import com.veken0m.mining.btcguild.BTCGuild;
import com.veken0m.mining.btcguild.Worker;
import com.veken0m.utils.CurrencyUtils;
import com.veken0m.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStreamReader;
import java.util.List;

public class BTCGuildFragment extends Fragment
{
    private static String pref_btcguildKey = "";
    private static int pref_widgetMiningPayoutUnit = 0;
    private static BTCGuild data = null;
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

    public BTCGuildFragment() { }

    private static void readPreferences(Context context)
    {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_btcguildKey = prefs.getString("btcguildKey", "");
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

            HttpGet post = new HttpGet("https://www.btcguild.com/api.php?api_key=" + pref_btcguildKey);
            HttpResponse response = client.execute(post);

            ObjectMapper mapper = new ObjectMapper();
            data = mapper.readValue(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), BTCGuild.class);

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
            String text = String.format(res.getString(R.string.error_minerConnection), "BTCGuild");
            text += "\n\n*NOTE* BTC Guild limits calls to once every 15 seconds";
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

                Activity activity = getActivity();

                TableRow tr1 = new TableRow(activity);
                TableRow tr2 = new TableRow(activity);
                TableRow tr3 = new TableRow(activity);

                TextView tvBTCRewards = new TextView(activity);
                TextView tvNMCRewards = new TextView(activity);
                TextView tvTotalHashrate = new TextView(activity);

                tr1.setGravity(Gravity.CENTER_HORIZONTAL);
                tr2.setGravity(Gravity.CENTER_HORIZONTAL);
                tr3.setGravity(Gravity.CENTER_HORIZONTAL);

                String RewardsBTC = "BTC Reward: "
                        + CurrencyUtils.formatPayout(data.getUser().getUnpaid_rewards(), pref_widgetMiningPayoutUnit, "BTC");
                String RewardsNMC = "NMC Reward: "
                        + CurrencyUtils.formatPayout(data.getUser().getUnpaid_rewards_nmc(), pref_widgetMiningPayoutUnit, "NMC");

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
                for (Worker worker : workers)
                {
                    TableRow tr8 = new TableRow(activity);
                    TableRow tr9 = new TableRow(activity);
                    TableRow tr11 = new TableRow(activity);
                    TableRow tr12 = new TableRow(activity);

                    TextView tvMinerName = new TextView(activity);
                    TextView tvHashrate = new TextView(activity);
                    TextView tvShares = new TextView(activity);
                    TextView tvStales = new TextView(activity);

                    tr8.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr9.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr11.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr12.setGravity(Gravity.CENTER_HORIZONTAL);

                    tvMinerName.setText("Miner: " + worker.getWorker_name());
                    tvHashrate.setText("Hashrate: "
                            + Utils.formatDecimal(worker.getHash_rate(), 2, 0, false)
                            + " MH/s");
                    tvShares.setText("Shares: "
                            + Utils.formatDecimal(worker.getValid_shares(), 0, 0,
                            true));
                    tvStales.setText("Stales: "
                            + Utils.formatDecimal(worker.getStale_shares(), 0, 0,
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
            }
            catch (Exception e)
            {
                e.printStackTrace();
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
