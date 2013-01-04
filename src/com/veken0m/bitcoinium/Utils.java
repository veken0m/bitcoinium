package com.veken0m.bitcoinium;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.money.CurrencyUnit;

import com.xeiam.xchange.mtgox.v1.dto.marketdata.MtGoxTicker;
import com.xeiam.xchange.virtex.dto.marketdata.VirtExTicker;

public class Utils {

	public static String formatDecimal(float valueToFormat, int numberOfDecimalPlaces, boolean useGroupings) {

		final NumberFormat numberFormat = DecimalFormat.getInstance();
		numberFormat.setMaximumFractionDigits(numberOfDecimalPlaces);
		numberFormat.setMinimumFractionDigits(numberOfDecimalPlaces);
		// Remove grouping if commas cause errors when parsing to
		// double/float
		numberFormat.setGroupingUsed(useGroupings);

		return numberFormat.format(valueToFormat);
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
	
	public static String getCurrencySymbol(String currencyCode){
		
		String symbol = "";
		
		if (!(currencyCode.equalsIgnoreCase("DKK")
				|| currencyCode.equalsIgnoreCase("BTC")
				|| currencyCode.equalsIgnoreCase("LTC")
				|| currencyCode.equalsIgnoreCase("NMC")
				|| currencyCode.equalsIgnoreCase("PLN")
				|| currencyCode.equalsIgnoreCase("RUB")
				|| currencyCode.equalsIgnoreCase("SEK")
				|| currencyCode.equalsIgnoreCase("SGD")
				|| currencyCode.equalsIgnoreCase("CHF")
				|| currencyCode.equalsIgnoreCase("RUR"))) {
			symbol = CurrencyUnit.of(currencyCode).getSymbol();
			symbol = symbol.substring(symbol.length() - 1);
		}
		
		return symbol;
	}

	public static double[] fetchVirtexTickerAlt()
			throws ClientProtocolException, IOException {

		double[] ticker = new double[4];

		HttpClient client = new DefaultHttpClient();
		HttpGet post = new HttpGet(
				"https://www.cavirtex.com/api/CAD/ticker.json");

		HttpResponse response = client.execute(post);

		ObjectMapper mapper = new ObjectMapper();
		VirtExTicker virtExTicker = mapper
				.readValue(new InputStreamReader(response.getEntity()
						.getContent(), "UTF-8"), VirtExTicker.class);
		ticker[0] = virtExTicker.getLast();
		ticker[1] = virtExTicker.getLow();
		ticker[2] = virtExTicker.getHigh();
		ticker[3] = virtExTicker.getVolume();

		return ticker;
	}

	public static double[] fetchMtgoxTickerAlt()
			throws ClientProtocolException, IOException {

		double[] ticker = new double[4];

		HttpClient client = new DefaultHttpClient();
		HttpGet post = new HttpGet(
				"https://mtgox.com/api/1/BTCUSD/public/ticker?raw");

		HttpResponse response = client.execute(post);

		ObjectMapper mapper = new ObjectMapper();
		MtGoxTicker Ticker = mapper.readValue(new InputStreamReader(response
				.getEntity().getContent(), "UTF-8"), MtGoxTicker.class);
		ticker[0] = Ticker.getLast().getValue();
		ticker[1] = Ticker.getLow().getValue();
		ticker[2] = Ticker.getHigh().getValue();
		ticker[3] = Ticker.getVol().getValue();

		return ticker;
	}
	
	public static boolean isBetween(float value, float min, float max)
	{
	  return((value >= min) && (value <= max));
	}
	
	public static String getCurrentTime()
	{
		final SimpleDateFormat sdf = new SimpleDateFormat("h:mm a",
				Locale.US);
		final String currentTime = sdf.format(new Date());
		
		return currentTime;
	}

}
