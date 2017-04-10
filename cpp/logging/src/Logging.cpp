/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISAgent.h"
#include "ISChunkCrypto.h"
#include "ISAgentSDKError.h"
#include "ISLog.h"

#define MY_APPLICATION_CHANNEL_NAME "SampleLoggingApplication"

void setupLogging() {
	//NOTE: Depending on the severity and channel name, this can cause many of the Ionic internal SDK actions to be logged.
	//	This is made available as it is useful for debugging, but you may want to filter on your channel instead
	//	of ISAGENT_LOG_CHANNEL to see content only from your application.
	ISLogFilterSeverity * pConsoleFilter = new ISLogFilterSeverity(SEV_INFO); // INFO and above goes to console
	ISLogWriterConsole * pConsoleWriter = new ISLogWriterConsole();
	pConsoleWriter->setFilter(pConsoleFilter);

	ISLogFilterSeverity * pFileFilter = new ISLogFilterSeverity(SEV_DEBUG); // DEBUG and above goes to file
	ISLogWriterFile * pFileWriter = new ISLogWriterFile("sample.log");
	pFileWriter->setFilter(pFileFilter);

	// Initialize log sink(s)
	ISLogSink * pSink = new ISLogSink();
	pSink->registerChannelName(ISAGENT_LOG_CHANNEL);
	pSink->registerChannelName(MY_APPLICATION_CHANNEL_NAME);
	pSink->registerWriter(pConsoleWriter);
	pSink->registerWriter(pFileWriter);

	// Initialize logger. We pass true to the constructor so that it owns all
	// the registered objects (the objects above that we created on the heap).
	ISLogImpl * pLogger = new ISLogImpl(true);
	pLogger->registerSink(pSink);

	// Assign logger to static interface ISLog
	ISLog::setSingleton(pLogger);
}

int main()
{
	std::string plainText = "Ionic Hello World";

	setupLogging();

	// Setup an agent object to talk to Ionic
	ISAgent agent;
	agent.initialize();

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
}