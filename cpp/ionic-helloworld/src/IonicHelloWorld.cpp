/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISAgent.h"
#include "ISAgentSDKError.h"
#include <ISChunkCrypto.h>
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
    std::string input = "Hello World!";

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

    // check if there are profiles.
	if (!agent.hasAnyProfiles()) {
		std::cout << "There are no device profiles on this device." << std::endl;
		std::cout << "Register a device before continuing." << std::endl;
		exit(1);
	}

    // initialize chunk cipher object
    ISChunkCryptoCipherAuto cipher(agent);

    // encrypt the input with an Ionic-managed key
    std::string ciphertext;
    nErrorCode = cipher.encrypt(input, ciphertext);
    if (nErrorCode != ISCRYPTO_OK) {
        std::cerr << "Error: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }

    std::cout << "Input: " << input << std::endl;
    std::cout << "Ionic Chunk Encrypted Ciphertext: " << ciphertext << std::endl;
}
