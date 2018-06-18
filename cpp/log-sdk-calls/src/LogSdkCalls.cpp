/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISAgent.h"
#include "ISChunkCrypto.h"
#include "ISAgentSDKError.h"
#include "ISLog.h"

#ifdef _WIN32
    #define HOMEVAR "USERPROFILE"
#else 
    #define HOMEVAR "HOME"
#endif

int main()
{
	int nErrorCode
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
	pSink->registerChannelName(MY_APPLICATION_CHANNEL_NAME);
	pSink->registerWriter(pConsoleWriter);
	pSink->registerWriter(pFileWriter);
	ISLogImpl * pLogger = new ISLogImpl(true);
	pLogger->registerSink(pSink);
	ISLog::setSingleton(pLogger);

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
	std::string encryptedText;
	int nErrorCode = chunkCrypto.encrypt(plainText, encryptedText);

	// Validate the response
	if (nErrorCode != ISCRYPTO_OK)
	{
		//std::cerr << "Error encrypting: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
		ISLOGF_ERROR(MY_APPLICATION_CHANNEL_NAME, "Error encrypting: %s\n", ISAgentSDKError::getErrorCodeString(nErrorCode).c_str());
	}
	else
	{
		std::cout << "Plain Text: " << plainText << std::endl;
		std::cout << "Chunk-Encrypted String: " << encryptedText << std::endl;
	}
	ISLOGF_INFO(MY_APPLICATION_CHANNEL_NAME, "Press return to exit.");
	std::getchar();
	return 0;
	*/
}