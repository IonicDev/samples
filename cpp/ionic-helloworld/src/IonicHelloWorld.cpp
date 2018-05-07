/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISAgent.h"
#include "ISChunkCrypto.h"
#include "ISAgentSDKError.h"
#include <cstdlib>
#include <iostream>

int main(int argc, char* argv[]) {
    // Set input string
    std::string input = "Hello World!";
    int nErrorCode;

    // Setup an agent object to talk to Ionic
    ISAgent agent;

#if __linux__
    std::string profilePath = std::string(getenv("HOME")) + "/.ionicsecurity/profiles.pt";
#endif

#if _WIN32
    std::string profilePath = std::string(getenv("HOMEPATH")) + "/.ionicsecurity/profiles.pt";
#endif

#if __MACH__
    std::string profilePath = std::string(getenv("HOME")) + "/.ionicsecurity/profiles.pt";
#endif

    ISAgentDeviceProfilePersistorPlaintext plainPersistor;
    plainPersistor.setFilePath(profilePath);
    nErrorCode = agent.initialize(plainPersistor);

    if (nErrorCode != ISAGENT_OK) {
    } 

    // Setup a Chunk Crypto object to handle Ionic encryption
    ISChunkCryptoCipherAuto chunkCrypto(agent);

    // Encrypt the string using an Ionic-managed Key
    std::string encryptedText = "";
    nErrorCode = chunkCrypto.encrypt(input, encryptedText);

    // Validate the response
    if (nErrorCode != ISCRYPTO_OK) {
        std::cerr << "Error encrypting: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
    }

    std::cout << "Plain Text: " << input << std::endl;
    std::cout << "Ionic Chunk Encrypted Text: " << encryptedText << std::endl;

    std::cout << "Press return to exit.";
    getchar();
}
