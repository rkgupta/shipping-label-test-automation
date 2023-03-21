package com.pb.testautomation.label.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import com.pb.testautomation.label.constant.CommonConstants;
/**
 * Utility class that gives message for a key from property file. It is used for
 * internationalization of the log messages.
 * @author RA013GU
 *
 */
public class Messages {
	
	private static String language;
	private static String country;
	/*
	 * Initialize language and country in case they are not defined in properties file.
	 */
	static {
		language = LabelUtil.getProperties().getProperty("language");
		if(language == null){
			language = "en";	//default language.
		}
		country = LabelUtil.getProperties().getProperty("country");
		if(country == null){
			country = "US";		// default country.
		}
	}
	/**
	 * Returns message for the given key from properties file.
	 * @param key - Message key.
	 * @param args -	List of values for placeholders in properties file.
	 * @return The message for the given key from properties file.
	 */
	public static String getMessage(String key, Object... args) {
		// Decide which locale to use
		Locale currentLocale = new Locale(language, country);
		ResourceBundle messages = ResourceBundle.getBundle("messages", currentLocale);
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(currentLocale);
		formatter.applyPattern(messages.getString(key));
		String output = formatter.format(args);
		return output;
	
	}
	
	public static void main(String args[]) {
		System.out.println("The Locale of the system is: "+System.getProperty("user.language")+"_"+System.getProperty("user.country"));
		System.out.println(Messages.getMessage(CommonConstants.CLASS_NOT_FOUND_EXCEPTION, null));
		//System.out.println(Messages.getMessage(CommonConstants.MASKING_EXCEPTION, "abc", "def"));
	}
}
