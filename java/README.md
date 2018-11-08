# Ionic Java SDK Samples

To build and run an Ionic C++ SDK Sample, navigate into the directory of a specific sample task and follow the steps below depending on your platform.
Note: the sample apps expect a Password Persistor located at `~/.ionicsecurity/profiles.pw`. The password needs to be provided as an environment variable.

```bash
export IONIC_PERSISTOR_PASSWORD=password123
```

**Requirements**:
- Maven
- Java 8

**Build:**
```
mvn package
```

**Run:**
```
java -jar target/<app>.jar
```
  
