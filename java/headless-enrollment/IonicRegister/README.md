# IonicRegister

This is a sample Eclipse project that shows how to use a Generated SAML Assertion to obtain a Secure Enrollment Profile (SEP).

The assertion should already have been generated using one of:
- Ionic Manager, using the CLI mode
- Server Enrollment Tool's `ionic_assertion` binary
- The example Java code in IonicAssertion, or a similar implementation

Furthermore, the enrollment endpoint must be setup to support the Generated SAML Assertion using a certificate that matches the key used in the assertion generation step.

This code consumes that assertion and utilizes the Ionic SDK to produce a SEP.

## Prerequisites

- The Ionic Java SDK
- Maven (to follow these instructions for building)

## Setup the SDK

- unzip the SDK into the working directory
- install the SDK into the local maven repository, after updating paths for your OS:
  ```
  mvn install:install-file -Dfile=IonicSDK_Java_OSX_1_2_0/Lib/MacOSX/Release/universal/AgentSdkJava.jar -DgroupId=com.ionic -DartifactId=sdk -Dversion=1.2.0 -Dpackaging=jar
  ```

## Configure the example

You can make configuration changes via the EnrollmentConstants project.

```java
public class EnrollmentConstants {
	public static final String ENROLLMENT_ENDPOINT = "https://dev-enrollment.ionic.com/keyspace/<keyspace>/sp/<tenant id>/<generated assertion endpoint name>/saml";
	public static final String ASSERTION_FILE = "/var/temp/samlAssertion.xml";
	//The following are required for IonicAssertion, and are not needed if you will only use IonicRegister:
	//public static final String PRIVATE_KEY_FILE = "/var/private/privatekey.pk8";
	//public static final String ENROLLMENT_USER = "server@mycompany.com";
	//public static final Integer VALID_DAYS_BEFORE = 2;
	//public static final Integer VALID_DAYS_AFTER = 2;
}
```

### ENROLLMENT_ENDPOINT 

Specifies the URL for an enrollment server associated with the tenant that we will enroll in.  This must match the configured Generated SAML Assertion enrollment endpoint for the EP.

### ASSERTION_FILE

Where the XML SAML Assertion can be found on the filesystem.
See IonicAssertion for generation of this file's contents.

## Build the example

- `mvn compile`
- `mvn assembly:single` 

## Run the example
Set the environment variable `IONIC_SDK_LIBDIR` to the path to your native dynamic libraries for the Ionic SDK, and then run:

```bash
./run.sh
```
