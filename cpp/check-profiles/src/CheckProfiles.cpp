/*
 * (c) 2017-2021 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include <iostream>
#include <ISAgent.h>
#include <ISAgentSDKError.h>
#include <ISCrypto.h>
#include "CrossPlatform.h"

#ifdef _WIN32
    #define HOMEVAR "USERPROFILE"
#else
    #define HOMEVAR "HOME"
#endif

int main(int argc, char* argv[])
{
    int nErrorCode;
    ISAgent agent;

    // read SDK path to use to load the crypto libs from environment variable
    char* cSdkPath = std::getenv("IONIC_SDK_PATH");
    if (cSdkPath == NULL) {
        std::cerr << "[!] Please provide the SDK path as env variable: IONIC_SDK_PATH" << std::endl;
        exit(1);
    }
    std::string sdkPath = std::string(cSdkPath);
    std::string cryptoPath = sdkPath + "/ISAgentSDKCpp/Lib/" + OS + "/Release/" + ARCH;
    ISCrypto::setCryptoSharedLibraryCustomDirectory(cryptoPath);

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
    nErrorCode = agent.initialize(persistor);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Failed to initialize agent from password persistor (" << persistorPath << ")" << std::endl;
        std::cerr << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }

    // Check if there are profiles.
    if (agent.hasAnyProfiles()) {
        std::vector< ISAgentDeviceProfile >::iterator iter;
        std::vector< ISAgentDeviceProfile > profiles = agent.getAllProfiles();
        for (iter = profiles.begin(); iter != profiles.end(); ++iter) {
            std::cout << "Name: " << iter->getName()
                << ", Id: " << iter->getDeviceId() << std::endl;
        }

        // Check if there is an active profile.
        if (agent.hasActiveProfile()) {
            ISAgentDeviceProfile profile = agent.getActiveProfile();
            std::cout << std::endl << "Active profile, Name: " << profile.getName()
                << ", Id: " << profile.getDeviceId() << std::endl;
        }
        else {
            std::cerr << "There is not an active device profile selected on this device." <<  std::endl;
        }
    }
    else {
        std::cerr << "There are no device profiles on this device." <<  std::endl;
    }

    std::cout << "\nPress Enter to continue...";
    getchar();
    return 0;
}
