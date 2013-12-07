package com.veken0m.bitcoinium.utils;

import java.text.DecimalFormat;

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
    
    public static String formatPayout(float amount){
        
        DecimalFormat df = new DecimalFormat("#.#####");
        
        if(amount < 0.0001){
            return df.format(amount*1000000) + " µBTC";        
        } else if(amount < 0.1){
            return df.format(amount*1000) + " mBTC";
        } else {
            return df.format(amount) + " BTC";
        }
    }
}
