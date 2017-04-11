# IonicAssertion

An example on how to generate a SAML assertion for use in headless enrollment

## Prerequisites

- Maven
- Eclipse
- This project requires the com.ionic.examples class.  This class is supplied by the IonicRegister project, but if needed, you can copy that class into this project

## Configure the example

You can make configuration changes via the EnrollmentConstants project.

```
public class EnrollmentConstants {
	public static final String ENROLLMENT_ENDPOINT = "https://dev-enrollment.ionic.com/keyspace/<keyspace>/sp/<tenant id>/<headless endpoint name>/saml";
	public static final String ASSERTION_FILE = "/var/temp/samlAssertion.xml";
	public static final String PRIVATE_KEY_FILE = "/var/private/privatekey.pk8";
	public static final String ENROLLMENT_USER = "server@mycompany.com";
	public static final Integer VALID_DAYS_BEFORE = 2;
	public static final Integer VALID_DAYS_AFTER = 2;
}
```


### ENROLLMENT_ENDPOINT

Specfies the URL for an enrollment server associated with the tenant that we will enroll in.  This must match the configured headless enrollment endpoint for the EP.

### ASSERTION_FILE

Where the XML SAML Assertion can be found on the filesystem.  See IonicAssertion for generation of this file's contents.

### PRIVATE_KEY_FILE

This is a PEM file that contains the RSA private key that matches the public key configured on the headless enrollment endpoint for the EP. This file must be a pkcs8 file.

### ENROLLMENT_USER

This must match a user configured on the tenant targeted.

## Build and run
- mvn clean compile exec:java
