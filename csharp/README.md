# Ionic C# Samples

Currently there are two styles of C# samples:

1. Solution file per sample
1. One solution file for all samples

The first style, one solution file per sample, is geared toward C# SDK 1.8.0 version, which
is [FIPS compliant](https://ionic.com/developers/machina-sdk-releases-and-fips-140-2/). This
style requires a SDK download. Solution
files are located under the sample directory.  Project files are located under the `<sample>/old` directory.  The source file, which is common to both styles, is located at
`<sample>/<app>/Program.cs`.

The second style, one solution file for all samples, is geared toward C# SDK 2.0.0 and above versions,
which are [non-FIPS compliant](https://ionic.com/developers/machina-sdk-releases-and-fips-140-2/).
This style uses NuGet to obtain the SDK. The solution file is named `IonicSamples.sln`.
Project files for each sample are located in
`<sample directory>/NuGet` directory. As stated above, the source file is common to both styles located at
`<sample>/<app>/Program.cs`.

## Solution File per Sample
To build and run an Ionic C# Sample, navigate into the directory of a specific sample task and 
follow the steps below.  The Ionic C# SDK version 1.8.0 is used in all the samples.  Go to [Ionic Developer SDK Setup](https://dev.ionic.com/getting-started/sdk-setup) and click on the C# icon.

### Requirements
- Visual Studio 2017
- Ionic Security C# SDK
- A password Persistor located at `~/.ionicsecurity/profiles.pw`. The persistor password needs to be provided in the environment variable `IONIC_PERSISTOR_PASSWORD`.

In PowerShell window do:

1. `$IONIC_PERSISTOR_PASSWORD="yourPass"`
2. `$IONIC_SDK_PATH=<sdk-path>` where `<sdk-path>` is the path where `ISAGentSDKNetWrapper` is located.

Or in a DOS window do:

1. `set IONIC_PERSISTOR_PASSWORD=password123`
1. `set IONIC_SDK_PATH=<sdk-path>` where `<sdk-path>` is the path where `ISAgentSDKNetWrapper` is located.

In PowerShell window do:
`$IONIC_PERSISTOR_PASSWORD="yourPass"`

Or in a DOS window do:

1. `set IONIC_PERSISTOR_PASSWORD=password123`
1. `set IONIC_SDK_PATH=<sdk-path>` where `<sdk-path>` is the path where `ISAgentADKNetWrapper` is located.

Note: the samples expect a Password Persistor located at `~/.ionicsecurity/profiles.pw`. The password needs to be provided as an environment variable.

More information:

- [Password persistor](https://dev.ionic.com/getting-started/create-ionic-profile)
- [Persistor password in environment variable](https://dev.ionic.com/getting-started/hello-world)

### Build

In Visual Studio:

* Open the solution file: `<app>.sln`.
* Set the build/run setting to `Release` or `Debug`.
* Set the architecture setting to be machine specific like `x64` and not `Any CPU`.
*  Click on `Build` -> `Build Solution`.

### Execute

In Visual Studio you can click on `Debug` -> `Start Debugging` or `Debug` or `Start Without Debugging`.  The application will run in a popped-up DOS window.

The app can also be executed in a DOS or PowerShell window:

```
<sample>\old\bin\<architecture>\<Release|Debug>\<app>.exe
```

For example: `keys\nuget\bin\x64\Debug\Keys.exe`.

## One Solution File for All Samples

These C# examples use the C# SDK 2.0.0 along with the NuGet package manager.  This means
that you don't have to download the C# SDK manually.  In this directory, there is one
solution file for all the examples.

### Requirements
- Visual Studio 2017
- A Password Persistor located at `~/.ionicsecurity/profiles.pw`. The persistor password needs to be provided in the environment variable `IONIC_PERSISTOR_PASSWORD`.

In PowerShell window do:
`$IONIC_PERSISTOR_PASSWORD="yourPass"`

Or in a DOS window do:

1. `set IONIC_PERSISTOR_PASSWORD=password123`
1. `set IONIC_SDK_PATH=<sdk-path>` where `<sdk-path>` is the path where `ISAgentADKNetWrapper` is located.

In PowerShell window do:
`$IONIC_PERSISTOR_PASSWORD="yourPass"`

Or in a DOS window do:

1. `set IONIC_PERSISTOR_PASSWORD=password123`
1. `set IONIC_SDK_PATH=<sdk-path>` where `<sdk-path>` is the path where `ISAgentADKNetWrapper` is located.

More information:

- [Password persistor](https://dev.ionic.com/getting-started/create-ionic-profile)
- [Persistor password in environment variable](https://dev.ionic.com/getting-started/hello-world)

### Build

In Visual Studio:

- Open the solution file: `IonicSamples.sln`.
- Set the build/run setting to `Release` or `Debug`.
- Set the architecture setting to be machine specific like `x64` or `x86`, but not `Any CPU`.
- Click on `Build` -> `Build Solution`.  This builds all the C# examples.

### Execute

In Visual Studio:

- Set the project setting (left of the architecture setting) to the example you want to execute.
- Click on `Debug` -> `Start Debugging` or `Debug` -> `Start Without Debugging`.  The application will run in a popped-up DOS window.

Also the app can also be executed in a DOS or PowerShell window:

```
<sample>\nuget\bin\<architecture>\<Release|Debug>\<app>.exe
```

For example: `keys\nuget\bin\x64\Debug\Keys.exe`.

## More information

There are more C# examples in the [GitHub samples repository](http:/https://github.com/IonicDev/samples/tree/master/csharp/).
