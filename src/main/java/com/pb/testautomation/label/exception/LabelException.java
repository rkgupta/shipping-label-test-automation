package com.pb.testautomation.label.exception;

import com.pb.testautomation.label.util.CommonUtil;

/**
 * This exception represents the base class of all the custom exceptions.
 * 
 * @author ta013ba
 *
 */
public class LabelException extends Exception {
	private static final long serialVersionUID = 1L;
	protected String message;
	

	/**
	 * Default constructor
	 */
	public LabelException() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Constructor
	 * @param e	Original Exception
	 */
	public LabelException(Exception e) {
		super(e);
	}
	
	/**
	 * Constructor
	 * @param message	The message of the exception.
	 */
	public LabelException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		if(CommonUtil.isEmpty(message)) {
			return super.getMessage();			
		} else {
			return message;
		}
	}
}
