package com.veken0m.utils;

import org.knowm.xchange.currency.CurrencyPair;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

public class CurrencyUtils
{
    public static String getSymbol(String currencyCode)
    {
        try
        {
            List<String> ignoredCurrencies = Arrays.asList("DKK", "NMC", "PLN", "RUB", "SEK", "SGD", "XVN", "XRP", "CHF", "RUR");

            if (!(ignoredCurrencies.contains(currencyCode)))
            {
                if (Constants.CRYPTO_SYMBOLS.containsKey(currencyCode))
                {
                    return Constants.CRYPTO_SYMBOLS.get(currencyCode);
                }
                else
                {
                    String symbol = Currency.getInstance(currencyCode).getSymbol();
                    return symbol.substring(symbol.length() - 1);
                }
            }
            return "";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }

    public static CurrencyPair stringToCurrencyPair(String currencyPair)
    {
        if (currencyPair.contains("/"))
        {
            String[] currPair = currencyPair.split("/");
            return new CurrencyPair(currPair[0], currPair[1]);
        }
        else
            return new CurrencyPair(org.knowm.xchange.currency.Currency.BTC.getCurrencyCode(), currencyPair);
    }

    public static String formatPayout(float amount, int payoutUnits, String symbol)
    {
        DecimalFormat df = new DecimalFormat("#.#######");

        switch (payoutUnits)
        {
            case 0:
                df = new DecimalFormat("#.####");
                if (amount < 0.0001)
                {
                    return df.format(amount * 1000000) + " µ" + symbol;
                }
                else if (amount < 0.1)
                {
                    return df.format(amount * 1000) + " m" + symbol;
                }
                else
                {
                    return df.format(amount) + " " + symbol;
                }
            case 1:
                df = new DecimalFormat("#.#######");
                return df.format(amount) + " " + symbol;
            case 2:
                df = new DecimalFormat("#.####");
                return df.format(amount * 1000) + " m" + symbol;
            case 3:
                df = new DecimalFormat("#.####");
                return df.format(amount * 1000000) + " µ" + symbol;
        }
        return df.format(amount) + " " + symbol;
    }
}
