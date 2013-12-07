
package com.veken0m.utils;

import java.text.DecimalFormat;

import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.currency.CurrencyPair;

public class CurrencyUtils {

    public static CurrencyPair stringToCurrencyPair(String currencyPair) {
        String baseCurrency = Currencies.BTC;
        String counterCurrency = currencyPair;

        String[] currPair = currencyPair.split("/");

        if (currPair.length == 2) {
            baseCurrency = currPair[0];
            counterCurrency = currPair[1];
        } else {
            baseCurrency = Currencies.BTC;
            counterCurrency = currencyPair;
        }

        return new CurrencyPair(baseCurrency, counterCurrency);
    }

    public static String formatPayout(float amount, int payoutUnits) {

        DecimalFormat df = new DecimalFormat("#.########");

        switch (payoutUnits) {
            case 0:
                df = new DecimalFormat("#.#####");
                if (amount < 0.0001) {
                    return df.format(amount * 1000000) + " µBTC";
                } else if (amount < 0.1) {
                    return df.format(amount * 1000) + " mBTC";
                } else {
                    return df.format(amount) + " BTC";
                }
            case 1:
                df = new DecimalFormat("#.########");
                return df.format(amount) + " BTC";
            case 2:
                df = new DecimalFormat("#.#####");
                return df.format(amount * 1000) + " mBTC";
            case 3:
                df = new DecimalFormat("#.#####");
                return df.format(amount * 1000000) + " µBTC";
        }
        return df.format(amount) + " BTC";
    }
}
