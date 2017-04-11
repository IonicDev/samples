package com.ionic.examples;

/**
* an Exception that we can use for enrollment errors
* @author Ionic Security
*
*/
public class IonicRegisterException extends Exception {

	private static final long serialVersionUID = 1L;

	public IonicRegisterException() {
	}

	public IonicRegisterException(String message) {
		super(message);
	}

	public IonicRegisterException(Throwable cause) {
		super(cause);
	}

	public IonicRegisterException(String message, Throwable cause) {
		super(message, cause);
	}

	public IonicRegisterException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
