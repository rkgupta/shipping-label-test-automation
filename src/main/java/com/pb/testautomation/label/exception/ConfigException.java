package com.pb.testautomation.label.exception;

import java.util.List;

import com.pb.testautomation.label.util.CommonUtil;

/**
 * This class represents the config exception. This exception is thrown if there is any structural problem with the configuration file.
 * 
 * @author ta013ba
 */
public class ConfigException extends Exception {
	private String message;
	private List<String> messages;
	
	private static final long serialVersionUID = 5331303883820072400L;

	public ConfigException() {}
	
	/**
	 * Constructor
	 * @param e	Original Exception
	 */
	public ConfigException(Exception e) {
		super(e);
	}
	
	/**
	 * Constructor
	 * @param message	The message of the exception.
	 */
	public ConfigException(String message) {
		this.message = message;
	}
	
	/**
	 * This method consolidates all the error messages representing the problems in the config file.
	 */
	@Override
	public String getMessage() {
		StringBuilder messageBuilder = new StringBuilder();
		if(messages != null && !messages.isEmpty()) {
			for(String message: messages) {
				if(messageBuilder.length() > 0) {
					messageBuilder.append("\n\t\t\t\t");					
				}
				messageBuilder.append(message);
			}
		}
		
		if(!CommonUtil.isEmpty(message)) {
			if(messageBuilder.length() > 0) {
				messageBuilder.append("\n\t\t\t\t");					
			}
			messageBuilder.append(message);
		}
		
		if(messageBuilder.length() == 0) {
			return super.getMessage();
		} else {
			return messageBuilder.toString();
		}
	}

	/**
	 * @return the messages
	 */
	public List<String> getMessages() {
		return messages;
	}

	/**
	 * This method sets the list of the messages, which represents all the problems with the configuration file.
	 * @param messages the messages to set
	 */
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
