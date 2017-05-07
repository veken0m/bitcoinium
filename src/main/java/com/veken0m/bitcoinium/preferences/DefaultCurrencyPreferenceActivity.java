package com.veken0m.bitcoinium.preferences;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;

import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.exchanges.ExchangeProperties;

import java.util.List;

import static com.veken0m.utils.ExchangeUtils.getAllDropdownItems;

public class DefaultCurrencyPreferenceActivity extends AppCompatActivity
{
    public static class DefaultCurrencyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_default_currency);

            Pair<List<String>, List<String>> exchanges = getAllDropdownItems(getActivity());
            PreferenceScreen defaultCurrencyPref = (PreferenceScreen) findPreference("defaultCurrencyPref");

            for (String sExchange : exchanges.second)
            {
                ExchangeProperties exchange = new ExchangeProperties(getActivity(), sExchange);
                String[] sCurrencies = exchange.getCurrencies();

                if (sCurrencies != null)
                {
                    // Default Currency List
                    ListPreference currencies = new ListPreference(getActivity());
                    currencies.setKey(exchange.getIdentifier() + "CurrencyPref");
                    currencies.setEntries(sCurrencies);
                    currencies.setEntryValues(sCurrencies);
                    currencies.setTitle(exchange.getExchangeName() + " " + getString(R.string.currency));
                    currencies.setSummary(getString(R.string.pref_currency_displayed, exchange.getExchangeName()));
                    currencies.setDefaultValue(exchange.getDefaultCurrency());

                    defaultCurrencyPref.addPreference(currencies);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new DefaultCurrencyPreferenceFragment())
                .commit();
    }
}
