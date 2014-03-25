package com.veken0m.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    //Constants
    public static final String REFRESH = "com.veken0m.bitcoinium.REFRESH";

    public static final String [] BITCOINAVERAGE_CURRENCIES = {
            "AUD", "BRL", "CAD", "CNY", "CZK", "EUR", "GBP", "ILS", "JPY", "NOK",
            "NZD","PLN", "RUB", "SEK", "USD", "ZAR"};

    public static Map<String, String> CRYPTO_SYMBOLS = new HashMap<String, String>();
    static
    {
        CRYPTO_SYMBOLS = new HashMap<String, String>();
        CRYPTO_SYMBOLS.put("BTC", "Ƀ");
        CRYPTO_SYMBOLS.put("XBT", "Ƀ");
        CRYPTO_SYMBOLS.put("LTC", "Ł");
        CRYPTO_SYMBOLS.put("DOGE", "Ð");
        CRYPTO_SYMBOLS.put("XDG", "Ð");
    }

    public static final String [] METRIC_UNITS = {"m","µ","n","p","f"};

    public static final String DEFAULT_EXCHANGE = "bitstamp";
    public static final String DEFAULT_CURRENCY_PAIR = "BTC/USD";

    public static final String DEFAULT_MINING_POOL = "BitMinter";
}
