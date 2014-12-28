package com.veken0m.bitcoinium.exchanges;

import android.content.Context;

public class ExchangeProperties {

    private final String exchange_name;
    private final String class_name;
    private final String main_currency;
    private final String identifier;
    private final String shortName;
    private final boolean supportsTicker;
    private final boolean supportsOrderbook;
    private final boolean supportsTrades;
    private Context context = null;

    public static class ItemType {
        public static final int EXCHANGE_NAME = 0;
        public static final int CLASS_NAME = 1;
        public static final int DEFAULT_CURRENCY_PAIR = 2;
        public static final int IDENTIFIER = 3;
        public static final int SHORT_NAME = 4;
        public static final int TICKER_ENABLED = 5;
        public static final int ORDERBOOK_ENABLED = 6;
        public static final int TRADES_ENABLED = 7;
    }

    public ExchangeProperties(Context context, String exchangeName) {

        // ToLower and Remove Exchange to keep compatibility with previous indexing system
        exchangeName = exchangeName.toLowerCase().replace("exchange", "").replaceAll("[ .-]", "");
        int resId = context.getResources().getIdentifier(exchangeName, "array", context.getPackageName());
        String[] exchangeProperties = context.getResources().getStringArray(resId);

        this.context = context;
        exchange_name = exchangeProperties[ItemType.EXCHANGE_NAME];
        class_name = exchangeProperties[ItemType.CLASS_NAME];
        main_currency = exchangeProperties[ItemType.DEFAULT_CURRENCY_PAIR];
        identifier = exchangeProperties[ItemType.IDENTIFIER];
        shortName = exchangeProperties[ItemType.SHORT_NAME];
        supportsTicker = exchangeProperties[ItemType.TICKER_ENABLED].equals("1");
        supportsOrderbook = exchangeProperties[ItemType.ORDERBOOK_ENABLED].equals("1");
        supportsTrades = exchangeProperties[ItemType.TRADES_ENABLED].equals("1");
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

        return supportsTrades;
    }

    public Boolean supportsOrderbook() {

        return supportsOrderbook;
    }

    public String[] getCurrencies() {

        int resId = context.getResources().getIdentifier(identifier + "currencies", "array", context.getPackageName());

        return (resId!=0) ? context.getResources().getStringArray(resId) : null;
    }
}
