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
    std::string message = "secret message";
    std::string keyHex = "A0444B8B5A7209780823617A98986831B8240BAA851A0B1696B0329280286B17";
    std::string authData = "data to authenticate";

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

    // manually load key
    ISCryptoHexString isKeyHex(keyHex);
    ISCryptoBytes keyBytes;
    isKeyHex.toBytes(keyBytes);

    // initialize aes cipher object
    ISCryptoAesGcmCipher cipher(keyBytes, authData);

     // encrypt
    ISCryptoBytes ciphertext;
    nErrorCode = cipher.encrypt(message, ciphertext);
    if (nErrorCode != ISCRYPTO_OK) {
        std::cerr << "Error: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }

    // decrypt
    ISCryptoBytes plaintext;
    cipher.decrypt(ciphertext, plaintext);
    if (nErrorCode != ISCRYPTO_OK) {
        std::cerr << "Error: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }

    // display data
    ISCryptoHexString ciphertextHex;
    std::string plaintextString;
    ciphertextHex.fromBytes(ciphertext);
    plaintext.toString(plaintextString);
    std::cout << "Ciphertext : " << ciphertextHex << std::endl;
    std::cout << "Plaintext  : " << plaintextString << std::endl;
}

