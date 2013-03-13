package com.veken0m.bitcoinium;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.joda.money.CurrencyUnit;

import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class Utils {

	public static String formatDecimal(float valueToFormat,
			int numberOfDecimalPlaces, boolean useGroupings) {

		final NumberFormat numberFormat = DecimalFormat.getInstance();
		numberFormat.setMaximumFractionDigits(numberOfDecimalPlaces);
		numberFormat.setMinimumFractionDigits(numberOfDecimalPlaces);
		// Remove grouping if commas cause errors when parsing to
		// double/float
		numberFormat.setGroupingUsed(useGroupings);

		return numberFormat.format(valueToFormat);
	}

	public static String formatDecimal(BigDecimal valueToFormat,
			int numberOfDecimalPlaces, boolean useGroupings) {

		final NumberFormat numberFormat = DecimalFormat.getInstance();
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

	public static String getCurrentTime() {
		final SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
		final String currentTime = sdf.format(new Date());

		return currentTime;
	}

	public static void setTextViewParams(TextView tv, String text) {

		LayoutParams params = new TableRow.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);

		tv.setText(text);
		tv.setLayoutParams(params);
		tv.setGravity(1);
	}

}
