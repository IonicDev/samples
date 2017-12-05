#!/bin/bash

if [ `uname` == "Linux" ]; then
    SDK_LIBDIR=Linux/Release/x86_64
elif [ `uname` == "Darwin" ]; then
    SDK_LIBDIR=MacOSX/Release/universal
else
    echo "Unexpected OS"
    exit 1
fi

IONIC_CLASSPATH="$IONIC_SDK_PATH/ISAgentSDKJava/Lib/${SDK_LIBDIR}"
export MAVEN_OPTS="-Djava.library.path=${IONIC_CLASSPATH}"
ARGS=\"$@\"
eval mvn exec:java -Dexec.args="$ARGS"
