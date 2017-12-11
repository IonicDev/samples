/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include <iostream>
#include <ISAgent.h>
#include <ISFileCrypto.h>
#include <ISAgentSDKError.h>

bool makeLinuxHomeDirPath(std::string pathFromHomeDir, std::string & fullPath);

int main()
{
    std::string inFilePath("example_file.docx");
    std::string outFilePath("example_file_encrypted.docx");
    
    // Setup an agent object to talk to Ionic
    ISAgent agent;
    int nErrorCode;
#if __linux__
    //NOTE: On Linux, you must add additional code here, see "Getting Started" for C++ on Linux.
    std::string plainSepPath;
    if (!makeLinuxHomeDirPath(".ionicsecurity/profiles.pt", plainSepPath)) { // Makes the absolute path for ~/.ionic/profiles.pt
        std::cerr << "Error getting home directory path." << std::endl;
        return -1;
    }
    ISAgentDeviceProfilePersistorPlaintext plainPersistor;
    plainPersistor.setFilePath(plainSepPath);
    std::cout << "Initializing agent..." << std::endl;
    nErrorCode = agent.initialize(plainPersistor);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error initializing agent: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
    } else {
        std::cout << "A plaintext SEP was loaded from " << plainSepPath << std::endl;
    }
#else
    nErrorCode = agent.initialize();
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error initializing agent: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
    }
#endif

    // Check if there are profiles.
    if (!agent.hasAnyProfiles()) {
        std::cout << "There are no device profiles on this device." << std::endl;
        std::cout << "Register a device before continuing." << std::endl;
        return -1;
    }
    
    // Create a classification
    std::map< std::string, std::vector< std::string > > map;
    map["classification"].push_back("Restricted");
    
    // Apply the classification
    ISFileCryptoEncryptAttributes attribs;
    attribs.setKeyAttributes(map);
    
    // Encrypt the file using the classification
    ISFileCryptoCipherAuto cipher(agent);
    int nErrorCode = cipher.encrypt(inFilePath, outFilePath, &attribs);
    
    // Validate the response
    if (nErrorCode != ISCRYPTO_OK) {
        std::cerr << "Error encrypting " << inFilePath << ": " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        std::cout << "Press return to exit.";
        std::getchar();
        return -2;
    }

    std::cout << "Encrypted file " << inFilePath << " to " << outFilePath << "." << std::endl;
    std::cout << "Press return to exit.";
    std::getchar();
    return 0;
}

bool makeLinuxHomeDirPath(std::string pathFromHomeDir, std::string & fullPath) {
    char const* tmp = getenv("HOME");
    if (tmp == NULL) {
        return false;
    } else {
        fullPath = std::string(tmp) + "/" + pathFromHomeDir;
        return true;
    }
}
