package com.pb.testautomation.label.exception;

/**
 * This exception is thrown if such a conversion unit is used which is not handled in the application.
 * 
 * @author ta013ba
 */
public class UnknownConversionUnitException extends LabelException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9077835444747239035L;

	/**
	 * Default Constructor
	 */
	public UnknownConversionUnitException() {
	}
	
	/**
	 * Constructor
	 * @param e	Original Exception
	 */
	public UnknownConversionUnitException(Exception e) {
		super(e);
	}

	/**
	 * Constructor
	 * @param message	The message of the exception.
	 */
	public UnknownConversionUnitException(String message) {
		super(message);
	}
}
