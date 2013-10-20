
package com.veken0m.bitcoinium;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veken0m.bitcoinium.utils.Utils;
import com.veken0m.mining.bitminter.BitMinterData;
import com.veken0m.mining.btcguild.BTCGuild;
import com.veken0m.mining.deepbit.DeepBitData;
import com.veken0m.mining.eligius.Eligius;
import com.veken0m.mining.eligius.EligiusBalance;
import com.veken0m.mining.emc.EMC;
import com.veken0m.mining.fiftybtc.FiftyBTC;
import com.veken0m.mining.fiftybtc.Worker;
import com.veken0m.mining.slush.Slush;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.List;

public class MinerWidgetProvider extends BaseWidgetProvider {

    private static String pref_apiKey;
    private static float hashRate;
    private static String hashRateString = "";
    private static String btcBalance;
    private static Boolean alive;
    private static int NOTIFY_ID;
    private static Boolean pref_minerDownAlert;

    @Override
    public void onReceive(Context ctxt, Intent intent) {

        if (REFRESH.equals(intent.getAction())) {
            setAlarm(ctxt);
        } else {
            super.onReceive(ctxt, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        setAlarm(context);
    }

    /**
     * This class lets us refresh the widget whenever we want to
     */
    public static class MinerUpdateService extends IntentService {

        public void buildUpdate(Context context) {
            AppWidgetManager widgetManager = AppWidgetManager
                    .getInstance(context);
            ComponentName widgetComponent = new ComponentName(context,
                    MinerWidgetProvider.class);
            int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);

            readGeneralPreferences(context);

            if (!pref_wifionly || checkWiFiConnected(context)) {

                for (int appWidgetId : widgetIds) {
                    
                    // Load Widget preferences
                    String pref_miningpool = MinerWidgetConfigureActivity
                            .loadMiningPoolPref(context, appWidgetId);

                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(context);
                    
                    PendingIntent pendingIntent;
                    if (pref_tapToUpdate) {
                        Intent intent = new Intent(this, WidgetProvider.class);
                        intent.setAction(REFRESH);
                        pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, 0);
                    } else {
                        Intent intent = new Intent(context, MinerStatsActivity.class);
                        Bundle tabSelection = new Bundle();
                        tabSelection.putString("poolKey", pref_miningpool);
                        intent.putExtras(tabSelection);
                        pendingIntent = PendingIntent.getActivity(
                                context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    RemoteViews views = new RemoteViews(
                            context.getPackageName(), R.layout.minerappwidget);
                    views.setOnClickPendingIntent(R.id.widgetMinerButton,
                            pendingIntent);


                    pref_minerDownAlert = prefs.getBoolean(
                            pref_miningpool.toLowerCase() + "AlertPref", false);

                    if (getMinerInfo(pref_miningpool)) {
                        // Switch to GH/s if over 3 digits to fit in widget
                        String hashRateAdjusted;
                        DecimalFormat df = new DecimalFormat("#0.00");
                        
                        if (hashRate > 999) {
                            hashRateAdjusted = "" + df.format((hashRate / 1000)) + " GH/s";
                        } else {
                            hashRateAdjusted = "" + df.format((hashRate)) + " MH/s";
                        }

                        if (pref_miningpool.equalsIgnoreCase("EclipseMC")) {
                        	views.setTextViewText(R.id.widgetMinerHashrate, hashRateString);
                        } else {
                        	views.setTextViewText(R.id.widgetMinerHashrate, hashRateAdjusted);
                        }
                        
                        views.setTextViewText(R.id.widgetMiner, pref_miningpool);
                        views.setTextViewText(R.id.widgetBTCPayout, btcBalance
                                + " BTC");

                        if (!alive && pref_minerDownAlert) {
                            createMinerDownNotification(context,
                                    pref_miningpool, NOTIFY_ID * 10);
                        }
                        
                        String refreshedTime = "Upd. @ "
                                + Utils.getCurrentTime(context);
                        views.setTextViewText(R.id.refreshtime, refreshedTime);
                        
                        updateWidgetTheme(views);

                    } else {
                        if (pref_enableWidgetCustomization) {
                            views.setTextColor(R.id.refreshtime,
                                    pref_widgetRefreshFailedColor);
                        } else {
                            views.setTextColor(R.id.refreshtime, Color.RED);
                        }
                    }
                    widgetManager.updateAppWidget(appWidgetId, views);
                }
            }
        }

        public Boolean getMinerInfo(String miningpool) {

            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());

            HttpClient client = new DefaultHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            try {
                
                if (miningpool.equalsIgnoreCase("DeepBit")) {
                    pref_apiKey = prefs.getString("deepbitKey", "");

                    HttpGet post = new HttpGet("http://deepbit.net/api/"
                            + pref_apiKey);

                    HttpResponse response = client.execute(post);
                    response = client.execute(post);
                    DeepBitData data = mapper.readValue(new InputStreamReader(response
                            .getEntity().getContent(), "UTF-8"),
                            DeepBitData.class);
                    btcBalance = data.getConfirmed_reward().toString();
                    hashRate = data.getHashrate().floatValue();
                    alive = data.getWorkers().getWorker(0).getAlive();
                    NOTIFY_ID = 1;
                    return true;
                    
                } else if (miningpool.equalsIgnoreCase("BitMinter")) {
                    pref_apiKey = prefs.getString("bitminterKey", "");

                    HttpGet post = new HttpGet(
                            "https://bitminter.com/api/users" + "?key="
                                    + pref_apiKey);

                    HttpResponse response = client.execute(post);
                    response = client.execute(post);
                    BitMinterData data = mapper.readValue(new InputStreamReader(response
                            .getEntity().getContent(), "UTF-8"),
                            BitMinterData.class);
                    btcBalance = "" + data.getBalances().getBTC();
                    hashRate = data.getHash_rate().floatValue();
                    alive = data.getWorkers().get(0).getAlive();
                    NOTIFY_ID = 2;
                    return true;
                    
                } else if (miningpool.equalsIgnoreCase("EclipseMC")) {

                    pref_apiKey = prefs.getString("emcKey", "");
                    HttpGet post = new HttpGet(
                            "https://eclipsemc.com/api.php?key=" + pref_apiKey
                                    + "&action=userstats");

                    HttpResponse response = client.execute(post);
                    response = client.execute(post);
                    EMC data = mapper.readValue(new InputStreamReader(response
                            .getEntity().getContent(), "UTF-8"), EMC.class);
                    btcBalance = data.getData().getUser()
                            .getConfirmed_rewards();
                    hashRateString = data.getWorkers().get(0).getHash_rate();
                    hashRate = 0.0f;
                    alive = true;
                    NOTIFY_ID = 3;
                    return true;
                    
                } else if (miningpool.equalsIgnoreCase("Slush")) {
                    pref_apiKey = prefs.getString("slushKey", "");

                    HttpGet post = new HttpGet(
                            "https://mining.bitcoin.cz/accounts/profile/json/"
                                    + pref_apiKey);

                    HttpResponse response = client.execute(post);
                    Slush data = mapper.readValue(new InputStreamReader(response
                            .getEntity().getContent(), "UTF-8"), Slush.class);
                    btcBalance = data.getConfirmed_reward();
                    hashRate = Float.parseFloat(data.getHashrate());
                    alive = data.getWorkers().getWorker(0).getAlive();
                    NOTIFY_ID = 4;
                    return true;
                    
                } else if (miningpool.equalsIgnoreCase("50BTC")) {
                    pref_apiKey = prefs.getString("50BTCKey", "");

                    HttpGet post = new HttpGet("https://50btc.com/en/api/"
                            + pref_apiKey + "?text=1");
                    HttpResponse response = client.execute(post);
                    FiftyBTC data = mapper
                            .readValue(new InputStreamReader(response
                                    .getEntity().getContent(), "UTF-8"),
                                    FiftyBTC.class);
                    btcBalance = data.getUser().getConfirmed_rewards()
                            .toString();
                    hashRate = 0.0f;
                    
                    List<Worker> workers = data.getWorkers().getWorkers();
                    for (int i = 0; i < workers.size(); i++) {
                        hashRate += Float.parseFloat(workers.get(i).getHash_rate());
                    }
                    
                    alive = data.getWorkers().getWorker(0).getAlive();
                    NOTIFY_ID = 5;
                    return true;
                    
                } else if (miningpool.equalsIgnoreCase("BTCGuild")) {
                    pref_apiKey = prefs.getString("btcguildKey", "");

                    HttpGet post = new HttpGet("https://www.btcguild.com/api.php?api_key="
                            + pref_apiKey);
                    HttpResponse response = client.execute(post);
                    BTCGuild data = mapper
                            .readValue(new InputStreamReader(response
                                    .getEntity().getContent(), "UTF-8"),
                                    BTCGuild.class);
                    btcBalance = data.getUser().getUnpaid_rewards()
                            .toString();
                    hashRate = 0.0f;
                    
                    List<com.veken0m.mining.btcguild.Worker> workers = data.getWorkers().getWorkers();
                    for (int i = 0; i < workers.size(); i++) {
                        hashRate += workers.get(i).getHash_rate().floatValue();
                    }
                    
                    alive = true;
                    NOTIFY_ID = 6;
                    return true;
                } else if (miningpool.equalsIgnoreCase("Eligius")){

                        pref_apiKey = prefs.getString("eligiusKey", "");

                        HttpGet post = new HttpGet("http://eligius.st/~wizkid057/newstats/hashrate-json.php/"
                                + pref_apiKey);
                        HttpResponse response = client.execute(post);
                        Eligius data = mapper
                                .readValue(new InputStreamReader(response
                                        .getEntity().getContent(), "UTF-8"),
                                        Eligius.class);
                        
                        hashRate = data.get256().getHashrate().floatValue()/1000000;
                        
                        post = new HttpGet("http://eligius.st/~luke-jr/balance.php?addr="
                                + pref_apiKey);
                        EligiusBalance data2 = mapper
                                .readValue(new InputStreamReader(client.execute(post)
                                        .getEntity().getContent(), "UTF-8"),
                                        EligiusBalance.class);
                        
                        btcBalance = "" + data2.getConfirmed().floatValue()/100000000;
    
                        alive = (hashRate > 0.0);
                        NOTIFY_ID = 7;
                        return true;      
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return false;

        }
        
        public void updateWidgetTheme(RemoteViews views){
            // set the color
            if (pref_enableWidgetCustomization) {
                views.setInt(R.id.minerwidget_layout,
                        "setBackgroundColor",
                        pref_backgroundWidgetColor);
                views.setTextColor(R.id.widgetMinerHashrate,
                        pref_mainWidgetTextColor);
                views.setTextColor(R.id.widgetMiner,
                        pref_mainWidgetTextColor);
                views.setTextColor(R.id.refreshtime,
                        pref_widgetRefreshSuccessColor);
                views.setTextColor(R.id.widgetBTCPayout, pref_secondaryWidgetTextColor);
                
            } else {
                views.setInt(
                        R.id.minerwidget_layout,
                        "setBackgroundColor",
                        getResources().getColor(
                                R.color.widgetBackgroundColor));
                views.setTextColor(
                        R.id.widgetMinerHashrate,
                        getResources().getColor(
                                R.color.widgetMainTextColor));
                views.setTextColor(
                        R.id.widgetMiner,
                        getResources().getColor(
                                R.color.widgetMainTextColor));
                
                views.setTextColor(R.id.widgetBTCPayout, Color.LTGRAY);
                views.setTextColor(R.id.refreshtime, Color.GREEN);
            }
        }

        public MinerUpdateService() {
            super("MinerWidgetProvider$MinerUpdateService");
        }

        @Override
        public void onCreate() {
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            super.onStartCommand(intent, flags, startId);
            return START_STICKY;
        }

        @Override
        public void onHandleIntent(Intent intent) {
            buildUpdate(this);
        }
    }

}
