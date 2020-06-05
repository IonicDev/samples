/*
 * (c) 2018-2020 Ionic Security Inc.
 *
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html) and the
 * Privacy Policy (https://www.ionic.com/privacy-notice/).
 *
*/

#include <iostream>
#include <ISAgent.h>
#include <ISAgentSDKError.h>

int main(int argc, char* argv[])
{
    int nErrorCode;
    ISAgent agent;

    // initialize agent with default persistor
    ISAgentDeviceProfilePersistorDefault persistor;
    nErrorCode = agent.initialize(persistor);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error initializing agent: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
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
