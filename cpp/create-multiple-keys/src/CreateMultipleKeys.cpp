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
    int keyCount = 5;

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

    // create multiple keys
    ISAgentCreateKeysRequest request;
    ISAgentCreateKeysRequest::Key requestKey("refId", keyCount);
    request.getKeys().push_back(requestKey);
    ISAgentCreateKeysResponse response;
    nErrorCode = agent.createKeys(request, response);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error creating key: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }
    std::vector<ISAgentCreateKeysResponse::Key> responseKeysList = response.getKeys();

    // display created keys
    for(int i = 0; i < responseKeysList.size(); i++) {
        ISAgentCreateKeysResponse::Key responseKey = responseKeysList.at(i);
        ISCryptoHexString hexKey;
        hexKey.fromBytes(responseKey.getKey());
        std::cout << "---" << std::endl;
        std::cout << "KeyId    : " << responseKey.getId() << std::endl;
        std::cout << "KeyBytes : " << hexKey << std::endl;
    }
}
