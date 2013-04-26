package com.veken0m.bitcoinium;

import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veken0m.bitcoinium.mining.bitminter.BitMinterData;
import com.veken0m.bitcoinium.mining.deepbit.DeepBitData;
import com.veken0m.bitcoinium.mining.emc.EMC;
import com.veken0m.bitcoinium.mining.fiftybtc.FiftyBTC;
import com.veken0m.bitcoinium.mining.slush.Slush;
import com.veken0m.bitcoinium.utils.Utils;

public class MinerWidgetProvider extends BaseWidgetProvider {

	private static String pref_apiKey;
	private static String hashRate;
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

			final Intent intent = new Intent(context, MainActivity.class);
			final PendingIntent pendingIntent = PendingIntent.getActivity(
					context, 0, intent, 0);

			readGeneralPreferences(context);

			if (!pref_wifionly || checkWiFiConnected(context)) {

				for (int appWidgetId : widgetIds) {

					RemoteViews views = new RemoteViews(
							context.getPackageName(), R.layout.minerappwidget);
					views.setOnClickPendingIntent(R.id.widgetMinerButton,
							pendingIntent);

					// Load Widget preferences
					String pref_miningpool = MinerWidgetConfigureActivity
							.loadMiningPoolPref(context, appWidgetId);

					SharedPreferences prefs = PreferenceManager
							.getDefaultSharedPreferences(context);

					pref_minerDownAlert = prefs.getBoolean(
							pref_miningpool.toLowerCase() + "AlertPref", false);

					if (getMinerInfo(pref_miningpool)) {

						views.setTextViewText(R.id.widgetMiner, pref_miningpool);
						views.setTextViewText(R.id.widgetMinerHashrate, ""
								+ hashRate + " MH/s");
						views.setTextViewText(R.id.widgetBTCPayout, btcBalance
								+ " BTC");

						if (!alive && pref_minerDownAlert) {
							createMinerDownNotification(context,
									pref_miningpool, NOTIFY_ID * 10);
						}

						String refreshedTime = "Ref. @ "
								+ Utils.getCurrentTime(context);
						views.setTextViewText(R.id.refreshtime, refreshedTime);
						views.setTextColor(R.id.refreshtime, Color.GREEN);

					} else {
						String refreshedTime = "Ref. @ "
								+ Utils.getCurrentTime(context);
						views.setTextViewText(R.id.refreshtime, refreshedTime);

						views.setTextColor(R.id.refreshtime, Color.RED);
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
					DeepBitData data = null;
					pref_apiKey = prefs.getString("deepbitKey", "");

					HttpGet post = new HttpGet("http://deepbit.net/api/"
							+ pref_apiKey);

					HttpResponse response = client.execute(post);
					response = client.execute(post);
					data = mapper.readValue(new InputStreamReader(response
							.getEntity().getContent(), "UTF-8"),
							DeepBitData.class);
					btcBalance = data.getConfirmed_reward().toString();
					hashRate = "" + data.getHashrate();
					alive = data.getWorkers().getWorker(0).getAlive();
					NOTIFY_ID = 1;
					return true;
				}

				if (miningpool.equalsIgnoreCase("BitMinter")) {
					BitMinterData data = null;
					pref_apiKey = prefs.getString("bitminterKey", "");

					HttpGet post = new HttpGet(
							"https://bitminter.com/api/users" + "?key="
									+ pref_apiKey);

					HttpResponse response = client.execute(post);
					response = client.execute(post);
					data = mapper.readValue(new InputStreamReader(response
							.getEntity().getContent(), "UTF-8"),
							BitMinterData.class);
					btcBalance = "" + data.getBalances().getBTC();
					hashRate = "" + data.getHash_rate();
					alive = data.getWorkers().get(0).getAlive();
					NOTIFY_ID = 2;
					return true;
				}

				if (miningpool.equalsIgnoreCase("EclipseMC")) {

					pref_apiKey = prefs.getString("emcKey", "");
					HttpGet post = new HttpGet(
							"https://eclipsemc.com/api.php?key=" + pref_apiKey
									+ "&action=userstats");

					EMC data = null;
					HttpResponse response = client.execute(post);
					response = client.execute(post);
					data = mapper.readValue(new InputStreamReader(response
							.getEntity().getContent(), "UTF-8"), EMC.class);
					btcBalance = data.getData().getUser()
							.getConfirmed_rewards();
					hashRate = data.getWorkers().get(0).getHash_rate();
					alive = true; // TODO: Look up "Alive" info from EclipseMC
					NOTIFY_ID = 3;
					return true;
				}

				if (miningpool.equalsIgnoreCase("Slush")) {
					pref_apiKey = prefs.getString("slushKey", "");

					HttpGet post = new HttpGet(
							"https://mining.bitcoin.cz/accounts/profile/json/"
									+ pref_apiKey);
					Slush data = null;
					HttpResponse response = client.execute(post);
					data = mapper.readValue(new InputStreamReader(response
							.getEntity().getContent(), "UTF-8"), Slush.class);
					btcBalance = data.getConfirmed_reward();
					hashRate = data.getHashrate();
					alive = data.getWorkers().getWorker(0).getAlive();
					NOTIFY_ID = 4;
					return true;
				}

				if (miningpool.equalsIgnoreCase("50BTC")) {
					pref_apiKey = prefs.getString("50BTCKey", "");

					HttpGet post = new HttpGet("https://50btc.com/en/api/"
							+ pref_apiKey + "?text=1");
					FiftyBTC data = null;
					HttpResponse response = client.execute(post);
					data = mapper
							.readValue(new InputStreamReader(response
									.getEntity().getContent(), "UTF-8"),
									FiftyBTC.class);
					btcBalance = data.getUser().getConfirmed_rewards()
							.toString();
					hashRate = data.getWorkers().getWorker(0).getHash_rate();
					alive = data.getWorkers().getWorker(0).getAlive();
					NOTIFY_ID = 5;
					return true;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;

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
