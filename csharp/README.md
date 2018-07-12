# Ionic C# SDK Samples

To build and run an Ionic C# Sample, navigate into the directory of a specific sample task and 
follow the steps below.  The Ionic Security C# SDK is used in all the samples.  Go to
[Ionic Developer SDK Setup](https://dev.ionic.com/tutorials/getting-started/sdk-setup) and
click on the C# icon.

Note: the sample apps expect a Password Persistor located at `~/.ionicsecurity/profiles.pw`. The password needs to be provided as an environment variable.

In a DOS window do:

1. `set IONIC_PERSISTOR_PASSWORD=password123`
1. `set IONIC_SDK_PATH=<sdk-path>` where `<sdk-path>` is the path where `ISAgentADKNetWrapper` is located.

## Windows

**Requirements:**
- Visual Studio 2017
- Ionic Security C# SDK

**Build:**

In Visual Studio:

* Open the solution file: `<app>.sln`.
* Set the build/run setting to `Release` or `Debug`.
* Set the architecture setting to be machine specific like `x64` and not `Any CPU`.
*  Click on `Build` -> `Build Solution`.

**Run:**

In Visual Studio you can click on `Debug` -> `Start Debugging` or `Debug` or `Start Without Debugging`.  The application will run in a popped-up DOS window.

The app can also be executed in a DOS window:

```
<app>\bin\<architecture>\Release\<app>.exe
```
