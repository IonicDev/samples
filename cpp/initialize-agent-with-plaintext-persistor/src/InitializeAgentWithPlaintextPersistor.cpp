/*
 * (c) 2018-2020 Ionic Security Inc.
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

    // initialize agent with plaintext persistor
    ISAgent agent;
    ISAgentDeviceProfilePersistorPlaintext persistor;
    persistor.setFilePath(persistorPath);
    nErrorCode = agent.initialize(persistor);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error initializing agent: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
    }

    // display all profiles in persistor
    const std::vector<ISAgentDeviceProfile> profiles = agent.getAllProfiles();
    for (int i=0; i < profiles.size(); i++) {
        const ISAgentDeviceProfile profile = profiles.at(i);
        std::cout << "---" << std::endl;
        std::cout << "ID       : " << profile.getDeviceId() << std::endl;
        std::cout << "Name     : " << profile.getName() << std::endl;
        std::cout << "Keyspace : " << profile.getKeySpace() << std::endl;
        std::cout << "ApiUrl   : " << profile.getServer() << std::endl;
    }
}

