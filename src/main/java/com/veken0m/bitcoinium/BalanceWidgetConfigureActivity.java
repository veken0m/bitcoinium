package com.veken0m.bitcoinium;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.veken0m.utils.Constants;

public class BalanceWidgetConfigureActivity extends AppCompatActivity
{
    private static final String PREF_ADDRESS_KEY = "address_";
    private static final String PREF_NICKNAME_KEY = "nickname_";
    private static final String PREF_CURRENCY_KEY = "currency_";

    public BalanceWidgetConfigureActivity()
    {
        super();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static String loadAddressPref(Context context, int appWidgetId)
    {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_WALLET_ADDRESS, 0);
        return prefs.getString(PREF_ADDRESS_KEY + appWidgetId, null);
    }

    // Write the prefix to the SharedPreferences object for this widget
    private static void saveAddressPref(Context context, int appWidgetId, String exchange)
    {
        Editor prefs = context.getSharedPreferences(Constants.PREFS_WALLET_ADDRESS, 0).edit();
        prefs.putString(PREF_ADDRESS_KEY + appWidgetId, exchange);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static String loadCurrencyPref(Context context, int appWidgetId)
    {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_WALLET_ADDRESS, 0);
        return prefs.getString(PREF_CURRENCY_KEY + appWidgetId, null);
    }

    // Write the prefix to the SharedPreferences object for this widget
    private static void saveCurrencyPref(Context context, int appWidgetId, String exchange)
    {
        Editor prefs = context.getSharedPreferences(Constants.PREFS_WALLET_ADDRESS, 0).edit();
        prefs.putString(PREF_CURRENCY_KEY + appWidgetId, exchange);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static String loadNicknamePref(Context context, int appWidgetId)
    {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_WALLET_ADDRESS, 0);
        return prefs.getString(PREF_NICKNAME_KEY + appWidgetId, null);
    }

    // Write the prefix to the SharedPreferences object for this widget
    private static void saveNicknamePref(Context context, int appWidgetId, String exchange)
    {
        Editor prefs = context.getSharedPreferences(Constants.PREFS_WALLET_ADDRESS, 0).edit();
        prefs.putString(PREF_NICKNAME_KEY + appWidgetId, exchange);
        prefs.apply();
    }

    public static class BalanceWidgetConfigureFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener
    {
        private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Context context;

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            context = getActivity().getApplicationContext();

            addPreferencesFromResource(R.xml.pref_balance_widget);

            // Set the result to CANCELED. This will cause the widget host to cancel
            // out of the widget placement if they press the back button.
            getActivity().setResult(RESULT_CANCELED);

            // Find the widget id from the intent.
            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null)
                mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            // If they gave us an intent without the widget id, just bail.
            if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
                getActivity().finish();

            Preference OKpref = findPreference("OKpref");
            OKpref.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference)
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            String sAddress = prefs.getString("widgetAddressPref", "INVALID ADDRESS");
            String sAddressNickname = prefs.getString("widgetAddressNicknamePref", "");
            String sBalanceValue = prefs.getString("widgetBalanceValuePref", Constants.DEFAULT_CURRENCY_PAIR);

            // Save widget configuration
            saveAddressPref(context, mAppWidgetId, sAddress.trim());
            saveNicknamePref(context, mAppWidgetId, sAddressNickname.trim());
            saveCurrencyPref(context, mAppWidgetId, sBalanceValue);

            // Clear potentially sensitive information
            prefs.edit().remove("widgetAddressPref").apply();
            prefs.edit().remove("widgetAddressNicknamePref").apply();

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            getActivity().setResult(RESULT_OK, resultValue);

            getActivity().finish();
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new BalanceWidgetConfigureFragment())
                .commit();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        sendBroadcast(new Intent(this, BalanceWidgetProvider.class).setAction(Constants.REFRESH));
    }

}
