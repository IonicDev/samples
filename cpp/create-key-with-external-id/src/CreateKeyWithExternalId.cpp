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
    std::string externalId = "d8ded396-4388-4489-9604-c2482205e55d";

    // read persistor password from environment variable
    char* cpersistorPassword = std::getenv("IONIC_PERSISTOR_PASSWORD");
    if (cpersistorPassword == NULL) {
        std::cerr << "[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD" << std::endl;
        exit(1);
    }
    std::string persistorPassword = std::string(cpersistorPassword);

    // initialize agent with password persistor
    std::string persistorPath = std::string(std::getenv("HOME")) + "/.ionicsecurity/profiles.pw";
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

    // define external id as fixed attribute
    std::map< std::string, std::vector< std::string > > attributes;
    std::vector<std::string> externalIdVal;
    externalIdVal.push_back(externalId);
    attributes["ionic-external-id"] = externalIdVal;

    // create key with an external id
    ISAgentCreateKeysRequest request;
    ISAgentCreateKeysRequest::Key requestKey("refid1", 1, attributes);
    request.getKeys().push_back(requestKey);
    ISAgentCreateKeysResponse response;
    nErrorCode = agent.createKeys(request, response);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error creating key: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }
    const ISAgentCreateKeysResponse::Key *responseKey = response.findKey("refid1");

    // display key
    ISCryptoHexString hexKey;
    hexKey.fromBytes(responseKey->getKey());
    std::cout << "KeyId    : " << responseKey->getId() << std::endl;
    std::cout << "KeyBytes : " << hexKey << std::endl;
}
