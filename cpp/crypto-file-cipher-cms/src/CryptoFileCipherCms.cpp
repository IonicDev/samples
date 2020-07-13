/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISAgent.h"
#include "ISAgentSDKError.h"
#include <ISFileCrypto.h>
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
    std::string fileOriginal = "../../sample-data/files/Message.txt";
    std::string fileCiphertext = "./Message-Protected.txt";
    std::string filePlaintext = "./Message.txt";

    // read persistor password from environment variable
    char* cpersistorPassword = std::getenv("IONIC_PERSISTOR_PASSWORD");
    if (cpersistorPassword == NULL) {
        std::cerr << "[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD" << std::endl;
        exit(1);
    }
    std::string persistorPassword = std::string(cpersistorPassword);

    // get password persistor
    std::string persistorPath = std::string(std::getenv(HOMEVAR)) + "/.ionicsecurity/profiles.pw";
    ISAgentDeviceProfilePersistorPassword persistor;
    persistor.setFilePath(persistorPath);
    persistor.setPassword(persistorPassword);

    // initialize agent with password persistor
    ISAgent agent;
    nErrorCode = agent.initialize(persistor);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Failed to initialize agent from password persistor (" << persistorPath << ")" << std::endl;
        std::cerr << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }

    // define attributes (optional)
    std::map<std::string, std::vector<std::string>> mutableAttributes;
    std::vector<std::string> classificationVal;
    classificationVal.push_back("Restricted");
    mutableAttributes["classification"] = classificationVal;
    ISFileCryptoEncryptAttributes fileCryptoAttributes;
    fileCryptoAttributes.setMutableKeyAttributes(mutableAttributes);

    // initialize generic file cipher object
    ISFileCryptoCipherCms cipher(agent);

    // encrypt
    std::cout << "Encrypting message and saving to Ciphertext File : " << fileCiphertext << std::endl;
    nErrorCode = cipher.encrypt(fileOriginal, fileCiphertext, &fileCryptoAttributes);
    if (nErrorCode != ISCRYPTO_OK) {
        std::cerr << "Error: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }

    // decrypt
    std::cout << "Decrypting message and saving to Plaintext File  : " << filePlaintext << std::endl;
    nErrorCode = cipher.decrypt(fileCiphertext, filePlaintext);
    if (nErrorCode != ISCRYPTO_OK) {
        std::cerr << "Error: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }
}
