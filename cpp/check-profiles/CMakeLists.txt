cmake_minimum_required (VERSION 3.1)

# get project name from directory
get_filename_component(ProjectId ${CMAKE_CURRENT_LIST_DIR} NAME)
string(REPLACE " " "_" ProjectId ${ProjectId})
project(ionic-sample C CXX)

set(CMAKE_CXX_STANDARD 14)
set(EXECUTABLE_OUTPUT_PATH ${PROJECT_BINARY_DIR}/target)
add_definitions(-D_GLIBCXX_USE_CXX11_ABI=0)
include_directories("$ENV{IONIC_SDK_PATH}/ISAgentSDKCpp/Include")
link_directories("$ENV{IONIC_SDK_PATH}/ISAgentSDKCpp/Lib/${platform}/Release/${architecture}")
include_directories("src")
file(GLOB IONIC_REGISTER_SOURCE src/*.cpp )
add_executable(${ProjectId} ${IONIC_REGISTER_SOURCE})

# osx specific
if ( platform STREQUAL "MacOSX" )
    target_link_libraries(${ProjectId} "ISAgentSDK")
    target_link_libraries(${ProjectId}
        "-framework CoreServices"
        "-framework CoreFoundation"
        "-framework IOKit"
        "-framework Security"
        "-framework Foundation"
    )
endif()

# linux specific
if ( platform STREQUAL "Linux" )
    target_link_libraries(${ProjectId} "ISAgentSDK")
    target_link_libraries(${ProjectId}
        "pthread"
        "curl"
        "stdc++"
        "m"
        "rt"
    )
    link_directories("/usr/lib")
endif()

# windows specific
if ( platform STREQUAL "Windows" )
    target_link_libraries(${ProjectId} "ISAgentSDK")
    #set(CMAKE_VS_PLATFORM_TOOLSET v140)
    set(CMAKE_GENERATOR_TOOLSET v140)
    set(CMAKE_CXX_FLAGS_RELEASE "/MT /D NDEBUG")
    set(CMAKE_CXX_FLAGS_DEBUG "/MTd")
endif()