/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.examples;

/**
* An Exception that we can use for enrollment errors
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
