# Ionic Chunk Decrypt Sample - Java

## Dependencies

* Java8
* Maven 3.3+
* [Java Cryptographic Extensions (JCE) Unlimited Strength](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
* [Ionic Java 2.x SDK](https://dev.ionic.com/getting-started/java-20.html)

## Build 
```
mvn package
```

## Run
```
java -jar target/ionic-chunk-decrypt.jar '<CHUNK_CRYPTO_CIPHERTEXT>'
```

> NOTE: This command expects a plaintext profile at `${HOME}/.ionicsecurity/profiles.pt`, and the ciphertext should be surrounded by single quotes. 
