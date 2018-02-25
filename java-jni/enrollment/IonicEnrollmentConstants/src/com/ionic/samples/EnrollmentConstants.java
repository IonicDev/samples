/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.examples;

public class EnrollmentConstants {
	public static final String ENROLLMENT_ENDPOINT = "https://dev-enrollment.ionic.com/keyspace/<keyspace>/sp/<tenant id>/<headless endpoint name>/saml";
	public static final String ASSERTION_FILE = "/var/temp/samlAssertion.xml";
	public static final String PRIVATE_KEY_FILE = "/var/private/privatekey.pk8";
	public static final String ENROLLMENT_USER = "server@mycompany.com";
	public static final Integer VALID_DAYS_BEFORE = 2;
	public static final Integer VALID_DAYS_AFTER = 2;
}
