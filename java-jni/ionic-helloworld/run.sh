#!/bin/bash

IS_SDK_LIB=${IONIC_REPO_ROOT}/ISAgentSDKJava/Lib/Linux/Release/x86_64
IS_SDK_JAR=${IONIC_REPO_ROOT}/ISAgentSDKJava/Lib/Linux/Release/universal/AgentSdkJava.jar

MAIN_CLASS=IonicHelloWorld
java -Djava.library.path=${IS_SDK_LIB} -cp ${IS_SDK_JAR}:./src/main/java/com/ionic/ihw ${MAIN_CLASS}



