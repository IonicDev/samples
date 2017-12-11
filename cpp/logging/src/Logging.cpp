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

bool makeLinuxHomeDirPath(std::string pathFromHomeDir, std::string & fullPath);

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

bool makeLinuxHomeDirPath(std::string pathFromHomeDir, std::string & fullPath) {
    char const* tmp = getenv("HOME");
    if (tmp == NULL) {
        return false;
    } else {
        fullPath = std::string(tmp) + "/" + pathFromHomeDir;
        return true;
    }
}
