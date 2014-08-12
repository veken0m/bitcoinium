package com.veken0m.utils;

import com.veken0m.bitcoinium.exchanges.ExchangeProperties;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.bitcurex.BitcurexExchange;
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

        PollingMarketDataService marketData;
        Exchange exchangeInstance = ExchangeFactory.INSTANCE.createExchange(exchange.getClassName());

        // Crypsy has a different API for public and private...
        if (exchange.getIdentifier().equals("cryptsy")) {
            return ((CryptsyExchange) exchangeInstance).getPublicPollingMarketDataService();

        // Bitcurex has a different API for PLN and EUR
        } else if (exchange.getIdentifier().equals("bitcurex") && currencyPair.counterSymbol.equals("PLN")) {
            BitcurexExchange bitcurexExchange = (BitcurexExchange) exchangeInstance;
            bitcurexExchange.applySpecification(bitcurexExchange.getDefaultExchangePLNSpecification());
            return bitcurexExchange.getPollingMarketDataService();

        // Other exchanges...
        } else {
            return ExchangeFactory.INSTANCE.createExchange(exchange.getClassName()).getPollingMarketDataService();
        }
    }

}
