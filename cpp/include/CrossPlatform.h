#if defined(WIN32) || defined(_WIN32) || defined(__WIN32__) || defined(__NT__)
   // Windows
   #define OS "Windows"
   #ifdef _WIN64
      // 64 bit detected
      #define ARCH "x64"
   #else
      // 32 bit x86 detected
      #define ARCH "win32"
   #endif
#elif __APPLE__
    // Mac
    #include <TargetConditionals.h>
    #if TARGET_OS_MAC
        #define OS "MacOSX"
    #else
    #   error "Unknown Apple platform"
    #endif
    #define ARCH "universal"
#elif __linux__
    // Linux
    #define OS "Linux"
    #if defined(__x86_64__)
    // 64 bit detected
    #define ARCH "x86_64"
    #endif
    #if defined(__i386__)
    // 32 bit x86 detected
    #define ARCH "i386"
    #endif
#else
#   error "Unknown compiler"
#endif
