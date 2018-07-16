## Build the Project

### Establish IONIC_SDK_PATH

Note that you will need to set the environment variable `$IONIC_SDK_PATH` to point wherever you extracted the Ionic SDK.
This directory should include the Lib and Include directories.

For example:
```bash
export IONIC_SDK_PATH=/Users/jdoe/Desktop/IonicRepoRoot/
```
This path should contain the folder `ISAgentSDKJava`.

### Install Ionic SDK into Maven Local Cache

```bash
mvn install:install-file -Dfile=${IONIC_SDK_PATH}/ISAgentSDKJava/Lib/Linux/Release/universal/AgentSdkJava.jar -DgroupId=com.ionicsecurity -DartifactId=sdk -Dversion=1.2.1 -Dpackaging=jar
```

## Run the Application

### Load Ionic Security Credentials

Ensure that your Ionic device credentials can be located at `${user.home}/.ionicsecurity/profile.pt` or that your platform will use a default persistor.

### VM argument

When running the java application, the following flag should be added to link the SDK's native (JNI) libraries.

For example:
```plain
-Djava.library.path=/Users/jdoe/Desktop/javaSDK/Lib/MacOSX/Debug/universal
```

### Using Maven and the run.sh script
You can execute the code using the run script ala ```./run.sh myfile.doc myoutputfile.doc```. This uses mvn exec:java to launch the executable.

## How to use Ionic SDK with `FileCryptoCoverPageImpl` class

The executable `FileCryptoAutoEncrypt` demonstrates how to use the `FileCryptoCoverPageImpl` in conjunction with the Ionic SDK in order to encrypt a file with a custom cover page.
First, the cache is initiated with a call to the static `cacheInit` method.
This is only called once, otherwise the original cache will be lost and replaced by a new, empty one. 

Next, an `AutoFileCipher` is instantiated with the arguments being an agent, and a `FileCryptoCoverPageImpl` instance which has been instantiated with that same agent.
It is important to keep in mind that the SEP which aforementioned agent is connected to will determine (along with the type of the file to be encrypted) what cover page is retrieved and used in the encrypted document.
