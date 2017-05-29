package com.xeiam.xbtctrader;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.exchanges.ExchangeProperties;
import com.veken0m.utils.Constants;
import com.veken0m.utils.CurrencyUtils;
import com.xeiam.business.ExchangeAccount;
import com.xeiam.dialogs.ApiKeyAlert;
import com.xeiam.dialogs.CancelOrderDialog;
import com.xeiam.dialogs.NoNetworkAlert;
import com.xeiam.dialogs.SubmitOrderDialog;
import com.xeiam.paint.Painter;
import com.xeiam.tasks.GeneralUpdateDeamon;
import com.xeiam.tasks.GetHistoricalDataTask;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.text.DecimalFormat;

public class TraderActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener
{
    private static final String TAG = "TraderActivity";
    public static ExchangeAccount exchangeAccount;
    public static MainView mainView;
    public static SharedPreferences preferences;
    public static DecimalFormat oneDecimalFormatter = new DecimalFormat("#.#");
    public static DecimalFormat twoDecimalFormatter = new DecimalFormat("#.##");
    public static DecimalFormat threeDecimalFormatter = new DecimalFormat("#.###");
    public static DecimalFormat fiveDecimalFormatter = new DecimalFormat("#.#####");
    public static DecimalFormat btcFormatter = new DecimalFormat("#.###BTC");
    public static DecimalFormat fiatFormatter;
    public static ExchangeProperties exchangeInfo;
    public static String tradableIdentifier = "BTC";
    public static String transactionCurrency = "USD";
    public static int DIALOG_EXCHANGE_CONNECTION_FAIL = 0;
    public static int DIALOG_INTERNET_CONNECTION_FAIL = 1;
    public static int CHART_TARGET_RESOLUTION = 1000;
    private GeneralUpdateDeamon dataUpdateDeamon;
    private Vibrator vibrator;

    @Override
    public void onStart()
    {
        super.onStart();
    }

    public void onStop()
    {
        super.onStop();
//		if(dataUpdateDeamon!=null){
//			dataUpdateDeamon.setActive(false);
//		}
    }

    public void onPause()
    {
        super.onPause();
        if (dataUpdateDeamon != null)
        {
            dataUpdateDeamon.setActive(false);
        }
    }

    public void onResume()
    {
        super.onResume();

        System.out.println("On Resume called");

        //check internet connection
        if (!isNetworkConnected())
        {
            showAlert(DIALOG_INTERNET_CONNECTION_FAIL);
            return;
        }

        //try to initialize the exchange
        if (exchangeAccount.isConnectionGood())
        {
            startAccountDeamon();
            queryHistoricalData();
        }
        else if (exchangeAccount.init())
        {
            startAccountDeamon();
            queryHistoricalData();
        }
        else
        {
            showAlert(DIALOG_EXCHANGE_CONNECTION_FAIL);
            return;
        }
    }

    private void showTradingInterface(boolean showTrading)
    {

        View tradingBalances = findViewById(R.id.tradingBalances);
        View tradingButtons = findViewById(R.id.tradingButtons);
        if (showTrading)
        {
            tradingBalances.setVisibility(View.VISIBLE);
            tradingButtons.setVisibility(View.VISIBLE);
        }
        else
        {
            tradingBalances.setVisibility(View.GONE);
            tradingButtons.setVisibility(View.GONE);
        }
    }

    private void queryHistoricalData()
    {
        Log.v("PreferenceChange", "queryHistoricalData");
        GetHistoricalDataTask getHistoricalDataTask = new GetHistoricalDataTask(exchangeAccount);
        getHistoricalDataTask.go();
    }

    public void showAlert(int key)
    {
        if (key == DIALOG_INTERNET_CONNECTION_FAIL)
        {
            FragmentManager fm = getSupportFragmentManager();
            NoNetworkAlert noNetworkAlert = new NoNetworkAlert();
            noNetworkAlert.show(fm, "NoticeDialogFragment");
        }
        else if (key == DIALOG_EXCHANGE_CONNECTION_FAIL)
        {
            ApiKeyAlert apiKeyAlert = new ApiKeyAlert();
            apiKeyAlert.show(getSupportFragmentManager(), "NoticeDialogFragment");
        }
    }

    public void startAccountDeamon()
    {
        dataUpdateDeamon = new GeneralUpdateDeamon(this);
        dataUpdateDeamon.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader_main);

        Bundle extras = getIntent().getExtras();
        String sCurrencyPair = Constants.DEFAULT_CURRENCY_PAIR;
        if (extras != null)
        {
            String symbol = extras.getString("exchange");

            int currencyPosition = symbol.length();
            if (currencyPosition > 3)
                currencyPosition -= 3;

            String exchangeSymbol = symbol.substring(0, currencyPosition);
            exchangeInfo = new ExchangeProperties(this, exchangeSymbol);
            sCurrencyPair = "BTC/" + symbol.substring(currencyPosition);
            //exchangeInfo = new Exchange(this, "bitstamp");
        }

        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        PreferenceManager.setDefaultValues(this, R.xml.pref_trader, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        CurrencyPair currencyPair = CurrencyUtils.stringToCurrencyPair(sCurrencyPair);
        tradableIdentifier = currencyPair.base.getCurrencyCode();
        transactionCurrency = currencyPair.counter.getCurrencyCode();

        showTradingInterface(preferences.getBoolean("enableTradingKey", false));

        if (savedInstanceState == null)
        {
            exchangeAccount = new ExchangeAccount(this);
        }

        //init the view variables.
        MainView view = (MainView) findViewById(R.id.main_view);
        view.setMainActivity(this);
        TraderActivity.mainView = view;

        //String fiat=pref_xtrader.getString("listCurrency", "USD");
        fiatFormatter = new DecimalFormat(CurrencyUtils.getSymbol(TraderActivity.transactionCurrency) + "#.##");

        System.out.println("on create was called.");
    }


