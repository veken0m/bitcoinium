package com.veken0m.cavirtex;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;

public class WatcherWidgetProvider2 extends BaseWidgetProvider {

	@Override
	public void onReceive(Context ctxt, Intent intent) {

		if (REFRESH.equals(intent.getAction())) {
			// createNotification(ctxt, R.drawable.bitcoin, "Recieved Update",
			// "It equals REFRESH", "onRecieved");
			readPreferences(ctxt);
			ctxt.startService(new Intent(ctxt, UpdateService2.class));

		} else if (PREFERENCES.equals(intent.getAction())) {

			readPreferences(ctxt);

		} else {
			super.onReceive(ctxt, intent);
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		setAlarm2(context);
		// context.startService(new Intent(context, UpdateService2.class));
		// //needed to update when app widget is created

	}

	/**
	 * This class lets us refresh the widget whenever we want to
	 */

	public static class UpdateService2 extends IntentService {

		public UpdateService2() {
			super("WatcherWidgetProvider2$UpdateService2");
		}

		/*
		 * Depreciated in Android Developper website; Use onStartCommand.
		 * 
		 * @Override public void onStart(Intent intent, int i) {
		 * super.onStart(intent, i); }
		 */

		@Override
		public void onCreate() {
			readPreferences(getApplicationContext());
			// getApplicationContext().startService(new
			// Intent(getApplicationContext(), UpdateService2.class));
			// //Disabled because caused widget to be updated twice (sound,
			// vibrate and notification)
			super.onCreate();
		}

		public int onStartCommand(Intent intent, int flags, int startId) {
			// buildUpdate(getApplicationContext());
			super.onStartCommand(intent, flags, startId);
			return START_STICKY;
		}

		/*
		 * Use buildupdate(Context context) private void buildUpdate() { String
		 * lastUpdated = DateFormat.format("MMMM dd, yyyy h:mmaa", new
		 * Date()).toString();
		 * 
		 * RemoteViews view = new RemoteViews(getPackageName(),
		 * R.layout.watcher_appwidget2);
		 * 
		 * //view.setTextViewText(R.id.label, lastUpdated);
		 * 
		 * // Push update for this widget to the home screen ComponentName
		 * thisWidget = new ComponentName(this, WatcherWidgetProvider2.class);
		 * AppWidgetManager manager = AppWidgetManager.getInstance(this);
		 * manager.updateAppWidget(thisWidget, view); }
		 */

		/*
		 * Returns null by default, no need to declare it
		 * 
		 * @Override public IBinder onBind(Intent intent) { return null; }
		 */

		@Override
		public void onHandleIntent(Intent intent) {

			// no matter what intent we get, lets update since we are
			// AN UPDATE SERVICE!!!!!!! (null is passed when widget created)
			ComponentName me = new ComponentName(this,
					WatcherWidgetProvider2.class);
			AppWidgetManager awm = AppWidgetManager.getInstance(this);
			awm.updateAppWidget(me, buildUpdate(this));

		}

		/**
		 * the actual update method where we perform an HTTP request to Mt. Gox,
		 * read the JSON, and update the text.
		 * 
		 * This displays a notficaiton if successful and sets the time to green,
		 * otherwise displays failure and sets text to red
		 */

		private RemoteViews buildUpdate(Context context) {

			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.watcher_appwidget2);

			// widgetButtonAction(context);

			Intent intent = new Intent(this, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					intent, 0);
			views.setOnClickPendingIntent(R.id.widgetButton2, pendingIntent);

			// HttpClient client = new DefaultHttpClient();
			// client = WebClientDevWrapper.wrapClient(client);
			// HttpGet post = new
			// HttpGet("https://mtgox.com/api/0/data/ticker.php");
			// HttpGet post = new
			// HttpGet("http://anyorigin.com/get/?url=https://mtgox.com/api/0/data/ticker.php");
			// HttpGet post = new
			// HttpGet("http://bitcoincharts.com/t/markets.json");

			HttpClient client = new DefaultHttpClient();
			HttpGet post = new HttpGet();

			try {
				HttpResponse response = null;
				try {
					post = new HttpGet("https://mtgox.com/api/0/data/ticker.php");
					response = client.execute(post);

				} catch (Exception e) {
					post = new HttpGet(
							"http://anyorigin.com/get/?url=https://mtgox.com/api/0/data/ticker.php");
					response = client.execute(post);
				}

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "UTF-8"));
				String text = reader.readLine();
				JSONTokener tokener = new JSONTokener(text);

				// String text = new String(new InputStreamReader(new
				// URL("https://mtgox.com/code/data/ticker.php").openStream());

				// Map jsonArray = JSON.parse(new InputStreamReader(new
				// URL("https://mtgox.com/code/data/ticker.php").openStream()));

				JSONObject jObject = new JSONObject(tokener);
				JSONObject jTicker = new JSONObject();

				try {
					JSONObject jcontents = jObject.getJSONObject("contents");
					jTicker = jcontents.getJSONObject("ticker");
				} catch (Exception e) {
					jTicker = jObject.getJSONObject("ticker");
				}
				

				NumberFormat numberFormat = DecimalFormat.getInstance();
				numberFormat.setMaximumFractionDigits(2);
				numberFormat.setMinimumFractionDigits(2);

				String lastPrice = numberFormat.format(Float.valueOf(jTicker
						.getString("last")));

				// highText.setText("High: " + jTicker.getString("high"));
				// lowText.setText("Low: " + jTicker.getString("low"));

				views.setTextViewText(R.id.widgetVolText2,
						"Volume: " + jTicker.getString("vol"));

				// views.setTextViewText(R.id.widgetBuyText,"Bid: $" +
				// numberFormat.format(new Float(jTicker.getString("buy"))));
				// views.setTextViewText(R.id.widgetSellText,"Ask: $" +
				// numberFormat.format(new Float(jTicker.getString("sell"))));
				// views.setTextViewText(R.id.widgetBuyText,"Bid: $" + "N/A");
				// views.setTextViewText(R.id.widgetSellText,"Ask: $" + "N/A");
				views.setTextViewText(
						R.id.widgetLowText2,
						"$"
								+ numberFormat.format(Float.valueOf(jTicker
										.getString("low"))));
				views.setTextViewText(
						R.id.widgetHighText2,
						"$"
								+ numberFormat.format(Float.valueOf(jTicker
										.getString("high"))));
				String s = "$" + lastPrice + " USD";
				views.setTextViewText(R.id.widgetLastText2, s);
				// views.setTextViewText(R.id.widgetLastText,s + " CAD");
				// views.setTextColor(R.id.widgetVolText,Color.CYAN);
				// views.setTextColor(R.id.widgetLowText,Color.CYAN);
				// views.setTextColor(R.id.widgetHighText,Color.CYAN);

				// Date date = new Date();
				// DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT,
				// Locale.US);
				// String currentTime = df.format(date);
				SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
				String currentTime = sdf.format(new Date());
				views.setTextViewText(R.id.label2, "Refreshed @ " + currentTime);
				views.setTextColor(R.id.label2, Color.GREEN);

				if (pref_DisplayUpdates == true) {
					createTicker(context, R.drawable.bitcoin, "MtGox Updated!");
				}

				if (pref_mtgoxTicker) {
					createPermanentNotification(getApplicationContext(),
							R.drawable.bitcoin, "Bitcoin at $" + lastPrice
									+ " USD", "Bitcoin value: $" + lastPrice
									+ " USD on MtGox", NOTIFY_ID_MTGOX);
				}

				try {
					if (pref_PriceAlarm) {
						if (!pref_mtgoxLower.equalsIgnoreCase("")) {

							if (Float.valueOf(jTicker.getString("last")) <= Float
									.valueOf(pref_mtgoxLower)) {
								createNotification(getApplicationContext(),
										R.drawable.bitcoin,
										"Bitcoin alarm value has been reached! \n"
												+ "Bitcoin valued at $"
												+ lastPrice + " USD on MtGox",
												"BTC @ $" + lastPrice + " USD",
										"Bitcoin value: $" + lastPrice
												+ " USD on MtGox",
										NOTIFY_ID_MTGOX);
							}
						}

						if (!pref_mtgoxUpper.equalsIgnoreCase("")) {
							if (Float.valueOf(jTicker.getString("last")) >= Float
									.valueOf(pref_mtgoxUpper)) {
								createNotification(getApplicationContext(),
										R.drawable.bitcoin,
										"Bitcoin alarm value has been reached! \n"
												+ "Bitcoin valued at $"
												+ lastPrice + " USD on MtGox",
												"BTC @ $" + lastPrice + " USD",
										"Bitcoin value: $" + lastPrice
												+ " USD on MtGox",
										NOTIFY_ID_MTGOX);
							}

						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					views.setTextColor(R.id.label2, Color.CYAN);
				}

			} catch (Exception e) {
				e.printStackTrace();
				// tv.setText(getStackTrace(e));
				// views.setTextViewText(R.id.widgetBuyText,"Error retrieving data from Mt. Gox.");

				// views.setTextViewText(R.id.label,"Make sure you have cell or internet reception. Last attempted at "
				// + currentTime);
				if (pref_DisplayUpdates == true) {
					createTicker(context, R.drawable.bitcoin,
							"MtGox Update failed!");
				}
				views.setTextColor(R.id.label2, Color.RED);

			}

			// Tell the AppWidgetManager to perform an update on the current App
			// Widget
			return views;
		}

		public void widgetButtonAction(Context context) {
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.watcher_appwidget);

			if (pref_widgetBehaviour.equalsIgnoreCase("mainMenu")) {
				Intent intent = new Intent(this, MainActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, 0, intent, 0);
				views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);

			}

