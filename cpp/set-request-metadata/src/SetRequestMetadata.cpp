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
    std::string keyId = "HVzG5eMZrc8";

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

    // define request metadata
    ISAgentGetKeysRequest request;
    request.getMetadata()["application-state"] = "active";

    // fetch key with request metadata
    request.getKeyIds().push_back(keyId);
    ISAgentGetKeysResponse response;
    nErrorCode = agent.getKeys(request, response);
    ISAgentGetKeysResponse::Key responseKey = response.getKeys().at(0);

    // display fetched key
    ISCryptoHexString hexKey;
    hexKey.fromBytes(responseKey.getKey());
    std::cout << "KeyId    : " << responseKey.getId() << std::endl;
    std::cout << "KeyBytes : " << hexKey << std::endl;
}

