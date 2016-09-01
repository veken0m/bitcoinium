package com.veken0m.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.util.Pair;

import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.exchanges.ExchangeProperties;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.cryptsy.CryptsyExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.polling.marketdata.PollingMarketDataService;
import org.knowm.xchange.utils.CertHelper;

import java.util.ArrayList;
import java.util.List;

public class ExchangeUtils
{
    // Some exchanges need to be handled differently; Do the funky stuff here...
    public static PollingMarketDataService getMarketData(ExchangeProperties exchange, CurrencyPair currencyPair)
    {
        // TODO: find way to import required certificates
        if (exchange.getIdentifier().equals("cryptotrade") || exchange.getIdentifier().equals("bitcoincentral"))
        {
            try
            {
                CertHelper.trustAllCerts();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        // Crypsy has a different API for public and private...
        Exchange exchangeInstance = ExchangeFactory.INSTANCE.createExchange(exchange.getClassName());
        if (exchange.getIdentifier().equals("cryptsy"))
        {
            return ((CryptsyExchange) exchangeInstance).getPollingPublicMarketDataService();
        }
        else
        {  // Other exchanges...
            return exchangeInstance.getPollingMarketDataService();
        }
    }

    public static Pair<List<String>, List<String>> getAllDropdownItems(Context context)
    {
        return getDropdownItems(context, 0, true);
    }

    public static Pair<List<String>, List<String>> getDropdownItems(Context context, int serviceType)
    {
        return getDropdownItems(context, serviceType, false);
    }

    public static Pair<List<String>, List<String>> getDropdownItems(Context context, int serviceType, boolean includeAll)
    {
        Resources res = context.getResources();
        String[] exchangeIds = res.getStringArray(R.array.exchangeID);
        String pkgName = context.getPackageName();
        List<String> dropdown = new ArrayList<>();
        List<String> dropdownIds = new ArrayList<>();

        for (String exchangeId : exchangeIds)
        {
            int id = res.getIdentifier(exchangeId, "array", pkgName);
            String[] exchangeMeta = context.getResources().getStringArray(id);

            if (includeAll || exchangeMeta[serviceType].equals("1"))
            {
                dropdown.add(exchangeMeta[ExchangeProperties.ItemType.EXCHANGE_NAME]); // Add exchange name
                dropdownIds.add(exchangeMeta[ExchangeProperties.ItemType.IDENTIFIER]);
            }
        }

        return new Pair(dropdown, dropdownIds);
    }
}
