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
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.veken0m.bitcoinium.R;
import com.veken0m.utils.CurrencyUtils;
import com.veken0m.utils.Utils;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.cexio.CexIOExchange;
import com.xeiam.xchange.cexio.dto.account.CexIOBalance;
import com.xeiam.xchange.cexio.dto.account.CexIOBalanceInfo;
import com.xeiam.xchange.cexio.dto.account.GHashIOHashrate;
import com.xeiam.xchange.cexio.dto.account.GHashIOWorker;
import com.xeiam.xchange.cexio.service.polling.CexIOAccountServiceRaw;

import java.util.Map;

public class GHashIOFragment extends Fragment {

    private static String pref_ghashioUsername = "";
    private static String pref_ghashioAPIKey = "";
    private static String pref_ghashioSecretKey = "";
    private static int pref_widgetMiningPayoutUnit = 0;
    private static GHashIOHashrate hashrate = null;
    private static Map<String, GHashIOWorker> workers = null;
    private static CexIOBalanceInfo account = null;
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

    public GHashIOFragment() {
    }

    private static void readPreferences(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        pref_ghashioUsername = prefs.getString("ghashioUsername", "");
        pref_ghashioAPIKey = prefs.getString("ghashioAPIKey", "");
        pref_ghashioSecretKey = prefs.getString("ghashioSecretKey", "");
        pref_widgetMiningPayoutUnit = Integer.parseInt(prefs.getString("widgetMiningPayoutUnitPref", "0"));
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

            Exchange cexioExchange = ExchangeFactory.INSTANCE.createExchange(CexIOExchange.class.getName());

            ExchangeSpecification specs = new ExchangeSpecification(CexIOExchange.class.getName());
            specs.setApiKey(pref_ghashioAPIKey);
            specs.setSecretKey(pref_ghashioSecretKey);
            specs.setUserName(pref_ghashioUsername);
            cexioExchange.applySpecification(specs);

            CexIOAccountServiceRaw pollingService = (CexIOAccountServiceRaw) cexioExchange.getPollingAccountService();
            account = pollingService.getCexIOAccountInfo();
            hashrate = pollingService.getHashrate();

            try {
                workers = pollingService.getWorkers();
            } catch (Exception e) {
                e.printStackTrace();
                // no workers... suppress
            }

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

            final Context context = getActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            Resources res = getResources();
            String text = String.format(res.getString(R.string.minerConnectionError), "GHash.IO");
            builder.setMessage(text);
            builder.setPositiveButton(R.string.OK,
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
                TableLayout t1 = (TableLayout) view.findViewById(R.id.minerStatlist);

                Activity activity = getActivity();

                TableRow tr1 = new TableRow(activity);
                TableRow tr2 = new TableRow(activity);
                TableRow tr3 = new TableRow(activity);
                TableRow tr4 = new TableRow(activity);
                TableRow tr5 = new TableRow(activity);

                TextView tvBTCRewards = new TextView(activity);
                TextView tvLTCRewards = new TextView(activity);
                TextView tvNMCRewards = new TextView(activity);
                TextView tvGHSRewards = new TextView(activity);
                TextView tvTotalHashrate = new TextView(activity);

                tr1.setGravity(Gravity.CENTER_HORIZONTAL);
                tr2.setGravity(Gravity.CENTER_HORIZONTAL);
                tr3.setGravity(Gravity.CENTER_HORIZONTAL);
                tr4.setGravity(Gravity.CENTER_HORIZONTAL);
                tr5.setGravity(Gravity.CENTER_HORIZONTAL);

                CexIOBalance balanceBTC = account.getBalanceBTC();
                CexIOBalance balanceLTC = account.getBalanceLTC();
                CexIOBalance balanceNMC = account.getBalanceNMC();
                CexIOBalance balanceGHS = account.getBalanceGHS();

                String RewardsBTC = "BTC Balance: ";
                String RewardsLTC = "LTC Balance: ";
                String RewardsNMC = "NMC Balance: ";
                String RewardsGHS = "GHS Balance: ";

                float hashRate = hashrate.getLast15m().floatValue();

                if (balanceBTC != null) {
                    RewardsBTC += CurrencyUtils.formatPayout(account.getBalanceBTC().getAvailable().floatValue(), pref_widgetMiningPayoutUnit, "BTC");
                    tvBTCRewards.setText(RewardsBTC);
                    tr1.addView(tvBTCRewards);
                    t1.addView(tr1);
                }
                if (balanceLTC != null) {
                    RewardsLTC += CurrencyUtils.formatPayout(account.getBalanceLTC().getAvailable().floatValue(), pref_widgetMiningPayoutUnit, "LTC");
                    tvLTCRewards.setText(RewardsLTC);
                    tr2.addView(tvLTCRewards);
                    t1.addView(tr2);
                }
                if (balanceGHS != null) {
                    RewardsGHS += CurrencyUtils.formatPayout(account.getBalanceGHS().getAvailable().floatValue(), pref_widgetMiningPayoutUnit, "GHS");
                    tvGHSRewards.setText(RewardsGHS);
                    tr4.addView(tvGHSRewards);
                    t1.addView(tr4);
                }
                if (balanceNMC != null) {
                    RewardsNMC += CurrencyUtils.formatPayout(account.getBalanceNMC().getAvailable().floatValue(), pref_widgetMiningPayoutUnit, "NMC");
                    tvNMCRewards.setText(RewardsNMC);
                    tr5.addView(tvNMCRewards);
                    t1.addView(tr5);
                }

                tvTotalHashrate.setText("Total Hashrate: " + hashRate + " MH/s");
                tr3.addView(tvTotalHashrate);
                t1.addView(tr3);

                if (workers != null) {
                    // End of Non-worker data
                    for (Map.Entry<String, GHashIOWorker> worker : workers.entrySet()) {
                        TableRow tr8 = new TableRow(activity);
                        TableRow tr9 = new TableRow(activity);
                        TableRow tr11 = new TableRow(activity);
                        TableRow tr12 = new TableRow(activity);

                        TextView tvMinerName = new TextView(activity);
                        TextView tvHashrate = new TextView(activity);
                        TextView tvStales = new TextView(activity);

                        tr8.setGravity(Gravity.CENTER_HORIZONTAL);
                        tr9.setGravity(Gravity.CENTER_HORIZONTAL);
                        tr11.setGravity(Gravity.CENTER_HORIZONTAL);
                        tr12.setGravity(Gravity.CENTER_HORIZONTAL);

                        float hashratef = worker.getValue().getLast15m().floatValue();
                        tvMinerName.setText("\nMiner: " + worker.getKey());
                        tvHashrate.setText("Hashrate: "
                                + Utils.formatDecimal(worker.getValue().getLast15m().floatValue(), 2, 0, false)
                                + " MH/s");
                        tvStales.setText("Stales: "
                                + Utils.formatDecimal(worker.getValue().getRejected().getStale().floatValue(), 0, 0,
                                true) + "\n");

                        tvMinerName.setTextColor((hashratef > 0) ? Color.GREEN : Color.RED);

                        tr8.addView(tvMinerName);
                        tr9.addView(tvHashrate);
                        tr12.addView(tvStales);

                        t1.addView(tr8);
                        t1.addView(tr9);
                        t1.addView(tr11);
                        t1.addView(tr12);
                    }
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
