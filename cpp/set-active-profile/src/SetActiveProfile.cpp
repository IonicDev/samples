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
    std::string persistorPath = "../../sample-data/persistors/sample-persistor.pt";
    std::string profileId = "ABcd.1.48sdf0-cs80-5802-sd80-d8s0df80sdfj";

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

