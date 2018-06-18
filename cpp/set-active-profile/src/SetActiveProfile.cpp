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

#ifdef _WIN32
    #define HOMEVAR "USERPROFILE"
#else 
    #define HOMEVAR "HOME"
#endif

int main(int argc, char* argv[]) {

    int nErrorCode;
    std::string persistorPath = "../../sample-data/persistors/sample-persistor.pt";
    std::string profileId = "ABcd.1.48sdf0-cs80-5802-sd80-d8s0df80sdfj";

    // read persistor password from environment variable
    char* cpersistorPassword = std::getenv("IONIC_PERSISTOR_PASSWORD");
    if (cpersistorPassword == NULL) {
        std::cerr << "[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD" << std::endl;
        exit(1);
    }
    std::string persistorPassword = std::string(cpersistorPassword);

    // initialize agent with password persistor
    std::string persistorPath = std::string(std::getenv(HOMEVAR)) + "/.ionicsecurity/profiles.pw";
    ISAgentDeviceProfilePersistorPassword persistor;
    persistor.setFilePath(persistorPath);
    persistor.setPassword(persistorPassword);
    ISAgent agent;
    nErrorCode = agent.initialize(persistor);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Failed to initialize agent from password persistor (" << persistorPath << ")" << std::endl;
        std::cerr << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }

    // list all available profiles
    const std::vector<ISAgentDeviceProfile> profiles = agent.getAllProfiles();
    std::cout << "ALL_PROFILES:" << std::endl;
    for (int i=0; i < profiles.size(); i++) {
        const ISAgentDeviceProfile profile = profiles.at(i);
        std::cout << profile.getDeviceId() << std::endl;
    }

    // set new active profile
    bool bSuccess = agent.setActiveProfile(profileId);
    if (!bSuccess) {
        std::cerr << "Failed to set active profile" << std::endl;
        exit(2);
    }

    // display new active profile
    ISAgentDeviceProfile activeProfile = agent.getActiveProfile();
    std::cout << std::endl << "ACTIVE_PROFILE:" << std::endl;
    std::cout << activeProfile.getDeviceId() << std::endl;
}

