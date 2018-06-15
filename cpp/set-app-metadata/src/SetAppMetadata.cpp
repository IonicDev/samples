/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISAgent.h"
#include "ISAgentSDKError.h"
#include <stdio.h>
#include <cstdlib>
#include <iostream>

int main(int argc, char* argv[]) {
    
    int nErrorCode;
    std::string appName = "cpp-sample-app";
    std::string appVersion = "1.0.0";

    // initialize agent with password persistor
    ISAgent agent;
    ISAgentDeviceProfilePersistorPassword persistor;
    persistor.setFilePath(std::string(std::getenv("HOME")) + "/.ionicsecurity/profiles.pw");
    persistor.setPassword(std::string(std::getenv("IONIC_PERSISTOR_PASSWORD")));
    nErrorCode = agent.initialize(persistor);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error initializing agent: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }

    // set app metadata
    agent.setMetadata("ionic-application-name", appName);
    agent.setMetadata("ionic-application-version", appVersion);
}

