package com.veken0m.bitcoinium.exchanges;

import android.content.Context;

public class ExchangeProperties {

    private final String exchange_name;
    private final String class_name;
    private final String main_currency;
    private final String identifier;
    private final String shortName;
    private Context context = null;

    public ExchangeProperties(Context context, String exchangeName) {

        // ToLower and Remove Exchange to keep compatibility with previous indexing system
        exchangeName = exchangeName.toLowerCase().replace("exchange", "").replaceAll("[ .-]", "");
        int resId = context.getResources().getIdentifier(exchangeName, "array", context.getPackageName());
        String[] exchangeProperties = context.getResources().getStringArray(resId);

        this.context = context;
        exchange_name = exchangeProperties[0];
        class_name = exchangeProperties[1];
        main_currency = exchangeProperties[2];
        identifier = exchangeProperties[3];
        shortName = exchangeProperties[4];
    }

    public String getExchangeName() {

        return exchange_name;
    }

    public String getClassName() {

        return class_name;
    }

    public String getDefaultCurrency() {

        return main_currency;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getShortName() {
        return shortName;
    }

    public Boolean supportsTicker() {

        int resId = context.getResources().getIdentifier("exchangeID", "array", context.getPackageName());
        String[] exchangesSupportTicker = context.getResources().getStringArray(resId);

        for (String exchangeName : exchangesSupportTicker) {
            if (exchangeName.equals(identifier))
                return true;
        }
        return false;
    }

    public Boolean supportsTrades() {

        int resId = context.getResources().getIdentifier("exchangesTrades", "array", context.getPackageName());
        String[] exchangesSupportGraph = context.getResources().getStringArray(resId);

        for (String exchangeName : exchangesSupportGraph) {
            if (exchangeName.replaceAll("[ .-]", "").equalsIgnoreCase(identifier))
                return true;
        }
        return false;
    }

    public Boolean supportsOrderbook() {

        int resId = context.getResources().getIdentifier("exchangesOrderbook", "array", context.getPackageName());
        String[] exchangesSupportOrderbook = context.getResources().getStringArray(resId);

        for (String exchangeName : exchangesSupportOrderbook) {
            if (exchangeName.replaceAll("[ .-]", "").equalsIgnoreCase(identifier))
                return true;
        }
        return false;
    }

    public String[] getCurrencies() {

        int resId = context.getResources().getIdentifier(identifier + "currencies", "array", context.getPackageName());

        return context.getResources().getStringArray(resId);
    }
}
