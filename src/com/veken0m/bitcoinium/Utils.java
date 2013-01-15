package com.veken0m.bitcoinium;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.joda.money.CurrencyUnit;

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

		double formattedValue;

		try {
			formattedValue = valueToFormat.doubleValue();
		} catch (Exception e) {
			formattedValue = 0;
		}

		final NumberFormat numberFormat = DecimalFormat.getInstance();
		numberFormat.setMaximumFractionDigits(numberOfDecimalPlaces);
		numberFormat.setMinimumFractionDigits(numberOfDecimalPlaces);
		// Remove grouping if commas cause errors when parsing to
		// double/float
		numberFormat.setGroupingUsed(useGroupings);

		return numberFormat.format(formattedValue);
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
		final SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
		final String currentTime = sdf.format(new Date());

		return currentTime;
	}

}
