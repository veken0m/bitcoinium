
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veken0m.bitcoinium.R;
import com.veken0m.mining.eligius.Eligius;
import com.veken0m.mining.eligius.EligiusBalance;
import com.veken0m.mining.eligius.TimeInterval;
import com.veken0m.utils.CurrencyUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStreamReader;
import java.util.ArrayList;


public class EligiusFragment extends SherlockFragment {

    private static String pref_eligiusKey = "";
    private static int pref_widgetMiningPayoutUnit = 0;
    private static Eligius data = null;
    private static EligiusBalance balanceData = null;
    private Boolean connectionFail = false;
    private ProgressDialog minerProgressDialog;
    private final Handler mMinerHandler = new Handler();

    public EligiusFragment() {
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

            // Test key
            //pref_eligiusKey = "1EXfBqvLTyFbL6Dr5CG1fjxNKEPSezg7yF";

            HttpGet post = new HttpGet("http://eligius.st/~wizkid057/newstats/hashrate-json.php/"
                    + pref_eligiusKey);

            HttpResponse response = client.execute(post);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Include.NON_NULL);

            data = mapper.readValue(new InputStreamReader(response.getEntity()
                    .getContent(), "UTF-8"), Eligius.class);

            post = new HttpGet("http://eligius.st/~luke-jr/balance.php?addr="
                    + pref_eligiusKey);

            balanceData = mapper
                    .readValue(new InputStreamReader(client.execute(post)
                            .getEntity().getContent(), "UTF-8"),
                            EligiusBalance.class);

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
            safelyDismiss(minerProgressDialog);
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
            String text = String.format(res.getString(R.string.minerConnectionError), "Eligius");
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
                TableLayout t1 = (TableLayout) view.findViewById(R.id.minerStatlist);

                TableRow tr1 = new TableRow(getActivity());
                TableRow tr2 = new TableRow(getActivity());
                TableRow tr3 = new TableRow(getActivity());

                TextView tvConfirmed_reward = new TextView(getActivity());
                TextView tvEstimated_reward = new TextView(getActivity());

                tr1.setGravity(Gravity.CENTER_HORIZONTAL);
                tr2.setGravity(Gravity.CENTER_HORIZONTAL);
                tr3.setGravity(Gravity.CENTER_HORIZONTAL);


                String confirmed_reward = "Confirmed Reward: ";
                String estimated_reward = "\nEstimated Reward: ";
                // USER INFO
                //if(balanceData.getConfirmed() != null && balanceData.getExpected() != null){
                confirmed_reward += CurrencyUtils.formatPayout(balanceData.getConfirmed() / 100000000, pref_widgetMiningPayoutUnit, "BTC");
                estimated_reward += CurrencyUtils.formatPayout(balanceData.getExpected() / 100000000, pref_widgetMiningPayoutUnit, "BTC");
                //} else {
                //    confirmed_reward += "N/A";
                //    estimated_reward += "N/A";
                //}

                tvConfirmed_reward.setText(confirmed_reward);
                tvEstimated_reward.setText(estimated_reward);

                tr2.addView(tvEstimated_reward);
                tr3.addView(tvConfirmed_reward);

                t1.addView(tr1);
                t1.addView(tr2);
                t1.addView(tr3);

                // WORKER INFO
                ArrayList<TimeInterval> intervals = new ArrayList<TimeInterval>();

                intervals.add(data.get128());
                intervals.add(data.get256());
                intervals.add(data.get1350());
                intervals.add(data.get10800());
                intervals.add(data.get43200());

                for (TimeInterval timeInterval : intervals) {

                    String name = "\nInterval: " + timeInterval.getInterval_name();
                    float hashRatef = timeInterval.getHashrate() / 1000000;
                    String minerHashrate = "Hashrate: " + hashRatef + " MH/s";
                    String shares = "Shares: " + timeInterval.getShares();

                    TableRow tr9 = new TableRow(getActivity());
                    TableRow tr10 = new TableRow(getActivity());
                    TableRow tr11 = new TableRow(getActivity());

                    tr9.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr10.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr11.setGravity(Gravity.CENTER_HORIZONTAL);

                    TextView tvMinerName = new TextView(getActivity());
                    TextView tvMinerHashrate = new TextView(getActivity());
                    TextView tvShares = new TextView(getActivity());

                    tvMinerName.setText(name);
                    tvMinerHashrate.setText(minerHashrate);
                    tvShares.setText(shares);

                    if (hashRatef > 0) {
                        tvMinerName.setTextColor(Color.GREEN);
                    } else {
                        tvMinerName.setTextColor(Color.RED);
                    }

                    tr9.addView(tvMinerName);
                    tr10.addView(tvMinerHashrate);
                    tr11.addView(tvShares);

                    t1.addView(tr9);
                    t1.addView(tr10);
                    t1.addView(tr11);
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    private static void readPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_eligiusKey = prefs.getString("eligiusKey", "");
        pref_widgetMiningPayoutUnit = Integer.parseInt(prefs.getString("widgetMiningPayoutUnitPref", "0"));
    }

}
