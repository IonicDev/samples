/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include <ISAgentSDKC.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>

#define MY_APPLICATION_CHANNEL_NAME "SampleLoggingApplication"

void setupLogging() 
{
	//NOTE: Depending on the severity and channel name, this can cause many of the Ionic internal SDK actions to be logged.
	//	This is made available as it is useful for debugging, but you may want to filter on your channel instead
	//	of ISAGENT_LOG_CHANNEL to see content only from your application.

    // Set up logging
    // Create file to write log to
    const char *pszOutputLogFile = "sample.log";
    int logError = ionic_log_setup_simple(pszOutputLogFile, true, ISLOG_SEV_DEBUG); 
    if (logError != ISC_OK) {
        printf("Error setting up log: %s\n", ionic_get_error_str(logError));
		exit(-1);
    }
}

int main()
{
	char *plainText = "Ionic Hello World";

	setupLogging();

    // Setup an agent object to talk to Ionic
    ionic_profile_persistor_t *profileLoaderOpt = ionic_profile_persistor_create_default();
    ionic_agent_t *agent = ionic_agent_create(profileLoaderOpt, NULL);

    // Setup a Chunk Crypto object to handle Ionic encryption
    ionic_chunkcipher_t *chunkCrypto = ionic_chunkcipher_create_auto(agent);

	// Encrypt the string using an Ionic-managed Key
	char *encryptedText;
	int nErrorCode = ionic_chunkcipher_encrypt_str(chunkCrypto, plainText, &encryptedText);

	// Validate the response
	if (nErrorCode != ISCRYPTO_OK) {
        // Write an error message to the log.
        char *errMsg = strcat("Error encrypting: ", ionic_get_error_str(nErrorCode));
        int logError = ionic_log(ISLOG_SEV_ERROR, MY_APPLICATION_CHANNEL_NAME, __LINE__, NULL, errMsg);
        if (logError != ISC_OK) {
            printf("Error writing to the log: %s\n", ionic_get_error_str(logError));
            exit(-2);
        }
	}
	else {
        // Show your plaintext and ciphertext
		printf("Plain Text: %s\n", plainText);
		printf("Chunk-Encrypted String: %s\n", encryptedText);
	}

    // You can write your own information to the log, too.
    int logErrorSecondary = ionic_log(ISLOG_SEV_INFO, MY_APPLICATION_CHANNEL_NAME, \
     __LINE__, NULL, "Exiting...\n");
    if (logErrorSecondary != ISC_OK) {
        printf("Error writing to the log: %s\n", ionic_get_error_str(logErrorSecondary));
        exit(-2);
    }

	return 0;
}