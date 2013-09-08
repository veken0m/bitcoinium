package com.veken0m.bitcoinium.utils;

import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.currency.CurrencyPair;

public class CurrencyUtils {
    
    public static CurrencyPair stringToCurrencyPair(String pref_currency){
        String baseCurrency = Currencies.BTC;
        String counterCurrency = pref_currency;
    
        if (pref_currency.contains("/")) {
            baseCurrency = pref_currency.substring(0, 3);
            counterCurrency = pref_currency.substring(4, 7);
        }
        return new CurrencyPair(baseCurrency, counterCurrency);
    }

}
