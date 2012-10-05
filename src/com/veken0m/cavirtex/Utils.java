package com.veken0m.cavirtex;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Utils {
	
	
	public static String formatTwoDecimals(float valueToFormat){
		
	NumberFormat numberFormat = DecimalFormat.getInstance();
	numberFormat.setMaximumFractionDigits(2);
	numberFormat.setMinimumFractionDigits(2);
	numberFormat.setGroupingUsed(false);
	
	return numberFormat.format(valueToFormat);
	}
	
	public static String formatFiveDecimals(float valueToFormat){
		
		NumberFormat numberFormat = DecimalFormat.getInstance();
		numberFormat.setMaximumFractionDigits(5);
		numberFormat.setMinimumFractionDigits(5);
		numberFormat.setGroupingUsed(false);
		
		return numberFormat.format(valueToFormat);
	}
	
	public static String formatMoney(String moneyToFormat, String currency){
		
		String money = "$" + moneyToFormat + " " + currency;
		
		return money;
	}
	

}