			else if (pref_widgetBehaviour.equalsIgnoreCase("refreshWidget")) {
				Intent intent = new Intent(this, WatcherWidgetProvider.class);
				intent.setAction(REFRESH);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						context, 0, intent, 0);
				views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
			}

			else if (pref_widgetBehaviour.equalsIgnoreCase("openGraph")) {
				// Intent intent = new Intent(getBaseContext(),
				// MainActivity.class);
				// intent.setAction(GRAPH);

				// PendingIntent pendingIntent =
				// PendingIntent.getActivity(context, 0, intent, 0);
				// views.setOnClickPendingIntent(R.id.widgetButton,
				// pendingIntent);

				Intent intent = new Intent(this, MainActivity.class);
				intent.setAction(GRAPH);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						context, 0, intent, 0);
				views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
				// startActivity(intent);

			}

			else if (pref_widgetBehaviour.equalsIgnoreCase("pref")) {
				// context.startService(new Intent(context,
				// UpdateService.class)); //find a way to set to button
				// context.startSe(new Intent(context, UpdateService.class));
				// context.startService(service).set
				Intent intent = new Intent(this, Preferences.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, 0, intent, 0);
				views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
			}

			else if (pref_widgetBehaviour.equalsIgnoreCase("extOrder")) {
				Intent intent = new Intent(getBaseContext(), WebViewer.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, 0, intent, 0);
				views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
			}
		}

	}

}
