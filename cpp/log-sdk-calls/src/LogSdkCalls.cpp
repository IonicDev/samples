/*
 * (c) 2017-2021 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISAgent.h"
#include "ISChunkCrypto.h"
#include "ISAgentSDKError.h"
#include "ISLog.h"
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
	std::string message = "Hello World!";

	// initialize logger
	ISLogFilterSeverity * pConsoleFilter = new ISLogFilterSeverity(SEV_INFO); // INFO and above goes to console
	ISLogWriterConsole * pConsoleWriter = new ISLogWriterConsole();
	pConsoleWriter->setFilter(pConsoleFilter);
	ISLogFilterSeverity * pFileFilter = new ISLogFilterSeverity(SEV_DEBUG); // DEBUG and above goes to file
	ISLogWriterFile * pFileWriter = new ISLogWriterFile("sample.log");
	pFileWriter->setFilter(pFileFilter);
	ISLogSink * pSink = new ISLogSink();
	pSink->registerChannelName(ISAGENT_LOG_CHANNEL);
	pSink->registerWriter(pConsoleWriter);
	pSink->registerWriter(pFileWriter);
	ISLogImpl * pLogger = new ISLogImpl(true);
	pLogger->registerSink(pSink);
	ISLog::setSingleton(pLogger);

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

	// Setup a Chunk Crypto object to handle Ionic encryption
	ISChunkCryptoCipherAuto chunkCrypto(agent);

	// Encrypt the string using an Ionic-managed Key
	std::string ciphertext;
	nErrorCode = chunkCrypto.encrypt(message, ciphertext);

	std::cout << "Input: " << message << std::endl;
	std::cout << "Chunk-Encrypted String: " << ciphertext << std::endl;
	return 0;
}
