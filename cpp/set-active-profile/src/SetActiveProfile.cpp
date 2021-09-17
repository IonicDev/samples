/*
 * (c) 2018-2021 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISAgent.h"
#include "ISAgentSDKError.h"
#include <stdio.h>
#include <cstdlib>
#include <iostream>
#include "CrossPlatform.h"

#ifdef _WIN32
    #define HOMEVAR "USERPROFILE"
#else 
    #define HOMEVAR "HOME"
#endif

int main(int argc, char* argv[]) {

    int nErrorCode;
    std::string persistorPath = "../../sample-data/persistors/sample-persistor.pt";
    std::string profileId = "ABcd.1.48sdf0-cs80-5802-sd80-d8s0df80sdfj";

    // read SDK path to use to load the crypto libs from environment variable
    char* cSdkPath = std::getenv("IONIC_SDK_PATH");
    if (cSdkPath == NULL) {
        std::cerr << "[!] Please provide the SDK path as env variable: IONIC_SDK_PATH" << std::endl;
        exit(1);
    }
    std::string sdkPath = std::string(cSdkPath);
    std::string cryptoPath = sdkPath + "/ISAgentSDKCpp/Lib/" + OS + "/Release/" + ARCH;
    ISCrypto::setCryptoSharedLibraryCustomDirectory(cryptoPath);

    // initialize agent with plaintext persistor
    ISAgent agent;
    ISAgentDeviceProfilePersistorPlaintext persistor;
    persistor.setFilePath(persistorPath);
    nErrorCode = agent.initialize(persistor);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error initializing agent: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
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

