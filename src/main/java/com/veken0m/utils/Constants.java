package com.veken0m.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    //Constants
    public static final String REFRESH = "com.veken0m.bitcoinium.REFRESH";
    public static final String PREFS_NAME_MINER = "com.veken0m.bitcoinium.MinerWidgetProvider";
    public static final String PREFS_NAME_PRICE = "com.veken0m.bitcoinium.WidgetProvider";
    public static final String[] METRIC_UNITS = {"m", "µ", "n", "p", "f"};
    // Defaults
    public static final String DEFAULT_EXCHANGE = "bitstamp";
    public static final String DEFAULT_CURRENCY_PAIR = "BTC/USD";
    public static final String DEFAULT_MINING_POOL = "BitMinter";
    public static Map<String, String> CRYPTO_SYMBOLS = new HashMap<String, String>();
    static {
        CRYPTO_SYMBOLS = new HashMap<String, String>();
        CRYPTO_SYMBOLS.put("BTC", "฿");
        CRYPTO_SYMBOLS.put("XBT", "฿");
        CRYPTO_SYMBOLS.put("LTC", "Ł");
        CRYPTO_SYMBOLS.put("DOGE", "Ð");
        CRYPTO_SYMBOLS.put("XDG", "Ð");
    }
}
