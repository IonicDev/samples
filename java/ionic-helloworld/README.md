# Ionic HelloWorld - Java

## Dependencies

* Java8
* Maven 3.3+
* [Java Cryptographic Extensions (JCE) Unlimited Strength](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
* Installed [Ionic Java 2.x SDK](https://dev.ionic.com/getting-started/java-20.html)

## Build 
```
mvn package
```

## Run
```
java -jar target/ionic-helloworld.jar 
```

> NOTE: This sample expects a password profile in the user's home directory, e.g. `${HOME}/.ionicsecurity/profiles.pw`
> or `C:\Users\username\.ionicsecurity\profiles.pw` on Windows.
