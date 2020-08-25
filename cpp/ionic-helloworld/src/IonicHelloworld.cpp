/*
 * (c) 2017-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISAgent.h"
#include "ISAgentSDKError.h"
#include <ISChunkCrypto.h>
#include <ISChunkCryptoEncryptAttributes.h>
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
    std::string appName = "ionic-helloworld-cpp";
    std::string appVersion = "1.0.0";

    std::string message = "Hello, World!";

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

    // set app metadata
    agent.setMetadata("ionic-application-name", appName);
    agent.setMetadata("ionic-application-version", appVersion);

    // define and set data marking clearance-level.
    std::map< std::string, std::vector< std::string > > dataMarkings;
    std::vector<std::string> clearanceLevel;
    clearanceLevel.push_back("secret");
    dataMarkings["clearance-level"] = clearanceLevel;
    ISChunkCryptoEncryptAttributes cipherAttributes;
    cipherAttributes.setKeyAttributes(dataMarkings);

    // initialize chunk cipher object
    ISChunkCryptoCipherAuto cipher(agent);

    // encrypt
    std::string ciphertext;
    nErrorCode = cipher.encrypt(message, ciphertext, &cipherAttributes);
    if (nErrorCode != ISCRYPTO_OK) {
        std::cerr << "Encryption Error: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }

    // decrypt
    std::string plaintext;

    // Note: Decryption only works if the policy allows it.
    nErrorCode = cipher.decrypt(ciphertext, plaintext);
    if (nErrorCode != ISCRYPTO_OK) {
        std::cerr << "Decryption Error: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        std::cerr << "Insufficient clearance to access this data." << std::endl;
        std::cerr << std::endl;
        exit(1);
    }

    // display data
    std::cout << std::endl;
    std::cout << "Input : " << message << std::endl;
    std::cout << "Ionic Chunk Encrypted Ciphertext : " << ciphertext << std::endl;
    std::cout << "Plaintext  : " << plaintext << std::endl;
    std::cout << std::endl;
}
