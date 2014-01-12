
package com.veken0m.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.currency.CurrencyPair;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Utils {

    public static final LayoutParams symbolParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);

    public static String formatDecimal(float valueToFormat,
            int numberOfDecimalPlaces, boolean useGroupings) {

        final NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(numberOfDecimalPlaces);
        numberFormat.setMinimumFractionDigits(numberOfDecimalPlaces);
        // Remove grouping if commas cause errors when parsing to double/float
        numberFormat.setGroupingUsed(useGroupings);

        return numberFormat.format(valueToFormat);
    }

    public static String formatDecimal(BigDecimal valueToFormat) {

        final NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setGroupingUsed(true);

        try {
            return numberFormat.format(valueToFormat.doubleValue());
        } catch (Exception e) {
            return "N/A";
        }
    }

    public static String formatWidgetMoney(float amount, CurrencyPair pair,
            boolean includeCurrencyCode, boolean displayInMilliBtc) {

        String symbol = getCurrencySymbol(pair.counterCurrency);
        int numOfDecimals = 2;
        
        // If BTC and user wants price in mBTC
        if (displayInMilliBtc && pair.baseCurrency.equalsIgnoreCase(Currencies.BTC)) {
            amount /= 1000;
            numOfDecimals = 3;
        }
        
        String currencyCode = (includeCurrencyCode) ? " " + pair.counterCurrency : "";
            
            // If too large, remove a digit behind decimal
            if (amount >= 1000 && !includeCurrencyCode)
                numOfDecimals--;

            // If too small, scale the value
            if (amount < 0.1) {
                amount *= 1000;
                currencyCode = currencyCode.replace(" ", " m");
            }

        return symbol + formatDecimal(amount, numOfDecimals, false) + currencyCode;
    }

    public static String getCurrencySymbol(String currencyCode) {

        String symbol = "";
        
        List<String> ignoredCurrencies =  Arrays.asList("DKK","BTC","LTC","NMC","PLN","RUB","SEK","SGD","XVN","XRP","CHF","RUR");

        if (!(ignoredCurrencies.contains(currencyCode))) {
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

        return DateFormat.format("E", time) + " " + DateFormat.getTimeFormat(ctxt).format(time);
    }

    public static String dateFormat(Context ctxt, long date) {
        Date dateFormatted = new Date(date);

        return DateFormat.format("MMM dd", dateFormatted) + " @ " + DateFormat.getTimeFormat(ctxt).format(dateFormatted);
    }

    public static void setTextViewParams(TextView tv, BigDecimal value) {

        LayoutParams params = new TableRow.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

        tv.setText(Utils.formatDecimal(value));
        tv.setLayoutParams(params);
        tv.setGravity(1);
    }

    public static void setTextViewParams(TextView tv, BigMoney value) {

        LayoutParams params = new TableRow.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

        tv.setText(Utils.formatDecimal(value.getAmount()));
        tv.setLayoutParams(params);
        tv.setGravity(1);
    }

    public static String formatHashrate(float hashRate) {

        DecimalFormat df = new DecimalFormat("#0.00");
        if (hashRate > 999) {
            return df.format((hashRate / 1000)) + " GH/s";
        } else {
            return df.format((hashRate)) + " MH/s";
        }
    }
    
    public static Dialog errorDialog(Context context, String msg, String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setTitle(title);
        builder.setPositiveButton("OK", null);
        builder.show();

        return builder.create();
    }

    public static Dialog errorDialog(Context context, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", null);
        builder.show();

        return builder.create();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    // Inserts a divider to separate rows in table
    public static void insertDivider(Context context, TableLayout table) {

        View divider = new View(context);
        divider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(Color.rgb(51, 51, 51));
        table.addView(divider);
    }

}
