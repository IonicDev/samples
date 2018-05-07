#!/bin/bash

SDK_DIR=$IONIC_SDK_PATH/ISAgentSDK

gcc -v -D__STDC_LIMIT_MACROS -I $SDK_DIR/Include src/IonicHelloWorld.cpp $SDK_DIR/Lib/Linux/Debug/x86_64/libISAgentSDK.a -lpthread -lcurl -lm -lstdc++ -o IonicHelloWorld
