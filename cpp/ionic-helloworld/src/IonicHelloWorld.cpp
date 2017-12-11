/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISAgent.h"
#include "ISChunkCrypto.h"
#include "ISAgentSDKError.h"
#include "ISLog.h"
#include <cstdlib>
#include <iostream>

void setupLogging();
bool makeLinuxHomeDirPath(std::string pathFromHomeDir, std::string & fullPath);

int main(int argc, char* argv[]) {
    setupLogging();

    // Set input string
    std::string input = "Hello World!";
    int nErrorCode;

    // Setup an agent object to talk to Ionic
    ISAgent agent;
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

// UTILITY FUNCTIONS

void setupLogging() {
    ISLogFilterSeverity * pConsoleFilter = new ISLogFilterSeverity(SEV_DEBUG);
    ISLogWriterConsole * pConsoleWriter = new ISLogWriterConsole();
    pConsoleWriter->setFilter(pConsoleFilter);
    // Initialize log sink(s)
    ISLogSink * pSink = new ISLogSink();
    pSink->registerChannelName(ISAGENT_LOG_CHANNEL);
    pSink->registerWriter(pConsoleWriter);
    // Initialize logger.
    ISLogImpl * pLogger = new ISLogImpl(true);
    pLogger->registerSink(pSink);
    // Assign logger to static interface ISLog
    ISLog::setSingleton(pLogger);
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