    public void vibrate(int duration)
    {
        vibrator.vibrate(duration);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.action_preferences:
                openPrefs();
                return true;
            case R.id.action_refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void refresh()
    {
        queryHistoricalData();
    }

    private void openPrefs()
    {
        Intent intent = new Intent(TraderActivity.this,
                PreferenceActivity.class);
        startActivity(intent);
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
    {

        Log.v("PreferenceChange", "key=" + key);

        boolean needToUpdate = false;
        if (key.contains("SecretKey") || key.contains("ApiKey") || key.contains("Username") || key.contains("Password"))
        {
            //String id = TraderActivity.exchangeInfo.getIdentifier();
            //String sCurrencyPair = TraderActivity.pref_trader.getString(id + "TradeCurrency", "");
            //CurrencyPair currencyPair = CurrencyUtils.stringToCurrencyPair(sCurrencyPair);
            //tradableIdentifier = currencyPair.base.getSymbol();
            //transactionCurrency = currencyPair.counter.getSymbol();

            needToUpdate = true;
        }

        exchangeAccount.setReferenceTicker();//every time the user sets a preference this is an opportuntity to set the scales.
        getPainter().setScales();

        if (key.equals("enableTradingKey"))
            showTradingInterface(preferences.getBoolean("enableTradingKey", false));

        if (needToUpdate || key.equals("time_window") || key.equals("price_window") || key.equals("ordergridsize"))
            queryHistoricalData();
    }

    public void onAccountInfoUpdate()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                //fiat account
                String fiatSymbol = TraderActivity.transactionCurrency;
                float fiatBalance = exchangeAccount.getTotalFiatBalance(fiatSymbol);
                TextView fiatBalanceView = ((TextView) findViewById(R.id.balance_fiat));
                fiatBalanceView.setText(fiatFormatter.format(fiatBalance));

                //total BTCs in wallets
                float totalBTC = exchangeAccount.getTotalBTC();
                ((TextView) findViewById(R.id.balance_btc)).setText(btcFormatter.format(totalBTC));

                //total account value
                double accountValue = exchangeAccount.getAccountValue(fiatSymbol);
                TextView accountValueView = ((TextView) findViewById(R.id.account_value));
                accountValueView.setText(fiatFormatter.format(accountValue));

                if (exchangeAccount.accountValueIncreasing())
                {
                    accountValueView.setTextColor(Color.GREEN);
                }
                else
                {
                    accountValueView.setTextColor(Color.RED);
                }
            }
        });
    }


    public void onDestroy()
    {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public void buy(View view)
    {
        MainView mainView = (MainView) findViewById(R.id.main_view);
        float[] orderCoord = mainView.getOrderCoords();
        SubmitOrderDialog submitOrderDialog = new SubmitOrderDialog();
        submitOrderDialog.set(OrderType.BID, orderCoord[1], orderCoord[0]);
        submitOrderDialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    public void sell(View view)
    {
        MainView mainView = (MainView) findViewById(R.id.main_view);
        float[] orderCoord = mainView.getOrderCoords();
        SubmitOrderDialog submitOrderDialog = new SubmitOrderDialog();
        submitOrderDialog.set(OrderType.ASK, orderCoord[1], orderCoord[0]);
        submitOrderDialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    public void cancle(LimitOrder limitOrder)
    {
        CancelOrderDialog cancelOrderDialog = new CancelOrderDialog();
        cancelOrderDialog.set(limitOrder);
        cancelOrderDialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    private boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    public float getOrderGridSize()
    {
        try
        {
            return Float.parseFloat(preferences.getString("ordergridsize", ".5"));
        }
        catch (Exception e)
        {
            return .5f;
        }
    }

    public float getPriceWindow()
    {
        String pw = preferences.getString("price_window", "FIVE_PERCENT");

        if (pw.equalsIgnoreCase("TWO_PERCENT"))
        {
            return .02f;
        }
        else if (pw.equalsIgnoreCase("FIVE_PERCENT"))
        {
            return .05f;
        }
        else if (pw.equalsIgnoreCase("TEN_PERCENT"))
        {
            return .1f;
        }
        else if (pw.equalsIgnoreCase("TWENTY_PERCENT"))
        {
            return .2f;
        }
        else if (pw.equalsIgnoreCase("FIFTY_PERCENT"))
        {
            return .5f;
        }
        else if (pw.equalsIgnoreCase("ONE_HUNDRED_PERCENT"))
        {
            return 1f;
        }

        return .02f;
    }

    public Painter getPainter()
    {
        if (mainView == null)
        {
            return null;
        }

        return mainView.getPainter();
    }

    public void generateToast(String msg, int duration)
    {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }
}
