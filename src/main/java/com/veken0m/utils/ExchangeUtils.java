package com.veken0m.utils;

import android.content.Context;
import android.support.v4.util.Pair;

import com.veken0m.bitcoinium.R;
import com.veken0m.bitcoinium.exchanges.ExchangeProperties;
import org.knowm.xchange.*;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.util.ArrayList;
import java.util.List;

public class ExchangeUtils
{
    // Some exchanges need to be handled differently; Do the funky stuff here...
    public static MarketDataService getMarketData(ExchangeProperties exchange)
    {
        ExchangeSpecification exchangeSpec = new ExchangeSpecification(exchange.getClassName());
        exchangeSpec.setShouldLoadRemoteMetaData(false); // Don't remote init metadata.
        Exchange exchangeInstance = ExchangeFactory.INSTANCE.createExchange(exchangeSpec);

        return exchangeInstance.getMarketDataService();
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
        String[] exchangeIds = context.getResources().getStringArray(R.array.exchangeId);
        List<String> dropdown = new ArrayList<>();
        List<String> dropdownIds = new ArrayList<>();

        for (String exchangeId : exchangeIds) {
            ExchangeProperties exchangeMetadata = new ExchangeProperties(context, exchangeId);

            if (includeAll || exchangeMetadata.supportsServiceType(serviceType)) {
                dropdown.add(exchangeMetadata.getExchangeName()); // Add exchange name
                dropdownIds.add(exchangeMetadata.getIdentifier());
            }
        }

        return new Pair(dropdown, dropdownIds);
    }
}
