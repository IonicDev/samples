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
    std::string keyId = "HVzG5eMZrc8";

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

    // define new mutable attributes
    std::vector<std::string> classificationVal;
    classificationVal.push_back("Highly Restricted");
    
    // fetch key
    ISAgentGetKeysResponse response;
    nErrorCode = agent.getKey(keyId, response);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error fetching key: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }
    ISAgentGetKeysResponse::Key fetchedKey = response.getKeys().at(0);

    // define key to update
    ISAgentUpdateKeysRequest::Key updateKey(fetchedKey);
    updateKey.getMutableAttributes()["classification"] = classificationVal;

    // update key
    ISAgentUpdateKeysResponse updateResponse;
    nErrorCode = agent.updateKey(updateKey, updateResponse);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error updating key: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }
    ISAgentUpdateKeysResponse::Key updatedKey = updateResponse.getKeys().at(0);

    // display updated key
    ISCryptoHexString hexKey;
    hexKey.fromBytes(updatedKey.getKey());
    std::cout << "KeyId    : " << updateKey.getId() << std::endl;
    std::cout << "KeyBytes : " << hexKey << std::endl;
    std::cout << updatedKey.getMutableAttributes() << std::endl;
}
