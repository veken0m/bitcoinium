package com.veken0m.cavirtex;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.money.CurrencyUnit;

import com.xeiam.xchange.Currencies;
import com.xeiam.xchange.mtgox.v1.dto.marketdata.MtGoxTicker;
import com.xeiam.xchange.virtex.dto.marketdata.VirtExTicker;

public class Utils {

	public static String formatTwoDecimals(float valueToFormat) {

		NumberFormat numberFormat = DecimalFormat.getInstance();
		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumFractionDigits(2);
		// Remove grouping because the commas cause errors when parsing to
		// double/float
		numberFormat.setGroupingUsed(false);

		return numberFormat.format(valueToFormat);
	}

	public static String formatFiveDecimals(float valueToFormat) {

		NumberFormat numberFormat = DecimalFormat.getInstance();
		numberFormat.setMaximumFractionDigits(5);
		numberFormat.setMinimumFractionDigits(5);
		// Remove grouping because the commas cause errors when parsing to
		// double/float
		numberFormat.setGroupingUsed(false);

		return numberFormat.format(valueToFormat);
	}
	
	public static String formatNoDecimals(float valueToFormat) {

		NumberFormat numberFormat0 = DecimalFormat.getInstance();
		numberFormat0.setMaximumFractionDigits(0);
		numberFormat0.setMinimumFractionDigits(0);
		numberFormat0.setGroupingUsed(true);

		return numberFormat0.format(valueToFormat);
	}

	public static String formatMoney(String moneyToFormat, String currency) {
		String symbol = CurrencyUnit.of(currency).getSymbol();
		symbol = symbol.substring(symbol.length() - 1); 
		if(currency.equalsIgnoreCase("DKK") || currency.equalsIgnoreCase("PLN") || currency.equalsIgnoreCase("RUB") || currency.equalsIgnoreCase("SEK") || currency.equalsIgnoreCase("SGD") || currency.equalsIgnoreCase("CHF")){
			symbol = "";
		}
		
		String money = "" + symbol + moneyToFormat + " " + currency;

		return money;
	}
	
	public static String formatMoney2(String moneyToFormat, String currency) {
		String symbol = CurrencyUnit.of(currency).getSymbol();
		symbol = symbol.substring(symbol.length() - 1); 
		if(currency.equalsIgnoreCase("DKK") || currency.equalsIgnoreCase("PLN") || currency.equalsIgnoreCase("RUB") || currency.equalsIgnoreCase("SEK") || currency.equalsIgnoreCase("SGD") || currency.equalsIgnoreCase("CHF")){
			symbol = "";
		}
		
		String money = "" + symbol + moneyToFormat;

		return money;
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

}
