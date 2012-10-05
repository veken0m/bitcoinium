package com.veken0m.cavirtex;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.xeiam.xchange.Currencies;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.marketdata.polling.PollingMarketDataService;

public class WatcherWidgetProvider2 extends BaseWidgetProvider {

  private static PollingMarketDataService marketDataService;

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
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

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
     * @Override public void onStart(Intent intent, int i) { super.onStart(intent, i); }
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

      // buildUpdate(getApplicationContext());
      super.onStartCommand(intent, flags, startId);
      return START_STICKY;
    }

    /*
     * Use buildupdate(Context context) private void buildUpdate() { String lastUpdated = DateFormat.format("MMMM dd, yyyy h:mmaa", new Date()).toString(); RemoteViews view = new RemoteViews(getPackageName(), R.layout.watcher_appwidget2);
     * //view.setTextViewText(R.id.label, lastUpdated); // Push update for this widget to the home screen ComponentName thisWidget = new ComponentName(this, WatcherWidgetProvider2.class); AppWidgetManager manager = AppWidgetManager.getInstance(this);
     * manager.updateAppWidget(thisWidget, view); }
     */

    /*
     * Returns null by default, no need to declare it
     * @Override public IBinder onBind(Intent intent) { return null; }
     */

    @Override
    public void onHandleIntent(Intent intent) {

      // no matter what intent we get, lets update since we are
      // AN UPDATE SERVICE!!!!!!! (null is passed when widget created)
      ComponentName me = new ComponentName(this, WatcherWidgetProvider2.class);
      AppWidgetManager awm = AppWidgetManager.getInstance(this);
      awm.updateAppWidget(me, buildUpdate(this));

    }

    /**
     * the actual update method where we perform an HTTP request to Mt. Gox, read the JSON, and update the text. This displays a notficaiton if successful and sets the time to green, otherwise displays failure and sets text to red
     */

    private RemoteViews buildUpdate(Context context) {

      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.watcher_appwidget2);

      Intent intent = new Intent(this, MainActivity.class);
      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
      views.setOnClickPendingIntent(R.id.widgetButton2, pendingIntent);

      try {

        // Use the factory to get the version 1 MtGox exchange API using
        // default settings
        Exchange mtGox = ExchangeFactory.INSTANCE.createExchange("com.xeiam.xchange.mtgox.v1.MtGoxExchange");

        // Interested in the public polling market data feed (no
        // authentication)
        marketDataService = mtGox.getPollingMarketDataService();

        // Get the latest ticker data showing BTC to USD
        Ticker ticker = marketDataService.getTicker(Currencies.BTC, Currencies.USD);

        NumberFormat numberFormat = DecimalFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setGroupingUsed(false);

        float lastValue = ticker.getLast().getAmount().floatValue();

        String lastPrice = "$" + numberFormat.format(lastValue) + " USD";
        String highPrice = "$" + numberFormat.format(ticker.getHigh().getAmount().floatValue());
        String lowPrice = "$" + numberFormat.format(ticker.getLow().getAmount().floatValue());
        String volume = numberFormat.format(ticker.getVolume());
        views.setTextViewText(R.id.widgetLowText2, lowPrice);
        views.setTextViewText(R.id.widgetHighText2, highPrice);
        views.setTextViewText(R.id.widgetLastText2, lastPrice);
        views.setTextViewText(R.id.widgetVolText2, "Volume: " + volume);

        // Date for widget "Refreshed" label
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
        String currentTime = sdf.format(new Date());
        views.setTextViewText(R.id.label2, "Refreshed @ " + currentTime);
        views.setTextColor(R.id.label2, Color.GREEN);

        if (pref_DisplayUpdates == true) {
          createTicker(context, R.drawable.bitcoin, "MtGox Updated!");
        }

        if (pref_mtgoxTicker) {
          createPermanentNotification(getApplicationContext(), R.drawable.bitcoin, "Bitcoin at " + lastPrice, "Bitcoin value: " + lastPrice + " on MtGox", NOTIFY_ID_MTGOX);
        }

        try {
          if (pref_PriceAlarm) {
            if (!pref_mtgoxLower.equalsIgnoreCase("")) {

              if (lastValue <= Float.valueOf(pref_mtgoxLower)) {
                createNotification(getApplicationContext(), R.drawable.bitcoin, "Bitcoin alarm value has been reached! \n" + "Bitcoin valued at " + lastPrice + " on MtGox", "BTC @ " + lastPrice, "Bitcoin value: "
                    + lastPrice + " on MtGox", NOTIFY_ID_MTGOX);
              }
            }

            if (!pref_mtgoxUpper.equalsIgnoreCase("")) {
              if (lastValue >= Float.valueOf(pref_mtgoxUpper)) {
                createNotification(getApplicationContext(), R.drawable.bitcoin, "Bitcoin alarm value has been reached! \n" + "Bitcoin valued at " + lastPrice + " on MtGox", "BTC @ " + lastPrice, "Bitcoin value: "
                    + lastPrice + " on MtGox", NOTIFY_ID_MTGOX);
              }

            }
          }

        } catch (Exception e) {
          e.printStackTrace();
          views.setTextColor(R.id.label2, Color.CYAN);
        }

      } catch (Exception e) {
        e.printStackTrace();
        if (pref_DisplayUpdates == true) {
          createTicker(context, R.drawable.bitcoin, "MtGox Update failed!");
        }
        views.setTextColor(R.id.label2, Color.RED);

      }
      return views;
    }

    public void widgetButtonAction(Context context) {

      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.watcher_appwidget);

      if (pref_widgetBehaviour.equalsIgnoreCase("mainMenu")) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);

      }

      else if (pref_widgetBehaviour.equalsIgnoreCase("refreshWidget")) {
        Intent intent = new Intent(this, WatcherWidgetProvider.class);
        intent.setAction(REFRESH);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
      }

      else if (pref_widgetBehaviour.equalsIgnoreCase("openGraph")) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(GRAPH);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
      }

      else if (pref_widgetBehaviour.equalsIgnoreCase("pref")) {

        Intent intent = new Intent(this, Preferences.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
      }

      else if (pref_widgetBehaviour.equalsIgnoreCase("extOrder")) {
        Intent intent = new Intent(getBaseContext(), WebViewer.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
      }
    }

  }

}
