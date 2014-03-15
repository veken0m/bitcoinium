
package com.veken0m.bitcoinium.exchanges;

import android.content.Context;

public class Exchange {

    private final String exchange_name;
    private final String class_name;
    private final String main_currency;
    private final String identifier;
    private final boolean tickerSupportsBidAsk;

    public Exchange(Context context, String exchangeName) {

        // ToLower and Remove Exchange to keep compatibility with previous indexing system
        String[] exchangeProperties = context.getResources().getStringArray(
                context.getResources().getIdentifier(exchangeName.toLowerCase().replace("exchange","").replace(".","").replace("-",""), "array",
                        context.getPackageName()));

        exchange_name = exchangeProperties[0];
        class_name = exchangeProperties[1];
        main_currency = exchangeProperties[2];
        identifier = exchangeProperties[3];
        tickerSupportsBidAsk = Boolean.parseBoolean(exchangeProperties[4]);
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

    public Boolean supportsTickerBidAsk() {

        return tickerSupportsBidAsk;
    }

}
