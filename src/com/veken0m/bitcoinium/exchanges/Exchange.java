
package com.veken0m.bitcoinium.exchanges;

import android.content.Context;

public class Exchange {

    private final String exchange_name;
    private final String class_name;
    private final String main_currency;
    private final String logo_path;
    private final String notification_id;
    private final String prefix;
    private final boolean supportsPriceGraph;
    private final boolean tickerSupportsBidAsk;

    public Exchange(Context ctxt, String exchangeName) {
        
        String[] exchangeProperties = ctxt.getResources().getStringArray(
                ctxt.getResources().getIdentifier(exchangeName, "array",
                        ctxt.getPackageName()));
        
        exchange_name = exchangeProperties[0];
        class_name = exchangeProperties[1];
        main_currency = exchangeProperties[2];
        logo_path = exchangeProperties[3];
        notification_id = exchangeProperties[4];
        prefix = exchangeProperties[5];
        supportsPriceGraph = Boolean.parseBoolean(exchangeProperties[6]);
        tickerSupportsBidAsk = Boolean.parseBoolean(exchangeProperties[7]);
    }

    public String getExchangeName() {

        return exchange_name;
    }

    public String getClassName() {

        return class_name;
    }

    public String getMainCurrency() {

        return main_currency;
    }

    public String getLogo() {

        return logo_path;
    }

    public String getNotificationID() {
        return notification_id;
    }

    public String getPrefix() {

        return prefix;
    }

    public Boolean supportsPriceGraph() {

        return supportsPriceGraph;
    }

    public Boolean supportsTickerBidAsk() {

        return tickerSupportsBidAsk;
    }

}
