package com.veken0m.bitcoinium;

public class Exchange{

	private final String exchange_name;
	private final String class_name;
	private final String main_currency;
	//private final String supported_currencies;
	private final String logo_path;
	private final int notification_id;
	private final String prefix;
	
	public Exchange(String[] exchangeProperties) {
		exchange_name = exchangeProperties[0];
		class_name = exchangeProperties[1];
		main_currency = exchangeProperties[2];
		logo_path = exchangeProperties[3];
		notification_id = Integer.parseInt(exchangeProperties[4]);
		prefix = exchangeProperties[5];
	}
	
	public String getExchangeName() {

		return exchange_name;
	}

	public String getClassName() {

		return class_name;
	}

	public String getMainCurrency() {

		return main_currency;
	}
	
	public String getLogo() {

		return logo_path;
	}

	public int getNotificationID() {

		return notification_id;
	}
	
	public String getPrefix() {

		return prefix;
	}

}
