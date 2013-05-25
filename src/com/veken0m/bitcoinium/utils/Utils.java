
package com.veken0m.bitcoinium.utils;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.xeiam.xchange.currency.Currencies;

import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;

public class Utils {

    public static String formatDecimal(float valueToFormat,
            int numberOfDecimalPlaces, boolean useGroupings) {

        final NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(numberOfDecimalPlaces);
        numberFormat.setMinimumFractionDigits(numberOfDecimalPlaces);
        // Remove grouping if commas cause errors when parsing to
        // double/float
        numberFormat.setGroupingUsed(useGroupings);

        return numberFormat.format(valueToFormat);
    }

    public static String formatDecimal(BigDecimal valueToFormat,
            int numberOfDecimalPlaces, boolean useGroupings) {

        final NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(numberOfDecimalPlaces);
        numberFormat.setMinimumFractionDigits(numberOfDecimalPlaces);
        numberFormat.setGroupingUsed(useGroupings);

        try {
            return numberFormat.format(valueToFormat.doubleValue());
        } catch (Exception e) {
            return "N/A";
        }
    }

    public static String formatWidgetMoney(float amount, String currencyCode,
            boolean includeCurrencyCode) {

        String symbol = getCurrencySymbol(currencyCode);

        if (includeCurrencyCode) {
            currencyCode = " " + currencyCode;
        } else {
            currencyCode = "";
        }

        // If too small, scale the value
        if (amount < 0.1) {
            amount *= 1000;
            currencyCode = currencyCode.replace(" ", " m");

        }

        return symbol + formatDecimal(amount, 2, false) + currencyCode;
    }

    public static String getCurrencySymbol(String currencyCode) {

        String symbol = "";

        if (!(currencyCode.equalsIgnoreCase("DKK")
                || currencyCode.equalsIgnoreCase("BTC")
                || currencyCode.equalsIgnoreCase("LTC")
                || currencyCode.equalsIgnoreCase("NMC")
                || currencyCode.equalsIgnoreCase("PLN")
                || currencyCode.equalsIgnoreCase("RUB")
                || currencyCode.equalsIgnoreCase("SEK")
                || currencyCode.equalsIgnoreCase("SGD")
                || currencyCode.equalsIgnoreCase("CHF") || currencyCode
                    .equalsIgnoreCase("RUR"))) {
            symbol = CurrencyUnit.of(currencyCode).getSymbol();
            symbol = symbol.substring(symbol.length() - 1);
        }

        return symbol;
    }

    public static boolean isBetween(float value, float min, float max) {

        return ((value >= min) && (value <= max));
    }

    public static String getCurrentTime(Context ctxt) {
        Date time = new Date();
        DateFormat.getTimeFormat(ctxt).format(time);

        StringBuilder sb = new StringBuilder();
        sb.append(DateFormat.format("E", time));
        sb.append(" ");
        sb.append(DateFormat.getTimeFormat(ctxt).format(time));

        return sb.toString();
    }

    public static String dateFormat(Context ctxt, long date) {
        Date dateFormatted = new Date(date);
        StringBuilder sb = new StringBuilder();
        sb.append(DateFormat.format("MMM dd", dateFormatted));
        sb.append(" @ ");
        sb.append(DateFormat.getTimeFormat(ctxt).format(dateFormatted));
        return sb.toString();
    }

    public static void setTextViewParams(TextView tv, String text) {

        LayoutParams params = new TableRow.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

        tv.setText(text);
        tv.setLayoutParams(params);
        tv.setGravity(1);
    }

    /*
     * Currency pair defined as "baseCurrency/counterCurrency". This methods
     * splits the pair into it's components and removes the "/"
     */
    public static void splitCurrencyPair(String currencyPair, String exchangeName, boolean appendBase) {

        // BTC is default baseCurrency for backwards compatibility with previous
        // currency pair format before Alt. Coins were introduced.
        String baseCurrency = Currencies.BTC;
        String counterCurrency = currencyPair;

        if (currencyPair.contains("/")) {
            baseCurrency = currencyPair.substring(0, 3);
            counterCurrency = currencyPair.substring(4, 7);
            if (!baseCurrency.equals(Currencies.BTC) && appendBase) {
                exchangeName = exchangeName + " (" + baseCurrency + ")";
            }
        }
    }

}
