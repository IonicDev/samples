# Ionic C++ SDK Samples

To build and run an Ionic C++ SDK Sample, navigate into the directory of a specific sample task and follow the steps below depending on your platform.

Note: the sample apps expect a Password Persistor located at `~/.ionicsecurity/profiles.pw`. The password needs to be provided as an environment variable.

```bash
export IONIC_PERSISTOR_PASSWORD=password123
```


## Linux

**Requirements**:
- g++
- CMake 3.1

**Build:**
```
export IONIC_SDK_PATH=<PATH_TO_IONIC_SDK>
cmake -Dplatform=Linux -Darchitecture=x86_64
make
```

**Run:**
```
./target/<app>
```

  
## MacOSX

**Requirements**:
- g++
- CMake 3.1

**Build:**
```
export IONIC_SDK_PATH=<PATH_TO_IONIC_SDK>
cmake -Dplatform=MacOSX -Darchitecture=universal
make
```

**Run:**
```
./target/<app>
```

## Windows

**Requirements:**
- Visual Build Tools: Toolset v140 (including ATL libs)
- CMake 3.1

**Build:**
```
set IONIC_SDK_PATH=<PATH_TO_IONIC_SDK>
cmake -Dplatform=Windows -Darchitecture=Win32
msbuild /p:Architecture=Win32 /p:Configuration=Release ionic-sample.sln
```

**Run:**
```
targets\Release\<app>.exe
```