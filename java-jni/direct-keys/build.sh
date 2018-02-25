#!/bin/bash

IS_SDK=${IONIC_REPO_ROOT}/ISAgentSDKJava/Lib/Linux/Release/universal/AgentSdkJava.jar
SRC=./src/main/java/com/ionic/direct-keys/IonicDirectKeys.java

javac -cp ${IS_SDK} ${SRC}

