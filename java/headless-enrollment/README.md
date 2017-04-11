# Server Enrollment Example

## Setup

This project uses maven. See the `pom.xml` files for dependencies. The project was built in Eclipse.

You must set the environment variable `IONIC_SDK_LIBDIR` to the path that contains the dynamic libraries.

The Ionic SDK is expected to be loaded into your Maven repository, such as with:
```bash
mvn install:install-file -Dfile=../../ISAgentSDKJava/Lib/MacOSX/Debug/universal/AgentSdkJava.jar -DgroupId=com.ionic -DartifactId=sdk -Dversion=0.5.0 -Dpackaging=jar
```

## Configuration 

Edit the EnrollmentConstants.java class to match your environment:

- ASSERTION_FILE should be be the path to a file that will store a generated assertion. This file will be overwritten on execution of SVREnroll.main.

- ENROLLMENT_ENDPOINT should be set to the headless SAML enrollment end point you configured on your enrollment server.

- ENROLLMENT_USER will be the Ionic user in your tenant whose identity is asserted.  For example: server@myco.com.

- PRIVATE_KEY_FILE should contain the PEM pkcs8 formatted  private key for your enrollment end point.

## Build

First you must build the EnrollmentConstants JAR:

```bash
cd IonicEnrollmentConstants
mvn clean install
```

Change back to the root of the sample, and then generate an assertion:

```bash
cd HeadlessAssertion
# Build and run it, generating the file ASSERTION_FILE
mvn clean compile exec:java
```

Change back to the root of the sample, and then utilize this assertion.
This relies on the SDK being added to your Maven repository, with a matching version to what this `pom.xml` expects (see above).

```bash
cd IonicRegister
# This builds and runs:
./run.sh
```
