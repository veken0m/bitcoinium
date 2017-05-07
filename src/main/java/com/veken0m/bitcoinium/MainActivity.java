package com.veken0m.bitcoinium;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.veken0m.bitcoinium.exchanges.ExchangeProperties;
import com.veken0m.bitcoinium.preferences.PreferencesActivity;
import com.veken0m.bitcoinium.preferences.PriceAlertPreferencesActivity;
import com.veken0m.utils.Constants;
import com.xeiam.xbtctrader.TraderActivity;

public class MainActivity extends BaseActivity
{
    String exchangeName = null;

    public static class HomeMenuFragment extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            return inflater.inflate(R.layout.fragment_menu, container, false);
        }
    }

    static
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        exchangeName = (extras != null) ? extras.getString("exchangeKey") : null;

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, new HomeMenuFragment());
        fragmentTransaction.commit();

        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.ab_background_textured_bitcoinium));

        // Some hack to make widgets appear on devices without rebooting
        sendBroadcast(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_preferences:
                startActivity(new Intent(this, PreferencesActivity.class));
                break;
            case R.id.action_alarm_preferences:
                startActivity(new Intent(this, PriceAlertPreferencesActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onStop()
    {
        super.onStop();
        // clear the extra
        getIntent().removeExtra("exchangeKey");
    }

    public void startActivity(View v)
    {
        Intent intent = null;
        ExchangeProperties exchange;

        switch (v.getId())
        {
            case R.id.bitcoinaverage:
                intent = new Intent(this, BitcoinAverageActivity.class);
                break;
            case R.id.bitcoincharts:
                intent = new Intent(this, BitcoinChartsActivity.class);
                break;
            case R.id.minerstats:
                intent = new Intent(this, MinerStatsActivity.class);
                break;
            case R.id.marketdepth:
                intent = new Intent(this, WebViewerActivity.class);
                break;
            case R.id.orderbook:
                intent = new Intent(this, OrderbookActivity.class);
                if(exchangeName!=null) {
                    exchange = new ExchangeProperties(this, exchangeName);
                    if (exchange.supportsOrderbook())
                        intent.putExtra("exchange", exchange.getIdentifier());
                }
                break;
            case R.id.displaygraph:
                intent = new Intent(this, GraphActivity.class);
                if(exchangeName!=null) {
                    exchange = new ExchangeProperties(this, exchangeName);
                    if (exchange.supportsTrades())
                        intent.putExtra("exchange", exchange.getIdentifier());
                }
                break;
        }
        startActivity(intent);
    }

    public void refreshWidgets(View v){
        sendBroadcast(new Intent(this, WidgetProvider.class).setAction(Constants.REFRESH));
        sendBroadcast(new Intent(this, MinerWidgetProvider.class).setAction(Constants.REFRESH));
        sendBroadcast(new Intent(this, BalanceWidgetProvider.class).setAction(Constants.REFRESH));
        moveTaskToBack(true);
    }

    public void startXTraderActivity(final View v) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        final CharSequence items[] = getResources().getStringArray(R.array.exchangesBitcoiniumWS);
        adb.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int n) {
                Intent intent = new Intent(v.getContext(), TraderActivity.class);
                intent.putExtra("exchange", items[n]);
                startActivity(intent);
            }
        });
        adb.setNegativeButton(getString(R.string.cancel), null);
        adb.setTitle("Select a market symbol");
        adb.show();
    }
}
