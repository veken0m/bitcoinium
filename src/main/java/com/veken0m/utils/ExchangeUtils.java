package com.veken0m.utils;

import com.veken0m.cavirtex.exchanges.ExchangeProperties;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.cryptsy.CryptsyExchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import com.xeiam.xchange.utils.CertHelper;

public class ExchangeUtils {

    // Some exchanges need to be handled differently; Do the funky stuff here...
    public static PollingMarketDataService getMarketData(ExchangeProperties exchange, CurrencyPair currencyPair) {

        // TODO: find way to import required certificates
        if (exchange.getIdentifier().equals("bitfinex") || exchange.getIdentifier().equals("cryptotrade")) {
            try {
                CertHelper.trustAllCerts();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Crypsy has a different API for public and private...
        Exchange exchangeInstance = ExchangeFactory.INSTANCE.createExchange(exchange.getClassName());
        if (exchange.getIdentifier().equals("cryptsy")) {
            return ((CryptsyExchange) exchangeInstance).getPublicPollingMarketDataService();
        } else {  // Other exchanges...
            return exchangeInstance.getPollingMarketDataService();
        }
    }

}
