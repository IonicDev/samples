# IonicRegister

This is a sample Eclipse project that shows how to use a Generated SAML Assertion or Email method to obtain a Secure Enrollment Profile (SEP).

## Prerequisites

- The Ionic Java SDK
- Maven (to follow these instructions for building)

### Generated SAML Assertion

If using a Generated SAML Assertion, the assertion should already have been generated using one of:
- Ionic Manager, using the CLI mode
- Server Enrollment Tool's `ionic_assertion` binary
- The example Java code in IonicAssertion, or a similar implementation

Furthermore, the enrollment endpoint must be setup to support the Generated SAML Assertion using a certificate that matches the key used in the assertion generation step.

This code consumes that assertion and utilizes the Ionic SDK to produce a SEP.

## Setup the SDK

- unzip the SDK into the working directory
- install the SDK into the local Maven repository

For example:
```bash
unzip IonicJavaSDK-2.1.0.zip
cd IonicJavaSDK-2.1.0/ionic-sdk-java/Lib
mvn install:install-file -Dfile=ionic-sdk-2.1.0.jar -DpomFile=pom.xml
```

## Configure the example

This example prompts for information at the command line.

The example is also configured for saving to a plaintext profile persistor.
You should update the code in `IonicEnroll.java` to handle other persistor types as desired.

## Build the example

```bash
mvn package
```

## Run the example

```bash
java -jar target/enroll-0.1.0.jar
```
