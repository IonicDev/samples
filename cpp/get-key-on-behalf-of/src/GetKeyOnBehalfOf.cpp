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
    std::string delegatedUserEmail = "test@ionic.com";
    std::string keyId = ""; // TODO: provide key to get

    if (keyId == "") {
        std::cout << "Please set the 'keyId' variable to a key you have already created" << std::endl;
        exit(1);
    }

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
    ISAgent agent;
    nErrorCode = agent.initialize(persistor);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Failed to initialize agent from password persistor (" << persistorPath << ")" << std::endl;
        std::cerr << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }

    // define on-behalf-of user in the request metadata
    ISAgentGetKeysRequest request;
    request.getMetadata()["ionic-delegated-email"] = delegatedUserEmail;

    // get key on behalf of user
    request.getKeyIds().push_back(keyId);
    ISAgentGetKeysResponse response;
    nErrorCode = agent.getKeys(request, response);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error creating key: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }
    if (response.getKeys().size() == 0) {
        std::cerr << "No keys were returned (key does not exist or access was denied)" << std::endl;
        exit(1);
    }
    ISAgentGetKeysResponse::Key responseKey = response.getKeys().at(0);

    // display fetched key
    ISCryptoHexString hexKey;
    hexKey.fromBytes(responseKey.getKey());
    std::cout << "KeyId    : " << responseKey.getId() << std::endl;
    std::cout << "KeyBytes : " << hexKey << std::endl;
}
